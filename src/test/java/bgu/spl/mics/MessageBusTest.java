package bgu.spl.mics;

import bgu.spl.mics.application.objects.Data;
import junit.framework.TestCase;
import org.junit.Test;

public class MessageBusTest extends TestCase {

    private MessageBus msgBus;

    private class testMicroService extends MicroService{

        public boolean ran = false;

        public testMicroService(String name) {
            super(name);
        }

        @Override
        protected void initialize() {
            msgBus.register(this);
            this.ran = true;
        }
    }
    private class testBroadcastMSG implements Broadcast {}
    private class testEventMSG implements Event<String>{}
    private class testEventMSG2 implements Event<String>{}

    @Test
    public void setUp() throws Exception {
        super.setUp();
        this.msgBus = new MessageBusImpl();
    }

    @Test
    public void tearDown() throws Exception {
    }

    //  Tests performed:
    //      * Register
    //      * Subscribe broadcast
    //      * Send broadcast
    //      * Unregister
    //  Checks valid communication between subscriber and sender
    public void testSubscribeSendEvent() {
        testMicroService testMS = new testMicroService("tesToro");
        testEventMSG event = new testEventMSG();
        this.msgBus.subscribeEvent(testEventMSG.class, testMS);
        this.msgBus.sendEvent(event);
        try{
            Message msg = this.msgBus.awaitMessage(testMS);
            assertEquals(msg, event);
        } catch (InterruptedException e) { assertEquals(true, false);}
        this.msgBus.unregister(testMS);
    }

    //  Tests performed:
    //      * Register
    //      * Subscribe event
    //      * Send event
    //      * Unregister
    //  Checks valid communication between subscriber and sender
    public void testSubscribeSendBroadcast() {
        testMicroService testMS = new testMicroService("tesToro");
        testBroadcastMSG broadcast = new testBroadcastMSG();
        this.msgBus.subscribeBroadcast(testBroadcastMSG.class, testMS);
        this.msgBus.sendBroadcast(broadcast);
        try{
            Message msg = this.msgBus.awaitMessage(testMS);
            assertEquals(msg, broadcast);
        } catch (InterruptedException e) { assertEquals(true, false);}
        this.msgBus.unregister(testMS);
    }

    //  Tests performed:
    //      * Register
    //      * Subscribe event
    //      * Send event
    //      * Complete
    //      * Unregister
    //  This test makes sure the complete sets the correct value of the correct variable.
    public void testComplete() {
        testMicroService testMS = new testMicroService("tesToro");
        testMicroService testMS2 = new testMicroService("testToto");
        testEventMSG eventMSG = new testEventMSG();
        testEventMSG2 eventMSG2 = new testEventMSG2();
        this.msgBus.subscribeEvent(testEventMSG.class, testMS);
        this.msgBus.subscribeEvent(testEventMSG2.class, testMS2);
        Future<String> future1 = this.msgBus.sendEvent(eventMSG);
        Future<String> future2 = this.msgBus.sendEvent(eventMSG2);
        assertNotNull(future1);
        assertNotNull(future2);
        assertEquals(future1.isDone(), false);
        assertEquals(future2.isDone(), false);
        String result1 = "String 1";
        String result2 = "String 2";
        this.msgBus.complete(eventMSG, result1);
        this.msgBus.complete(eventMSG2, result2);
        assertEquals(future1.isDone(), true);
        assertEquals(future2.isDone(), true);
        assertEquals(future1.get(), result1);
        assertEquals(future2.get(), result2);
    }
}