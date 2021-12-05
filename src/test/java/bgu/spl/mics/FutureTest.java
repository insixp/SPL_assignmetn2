package bgu.spl.mics;

import bgu.spl.mics.application.objects.Data;
import junit.framework.TestCase;
import org.junit.Test;

import java.util.concurrent.TimeUnit;

public class FutureTest extends TestCase {

    Future<String> future;

    @Test
    public void setUp() throws Exception {
        super.setUp();
        this.future = new Future<String>();
    }

    @Test
    public void tearDown() throws Exception {
    }

    @Test
    public void testGet() {
        assertEquals(this.future.isDone(), false);
        String test_string = "This string is just a test!";
        this.future.resolve(test_string);
        assertEquals(test_string, this.future.get());
    }

    @Test
    public void testResolve() {
        String test_string = "This string is just a test!";
        this.future.resolve(test_string);
        assertEquals(test_string, this.future.get());
    }

    @Test
    public void testIsDone() {
        assertEquals(false, this.future.isDone());
        this.future.resolve("");
        assertEquals(true, this.future.isDone());
    }

    @Test
    public void testTimeoutGet() {
        assertEquals(false, this.future.isDone());
        String result = this.future.get(3, TimeUnit.SECONDS);
        assertEquals(null, result);
        assertEquals(false, this.future.isDone());
        String return_val = "Test of a run";
        this.future.resolve(return_val);
        result = this.future.get(3, TimeUnit.SECONDS);
        assertEquals(return_val, result);
        assertEquals(true, this.future.isDone());

    }
}