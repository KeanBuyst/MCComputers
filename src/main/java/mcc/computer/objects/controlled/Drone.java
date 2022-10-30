package mcc.computer.objects.controlled;

import mcc.MCC;
import mcc.computer.objects.Authentication;
import mcc.computer.objects.Object;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Player;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.metadata.Metadatable;
import org.bukkit.util.Consumer;
import org.python.modules.time.Time;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.function.Function;

public class Drone implements Object, Authentication, Controlled {
    public static final ItemStack ITEM;
    private static final int ID = 12003;

    static {
        ITEM = new ItemStack(Material.ENDERMITE_SPAWN_EGG);
        ItemMeta meta = ITEM.getItemMeta();
        meta.setDisplayName("Drone");
        meta.setCustomModelData(ID);
        ITEM.setItemMeta(meta);
    }

    private ArrayList<UUID> authorized = new ArrayList<>();
    private boolean isPlaced = false;
    private boolean disabled = true;
    public entity entity;

    public Drone() {}
    public Drone(UUID bodyID,List<UUID> bladesID){
        isPlaced = true;
        entity = new entity(bodyID,bladesID);
    }

    public boolean place(Player player, Location location, BlockFace face) {
        location.add(0.5,-.4,0.5);
        entity = new entity(location);
        isPlaced = true;
        return true;
    }
    public void destroy(Player player) {
        isPlaced = false;
        entity.disable();
        entity.kill();
    }
    public ItemStack getItem() {
        return ITEM;
    }
    public int getID() {
        return ID;
    }
    public Location getLocation() {
        return entity.body.getLocation();
    }

    public boolean isPlaced() {
        return isPlaced;
    }

    public Metadatable getResult() {
        return entity.body;
    }

    public void enable() {
        if (!disabled) return;
        entity.enable();
        disabled = false;
    }
    public void disable() {
        if (disabled) return;
        entity.disable();
        disabled = true;
    }
    public void setDirection(BlockFace face) {
        if (disabled) return;
        entity.setDirection(face);
        Time.sleep(.5);
    }
    public void turnLeft(){
        if (disabled) return;
        BlockFace direction = entity.getDirection();
        switch (direction){
            case EAST -> direction = BlockFace.NORTH;
            case NORTH -> direction = BlockFace.WEST;
            case WEST -> direction = BlockFace.SOUTH;
            case SOUTH -> direction = BlockFace.EAST;
        }
        entity.setDirection(direction);
    }
    public void turnRight(){
        if (disabled) return;
        BlockFace direction = entity.getDirection();
        switch (direction){
            case EAST -> direction = BlockFace.SOUTH;
            case SOUTH -> direction = BlockFace.WEST;
            case WEST -> direction = BlockFace.NORTH;
            case NORTH -> direction = BlockFace.EAST;
        }
        entity.setDirection(direction);
    }
    public boolean isAir(BlockFace direction) {
        return entity.isAir(direction);
    }

    public void move() {
        if (disabled) return;
        entity.move();
        Time.sleep(1);
    }
    public void destroy() {
        if (disabled) return;
        entity.destroy();
        Time.sleep(.5);
    }
    public BlockFace getDirection() {
        return entity.direction;
    }
    public ArmorStand getBody(){
        return entity.body;
    }
    public ArmorStand[] getBlades(){
        return entity.blades;
    }

    public ArrayList<UUID> getAuthorized() {
        return authorized;
    }
    public void authorize(UUID uuid) {
        authorized.add(uuid);
    }
    public void unauthorize(UUID uuid) {
        authorized.remove(uuid);
    }
    public boolean authorized(UUID uuid) {
        return authorized.contains(uuid);
    }
    public void setAuthorized(ArrayList<UUID> uuids) {
        authorized = uuids;
    }

    private static class entity {
        private final ConcurrentLinkedQueue<Runnable> synchronised = new ConcurrentLinkedQueue<>();
        private ArmorStand[] blades = new ArmorStand[4];
        private final ArmorStand body;
        private BlockFace direction = BlockFace.SOUTH;
        private int loopID;

