package com.ChessGame.Model;

import java.awt.*;
import java.util.List;

/**
 * Classe représentant une partie d'échecs, gérant les joueurs, le plateau et les règles du jeu
 */
public class Partie {
    private Joueur jBlanc;
    private Joueur jNoir;
    private Joueur joueurCourant;
    private Board board;

    /**
     * Constructeur de la classe Partie
     * @param board Le plateau de jeu à utiliser pour la partie
     */
    public Partie(Board board) {
        this.board = board;
        this.jBlanc = new Joueur(true);
        this.jNoir = new Joueur(false);
        this.joueurCourant = jBlanc;
    }
    /** Getters pour accéder aux joueurs et au plateau de jeu
     */
    public Joueur getJoueurCourant() { return joueurCourant; }
    /**
     * Permet d'obtenir le plateau de jeu actuel
     * @return Le plateau de jeu utilisé dans la partie
     */
    public Board getBoard() { return board; }

    /**
     * Permet de passer le tour au joueur suivant
     */
    public void passerTour() {
        joueurCourant = (joueurCourant == jBlanc) ? jNoir : jBlanc;
    }
    /**
     * Applique un coup sur le plateau de jeu
     * @param coup Le coup à appliquer
     */
    public void appliquerCoup(Coup coup) {
        Piece piece = board.getPiece(coup.depart.x, coup.depart.y);
        if (piece != null) {
            board.setPiece(coup.arrivee.x, coup.arrivee.y, piece);
            board.setPiece(coup.depart.x, coup.depart.y, null);
        }
    }

    /**
     * Vérifie si un coup est valide selon les règles du jeu
     * @param coup Le coup à vérifier
     * @return true si le coup est valide, false sinon
     */
    public boolean coupValide(Coup coup) {
        Piece piece = board.getPiece(coup.depart.x, coup.depart.y);
        if (piece == null) return false;

        List<Point> mouvements = piece.mouvementsValides(coup.depart, board);
        return mouvements.contains(coup.arrivee);
    }

    /**
     * Vérifie si la partie est terminée (échec et mat, pat, etc.)
     * @return true si la partie est terminée, false sinon
     */
    public boolean estTerminee() {
        //-----TO DO: implémenter la logique de fin de partie (échec et mat, pat, etc.)
        return false;
    }
}