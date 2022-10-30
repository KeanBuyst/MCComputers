package mcc.commands;

import mcc.MCC;
import mcc.computer.objects.Authentication;
import mcc.computer.objects.Computer;
import mcc.computer.objects.Object;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class TComputer implements TabCompleter {
    public List<String> onTabComplete(CommandSender sender, Command command, String label, String[] args) {
        List<String> list = new ArrayList<>();
        if (sender instanceof Player player){
            switch (args.length){
                case 0 -> {return null;}
                case 1 -> {
                    list.add("auth");
                    list.add("link");
                    list.add("rename");
                }
                case 2 -> {
                    if (args[0].equalsIgnoreCase("link")){
                        for (Map.Entry<String, Object> entry : MCC.HANDLER.getObjects().entrySet()){
                            if (entry.getValue() instanceof Computer computer){
                                if (computer.isPlaced() && computer.authorized(player.getUniqueId())){
                                    list.add(entry.getKey());
                                }
                            }
                        }
                    }else if (args[0].equalsIgnoreCase("rename")){
                        for (Map.Entry<String,Object> entry : MCC.HANDLER.getObjects().entrySet()){
                            if (entry.getValue() instanceof Authentication authentication){
                                if (entry.getValue().isPlaced() && authentication.authorized(player.getUniqueId())){
                                    list.add(entry.getKey());
                                }
                            }
                        }
                    } else if (args[0].equalsIgnoreCase("auth")){
                        for (Map.Entry<String,Object> entry : MCC.HANDLER.getObjects().entrySet()){
                            if (entry.getValue() instanceof Authentication){
                                list.add(entry.getKey());
                            }
                        }
                    }
                }
                case 3 -> {
                    if (args[0].equalsIgnoreCase("auth")){
                        for (Player p : Bukkit.getOnlinePlayers()){
                            list.add(p.getName());
                        }
                    }
                }
            }
        }
        return list;
    }
}
