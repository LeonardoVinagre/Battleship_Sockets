package frames;

import static commons.ComunicationMessages.SEND_HIT;
import static commons.ComunicationMessages.SEND_MISS;
import static commons.ComunicationMessages.SEND_SUNK;
import static commons.ComunicationMessages.START_TURN;
import static commons.ComunicationMessages.STOP_TURN;
import frames.ClientFrame;
import socketgame.client.ClientSideConnection;
import frames.GridBoats;
import frames.Lobby;


public class FrameController {
    private static FrameController instance;
    private ClientSideConnection connection;
    private Lobby lobby;
    private GridBoats gridBoats;
    private GameBoard gameBoard;
    
    private FrameController(ClientSideConnection connection) {
        this.connection = connection;
        this.lobby = new Lobby(this);
        this.gridBoats = new GridBoats(this);
    }
    
    public static FrameController getInstance(ClientSideConnection connection) {
        if (instance == null) {
            instance = new FrameController(connection);
        }
        return instance;
    }
    
    public void changeEnemyBoard(String action, String x, String y, String logMessage) {
        this.gameBoard.blockTurn();
        this.gameBoard.addLogMessage(logMessage);
        if(action.equals(SEND_MISS)) this.gameBoard.setMissOnEnemy(Integer.parseInt(x), Integer.parseInt(y));
        if(action.equals(SEND_HIT) || action.equals(SEND_SUNK)) this.gameBoard.setHitOnEnemy(Integer.parseInt(x), Integer.parseInt(y));
    }
    
    public void changeMyBoard(String action, String x, String y, String logMessage) {
        this.gameBoard.unblockTurn();
        this.gameBoard.addLogMessage(logMessage);
        if(action.equals(SEND_MISS)) this.gameBoard.setMissOnMine(Integer.parseInt(x), Integer.parseInt(y));
        if(action.equals(SEND_HIT) || action.equals(SEND_SUNK)) this.gameBoard.setHitOnMine(Integer.parseInt(x), Integer.parseInt(y));
    }
    
    public void delegateWinner(String logMessage) {
        this.gameBoard.addLogMessage(logMessage);
        this.gameBoard.showResult(logMessage);
    }
    
    public void notifyLobby(String message) {
        lobby.notifyFrame(message);
        startGame();
    }
    
    public void newGame() {
        this.gridBoats = new GridBoats(this);
        this.gridBoats.setVisible(true);
    }
    
    public void startGame() {
        this.gameBoard = new GameBoard(this); 
        this.gameBoard.setVisible(true);
        
    }

    public Lobby getLobby() {
        return lobby;
    }

    public GridBoats getGridBoats() {
        return gridBoats;
    }

    public ClientSideConnection getConnection() {
        return connection;
    }
}
