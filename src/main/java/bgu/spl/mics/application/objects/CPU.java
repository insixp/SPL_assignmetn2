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
    private Cluster cluster;
    private final int ticks_to_process;
    private int ticks_processed;
    private boolean active;
    private Queue<DataBatch> data_collection;

    public CPU(int id, int cores, Cluster cluster){
        this.id = id;
        this.cores = cores;
        this.cluster = cluster;
        this.cluster.registerCPU(this.id);
        this.ticks_to_process = 32/(cores);
        this.data_collection = new ArrayBlockingQueue<DataBatch>(10);
        this.ticks_processed = 1;
        this.active = false;
    }

    /**
     *  @Invariant: 0 <= this.getCores() <= 32
     *  @Invariant: 0 <= this.getTicksProcessed() <= 32
     *  @Invariant: data_collection.size()>=0;
     **/


    /**
     *  Read from cluster the next Data batch and add it to the local collection.
     *  @param: none
     *  @pre: this.cluster != null
     *  @pre: this.cluster.messagesToCPU(this.id) > 0
     *  @post:this.data_collection.size() == @pre data_collection.size()+1
     *  @post:Vi<data_collection.size(): data_collection.contains(@pre:data_collection[i])
     *  @post:data_collection.contains(this.cluster.readByCpu())
     *  @post: this.cluster.messagesToCPU(this.id) = @PRE: this.cluster.messagesToCPU()-1
     * */
    public void addDataBatch(){}

    /**
     * Return the number of cores.
     * @param: none
     * @pre: none
     * @post: this.cores = @pre:this.cores
     */
    public int getCores(){ return this.cores; }

    /**
     * Return if the cpu is active
     * @param: none
     * @pre: none
     * @post: this.active = @pre:this.active
     */
    public boolean getActive(){ return this.active; }

    /**
     * Return all data batches in the local collection
     *@param:data_collection!=null
     *@pre: none
     *@post:this.data_collection.size()==@pre data_collection.size()
     *@post: Vi<data_collection.size()
            data_collection[i].getData()==@pre data_collection[i].getData()&&
            data_collection[i].getStart_index()==@pre data_collection[i].getStart_index()&&
            data_collection[i].getGpu_id()==@pre data_collection[i].getGpu_id();
     */
    public Collection<DataBatch> getDataBatches(){ return data_collection; }

    /**
     *  Return the cpu Id
     *@param:this.id!=null
     *@pre: none
     *@post:this.id==@pre this.id;
     */
    public int getId(){ return this.id; }

    /**
     *  Return the amount of ticks processed on this Data Batch
     *@param:this.ticks_processed!=null
     *@pre: none
     *@post:this.ticks_processed==@pre this.ticks_processed;
     */
    public int getTicksProcessed(){ return this.ticks_processed; }

    /**
     * Return the next data batch to be processed.
     *@param:data_collection!=null
     *@pre: none
     *@post:this.data_collection.size()==@pre data_collection.size()
     *@post: Vi<data_collection.size()
            data_collection[i].getData()==@pre data_collection[i].getData()&&
            data_collection[i].getStart_index()==@pre data_collection[i].getStart_index()&&
            data_collection[i].getGpu_id()==@pre data_collection[i].getGpu_id();
     */
    private DataBatch getTopDataBatch(){ return this.data_collection.peek(); }

    /**
     *  Return the number of all data Batch in the local collection
     *@param:data_collection.size()!=null
     *@pre: none
     *@post:this.data_collection.size()==@pre data_collection.size()
     */
    private int getDataBatchSize(){ return this.data_collection.size(); }

    /**
     *  Increase the internal tick counter
     *@param: this.ticks_processed!=null
     *@pre: none
     *@post:this.ticks_processed==@pre (this.ticks_processed+1)%(this.getTopDataBatch().getData().getTicksToProcess())
     */
    private void incTick(){
        this.ticks_processed = (this.ticks_processed+1)%(this.getTopDataBatch().getData().getTicksToProcess());
    }

    /**
     *  Unregister the CPU from the cluster
     * @PRE: this.cluster.isCPURegistered(this.id) == true
     * @POST: this.cluster.isCPURegistered(this.id) == false
     */
    public void clusterUnregister(){ this.cluster.unregisterCPU(this.id); }

    /**
     *  Remove processed data and push it to the Cluster.
     *@pre: data_collection.size()>0
     *@post:getTopDataBatch()==(@pre data_collection.poll()).getTopDataBatch();
     */
    private void removeProcessedData(){}

    /**
     *  Process Next tick and push data to cluster if we finished working on it and if the Cluster
     *  have enought space for it.
     *@pre: data_collection.size()>=0
     *@post:data_collection.size()<=@pre data_collection.size()
     */
    public void processNextTick(){
//        this.incTick();
//        if(this.getTicksProcessed() == 0){
//            DataBatch db = this.getTopDataBatch();
//            // update data processed size
//            removeProcessedData();
//            return db;
//        }
//        return null;
    }
}
