package com.ChessGame.Vue;

import javax.swing.*;

import com.ChessGame.Model.jeu.Partie;
import com.ChessGame.Model.plateau.Board;
import java.awt.*;
import com.ChessGame.Model.jeu.EvenementMouvement;
import com.ChessGame.Model.ChessPieces.Piece;
import javax.imageio.ImageIO;
import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.List;

/**
 * Classe représentant la vue du plateau d'échecs
 */
public class BoardPanel extends JPanel implements Observer {
    private final Board board;
    private Partie partie;
    public static final int TILE_SIZE = ChessFrame.TILE_SIZE;
    public static final int MARGE = Math.max(30, ChessFrame.TILE_SIZE / 4);
    private boolean estRetourne = false;
    private int selectedX = -1;
    private int selectedY = -1;

    private final Map<String, Image> imageCache = new HashMap<>();

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
     * Convertit une coordonnée de colonne (0-7) en coordonnée pixel X en fonction
     * du retournement du plateau
     * 
     * @param col La coordonnée de colonne (0-7)
     * @return La coordonnée pixel X correspondante
     */
    private int toPixelX(int col) {
        return MARGE + (estRetourne ? (7 - col) : col) * TILE_SIZE;
    }

    /**
     * Convertit une coordonnée de ligne (0-7) en coordonnée pixel Y en fonction du
     * retournement du plateau
     * 
     * @param row La coordonnée de ligne (0-7)
     * @return La coordonnée pixel Y correspondante
     */
    private int toPixelY(int row) {
        return MARGE + (estRetourne ? (7 - row) : row) * TILE_SIZE;
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

    public void setRetourne(boolean retourne) {
        this.estRetourne = retourne;
        repaint();
    }

    public void setPartie(Partie p) {
        this.partie = p;
    }

    /**
     * Met à jour la vue du plateau d'échecs lorsque le modèle change
     * Réagit uniquement aux EvenementMouvement → repaint.
     * 
     * @param o   l'objet observable (Partie) qui a changé
     * @param arg un argument optionnel (non utilisé ici)
     **/

    @Override
    public void update(java.util.Observable o, Object arg) {
        if (arg instanceof EvenementMouvement) {
            SwingUtilities.invokeLater(this::repaint);
        }
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
        g.setColor(new Color(201, 162, 39)); // Ou Color.WHITE si votre fond de fenêtre est sombre
        g.setFont(new Font("Arial", Font.BOLD, 14));
        FontMetrics fm = g.getFontMetrics();

        for (int i = 0; i < 8; i++) {

            int colLogique = estRetourne ? (7 - i) : i;
            String lettre = String.valueOf((char) ('a' + colLogique));

            int textWidth = fm.stringWidth(lettre);
            int xLettre = MARGE + (i * TILE_SIZE) + (TILE_SIZE - textWidth) / 2;
            int yLettre = MARGE + (8 * TILE_SIZE) + 20; // 20 pixels sous le plateau
            g.drawString(lettre, xLettre, yLettre);

            int rowLogique = estRetourne ? i : (7 - i);
            String chiffre = String.valueOf(rowLogique + 1);
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

                g.fillRect(toPixelX(col), toPixelY(row), TILE_SIZE, TILE_SIZE);
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
            g.setColor(new Color(30, 60, 180, 180));
            g.fillRect(toPixelX(selectedX), toPixelY(selectedY), TILE_SIZE, TILE_SIZE);
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

            g.fillRect(toPixelX(p.x), toPixelY(p.y), TILE_SIZE, TILE_SIZE);
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
     * Indique si le plateau est retourné (le joueur joue les Noirs)
     * 
     * @return true si le plateau est retourné, false sinon
     */
    public boolean isRetourne() {
        return this.estRetourne;
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
                    // Surligner le roi en echec en rouge
                    if (piece.getSymbol() == 'k' || piece.getSymbol() == 'K') {
                        if (partie != null) {
                            com.ChessGame.Model.jeu.Joueur joueur = piece.getColor().equals(java.awt.Color.WHITE)
                                    ? partie.getJoueurBlanc()
                                    : partie.getJoueurNoir();
                            if (joueur != null && partie.roiEnEchec(joueur)) {
                                g.setColor(new Color(220, 50, 50, 180));
                                g.fillRect(toPixelX(col), toPixelY(row), TILE_SIZE, TILE_SIZE);
                            }
                        }
                    }
                    Image img = imageCache.get(piece.getImagePath());
                    if (img != null) {
                        // MODIFICATION : Ajout de la MARGE
                        g.drawImage(img, toPixelX(col), toPixelY(row), TILE_SIZE, TILE_SIZE, this);
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