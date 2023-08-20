package mcc.events;

import mcc.MCC;
import mcc.computer.objects.Object;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.block.Action;

public record OnInteract (Player player, Location location, Action action, Entity entity, Block block, BlockFace face) {

    public Object getInteractedObject(){
        if (block != null && block.hasMetadata("ID")){
            String id = block.getMetadata("ID").get(0).asString();
            Object object = MCC.HANDLER.getObject(id);
            if (object != null) return object;
        }
        if (entity != null && entity.hasMetadata("ID")){
            String id = entity.getMetadata("ID").get(0).asString();
            return MCC.HANDLER.getObject(id);
        }
        return null;
    }
}
