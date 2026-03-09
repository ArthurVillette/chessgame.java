package com.ChessGame.Model;
import java.awt.*;
import java.util.List;
import java.util.Set;

/**
 * Classe abstraite représentant une pièce d'échecs
 */
public abstract class Piece {
    protected Color color; 
    protected char symbol;

    /**
     * Constructeur de la classe Piece
     * @param color La couleur de la pièce (Color.WHITE ou Color.BLACK)
     * @param symbol Le symbole représentant la pièce (ex: 'P' pour Pawn, 'K' pour King, etc.)
     */
    public Piece(Color color, char symbol) {
        this.color = color;
        this.symbol = symbol;
    }

    /**
     * Retourne la couleur de la pièce
     * @return La couleur de la pièce (Color.WHITE ou Color.BLACK)
     */
    public Color getColor() {
        return color;
    }

    /**
     * Retourne le symbole de la pièce
     * @return Le symbole représentant la pièce (ex: 'P' pour Pawn, 'K' pour King, etc.)
     */
    public char getSymbol() {
        return symbol;
    }

    /**
     * Retourne le chemin de l'image représentant la pièce
     * @return Le chemin de l'image (ex: "/assets/pieces/wP.png" pour un pion blanc)
     */
    public String getImagePath() {
        String colorP = (color == Color.WHITE) ? "w" : "b";
        return "/assets/pieces/" + colorP + symbol + ".png";

}
    /**
     * Méthode abstraite à implémenter dans les classes filles pour retourner les mouvements valides de la pièce
     * @param position La position actuelle de la pièce sur le plateau
     * @param board Le plateau de jeu actuel
     * @return Une liste de points représentant les positions valides où la pièce peut se déplacer
     */
    public abstract List<Point> mouvementsValides(Point position, Board board);
}