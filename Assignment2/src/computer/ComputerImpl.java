package computer;

import api.Computer;
import api.Space;
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.rmi.registry.Registry;
import java.rmi.registry.LocateRegistry;
import java.rmi.Naming;
import java.util.concurrent.BlockingQueue;
import java.util.List;

public class ComputerImpl extends UnicastRemoteObject implements Computer{
    public <T> T Execute(Task<T> t){
        return t.Execute();
    }

    public void main(String[] args){
        String domainName = "localhost";
        System.setSecurityManager( new SecurityManager() );
        String url = "rmi://" + domainName + ":" + Space.PORT + "/" + Space.SERVICE_NAME;

        Space space = (Space) Naming.lookup(url);
        space.register(this);
    }
}
