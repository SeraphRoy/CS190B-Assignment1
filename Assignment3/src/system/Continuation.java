package system;

class Continuation{
    public Closure closure;
    public int slot;

    public Continuation(Closure c, int s){
        closure = c;
        slot = s;
    }
}
