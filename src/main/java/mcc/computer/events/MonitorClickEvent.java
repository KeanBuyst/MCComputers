package mcc.computer.events;

import mcc.computer.objects.Monitor;
import mcc.computer.view.ScreenView;
import org.bukkit.Location;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import org.bukkit.event.player.PlayerEvent;
import org.bukkit.util.Vector;

public class MonitorClickEvent extends PlayerEvent {

    private static final HandlerList handlers = new HandlerList();

    private final Monitor monitor;
    private final BlockFace facing;
    private final int x,y;

    public MonitorClickEvent(Player who, Vector vector, Monitor monitor, BlockFace facing) {
        super(who);
        this.monitor = monitor;
        this.facing = facing;
        // Item frame hitbox vector points = X: from -0.375 to 0.375 | Y: from -0.375 to 0.375
        double x;
        double y = vector.getY();
        if (facing == BlockFace.WEST || facing == BlockFace.EAST){
            x = vector.getZ();
        }else {
            x = vector.getX();
        }
        // Shift range from (-0.375 - 0.375) to (0 - 0.750)
        x += 0.375;
        // Rotate y
        y = 0.750 - (y + 0.375);
        // 96 / 0.750 = 128
        // 96 being the amount of pixels found within the hitbox area of the item frame
        x *= 128;
        y *= 128;
        // 16 pixel shift since hit box is in the centre of item frame
        x += 16;
        y += 16;
        // Rotating x based on frames direction
        if (facing == BlockFace.NORTH || facing == BlockFace.EAST){
            x = 127 - x;
        }
        ScreenView view = monitor.getView();
        x -= view.getOffsetX();
        y -= view.getOffsetY();
        this.x = (int) x;
        this.y = (int) y;
        view.input(this);
    }

    public MonitorClickEvent(Player who, Location location, Monitor monitor,BlockFace facing){
        super(who);
        this.monitor = monitor;
        this.facing = facing;
        int x;
        int y = (int) location.getY();
        y = (int) (((location.getY() - y) * 127));
        if (facing == BlockFace.WEST || facing == BlockFace.EAST){
            x = (int) location.getZ();
            x = (int) ((location.getZ() - x) * 127);
            x *= -1;
        }else {
            x = (int) location.getX();
            x = (int) ((location.getX() - x) * 127);
        }
        //x *= 0.12712712712712712;
        //y *= 0.12712712712712712;
        y -= 127;
        y *= -1;
        y -= player.getLocation().getPitch() / 5;
        if (facing == BlockFace.NORTH || facing == BlockFace.WEST){
            x = 127 - x;
        }
        ScreenView view = monitor.getView();
        x -= view.getOffsetX();
        y -= view.getOffsetY();
        this.x = x;
        this.y = y;
        view.input(this);
    }

    public Monitor getMonitor() {
        return monitor;
    }

    public BlockFace getFacing() {
        return facing;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public HandlerList getHandlers() {
        return handlers;
    }
}
