package bgu.spl.mics.application.objects;

import bgu.spl.mics.application.services.CPUService;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Passive object representing a single CPU.
 * Add all the fields described in the assignment as private fields.
 * Add fields and methods to this class as you see fit (including public methods and constructors).
 */
public class CPU {

    private int cores;
    private int id;
    private final int ticks_to_process;
    private int ticks_processed;
    private Queue<DataBatch> data_collection;

    public CPU(int id, int cores){
        this.id = id;
        this.cores = cores;
        this.ticks_to_process = 32/(cores);
        this.data_collection = new ArrayBlockingQueue<DataBatch>(10);
        this.ticks_processed = 1;
    }

    //  @INV: 0 <= getTicksProcessed <= 4

    //  @PRE: none
    //  @POST: trivial
    public void addDataBatch(DataBatch dataBatch){
        this.data_collection.add(dataBatch);
    }

    //  @PRE: none
    //  @POST: trivial
    public Collection<DataBatch> getDataBatches(){ return data_collection; }

    //  @PRE: none
    //  @POST: trivial
    public int getId(){ return this.id; }

    //  @PRE: none
    //  @POST: trivial
    public int getTicksProcessed(){ return this.ticks_processed; }

    //  @PRE: none
    //  @POST: trivial
    private DataBatch getTopDataBatch(){ return this.data_collection.peek(); }

    //  @PRE: none
    //  @POST: trivial
    private int getDataBatchSize(){ return this.data_collection.size(); }

    //  @PRE: none
    //  @POST: this.ticks_processed = (this.ticks_processed+1)%(this.getTopData().getData().getTicksToProcess());
    private void incTick(){
        this.ticks_processed = (this.ticks_processed+1)%(this.getTopDataBatch().getData().getTicksToProcess());
    }
    //  @PRE: getDataBatchSize > 0 && getTicksProcessed == 0
    //  @POST: Vi < getDataBatchSize(): @POST(data_collection[i]) = @PRE(
    private void removeProcessedData(){ this.data_collection.poll(); }

    //  @PRE: 0 <= getTicksProcessed <= 4 &
    //  @POST:
    public DataBatch processNextTick(){
        this.incTick();
        if(this.getTicksProcessed() == 0){
            DataBatch db = this.getTopDataBatch();
            // update data processed size
            removeProcessedData();
            return db;
        }
        return null;
    }
}
