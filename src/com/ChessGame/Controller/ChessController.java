package com.ChessGame.Controller;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import com.ChessGame.Model.Board;
import com.ChessGame.Model.Coup;
import com.ChessGame.Model.Partie;
import com.ChessGame.Model.Piece;
import com.ChessGame.Vue.ChessFrame;
import com.ChessGame.Vue.BoardPanel;

/**
 * Classe représentant le contrôleur du jeu d'échecs
 */
public class ChessController extends MouseAdapter {
    private Board model;
    private ChessFrame view;
    private BoardPanel boardPanel;
    private Partie partie;

    private Point selection = null; // null = aucune pièce sélectionnée

    /**
     * Constructeur du contrôleur d'échecs
     * 
     * @param model Le modèle du plateau d'échecs
     * @param view  La vue principale de l'application
     */
    public ChessController(Board model, ChessFrame view) {
        this.model = model;
        this.view = view;
        this.boardPanel = view.getBoardPanel();
    }

    public ChessController(Board model, ChessFrame view, Partie partie) {
        this.model = model;
        this.view = view;
        this.boardPanel = view.getBoardPanel();
        this.partie = partie; // ajout
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
        int x = e.getX() / boardPanel.TILE_SIZE;
        int y = e.getY() / boardPanel.TILE_SIZE;

        // boardPanel.setSelection(x, y);
        if (selection == null) {
            Piece piece = model.getPiece(x, y);
            if (piece == null)
                return;
            if (piece.getColor() != partie.getJoueurCourant().getCouleur())
                return;
            // Premier clic : sélectionner une pièce
            if (model.getPiece(x, y) != null) {
                selection = new Point(x, y);
                boardPanel.setSelection(x, y);

                // Calcule et affiche les cases possibles
                List<Point> moves = piece.getMouvementsLegaux(selection, model, partie);
                boardPanel.setCasesPossibles(moves);
            }
        } else {
            // deuxieme click
            // Si on reclique la même case → désélectionner simplement
            if (selection.x == x && selection.y == y) {
                selection = null;
                boardPanel.setSelection(-1, -1);
                boardPanel.setCasesPossibles(new ArrayList<>());
                return;
            }
            // Sinon envoyer le coup normalement
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
