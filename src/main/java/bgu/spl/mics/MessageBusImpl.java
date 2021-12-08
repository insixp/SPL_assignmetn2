package bgu.spl.mics;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * The {@link MessageBusImpl class is the implementation of the MessageBus interface.
 * Write your implementation here!
 * Only private fields and methods can be added to this class.
 */
public class MessageBusImpl implements MessageBus {

	List<BlockingQueue<Message>> Q_list;

	private class MessageQ {
		BlockingQueue<Message> Q;
		Class<?> classType;

		public MessageQ(Class<?> classType){
			this.classType = classType;
			this.Q = new LinkedBlockingQueue<Message>();
		}
	}

	private static class singletonHolder{
		private static MessageBusImpl instance = new MessageBusImpl();
	}

	private MessageBusImpl(){
		this.Q_list = new ArrayList<BlockingQueue<Message>>();
	}

	public static MessageBusImpl getInstance(){
		return singletonHolder.instance;
	}

	@Override
	public <T> void subscribeEvent(Class<? extends Event<T>> type, MicroService m) {

	}

	@Override
	public void subscribeBroadcast(Class<? extends Broadcast> type, MicroService m) {
		// TODO Auto-generated method stub

	}

	@Override
	public <T> void complete(Event<T> e, T result) {
		// TODO Auto-generated method stub

	}

	@Override
	public void sendBroadcast(Broadcast b) {
		// TODO Auto-generated method stub

	}

	
	@Override
	public <T> Future<T> sendEvent(Event<T> e) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void register(MicroService m) {
		// TODO Auto-generated method stub

	}

	@Override
	public void unregister(MicroService m) {
		// TODO Auto-generated method stub

	}

	@Override
	public Message awaitMessage(MicroService m) throws InterruptedException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean isSubscribed(Class<? extends Message> type, MicroService m) {
		return false;
	}

	@Override
	public <T> boolean isComplete(Event<T> e) {
		return false;
	}

	@Override
	public boolean isRegister(MicroService m) {
		return false;
	}

	@Override
	public int messagesWaitingForMicroService(MicroService m) {
		return 0;
	}
}
