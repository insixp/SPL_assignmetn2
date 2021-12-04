package bgu.spl.mics.application.objects;

import java.util.Random;

/**
 * Passive object representing a single GPU.
 * Add all the fields described in the assignment as private fields.
 * Add fields and methods to this class as you see fit (including public methods and constructors).
 */
public class GPU {
    /**
     * Enum representing the type of the GPU.
     */
    enum Type {RTX3090, RTX2080, GTX1080}

    private final int BATCH_SIZE = 1000;
    private final Type type;
    private Model model;
    private final int gpuId;
    private int nextBatch;
    private boolean active;
    private final int vmemSize;
    private int vmemOccupied;
    private int ticks_processed;
    private int batchesSent;

    public GPU(int gpuId, Type type){
        this.gpuId = gpuId;
        this.type = type;
        this.nextBatch = 0;
        this.active = false;
        this.model = null;
        this.vmemOccupied = 0;
        this.ticks_processed = 0;
        this.batchesSent = 0;
        if(type == Type.RTX3090)
            this.vmemSize = 32;
        else if(type == Type.RTX2080)
            this.vmemSize = 16;
        else if(type == Type.GTX1080)
            this.vmemSize = 8;
        else
            this.vmemSize = 0;
    }

    //  @INV: 0 <= getTicksProcessed() <= 4

    //  @PRE: none
    //  @POST: trivial
    public int getVmemSize(){ return this.vmemSize; }

    //  @PRE: none
    //  @POST: trivial
    public int getVmemOccupied(){ return this.vmemOccupied; }

    //  @PRE: none
    //  @POST: trivial
    public int getVmemFree(){ return this.getVmemSize() - this.getVmemOccupied(); }

    //  @PRE: none
    //  @POST: trivial
    public int getBatchAmount(){ return this.model.getData().getSize()/BATCH_SIZE; }

    //  @PRE: none
    //  @POST: trivial
    public int getBatchSent(){ return this.batchesSent; }

    //  @PRE: none
    //  @POST: trivial
    public int getBatchProcessed(){ return this.model.getData().getProcessed(); }

    //  @PRE: nextBatch + BATCH_SIZE <= model.getData().getSize()
    //  @POST: nextBatch += BATCH_SIZE
    private void incDataBatch(){
        if(this.nextBatch + this.BATCH_SIZE <= this.model.getData().getSize())
            this.nextBatch += this.BATCH_SIZE;
    }

    //  @PRE: none
    //  @POST: trivial
    private DataBatch getNextDataBatch(){
        return new DataBatch(this.model.getData(), this.nextBatch, this.gpuId);
    }

    //  @PRE: none
    //  @POST: trivial
    private int getTicksToProcess(){
        if(this.type == Type.RTX3090)
            return 1;
        if(this.type == Type.RTX2080)
            return 2;
        if(this.type == Type.GTX1080)
            return 4;
        return -1;
    }

    //  @PRE: @PRE(incDataBatch)
    //  @POST: @POST(incDataBatch)
    public DataBatch popNextDataBatch(){
        DataBatch db = this.getNextDataBatch();
        this.incDataBatch();
        return db;
    }

    //  @PRE: this.model = null
    //  @POST: this.model = model
    public void setModel(Model model) {
        if(!active) {
            this.model = model;
            this.nextBatch = 0;
            this.active = true;
        }
    }

    //  @PRE:
    //  @POST:
    public void setProcessedData(DataBatch db_in){}

    //  @PRE: none
    //  @POST: trivial
    public int getTicksProcessed(){ return this.ticks_processed; }

    //  @PRE: none
    //  @POST: (ticks_processed+1)%(getTicksToProcess(this.type))
    private void incTick(){
        this.ticks_processed = (this.ticks_processed+1)%(getTicksToProcess());
    }

    //  @PRE:
    //  @POST:
    public void processNextTick(){}

    //  @PRE:
    //  @POST:
    public Model.Result testModel(Model model_in){
        Random rnd = new Random();
        int rand = rnd.nextInt(100);
        if(model_in.getStudent().getStatus() == Student.Degree.MSc)
            return rand<10? Model.Result.Good: Model.Result.Bad;
        if(model_in.getStudent().getStatus() == Student.Degree.PhD)
            return rand<20? Model.Result.Good: Model.Result.Bad;
        return Model.Result.None;
    }
}
