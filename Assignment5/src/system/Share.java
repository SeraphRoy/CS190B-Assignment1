package system;

import api.*;
import java.io.Serializable;

public class Share implements Serializable{
    private Comparable value;

    public Share(Comparable value){
        this.value = value;
    }

    public synchronized Comparable getValue(){return value;}

    public synchronized void setValue(Comparable value){
        this.value = value;
    }

    public synchronized Share getBetterOne(Share that){
        if(this.getValue().compareTo(that.getValue()) > 0)
            return that;
        return this;
    }

    public synchronized boolean isBetterThan(Share that){
        if(this.getValue().compareTo(that.getValue()) < 0)
            return true;
        return false;
    }
}
