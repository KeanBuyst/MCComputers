package mcc.computer.objects.controlled;

import org.bukkit.block.BlockFace;

public interface Controlled {
    void enable();
    void disable();
    void setDirection(BlockFace face);
    BlockFace getDirection();
    void turnLeft();
    void turnRight();
    boolean isAir(BlockFace direction);
    void move();
    void destroy();
}
