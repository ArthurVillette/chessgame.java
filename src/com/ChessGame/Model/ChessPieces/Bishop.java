package com.ChessGame.Model.ChessPieces;

import com.ChessGame.Model.plateau.*;
import java.awt.Color;

/**
 * La classe Bishop représente le fou dans le jeu d'échecs. Elle hérite de la classe Piece et utilise un décorateur pour définir les mouvements en diagonale.
 */
public class Bishop extends Piece {
    public Bishop(Color color) {
        super(color, new DecoratorCasesEnDiagonale(null));
    }
    @Override public char getSymbol() { return 'b'; }
    @Override public String getImagePath() {
        return color == Color.WHITE ? "/assets/pieces/wb.png" : "/assets/pieces/bb.png";
    }

    /**
     * Classe représentant le Pion, une pièce d'échecs qui se déplace principalement vers l'avant, avec des règles spécifiques pour les premiers déplacements et les captures
     * et l'utilisation d'un décorateur pour définir les mouvements possibles du pion
     */
    public static class Pawn extends Piece {
        public Pawn(Color color) {
            super(color, new DecoratorCasesPion(null));
        }
        @Override public char getSymbol() { return 'p'; }
        @Override public String getImagePath() {
            return color == Color.WHITE ? "/assets/pieces/wp.png" : "/assets/pieces/bp.png";
        }
    }
}