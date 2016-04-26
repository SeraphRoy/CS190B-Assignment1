package api;

public abstract class Client{
    abstract public bool isChild();
    abstract public void send_argument();
    abstract public void spawn_next();
    abstract public void spawn();
    public void run(){
        if(this.isChild())
            send_argument();
        else{
            spawn_next();
            spawn();
        }
    }
}
