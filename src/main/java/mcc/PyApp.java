package mcc;

import mcc.computer.events.MonitorClickEvent;
import mcc.computer.events.TextInput;
import mcc.computer.view.ScreenView;
import mcc.computer.view.apps.App;

public class PyApp extends App {
    // Ignore
    public void input(TextInput textInput) {

    }
    public void onClick(MonitorClickEvent event) {

    }
    // ^^

    public PyApp(ScreenView view) {
        super(view);
    }

    public void onRender() {}
    public void input(String text){}
    public void onClick(int x,int y){}
    public void onClose(){}
}
