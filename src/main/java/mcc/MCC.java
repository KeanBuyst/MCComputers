package mcc;

import mcc.commands.CComputer;
import mcc.commands.TComputer;
import mcc.computer.files.PyFile;
import mcc.computer.inventory.Crafting;
import mcc.events.events;
import mcc.placement.ObjectHandler;
import mcc.storage.Storage;
import mcc.storage.ToLoad;
import org.bukkit.Bukkit;
import org.bukkit.command.PluginCommand;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitWorker;

import java.io.File;
import java.util.concurrent.ConcurrentLinkedQueue;

public final class MCC extends JavaPlugin {

    public final static ObjectHandler HANDLER = new ObjectHandler();
    public static MCC This;
    public final ConcurrentLinkedQueue<ToLoad> toLoads = new ConcurrentLinkedQueue<>();

    public boolean ANIMATIONS = true;

    public void onEnable() {
        This = this;
        saveDefaultConfig();
        PyFile.config();
        ANIMATIONS = getConfig().getBoolean("drone.animations");
        File computers = new File(getDataFolder(),"computers");
        if (computers.exists()){
            for (String id : computers.list()){
                Storage.prepareComputer(id);
            }
        }
        File drones = new File(getDataFolder(),"drones");
        if (drones.exists()){
            for (File file : drones.listFiles()){
                Storage.prepareDrone(file);
            }
        }
        getServer().getPluginManager().registerEvents(new events(),this);
        PluginCommand command = getCommand("computer");
        command.setExecutor(new CComputer());
        command.setTabCompleter(new TComputer());
        new Crafting();
    }

    public void onDisable() {
        Bukkit.getScheduler().cancelTasks(this);
        for (String id : HANDLER.getIds()){
            Storage.uploadComputer(id);
            Storage.uploadDrone(id);
        }
    }
}
