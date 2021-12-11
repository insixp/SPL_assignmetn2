package bgu.spl.mics.application.objects;


import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Passive object representing the cluster.
 * <p>
 * This class must be implemented safely as a thread-safe singleton.
 * Add all the fields described in the assignment as private fields.
 * Add fields and methods to this class as you see fit (including public methods and constructors).
 */
public class Cluster {

	List<Integer> 	gpuCollection;
	Queue<Integer> 	cpuCollection;
	ConcurrentHashMap<Integer, ConcurrentLinkedQueue<DataBatch>> toCPUQs;
	ConcurrentHashMap<Integer, ConcurrentLinkedQueue<DataBatch>> toGPUQs;
	Statistics		statistics;


	public Cluster(){
		this.gpuCollection = new LinkedList<>();
		this.cpuCollection = new LinkedList<>();
		this.toCPUQs = new ConcurrentHashMap<>();
		this.toGPUQs = new ConcurrentHashMap<>();
		this.statistics = new Statistics();
	}

	/**
     * Retrieves the single instance of this class.
     */
	public static class SingletonHolder{
		private static Cluster instance = new Cluster();
	}

	public static Cluster getInstance() {
		//TODO: Implement this
		return SingletonHolder.instance;
	}

	public void sendToCpu(DataBatch db){
		Integer cpuId;
		ConcurrentLinkedQueue<DataBatch> dbQ;
		synchronized (this.cpuCollection) {
			cpuId = this.cpuCollection.poll();
			this.cpuCollection.add(cpuId);
			dbQ = this.toCPUQs.get(cpuId);
		}
		dbQ.add(db);
	}
	public long messagesByCPU(int cpuId) { return this.statistics.getSentCpu(cpuId); }
	public long messagesToCPU(int cpuId) { return this.statistics.getRecievedCpu((cpuId)); }
	public DataBatch readByCpu(int cpuId){
		ConcurrentLinkedQueue<DataBatch> CpuQ = this.toCPUQs.get(cpuId);
		if(CpuQ != null)
			return CpuQ.poll();
		return null;
	}
	public boolean sendToGpu(DataBatch db){
		//	Put inside GPU
		return false;
	}
	public long messagesByGPU(int gpuId) { return this.statistics.getSentGpu(gpuId); }
	public long messagesToGPU(int gpuId) { return this.statistics.getRecievedGpu(gpuId); }
	public DataBatch readByGpu(int gpuId){
		ConcurrentLinkedQueue<DataBatch> GpuQ = this.toGPUQs.get(gpuId);
		if(GpuQ != null)
			return GpuQ.poll();
		return null;
	}
	public void registerCPU(int cpuId){
		ConcurrentLinkedQueue<DataBatch> CpuQ = new ConcurrentLinkedQueue<DataBatch>();

		this.toCPUQs.put(new Integer(cpuId), CpuQ);
		this.cpuCollection.add(new Integer(cpuId));
	}
	public void unregisterCPU(int cpuId){
		synchronized (cpuCollection) {
			this.cpuCollection.remove(new Integer(cpuId));
		}
	}
	public void registerGPU(int gpuId){
		ConcurrentLinkedQueue<DataBatch> GpuQ = new ConcurrentLinkedQueue<DataBatch>();
		this.toCPUQs.put(new Integer(gpuId), GpuQ);
		this.cpuCollection.add(new Integer(gpuId));
	}
	public void unregisterGPU(int gpuId){
		synchronized (gpuCollection) {
			this.cpuCollection.remove(new Integer(gpuId));
		}
	}
	public boolean isCPURegistered(int cpuId){ return this.cpuCollection.contains(new Integer(cpuId)); }
	public boolean isGPURegistered(int gpuId){ return this.cpuCollection.contains(new Integer(gpuId)); }
}
