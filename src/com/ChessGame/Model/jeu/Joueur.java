package com.ChessGame.Model.jeu;

import java.awt.*;

/**
 * Classe représentant un joueur dans le jeu d'échecs
 */
public class Joueur {
    private boolean isWhite;
    private Coup coupEnAttente = null;

    /**
     * Constructeur de la classe Joueur
     * @param isWhite Indique si le joueur est blanc (true) ou noir (false)
     */
    public Joueur(boolean isWhite) {
        this.isWhite = isWhite;
    }

    /**
     * Indique si le joueur est blanc ou noir
     * @return true si le joueur est blanc, false sinon
     */
    public boolean isWhite() { return isWhite; }

    /**
     * Méthode appelée par le thread du jeu pour obtenir le coup joué par l'utilisateur.
     * Cette méthode BLOQUE jusqu'à ce que le Controller (thread Swing) appelle setCoup().
     */
    public synchronized Coup getCoup() throws InterruptedException {
        while (coupEnAttente == null) {
            wait();
        }
        Coup coup = coupEnAttente;
        coupEnAttente = null;
        return coup;
    }

    /**
     * Méthode appelée par le Controller (thread Swing) pour fournir le coup joué par l'utilisateur.
     * Cette méthode DÉBLOQUE getCoup() qui attendait un coup.
     */
    public synchronized void setCoup(Coup coup) {
        this.coupEnAttente = coup;
        notify();
    }

    /**
     * Retourne la couleur du joueur (blanc ou noir)
     * @return Color.WHITE si le joueur est blanc, sinon Color.BLACK
     */
    public Color getCouleur() {
        return isWhite ? Color.WHITE : Color.BLACK;
    }
}