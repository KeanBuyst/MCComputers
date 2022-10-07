package mcc.computer.objects.controlled;

public class Action {
    public final int delay;
    private final Runnable runnable;
    Action(int delay,Runnable action) {
        this.delay = delay;
        this.runnable = action;
    }
    public void run(){
        runnable.run();
    }
}
