package socketgame.server;

import static commons.ActionMessages.ENEMY_HIT;
import static commons.ActionMessages.ENEMY_MISS;
import static commons.ActionMessages.ENEMY_SUNK;
import static commons.ActionMessages.WO_WIN_MESSAGE;
import static commons.ActionMessages.YOUR_HIT;
import static commons.ActionMessages.YOUR_MISS;
import static commons.ActionMessages.YOUR_SUNK;
import static commons.ActionMessages.YOU_LOSE;
import static commons.ActionMessages.YOU_WIN;
import commons.ComunicationMessages;
import static commons.ComunicationMessages.CHANGE_ID;
import static commons.ComunicationMessages.CLOSE_CONNECTION;
import static commons.ComunicationMessages.GIVE_UP;
import static commons.ComunicationMessages.INIT_GAME;
import static commons.ComunicationMessages.SEND_BOATS;
import static commons.ComunicationMessages.SEND_BOMB;
import static commons.ComunicationMessages.SEND_HIT;
import static commons.ComunicationMessages.SEND_MISS;
import static commons.ComunicationMessages.SEND_SUNK;
import static commons.ComunicationMessages.SEND_WINNER;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import socketgame.client.ClientSideData;


public class ServerSideConnection implements Runnable{
    private ServerSideData cliente;
    private GameServer caller;
    private int currentTurn = 1;
    private String delimiter = "$";
    
    public ServerSideConnection(ServerSideData serverSideData, GameServer caller) {
        this.cliente = serverSideData;
        this.caller = caller;
    }

    @Override
    public void run() {
        String message;
        try {
            cliente.getDataOut().writeInt(cliente.getPlayerData().getId());
            cliente.getDataOut().flush();
            
            while(true) {
                if (this.cliente.getSocket().isConnected() && this.cliente.getDataIn() != null) {
                    message = this.cliente.getDataIn().readUTF();
                    processMessage(message);
                }
            }
        } catch(IOException e) {
            System.out.println("ServerSideConnection IOException from run() method.");
        } catch (Throwable ex) {
            System.out.println("ServerSideConnection Throwable from run() method.");
        }
    } 
    
    public synchronized void messageDispatcher(String message) throws IOException {
        List<ServerSideData> clientes = this.caller.getClientes();
        for (ServerSideData cli : clientes) {
            if (cli.getSocket() != null && cli.getSocket().isConnected() && cli.getDataOut() != null) {
                cli.getDataOut().writeUTF(message);
                cli.getDataOut().flush();
            }
        }
    }
    
    public synchronized void messageDispatcher(int id, String yourMessage, String enemyMessage) throws IOException {
        List<ServerSideData> clientes = this.caller.getClientes();
        for (ServerSideData cli : clientes) {
            if (cli.getSocket() != null && cli.getSocket().isConnected() && cli.getDataOut() != null && cli.getPlayerData().getId() == id) {
                cli.getDataOut().writeUTF(yourMessage);
                cli.getDataOut().flush();
            }
            else if(cli.getSocket() != null && cli.getSocket().isConnected() && cli.getDataOut() != null && cli.getPlayerData().getId() != id) {
                cli.getDataOut().writeUTF(enemyMessage);
                cli.getDataOut().flush();
            }
        }
    }
    
    public synchronized void processMessage(String message) throws IOException, Throwable {
        String[] parts = message.split("\\$");
        switch (parts[0]) {
            case SEND_BOATS:
                int[][] boats = deserializeBoats(parts[1]);
                this.cliente.getPlayerData().setMatriz(boats);
                int[][] matriz = this.cliente.getPlayerData().getMatriz();
                tryStartGame();
                break;
            
            case CLOSE_CONNECTION:
                finalize();
                break;
                
            case SEND_BOMB:
                processAttack(parts[1], parts[2]);
                break;
                
            case GIVE_UP:
                processGiveUp();
                finalize();
                break;
        }
    }
    
    public synchronized void processAttack(String x, String y) throws IOException {
        int row = Integer.parseInt(x);
        int column = Integer.parseInt(y);
        ServerSideData enemyData = getEnemyData();
        int[][] board = enemyData.getPlayerData().getMatriz();
        int callerId = this.cliente.getPlayerData().getId();
        if(board[row][column] != 0) {
            int remain = enemyData.getPlayerData().decrementBoat(board[row][column]);
            if(remain == 0) {
                sendSunk(x, y, callerId);
                if(verifyWinner(enemyData.getPlayerData().getBoatsRemains())) {
                    resetBoard();
                    sendWinner(callerId);
                }
            }
            else sendHit(x, y, callerId);
        }
        else sendMiss(x, y, callerId);
    }
    
    public synchronized void processGiveUp() throws IOException {
        ServerSideData winner = getEnemyData();
        String winnerMessage = SEND_WINNER.concat(delimiter).concat(WO_WIN_MESSAGE);
        
        if (winner.getSocket() != null && winner.getSocket().isConnected() && winner.getDataOut() != null) {
            winner.getDataOut().writeUTF(winnerMessage);
            winner.getDataOut().flush();
        }
    }
    
