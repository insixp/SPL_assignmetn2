package bgu.spl.mics;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.*;

/**
 * The {@link MessageBusImpl class is the implementation of the MessageBus interface.
 * Write your implementation here!
 * Only private fields and methods can be added to this class.
 */
public class MessageBusImpl implements MessageBus {

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
		if(!BroadcastQ.isEmpty()) {
			for (MicroService microService : BroadcastQ) {
				BlockingQueue<Message> MSQ = MSToQHT.get(microService);
				MSQ.add(b);
			}
		}
	}

	
	@Override
	public <T> Future<T> sendEvent(Event<T> e) {
		ConcurrentLinkedQueue<MicroService> MSQ = this.MessageToMSHT.get(e.getClass());
		if(!MSQ.isEmpty()) {
			MicroService MS = MSQ.poll();
			BlockingQueue<Message> MsgQ = this.MSToQHT.get(MS);
			Future<T> future = new Future<>();
			this.EventToFutureHT.put(e, future);
			MsgQ.add(e);
			return future;
		}
		return null;
	}

	@Override
	public void register(MicroService m) {
		//	Create new Q for the Micro service;
		this.MSToQHT.put(m, new LinkedBlockingQueue<>());
	}

	@Override
	public void unregister(MicroService m) {
		//	First remove Microservice from any messages he's subscribed too.
		List<ConcurrentLinkedQueue<MicroService>> MSQ_list = Collections.list(this.MessageToMSHT.elements());
		for(ConcurrentLinkedQueue<MicroService> MSQ : MSQ_list){
			if(MSQ.contains(m))
				MSQ.remove(m);
		}
		this.MSToQHT.remove(m);
	}

	@Override
	public Message awaitMessage(MicroService m) throws InterruptedException {
		BlockingQueue<Message> MsgQ = null;
		synchronized (this.MSToQHT) {
			if(this.MSToQHT.containsKey(m))
				MsgQ = this.MSToQHT.get(m);
		}
		if(MsgQ != null) {	//	ask if synchronize can fuck up between this line and the next.
			synchronized (MsgQ) {
				while(MsgQ != null && MsgQ.isEmpty())
					wait();
				if(MsgQ != null)
					return MsgQ.poll();
			}
		}
		return null;
	}

	@Override
	public boolean isSubscribed(Class<? extends Message> type, MicroService m) {
		synchronized (this.MessageToMSHT) {
			if (this.MessageToMSHT.containsKey(type))
				return this.MessageToMSHT.get(type).contains(m);
			return false;
		}
	}

	@Override
	public <T> boolean isComplete(Event<T> e) {
		synchronized (this.EventToFutureHT) {
			if (this.EventToFutureHT.containsKey(e))
				return this.EventToFutureHT.get(e).isDone();
			return false;
		}
	}

	@Override
	public boolean isRegister(MicroService m) {
		return this.MSToQHT.containsKey(m);
	}

	@Override
	public int messagesWaitingForMicroService(MicroService m) {
		synchronized (this.MSToQHT) {
			if (this.MSToQHT.containsKey(m))
				return this.MSToQHT.get(m).size();
			return -1;
		}
	}

	private void subscribeMicroServiceToMessages(Class<? extends Message> type, MicroService m){
		//if (this.isRegister(m) & !this.isSubscribed(type, m)){
			//	add Microservice to the MessageToMSHT
		ConcurrentLinkedQueue<MicroService> MSQ = null;
		synchronized (this.MessageToMSHT) {
			if(this.MessageToMSHT.containsKey(type))
				MSQ = this.MessageToMSHT.get(type);
		}
		if (MSQ != null) {
			MSQ.add(m);
		} else {
			MSQ = new ConcurrentLinkedQueue<>();
			MSQ.add(m);
			this.MessageToMSHT.put(type, MSQ);
		}
	}
}
