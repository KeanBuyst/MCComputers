package mcc.computer.objects;

import mcc.MCC;
import mcc.computer.view.ScreenView;
import mcc.events.OnInteract;
import net.kyori.adventure.text.Component;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.metadata.Metadatable;

import java.util.ArrayList;
import java.util.UUID;

public class Computer implements Object, Authentication {

    public static final ItemStack ITEM;
    private static final int ID = 12002;
    private Monitor monitor;
    private Block block;
    private boolean isPlaced = false;
    // User Authentication
    private ArrayList<UUID> authorized = new ArrayList<>();
    public Computer(){}

    public Computer(Block block, Monitor monitor){
        isPlaced = true;
        this.block = block;
        this.monitor = monitor;
    }

    static {
        ITEM = new ItemStack(Material.IRON_BLOCK);
        ItemMeta meta = ITEM.getItemMeta();
        meta.displayName(Component.text("Computer"));
        meta.setCustomModelData(ID);
        ITEM.setItemMeta(meta);
    }
    public boolean place(Player player, Location location, BlockFace face){
        face = getCorrectFace(player,face);
        block = location.getBlock();
        if (!block.getType().isAir()) return false;
        block.setType(Material.IRON_BLOCK);
        monitor = new Monitor();
        if(monitor.place(player,block.getRelative(face).getLocation(),face)){
            String id = monitor.getFrame().getUniqueId().toString();
            monitor.getFrame().setMetadata("ID",new FixedMetadataValue(MCC.This,id));
            MCC.HANDLER.addObject(id,monitor);
            isPlaced = true;
            return true;
        }
        return false;
    }

    private BlockFace getCorrectFace(Player player, BlockFace face){
        if (face != BlockFace.DOWN && face != BlockFace.UP) return face;
        face = player.getFacing().getOppositeFace();
        if (face != BlockFace.DOWN && face != BlockFace.UP) return face;
        return BlockFace.NORTH;
    }

    public void destroy(Player player) {
        isPlaced = false;
        monitor.destroy(player);
        block.setType(Material.AIR);
    }

    public void onInteract(OnInteract onInteract) {

    }

    public ItemStack getItem() {
        return ITEM;
    }

    public int getID() {
        return ID;
    }

    public Location getLocation() {
        return block.getLocation();
    }

    public boolean isPlaced() {
        return isPlaced;
    }

    public Metadatable getResult() {
        return block;
    }

    public ScreenView getView(){
        return monitor.getView();
    }
    public Monitor getMonitor(){
        return monitor;
    }

    public ArrayList<UUID> getAuthorized() {
        return authorized;
    }
    public void authorize(UUID uuid){
        authorized.add(uuid);
    }
    public void unauthorize(UUID uuid){
        authorized.remove(uuid);
    }
    public boolean authorized(UUID uuid){
        return authorized.contains(uuid);
    }
    public void setAuthorized(ArrayList<UUID> uuids){
        authorized = uuids;
    }
}