    public synchronized void sendSunk(String x, String y, int id) {
        switchTurn();
        String yourMessage = SEND_SUNK.concat(delimiter).concat(String.valueOf(id)).concat(delimiter).concat(x).concat(delimiter).concat(y).concat(delimiter).concat(YOUR_SUNK);
        String enemyMessage = SEND_SUNK.concat(delimiter).concat(String.valueOf(id)).concat(delimiter).concat(x).concat(delimiter).concat(y).concat(delimiter).concat(ENEMY_SUNK);
        try {
            messageDispatcher(id, yourMessage, enemyMessage);
        } catch (IOException ex) {
            System.out.println("IOException from send Hit at ServerSideConnection");
        }
    }
    
    public synchronized void sendHit(String x, String y, int id) {
        switchTurn();
        String yourMessage = SEND_HIT.concat(delimiter).concat(String.valueOf(id)).concat(delimiter).concat(x).concat(delimiter).concat(y).concat(delimiter).concat(YOUR_HIT);
        String enemyMessage = SEND_HIT.concat(delimiter).concat(String.valueOf(id)).concat(delimiter).concat(x).concat(delimiter).concat(y).concat(delimiter).concat(ENEMY_HIT);
        try {
            messageDispatcher(id, yourMessage, enemyMessage);
        } catch (IOException ex) {
            System.out.println("IOException from send Hit at ServerSideConnection");
        }
    }
    
    public synchronized void sendMiss(String x, String y, int id) {
        switchTurn();
        String yourMessage = SEND_MISS.concat(delimiter).concat(String.valueOf(id)).concat(delimiter).concat(x).concat(delimiter).concat(y).concat(delimiter).concat(YOUR_MISS);
        String enemyMessage = SEND_MISS.concat(delimiter).concat(String.valueOf(id)).concat(delimiter).concat(x).concat(delimiter).concat(y).concat(delimiter).concat(ENEMY_MISS);
        try {
            messageDispatcher(id, yourMessage, enemyMessage);
        } catch (IOException ex) {
            System.out.println("IOException from send Miss at ServerSideConnection");
        }
    }
    
    public synchronized void sendWinner(int winnerId) {
        String winnerMessage = SEND_WINNER.concat(delimiter).concat(YOU_WIN);
        String loserMessage = SEND_WINNER.concat(delimiter).concat(YOU_LOSE);
        try {
            messageDispatcher(winnerId, winnerMessage, loserMessage);
        } catch (IOException ex) {
            System.out.println("IOException from send Miss at ServerSideConnection");
        }
    }
    
    public void resetBoard() {
        List<ServerSideData> clientes = caller.getClientes();
        for (ServerSideData ssd : clientes) {
            ssd.getPlayerData().resetBoard();
        }
    }
    
    public int[][] deserializeBoats(String serializedMatriz){
        int[][] matriz = new int[10][10];
        String[] values = serializedMatriz.split("\\|");
        if (values.length != 100) {
            throw new IllegalArgumentException("Input string size does not match array size.");
        }
        int index = 0;
        for (int i = 0; i < 10; i++) {
            for (int j = 0; j < 10; j++) {
                matriz[i][j] = Integer.parseInt(values[index]);
                index++;
            }
        }
        return matriz;
    }
    
    public synchronized void tryStartGame() throws IOException {
        if(this.caller.verifyStartGame()) {
            messageDispatcher(INIT_GAME);
        }
    }
    
    public boolean verifyWinner(Map<Integer, Integer> remains) {
        for (int i = 1; i <= 5; i++) {
            if (remains.getOrDefault(i, 0) != 0) {
                return false;
            }
        }
        return true;
    }
    
    public synchronized void sendId(int id) throws IOException {
        String message = CHANGE_ID.concat(delimiter).concat(String.valueOf(id));
        messageDispatcher(message);
    }

    public ServerSideData verifyCaller() {
        List<ServerSideData> clientes = caller.getClientes();
        for (ServerSideData ssd : clientes) {
            if(this.cliente.getSocket() == ssd.getSocket()) {
                return ssd;
            }
        }
        return null;
    }
    
    public ServerSideData getEnemyData() throws IOException {
        List<ServerSideData> clientes = caller.getClientes();
        for (ServerSideData ssd : clientes) {
            if(this.cliente.getSocket() != ssd.getSocket()) {
                return ssd;
            }
        }
        throw new IOException("IOException from getEnemyData() at ServerSideConnection");
    }
    
    public void switchTurn() {
        currentTurn = (currentTurn == 1) ? 2 : 1;
    }
    
    @Override
    protected void finalize() throws Throwable {
        encerrar();
    }

    private void encerrar() {
        this.caller.removerCliente(this.cliente);
    }
}
