package com.ChessGame.Vue;

import javax.swing.*;

import com.ChessGame.Model.Board;

import java.awt.*;

import com.ChessGame.Model.Piece;
import javax.imageio.ImageIO;
import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.List;

/**
 * Classe représentant la vue du plateau d'échecs
 */
public class BoardPanel extends JPanel implements Observer {
    private Board board;
    public static final int TILE_SIZE = 80;

    private int selectedX = -1;
    private int selectedY = -1;
    private Map<String, Image> imageCache = new HashMap<>();

    private List<Point> casesPossibles = new ArrayList<>();

    /**
     * Constructeur de la vue du plateau d'échecs
     * 
     * @param board Le modèle du plateau d'échecs
     */
    public BoardPanel(Board board) {
        this.board = board;
        setPreferredSize(new Dimension(8 * TILE_SIZE, 8 * TILE_SIZE));
        loadAllImages();
    }

    /**
     * Met à jour la sélection de la case cliquée
     * 
     * @param x les coordonnées x de la case sélectionnée
     * @param y les coordonnées y de la case sélectionnée
     */
    public void setSelection(int x, int y) {
        this.selectedX = x;
        this.selectedY = y;
        this.repaint();
    }

    /**
     * Met à jour la vue du plateau d'échecs lorsque le modèle change
     * 
     * @param o   l'objet observable (Partie) qui a changé
     * @param arg un argument optionnel (non utilisé ici)
     */
    @Override
    public void update(Observable o, Object arg) {
        SwingUtilities.invokeLater(() -> repaint());
    }

    /**
     * Met à jour la liste des cases possibles pour le déplacement de la pièce
     * sélectionnée
     * 
     * @param cases la liste des cases possibles pour le déplacement de la pièce
     *              sélectionnée
     */
    public void setCasesPossibles(List<Point> cases) {
        this.casesPossibles = cases;
        repaint();
    }

    /**
     * Dessine les cases possibles pour le déplacement de la pièce sélectionnée
     * 
     * @param g le graph pour dessiner les cases possibles
     */
    private void drawCasesPossibles(Graphics g) {
        g.setColor(new Color(0, 0, 255, 150));
        for (Point p : casesPossibles) {
            g.fillRect(p.x * TILE_SIZE, p.y * TILE_SIZE, TILE_SIZE, TILE_SIZE);
        }
    }

    /**
     * Dessine le plateau d'échecs et la sélection
     * 
     * @param g le graph pour dessiner le plateau et la sélection
     */
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        drawGrid(g);
        drawSelection(g);
        drawCasesPossibles(g);
        drawPieces(g);
    }

    /**
     * Dessine la grille du plateau d'échecs
     * 
     * @param g le graph pour dessiner la grille
     */
    private void drawGrid(Graphics g) {
        for (int row = 0; row < 8; row++) {
            for (int col = 0; col < 8; col++) {
                if ((row + col) % 2 == 0)
                    g.setColor(new Color(235, 235, 208));
                else
                    g.setColor(new Color(119, 148, 85));
                g.fillRect(col * TILE_SIZE, row * TILE_SIZE, TILE_SIZE, TILE_SIZE);
            }
        }
    }

    /**
     * Dessine un carré rouge semi-transparent sur la case sélectionnée
     * 
     * @param g le graph pour dessiner la sélection
     */
    private void drawSelection(Graphics g) {
        if (selectedX != -1 && selectedY != -1) {
            g.setColor(new Color(255, 0, 0, 150));
            g.fillRect(selectedX * TILE_SIZE, selectedY * TILE_SIZE, TILE_SIZE, TILE_SIZE);
        }
    }

    /**
     * Charge toutes les images des pièces d'échecs dans le cache
     */
    private void loadAllImages() {
        String[] colors = { "w", "b" };
        String[] pieces = { "p", "r", "n", "b", "q", "k" }; // Symboles utilisés dans tes classes

        for (String c : colors) {
            for (String p : pieces) {
                String path = "assets/pieces/" + c + p + ".png";
                try {
                    Image img = ImageIO.read(new File(path));
                    imageCache.put("/" + path, img);
                } catch (IOException e) {
                    System.out.println("Image manquante : " + path);
                }
            }
        }
    }

    /**
     * Dessine les pièces sur le plateau d'échecs
     * 
     * @param g le graph pour dessiner les pièces
     */
    private void drawPieces(Graphics g) {
        for (int row = 0; row < 8; row++) {
            for (int col = 0; col < 8; col++) {
                Piece piece = board.getPiece(col, row);
                if (piece != null) {
                    Image img = imageCache.get(piece.getImagePath());
                    if (img != null) {
                        g.drawImage(img, col * TILE_SIZE, row * TILE_SIZE, TILE_SIZE, TILE_SIZE, this);
                    } else {
                        g.setColor(piece.getColor());
                        g.drawString(String.valueOf(piece.getSymbol()), col * TILE_SIZE + 35, row * TILE_SIZE + 45);
                    }
                }
            }
        }
    }

}
