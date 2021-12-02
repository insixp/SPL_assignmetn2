package bgu.spl.mics;

import junit.framework.TestCase;
import org.junit.Test;

public class FutureTest extends TestCase {

    Future<Integer> future;

    @Test
    public void setUp() throws Exception {
        super.setUp();
        this.future = new Future<Integer>();
    }

    @Test
    public void tearDown() throws Exception {
    }

    @Test
    public void testGet() {
        assertEquals(true, true);
    }

    @Test
    public void testResolve() {
    }

    @Test
    public void testIsDone() {
    }

    @Test
    public void testTestGet() {
    }
}