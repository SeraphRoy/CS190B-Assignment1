package system;

import api.*;
import java.io.Serializable;

public class Share implements Serializable{
    private Comparable value;

    public Share(Comparable value){
        this.value = value;
    }

    public Comparable getValue(){return value;}

    public void setValue(Comparable value){
        this.value = value;
    }

    public Share getBetterOne(Share that){
        if(this.getValue().compareTo(that.getValue()) > 0)
            return that;
        return this;
    }

    public boolean isBetterThan(Share that){
        if(this.getValue().compareTo(that.getValue()) < 0)
            return true;
        return false;
    }
}
