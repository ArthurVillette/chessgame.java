package com.ChessGame.Model.IA;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;

/**
 * Classe gérant la communication avec le moteur d'échecs Stockfish
 */
public class IAClient {
    private Process processus;
    private BufferedReader lecteur;
    private OutputStreamWriter ecrivain;

    public void IAClient() {
    }

    /**
     * Lance l'exécutable Stockfish
     * 
     * @param chemin Le chemin vers le fichier exécutable
     * @return true si le démarrage a réussi, false sinon
     */

    public boolean demarrerMoteur(String chemin) {
        try {
            processus = new ProcessBuilder(chemin.split(" ")).start();
            lecteur = new BufferedReader(new InputStreamReader(processus.getInputStream()));
            ecrivain = new OutputStreamWriter(processus.getOutputStream());
            return true;
        } catch (Exception e) {
            System.err.println("Erreur au lancement de Stockfish : " + e.getMessage());
            return false;
        }
    }

    /**
     * Envoie une commande texte au moteur (Protocole UCI)
     * 
     * @param commande La commande à envoyer (ex: "uci", "isready", "position
     *                 startpos moves e2e4", etc.)
     */
    public void envoyerCommande(String commande) {
        try {
            ecrivain.write(commande + "\n");
            ecrivain.flush();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Lit la réponse du moteur après une commande
     * 
     * @param motDeFin Le mot indiquant la fin de la réponse (ex: "uciok" pour la
     *                 commande "uci")
     * @return La réponse du moteur sous forme de chaîne de caractères
     */
    public String lireReponseComplete(String motDeFin) {
        StringBuilder reponse = new StringBuilder();
        try {
            String ligne;
            while ((ligne = lecteur.readLine()) != null) {
                reponse.append(ligne).append("\n");

                // On s'arrête si la ligne commence par le mot attendu
                if (ligne.startsWith(motDeFin)) {
                    break;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return reponse.toString();
    }

    /**
     * Ferme proprement le moteur
     */
    public void arreterMoteur() {
        try {
            envoyerCommande("quit");
            if (lecteur != null)
                lecteur.close();
            if (ecrivain != null)
                ecrivain.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}