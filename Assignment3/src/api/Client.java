package api;

public abstract class Client{
    abstract public boolean isCrossedThreshold();
    abstract public void send_argument();
    abstract public void spawn_next();
    abstract public void spawn();
    public void run(){
        if(this.isCrossedThreshold())
            send_argument();
        else{
            spawn_next();
            spawn();
        }
    }
}
