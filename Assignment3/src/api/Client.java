package api;
import system.*;
import java.rmi.RemoteException;
import java.rmi.NotBoundException;
import java.net.MalformedURLException;
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

public class Client<V> extends JFrame{
    final private Space space;
    final private Task<V> task;
    final private long startTime = System.nanoTime();

    public Client(Task<V> task, String title, String domainName) throws RemoteException, NotBoundException, MalformedURLException{
    System.setSecurityManager( new SecurityManager() );
    setTitle( title );
    setDefaultCloseOperation( JFrame.EXIT_ON_CLOSE );
    this.task = task;
    final String url = "rmi://" + domainName + ":" + Space.PORT + "/" + Space.SERVICE_NAME;
    space = (Space) Naming.lookup( url );
    }

    private void view(final JLabel jLabel){
        final Container container = getContentPane();
        container.setLayout(new BorderLayout());
        container.add(new JScrollPane(jLabel), BorderLayout.CENTER);
        pack();
        setVisible(true);
    }

    //need to implement
    public void run() throws RemoteException{
        space.putReady(task);
        view(task.viewResult(space.getResult()));
        Logger.getLogger( this.getClass().getCanonicalName() )
            .log( Level.INFO, "Job run time: {0} ms.", ( System.nanoTime() - startTime) / 1000000 );
    }
}
