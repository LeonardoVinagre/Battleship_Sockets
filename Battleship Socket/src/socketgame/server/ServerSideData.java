/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package socketgame.server;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.List;

/**
 *
 * @author leona
 */
public class ServerSideData {
    private Socket socket;
    private DataInputStream dataIn;
    private DataOutputStream dataOut;
    private PlayerData playerData;

    
    public ServerSideData(Socket socket, int id) {
        this.playerData = new PlayerData(id);
        this.socket = socket;

        try {
            dataIn = new DataInputStream(socket.getInputStream());
            dataOut = new DataOutputStream(socket.getOutputStream());
        } catch (IOException ex) {
            System.out.println(ex.getMessage());
        }
    }

    public DataInputStream getDataIn() {
        return dataIn;
    }

    public DataOutputStream getDataOut() {
        return dataOut;
    }

    public Socket getSocket() {
        return socket;
    }

    public PlayerData getPlayerData() {
        return playerData;
    }
}
