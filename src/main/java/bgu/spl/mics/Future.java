package bgu.spl.mics;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;

/**
 * A Future object represents a promised result - an object that will
 * eventually be resolved to hold a result of some operation. The class allows
 * Retrieving the result once it is available.
 * 
 * Only private methods may be added to this class.
 * No public constructor is allowed except for the empty constructor.
 */
public class Future<T> {

	T result;
	boolean completed;
	
	/**
	 * This should be the the only public constructor in this class.
	 */
	public Future() {
		this.result = null;
		this.completed = false;
	}
	
	/**
     * retrieves the result the Future object holds if it has been resolved.
     * This is a blocking method! It waits for the computation in case it has
     * not been completed.
     * <p>
     * @return return the result of type T if it is available, if not wait until it is available.
     * @PRE: none
	 * @POST: trivial
     */
	public synchronized T get() {
		while (!this.completed) {
			try{
				this.wait();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		return this.result;
	}
	
	/**
     * Resolves the result of this Future object.
	 * @PRE: none
	 * @POST: get() == result
     */
	public synchronized void resolve (T result) {
		if(!this.completed) {
			this.result = result;
			this.completed = true;
			this.notifyAll();
		}
	}
	
	/**
     * @return true if this object has been resolved, false otherwise
	 * @PRE: none
	 * @POST: trivial
     */
	public synchronized boolean isDone() {
		return this.completed;
	}
	
	/**
     * retrieves the result the Future object holds if it has been resolved,
     * This method is non-blocking, it has a limited amount of time determined
     * by {@code timeout}
     * <p>
     * @param timeout 	the maximal amount of time units to wait for the result.
     * @param unit		the {@link TimeUnit} time units to wait.
     * @return return the result of type T if it is available, if not, 
     * 	       wait for {@code timeout} TimeUnits {@code unit}. If time has
     *         elapsed, return null.
	 * @PRE: none
	 * @POST: trivial
     */
	public synchronized T get(long timeout, TimeUnit unit) {
		if(!this.completed) {
			try{
				this.wait(unit.toMillis(timeout));
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		if(this.completed)
			return this.result;
		return null;
	}

}
