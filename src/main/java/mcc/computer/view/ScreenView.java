package mcc.computer.view;

import mcc.MCC;
import mcc.PyApp;
import mcc.computer.events.MonitorClickEvent;
import mcc.computer.events.TextInput;
import mcc.computer.view.apps.App;
import mcc.computer.view.apps.Terminal;
import org.bukkit.Bukkit;
import org.bukkit.map.MapCanvas;
import org.python.core.PyException;

import java.awt.*;
import java.util.ArrayList;

public class ScreenView extends View {

    private App currentApp = new Terminal(this);
    public ScreenView(MapCanvas canvas) {
        super(canvas);
        Color colour = Color.GRAY;
        for (int d = 0;d < 2;d++){
            for (int x = d; x <= width; x++){
                setPixel(x,d,colour);
            }
            for (int y = 1 + d; y <= height;y++){
                setPixel(d,y,colour);
            }
            for (int x = 1 + d; x <= width;x++){
                setPixel(x,height - d,colour);
            }
            for (int y = 1 + d; y <= height - 1;y++){
                setPixel(width - d,y,colour);
            }
        }
        width -= 4;
        height -= 4;
        offsetY = 2;
        offsetX = 2;
        fill(Color.BLACK);
    }
    public void input(TextInput textInput){
        if (currentApp instanceof PyApp app){
            if (textInput.text().equals("end")){
                app.onClose();
                currentApp = new Terminal(this);
            } else {
                Bukkit.getScheduler().runTaskAsynchronously(MCC.This, new Runnable() {
                    public void run() {
                        try {
                            app.input(textInput.text());
                        } catch (PyException e){
                            Terminal terminal = new Terminal(ScreenView.this);
                            setApp(terminal);
                            terminal.write(e.getMessage());
                        }
                    }
                });
            }
        } else {
            currentApp.input(textInput);
        }
    }
    public void input(MonitorClickEvent event){
        if (currentApp instanceof PyApp app){
            Bukkit.getScheduler().runTaskAsynchronously(MCC.This, new Runnable() {
                public void run() {
                    try {
                        app.onClick(event.getX(),event.getY());
                    } catch (PyException e){
                        Terminal terminal = new Terminal(ScreenView.this);
                        setApp(terminal);
                        terminal.write(e.getMessage());
                    }
                }
            });
        } else {
            currentApp.onClick(event);
        }
    }
    public void onRender(){
        Bukkit.getScheduler().runTaskAsynchronously(MCC.This, new Runnable() {
            public void run() {
                try {
                    currentApp.onRender();
                } catch (PyException e){
                    Terminal terminal = new Terminal(ScreenView.this);
                    setApp(terminal);
                    terminal.write(e.getMessage());
                }
            }
        });
    }
    public void setApp(App app){
        this.currentApp = app;
    }
    public App getApp() {
        return currentApp;
    }

    public void shiftUp(int amount){
        ArrayList<Color> bytes = new ArrayList<>();
        for (int y = amount;y <= height;y++){
            for (int x = 0;x <= width;x++){
                bytes.add(getPixel(x,y));
            }
        }
        fill(Color.black);
        int index = 0;
        for (int y = 0;y <= height-amount;y++){
            for (int x = 0;x <= width;x++){
                setPixel(x,y,bytes.get(index));
                index++;
            }
        }
    }
}
