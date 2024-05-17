package socketgame.server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class GameServer extends Thread {
    private ServerSocket serverSocket;
    private int numPlayers;
    List<ServerSideData> clientes;
    private ServerSideConnection player1;
    private ServerSideConnection player2;
    
    public GameServer(int porta) throws IOException {
        System.out.println("-----Game server-----");
        this.numPlayers = 0;
        this.clientes = new ArrayList<>();
        this.serverSocket = new ServerSocket(porta);
        System.out.println(this.getClass().getSimpleName() + " running at port: " + serverSocket.getLocalPort());
    }

    @Override
    public void run() {
        Socket socket;
        System.out.println("Waiting for connections...");
        while (true) {
            try {
                while(numPlayers < 2) {
                    socket = serverSocket.accept();
                    numPlayers++;
                    ServerSideData serverSideData = new ServerSideData(socket, numPlayers);
                    novoCliente(serverSideData);
                    System.out.println("Player #" + numPlayers + " has connected.");
                    ServerSideConnection ssc = new ServerSideConnection(serverSideData, this);
                    if(numPlayers == 1) {
                        player1 = ssc;
                    }
                    else player2 = ssc;
                    Thread thread = new Thread(ssc);
                    thread.start();
                    if(numPlayers == 2) System.out.println("No longer accepting connections.");
                }
            }catch(IOException e) {
                e.printStackTrace();
            }
        } 
    }
    
    public synchronized void novoCliente(ServerSideData cliente) throws IOException {
        clientes.add(cliente);
    }

    public synchronized void removerCliente(ServerSideData cliente) {
        clientes.remove(cliente);
        try {
            cliente.getDataIn().close();
            cliente.getDataOut().close();
            cliente.getSocket().close();
        } catch (IOException ex) {
            System.out.println(ex.getMessage());
        }
        numPlayers--;
        if(clientes.size() == 1) {
            if(clientes.get(0).getPlayerData().getId() == 2) {
                player1 = player2;
                player2 = null;
                clientes.get(0).getPlayerData().setId(numPlayers);
                try {
                    player1.sendId(numPlayers);
                } catch (IOException ex) {
                    Logger.getLogger(GameServer.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }
    }
    
    public boolean verifyStartGame() {
        if(clientes.size() == 2) {
            for(ServerSideData cliente : clientes) {
                if(cliente.getPlayerData().getMatriz() == null) return false;
            }
            return true;
        }
        return false;
    }

    public List<ServerSideData> getClientes() {
        return clientes;
    }
}
