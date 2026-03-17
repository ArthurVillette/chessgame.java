package com.ChessGame.Model.IA;

import com.ChessGame.Model.jeu.Coup;
import com.ChessGame.Model.jeu.Joueur;
import com.ChessGame.Model.jeu.Partie;

import java.awt.Point;

/**
 * Représente un joueur contrôlé par le réseau de neurones (VILLETTE)
 */
public class JoueurIA extends Joueur {

    private IAClient moteurIA;
    private Partie partie;

    /**
     * Constructeur de JoueurIA
     * 
     * @param estBlanc Indique si le joueur est blanc ou noir
     * @param moteurIA Le client pour communiquer avec le moteur d'échecs
     * 
     * @param partie   La partie en cours, nécessaire pour obtenir l'état du plateau
     */
    public JoueurIA(boolean estBlanc, IAClient moteurIA, Partie partie) {
        super(estBlanc);
        this.moteurIA = moteurIA;
        this.partie = partie;
    }

    @Override
    public Coup getCoup() throws InterruptedException {
        String fen = partie.getBoard().toFEN(isWhite());

        moteurIA.envoyerCommande("position fen " + fen);
        moteurIA.envoyerCommande("go");

        String reponse = moteurIA.lireReponseComplete("bestmove");

        String uciCoup = "";
        String[] lignes = reponse.split("\n");
        for (String ligne : lignes) {
            if (ligne.startsWith("bestmove")) {
                uciCoup = ligne.split(" ")[1];
                break;
            }
        }

        if (uciCoup.isEmpty() || uciCoup.length() < 4) {
            System.err.println("Erreur fatale : L'IA n'a pas renvoyé de coup valide.");
            throw new InterruptedException("Coup IA invalide");
        }

        System.out.println("VILLETTE a choisi de jouer : " + uciCoup);

        int departX = uciCoup.charAt(0) - 'a';
        int arriveeX = uciCoup.charAt(2) - 'a';

        int departY = '8' - uciCoup.charAt(1);
        int arriveeY = '8' - uciCoup.charAt(3);

        Point depart = new Point(departX, departY);
        Point arrivee = new Point(arriveeX, arriveeY);

        return new Coup(depart, arrivee);
    }
}