package bgu.spl.mics.application.objects;

import bgu.spl.mics.application.services.CPUService;

import java.util.ArrayList;
import java.util.Collection;

/**
 * Passive object representing a single CPU.
 * Add all the fields described in the assignment as private fields.
 * Add fields and methods to this class as you see fit (including public methods and constructors).
 */
public class CPU {

    private int cores;
    private int id;
    private Collection<DataBatch> data_collection;

    public CPU(int id, int cores){
        this.id = id;
        this.cores = cores;
        this.data_collection = new ArrayList<DataBatch>();
    }

    public void addDataBatch(DataBatch dataBatch){
        this.data_collection.add(dataBatch);
    }

    public Collection<DataBatch> getDataCollection(){
        return data_collection;
    }

    public DataBatch processNextBatch(){
        //  Get next batch from Cluster and process it
        return null;
    }
}
