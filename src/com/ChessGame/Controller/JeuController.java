package com.ChessGame.Controller;

import com.ChessGame.Model.jeu.Coup;
import com.ChessGame.Model.jeu.Joueur;
import com.ChessGame.Model.jeu.Partie;
import com.ChessGame.Model.ChessPieces.Piece;
import com.ChessGame.Vue.BoardPanel;
import javax.swing.SwingUtilities;
import com.ChessGame.Vue.ChessFrame;
import com.ChessGame.Vue.EvaluationPanel;
import com.ChessGame.Vue.SettingPanel;
import com.ChessGame.Vue.PopupFinPartie;

/**
 * Classe responsable de la boucle de jeu, exécutée dans un thread séparé
 */
public class JeuController implements Runnable {

    private final Partie partie;
    private final BoardPanel boardPanel;
    private final ChessFrame frame;
    private final EvaluationPanel evaluationPanel;
    private int moveCount = 1;
    private boolean estEvaluer = false;
    private boolean anotationEchec = true;

    // ── Timer ─────────────────────────────────────────────────────
    private final int timerInitial;
    private int tempsRestantBlanc;
    private int tempsRestantNoir;
    private javax.swing.Timer tickTimer;
    private boolean enPause = false;
    private volatile boolean partieTerminee = false;
    private Runnable onNouvellePartie;

    // Noms des joueurs (pour popups)
    private final String nomBlanc;
    private final String nomNoir;

    public JeuController(Partie partie, BoardPanel boardPanel,
            EvaluationPanel evaluationPanel, ChessFrame frame,
            String nomBlanc, String nomNoir, int timerMinutes) {
        this.partie = partie;
        this.boardPanel = boardPanel;
        this.evaluationPanel = evaluationPanel;
        this.frame = frame;
        this.nomBlanc = nomBlanc;
        this.nomNoir = nomNoir;
        this.timerInitial = timerMinutes * 60;
        this.tempsRestantBlanc = this.timerInitial;
        this.tempsRestantNoir = this.timerInitial;
    }

    /**
     * La boucle de jeu principale, qui attend les coups des joueurs et met à jour
     * la partie
     * jusqu'à ce que la partie soit terminée
     */
    @Override
    public void run() {

        // ── Connecter les callbacks Vue → Contrôleur ──────────────
        frame.setOnForfait(this::gererForfait);
        frame.setOnPause(this::togglePause);

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
        frame.setOnJaugeToggle(() -> {
            boolean actif = frame.getEvaluationPanel().isVisible();
            this.SetEstEvaluer(actif);
        });

        // ── Démarrer le timer si configuré ────────────────────────
        if (timerInitial > 0)
            demarrerTick();

        while (!partieTerminee && !partie.estTerminee()) {
            Joueur joueurCourant = partie.getJoueurCourant();
            try {
                Coup coup = joueurCourant.getCoup();
                if (partieTerminee)
                    break;

                // Arrêter le tick pendant qu'on traite
                if (tickTimer != null)
                    SwingUtilities.invokeLater(() -> tickTimer.stop());
                //
                Piece pieceCaptured = partie.getBoard().getPiece(coup.arrivee.x, coup.arrivee.y);
                boolean parLesBlancs = joueurCourant.isWhite();

                if (this.anotationEchec) {
                    String notationCoup = genererNotation(coup, partie);
                    SwingUtilities.invokeLater(() -> frame.ajouterCoup(notationCoup));
                }

                partie.appliquerCoup(coup);
                partie.passerTour();
                SwingUtilities.invokeLater(() -> frame.getBoardPanel().repaint());

                // . Afficher la pièce capturée dans le bon PlayerInfoPanel ─
                if (pieceCaptured != null) {
                    SwingUtilities.invokeLater(() -> frame.ajouterPieceCaptured(pieceCaptured, parLesBlancs));
                }

                if (this.estEvaluer) {
                    new Thread(() -> {
                        double scoreAvantage = partie.evaluerPositionAvecStockfish();
                        SwingUtilities.invokeLater(() -> frame.mettreAJourJauge(scoreAvantage));
                    }).start();
                }
                // Reprendre le tick pour le prochain joueur
                if (timerInitial > 0 && !enPause)
                    SwingUtilities.invokeLater(() -> tickTimer.start());

            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                break;
            }
        }

        if (!partieTerminee)
            afficherFinPartie();

    }

    // ── Timer tick (1 seconde) ────────────────────────────────────

    private void demarrerTick() {
        tickTimer = new javax.swing.Timer(1000, e -> {
            if (enPause || partieTerminee)
                return;
            boolean tourBlanc = partie.getJoueurCourant().isWhite();
            if (tourBlanc) {
                tempsRestantBlanc--;
                frame.setTempsJoueur(true, tempsRestantBlanc);
                if (tempsRestantBlanc <= 0)
                    gererTimeout(false); // Noir gagne
            } else {
                tempsRestantNoir--;
                frame.setTempsJoueur(false, tempsRestantNoir);
                if (tempsRestantNoir <= 0)
                    gererTimeout(true); // Blanc gagne
            }
        });
        tickTimer.start();
    }

