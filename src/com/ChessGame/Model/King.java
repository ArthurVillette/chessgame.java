package com.ChessGame.Model;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Classe représentant la pièce de type King (Roi)
 */
public class King extends Piece {
    /**
     * Constructeur de la classe King
     * 
     * @param color La couleur de la pièce (Color.WHITE ou Color.BLACK)
     */
    public King(Color color) {
        super(color, 'k');
    }

    /**
     * Méthode pour obtenir les mouvements valides du roi à partir de sa position
     * actuelle
     * 
     * @param position La position actuelle du roi sur le plateau
     * @param board    Le plateau de jeu pour vérifier les mouvements
     * @return Une liste de points représentant les mouvements valides du roi
     */
    @Override
    public List<Point> mouvementsValides(Point position, Board board) {
        List<Point> moves = new ArrayList<>();
        int[][] directions = { { 1, 0 }, { -1, 0 }, { 0, 1 }, { 0, -1 }, { 1, 1 }, { 1, -1 }, { -1, 1 }, { -1, -1 } };

        for (int[] dir : directions) {
            int x = position.x + dir[0];
            int y = position.y + dir[1];
            if (x >= 0 && x < 8 && y >= 0 && y < 8) {
                Piece cible = board.getPiece(x, y);
                if (cible == null || cible.getColor() != this.color)
                    moves.add(new Point(x, y));
            }
        }
        return moves;
    }
}
