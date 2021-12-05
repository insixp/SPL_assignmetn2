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
    private boolean active;
    private Queue<DataBatch> data_collection;

    public CPU(int id, int cores){
        this.id = id;
        this.cores = cores;
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
     *  @param:dataBatch!=null
     *  @pre: none
     *  @post:this.data_collection.size() == @pre data_collection.size()+1
     *  @post:dataBatch.getData() == @pre dataBatch.getData();
     *  @post:dataBatch.getStart_index() == @pre dataBatch.getStart_index();
     *  @post:dataBatch.getGpu_id() == @pre dataBatch.getGpu_id();
     * */
    public void addDataBatch(DataBatch dataBatch){
        this.data_collection.add(dataBatch);
    }

    /**
     * @param: none
     * @pre: none
     * @post: this.cores = @pre:this.cores
     */
    public int getCores(){ return this.cores; }

    /**
     * @param: none
     * @pre: none
     * @post: this.active = @pre:this.active
     */
    public boolean getActive(){ return this.active; }

    /**
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
     *@param:this.id!=null
     *@pre: none
     *@post:this.id==@pre this.id;
     */
    public int getId(){ return this.id; }

    /**
     *@param:this.ticks_processed!=null
     *@pre: none
     *@post:this.ticks_processed==@pre this.ticks_processed;
     */
    public int getTicksProcessed(){ return this.ticks_processed; }

    /**
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
     *@param:data_collection.size()!=null
     *@pre: none
     *@post:this.data_collection.size()==@pre data_collection.size()
     */
    private int getDataBatchSize(){ return this.data_collection.size(); }

    /**
     *@param: this.ticks_processed!=null
     *@pre: none
     *@post:this.ticks_processed==@pre (this.ticks_processed+1)%(this.getTopDataBatch().getData().getTicksToProcess())
     */
    private void incTick(){
        this.ticks_processed = (this.ticks_processed+1)%(this.getTopDataBatch().getData().getTicksToProcess());
    }

    /**
     *@pre: data_collection.size()>0
     *@post:getTopDataBatch()==(@pre data_collection.poll()).getTopDataBatch();
     */
    private void removeProcessedData(){ this.data_collection.poll(); }

    /**
     *@pre: data_collection.size()>=0
     *@post:data_collection.size()<=@pre data_collection.size()
     */
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
