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
    public void testAddDataBatch(){
        this.cpu = new CPU(3, 32);
        Data data = new Data(Data.Type.Images, 20000);
        DataBatch db = new DataBatch(data, 3000, 2);
        Collection<DataBatch> db_collection = this.cpu.getDataCollection();
        assertEquals(db_collection.contains(db), false);
        this.cpu.addDataBatch(db);
        db_collection = this.cpu.getDataCollection();
        assertEquals(db_collection.size(), 1);
        assertEquals(db_collection.contains(db), true);
    }

    @Test
    public void testSrcDestGpuValid() {
        this.cpu = new CPU(0, 32);
        Data data = new Data(Data.Type.Images, 20000);
        DataBatch db = new DataBatch(data, 3000, 2);
        this.cpu.addDataBatch(db);
        DataBatch proccessed_db = this.cpu.processNextBatch();
        assertEquals(proccessed_db.getGpuId(), db.getGpuId());
    }

    @Test
    public void test32Cores(){
        this.cpu = new CPU(0, 32);
        Data data = new Data(Data.Type.Images, 20000);
        DataBatch db = new DataBatch(data, 3000, 2);
        this.cpu.addDataBatch(db);
        this.cpu.processNextBatch();
    }
}