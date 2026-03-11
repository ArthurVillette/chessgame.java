package com.ChessGame.Controller;

import com.ChessGame.Model.*;
import com.ChessGame.Vue.BoardPanel;
import javax.swing.SwingUtilities;
import com.ChessGame.Vue.ChessFrame;
import com.ChessGame.Vue.EvaluationPanel;
import com.ChessGame.Vue.SettingPanel;
import javax.swing.JScrollPane;

/**
 * Classe responsable de la boucle de jeu, exécutée dans un thread séparé
 */
public class JeuController implements Runnable {

    private Partie partie;
    private BoardPanel boardPanel;
    private ChessFrame frame;
    private EvaluationPanel evaluationPanel;
    private int moveCount = 1;
    private boolean estEvaluer = false;
    private boolean anotationEchec = true;
    private JScrollPane scrollPaneHistorique;
    private SettingPanel settingPanel;

    /**
     * Constructeur du JeuController
     * 
     * @param partie          La partie en cours à contrôler
     * @param boardPanel      Le panneau de jeu à mettre à jour après chaque coup
     * @param evaluationPanel Le panneau d'évaluation à mettre à jour après chaque
     *                        coup
     * @param frame           La fenêtre principale pour afficher les messages de
     *                        fin de partie
     */
    public JeuController(Partie partie, BoardPanel boardPanel, EvaluationPanel evaluationPanel, ChessFrame frame) {
        this.partie = partie;
        this.boardPanel = boardPanel;
        this.evaluationPanel = evaluationPanel;
        this.frame = frame;
    }

    /**
     * La boucle de jeu principale, qui attend les coups des joueurs et met à jour
     * la partie
     * jusqu'à ce que la partie soit terminée
     */
    @Override
    public void run() {
        SettingPanel menu = frame.getSettingPanel();

        menu.getItemJauge().addActionListener(e -> {
            boolean actif = menu.getItemJauge().isSelected();
            this.SetEstEvaluer(actif);
            frame.getEvaluationPanel().setVisible(actif);
            frame.pack();
        });

        menu.getItemNotation().addActionListener(e -> {
            boolean actif = menu.getItemNotation().isSelected();
            this.setAnotationEchec(actif);
            frame.getScrollPaneHistorique().setVisible(actif);
            frame.pack();
        });

        while (!partie.estTerminee()) {
            Joueur joueurCourant = partie.getJoueurCourant();
            try {
                Coup coup = joueurCourant.getCoup();
                if (this.anotationEchec) {
                    String notationCoup = genererNotation(coup, partie);
                    SwingUtilities.invokeLater(() -> frame.ajouterCoup(notationCoup));
                }

                partie.appliquerCoup(coup);
                partie.passerTour();
                SwingUtilities.invokeLater(() -> frame.getBoardPanel().repaint());
                if (this.estEvaluer) {
                    new Thread(() -> {
                        double scoreAvantage = partie.evaluerPositionAvecStockfish();
                        SwingUtilities.invokeLater(() -> frame.mettreAJourJauge(scoreAvantage));
                    }).start();
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                break;
            }
        }
        SwingUtilities.invokeLater(() -> {
            String message;
            if (partie.roiEnEchec(partie.getJoueurCourant())) {
                message = "Échec et mat ! " + (partie.getJoueurCourant().isWhite() ? "Les Noirs" : "Les Blancs")
                        + " ont gagné.";
            } else {
                message = "Match nul (Pat) !";
            }

            javax.swing.JOptionPane.showMessageDialog(boardPanel, message, "Fin de partie",
                    javax.swing.JOptionPane.INFORMATION_MESSAGE);
        });

    }

    /**
     * Génère la notation d'un coup pour l'afficher dans l'historique
     * 
     * @param coup   Le coup joué
     * @param partie La partie en cours, nécessaire pour vérifier les règles
     *               spéciales (ex: échec)
     * @return La notation du coup
     */
    private String genererNotation(Coup coup, Partie partie) {
        Piece piece = partie.getBoard().getPiece(coup.depart.x, coup.depart.y);
        Piece cible = partie.getBoard().getPiece(coup.arrivee.x, coup.arrivee.y);
        String symbol = piece.getSymbol() == 'p' ? "" : String.valueOf(piece.getSymbol());
        boolean isCapture = (cible != null);

        char colArrivee = (char) ('a' + coup.arrivee.x);
        int ligneArrivee = 8 - coup.arrivee.y;
        String caseArrivee = "" + colArrivee + ligneArrivee;

        String notation = symbol;
        if (isCapture) {
            // Règle spéciale : si un pion capture, on note sa colonne de départ (ex: exd5)
            if (symbol.equals("")) {
                notation += (char) ('a' + coup.depart.x);
            }
            notation += "x"; // Symbole de capture
        }
        notation += caseArrivee;

        // Note: Pour ajouter '+' (échec) ou '#' (mat), il faut le vérifier APRÈS avoir
        // joué le coup.

        // 5. Mise en page (Tour 1. Blancs Noirs)
        if (piece.getColor().equals(java.awt.Color.WHITE)) {
            String affichage = moveCount + ". " + String.format("%-8s", notation);
            moveCount++;
            return affichage;
        } else {
            return notation + "\n";
        }
    }

    /**
     * Permet de définir si l'évaluation de la position doit être affichée ou non
     * 
     * @param estEvaluer true pour afficher l'évaluation, false pour la masquer
     */
    public void SetEstEvaluer(boolean estEvaluer) {
        this.estEvaluer = estEvaluer;
    }

    /**
     * Permet de définir si les notations des coups doivent être affichées ou non
     * 
     * @param anotationEchec true pour afficher les notations, false pour les
     *                       masquer
     */
    public void setAnotationEchec(boolean anotationEchec) {
        this.anotationEchec = anotationEchec;
    }

    /**
     * Permet de savoir si les notations des coups sont affichées ou non
     * 
     * @return true si les notations sont affichées, false sinon
     */
    public boolean isAnotationEchec() {
        return anotationEchec;
    }

    /**
     * Permet de savoir si l'évaluation de la position est affichée ou non
     * 
     * @return true si l'évaluation est affichée, false sinon
     */
    public boolean isEstEvaluer() {
        return estEvaluer;
    }
}