package mcc;

import mcc.computer.objects.Object;
import mcc.computer.objects.controlled.Controlled;
import org.bukkit.block.BlockFace;

public class Controller {

    private Controlled controlled;
    private boolean failed = true;
    private boolean disabled = true;

    public Controller(String id){
        Object object = MCC.HANDLER.getObject(id);
        if (!object.isPlaced()) return;
        if (object instanceof Controlled controller){
            controlled = controller;
            failed = false;
        }
    }
    public void enable(){
        if (failed || !disabled) return;
        controlled.enable();
        disabled = false;
    }
    public void disable(){
        if (failed || disabled) return;
        controlled.disable(false);
        disabled = true;
    }
    public void disable(boolean force){
        if (failed || disabled) return;
        controlled.disable(force);
        disabled = true;
    }
    public void setDirection(String direction){
        if (failed || disabled) return;
        switch (direction.charAt(0)){
            case 'N' -> controlled.setDirection(BlockFace.NORTH);
            case 'S' -> controlled.setDirection(BlockFace.SOUTH);
            case 'W' -> controlled.setDirection(BlockFace.WEST);
            case 'E' -> controlled.setDirection(BlockFace.EAST);
            case 'U' -> controlled.setDirection(BlockFace.UP);
            case 'D' -> controlled.setDirection(BlockFace.DOWN);
        }
    }
    public void move(){
        if (failed || disabled) return;
        controlled.move();
    }
    public void destroy(){
        if (failed || disabled) return;
        controlled.destroy();
    }
    public void turnLeft(){
        if (failed || disabled) return;
        controlled.turnLeft();
    }
    public void turnRight(){
        if (failed || disabled) return;
        controlled.turnRight();
    }
}
