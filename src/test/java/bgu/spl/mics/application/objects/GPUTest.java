package bgu.spl.mics.application.objects;

import junit.framework.TestCase;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

public class GPUTest extends TestCase {

    GPU gpu;
    Cluster cluster;

    public void setUp() throws Exception {
        super.setUp();
        this.cluster = Cluster.getInstance();
        this.cluster.registerCPU(0);
        this.gpu = new GPU(80, GPU.Type.GTX1080, 1000, this.cluster);
        Data data = new Data(Data.Type.Tabular, 23000);
        Student student = new Student("a", "b", Student.Degree.PhD,1);
        Model model = new Model("Model name", data, student);
        this.gpu.setModel(model);
    }

    public void tearDown() throws Exception {
        this.gpu.clusterUnregister();
    }

    @Test
    public void testGetVmemSize() {
        this.gpu = new GPU(0, GPU.Type.RTX3090, 1000, this.cluster);
        assertEquals(32, this.gpu.getVmemSize());
        this.gpu = new GPU(0, GPU.Type.RTX2080, 1000, this.cluster);
        assertEquals(16, this.gpu.getVmemSize());
        this.gpu = new GPU(0, GPU.Type.GTX1080, 1000, this.cluster);
        assertEquals(8, this.gpu.getVmemSize());
    }

    @Test
    public void testVmem() {
        assertEquals(0, this.gpu.getVmemOccupied());
        this.gpu.processNextTick();
        assertEquals(this.gpu.getVmemFree(), 0);
        DataBatch db = new DataBatch(new Data(Data.Type.Images, 200000), 3000, 80);
        this.cluster.sendToGpu(db);
        assertEquals(0, this.gpu.getBatchProcessed());
        this.gpu.processNextTick();
        assertEquals(0, this.gpu.getBatchProcessed());
        this.gpu.processNextTick();
        assertEquals(0, this.gpu.getBatchProcessed());
        this.gpu.processNextTick();
        assertEquals(0, this.gpu.getBatchProcessed());
        this.gpu.processNextTick();
        assertEquals(0, this.gpu.getBatchProcessed());
        this.gpu.processNextTick();
        assertEquals(1000, this.gpu.getBatchProcessed());
    }

    @Test
    public void testGetTotalBatch() {
        assertEquals(23, this.gpu.getTotalBatch());
    }

    @Test
    public void testSetModel() {
        Data d1 = new Data(Data.Type.Tabular, 250000);
        Data d2 = new Data(Data.Type.Images, 2500000);
        Data d3 = new Data(Data.Type.Text, 9999000);
        Student s1 = new Student("asdasd", "asv vasv", Student.Degree.PhD,2);
        Student s2 = new Student("asdasd", "asv vasv", Student.Degree.MSc,3);
        this.gpu = new GPU(8, GPU.Type.RTX2080, 1000, this.cluster);
        this.gpu.setModel(new Model("Name", d1, s1));
        assertEquals(this.gpu.getModel().getData(), d1);
        assertEquals(this.gpu.getModel().getStudent(), s1);
        this.gpu.clusterUnregister();
        this.gpu = new GPU(8, GPU.Type.RTX2080, 1000, this.cluster);
        this.gpu.setModel(new Model("Name", d2, s1));
        assertEquals(this.gpu.getModel().getData(), d2);
        assertEquals(this.gpu.getModel().getStudent(), s1);
        this.gpu.clusterUnregister();
        this.gpu = new GPU(8, GPU.Type.RTX2080, 1000, this.cluster);
        this.gpu.setModel(new Model("Name", d3, s1));
        assertEquals(this.gpu.getModel().getData(), d3);
        assertEquals(this.gpu.getModel().getStudent(), s1);
        this.gpu.clusterUnregister();
        this.gpu = new GPU(8, GPU.Type.RTX2080, 1000, this.cluster);
        this.gpu.setModel(new Model("Name", d1, s2));
        assertEquals(this.gpu.getModel().getData(), d1);
        assertEquals(this.gpu.getModel().getStudent(), s2);
        this.gpu.clusterUnregister();
        this.gpu = new GPU(8, GPU.Type.RTX2080, 1000, this.cluster);
        this.gpu.setModel(new Model("Name", d2, s2));
        assertEquals(this.gpu.getModel().getData(), d2);
        assertEquals(this.gpu.getModel().getStudent(), s2);
        this.gpu.clusterUnregister();
        this.gpu = new GPU(8, GPU.Type.RTX2080, 1000, this.cluster);
        this.gpu.setModel(new Model("Name", d3, s2));
        assertEquals(this.gpu.getModel().getData(), d3);
        assertEquals(this.gpu.getModel().getStudent(), s2);
        this.gpu.clusterUnregister();
    }

    @Test
    public void testSetProcessedData() {
        this.gpu.processNextTick();
        for(int i = 0; i < 8; i++) {
            DataBatch db = this.cluster.readByCpu(0);
            assertNotNull(db);
            assertEquals(db.getStartIndex(), i * this.gpu.BATCH_SIZE);
        }
    }

    @Test
    public void testGetTicksProcessed() {
        assertEquals(this.gpu.getTicksProcessed(), 0);
        this.gpu.processNextTick();
        DataBatch db = this.cluster.readByCpu(0);
        this.cluster.sendToGpu(db);
        this.gpu.processNextTick();
        assertEquals(this.gpu.getTicksProcessed(), 0);
        this.gpu.processNextTick();
        assertEquals(this.gpu.getTicksProcessed(), 1);
        this.gpu.processNextTick();
        assertEquals(this.gpu.getTicksProcessed(), 2);
        this.gpu.processNextTick();
        assertEquals(this.gpu.getTicksProcessed(), 3);
        this.gpu.processNextTick();
        assertEquals(this.gpu.getTicksProcessed(), 0);
    }

    @Test
    public void testTestModel() {
        assertEquals(this.gpu.getModel().getResult(), Model.Result.None);
        Model.Result r = this.gpu.testModel(this.gpu.getModel());
        assert(r == Model.Result.Bad || r == Model.Result.Good);
    }
}