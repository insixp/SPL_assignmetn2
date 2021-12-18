package bgu.spl.mics.application.services;

import bgu.spl.mics.*;
import bgu.spl.mics.application.messages.TerminateBroadcast;
import bgu.spl.mics.application.messages.TrainModelEvent;
import bgu.spl.mics.application.messages.TestModelEvent;
import bgu.spl.mics.application.messages.TickBroadcast;
import bgu.spl.mics.application.objects.Cluster;
import bgu.spl.mics.application.objects.Data;
import bgu.spl.mics.application.objects.GPU;
import bgu.spl.mics.application.objects.Model;

import java.util.Collection;
import java.util.Iterator;
import java.util.Queue;
import java.util.concurrent.LinkedBlockingQueue;

import static bgu.spl.mics.application.objects.Model.Status.Tested;

/**
 * GPU service is responsible for handling the
 * {@link TrainModelEvent} and {@link TestModelEvent},
 * in addition to sending the {@link //DataPreProcessEvent}.
 * This class may not hold references for objects which it is not responsible for.
 *
 * You can add private fields and public methods to this class.
 * You MAY change constructor signatures and even add new public constructors.
 */
public class GPUService extends MicroService {

    private GPU gpu;
    private TrainModelEvent currentEvent;
    private Queue<TrainModelEvent> eventQ;
    private MessageBusImpl messegebusIns;

    public GPUService(int gpuId, GPU.Type type, int BATCHSIZE, Cluster cluster) {
        super("GPU " + gpuId);
        gpu = new GPU( gpuId,type, BATCHSIZE, cluster);
        currentEvent = null;
        eventQ = new LinkedBlockingQueue<>();
        messegebusIns = MessageBusImpl.getInstance();
    }
    /**
     *   the initialization of the service, register it to the Messegebus, and creates the right callbacks for the service
     *  @PRE: this.messegebusIns.isRegister(this)==false
     *  @POST: this.messegebusIns.isRegister(this)==true
     **/
    @Override
    protected void initialize() {
        this.messegebusIns.register(this);

        Callback<TrainModelEvent>trainEv=e-> { ////trainmodel event callback
            this.eventQ.add(e);
        };
        Callback<TestModelEvent> testEV= e->{ /////test model event callback
            Model.Result r=this.gpu.testModel(e.getModel());
            e.getModel().setResult(r); //updating the result after the testing
            e.getModel().setStatus(Tested);
            this.complete(e,e.getModel()); //Updating the future with the result
        };
        Callback<TickBroadcast> tickB = e -> {
            this.gpu.processNextTick();
            if(this.gpu.modelDone() & currentEvent != null){
                this.complete(currentEvent, currentEvent.getModel()); //updating the future in the messege bus that the training had finished
                currentEvent = null;
            }
            if(!this.gpu.isActive() & !this.eventQ.isEmpty()) {
                currentEvent = this.eventQ.poll();
                this.gpu.setModel(currentEvent.getModel());
            }
        };
        this.subscribeEvent(TrainModelEvent.class,trainEv); ///////Trainmodel Event
        this.subscribeEvent(TestModelEvent.class,testEV); //////Test Model
        this.subscribeBroadcast(TickBroadcast.class,tickB);
    }
}
