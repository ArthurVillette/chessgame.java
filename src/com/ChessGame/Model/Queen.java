package com.ChessGame.Model;
import java.awt.Color;

/**
 * Classe représentant la pièce de la reine dans le jeu d'échecs
 */
public class Queen extends Piece {
    /**
     * Constructeur de la classe Queen
     * @param color La couleur de la pièce (Color.WHITE ou Color.BLACK)
     */
    public Queen(Color color) {
        super(color,'Q'); 
    }
    
}
