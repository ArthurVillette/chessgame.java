package com.ChessGame.Vue;
import javax.swing.JPanel;
import com.ChessGame.Model.Board;
import java.awt.Graphics;
import java.awt.Dimension;
import java.awt.Color;

public class BoardPanel extends JPanel {
    private Board board;
    public static final int TILE_SIZE = 80;
    
    private int selectedX = -1;
    private int selectedY = -1;

    /**
     * Constructeur du panel de l'échiquier
     * @param board Le modèle du plateau d'échecs
     */
    public BoardPanel(Board board) {
        this.board = board;
        setPreferredSize(new Dimension(8 * TILE_SIZE, 8 * TILE_SIZE));
    }

    /**
     * Met à jour la sélection de la case cliquée
     * @param x les coordonnées x de la case sélectionnée
     * @param y les coordonnées y de la case sélectionnée
     */
    public void setSelection(int x, int y) {
        this.selectedX = x;
        this.selectedY = y;
        this.repaint();
    }

    /**
     * Dessine le plateau d'échecs et la sélection
     * @param g le graph pour dessiner le plateau et la sélection
     */
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        drawGrid(g);
        drawSelection(g);
    }

    /**
     * Dessine la grille du plateau d'échecs
     * @param g le graph pour dessiner la grille
     */
    private void drawGrid(Graphics g) {
        for (int row = 0; row < 8; row++) {
            for (int col = 0; col < 8; col++) {
                if ((row + col) % 2 == 0) g.setColor(new Color(235, 235, 208));
                else g.setColor(new Color(119, 148, 85));
                g.fillRect(col * TILE_SIZE, row * TILE_SIZE, TILE_SIZE, TILE_SIZE);
            }
        }
    }

    /**
     * Dessine un carré rouge semi-transparent sur la case sélectionnée
     * @param g le graph pour dessiner la sélection
     */
    private void drawSelection(Graphics g) {
        if (selectedX != -1 && selectedY != -1) {
            g.setColor(new Color(255, 0, 0, 150));
            g.fillRect(selectedX * TILE_SIZE, selectedY * TILE_SIZE, TILE_SIZE, TILE_SIZE);
        }
    }
}
