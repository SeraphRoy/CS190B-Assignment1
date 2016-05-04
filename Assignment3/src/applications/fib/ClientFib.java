package applications.fib;
import api.*;
//import system.*;
import java.util.List;
import java.util.ArrayList;
import java.net.MalformedURLException;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;

public class ClientFib extends Client{
    public ClientFib(Task task, String domainName) throws RemoteException, NotBoundException, MalformedURLException{
        super(task, "Fib", domainName);
    }

    public static void main(String[] args) throws Exception{
        Client c = new ClientFib(null, "localhost");
        Argument a = new Argument(10, 0);
        List<Argument> list = new ArrayList<>();
        list.add(a);
        Continuation cont = new Continuation(-1, 0);
        Task t = new TaskFib(c.getSpace(), list, cont);
        c.setTask(t);
        c.run();
    }
}