        public entity(UUID bodyID,List<UUID> bladesID){
            body = (ArmorStand) Bukkit.getEntity(bodyID);
            blades = bladesID.stream().map(new Function<UUID, ArmorStand>() {
                public ArmorStand apply(UUID uuid) {
                    return (ArmorStand) Bukkit.getEntity(uuid);
                }
            }).toArray(ArmorStand[]::new);
        }
        @SuppressWarnings("deprecation")
        public entity(Location location){
            body = location.getWorld().spawn(location, ArmorStand.class, new Consumer<ArmorStand>() {
                public void accept(ArmorStand armorStand) {
                    armorStand.setVisible(false);
                    armorStand.setSmall(true);
                    armorStand.setBasePlate(false);
                    armorStand.setGravity(false);
                    armorStand.setCustomName("Drone");
                    armorStand.setCustomNameVisible(false);
                    armorStand.addEquipmentLock(EquipmentSlot.HEAD, ArmorStand.LockType.REMOVING_OR_CHANGING);
                    ItemStack skull = new ItemStack(Material.PLAYER_HEAD);
                    skull = Bukkit.getUnsafe().modifyItemStack(skull,"{display:{Name:\"Computer Tower\"},SkullOwner:{Id:\"6ce84ae3-53e0-43f1-99df-6f680850a43e\",Properties:{textures:[{Value:\"eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvM2I5MjVkM2E1Mjc1OWZkZThjMDI1ODg3MWZlZmQ5MTQxZTVjOTdmZGY0NTNhZjNkZjIxMTA0Y2M4YzQ4OCJ9fX0=\"}]}}}");
                    armorStand.getEquipment().setHelmet(skull,true);
                }
            });
            for (int i = 0; i < blades.length; i++){
                Location loc = location.clone();
                switch (i){
                    case 0 -> loc.add(0.3,0.4,0.3);
                    case 1 -> loc.add(0.3,0.4,-0.3);
                    case 2 -> loc.add(-0.3,0.4,-0.3);
                    case 3 -> loc.add(-0.3,0.4,0.3);
                }
                blades[i] = location.getWorld().spawn(loc, ArmorStand.class, new Consumer<ArmorStand>() {
                    public void accept(ArmorStand armorStand) {
                        armorStand.setVisible(false);
                        armorStand.setSmall(true);
                        armorStand.setMarker(true);
                        armorStand.setBasePlate(false);
                        armorStand.setInvisible(true);
                        armorStand.setGravity(false);
                        armorStand.setInvulnerable(true);
                        armorStand.getEquipment().setHelmet(new ItemStack(Material.IRON_TRAPDOOR),true);
                    }
                });
            }
        }
        public void enable(){
            loopID = Bukkit.getScheduler().scheduleSyncRepeatingTask(MCC.This, new Runnable() {
                public void run() {
                    Chunk chunk = body.getLocation().getChunk();
                    if (!chunk.isLoaded()) chunk.load();
                    while (!synchronised.isEmpty()){
                        synchronised.poll().run();
                    }
                    animate();
                }
            },0,0);
        }
        public void disable(){
            Bukkit.getScheduler().cancelTask(loopID);
        }
        short soundTick = 0;
        public void animate(){
            if (!MCC.This.ANIMATIONS) return;
            for (ArmorStand blade : blades){
                Location location = blade.getLocation().clone();
                location.add(0,1,0);
                float yaw = location.getYaw();
                if (yaw >= 360){
                    yaw = 0;
                } else {
                    yaw += 15;
                }
                blade.setRotation(yaw, location.getPitch());
                if (Math.random()*100 > 98) {
                    location.getWorld().spawnParticle(Particle.SWEEP_ATTACK,location,0);
                }
                if (soundTick == 20){
                    location.getWorld().playSound(location, Sound.ENTITY_PLAYER_ATTACK_SWEEP,.5f,3);
                    soundTick = 0;
                } else {
                    soundTick++;
                }
            }
        }
        public void setDirection(BlockFace face){
            direction = face;
            synchronised.add(new Runnable() {
                public void run() {
                    Location loc = body.getLocation().setDirection(face.getDirection());
                    body.teleport(loc);
                }
            });
        }
        public void move(){
            // Synchronise task
            synchronised.add(new Runnable() {
                public void run() {
                    Location b = body.getLocation().add(direction.getDirection());
                    if (body.getEyeLocation().add(direction.getDirection()).getBlock().getType() != Material.AIR) return;
                    body.teleport(b);
                    for (ArmorStand blade : blades){
                        Location l = blade.getLocation().add(direction.getDirection());
                        blade.teleport(l);
                    }
                }
            });
        }
        public void destroy(){
            synchronised.add(new Runnable() {
                public void run() {
                    Block block = body.getEyeLocation().getBlock().getRelative(direction);
                    if (block.getType().isAir()) return;
                    block.breakNaturally(new ItemStack(Material.NETHERITE_PICKAXE));
                }
            });
        }
        public void kill(){
            body.remove();
            for (ArmorStand blade : blades) blade.remove();
        }
        public BlockFace getDirection(){
            return direction;
        }
        public boolean isAir(BlockFace direction){
            return body.getEyeLocation().getBlock().getRelative(direction).getType().isAir();
        }

        public UUID getID(){
            return body.getUniqueId();
        }
    }
}
