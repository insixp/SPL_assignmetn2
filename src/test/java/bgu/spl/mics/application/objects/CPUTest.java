package bgu.spl.mics.application.objects;

import junit.framework.TestCase;
import org.junit.Test;

import java.util.Collection;

public class CPUTest extends TestCase {

    private CPU cpu;

    public void setUp() throws Exception {
        super.setUp();
    }

    public void tearDown() throws Exception {
    }

    @Test
    public void testAddGetDataBatch(){
        this.cpu = new CPU(3, 32);
        Data data = new Data(Data.Type.Images, 20000);
        DataBatch db = new DataBatch(data, 3000, 2);
        Collection<DataBatch> db_collection = this.cpu.getDataBatches();
        assertEquals(false, db_collection.contains(db));
        this.cpu.addDataBatch(db);
        db_collection = this.cpu.getDataBatches();
        assertEquals(1, db_collection.size());
        assertEquals(true, db_collection.contains(db));
    }

    @Test
    public void testProcessedData() {
        this.cpu = new CPU(0, 32);
        Data data = new Data(Data.Type.Images, 20000);
        DataBatch db = new DataBatch(data, 3000, 2);
        this.cpu.addDataBatch(db);
        DataBatch processed_db = this.cpu.processNextTick();
        assertEquals(processed_db, null);
        processed_db = this.cpu.processNextTick();
        assertEquals(processed_db, null);
        processed_db = this.cpu.processNextTick();
        assertEquals(processed_db, null);
        processed_db = this.cpu.processNextTick();
        assertEquals(processed_db.getGpuId(), db.getGpuId());
        Data processed_Data = processed_db.getData();
        assertEquals(processed_db.getStartIndex(), db.getStartIndex());
        assertEquals(processed_Data.getProcessed(), 1000);
        assertEquals(processed_Data.getSize(), data.getSize());
        assertEquals(processed_Data.getType(), data.getType());
    }

    @Test
    public void test32CoresImage(){
        this.cpu = new CPU(0, 32);
        Data data = new Data(Data.Type.Images, 20000);
        DataBatch db = new DataBatch(data, 3000, 2);
        this.cpu.addDataBatch(db);
        //  Image
        for(int i = 0; i < 4;i++){
            DataBatch processedDB = this.cpu.processNextTick();
            if(i == 3)
                assertNotNull(processedDB);
            else
                assertNull(processedDB);
        }
        data = new Data(Data.Type.Text, 5000);
        db = new DataBatch(data, 2000, 5);
        this.cpu.addDataBatch(db);
        //  Text
        for(int i = 0; i < 2;i++){
            DataBatch processedDB = this.cpu.processNextTick();
            if(i == 1)
                assertNotNull(processedDB);
            else
                assertNull(processedDB);
        }
        //  Tabular
        data = new Data(Data.Type.Tabular, 5000);
        db = new DataBatch(data, 2000, 5);
        this.cpu.addDataBatch(db);
        for(int i = 0; i < 1;i++){
            DataBatch processedDB = this.cpu.processNextTick();
            assertNotNull(processedDB);
        }
    }

    @Test
    public void test16CoresImage(){
        this.cpu = new CPU(0, 16);
        Data data = new Data(Data.Type.Images, 20000);
        DataBatch db = new DataBatch(data, 3000, 2);
        this.cpu.addDataBatch(db);
        //  Image
        for(int i = 0; i < 8;i++){
            DataBatch processedDB = this.cpu.processNextTick();
            if(i == 7)
                assertNotNull(processedDB);
            else
                assertNull(processedDB);
        }
        data = new Data(Data.Type.Text, 5000);
        db = new DataBatch(data, 2000, 5);
        this.cpu.addDataBatch(db);
        //  Text
        for(int i = 0; i < 4;i++){
            DataBatch processedDB = this.cpu.processNextTick();
            if(i == 3)
                assertNotNull(processedDB);
            else
                assertNull(processedDB);
        }
        //  Tabular
        data = new Data(Data.Type.Tabular, 5000);
        db = new DataBatch(data, 2000, 5);
        this.cpu.addDataBatch(db);
        for(int i = 0; i < 2;i++){
            DataBatch processedDB = this.cpu.processNextTick();
            if(i == 1)
                assertNotNull(processedDB);
            else
                assertNull(processedDB);
        }
    }

    @Test
    public void test8CoresImage(){
        this.cpu = new CPU(0, 8);
        Data data = new Data(Data.Type.Images, 20000);
        DataBatch db = new DataBatch(data, 3000, 2);
        this.cpu.addDataBatch(db);
        //  Image
        for(int i = 0; i < 16;i++){
            DataBatch processedDB = this.cpu.processNextTick();
            if(i == 15)
                assertNotNull(processedDB);
            else
                assertNull(processedDB);
        }
        data = new Data(Data.Type.Text, 5000);
        db = new DataBatch(data, 2000, 5);
        this.cpu.addDataBatch(db);
        //  Text
        for(int i = 0; i < 8;i++){
            DataBatch processedDB = this.cpu.processNextTick();
            if(i == 7)
                assertNotNull(processedDB);
            else
                assertNull(processedDB);
        }
        //  Tabular
        data = new Data(Data.Type.Tabular, 5000);
        db = new DataBatch(data, 2000, 5);
        this.cpu.addDataBatch(db);
        for(int i = 0; i < 4;i++){
            DataBatch processedDB = this.cpu.processNextTick();
            if(i == 3)
                assertNotNull(processedDB);
            else
                assertNull(processedDB);
        }
    }

    public void testGetId() {
        this.cpu = new CPU(98, 32);
        assertEquals(this.cpu.getId(), 98);
    }

    @Test
    public void testGetCores(){
        this.cpu = new CPU(98, 24);
        assertEquals(24, this.cpu.getCores());
    }

    @Test
    public void testGetActive(){
        this.cpu = new CPU(98, 24);
        assertEquals(false, this.cpu.getActive());
        this.cpu.addDataBatch(new DataBatch(new Data(Data.Type.Images, 20000), 5000, 3));
        assertEquals(true, this.cpu.getActive());
    }

    public void testGetTicksProcessed() {
        this.cpu = new CPU(95, 23);
        Data data = new Data(Data.Type.Images, 20000);
        DataBatch db = new DataBatch(data, 3000, 2);
        this.cpu.addDataBatch(db);
        assertEquals(this.cpu.getTicksProcessed(), 0);
        for(int i = 1; i < 100; i++){
            this.cpu.processNextTick();
            assertEquals(this.cpu.getTicksProcessed(), i%(data.getTicksToProcess()));
        }
    }
}