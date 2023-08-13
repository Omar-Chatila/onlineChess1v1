package chessModel;

import com.example.messengerserver.Client;
import com.example.messengerserver.Server;

public class ApplicationData {
    private static ApplicationData instance = new ApplicationData();
    private String sharedData;
    private Client client;
    private Server server;

    private ApplicationData() {

    }

    public static ApplicationData getInstance() {
        return instance;
    }

    public String getSharedData() {
        return sharedData;
    }

    public void setSharedData(String sharedData) {
        this.sharedData = sharedData;
    }

    public Client getClient() {
        return client;
    }

    public void setClient(Client client) {
        this.client = client;
    }

    public Server getServer() {
        return server;
    }

    public void setServer(Server server) {
        this.server = server;
    }
}
