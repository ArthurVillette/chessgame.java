package com.ChessGame.Model;

import com.ChessGame.Model.plateau.*;
import java.awt.Color;

/**
 * Classe représentant le Pion, une pièce d'échecs qui se déplace principalement vers l'avant, avec des règles spécifiques pour les premiers déplacements et les captures
 * et l'utilisation d'un décorateur pour définir les mouvements possibles du pion
 */
public class Pawn extends Piece {
    public Pawn(Color color) {
        super(color, new DecoratorCasesPion(null));
    }
    @Override public char getSymbol() { return 'p'; }
    @Override public String getImagePath() {
        return color == Color.WHITE ? "/assets/pieces/wp.png" : "/assets/pieces/bp.png";
    }
}