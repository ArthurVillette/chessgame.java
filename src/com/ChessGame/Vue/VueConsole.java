package com.ChessGame.Vue;

import com.ChessGame.Model.ChessPieces.Piece;
import com.ChessGame.Model.jeu.EvenementMouvement;
import com.ChessGame.Model.jeu.Partie;
import com.ChessGame.Model.plateau.Board;

import java.util.ArrayList;
import java.util.List;
import java.util.Observable;
import java.util.Observer;

/**
 * Vue console du jeu d'echecs.
 * Affiche le plateau ASCII et l'historique cote a cote.
 * Implementee comme Observer sans modifier le modele.
 *
 * Exemple d'affichage :
 *   +---+---+---+---+---+---+---+---+    Historique des coups
 * 8 | r | n | b | q | k | b | n | r |    1. e4       e5
 *   +---+---+---+---+---+---+---+---+    2. Nf3       Nc6
 * 7 | p | p | p | p |   | p | p | p |
 *     a   b   c   d   e   f   g   h
 */
public class VueConsole implements Observer {

    private final List<String> historique = new ArrayList<>();

    @Override
    public void update(Observable o, Object arg) {
        if (!(arg instanceof EvenementMouvement)) return;
        if (!(o instanceof Partie)) return;

        Partie partie = (Partie) o;
        afficherPlateau(partie.getBoard(), partie);

        String tour = partie.getJoueurCourant().isWhite() ? "Blancs" : "Noirs";
        System.out.println("  Tour des " + tour);
        System.out.println();
    }

    /**
     * Appele depuis Main pour afficher la position initiale.
     */
    public void afficherPositionInitiale(Board board) {
        System.out.println("=== POISSON BLOQUE — Vue Console ===");
        System.out.println();
        afficherPlateau(board, null);
        System.out.println("  Tour des Blancs");
        System.out.println();
    }

    /**
     * Enregistre un coup dans l'historique local de la vue console.
     * Appele depuis Main apres chaque coup.
     */
    public void ajouterCoup(String notation) {
        historique.add(notation.trim());
    }

    /**
     * Affiche plateau + historique cote a cote.
     * Le plateau occupe ~38 caracteres de large, l'historique suit a droite.
     */
    private void afficherPlateau(Board board, Partie partie) {
        // Construire les lignes du plateau (17 lignes : separateurs + rangees)
        List<String> lignesPlateau = new ArrayList<>();
        String sep = "  +---+---+---+---+---+---+---+---+";

        for (int y = 0; y < 8; y++) {
            lignesPlateau.add(sep);
            StringBuilder ligne = new StringBuilder();
            ligne.append((8 - y)).append(" ");
            for (int x = 0; x < 8; x++) {
                Piece piece = board.getPiece(x, y);
                ligne.append("| ").append(piece == null ? " " : getSymbole(piece)).append(" ");
            }
            ligne.append("|");
            lignesPlateau.add(ligne.toString());
        }
        lignesPlateau.add(sep);
        lignesPlateau.add("    a   b   c   d   e   f   g   h  ");

        // Construire les lignes de l'historique
        List<String> lignesHisto = new ArrayList<>();
        lignesHisto.add("  Historique des coups");
        lignesHisto.add("  " + "-".repeat(22));

        // Regrouper par tour (blanc + noir sur la meme ligne)
        List<String> h = partie != null ? partie.getHistoriquePGN() : historique;
        for (int i = 0; i < h.size(); i += 2) {
            String blanc = h.get(i);
            String noir  = (i + 1 < h.size()) ? h.get(i + 1) : "";
            lignesHisto.add(String.format("  %2d. %-8s %s", (i/2 + 1), blanc, noir));
        }

        // Afficher cote a cote
        int nbLignes = Math.max(lignesPlateau.size(), lignesHisto.size());
        for (int i = 0; i < nbLignes; i++) {
            String gauche = i < lignesPlateau.size() ? lignesPlateau.get(i) : "";
            String droite = i < lignesHisto.size()   ? lignesHisto.get(i)   : "";
            // Padder le plateau a 40 chars pour aligner l'historique
            System.out.printf("%-40s %s%n", gauche, droite);
        }
    }

    private String getSymbole(Piece piece) {
        char sym = piece.getSymbol();
        boolean blanc = piece.getColor().equals(java.awt.Color.WHITE);
        switch (Character.toLowerCase(sym)) {
            case 'p': return blanc ? "P" : "p";
            case 'r': return blanc ? "R" : "r";
            case 'n': return blanc ? "N" : "n";
            case 'b': return blanc ? "B" : "b";
            case 'q': return blanc ? "Q" : "q";
            case 'k': return blanc ? "K" : "k";
            default:   return blanc ? String.valueOf(Character.toUpperCase(sym))
                    : String.valueOf(Character.toLowerCase(sym));
        }
    }
}