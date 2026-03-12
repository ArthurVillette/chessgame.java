package com.ChessGame.Model;

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
}