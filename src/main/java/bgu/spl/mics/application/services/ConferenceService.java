package bgu.spl.mics.application.services;
import bgu.spl.mics.application.messages.PublishConferenceBroadcast;
import bgu.spl.mics.Broadcast;
import bgu.spl.mics.Callback;
import bgu.spl.mics.MessageBusImpl;
import bgu.spl.mics.MicroService;
import bgu.spl.mics.application.messages.TickBroadcast;
import bgu.spl.mics.application.objects.ConfrenceInformation;

/**
 * Conference service is in charge of
 * aggregating good results and publishing them via the {@link PublishConfrenceBroadcast},
 * after publishing results the conference will unregister from the system.
 * This class may not hold references for objects which it is not responsible for.
 *
 * You can add private fields and public methods to this class.
 * You MAY change constructor signatures and even add new public constructors.
 */
public class ConferenceService extends MicroService {
    ConfrenceInformation conInf;
    public ConferenceService(String name,int date) {
        super("Conference: " + name);
        // TODO Implement this
        conInf=new ConfrenceInformation(name,date);////fill information
    }

    @Override
    protected void initialize() {
        // TODO Implement this
        MessageBusImpl.getInstance().register(this);
        Callback<TickBroadcast> tickBrod= e->{ this.conInf.nextTick();};
        this.subscribeBroadcast(TickBroadcast.class,tickBrod);

    }
}