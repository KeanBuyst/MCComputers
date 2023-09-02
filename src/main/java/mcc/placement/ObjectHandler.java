package mcc.placement;

import mcc.MCC;
import mcc.computer.objects.Object;
import mcc.storage.Storage;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.metadata.Metadatable;

import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class ObjectHandler {
    private final HashMap<String,Object> objects = new HashMap<>();
    public void addObject(String id,Object object){
        objects.put(id,object);
    }
    public Object getObject(String id){
        return objects.get(id);
    }
    public boolean contains(String id){
        return objects.containsKey(id);
    }
    @SuppressWarnings("ResultOfMethodCallIgnored")
    public void removeObject(String id){
        objects.remove(id);
        File file = new File(MCC.This.getDataFolder(), "computers/"+id);
        if (file.exists()){
            file.delete();
        } else {
            file = new File(MCC.This.getDataFolder(), "drones/"+id);
            file.delete();
        }
    }
    public boolean rename(String from,String to){
        if (objects.containsKey(to)) return false;
        Object object = objects.remove(from);
        if (object == null) return false;
        Metadatable meta = object.getResult();
        if (meta != null){
            meta.removeMetadata("ID",MCC.This);
            meta.setMetadata("ID",new FixedMetadataValue(MCC.This,to));
        }
        objects.put(to,object);
        Storage.setID(from,to);
        return true;
    }
    public String id(Object object){
        for (Map.Entry<String, Object> entry : objects.entrySet()){
            if (entry.getValue() == object) return entry.getKey();
        }
        return "";
    }
    public HashMap<String,Object> getObjects(){
        return objects;
    }
    public Set<String> getIds(){
        return objects.keySet();
    }
    public String print(){
        StringBuilder builder = new StringBuilder("HANDLER OBJECTS");
        objects.forEach((s, object) -> builder.append("\n").append(s));
        return builder.toString();
    }
}
