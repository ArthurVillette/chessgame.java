package com.ChessGame.Model;

import com.ChessGame.Model.plateau.*;
import java.awt.Color;

/**
 * Classe représentant le Cavalier, une pièce d'échecs qui se déplace en "L" (deux cases dans une direction puis une case perpendiculaire)
 */
public class Knight extends Piece {
    public Knight(Color color) {
        super(color, new DecoratorCasesCavalier(null));
    }
    @Override public char getSymbol() { return 'n'; }
    @Override public String getImagePath() {
        return color == Color.WHITE ? "/assets/pieces/wn.png" : "/assets/pieces/bn.png";
    }
}