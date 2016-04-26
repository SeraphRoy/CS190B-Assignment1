/*
 * The MIT License
 *
 * Copyright 2016 peter.
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package api;

import java.awt.BorderLayout;
import java.awt.Container;
import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JScrollPane;
import system.ComputerImpl;
import system.SpaceImpl;

/**
 * The class used to "run" the Job - primarily send the tasks to the Space
 * and retrieve the corresponding results from the Space.
 * @author Peter Cappello
 * @param <T> type of value returned by value.
 */
public class JobRunner<T> extends JFrame
{
    final private Job<T> job;
    final private Space  space;
    final private long   startTime = System.nanoTime();

    /**
     *
     * @param job the Job to be run.
     * @param title the String to be displaced on the JPanel containing the JLabel.
     * @param domainName of the Space to be used.
     * @throws RemoteException occurs if there is a communication problem or
     * the remote service is not responding
     * @throws NotBoundException There is no Space service bound in the RMI registry.
     * @throws MalformedURLException the URL provided for the Space RMI registry is malformed.
     */
    public JobRunner( Job<T> job, String title, String domainName )
        throws RemoteException, NotBoundException, MalformedURLException
    {
        System.setSecurityManager( new SecurityManager() );
        setTitle( title );
        setDefaultCloseOperation( JFrame.EXIT_ON_CLOSE );
        this.job = job;
        if ( domainName.isEmpty() )
            {
                space = new SpaceImpl();
                for ( int i = 0; i < Runtime.getRuntime().availableProcessors(); i++ )
                    {
                        space.register( new ComputerImpl() );
                    }
            }
        else
            {
                final String url = "rmi://" + domainName + ":" + Space.PORT + "/" + Space.SERVICE_NAME;
                space = (Space) Naming.lookup( url );

            }
    }

    /**
     * Run the Job: Generate the tasks, retrieve the results, compose a solution
     * to the original problem, and display the solution.
     * @throws RemoteException occurs if there is a communication problem or
     * the remote service is not responding
     */
    public void run() throws RemoteException
    {
        space.putAll( job.decompose() );
        job.collectResults(space);
        view( job.viewResult(job.value() ) );
        Logger.getLogger( this.getClass().getCanonicalName() )
            .log( Level.INFO, "Job run time: {0} ms.", ( System.nanoTime() - startTime) / 1000000 );

    }

    private void view( final JLabel jLabel )
    {
        final Container container = getContentPane();
        container.setLayout( new BorderLayout() );
        container.add( new JScrollPane( jLabel ), BorderLayout.CENTER );
        pack();
        setVisible( true );

    }

}
