package socketgame.client;

import commons.ComunicationMessages;
import static commons.ComunicationMessages.CLOSE_CONNECTION;
import static commons.ComunicationMessages.GIVE_UP;
import static commons.ComunicationMessages.SEND_BOATS;
import static commons.ComunicationMessages.SEND_BOMB;
import frames.FrameController;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.logging.Level;
import java.util.logging.Logger;
import socketgame.server.ConnectionInfo;



public class ClientSideConnection {
    private FrameController frameController;
    private ClientHandler clientHandler;
    private ClientSideData clientSideData;
    private Socket socket;
    private DataInputStream dataIn;
    private DataOutputStream dataOut;
    private int currentTurn = 1;
    String delimiter = "$";
    
    public ClientSideConnection(ClientSideData clientSideData, ConnectionInfo connectionInfo) throws IOException {
        this.clientSideData = clientSideData;
        System.out.println("-----Client-----");
        try {
            socket = new Socket(connectionInfo.getAddres(), connectionInfo.getPort());
            dataIn = new DataInputStream(socket.getInputStream());
            dataOut = new DataOutputStream(socket.getOutputStream());
            this.clientHandler = new ClientHandler(socket, this, dataIn);
            this.clientSideData.setPlayerId(dataIn.readInt());
            Thread thread = new Thread(clientHandler);
            thread.start();
            System.out.println("Connected to server as player #" + clientSideData.getPlayerId() + ".");
            
            
        } catch(IOException e) {
            System.out.println("IOException at constructor from ClientSideConnection");
            throw e;
        }
    }

    public ClientSideData getClientSideData() {
        return clientSideData;
    }

    public void setClientSideData(ClientSideData clientSideData) {
        this.clientSideData = clientSideData;
    }
    
    public void setBoats(int[][] boats) {
        this.clientSideData.setMatriz(boats);
    }

    public FrameController getFrameController() {
        return frameController;
    }

    public void setFrameController(FrameController frameController) {
        if(this.frameController == null) {
            this.frameController = frameController;
        }
    }
    
    
    private void sendMessage(String message) {
        try {
            dataOut.writeUTF(message);
            dataOut.flush();
        } catch (IOException ex) {
            Logger.getLogger(ClientSideConnection.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    public void createMessage(String identity, String message) {
        String messageToSend;
        if(message != null) {
            messageToSend = identity.concat(delimiter).concat(message);
        }else{
            messageToSend = identity.concat(delimiter);
        }
        sendMessage(messageToSend);
    }
    
    public void sendDisconnectMessage() throws Throwable {
        createMessage(CLOSE_CONNECTION, null);
        finalize();
    }
    
    public void sendGiveUp() throws Throwable {
        createMessage(GIVE_UP, null);
        finalize();
    }
    
    @Override
    protected void finalize() throws Throwable {
        encerrar();
    }
    
    private void encerrar() {
        try {
            this.dataIn.close();
            this.dataOut.close();
            this.socket.close();
            System.exit(0);
        } catch (IOException ex) {
            Logger.getLogger(ClientSideConnection.class.getName()).log(Level.SEVERE, null, ex);
        } 
    }
    
    public void setBoatsMessage(int[][] matriz){
        StringBuilder serializedMatrix = new StringBuilder();
        for (int[] row : matriz) {
            for (int num : row) {
                serializedMatrix.append(num).append("|");
            }
        }
        if (serializedMatrix.length() > 0) {
            serializedMatrix.setLength(serializedMatrix.length() - 1);
        }
        createMessage(SEND_BOATS, serializedMatrix.toString()); 
    }
    
    public void createBombOrder(int zOrder) {
        int x,y;
        if(zOrder >= 0 && zOrder <= 9) {
            x = 0;
            y = zOrder;
        }else {
            x = zOrder / 10;
            y = zOrder % 10;
        }
        String order = String.valueOf(x).concat(delimiter).concat(String.valueOf(y));
        createMessage(SEND_BOMB, order);
    }
    
    
    public void playerAction(String action, String id, String x, String y, String logMessage) {
        if(this.clientSideData.getPlayerId() == Integer.parseInt(id)) {
            this.frameController.changeEnemyBoard(action, x, y, logMessage);
        }else this.frameController.changeMyBoard(action, x, y, logMessage);
    }
    
    public void delegateWinner(String logMessage) {
        this.frameController.delegateWinner(logMessage);
    }
}
