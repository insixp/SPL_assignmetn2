package bgu.spl.mics.application.services;
import bgu.spl.mics.application.messages.*;
import bgu.spl.mics.Broadcast;
import bgu.spl.mics.Callback;
import bgu.spl.mics.MessageBusImpl;
import bgu.spl.mics.MicroService;
import bgu.spl.mics.application.objects.ConfrenceInformation;

/**
 * Conference service is in charge of
 * aggregating good results and publishing them via the {@link },
 * after publishing results the conference will unregister from the system.
 * This class may not hold references for objects which it is not responsible for.
 *
 * You can add private fields and public methods to this class.
 * You MAY change constructor signatures and even add new public constructors.
 */
public class ConferenceService extends MicroService {
    ConfrenceInformation conInf;
    MessageBusImpl messegebus = MessageBusImpl.getInstance();

    public ConferenceService(String name,int date) {
        super("Conference: " + name);
        conInf = new ConfrenceInformation(name,date);
    }
    /**
     *   the initialization of the service, register it to the Messegebus, and creates the right callbacks for the service
     *  @PRE: this.conInf!=null
     *  @POST: this.conInf==@pre this.conInf
     **/
    public ConfrenceInformation getConferenceInformation() { return this.conInf; }

    @Override
    /**
     *   the initialization of the service, register it to the Messegebus, and creates the right callbacks for the service
     *  @PRE: this.messegebusIns.isRegister(this)==false
     *  @POST: this.messegebusIns.isRegister(this)==true
     **/
    protected void initialize() {
        MessageBusImpl.getInstance().register(this);
        Callback<PublishResultsEvent>ResEv= e-> {
            this.conInf.addResult(e.getModel());
        };
        Callback<TickBroadcast> tickBrod= e->{
            if(e.getTick() == this.conInf.getDate()){
                this.sendBroadcast(this.conInf.getPulishBrod());
                this.messegebus.unregister(this);
                this.terminate();
            }
        };
        this.subscribeBroadcast(TickBroadcast.class,tickBrod);
        this.subscribeEvent(PublishResultsEvent.class,ResEv);

    }
}
