package com.ChessGame.Controller;

import com.ChessGame.Model.*;
import com.ChessGame.Vue.BoardPanel;
import javax.swing.SwingUtilities;

/**
 * Classe responsable de la boucle de jeu, exécutée dans un thread séparé
 */
public class JeuController implements Runnable {

    private Partie partie;
    private BoardPanel boardPanel;

    /**
     * Constructeur du JeuController
     * @param partie La partie en cours à contrôler
     * @param boardPanel Le panneau de jeu à mettre à jour après chaque coup
     */
    public JeuController(Partie partie, BoardPanel boardPanel) {
        this.partie = partie;
        this.boardPanel = boardPanel;
    }

    /**
     * La boucle de jeu principale, qui attend les coups des joueurs et met à jour la partie
     * jusqu'à ce que la partie soit terminée
     */
    @Override
    public void run() {
        while (!partie.estTerminee()) {
            Joueur joueurCourant = partie.getJoueurCourant();
            try {
                Coup coup = joueurCourant.getCoup(); // BLOQUE ici
                partie.appliquerCoup(coup);
                partie.passerTour();
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                break;
            }
        }
    }
}