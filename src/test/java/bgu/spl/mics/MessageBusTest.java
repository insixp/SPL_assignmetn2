package bgu.spl.mics;

import bgu.spl.mics.application.objects.Data;
import junit.framework.TestCase;
import org.junit.Test;

public class MessageBusTest extends TestCase {

    private MessageBus msgBus;

    private class testMicroService2 extends MicroService {
        public testMicroService2(String name) { super(name); }
        protected void initialize() {}
    }

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
    private class testEventMSG implements Event<String>{
        String name = "message1";
    }
    private class testEventMSG2 implements Event<String>{
        String name = "message2";
    }

    @Test
    public void setUp() throws Exception {
        super.setUp();
        this.msgBus = MessageBusImpl.getInstance();
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
        testMS.initialize();
        testEventMSG event = new testEventMSG();
        this.msgBus.subscribeEvent(testEventMSG.class, testMS);
        this.msgBus.sendEvent(event);
        try{
            Message msg = this.msgBus.awaitMessage(testMS);
            assertEquals(event, msg);
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
        testMS.initialize();
        testBroadcastMSG broadcast = new testBroadcastMSG();
        this.msgBus.subscribeBroadcast(testBroadcastMSG.class, testMS);
        this.msgBus.sendBroadcast(broadcast);
        try{
            Message msg = this.msgBus.awaitMessage(testMS);
            assertEquals(broadcast, msg);
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
        testMS.initialize();
        testMicroService testMS2 = new testMicroService("testToto");
        testMS2.initialize();
        testEventMSG eventMSG = new testEventMSG();
        testEventMSG2 eventMSG2 = new testEventMSG2();
        this.msgBus.subscribeEvent(testEventMSG.class, testMS);
        this.msgBus.subscribeEvent(testEventMSG2.class, testMS2);
        assertEquals(0, this.msgBus.messagesWaitingForMicroService(testMS));
        assertEquals(0, this.msgBus.messagesWaitingForMicroService(testMS2));
        Future<String> future1 = this.msgBus.sendEvent(eventMSG);
        Future<String> future2 = this.msgBus.sendEvent(eventMSG2);
        assertEquals(1, this.msgBus.messagesWaitingForMicroService(testMS));
        assertEquals(1, this.msgBus.messagesWaitingForMicroService(testMS2));
        assertNotNull(future1);
        assertNotNull(future2);
        assertEquals(false, future1.isDone());
        assertEquals(false, future2.isDone());
        String result1 = "String 1";
        String result2 = "String 2";
        assertEquals(false, this.msgBus.isComplete(eventMSG));
        assertEquals(false, this.msgBus.isComplete(eventMSG2));
        this.msgBus.complete(eventMSG, result1);
        this.msgBus.complete(eventMSG2, result2);
        assertEquals(true, this.msgBus.isComplete(eventMSG));
        assertEquals(true, this.msgBus.isComplete(eventMSG2));
        assertEquals(true, future1.isDone());
        assertEquals(true, future2.isDone());
        assertEquals(result1, future1.get());
        assertEquals(result2, future2.get());
        this.msgBus.unregister(testMS);
        this.msgBus.unregister(testMS2);
    }

    public void testIsSubscribed() {
        testMicroService testMS = new testMicroService("tesToro");
        testMS.initialize();
        testEventMSG eventMSG = new testEventMSG();
        this.msgBus.subscribeEvent(testEventMSG.class, testMS);
        assertEquals(true, this.msgBus.isSubscribed(testEventMSG.class, testMS));
        assertEquals(false, this.msgBus.isSubscribed(testBroadcastMSG.class, testMS));
    }

    public void testIsRegister() {
        testMicroService testMS = new testMicroService("tesToro");
        testMS.initialize();
        assertEquals(true, this.msgBus.isRegister(testMS));
        testMicroService2 testMS2 = new testMicroService2("failoro");
        testMS2.initialize();
        assertEquals(false, this.msgBus.isRegister(testMS2));
    }
}