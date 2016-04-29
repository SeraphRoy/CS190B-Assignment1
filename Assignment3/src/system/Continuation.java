package system;

class Continuation{
    final private Closure closure;
    final private int slot;

    public Continuation(Closure c, int s){
        closure = c;
        slot = s;
    }

    public Closure getClosure(){return closure;}

    public int getSlot(){return slot;}
}
