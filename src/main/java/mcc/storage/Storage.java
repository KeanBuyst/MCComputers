package mcc.storage;

import mcc.MCC;
import mcc.computer.objects.Computer;
import mcc.computer.objects.Monitor;
import mcc.computer.objects.controlled.Drone;
import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.GlowItemFrame;
import org.bukkit.metadata.FixedMetadataValue;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import java.util.function.Function;

public class Storage {
    public static void uploadComputer(String id){
        Object object = MCC.HANDLER.getObject(id);
        if (object instanceof Computer computer){
            File dataFile = new File(MCC.This.getDataFolder(),"computers/"+id+"/data.yml");
            if (!dataFile.exists()){
                try {
                    dataFile.getParentFile().mkdirs();
                    dataFile.createNewFile();
                } catch (IOException e) {
                    e.printStackTrace();
                    return;
                }
            }
            YamlConfiguration yml = new YamlConfiguration();
            try {
                yml.load(dataFile);
                Location location = computer.getLocation();
                Chunk chunk = computer.getMonitor().getLocation().getChunk();
                yml.set("Placed", computer.isPlaced());
                if (computer.isPlaced()){
                    yml.set("Chunk.x", chunk.getX());
                    yml.set("Chunk.z", chunk.getZ());
                    yml.set("Computer.location", location.getWorld().getName() + ":" + location.getBlockX() + ":" + location.getBlockY() + ":" + location.getBlockZ());
                    yml.set("Monitor.id", computer.getMonitor().getFrame().getUniqueId().toString());
                }
                yml.set("Users.authorized", computer.getAuthorized().stream().map(new Function<UUID, String>() {
                    public String apply(UUID uuid) {
                            return uuid.toString();
                        }
                }).toList());
                yml.save(dataFile);
            } catch (IOException | InvalidConfigurationException e) {
                e.printStackTrace();
            }
        }
    }
    public static void prepareComputer(String id){
        File dataFile = new File(MCC.This.getDataFolder(),"computers/"+id+"/data.yml");
        if (!dataFile.exists()) return;
        YamlConfiguration yml = new YamlConfiguration();
        try {
            yml.load(dataFile);
            boolean isPlaced = yml.getBoolean("Placed");
            if (isPlaced){
                ToLoad toLoad = new ToLoad(yml.getInt("Chunk.x"),yml.getInt("Chunk.z"));
                toLoad.setOnload(new Runnable() {
                    public void run() {
                        String[] args = yml.getString("Computer.location").split(":");
                        Block block = new Location(Bukkit.getWorld(args[0]),Integer.parseInt(args[1]),Integer.parseInt(args[2]),Integer.parseInt(args[3])).getBlock();
                        String mid = yml.getString("Monitor.id");
                        GlowItemFrame frame = (GlowItemFrame) Bukkit.getEntity(UUID.fromString(mid));
                        Monitor monitor = new Monitor(frame);
                        ArrayList<UUID> uuids = new ArrayList<>(yml.getStringList("Users.authorized").stream().map(UUID::fromString).toList());
                        Computer computer = new Computer(block,monitor);
                        computer.setAuthorized(uuids);
                        frame.setMetadata("ID",new FixedMetadataValue(MCC.This,mid));
                        block.setMetadata("ID",new FixedMetadataValue(MCC.This,id));
                        MCC.HANDLER.addObject(mid,monitor);
                        MCC.HANDLER.addObject(id,computer);
                        MCC.This.getLogger().info("Successfully loaded: "+id);
                    }
                });
                MCC.This.toLoads.add(toLoad);
            } else {
                Computer computer = new Computer();
                computer.setAuthorized(new ArrayList<>(yml.getStringList("Users.authorized").stream().map(UUID::fromString).toList()));
                MCC.HANDLER.addObject(id,computer);
            }
        } catch (IOException | InvalidConfigurationException e) {
            e.printStackTrace();
        }
    }
    public static void uploadDrone(String id){
        Object object = MCC.HANDLER.getObject(id);
        if (object instanceof Drone drone){
            File dataFile = new File(MCC.This.getDataFolder(),"drones/"+id+".yml");
            if (!dataFile.exists()){
                try {
                    dataFile.getParentFile().mkdirs();
                    dataFile.createNewFile();
                } catch (IOException e) {
                    e.printStackTrace();
                    return;
                }
            }
            YamlConfiguration yml = new YamlConfiguration();
            try {
                yml.load(dataFile);
                if (drone.isPlaced()){
                    Chunk chunk = drone.getLocation().getChunk();
                    yml.set("Placed",true);
                    yml.set("Chunk.x",chunk.getX());
                    yml.set("Chunk.z",chunk.getZ());
                    yml.set("Body",drone.getBody().getUniqueId().toString());
                    yml.set("Blades", Arrays.stream(drone.getBlades()).map(new Function<ArmorStand, String>() {
                        public String apply(ArmorStand armorStand) {
                            return armorStand.getUniqueId().toString();
                        }
                    }).toList());
                } else {
                    yml.set("Placed",false);
                }
                yml.set("Users.authorized",drone.getAuthorized().stream().map(UUID::toString).toList());
                yml.save(dataFile);
            } catch (IOException | InvalidConfigurationException e) {
                e.printStackTrace();
            }
        }
    }
    public static void prepareDrone(File file){
        if (!file.exists()) return;
        YamlConfiguration yml = new YamlConfiguration();
        try {
            yml.load(file);
            String filename = file.getName();
            String id = filename.substring(0,filename.length()-4);
            boolean isPlaced = yml.getBoolean("Placed");
            if (isPlaced){
                ToLoad toLoad = new ToLoad(yml.getInt("Chunk.x"),yml.getInt("Chunk.z"));
                toLoad.setOnload(new Runnable() {
                    public void run() {
                        UUID body = UUID.fromString(yml.getString("Body"));
                        List<UUID> blades = yml.getStringList("Blades").stream().map(UUID::fromString).toList();
                        ArrayList<UUID> authorized = new ArrayList<>(yml.getStringList("Users.authorized").stream().map(UUID::fromString).toList());
                        Drone drone = new Drone(body,blades);
                        drone.setAuthorized(authorized);
                        drone.getBody().setMetadata("ID",new FixedMetadataValue(MCC.This,id));
                        MCC.HANDLER.addObject(id,drone);
                        MCC.This.getLogger().info("Successfully loaded: "+id);
                    }
                });
                MCC.This.toLoads.add(toLoad);
            } else {
                Drone drone = new Drone();
                drone.setAuthorized(new ArrayList<>(yml.getStringList("Users.authorized").stream().map(UUID::fromString).toList()));
                MCC.HANDLER.addObject(id,drone);
            }
        } catch (IOException | InvalidConfigurationException e) {
            e.printStackTrace();
        }
    }
    public static void setID(String from,String to){
        String[] parents = new String[]{"computers","drones"};
        for (String parent : parents){
            File file = new File(MCC.This.getDataFolder(), parent+"/"+from);
            if (!file.exists()) continue;
            file.renameTo(new File(MCC.This.getDataFolder(), parent+"/"+to));
        }
    }
}
