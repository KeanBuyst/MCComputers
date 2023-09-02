package mcc.computer.view.apps;

import mcc.MCC;
import mcc.computer.events.MonitorClickEvent;
import mcc.computer.events.TextInput;
import mcc.computer.files.PyFile;
import mcc.computer.view.ScreenView;
import org.bukkit.ChatColor;

import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.util.List;

public class Terminal extends App {

    private int position;

    public Terminal(ScreenView view) {
        super(view);
    }

    public void input(TextInput text) {
        String[] args = text.getArgs();
        String response = null;
        String cid = text.player().getMetadata("cc_linked").get(0).asString();
        String s1 = args.length > 0 ? args[0] : null;
        String s2 = args.length > 1 ? args[1] : null;
        String s3 = args.length > 2 ? args[2] : null;
        if (equals(s1,"clear")){
            view.fill(Color.BLACK);
            position = 0;
        }
        if (equals(s1, "end")) {
            text.player().removeMetadata("cc_linked", MCC.This);
            response = text.player().getName() + " disconnected";
            text.player().sendMessage(ChatColor.RED + "Connection closed");
        }
        if (equals(s1,"ls","dir")){
            File parent = new File(MCC.This.getDataFolder(),"computers/"+cid);
            StringBuilder builder = new StringBuilder();
            for (String file : parent.list((f,name) -> !name.endsWith(".yml")))
                builder.append(file).append('\n');
            response = builder.toString();
        }
        if (equals(s1, "file","f")){
            if (equals(s2,"create")){
                if (s3 != null){
                    File file = new File(MCC.This.getDataFolder(),"computers/"+cid+"/"+s3);
                    if (!file.exists()) {
                        try {
                            //noinspection ResultOfMethodCallIgnored
                            file.createNewFile();
                            response = "File successfully created";
                        } catch (IOException ignored) {}
                    } else {
                        response = "File already exists";
                    }
                }
            }
        }
        if (equals(s1,"pastebin","pb")){
            if (s2 != null && s3 != null){
                new PyFile(text.player(), cid,s3).clonePastebin(s2);
                response = s3+" has been written to";
            }
        }
        if (equals(s1,"python","py")){
            if (s2 != null){
                new PyFile(text.player(), cid,s2).execute();
            }
        }
        write("> " + text.text());
        if (response != null) write(response);
    }
    public void onClick(MonitorClickEvent event) {}

    public void onRender() {}

    public void write(String text){
        List<String> lines = splice(text);
        for (String l : lines){
            view.drawText(l,0, position);
            position += 10;
            if (position >= view.getHeight()-10){
                view.shiftUp(10);
                position -= 10;
            }
        }
    }

    private boolean equals(String cons,String... tests){
        if (cons == null) return false;
        for (String test : tests){
            if (cons.equalsIgnoreCase(test)) return true;
        }
        return false;
    }
}
