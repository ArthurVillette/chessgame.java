package com.ChessGame.Model.jeu;

import com.ChessGame.Model.plateau.*;
import java.awt.Color;

/**
 * La classe Queen représente la reine dans le jeu d'échecs. Elle hérite de la classe Piece et utilise un décorateur pour définir les mouvements en ligne et en diagonale.
 */
public class Queen extends Piece {
    public Queen(Color color) {
        // Reine = Lignes + Diagonales chaînées
        super(color, new DecoratorCasesEnLigne(new DecoratorCasesEnDiagonale(null)));
    }
    @Override public char getSymbol() { return 'q'; }
    @Override public String getImagePath() {
        return color == Color.WHITE ? "/assets/pieces/wq.png" : "/assets/pieces/bq.png";
    }
}