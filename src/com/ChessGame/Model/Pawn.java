package com.ChessGame.Model;

import java.awt.Color;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;
/**
 * Classe représentant la pièce de type Pawn (Pion)
 */
public class Pawn extends Piece {
    /**
     * Constructeur de la classe Pawn
     * @param color La couleur de la pièce (Color.WHITE ou Color.BLACK)
     */
    public Pawn(Color color) {
        super(color,'P'); 
    }

    /**
     * Méthode pour obtenir les mouvements valides du pion à partir de sa position actuelle
     * @param position La position actuelle du pion sur le plateau
     * @param board Le plateau de jeu pour vérifier les mouvements
     * @return Une liste de points représentant les mouvements valides du pion
     */
    @Override
    public List<Point> mouvementsValides(Point position, Board board) {
        List<Point> moves = new ArrayList<>();
        int direction = (this.color == java.awt.Color.WHITE) ? -1 : 1; // blanc monte, noir descend
        int x = position.x;
        int y = position.y;

        // Avancer d'une case
        if (y + direction >= 0 && y + direction < 8 && board.getPiece(x, y + direction) == null) {
            moves.add(new Point(x, y + direction));

            boolean departBlanc = (this.color == java.awt.Color.WHITE && y == 6);
            boolean departNoir  = (this.color == java.awt.Color.BLACK && y == 1);
            if ((departBlanc || departNoir) && board.getPiece(x, y + 2 * direction) == null) {
                moves.add(new Point(x, y + 2 * direction));
            }
        }

        // Manger en diagonale
        for (int dx : new int[]{-1, 1}) {
            int nx = x + dx;
            int ny = y + direction;
            if (nx >= 0 && nx < 8 && ny >= 0 && ny < 8) {
                Piece cible = board.getPiece(nx, ny);
                if (cible != null && cible.getColor() != this.color) moves.add(new Point(nx, ny));
            }
        }
        return moves;
    }
    
}
