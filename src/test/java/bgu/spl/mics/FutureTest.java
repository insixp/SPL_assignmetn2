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
        assertEquals(this.future.get(), test_string);
    }

    @Test
    public void testResolve() {
        String test_string = "This string is just a test!";
        this.future.resolve(test_string);
        assertEquals(this.future.get(), test_string);
    }

    @Test
    public void testIsDone() {
        assertEquals(this.future.isDone(), false);
        this.future.resolve("");
        assertEquals(this.future.isDone(), true);
    }

    @Test
    public void testTimeoutGet() {
        assertEquals(this.future.isDone(), false);
        String result = this.future.get(3, TimeUnit.SECONDS);
        assertEquals(result, null);
        assertEquals(this.future.isDone(), false);
        String return_val = "Test of a run";
        this.future.resolve(return_val);
        result = this.future.get(3, TimeUnit.SECONDS);
        assertEquals(result, return_val);
        assertEquals(this.future.isDone(), true);

    }
}