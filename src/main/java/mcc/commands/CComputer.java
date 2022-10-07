package mcc.commands;

import mcc.MCC;
import mcc.computer.objects.Authentication;
import mcc.computer.objects.Computer;
import mcc.computer.objects.Object;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.metadata.FixedMetadataValue;

public class CComputer implements CommandExecutor {
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        boolean passed = true;
        if (sender instanceof Player player){
            switch (args.length){
                case 2 -> {
                    String id = args[1];
                    Object object = MCC.HANDLER.getObject(id);
                    if (object instanceof Computer computer){
                        if (computer.isPlaced() && computer.authorized(player.getUniqueId()) && args[0].equalsIgnoreCase("link")){
                            player.setMetadata("cc_linked",new FixedMetadataValue(MCC.This,id));
                            player.sendMessage(ChatColor.GOLD+"Connected to "+id);
                        }
                    }
                }
                case 3 -> {
                    String id = args[1];
                    String arg = args[2];
                    Object object = MCC.HANDLER.getObject(id);
                    if (object instanceof Authentication authentication){
                        if (object.isPlaced() && authentication.authorized(player.getUniqueId()) && args[0].equalsIgnoreCase("rename")){
                            MCC.HANDLER.rename(id,arg);
                            player.sendMessage(ChatColor.GOLD+"Successfully renamed "+id+" to "+arg);
                        }
                        if (args[0].equalsIgnoreCase("auth")){
                            Player p = Bukkit.getPlayer(arg);
                            if (p == null) {
                                player.sendMessage("No player found by the name "+arg);
                            } else {
                                authentication.authorize(p.getUniqueId());
                            }
                        }
                    }
                }
                default -> passed = false;
            }
        }
        return passed;
    }
}
