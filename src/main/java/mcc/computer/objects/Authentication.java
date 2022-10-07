package mcc.computer.objects;

import java.util.ArrayList;
import java.util.UUID;

public interface Authentication {
    ArrayList<UUID> getAuthorized();
    void authorize(UUID uuid);
    void unauthorize(UUID uuid);
    boolean authorized(UUID uuid);
    void setAuthorized(ArrayList<UUID> uuids);
}
