package bgu.spl.mics.application.services;

import bgu.spl.mics.Broadcast;
import bgu.spl.mics.Callback;
import bgu.spl.mics.MessageBusImpl;
import bgu.spl.mics.MicroService;
import bgu.spl.mics.application.messages.TerminateBroadcast;
import bgu.spl.mics.application.messages.TickBroadcast;

import java.util.Timer;
import java.util.TimerTask;

/**
 * TimeService is the global system timer There is only one instance of this micro-service.
 * It keeps track of the amount of ticks passed since initialization and notifies
 * all other micro-services about the current time tick using {@link TickBroadcast}.
 * This class may not hold references for objects which it is not responsible for.
 * 
 * You can add private fields and public methods to this class.
 * You MAY change constructor signatures and even add new public constructors.
 */
public class TimeService extends MicroService{

	private MessageBusImpl msgBus;
	private int tickTime;
	private int duration;
	private tickTask tickT;

	private static class tickTask extends TimerTask{

		MessageBusImpl msgBus;
		private int ticksPassed;
		private int duration;

		public tickTask(MessageBusImpl msgBus, int duration){
			this.msgBus = msgBus;
			this.ticksPassed = 0;
			this.duration = duration;
		}

		@Override
		public void run() {
			if(this.ticksPassed <= this.duration){
				this.ticksPassed++;
				this.msgBus.sendBroadcast(new TickBroadcast(this.ticksPassed));
			}else {
				this.msgBus.sendBroadcast(new TerminateBroadcast());
				this.cancel();
			}
		}

		public int getTicksPassed() { return this.ticksPassed; }
	}

	public TimeService(int tickTime ,int duration) {
		super("Time service");
		this.tickTime = tickTime;
		this.duration = duration;
		this.msgBus = MessageBusImpl.getInstance();
		this.tickT = new tickTask(msgBus, this.duration/this.tickTime);
	}

	@Override
	protected void initialize() {
		msgBus.register(this);
		Timer timer = new Timer(true);
		timer.scheduleAtFixedRate(this.tickT, 0, this.tickTime);
	}
}
