package api;

import java.rmi.RemoteException;

public interface Job<T>{
    /**
     * Decompose a problem into a List of Tasks.
     * @return the List of generated Tasks
     * @throws RemoteException occurs if there is a communication problem or
     * the remote service is not responding.
     */
    List<Task> decompose() throws RemoteException;

    /**
     * Take a result for each generated task, and composes these results into
     * a solution to the original problem.
     * This result is stored, so that its value can be obtained via the Job
     * value method.
     * (according to each implementation of this interface).
     * @param space the Space from which the results are taken.
     * @throws RemoteException occurs if there is a communication problem or
     * the remote service is not responding.
     */
    void compose( Space space ) throws RemoteException;

    /**
     * Returns the solution to the problem represented by the Job.
     * @return the value of the Job.
     */
    T value();

    /**
     * Display the solution as a JLabel.
     * @param returnValue the solution. (See value method above.)
     * @return the JLabel that contains some representation of the solution.
     */
    abstract JLabel viewResult( final T returnValue );
}
