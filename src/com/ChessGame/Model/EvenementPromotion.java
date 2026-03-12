package com.ChessGame.Model;

import java.awt.Color;

/**
 * Objet transporté dans notifyObservers() quand une promotion est détectée.
 * La Vue l'intercepte pour afficher la popup.
 */
public class EvenementPromotion {
    public final int x;
    public final int y;
    public final Color couleur;

    public EvenementPromotion(int x, int y, Color couleur) {
        this.x = x;
        this.y = y;
        this.couleur = couleur;
    }
}