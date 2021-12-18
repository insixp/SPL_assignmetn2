package bgu.spl.mics;
import bgu.spl.mics.application.messages.TickBroadcast;

import java.util.Collections;
import java.util.List;
import java.util.concurrent.*;

/**
 * The {@link MessageBusImpl class is the implementation of the MessageBus interface.
 * Write your implementation here!
 * Only private fields and methods can be added to this class.
 */
public class MessageBusImpl implements MessageBus {

	Object Sync = new Object();
	ConcurrentHashMap<MicroService, BlockingQueue<Message>> MSToQHT;	//	Micro service to Queue
	ConcurrentHashMap<Class<? extends Message>, ConcurrentLinkedQueue<MicroService>> MessageToMSHT;	//	Message to Micro service queue helps with round robin too.
	ConcurrentHashMap<Event, Future> EventToFutureHT;	//	Message to Future

	private static class singletonHolder{
		private static MessageBusImpl instance = new MessageBusImpl();
	}

	private MessageBusImpl(){
		this.MSToQHT = new ConcurrentHashMap<>();
		this.MessageToMSHT = new ConcurrentHashMap<>();
		this.EventToFutureHT = new ConcurrentHashMap<>();
	}

	public static MessageBusImpl getInstance(){
		return singletonHolder.instance;
	}

	@Override
	public <T> void subscribeEvent(Class<? extends Event<T>> type, MicroService m) {
		//	add Microservice to the MessageToMSHT
		subscribeMicroServiceToMessages(type, m);
	}

	@Override
	public void subscribeBroadcast(Class<? extends Broadcast> type, MicroService m) {
		//	add Microservice to the MessageToMSHT
		subscribeMicroServiceToMessages(type, m);
	}

	@Override
	public <T> void complete(Event<T> e, T result) {
		Future<T> future = this.EventToFutureHT.get(e);
		future.resolve(result);
	}

	@Override
	public void sendBroadcast(Broadcast b) {
		ConcurrentLinkedQueue<MicroService> BroadcastQ = this.MessageToMSHT.get(b.getClass());
		if(BroadcastQ != null && !BroadcastQ.isEmpty()) {
			for (MicroService microService : BroadcastQ) {
				BlockingQueue<Message> MsgQ = MSToQHT.get(microService);
				MsgQ.add(b);
			}
		}
	}

	
	@Override
	public <T> Future<T> sendEvent(Event<T> e) {
		ConcurrentLinkedQueue<MicroService> MSQ = this.MessageToMSHT.get(e.getClass());
		if(MSQ != null && !MSQ.isEmpty()) {
			MicroService MS = MSQ.poll();
			MSQ.add(MS);
			BlockingQueue<Message> MsgQ = this.MSToQHT.get(MS);
			Future<T> future = new Future<T>();
			this.EventToFutureHT.put(e, future);
			MsgQ.add(e);
			return future;
		}
		return null;
	}

	@Override
	public void register(MicroService m) {
		//	Create new Q for the Micro service;
		synchronized (Sync) {
			this.MSToQHT.put(m, new LinkedBlockingQueue<>());
		}
	}

	@Override
	public void unregister(MicroService m) {
		//	First remove Microservice from any messages he's subscribed too.
		synchronized (Sync) {
			List<ConcurrentLinkedQueue<MicroService>> MSQ_list = Collections.list(this.MessageToMSHT.elements());
			for (ConcurrentLinkedQueue<MicroService> MSQ : MSQ_list) {
				if (MSQ.contains(m))
					MSQ.remove(m);
			}
			this.MSToQHT.remove(m);
		}
	}

	@Override
	public Message awaitMessage(MicroService m) throws InterruptedException {
		BlockingQueue<Message> MsgQ = this.MSToQHT.get(m);
		if(MsgQ == null)
			throw new InterruptedException("Microservice " + m.getName() + " is unregistered");
		return MsgQ.take();
	}

	@Override
	public boolean isSubscribed(Class<? extends Message> type, MicroService m) {
		ConcurrentLinkedQueue<MicroService> MSQ = this.MessageToMSHT.get(type);
		if(MSQ == null)
			return false;
		return MSQ.contains(m);
	}

	@Override
	public <T> boolean isComplete(Event<T> e) {
		Future f = this.EventToFutureHT.get(e);
		if(f == null)
			return false;
		return f.isDone();
	}

	@Override
	public boolean isRegister(MicroService m) {
		return this.MSToQHT.containsKey(m);
	}

	@Override
	public int messagesWaitingForMicroService(MicroService m) {
		BlockingQueue<Message> MsgQ = this.MSToQHT.get(m);
		if(MsgQ == null)
			return -1;
		return MsgQ.size();
	}

	private void subscribeMicroServiceToMessages(Class<? extends Message> type, MicroService m){
		synchronized (Sync){
			if(this.isRegister(m) & !this.isSubscribed(type, m)){
				ConcurrentLinkedQueue<MicroService> MSQ = this.MessageToMSHT.get(type);
				if (MSQ != null) {
					MSQ.add(m);
				} else {
					MSQ = new ConcurrentLinkedQueue<>();
					MSQ.add(m);
					this.MessageToMSHT.put(type, MSQ);
				}
			}
		}
	}
}
