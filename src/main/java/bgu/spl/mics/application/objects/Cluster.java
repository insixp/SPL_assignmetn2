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
	public static Cluster getInstance() {
		//TODO: Implement this
		return null;
	}

}
