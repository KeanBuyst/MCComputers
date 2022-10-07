package mcc.computer.events;

import org.bukkit.entity.Player;

public record TextInput(String text, Player player) {
    public String[] getArgs(){
        return text.split(" ");
    }
}