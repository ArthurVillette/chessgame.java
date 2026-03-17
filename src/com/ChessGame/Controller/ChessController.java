package com.ChessGame.Controller;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import com.ChessGame.Model.plateau.Board;
import com.ChessGame.Model.jeu.Coup;
import com.ChessGame.Model.jeu.Partie;
import com.ChessGame.Model.ChessPieces.Piece;
import com.ChessGame.Vue.ChessFrame;
import com.ChessGame.Vue.BoardPanel;

/**
 * Classe représentant le contrôleur du jeu d'échecs
 */
public class ChessController extends MouseAdapter {
    private final Board model;
    private final ChessFrame view;
    private final BoardPanel boardPanel;
    private final Partie partie;

    private Point selection = null; // null = aucune pièce sélectionnée
    private boolean enPause = false;


    public ChessController(Board model, ChessFrame view, Partie partie) {
        this.model = model;
        this.view = view;
        this.boardPanel = view.getBoardPanel();
        this.partie = partie; // ajout
    }

    public void setEnPause(boolean enPause) {
        this.enPause = enPause;
    }

    /**
     * Gère les clics de souris sur le plateau d'échecs
     * 
     * @param e l'événement de clic de souris
     * @Override une méthode de MouseAdapter pour gérer les clics de souris sur le
     *           plateau d'échecs
     */
    @Override
    public void mousePressed(MouseEvent e) {
        if (enPause) return;
        int clicX = e.getX() - BoardPanel.MARGE;
        int clicY = e.getY() - BoardPanel.MARGE;
        if (clicX < 0 || clicY < 0 || clicX >= 8 * BoardPanel.TILE_SIZE || clicY >= 8 * BoardPanel.TILE_SIZE) {
            if (selection != null) {
                selection = null;
                boardPanel.setSelection(-1, -1);
                boardPanel.setCasesPossibles(new ArrayList<>());
            }
            return;
        }

        int x = clicX / BoardPanel.TILE_SIZE;
        int y = clicY / BoardPanel.TILE_SIZE;

        if (selection == null) {
            Piece piece = model.getPiece(x, y);
            if (piece == null)
                return;
            if (piece.getColor() != partie.getJoueurCourant().getCouleur())
                return;
            if (model.getPiece(x, y) != null) {
                selection = new Point(x, y);
                boardPanel.setSelection(x, y);

                List<Point> moves = piece.getMouvementsLegaux(selection, model, partie);
                boardPanel.setCasesPossibles(moves);
            }
        } else {
            if (selection.x == x && selection.y == y) {
                selection = null;
                boardPanel.setSelection(-1, -1);
                boardPanel.setCasesPossibles(new ArrayList<>());
                return;
            }
            // Re-selection directe si on clique sur une autre piece de sa couleur
            Piece autrePiece = model.getPiece(x, y);
            if (autrePiece != null && autrePiece.getColor() == partie.getJoueurCourant().getCouleur()) {
                selection = new Point(x, y);
                boardPanel.setSelection(x, y);
                List<Point> moves = autrePiece.getMouvementsLegaux(selection, model, partie);
                boardPanel.setCasesPossibles(moves);
                return;
            }
            Coup coup = new Coup(selection, new Point(x, y));
            if (partie.coupValide(coup)) {
                partie.getJoueurCourant().setCoup(coup);
                selection = null;
                boardPanel.setSelection(-1, -1);
                boardPanel.setCasesPossibles(new ArrayList<>());
            }
        }
    }
}
