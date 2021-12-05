package bgu.spl.mics.application.objects;


import java.util.Collection;

/**
 * Passive object representing the cluster.
 * <p>
 * This class must be implemented safely as a thread-safe singleton.
 * Add all the fields described in the assignment as private fields.
 * Add fields and methods to this class as you see fit (including public methods and constructors).
 */
public class Cluster {

	Collection<GPU> gpuCollection;
	Collection<CPU> cpuCollection;
	Statistics		statistics;


	public Cluster(Collection<GPU> gpuCollection, Collection<CPU> cpuCollection){
		this.gpuCollection = gpuCollection;
		this.cpuCollection = cpuCollection;
		this.statistics = new Statistics();
	}

	/**
     * Retrieves the single instance of this class.
     */
	public static class SingletonHolder{
		private static Cluster instance = new Cluster(null, null);
	}

	public static Cluster getInstance() {
		//TODO: Implement this
		return SingletonHolder.instance;
	}

	public boolean sendToCpu(DataBatch db){ return false; }
	public int messagesByCPU(int cpuId) { return 0; }
	public int messagesToCPU(int cpuId) { return 0; }
	public DataBatch readByCpu(int cpuId){ return null; }
	public boolean sendToGpu(DataBatch db){ return false; }
	public int messagesByGPU(int gpuId) { return 0; }
	public int messagesToGPU(int gpuId) { return 0; }
	public DataBatch readByGpu(int gpuId){ return null; }
	public void registerCPU(int cpuId){}
	public void unregisterCPU(int cpu){}
	public void registerGPU(int gpuId){}
	public void unregisterGPU(int gpuId){}
	public boolean isCPURegistered(int cpuId){ return true; }
	public boolean isGPURegistered(int gpuId){ return true; }
}
