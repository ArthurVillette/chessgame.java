package com.ChessGame.Model;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Classe représentant la pièce de type Knight (Cavalier)
 */
public class Knight extends Piece {
    /**
     * Constructeur de la classe Knight
     * 
     * @param color La couleur de la pièce (Color.WHITE ou Color.BLACK)
     */
    public Knight(Color color) {
        super(color, 'n');
    }

    /**
     * Méthode pour obtenir les mouvements valides du cavalier à partir de sa
     * position actuelle
     * 
     * @param position La position actuelle du cavalier sur le plateau
     * @param board    Le plateau de jeu pour vérifier les mouvements
     * @return Une liste de points représentant les mouvements valides du cavalier
     */
    @Override
    public List<Point> mouvementsValides(Point position, Board board) {
        List<Point> moves = new ArrayList<>();
        int[][] sauts = { { 2, 1 }, { 2, -1 }, { -2, 1 }, { -2, -1 }, { 1, 2 }, { 1, -2 }, { -1, 2 }, { -1, -2 } };

        for (int[] saut : sauts) {
            int x = position.x + saut[0];
            int y = position.y + saut[1];
            if (x >= 0 && x < 8 && y >= 0 && y < 8) {
                Piece cible = board.getPiece(x, y);
                if (cible == null || cible.getColor() != this.color)
                    moves.add(new Point(x, y));
            }
        }
        return moves;
    }
}
