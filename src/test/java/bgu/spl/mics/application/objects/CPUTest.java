package bgu.spl.mics.application.objects;

import junit.framework.TestCase;
import org.junit.Test;
import java.util.Collection;

public class CPUTest extends TestCase {

    private int BATCH_SIZE = 1000;
    private CPU cpu32;
    private CPU cpu16;
    private CPU cpu8;
    private Cluster cluster;

    public void setUp() throws Exception {
        super.setUp();
        this.cluster = Cluster.getInstance();
        this.cpu32 = new CPU(0, 32, 1000, this.cluster);
        this.cpu32.clusterUnregister();
        this.cpu16 = new CPU(1, 16, 1000, this.cluster);
        this.cpu16.clusterUnregister();
        this.cpu8 = new CPU(2, 8, 1000, this.cluster);
        this.cpu8.clusterUnregister();
    }

    public void tearDown() throws Exception {
    }

    @Test
    public void testAddGetDataBatch(){
        this.cluster.registerCPU(0);
        this.cluster.registerGPU(0);
        Data data = new Data(Data.Type.Images, 1000);
        DataBatch db = new DataBatch(data, 3000, 0);
        Collection<DataBatch> db_collection = this.cpu32.getDataBatches();
        assertEquals(false, db_collection.contains(db));
        this.cluster.sendToCpu(db);
        this.cpu32.processNextTick();
        db_collection = this.cpu32.getDataBatches();
        assertEquals(1, db_collection.size());
        assertEquals(true, db_collection.contains(db));
        this.cluster.unregisterGPU(0);
        this.cluster.unregisterCPU(0);
    }

    @Test
    public void testProcessedData() {
        this.cluster.registerCPU(0);
        this.cluster.registerGPU(1);
        Data data = new Data(Data.Type.Images, 1000);
        DataBatch db = new DataBatch(data, 3000, 1);
        this.cluster.sendToCpu(db);
        this.cpu32.processNextTick();
        assertNull(this.cluster.readByGpu(1));
        this.cpu32.processNextTick();
        assertNull(this.cluster.readByGpu(1));
        this.cpu32.processNextTick();
        assertNull(this.cluster.readByGpu(1));
        this.cpu32.processNextTick();
        assertNull(this.cluster.readByGpu(1));
        this.cpu32.processNextTick();
        DataBatch processed_db = this.cluster.readByGpu(1);
        assertNotNull(processed_db);
        assertEquals(db.getGpuId(), processed_db.getGpuId());
        Data processed_Data = processed_db.getData();
        assertEquals(db.getStartIndex(), processed_db.getStartIndex());
        assertEquals(1000, processed_Data.getProcessed());
        assertEquals(data.getSize(), processed_Data.getSize());
        assertEquals(data.getType(), processed_Data.getType());
        this.cluster.unregisterGPU(1);
        this.cluster.unregisterCPU(0);
    }

    @Test
    public void test32CoresImage(){
        this.cluster.registerCPU(0);
        this.cluster.registerGPU(2);
        this.cluster.registerGPU(3);
        this.cluster.registerGPU(4);
        Data data = new Data(Data.Type.Images, 1000);
        DataBatch db = new DataBatch(data, 3000, 2);
        this.cluster.sendToCpu(db);
        this.cpu32.processNextTick();
        //  Image
        for(int i = 0; i < 4;i++){
            this.cpu32.processNextTick();
            DataBatch processedDB = this.cluster.readByGpu(2);
            if(i == 3)
                assertNotNull(processedDB);
            else
                assertNull(processedDB);
        }
        data = new Data(Data.Type.Text, 1000);
        db = new DataBatch(data, 2000, 3);
        this.cluster.sendToCpu(db);
        this.cpu32.processNextTick();
        //  Text
        for(int i = 0; i < 2;i++){
            this.cpu32.processNextTick();
            DataBatch processedDB = this.cluster.readByGpu(3);
            if(i == 1)
                assertNotNull(processedDB);
            else
                assertNull(processedDB);
        }
        //  Tabular
        data = new Data(Data.Type.Tabular, 1000);
        db = new DataBatch(data, 2000, 4);
        this.cluster.sendToCpu(db);
        this.cpu32.processNextTick();
        for(int i = 0; i < 1;i++){
            this.cpu32.processNextTick();
            DataBatch processedDB = this.cluster.readByGpu(4);
            assertNotNull(processedDB);
        }
        this.cluster.unregisterGPU(2);
        this.cluster.unregisterGPU(3);
        this.cluster.unregisterGPU(4);
        this.cluster.unregisterCPU(0);
    }

