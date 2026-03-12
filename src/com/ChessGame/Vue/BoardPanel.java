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
    public static final int MARGE = 30; // Notre espace pour le texte

    private int selectedX = -1;
    private int selectedY = -1;
    private Map<String, Image> imageCache = new HashMap<>();

    private List<Point> casesPossibles = new ArrayList<>();

    /**
     * Constructeur de la vue du plateau d'échecs
     * * @param board Le modèle du plateau d'échecs
     */
    public BoardPanel(Board board) {
        this.board = board;
        setPreferredSize(new Dimension(8 * TILE_SIZE + 2 * MARGE, 8 * TILE_SIZE + 2 * MARGE));
        loadAllImages();
    }

    /**
     * Met à jour la sélection de la case et redessine le plateau
     * 
     * @param x La coordonnée x de la case sélectionnée
     * @param y La coordonnée y de la case sélectionnée
     */
    public void setSelection(int x, int y) {
        this.selectedX = x;
        this.selectedY = y;
        this.repaint();
    }

    @Override
    public void update(Observable o, Object arg) {
        SwingUtilities.invokeLater(() -> repaint());
    }

    public void setCasesPossibles(List<Point> cases) {
        this.casesPossibles = cases;
        repaint();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        drawGrid(g);
        drawCoordinates(g); // NOUVEAU : Dessin du texte
        drawSelection(g);
        drawCasesPossibles(g);
        drawPieces(g);
    }

    /**
     * Dessine les coordonnées (a-h et 1-8) autour du plateau
     * 
     * @param g Le contexte graphique pour dessiner les coordonnées
     */
    private void drawCoordinates(Graphics g) {
        g.setColor(Color.BLACK); // Ou Color.WHITE si votre fond de fenêtre est sombre
        g.setFont(new Font("Arial", Font.BOLD, 14));
        FontMetrics fm = g.getFontMetrics();

        for (int i = 0; i < 8; i++) {
            // Dessin des lettres (a-h) en bas
            String lettre = String.valueOf((char) ('a' + i));
            int textWidth = fm.stringWidth(lettre);
            int xLettre = MARGE + (i * TILE_SIZE) + (TILE_SIZE - textWidth) / 2;
            int yLettre = MARGE + (8 * TILE_SIZE) + 20; // 20 pixels sous le plateau
            g.drawString(lettre, xLettre, yLettre);

            // Dessin des chiffres (8-1) à gauche
            String chiffre = String.valueOf(8 - i);
            int textHeight = fm.getAscent();
            int xChiffre = 10; // 10 pixels depuis le bord gauche de la fenêtre
            int yChiffre = MARGE + (i * TILE_SIZE) + (TILE_SIZE + textHeight) / 2 - 4;
            g.drawString(chiffre, xChiffre, yChiffre);
        }
    }

    /**
     * Dessine la grille du plateau d'échecs
     * 
     * @param g Le contexte graphique pour dessiner la grille
     */
    private void drawGrid(Graphics g) {
        for (int row = 0; row < 8; row++) {
            for (int col = 0; col < 8; col++) {
                if ((row + col) % 2 == 0)
                    g.setColor(new Color(235, 235, 208));
                else
                    g.setColor(new Color(119, 148, 85));

                // MODIFICATION : Ajout de la MARGE au X et au Y
                g.fillRect(MARGE + col * TILE_SIZE, MARGE + row * TILE_SIZE, TILE_SIZE, TILE_SIZE);
            }
        }
    }

    /**
     * Dessine la case sélectionnée en rouge transparent
     * 
     * @param g Le contexte graphique pour dessiner la sélection
     */
    private void drawSelection(Graphics g) {
        if (selectedX != -1 && selectedY != -1) {
            g.setColor(new Color(255, 0, 0, 150));
            // MODIFICATION : Ajout de la MARGE
            g.fillRect(MARGE + selectedX * TILE_SIZE, MARGE + selectedY * TILE_SIZE, TILE_SIZE, TILE_SIZE);
        }
    }

    /**
     * Dessine les cases possibles pour le mouvement d'une pièce en bleu transparent
     * 
     * @param g Le contexte graphique pour dessiner les cases possibles
     */
    private void drawCasesPossibles(Graphics g) {
        g.setColor(new Color(0, 0, 255, 150));
        for (Point p : casesPossibles) {
            // MODIFICATION : Ajout de la MARGE
            g.fillRect(MARGE + p.x * TILE_SIZE, MARGE + p.y * TILE_SIZE, TILE_SIZE, TILE_SIZE);
        }
    }

    /**
     * Charge toutes les images des pièces d'échecs dans le cache
     */
    private void loadAllImages() {
        String[] colors = { "w", "b" };
        String[] pieces = { "p", "r", "n", "b", "q", "k" };

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
     * Dessine les pièces sur le plateau en utilisant les images chargées
     * 
     * @param g Le contexte graphique pour dessiner les pièces
     */
    private void drawPieces(Graphics g) {
        for (int row = 0; row < 8; row++) {
            for (int col = 0; col < 8; col++) {
                Piece piece = board.getPiece(col, row);
                if (piece != null) {
                    Image img = imageCache.get(piece.getImagePath());
                    if (img != null) {
                        // MODIFICATION : Ajout de la MARGE
                        g.drawImage(img, MARGE + col * TILE_SIZE, MARGE + row * TILE_SIZE, TILE_SIZE, TILE_SIZE, this);
                    } else {
                        g.setColor(piece.getColor());
                        // MODIFICATION : Ajout de la MARGE
                        g.drawString(String.valueOf(piece.getSymbol()), MARGE + col * TILE_SIZE + 35,
                                MARGE + row * TILE_SIZE + 45);
                    }
                }
            }
        }
    }
}