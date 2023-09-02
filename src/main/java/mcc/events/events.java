package mcc.events;

import mcc.MCC;
import mcc.computer.events.TextInput;
import mcc.computer.objects.Computer;
import mcc.computer.objects.Monitor;
import mcc.computer.objects.Object;
import mcc.computer.objects.controlled.Drone;
import mcc.storage.ToLoad;
import org.bukkit.ChatColor;
import org.bukkit.Chunk;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.*;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.ItemDespawnEvent;
import org.bukkit.event.inventory.CraftItemEvent;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerInteractAtEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.world.EntitiesLoadEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.metadata.FixedMetadataValue;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class events implements Listener {

    @EventHandler
    void onClick(PlayerInteractEvent event){
        if (event.getHand() != EquipmentSlot.HAND) return;
        ItemStack item = event.getItem();
        BlockFace face = event.getBlockFace();
        Action action = event.getAction();
        Block block = event.getClickedBlock();
        if (block != null) {
            if (item != null && item.hasItemMeta()) {
                ItemMeta meta = item.getItemMeta();
                if (meta.hasLore()){
                    String id = meta.getLore().get(0);
                    Object object = MCC.HANDLER.getObject(id);
                    if (object != null){
                        Block pos = block.getRelative(face);
                        if(object.place(event.getPlayer(), pos.getLocation(),face)){
                            item.setAmount(item.getAmount()-1);
                            object.getResult().setMetadata("ID",new FixedMetadataValue(MCC.This,id));
                        }
                    }
                }
            }

            if (block.hasMetadata("ID")) {
                Object object = MCC.HANDLER.getObject(block.getMetadata("ID").get(0).asString());
                if (object != null) {
                    object.onInteract(new OnInteract(event.getPlayer(), event.getInteractionPoint(),action,null,block,face));
                } else {
                    block.removeMetadata("ID",MCC.This);
                }
            }
        }
    }

    @EventHandler
    void onEntityClick(PlayerInteractAtEntityEvent event){
        if (event.getHand() != EquipmentSlot.HAND) return;
        Entity entity = event.getRightClicked();
        if (entity.hasMetadata("ID")){
            String id = entity.getMetadata("ID").get(0).asString();
            Object object = MCC.HANDLER.getObject(id);
            // TODO call onInteract
            if (object != null) {
                object.onInteract(new OnInteract(event.getPlayer(),event.getClickedPosition().toLocation(entity.getWorld()),null,entity,null,null));
            } else {
                entity.removeMetadata("ID",MCC.This);
            }
        }
    }
    @EventHandler
    void hitEntity(EntityDamageByEntityEvent event){
        if (event.getDamager().getType() != EntityType.PLAYER) return;
        Entity entity = event.getEntity();
        if (!entity.hasMetadata("ID")) return;
        String id = entity.getMetadata("ID").get(0).asString();
        Object object = MCC.HANDLER.getObject(id);
        if (object == null) {
            entity.removeMetadata("ID",MCC.This);
            return;
        }
        object.destroy((Player) event.getDamager());
        ItemStack item = object.getItem().clone();
        ItemMeta meta = item.getItemMeta();
        meta.setLore(List.of(id));
        item.setItemMeta(meta);
        entity.getWorld().dropItemNaturally(object.getLocation(),item);
    }
    @EventHandler
    void onDestroy(BlockBreakEvent event){
        Block block = event.getBlock();
        if (!block.hasMetadata("ID")) return;
        String id = block.getMetadata("ID").get(0).asString();
        Object object = MCC.HANDLER.getObject(id);
        if (object == null) return;
        event.setCancelled(true);
        block.removeMetadata("ID",MCC.This);
        object.destroy(event.getPlayer());
        ItemStack item = object.getItem().clone();
        ItemMeta meta = item.getItemMeta();
        meta.setLore(List.of(id));
        item.setItemMeta(meta);
        block.getWorld().dropItemNaturally(object.getLocation(),item);
    }
    @EventHandler
    void onExplode(BlockExplodeEvent event){
        Block block = event.getBlock();
        if (!block.hasMetadata("ID")) return;
        String id = block.getMetadata("ID").get(0).asString();
        Object object = MCC.HANDLER.getObject(id);
        if (object == null) return;
        event.setCancelled(true);
        MCC.HANDLER.removeObject(id);
        block.removeMetadata("ID",MCC.This);
        object.destroy(null);
    }
    @EventHandler
    void onPiston(BlockPistonExtendEvent event){
        for (Block block : event.getBlocks()){
            if (!block.hasMetadata("ID")) continue;
            event.setCancelled(true);
        }
    }
    @EventHandler
    void onPiston(BlockPistonRetractEvent event){
        for (Block block : event.getBlocks()){
            if (!block.hasMetadata("ID")) continue;
            event.setCancelled(true);
        }
    }
    @EventHandler
    void onKill(EntityDeathEvent event){
        LivingEntity entity = event.getEntity();
        if (!entity.hasMetadata("ID")) return;
        Player player = entity.getKiller();
        if (player == null) return;
        String id = entity.getMetadata("ID").get(0).asString();
        Object object = MCC.HANDLER.getObject(id);
        if (object == null) return;
        object.destroy(player);
        ItemStack item = object.getItem().clone();
        ItemMeta meta = item.getItemMeta();
        meta.setLore(List.of(id));
        item.setItemMeta(meta);
        entity.getWorld().dropItemNaturally(object.getLocation(),item);
    }
    @EventHandler
    void onDespawn(ItemDespawnEvent event){
        ItemStack item = event.getEntity().getItemStack();
        if (!item.hasItemMeta()) return;
        ItemMeta meta = item.getItemMeta();
        if (!meta.hasLore()) return;
        String id = meta.getLore().get(0);
        MCC.HANDLER.removeObject(id);
    }
    @EventHandler
    void Craft(CraftItemEvent event){
        ItemStack result = event.getInventory().getResult();
        Player player = (Player) event.getWhoClicked();
        ItemMeta meta = result.getItemMeta();
        if (result.hasItemMeta() && meta.hasCustomModelData()){
            ArrayList<String> lore = new ArrayList<>();
            switch (meta.getCustomModelData()){
                case 12001 -> {
                    String uuid = UUID.randomUUID().toString();
                    lore.add(uuid);
                    MCC.HANDLER.addObject(uuid,new Monitor());
                }
                case 12002 -> {
                    String uuid = UUID.randomUUID().toString();
                    lore.add(uuid);
                    Computer computer = new Computer();
                    computer.authorize(player.getUniqueId());
                    MCC.HANDLER.addObject(uuid,computer);
                }
                case 12003 -> {
                    String uuid = UUID.randomUUID().toString();
                    lore.add(uuid);
                    Drone drone = new Drone();
                    drone.authorize(player.getUniqueId());
                    MCC.HANDLER.addObject(uuid,drone);
                }
            }
            meta.setLore(lore);
            result.setItemMeta(meta);
        }
    }

    @EventHandler
    void onChat(AsyncPlayerChatEvent event){
        String key = "cc_linked";
        event.getRecipients().removeIf(p -> p.hasMetadata(key));
        Player player = event.getPlayer();
        if (player.hasMetadata(key)){
            String id = player.getMetadata(key).get(0).asString();
            Computer computer = (Computer) MCC.HANDLER.getObject(id);
            if (computer == null){
                player.sendMessage(ChatColor.RED+"Connection failed");
                return;
            }
            if (!computer.isPlaced()){
                player.sendMessage(ChatColor.RED+"Computer["+id+"]: no longer exists");
                player.removeMetadata(key,MCC.This);
                return;
            }
            computer.getView().input(new TextInput(event.getMessage(), player));
            event.setCancelled(true);
        }
    }
    // Block and entity loading
    @EventHandler
    void onEntityLoad(EntitiesLoadEvent event){
        Chunk chunk = event.getChunk();
        for (ToLoad toLoad : MCC.This.toLoads){
            if (toLoad.x == chunk.getX() && toLoad.z == chunk.getZ()){
                toLoad.load();
                MCC.This.toLoads.remove(toLoad);
            }
        }
    }
}
