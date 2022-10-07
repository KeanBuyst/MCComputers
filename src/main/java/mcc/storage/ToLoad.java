package mcc.storage;

public class ToLoad {
    public final int x,z;
    private Runnable onload;

    public ToLoad(int x,int z){
        this.x = x;
        this.z = z;
    }

    public void setOnload(Runnable onload){
        this.onload = onload;
    }
    public void load(){
        onload.run();
    }
}
