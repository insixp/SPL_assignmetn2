package bgu.spl.mics.application.objects;

import java.util.Queue;
import java.util.Random;
import java.util.concurrent.ArrayBlockingQueue;

import static bgu.spl.mics.application.objects.Model.Status.Trained;
import static bgu.spl.mics.application.objects.Model.Status.Training;

/**
 * Passive object representing a single GPU.
 * Add all the fields described in the assignment as private fields.
 * Add fields and methods to this class as you see fit (including public methods and constructors).
 */
public class GPU {
    /**
     * Enum representing the type of the GPU.
     */
   public enum Type {RTX3090, RTX2080, GTX1080}

    public final int BATCH_SIZE;
    private final Type type;
    private final int vmemSize;
    private Cluster cluster;
    private Model model;
    private final int gpuId;
    private int nextBatch;
    public boolean active;
    private int vmemOccupied;
    private int ticks_processed;
    public boolean updateFuture;
    private Queue<DataBatch> vmem;

    public GPU(int gpuId, Type type, int batchSize, Cluster cluster){

        //  Set values
        this.gpuId = gpuId;
        this.type = type;
        this.BATCH_SIZE = batchSize;
        this.cluster = cluster;

        //  Vmem
        this.vmemSize = this.typeToVmemSize(this.type);
        this.vmem = new ArrayBlockingQueue<>(this.getVmemSize());
        this.vmemOccupied = 0;

        //  Initialize
        this.active = false;
        this.model = null;
        this.ticks_processed = 0;
        this.nextBatch = 0;

        //  Register to cluster
        this.cluster.registerGPU(this.gpuId);
    }

    /**
     * @Invariant: 0 <= this.getTicksProcessed <= 4
     * @Invariant: this.getVmemSize() == 8 || this.getVmemSize() == 16 || this.getVmemSize() == 32
     * @Invariant: 0 <= this.getVmemOccupied <= 32
     * @Invariant: 0 <= this.getVmemFree() <= 32
     */

    public int getGpuId() { return this.gpuId; }

    /**
     *  Returns the size of the virtual memory the GPU has.
     *  @PRE: none
     *  @POST: trivial
     **/
    public int getVmemSize(){ return this.vmemSize; }

    /**
     *  Return the amount of occupied virtual memory the GPU has.
     *  @PRE: none
     *  @POST: trivial
     **/
    public int getVmemOccupied(){ return this.vmemOccupied; }

    /**
     *  Return the amount of free virtual memory the GPU has.
     *  @PRE: none
     *  @POST: trivial
     **/
    public int getVmemFree(){ return this.getVmemSize() - this.getVmemOccupied(); }

    /**
     *  Return the total amount of Batches the current model has.
     *  @PRE: none
     *  @POST: trivial
     **/
    public int getTotalBatch(){ return this.model.getData().getSize()/BATCH_SIZE; }

    /**
     *  Return the amount of batches that were processed (CPU process only)
     *  @PRE: none
     *  @POST: trivial
     **/
    public int getBatchProcessed(){ return this.model.getData().getProcessed(); }

    /**
     *  Return the current Model.
     *  @PRE: none
     *  @POST: trivial
     **/
    public Model getModel(){ return this.model; }

    /**
     *  Increase the Data Batch pointer
     *  @PRE: nextBatch + BATCH_SIZE < model.getData().getSize()
     *  @POST: nextBatch += BATCH_SIZE
     **/
    private void incDataBatch(){
            this.nextBatch += this.BATCH_SIZE;
    }

    /**
     *  Increase virtual memory occupied
     *  @PRE: none
     *  @POST: trivial
     **/
    private void incVmemOccupied(){ this.vmemOccupied++; }

    /**
     *  Decrease virtual memory occupied
     *  @PRE: none
     *  @POST: trivial
     **/
    private void decVmemOccupied(){ this.vmemOccupied--; }

    /**
     *  Return new data batch with the current pointer location
     *  @PRE: none
     *  @POST: trivial
     **/
    private DataBatch getNextDataBatch(){
        return new DataBatch(this.model.getData(), this.nextBatch, this.gpuId);
    }

    /**
     *   Send the next data batch to be processed to the cluster if possible, increase the pointer
     *   to the next batch and update all the necessary statistics.
     *  @PRE: nextBatch + BATCH_SIZE < model.getData().getSize()
     *  @POST: nextBatch = @pre: nextBatch + BATCH_SIZE
     *  @POST: this.cluster.messagesByGPU() = @PRE: this.cluster.messagesByGPU() + 1
     **/
    private DataBatch popNextDataBatch() {
        DataBatch db = this.getNextDataBatch();
        incDataBatch();
        incVmemOccupied();
        return db;
    }

