package com.ChessGame.Model;

import java.awt.*;
import java.util.ArrayList;

import java.util.List;

/**
 * Classe représentant la pièce de type Rook (Tour)
 */
public class Rook extends Piece {
    /**
     * Constructeur de la classe Rook
     * @param color La couleur de la pièce (Color.WHITE ou Color.BLACK)
     */
    public Rook(Color color) {
        super(color,'R'); 
    }

    /**
     * Méthode pour obtenir les mouvements valides de la tour à partir de sa position actuelle
     * @param position La position actuelle de la tour sur le plateau
     * @param board Le plateau de jeu pour vérifier les obstacles et les pièces adverses
     * @return Une liste de points représentant les positions valides où la tour peut se déplacer
     */
    @Override
    public List<Point> mouvementsValides(Point position, Board board) {
        List<Point> moves = new ArrayList<>();
        int[][] directions = {{1, 0}, {-1, 0}, {0, 1}, {0, -1}};

        for (int[] dir : directions) {
            int x = position.x + dir[0];
            int y = position.y + dir[1];
            while (x >= 0 && x < 8 && y >= 0 && y < 8) {
                Piece cible = board.getPiece(x, y);
                if (cible == null) {
                    moves.add(new Point(x, y)); // case vide
                } else {
                    if (cible.getColor() != this.color) moves.add(new Point(x, y)); // mange
                    break; // bloqué
                }
                x += dir[0];
                y += dir[1];
            }
        }
        return moves;
    }
    
}
