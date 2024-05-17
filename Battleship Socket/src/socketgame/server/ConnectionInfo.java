package socketgame.server;

public class ConnectionInfo {
    private String addres;
    private String port;
    
    public ConnectionInfo(String addres, String port) {
        this.addres = addres;
        this.port = port;
    }

    public String getAddres() {
        return addres;
    }

    public void setAddres(String addres) {
        this.addres = addres;
    }

    public Integer getPort() {
        return Integer.parseInt(port);
    }

    public void setPort(String port) {
        this.port = port;
    }
}
