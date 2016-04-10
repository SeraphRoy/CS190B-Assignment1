/*
 * The MIT License
 *
 * Copyright 2015 peter.
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
package clients;
import api.Computer;
import api.Task;
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


/**
 *
 * @author Peter Cappello
 * @param <T> return type the Task that this Client executes.
 */
public class Client<T> extends JFrame
{
    final protected Task<T> task;
    final private Computer computer;

    public Client( final String domainName, final Task<T> task )
        throws RemoteException, NotBoundException, MalformedURLException
    {
        this.task = task;
        String url = "rmi://" + domainName + ":" + Computer.PORT + "/" + Computer.SERVICE_NAME;
        //computer = domainName == null || domainName.isEmpty()
        //    ? new ComputerImpl() : (Computer) Naming.lookup( url );
        computer = (Computer) Naming.lookup(url);
    }

    void init( final String title )
    {
        this.setTitle( title );
        setDefaultCloseOperation( JFrame.EXIT_ON_CLOSE );
    }

    public void add( final JLabel jLabel )
    {
        final Container container = getContentPane();
        container.setLayout( new BorderLayout() );
        container.add( new JScrollPane( jLabel ), BorderLayout.CENTER );
        pack();
        setVisible( true );

    }

    public T runTask() throws RemoteException
    {
        final long taskStartTime = System.nanoTime();
        final T value = computer.Execute( task );
        final long taskRunTime = ( System.nanoTime() - taskStartTime ) / 1000000;
        Logger.getLogger( Client.class.getCanonicalName() )
            .log( Level.INFO, "Task {0}Task time: {1} ms.", new Object[]{ task, taskRunTime } );
        return value;

    }

}