    /**
     *  Unregister the GPU from the cluster
     * @PRE: this.cluster.isGPURegistered(this.gpuId) == true
     * @POST: this.cluster.isGPURegistered(this.gpuId) == false
     */
    public void clusterUnregister(){ this.cluster.unregisterGPU(this.gpuId);}

    /**
     *  Return the amount of ticks needed to train model.
     *  @PRE: none
     *  @POST: trivial
     **/
    private int getTicksToProcess(){
        if(!vmem.isEmpty()) {
            if (this.type == Type.RTX3090)
                return 1;
            if (this.type == Type.RTX2080)
                return 2;
            if (this.type == Type.GTX1080)
                return 4;
        }
        return -1;
    }

    /**
     *  Sets the current model of the GPU.
     *  @PRE: this.active = false
     *  @POST: this.model = model
     **/
    public void setModel(Model model) {
        if(!active) {
            this.model = model;
            this.model.setStatus(Training);
            this.nextBatch = 0;
            this.active = true;
            this.ticks_processed=0;
            this.updateFuture=false;
        }
    }

    public void sendDataToCPU() throws InterruptedException{
        DataBatch db = this.popNextDataBatch();
        if(db == null)
            throw new InterruptedException("Cannot do this operation");
        this.cluster.sendToCpu(db);
    }

    /**
     *  Get the processed data of the CPU from the cluster.
     *  @PRE: this.getVmemOccupied > 0
     *  @PRE: this.cluster.messagesToGPU() > 0
     *  @POST: Vmem.contains(db_in)
     *  @POST: this.cluster.messagesToGPU() = @PRE:this.cluster.messagesToGPU()-1
     **/
    public void getProcessedData(){
        this.vmem.add(this.cluster.readByGpu(this.gpuId));
    }

    /**
     *  Get amount of tick processed ON THE CURRENT DATA BATCH.
     *  @PRE: none
     *  @POST: trivial
     **/
    public int getTicksProcessed(){ return this.ticks_processed; }

    /**
     *  Increase tick
     *  @PRE: none
     *  @POST: (ticks_processed+1)%(getTicksToProcess(this.type))
     **/
    private void incTick(){
        this.ticks_processed = (this.ticks_processed+1)%(getTicksToProcess());
    }

    /**
     *  Process the next Tick, train the model 1 tick and check to see if finished. if so,
     *  get another batch from the vmem and start working on it.
     *  @PRE: none
     *  @POST: (ticks_processed+1)%(getTicksToProcess(this.type))
     *  @POST: this.getVmemOccupied() <= @pre:this.getVmemOcuupied()
     *  @POST: this.getVmemFree() >= @pre:this.getVmemFree()
     **/
    public void processNextTick() {
        this.incTick();
        if (this.getTicksProcessed() == 0) {    // if finishes to process the last data batch
            if(!vmem.isEmpty()) {
                vmem.poll();
                decVmemOccupied();
                this.getModel().getData().incProcessed(this.BATCH_SIZE);    // ask chupa
                if(this.getModel().getData().getProcessed() == this.getModel().getData().getSize()){
                    this.model.setStatus(Trained);
                    updateFuture = true;
                }
            }
        }
        while (canSendToCPU()) {   // sending databatch to cpu if possible
            if (this.nextBatch + this.BATCH_SIZE < this.model.getData().getSize()) {
                cluster.sendToCpu(popNextDataBatch());
            }
        }
        while(!this.cluster.GpuQIsEmpty(this.gpuId))
            this.vmem.add(this.cluster.readByGpu(this.gpuId));
    }

    /**
     *  Testing a model.
     *  @PRE: none
     *  @POST: trivial
     **/
    public Model.Result testModel(Model model_in){
        Random rnd = new Random();
        int rand = rnd.nextInt(100);
        if(model_in.getStudent().getStatus() == Student.Degree.MSc)
            return rand<10? Model.Result.Good: Model.Result.Bad;
        if(model_in.getStudent().getStatus() == Student.Degree.PhD)
            return rand<20? Model.Result.Good: Model.Result.Bad;
        return Model.Result.None;
    }

    public static Type stringToType(String type){
        if(type == "RTX3090")
            return Type.RTX3090;
        if(type == "RTX2080")
            return Type.RTX2080;
        if(type == "GTX1080")
            return Type.GTX1080;
        return null;
    }

    private static int typeToVmemSize(Type type){
        if(type == Type.RTX3090)
            return 32;
        else if(type == Type.RTX2080)
            return 16;
        else if(type == Type.GTX1080)
            return 8;
        else
            return 0;
    }

    public boolean canSendToCPU(){
        if(getVmemFree()>0 && nextBatch<model.getData().getSize())
            return true;
        return false;
    }
}
