package mcc.computer.objects;

import mcc.computer.events.MonitorClickEvent;
import mcc.computer.view.ScreenView;
import mcc.events.OnInteract;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.GlowItemFrame;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.MapMeta;
import org.bukkit.map.MapCanvas;
import org.bukkit.map.MapRenderer;
import org.bukkit.map.MapView;
import org.bukkit.metadata.Metadatable;
import org.bukkit.util.Consumer;
import org.jetbrains.annotations.NotNull;

public class Monitor implements Object {

    public static final ItemStack ITEM;
    private static final int ID = 12001;
    private ScreenView view;
    private GlowItemFrame frame;
    private boolean isPlaced = false;

    static {
        ITEM = new ItemStack(Material.FILLED_MAP);
        ItemMeta meta = ITEM.getItemMeta();
        meta.displayName(Component.text("Monitor"));
        meta.setCustomModelData(ID);
        ITEM.setItemMeta(meta);
    }

    public Monitor(){}
    public Monitor(GlowItemFrame frame, Byte[] byteMap){
        this.frame = frame;
        MapMeta meta = (MapMeta) frame.getItem().getItemMeta();
        MapView v = Bukkit.getMap(meta.getMapId());
        for (MapRenderer renderer : v.getRenderers()) v.removeRenderer(renderer);
        v.addRenderer(new MapRenderer() {
            private boolean first = true;
            public void render(@NotNull MapView map, @NotNull MapCanvas canvas, @NotNull Player player) {
                if (first){
                    first = false;
                    Monitor.this.view = new ScreenView(canvas);
                    Monitor.this.view.setByteMap(byteMap);
                }
            }
        });
        meta.setMapView(v);
        frame.getItem().setItemMeta(meta);
    }
    public Monitor(GlowItemFrame frame){
        isPlaced = true;
        this.frame = frame;
        MapMeta meta = (MapMeta) frame.getItem().getItemMeta();
        MapView v = Bukkit.getMap(meta.getMapId());
        for (MapRenderer renderer : v.getRenderers()) v.removeRenderer(renderer);
        v.addRenderer(new MapRenderer() {
            private boolean first = true;
            public void render(@NotNull MapView map, @NotNull MapCanvas canvas, @NotNull Player player) {
                if (first){
                    first = false;
                    Monitor.this.view = new ScreenView(canvas);
                }
            }
        });
        meta.setMapView(v);
        frame.getItem().setItemMeta(meta);
    }
    public boolean place(Player player,Location location, BlockFace face) {
        if (!location.getBlock().getType().isAir()) return false;
        frame = location.getWorld().spawn(location, GlowItemFrame.class, glowItemFrame -> {
            glowItemFrame.setFacingDirection(face,true);
            glowItemFrame.setFixed(true);
            glowItemFrame.setInvulnerable(true);
        });
        MapView view = Bukkit.createMap(location.getWorld());
        view.addRenderer(new MapRenderer() {
            private boolean first = true;
            public void render(@NotNull MapView map, @NotNull MapCanvas canvas, @NotNull Player player) {
                if (first){
                    first = false;
                    Monitor.this.view = new ScreenView(canvas);
                }
                Monitor.this.view.onRender();
            }
        });
        ItemStack map = new ItemStack(Material.FILLED_MAP);
        MapMeta meta = (MapMeta) map.getItemMeta();
        meta.setMapView(view);
        map.setItemMeta(meta);
        frame.setItem(map);
        isPlaced = true;
        return true;
    }

    public void destroy(Player player) {
        isPlaced = false;
        frame.remove();
        view = null;
    }

    public void onInteract(OnInteract onInteract) {
        MonitorClickEvent event;
        if (onInteract.block() == null)
            event = new MonitorClickEvent(onInteract.player(), onInteract.location().toVector(), this, onInteract.entity().getFacing());
        else
            event = new MonitorClickEvent(onInteract.player(), onInteract.location(), this, onInteract.entity().getFacing());
        Bukkit.getServer().getPluginManager().callEvent(event);
    }

    public ItemStack getItem() {
        return ITEM;
    }
    public int getID() {
        return ID;
    }
    public Location getLocation() {
        return frame.getLocation();
    }

    public boolean isPlaced() {
        return isPlaced;
    }

    public Metadatable getResult() {
        return frame;
    }

    public ScreenView getView() {
        return view;
    }
    public GlowItemFrame getFrame(){
        return frame;
    }
}
