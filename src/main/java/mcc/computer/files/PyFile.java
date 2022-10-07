package mcc.computer.files;

import mcc.MCC;
import mcc.PyApp;
import mcc.computer.events.TextInput;
import mcc.computer.objects.Computer;
import mcc.computer.view.ScreenView;
import mcc.computer.view.apps.App;
import mcc.computer.view.apps.Terminal;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.python.core.*;
import org.python.util.PythonInterpreter;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class PyFile extends File {

    private static final Loader LOADER = new Loader();
    private final String id;
    private final Player executor;

    public PyFile(Player executor, String cid, String name){
        super(MCC.This.getDataFolder(),"computers/"+cid+"/"+name);
        id = cid;
        this.executor = executor;
    }

    public void clonePastebin(String key){
        try {
            URL url = new URL("https://pastebin.com/raw/"+key);
            String text = new String(url.openStream().readAllBytes());
            FileWriter writer = new FileWriter(this);
            writer.write(text);
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void execute(){
        Bukkit.getScheduler().runTaskAsynchronously(MCC.This, new Runnable() {
            public void run() {
                Computer computer = (Computer) MCC.HANDLER.getObject(id);
                try {
                    PythonInterpreter interpreter = new PythonInterpreter();
                    interpreter.getSystemState().setClassLoader(LOADER);
                    Stdout out = new Stdout(computer.getView().getApp(),executor);
                    interpreter.getSystemState().stdout = out;
                    interpreter.set("computer", MCC.HANDLER.getObject(id));
                    interpreter.execfile(PyFile.this.getPath());
                    PyObject clazz = interpreter.get("App");
                    if (clazz != null){
                        // App mode
                        PyApp app = (PyApp) clazz.__call__(Py.java2py(computer.getView())).__tojava__(PyApp.class);
                        computer.getView().setApp(app);
                        out.setApp(app);
                    }
                } catch (Exception e){
                    ScreenView view = computer.getView();
                    if (view.getApp() instanceof Terminal terminal){
                        terminal.write(e.getMessage());
                    } else {
                        Terminal terminal = new Terminal(view);
                        view.setApp(terminal);
                        terminal.write(e.getMessage());
                    }
                }
            }
        });
    }
    private static class Loader extends ClassLoader {
        private final ArrayList<String> permit = new ArrayList<>(List.of("mcc.PyApp","mcc.Controller"));
        private boolean allowAll;

        @Override
        protected Class<?> loadClass(String name, boolean resolve) throws ClassNotFoundException {
            if (allowAll || permit.contains(name)){
                Class<?> clazz = findLoadedClass(name);
                if (clazz == null) clazz = Class.forName(name);
                return clazz;
            }
            return null;
        }
        public void updatePermit(){
            allowAll = MCC.This.getConfig().getBoolean("permit.all");
            permit.addAll(MCC.This.getConfig().getStringList("permit.list"));
        }
    }
    public static class Stdout extends PySystemState {
        private App app;
        private final Player player;
        public Stdout(App app,Player player){
            this.app = app;
            this.player = player;
        }
        public void write(String text){
            if (text.trim().isEmpty()) return;
            if (app instanceof Terminal terminal){
                terminal.write(text);
            } else {
                app.input(new TextInput(text,player));
            }
        }
        public void setApp(App app) {
            this.app = app;
        }
    }

    public static void config(){
        LOADER.updatePermit();
    }
}
