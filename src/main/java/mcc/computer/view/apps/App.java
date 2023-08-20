package mcc.computer.view.apps;

import mcc.computer.events.MonitorClickEvent;
import mcc.computer.events.TextInput;
import mcc.computer.view.ScreenView;
import org.bukkit.map.MinecraftFont;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public abstract class App {
    public final ScreenView view;
    public abstract void input(TextInput textInput);
    public abstract void onClick(MonitorClickEvent event);
    public abstract void onRender();

    public App(ScreenView view){
        this.view = view;
        view.fill(Color.BLACK);
    }

    protected List<String> splice(String text){
        List<String> list = new ArrayList<>();
        text.lines().forEach(new Consumer<String>() {
            public void accept(String s) {
                while (MinecraftFont.Font.getWidth(s) > view.getWidth()){
                    // Break down string
                    int estimate = view.getWidth()/5; // higher side so no upwards notation has to take place
                    if (estimate > s.length()) estimate = s.length();
                    String sub = s.substring(0,estimate);
                    boolean neg = MinecraftFont.Font.getWidth(sub) > view.getWidth();
                    if (neg){
                        while (MinecraftFont.Font.getWidth(sub) > view.getWidth()){
                            estimate--;
                            sub = s.substring(0,estimate);
                        }
                    } else {
                        while (MinecraftFont.Font.getWidth(sub) < view.getWidth()){
                            estimate++;
                            sub = s.substring(0,estimate);
                        }
                        if (MinecraftFont.Font.getWidth(sub) > view.getWidth()){
                            estimate--;
                            sub = s.substring(0,estimate);
                        }
                    }
                    list.add(sub);
                    s = s.substring(estimate);
                }
                list.add(s);
            }
        });
        return list;
    }
}
