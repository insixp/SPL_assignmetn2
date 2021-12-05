package bgu.spl.mics.application.objects;

import junit.framework.TestCase;
import org.junit.Test;

import java.util.Collection;

public class CPUTest extends TestCase {

    private CPU cpu;
    private Cluster cluster;

    public void setUp() throws Exception {
        super.setUp();
        this.cluster = Cluster.getInstance();
    }

    public void tearDown() throws Exception {
        this.cpu.clusterUnregister();
    }

    @Test
    public void testAddGetDataBatch(){
        this.cpu = new CPU(3, 32, this.cluster);
        Data data = new Data(Data.Type.Images, 20000);
        DataBatch db = new DataBatch(data, 3000, 2);
        Collection<DataBatch> db_collection = this.cpu.getDataBatches();
        assertEquals(false, db_collection.contains(db));
        this.cluster.sendToCpu(db);
        db_collection = this.cpu.getDataBatches();
        assertEquals(1, db_collection.size());
        assertEquals(true, db_collection.contains(db));
    }

    @Test
    public void testProcessedData() {
        this.cpu = new CPU(0, 32, this.cluster);
        Data data = new Data(Data.Type.Images, 20000);
        DataBatch db = new DataBatch(data, 3000, 2);
        this.cluster.sendToCpu(db);
        this.cpu.processNextTick();
        assertNull(this.cluster.readByGpu(2));
        this.cpu.processNextTick();
        assertNull(this.cluster.readByGpu(2));
        this.cpu.processNextTick();
        assertNull(this.cluster.readByGpu(2));
        this.cpu.processNextTick();
        DataBatch processed_db = this.cluster.readByGpu(2);
        assertNotNull(processed_db);
        assertEquals(db.getGpuId(), processed_db.getGpuId());
        Data processed_Data = processed_db.getData();
        assertEquals(db.getStartIndex(), processed_db.getStartIndex());
        assertEquals(1000, processed_Data.getProcessed());
        assertEquals(data.getSize(), processed_Data.getSize());
        assertEquals(data.getType(), processed_Data.getType());
    }

    @Test
    public void test32CoresImage(){
        this.cpu = new CPU(0, 32, this.cluster);
        Data data = new Data(Data.Type.Images, 20000);
        DataBatch db = new DataBatch(data, 3000, 2);
        this.cluster.sendToCpu(db);
        //  Image
        for(int i = 0; i < 4;i++){
            this.cpu.processNextTick();
            DataBatch processedDB = this.cluster.readByGpu(2);
            if(i == 3)
                assertNotNull(processedDB);
            else
                assertNull(processedDB);
        }
        data = new Data(Data.Type.Text, 5000);
        db = new DataBatch(data, 2000, 5);
        this.cluster.sendToCpu(db);
        //  Text
        for(int i = 0; i < 2;i++){
            this.cpu.processNextTick();
            DataBatch processedDB = this.cluster.readByGpu(2);
            if(i == 1)
                assertNotNull(processedDB);
            else
                assertNull(processedDB);
        }
        //  Tabular
        data = new Data(Data.Type.Tabular, 5000);
        db = new DataBatch(data, 2000, 5);
        this.cluster.sendToCpu(db);
        for(int i = 0; i < 1;i++){
            this.cpu.processNextTick();
            DataBatch processedDB = this.cluster.readByGpu(2);
            assertNotNull(processedDB);
        }
    }

    @Test
    public void test16CoresImage(){
        this.cpu = new CPU(0, 16, this.cluster);
        Data data = new Data(Data.Type.Images, 20000);
        DataBatch db = new DataBatch(data, 3000, 2);
        this.cluster.sendToCpu(db);
        //  Image
        for(int i = 0; i < 8;i++){
            this.cpu.processNextTick();
            DataBatch processedDB = this.cluster.readByGpu(2);
            if(i == 7)
                assertNotNull(processedDB);
            else
                assertNull(processedDB);
        }
        data = new Data(Data.Type.Text, 5000);
        db = new DataBatch(data, 2000, 5);
        this.cluster.sendToCpu(db);
        //  Text
        for(int i = 0; i < 4;i++){
            this.cpu.processNextTick();
            DataBatch processedDB = this.cluster.readByGpu(2);
            if(i == 3)
                assertNotNull(processedDB);
            else
                assertNull(processedDB);
        }
        //  Tabular
        data = new Data(Data.Type.Tabular, 5000);
        db = new DataBatch(data, 2000, 5);
        this.cluster.sendToCpu(db);
        for(int i = 0; i < 2;i++){
            this.cpu.processNextTick();
            DataBatch processedDB = this.cluster.readByGpu(2);
            if(i == 1)
                assertNotNull(processedDB);
            else
                assertNull(processedDB);
        }
    }

    @Test
    public void test8CoresImage(){
        this.cpu = new CPU(0, 8, this.cluster);
        Data data = new Data(Data.Type.Images, 20000);
        DataBatch db = new DataBatch(data, 3000, 2);
        this.cluster.sendToCpu(db);
        //  Image
        for(int i = 0; i < 16;i++){
            this.cpu.processNextTick();
            DataBatch processedDB = this.cluster.readByGpu(2);
            if(i == 15)
                assertNotNull(processedDB);
            else
                assertNull(processedDB);
        }
        data = new Data(Data.Type.Text, 5000);
        db = new DataBatch(data, 2000, 5);
        this.cluster.sendToCpu(db);
        //  Text
        for(int i = 0; i < 8;i++){
            this.cpu.processNextTick();
            DataBatch processedDB = this.cluster.readByGpu(2);
            if(i == 7)
                assertNotNull(processedDB);
            else
                assertNull(processedDB);
        }
        //  Tabular
        data = new Data(Data.Type.Tabular, 5000);
        db = new DataBatch(data, 2000, 5);
        this.cluster.sendToCpu(db);
        for(int i = 0; i < 4;i++){
            this.cpu.processNextTick();
            DataBatch processedDB = this.cluster.readByGpu(2);
            if(i == 3)
                assertNotNull(processedDB);
            else
                assertNull(processedDB);
        }
    }

    public void testGetId() {
        this.cpu = new CPU(98, 32, this.cluster);
        assertEquals(this.cpu.getId(), 98);
    }

    @Test
    public void testGetCores(){
        this.cpu = new CPU(98, 24, this.cluster);
        assertEquals(24, this.cpu.getCores());
    }

    @Test
    public void testGetActive(){
        this.cpu = new CPU(98, 24, this.cluster);
        assertEquals(false, this.cpu.getActive());
        this.cluster.sendToCpu(new DataBatch(new Data(Data.Type.Images, 20000), 5000, 3));
        assertEquals(true, this.cpu.getActive());
    }

    public void testGetTicksProcessed() {
        this.cpu = new CPU(95, 23, this.cluster);
        Data data = new Data(Data.Type.Images, 20000);
        DataBatch db = new DataBatch(data, 3000, 2);
        this.cluster.sendToCpu(db);
        assertEquals(this.cpu.getTicksProcessed(), 0);
        for(int i = 1; i < 100; i++){
            this.cpu.processNextTick();
            assertEquals(this.cpu.getTicksProcessed(), i%(data.getTicksToProcess()));
        }
    }
}