    private void togglePause() {
        enPause = !enPause;
        if (enPause) {
            if (tickTimer != null)
                tickTimer.stop();
        } else {
            if (tickTimer != null && timerInitial > 0)
                tickTimer.start();
        }
    }

    private void gererTimeout(boolean blancGagne) {
        partieTerminee = true;
        if (tickTimer != null)
            tickTimer.stop();
        // Débloquer le thread bloqué sur getCoup()
        partie.getJoueurCourant().setCoup(new com.ChessGame.Model.jeu.Coup(
                new java.awt.Point(0, 0), new java.awt.Point(0, 0)));
        String gagnant = blancGagne ? nomBlanc : nomNoir;
        if (blancGagne) {
            partie.genererFichiersFinDePartie("1-0");
        } else {
            partie.genererFichiersFinDePartie("0-1");
        }
        PopupFinPartie.afficher(frame, gagnant, PopupFinPartie.TypeFin.TIMEOUT, onNouvellePartie);
    }

    private void gererForfait() {
        partieTerminee = true;
        if (tickTimer != null)
            tickTimer.stop();
        boolean estBlanc = partie.getJoueurCourant().isWhite();
        String gagnant = estBlanc ? nomNoir : nomBlanc; // l'adversaire gagne
        // Débloquer le thread bloqué sur getCoup()
        partie.getJoueurCourant().setCoup(new com.ChessGame.Model.jeu.Coup(
                new java.awt.Point(0, 0), new java.awt.Point(0, 0)));
        // PopupFinPartie.afficher(frame, gagnant, PopupFinPartie.TypeFin.FORFAIT,
        // null);
        PopupFinPartie.afficher(frame, gagnant, PopupFinPartie.TypeFin.FORFAIT, onNouvellePartie);
    }

    private void afficherFinPartie() {
        if (tickTimer != null)
            tickTimer.stop();
        SwingUtilities.invokeLater(() -> {
            String gagnant;
            PopupFinPartie.TypeFin type;
            if (partie.roiEnEchec(partie.getJoueurCourant())) {
                type = PopupFinPartie.TypeFin.ECHEC_MAT;
                gagnant = partie.getJoueurCourant().isWhite() ? nomNoir : nomBlanc;
            } else {
                type = PopupFinPartie.TypeFin.PAT;
                gagnant = null;
            }
            PopupFinPartie p = new PopupFinPartie(frame, gagnant, type, onNouvellePartie);
            p.setVisible(true);
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
    /**
     * Génère la notation algébrique standard d'un coup
     */
    private String genererNotation(Coup coup, Partie partie) {
        Piece piece = partie.getBoard().getPiece(coup.depart.x, coup.depart.y);
        Piece cible = partie.getBoard().getPiece(coup.arrivee.x, coup.arrivee.y);

        char rawSymbol = piece.getSymbol();
        boolean estUnPion = Character.toLowerCase(rawSymbol) == 'p';

        String symbol = estUnPion ? "" : String.valueOf(Character.toUpperCase(rawSymbol));
        if (Character.toLowerCase(rawSymbol) == 'k') {
            int deltaX = coup.arrivee.x - coup.depart.x;
            if (deltaX == 2) {
                return formaterSortie("O-O", piece.getColor().equals(java.awt.Color.WHITE));
            } else if (deltaX == -2) {
                return formaterSortie("O-O-O", piece.getColor().equals(java.awt.Color.WHITE));
            }
        }

        boolean isCapture = (cible != null);
        if (estUnPion && coup.depart.x != coup.arrivee.x && cible == null) {
            isCapture = true;
        }

        char colDepart = (char) ('a' + coup.depart.x);
        char colArrivee = (char) ('a' + coup.arrivee.x);
        int ligneArrivee = 8 - coup.arrivee.y;
        String caseArrivee = "" + colArrivee + ligneArrivee;

        String notation = symbol;

        if (isCapture) {
            if (estUnPion) {
                notation += colDepart;
            }
            notation += "x";
        }

        notation += caseArrivee;

        if (estUnPion && (coup.arrivee.y == 0 || coup.arrivee.y == 7)) {
            notation += "=Q";
        }

        return formaterSortie(notation, piece.getColor().equals(java.awt.Color.WHITE));
    }

    /**
     * Petite méthode utilitaire pour éviter de répéter le code d'affichage
     */
    private String formaterSortie(String notation, boolean estBlanc) {
        if (estBlanc) {
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

    public void setOnNouvellePartie(Runnable r) {
        this.onNouvellePartie = r;
    }
}