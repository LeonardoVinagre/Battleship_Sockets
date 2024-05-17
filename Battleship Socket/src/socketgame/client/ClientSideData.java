
package socketgame.client;


public class ClientSideData {
    private int playerId;
    private int[][] myBoats;

    public int getPlayerId() {
        return playerId;
    }

    public void setPlayerId(int playerId) {
        this.playerId = playerId;
    }

    public int[][] getMatriz() {
        return myBoats;
    }

    public void setMatriz(int[][] matriz) {
        this.myBoats = matriz;
    }
    
    
}
