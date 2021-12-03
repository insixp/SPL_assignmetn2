package bgu.spl.mics.application.objects;

/**
 * Passive object representing a data used by a model.
 * Add fields and methods to this class as you see fit (including public methods and constructors).
 */

public class DataBatch {

    private Data data;
    private int start_index;
    private int gpu_id;

    public DataBatch(Data data, int start_index, int gpu_id){
        this.data = data;
        this.start_index = start_index;
        this.gpu_id = gpu_id;
    }

    public int getGpuId(){ return this.gpu_id; }
    public int getStartIndex(){
        return this.start_index;
    }
    public Data getData(){ return this.data; }

}
