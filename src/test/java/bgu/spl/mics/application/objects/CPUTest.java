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
        assertEquals(db_collection.contains(db), false);
        this.cpu.addDataBatch(db);
        db_collection = this.cpu.getDataBatches();
        assertEquals(db_collection.size(), 1);
        assertEquals(db_collection.contains(db), true);
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
            if(i == 3)
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
            if(i == 3)
                assertNotNull(processedDB);
            else
                assertNull(processedDB);
        }
    }

    @Test
    public void test16CoresImage(){
        this.cpu = new CPU(0, 32);
        Data data = new Data(Data.Type.Images, 20000);
        DataBatch db = new DataBatch(data, 3000, 2);
        this.cpu.addDataBatch(db);
        //  Image
        for(int i = 0; i < 4*2;i++){
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
        for(int i = 0; i < 2*2;i++){
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
        for(int i = 0; i < 1*2;i++){
            DataBatch processedDB = this.cpu.processNextTick();
            if(i == 3)
                assertNotNull(processedDB);
            else
                assertNull(processedDB);
        }
    }

    @Test
    public void test8CoresImage(){
        this.cpu = new CPU(0, 32);
        Data data = new Data(Data.Type.Images, 20000);
        DataBatch db = new DataBatch(data, 3000, 2);
        this.cpu.addDataBatch(db);
        //  Image
        for(int i = 0; i < 4*4;i++){
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
        for(int i = 0; i < 2*4;i++){
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
        for(int i = 0; i < 1*4;i++){
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

    public void testGetTicksProcessed() {
        this.cpu = new CPU(95, 23);
        assertEquals(this.cpu.getTicksProcessed(), 0);
        for(int i = 1; i < 100; i++){
            this.cpu.processNextTick();
            assertEquals(this.cpu.getTicksProcessed(), i);
        }
    }
}