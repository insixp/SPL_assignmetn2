package bgu.spl.mics.application.objects;

import java.util.ArrayList;
import java.util.Collection;

public class Statistics {

    Collection<String>  modelNameCollection;
    long                totalDataBatches;
    long                totalCpuTimeUnits;
    long                totalGpuTimeUnits;

    public Statistics(){
        modelNameCollection = new ArrayList<String>();
        this.totalDataBatches = 0;
        this.totalCpuTimeUnits = 0;
        this.totalGpuTimeUnits = 0;
    }

    public void increaseDataBatches(long inc_size){
        //  Implement this
    }

    public void increaseCpuTimeunits(long inc_size){
        //  Implement this
    }

    public void increaseGpuTimeunits(long inc_size){
        //  Implement this
    }
}
