package bgu.spl.mics.application.services;

import bgu.spl.mics.Callback;
import bgu.spl.mics.MessageBusImpl;
import bgu.spl.mics.MicroService;
import bgu.spl.mics.application.messages.PublishConferenceBroadcast;
import bgu.spl.mics.application.messages.TerminateBroadcast;
import bgu.spl.mics.application.messages.TickBroadcast;
import bgu.spl.mics.application.objects.Cluster;
import bgu.spl.mics.application.objects.CPU;
/**
 * CPU service is responsible for handling the {@link //DataPreProcessEvent}.
 * This class may not hold references for objects which it is not responsible for.
 *
 * You can add private fields and public methods to this class.
 * You MAY change constructor signatures and even add new public constructors.
 */
public class CPUService extends MicroService {

    CPU cpu;
    MessageBusImpl msgBus;

    public CPUService(int id, int cores, int batchSize, Cluster cluster) {
        super("CPU " + id);
        cpu = new CPU(id, cores, batchSize, cluster);
        msgBus = MessageBusImpl.getInstance();
    }

    public CPUService(CPU cpu) {
        super("CPU " + cpu.getId());
        this.cpu = cpu;
        msgBus = MessageBusImpl.getInstance();
    }

    @Override
    protected void initialize() {
        msgBus.register(this);
        Callback<TerminateBroadcast> terminateBroadcast = (TerminateBroadcast e) -> { this.terminate(); };
        this.subscribeBroadcast(TerminateBroadcast.class, terminateBroadcast);
    }

}
