package mcc.computer.inventory;

import mcc.MCC;
import mcc.computer.objects.Computer;
import mcc.computer.objects.Monitor;
import mcc.computer.objects.controlled.Drone;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ShapedRecipe;

public class Crafting {
    public Crafting(){
        r1();
        //r2();
        r3();
    }
    public void r1(){
        NamespacedKey key = new NamespacedKey(MCC.This,"Computer");
        ShapedRecipe recipe = new ShapedRecipe(key, Computer.ITEM);
        recipe.shape("III","ISI","III");
        recipe.setIngredient('I', Material.IRON_INGOT);
        recipe.setIngredient('S',Material.NETHER_STAR);
        Bukkit.addRecipe(recipe);
    }
    public void r2(){
        NamespacedKey key = new NamespacedKey(MCC.This,"Monitor");
        ShapedRecipe recipe = new ShapedRecipe(key, Monitor.ITEM);
        recipe.shape("NNN","NPN","NNN");
        recipe.setIngredient('N',Material.IRON_NUGGET);
        recipe.setIngredient('P',Material.GLASS_PANE);
        Bukkit.addRecipe(recipe);
    }
    public void r3(){
        NamespacedKey key = new NamespacedKey(MCC.This,"Drone");
        ShapedRecipe recipe = new ShapedRecipe(key, Drone.ITEM);
        recipe.shape("IGI","RCR","IGI");
        recipe.setIngredient('I',Material.IRON_TRAPDOOR);
        recipe.setIngredient('C',Material.ENDER_EYE);
        recipe.setIngredient('G',Material.GLOWSTONE_DUST);
        recipe.setIngredient('R',Material.REDSTONE);
        Bukkit.addRecipe(recipe);
    }
}
