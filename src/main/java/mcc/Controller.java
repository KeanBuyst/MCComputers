package mcc;

import mcc.computer.objects.Object;
import mcc.computer.objects.controlled.Controlled;
import org.bukkit.Material;
import org.bukkit.block.BlockFace;

public class Controller {

    private Controlled controlled;
    private boolean failed = true;

    public Controller(String id){
        Object object = MCC.HANDLER.getObject(id);
        if (!object.isPlaced()) return;
        if (object instanceof Controlled controller){
            controlled = controller;
            failed = false;
        }
    }
    public void enable(){
        if (failed) return;
        controlled.enable();
    }
    public void disable(){
        if (failed) return;
        controlled.disable();
    }
    public void setDirection(char direction){
        if (failed) return;
        switch (direction){
            case 'N' -> controlled.setDirection(BlockFace.NORTH);
            case 'S' -> controlled.setDirection(BlockFace.SOUTH);
            case 'W' -> controlled.setDirection(BlockFace.WEST);
            case 'E' -> controlled.setDirection(BlockFace.EAST);
            case 'U' -> controlled.setDirection(BlockFace.UP);
            case 'D' -> controlled.setDirection(BlockFace.DOWN);
        }
    }
    public void move(){
        if (failed) return;
        controlled.move();
    }
    public void destroy(){
        if (failed) return;
        controlled.destroy();
    }
    public void turnLeft(){
        if (failed) return;
        controlled.turnLeft();
    }
    public void turnRight(){
        if (failed) return;
        controlled.turnRight();
    }
    public char getDirection(){
        if (failed) return ' ';
        return controlled.getDirection().toString().charAt(0);
    }
    public boolean isAir(char direction){
        if (failed) return false;
        BlockFace face = BlockFace.SOUTH;
        switch (direction){
            case 'N' -> face = BlockFace.NORTH;
            case 'W' -> face = BlockFace.WEST;
            case 'E' -> face = BlockFace.EAST;
            case 'U' -> face = BlockFace.UP;
            case 'D' -> face = BlockFace.DOWN;
        }
        return controlled.isAir(face);
    }
    public boolean hasInventory(){
        return controlled.hasInventory();
    }
    public boolean areSlotsFull(){
        if (!controlled.hasInventory()) return true;
        return controlled.areSlotsFull();
    }
    public int getAmountOf(String material) throws Exception {
        if (!controlled.hasInventory()) return 0;
        Material mat = Material.getMaterial(material);
        if (mat == null) throw new Exception("Unkown material: " + material);
        return controlled.getAmountOf(mat);
    }
    public String getMaterialAt(int slot){
        if (!controlled.hasInventory()) return "AIR";
        return controlled.getMaterialAt(slot).toString();
    }
}