    @Test
    public void test16CoresImage(){
        this.cluster.registerCPU(1);
        this.cluster.registerGPU(5);
        this.cluster.registerGPU(6);
        this.cluster.registerGPU(7);
        Data data = new Data(Data.Type.Images, 1000);
        DataBatch db = new DataBatch(data, 3000, 5);
        this.cluster.sendToCpu(db);
        this.cpu16.processNextTick();
        //  Image
        for(int i = 0; i < 8;i++){
            this.cpu16.processNextTick();
            DataBatch processedDB = this.cluster.readByGpu(5);
            if(i == 7)
                assertNotNull(processedDB);
            else
                assertNull(processedDB);
        }
        data = new Data(Data.Type.Text, 1000);
        db = new DataBatch(data, 2000, 6);
        this.cluster.sendToCpu(db);
        this.cpu16.processNextTick();
        //  Text
        for(int i = 0; i < 4;i++){
            this.cpu16.processNextTick();
            DataBatch processedDB = this.cluster.readByGpu(6);
            if(i == 3)
                assertNotNull(processedDB);
            else
                assertNull(processedDB);
        }
        //  Tabular
        data = new Data(Data.Type.Tabular, 1000);
        db = new DataBatch(data, 2000, 7);
        this.cluster.sendToCpu(db);
        this.cpu16.processNextTick();
        for(int i = 0; i < 2;i++){
            this.cpu16.processNextTick();
            DataBatch processedDB = this.cluster.readByGpu(7);
            if(i == 1)
                assertNotNull(processedDB);
            else
                assertNull(processedDB);
        }
        this.cluster.unregisterGPU(5);
        this.cluster.unregisterGPU(6);
        this.cluster.unregisterGPU(7);
        this.cluster.unregisterCPU(1);
    }

    @Test
    public void test8CoresImage(){
        this.cluster.registerCPU(2);
        this.cluster.registerGPU(8);
        this.cluster.registerGPU(9);
        this.cluster.registerGPU(10);
        Data data = new Data(Data.Type.Images, 1000);
        DataBatch db = new DataBatch(data, 3000, 8);
        System.out.println(this.cluster.isCPURegistered(2));
        this.cluster.sendToCpu(db);
        this.cpu8.processNextTick();
        //  Image
        for(int i = 0; i < 16;i++){
            this.cpu8.processNextTick();
            DataBatch processedDB = this.cluster.readByGpu(8);
            if(i == 15)
                assertNotNull(processedDB);
            else
                assertNull(processedDB);
        }
        data = new Data(Data.Type.Text, 1000);
        db = new DataBatch(data, 2000, 9);
        this.cluster.sendToCpu(db);
        this.cpu8.processNextTick();
        //  Text
        for(int i = 0; i < 8;i++){
            this.cpu8.processNextTick();
            DataBatch processedDB = this.cluster.readByGpu(9);
            if(i == 7)
                assertNotNull(processedDB);
            else
                assertNull(processedDB);
        }
        //  Tabular
        data = new Data(Data.Type.Tabular, 1000);
        db = new DataBatch(data, 2000, 10);
        this.cluster.sendToCpu(db);
        this.cpu8.processNextTick();
        for(int i = 0; i < 4;i++){
            this.cpu8.processNextTick();
            DataBatch processedDB = this.cluster.readByGpu(10);
            if(i == 3)
                assertNotNull(processedDB);
            else
                assertNull(processedDB);
        }
        this.cluster.unregisterGPU(8);
        this.cluster.unregisterGPU(9);
        this.cluster.unregisterGPU(10);
        this.cluster.unregisterCPU(2);
    }

    public void testGetId() {
        this.cluster.registerCPU(0);
        assertEquals(this.cpu32.getId(), 0);
        this.cluster.unregisterCPU(0);
    }

    @Test
    public void testGetCores(){
        this.cluster.registerCPU(0);
        assertEquals(32, this.cpu32.getCores());
        this.cluster.unregisterCPU(0);
    }

    @Test
    public void testGetActive(){
        this.cluster.registerCPU(0);
        this.cluster.registerGPU(11);
        assertEquals(false, this.cpu32.isActive());
        this.cluster.sendToCpu(new DataBatch(new Data(Data.Type.Images, 1000), 5000, 11));
        this.cpu32.processNextTick();
        assertEquals(true, this.cpu32.isActive());
        this.cluster.unregisterGPU(11);
        this.cluster.unregisterCPU(0);
    }

    public void testGetTicksProcessed() {
        this.cluster.registerCPU(0);
        this.cluster.registerGPU(12);
        Data data = new Data(Data.Type.Images, 1000);
        DataBatch db = new DataBatch(data, 3000, 12);
        for(int i = 0; i < 40; i++) {
            this.cluster.sendToCpu(db);
        }
        assertEquals(this.cpu32.getTicksProcessed(), 0);
        this.cpu32.processNextTick();
        for(int i = 1; i < 100; i++){
            this.cpu32.processNextTick();
            assertEquals(i%(data.getTicksToProcess()), this.cpu32.getTicksProcessed());
        }
        this.cluster.unregisterGPU(12);
        this.cluster.unregisterCPU(0);
    }
}