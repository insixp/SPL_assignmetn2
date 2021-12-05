package bgu.spl.mics.application.objects;

import junit.framework.TestCase;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

public class GPUTest extends TestCase {

    GPU gpu;

    public void setUp() throws Exception {
        super.setUp();
        this.gpu = new GPU(80, GPU.Type.GTX1080);
        Data data = new Data(Data.Type.Tabular, 23000);
        Student student = new Student("a", "b", Student.Degree.PhD);
        Model model = new Model("Model name", data, student);
        this.gpu.setModel(model);
    }

    public void tearDown() throws Exception {
    }

    @Test
    public void testGetVmemSize() {
        this.gpu = new GPU(0, GPU.Type.RTX3090);
        assertEquals(32, this.gpu.getVmemSize());
        this.gpu = new GPU(0, GPU.Type.RTX2080);
        assertEquals(16, this.gpu.getVmemSize());
        this.gpu = new GPU(0, GPU.Type.GTX1080);
        assertEquals(8, this.gpu.getVmemSize());
    }

    @Test
    public void testVmem() {
        assertEquals(0, this.gpu.getVmemOccupied());
        for(int i = 1; i < this.gpu.getVmemSize(); i++){
            this.gpu.popNextDataBatch();
            assertEquals(i, this.gpu.getVmemOccupied());
        }
        assertEquals(this.gpu.getVmemFree(), 0);
        DataBatch db = new DataBatch(new Data(Data.Type.Images, 200000), 3000, 2);
        this.gpu.setProcessedData(db);
        this.gpu.processNextTick();
        this.gpu.processNextTick();
        this.gpu.processNextTick();
        this.gpu.processNextTick();
        assertEquals(this.gpu.getVmemFree(), 1);
    }

    @Test
    public void testGetTotalBatch() {
        assertEquals(23, this.gpu.getTotalBatch());
    }

    @Test
    public void testGetBatchProcessed() {
        List<DataBatch> db_list = new ArrayList<DataBatch>();
        for(int i = 0; i< 13; i++) {
            db_list.add(this.gpu.popNextDataBatch());
        }
        for(int i = 0; i < 13; i++){
            this.gpu.setProcessedData(db_list.get(i));
        }
        assertEquals(13, this.gpu.getBatchProcessed());
    }

    @Test
    public void testPopNextDataBatch() {
        for(int i = 0; i < this.gpu.getTotalBatch(); i++) {
            DataBatch db = this.gpu.popNextDataBatch();
            assertEquals(i*this.gpu.BATCH_SIZE, db.getStartIndex());
            this.gpu.setProcessedData(db);
            this.gpu.processNextTick();
            this.gpu.processNextTick();
            this.gpu.processNextTick();
            this.gpu.processNextTick();
        }
    }

    @Test
    public void testSetModel() {
        Data d1 = new Data(Data.Type.Tabular, 250000);
        Data d2 = new Data(Data.Type.Images, 2500000);
        Data d3 = new Data(Data.Type.Text, 9999000);
        Student s1 = new Student("asdasd", "asv vasv", Student.Degree.PhD);
        Student s2 = new Student("asdasd", "asv vasv", Student.Degree.MSc);
        this.gpu = new GPU(8, GPU.Type.RTX2080);
        this.gpu.setModel(new Model("Name", d1, s1));
        assertEquals(this.gpu.getModel().getData(), d1);
        assertEquals(this.gpu.getModel().getStudent(), s1);
        this.gpu = new GPU(8, GPU.Type.RTX2080);
        this.gpu.setModel(new Model("Name", d2, s1));
        assertEquals(this.gpu.getModel().getData(), d2);
        assertEquals(this.gpu.getModel().getStudent(), s1);
        this.gpu = new GPU(8, GPU.Type.RTX2080);
        this.gpu.setModel(new Model("Name", d3, s1));
        assertEquals(this.gpu.getModel().getData(), d3);
        assertEquals(this.gpu.getModel().getStudent(), s1);
        this.gpu = new GPU(8, GPU.Type.RTX2080);
        this.gpu.setModel(new Model("Name", d1, s2));
        assertEquals(this.gpu.getModel().getData(), d1);
        assertEquals(this.gpu.getModel().getStudent(), s2);
        this.gpu = new GPU(8, GPU.Type.RTX2080);
        this.gpu.setModel(new Model("Name", d2, s2));
        assertEquals(this.gpu.getModel().getData(), d2);
        assertEquals(this.gpu.getModel().getStudent(), s2);
        this.gpu = new GPU(8, GPU.Type.RTX2080);
        this.gpu.setModel(new Model("Name", d3, s2));
        assertEquals(this.gpu.getModel().getData(), d3);
        assertEquals(this.gpu.getModel().getStudent(), s2);
    }

    @Test
    public void testSetProcessedData() {
        for(int i = 0; i < this.gpu.getVmemFree(); i++) {
            DataBatch db = this.gpu.popNextDataBatch();
            assertEquals(db.getStartIndex(), i*this.gpu.BATCH_SIZE);
            this.gpu.setProcessedData(db);
            assertEquals(this.gpu.getBatchProcessed(), i);
        }
    }

    @Test
    public void testGetTicksProcessed() {
        assertEquals(this.gpu.getTicksProcessed(), 0);
        DataBatch db = this.gpu.popNextDataBatch();
        this.gpu.setProcessedData(db);
        assertEquals(this.gpu.getTicksProcessed(), 0);
        this.testProcessNextTick();
        assertEquals(this.gpu.getTicksProcessed(), 1);
        this.testProcessNextTick();
        assertEquals(this.gpu.getTicksProcessed(), 2);
        this.testProcessNextTick();
        assertEquals(this.gpu.getTicksProcessed(), 3);
        this.testProcessNextTick();
        assertEquals(this.gpu.getTicksProcessed(), 0);
    }

    @Test
    public void testProcessNextTick() {
        assertEquals(this.gpu.getTicksProcessed(), 0);
        DataBatch db = this.gpu.popNextDataBatch();
        int free = this.gpu.getVmemFree();
        this.gpu.setProcessedData(db);
        assertEquals(this.gpu.getVmemFree(), free);
        this.testProcessNextTick();
        assertEquals(this.gpu.getVmemFree(), free);
        this.testProcessNextTick();
        assertEquals(this.gpu.getVmemFree(), free);
        this.testProcessNextTick();
        assertEquals(this.gpu.getVmemFree(), free);
        this.testProcessNextTick();
        assertEquals(this.gpu.getVmemFree(), free+1);
    }

    @Test
    public void testTestModel() {
        assertEquals(this.gpu.getModel().getResult(), Model.Result.None);
        Model.Result r = this.gpu.testModel(this.gpu.getModel());
        assert(r == Model.Result.Bad || r == Model.Result.Good);
    }
}