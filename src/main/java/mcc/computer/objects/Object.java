package mcc.computer.objects;

import org.bukkit.Location;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.metadata.Metadatable;

public interface Object {
    boolean place(Player player,Location location, BlockFace face);
    void destroy(Player player);
    ItemStack getItem();
    int getID();
    Location getLocation();
    boolean isPlaced();

    /**
     * <p>
     *     Will return null if the method
     *     is called before the object is placed
     * </p>
     * @return the block or entity placed
     */
    Metadatable getResult();
}
