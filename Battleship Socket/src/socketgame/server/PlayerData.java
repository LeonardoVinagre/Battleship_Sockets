/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package socketgame.server;

import java.awt.Color;
import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author leona
 */
public class PlayerData {
    private int id;
    private int[][] matriz;
    private Map<Integer, Integer> boatsRemains = new HashMap<>();
    
    public PlayerData(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int[][] getMatriz() {
        return matriz;
    }

    public void setMatriz(int[][] matriz) {
        this.matriz = matriz;
        setBoatsRemains();
    }
    
    public void setBoatsRemains() {
        for(int i = 0 ; i < matriz.length ; i++) {
            for(int j = 0 ; j < matriz[0].length ; j++) {
                int valor = matriz[i][j];
                if(valor >= 1 && valor <= 5) {
                    int count = boatsRemains.getOrDefault(valor, 0);
                    boatsRemains.put(valor, count + 1);
                }
            }
        }
    }
    
    public void resetBoard() {
        this.matriz = null;
        this.boatsRemains.clear();
    }

    public int decrementBoat(int tipo) {
        int valor = this.boatsRemains.get(tipo);
        valor--;
        this.boatsRemains.put(tipo, valor);
        return valor;
    }
    
    public Map<Integer, Integer> getBoatsRemains() {
        return this.boatsRemains;
    }
}