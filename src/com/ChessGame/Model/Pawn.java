package com.ChessGame.Model;

import java.awt.Color;

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
    
}
