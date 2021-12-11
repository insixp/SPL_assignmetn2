package bgu.spl.mics.application.objects;

import java.util.ArrayList;
import java.util.Collection;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

public class Statistics {

    private class ClusterObjectStatistic{
        public AtomicLong msgSent;
        public AtomicLong msgRecieved;

        public ClusterObjectStatistic(){
            this.msgSent = new AtomicLong(0);
            this.msgRecieved = new AtomicLong(0);
        }
    }
    Collection<String>  modelNameCollection;
    ConcurrentHashMap<Integer, ClusterObjectStatistic> cpuIdToCpuStats;
    ConcurrentHashMap<Integer, ClusterObjectStatistic> gpuIdToGpuStats;
    AtomicLong      totalDataBatches;
    AtomicLong      totalCpuTimeUnits;
    AtomicLong      totalGpuTimeUnits;

    public Statistics(){
        modelNameCollection = new ArrayList<String>();
        cpuIdToCpuStats = new ConcurrentHashMap<>();
        gpuIdToGpuStats = new ConcurrentHashMap<>();
        this.totalDataBatches  = new AtomicLong(0);
        this.totalCpuTimeUnits = new AtomicLong(0);
        this.totalGpuTimeUnits = new AtomicLong(0);
    }

    public void addCpu(int cpuId){
        this.cpuIdToCpuStats.put(cpuId, new ClusterObjectStatistic());
    }

    public void addGpu(int gpuId){
        this.cpuIdToCpuStats.put(gpuId, new ClusterObjectStatistic());
    }

    public void incRecievedCpu(int cpuId){
        ClusterObjectStatistic stats = this.cpuIdToCpuStats.get(cpuId);
        stats.msgRecieved.getAndIncrement();
    }

    public long getRecievedCpu(int cpuId){
        return this.cpuIdToCpuStats.get(cpuId).msgRecieved.get();
    }

    public void incSentCpu(int cpuId){
        ClusterObjectStatistic stats = this.cpuIdToCpuStats.get(cpuId);
        stats.msgSent.getAndIncrement();
    }

    public long getSentCpu(int cpuId){
        return this.cpuIdToCpuStats.get(cpuId).msgSent.get();
    }

    public void incRecievedGpu(int gpuId){
        ClusterObjectStatistic stats = this.cpuIdToCpuStats.get(gpuId);
        stats.msgRecieved.getAndIncrement();
    }

    public long getRecievedGpu(int cpuId){
        return this.gpuIdToGpuStats.get(cpuId).msgRecieved.get();
    }

    public void incSentGpu(int gpuId){
        ClusterObjectStatistic stats = this.cpuIdToCpuStats.get(gpuId);
        stats.msgSent.getAndIncrement();
    }

    public long getSentGpu(int cpuId){
        return this.gpuIdToGpuStats.get(cpuId).msgSent.get();
    }

    public long getTotalDataBatches(){
        return this.totalDataBatches.get();
    }

    public long getTotalCpuTime(){
        return this.totalCpuTimeUnits.get();
    }

    public long getTotalGpuTime(){
        return this.totalGpuTimeUnits.get();
    }

    public void increaseDataBatches(){
        this.totalDataBatches.getAndIncrement();
    }

    public void increaseCpuTimeunits(){
        this.totalCpuTimeUnits.getAndIncrement();
    }

    public void increaseGpuTimeunits(){
        this.totalGpuTimeUnits.getAndIncrement();
    }
}
