package socketgame.client;

import static commons.ComunicationMessages.CHANGE_ID;
import static commons.ComunicationMessages.INIT_GAME;
import static commons.ComunicationMessages.SEND_HIT;
import static commons.ComunicationMessages.SEND_MISS;
import static commons.ComunicationMessages.SEND_SUNK;
import static commons.ComunicationMessages.SEND_WINNER;
import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.util.logging.Level;
import java.util.logging.Logger;


public class ClientHandler implements Runnable{
    private Socket socket;
    private ClientSideConnection caller;
    private DataInputStream dataIn;

    public ClientHandler(Socket socket, ClientSideConnection caller, DataInputStream dataIn) throws IOException {
        this.socket = socket;
        this.caller = caller;
        this.dataIn = dataIn;
    }

    @Override
    public void run() {
        try {
            String message;
            while(true) {
                if (this.socket.isConnected() && this.dataIn != null) {
                    message = this.dataIn.readUTF();
                    processMessage(message);
                }
            }
            
        } catch (IOException ex) {
            System.out.println("ClientHandler IOException from run() method.");
        }

    }
    
    public synchronized void processMessage(String message){
        String[] parts = message.split("\\$");
        switch (parts[0]) {
            case INIT_GAME:
                System.out.println("Jogo:" + caller.getClientSideData().getPlayerId() + " sera iniciado");
                this.caller.getFrameController().notifyLobby(INIT_GAME);
                break;
            
            case CHANGE_ID:
                int id = Integer.parseInt(parts[1]);
                this.caller.getClientSideData().setPlayerId(id);
                System.out.println("Your id now is: " + id + ".");
                break;
                
            case SEND_MISS:
                this.caller.playerAction(parts[0], parts[1], parts[2], parts[3], parts[4]);
                break;
                
            case SEND_HIT:
                this.caller.playerAction(parts[0],parts[1], parts[2], parts[3], parts[4]);
                break;
                
            case SEND_SUNK:
                this.caller.playerAction(parts[0],parts[1], parts[2], parts[3], parts[4]);
                break;
                
            case SEND_WINNER:
                this.caller.delegateWinner(parts[1]);
                break;
        }
    }
}
