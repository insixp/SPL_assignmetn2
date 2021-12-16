package bgu.spl.mics.application.services;

import bgu.spl.mics.*;
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
    private TrainModelEvent lastEvent;
    private Queue<TrainModelEvent>TrainModelQ;
    public GPUService(String name,int gpuId, GPU.Type type, Cluster cluster) {
        super("Change_This_Name");
        // TODO Implement this
        gpu=new GPU( gpuId,type,cluster);
        lastEvent=null;
        TrainModelQ= new Queue<TrainModelEvent>() {
            @Override
            public boolean add(TrainModelEvent trainModelEvent) {
                return false;
            }

            @Override
            public boolean offer(TrainModelEvent trainModelEvent) {
                return false;
            }

            @Override
            public TrainModelEvent remove() {
                return null;
            }

            @Override
            public TrainModelEvent poll() {
                return null;
            }

            @Override
            public TrainModelEvent element() {
                return null;
            }

            @Override
            public TrainModelEvent peek() {
                return null;
            }

            @Override
            public int size() {
                return 0;
            }

            @Override
            public boolean isEmpty() {
                return false;
            }

            @Override
            public boolean contains(Object o) {
                return false;
            }

            @Override
            public Iterator<TrainModelEvent> iterator() {
                return null;
            }

            @Override
            public Object[] toArray() {
                return new Object[0];
            }

            @Override
            public <T> T[] toArray(T[] a) {
                return null;
            }

            @Override
            public boolean remove(Object o) {
                return false;
            }

            @Override
            public boolean containsAll(Collection<?> c) {
                return false;
            }

            @Override
            public boolean addAll(Collection<? extends TrainModelEvent> c) {
                return false;
            }

            @Override
            public boolean removeAll(Collection<?> c) {
                return false;
            }

            @Override
            public boolean retainAll(Collection<?> c) {
                return false;
            }

            @Override
            public void clear() {

            }
        };
    }

    @Override
    protected void initialize() {
        // TODO Implement this
        MessageBusImpl messegebusIns=MessageBusImpl.getInstance();
        messegebusIns.register(this);

        Callback<TrainModelEvent>trainEv=e-> {////trainmodel event callback
            this.TrainModelQ.add(e);
            //this.lastEvent=e;

        };
        Callback<TestModelEvent> testEV= e->{/////test model event callback
            Model.Result r=this.gpu.testModel(e.getModel());
            e.getModel().setResult(r);//updating the result after the testing
            e.getModel().setStatus(Tested);
            this.complete(e,e.getModel());//Updating the future with the result
        };
        Callback<TickBroadcast> tickB= e-> {
            this.gpu.processNextTick();
            if(!this.gpu.active&!this.TrainModelQ.isEmpty()) {
                lastEvent=this.TrainModelQ.poll();
                this.gpu.setModel(lastEvent.getModel());
            }
            if(this.gpu.updateFuture){
                this.complete(lastEvent,lastEvent.getModel());//updating the future in the messege bus that the training had finished
                this.gpu.updateFuture=false;
            }
        };
        this.subscribeEvent(TrainModelEvent.class,trainEv);///////Trainmodel Event
        this.subscribeEvent(TestModelEvent.class,testEV);//////Test Model
        this.subscribeBroadcast(TickBroadcast.class,tickB);

    }
}