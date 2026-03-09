package com.ChessGame.Model;

import java.awt.*;
import java.util.ArrayList;
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
     * 
     * @param color  La couleur de la pièce (Color.WHITE ou Color.BLACK)
     * @param symbol Le symbole représentant la pièce (ex: 'P' pour Pawn, 'K' pour
     *               King, etc.)
     */
    public Piece(Color color, char symbol) {
        this.color = color;
        this.symbol = symbol;
    }

    /**
     * Retourne la couleur de la pièce
     * 
     * @return La couleur de la pièce (Color.WHITE ou Color.BLACK)
     */
    public Color getColor() {
        return color;
    }

    /**
     * Retourne le symbole de la pièce
     * 
     * @return Le symbole représentant la pièce (ex: 'P' pour Pawn, 'K' pour King,
     *         etc.)
     */
    public char getSymbol() {
        return symbol;
    }

    /**
     * Retourne le chemin de l'image représentant la pièce
     * 
     * @return Le chemin de l'image (ex: "/assets/pieces/wP.png" pour un pion blanc)
     */
    public String getImagePath() {
        String colorP = (color == Color.WHITE) ? "w" : "b";
        return "/assets/pieces/" + colorP + symbol + ".png";

    }

    /**
     * Méthode abstraite à implémenter dans les classes filles pour retourner les
     * mouvements valides de la pièce
     * 
     * @param position La position actuelle de la pièce sur le plateau
     * @param board    Le plateau de jeu actuel
     * @return Une liste de points représentant les positions valides où la pièce
     *         peut se déplacer
     */
    public abstract List<Point> mouvementsValides(Point position, Board board);

    /**
     * Retourne les mouvements légaux de la pièce en filtrant les mouvements valides
     * pour ne garder que ceux qui ne laissent pas le roi en échec
     * 
     * @param position La position actuelle de la pièce sur le plateau
     * @param board    Le plateau de jeu actuel
     * @param partie   La partie en cours pour vérifier les règles d'échec
     * @return Une liste de points représentant les positions légales où la pièce
     *         peut se déplacer sans laisser le roi en échec
     */
    public List<Point> getMouvementsLegaux(Point position, Board board, Partie partie) {
        List<Point> coupsPossibles = mouvementsValides(position, board);
        List<Point> coupsLegaux = new ArrayList<>();
        for (Point arrivee : coupsPossibles) {
            if (!partie.coupLaisseLeRoiEnEchec(new Coup(position, arrivee))) {
                coupsLegaux.add(arrivee);
            }
        }

        return coupsLegaux;
    }
}