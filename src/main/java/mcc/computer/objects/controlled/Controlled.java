package mcc.computer.objects.controlled;

import org.bukkit.block.BlockFace;

public interface Controlled {
    void enable();
    void disable(boolean force);
    void setDirection(BlockFace face);
    void turnLeft();
    void turnRight();
    void move();
    void destroy();
}
