package com.ChessGame.Model.jeu;

import com.ChessGame.Model.ChessPieces.Bishop;
import com.ChessGame.Model.ChessPieces.King;
import com.ChessGame.Model.ChessPieces.Piece;
import com.ChessGame.Model.ChessPieces.Rook;
import com.ChessGame.Model.IA.Config;
import com.ChessGame.Model.IA.IAClient;
import com.ChessGame.Model.IA.JoueurIA;
import com.ChessGame.Model.plateau.Board;

import java.awt.*;
import java.util.List;
import java.util.Observable;

/**
 * Classe représentant une partie d'échecs, gérant les joueurs, le plateau et
 * les règles du jeu
 */
public class Partie extends Observable {
    private Joueur jBlanc;
    private Joueur jNoir;
    private Joueur joueurCourant;
    private Board board;
    private boolean stockfishActif;
    private Piece choixPromotion = null;
    private boolean villetteActif;
    private IAClient moteurStockfish;
    private IAClient moteurVillette;
    private boolean contreIA;
    private boolean humainEstBlanc;



    /**
     * Constructeur de la classe Partie
     * * @param board Le plateau de jeu à utiliser pour la partie
     * 
     * @param contreIA       Indique si la partie est contre l'IA
     * @param humainEstBlanc Indique si le joueur humain joue avec les pièces
     *                       blanches
     */
    public Partie(Board board, boolean contreIA, boolean humainEstBlanc) {
        this.board = board;
        this.contreIA = contreIA;
        this.humainEstBlanc = humainEstBlanc;
        this.moteurStockfish = new IAClient();
        this.stockfishActif = this.moteurStockfish.demarrerMoteur(Config.get("CHEMIN_STOCKFISH", "stockfish"));
        if (this.stockfishActif) {
            this.moteurStockfish.envoyerCommande("uci");
            this.moteurStockfish.lireReponseComplete("uciok");
        }

        if (this.contreIA) {
            this.moteurVillette = new IAClient();
            this.villetteActif = this.moteurVillette
                    .demarrerMoteur(Config.get("CHEMIN_VILLETTE", "./lanceur_villette.sh"));
            if (this.villetteActif) {
                this.moteurVillette.envoyerCommande("uci");
                this.moteurVillette.lireReponseComplete("uciok");
            }
        }

        if (!this.contreIA) {
            this.jBlanc = new Joueur(true);
            this.jNoir = new Joueur(false);
        } else {
            if (this.humainEstBlanc) {
                this.jBlanc = new Joueur(true);
                this.jNoir = new JoueurIA(false, this.moteurVillette, this);
            } else {
                this.jBlanc = new JoueurIA(true, this.moteurVillette, this);
                this.jNoir = new Joueur(false);
            }
        }

        this.joueurCourant = jBlanc;
    }

    /**
     * Getters pour accéder aux joueurs et au plateau de jeu
     */
    public Joueur getJoueurCourant() {
        return joueurCourant;
    }

    /**
     * Permet d'obtenir le plateau de jeu actuel
     * 
     * @return Le plateau de jeu utilisé dans la partie
     */
    public Board getBoard() {
        return board;
    }

    /**
     * Permet de passer le tour au joueur suivant
     */
    public void passerTour() {
        joueurCourant = (joueurCourant == jBlanc) ? jNoir : jBlanc;
    }

    public Joueur getJoueurBlanc() { return jBlanc; }
    public Joueur getJoueurNoir()  { return jNoir; }

    /**
     * Applique un coup sur le plateau, gère aussi :
     * - la prise en passant (supprime le pion capturé)
     * - le roque (déplace aussi la tour)
     * - la promotion (popup pour choisir la pièce)
     * - le flag aBouge sur Roi et Tour
     */
    /**
     * Applique un coup. Gère : prise en passant, roque, promotion.
     * Pour la promotion, notifie la Vue via Observer et attend son choix (wait/notify).
     */
    public void appliquerCoup(Coup coup) {
        Piece piece = board.getPiece(coup.depart.x, coup.depart.y);
        if (piece == null) return;

        // --- PRISE EN PASSANT ---
        if (piece instanceof Bishop.Pawn) {
            boolean captureEnDiagonale = (coup.arrivee.x != coup.depart.x);
            boolean caseArriveeVide    = (board.getPiece(coup.arrivee.x, coup.arrivee.y) == null);
            if (captureEnDiagonale && caseArriveeVide) {
                board.setPiece(coup.arrivee.x, coup.depart.y, null);
            }
        }

        // --- ROQUE ---
        if (piece instanceof King) {
            int deltaX = coup.arrivee.x - coup.depart.x;
            int y = coup.depart.y;
            if (deltaX == 2) {
                Piece tour = board.getPiece(7, y);
                board.setPiece(5, y, tour);
                board.setPiece(7, y, null);
                if (tour instanceof Rook) ((Rook) tour).setABouge();
            } else if (deltaX == -2) {
                Piece tour = board.getPiece(0, y);
                board.setPiece(3, y, tour);
                board.setPiece(0, y, null);
                if (tour instanceof Rook) ((Rook) tour).setABouge();
            }
            ((King) piece).setABouge();
        }

        if (piece instanceof Rook) ((Rook) piece).setABouge();

        // Déplacer la pièce
        board.setPiece(coup.arrivee.x, coup.arrivee.y, piece);
        board.setPiece(coup.depart.x, coup.depart.y, null);
        board.setDernierCoup(coup);

        // --- PROMOTION ---
        if (piece instanceof Bishop.Pawn) {
            int lignePromotion = piece.getColor().equals(Color.WHITE) ? 0 : 7;
            if (coup.arrivee.y == lignePromotion) {
                demanderPromotion(coup.arrivee.x, coup.arrivee.y, piece.getColor());
                return;
            }
        }

        setChanged();
        notifyObservers(new EvenementMouvement());
    }

    /**
     * Notifie PromotionDialog via EvenementPromotion, puis BLOQUE
     * jusqu'à ce que setChoixPromotion() soit appelé.
     * Même pattern que Joueur.getCoup().
     */
    private synchronized void demanderPromotion(int x, int y, Color couleur) {
        choixPromotion = null;

        // Notifier la Vue → PromotionDialog affiche la popup
        setChanged();
        notifyObservers(new EvenementPromotion(x, y, couleur));

        while (choixPromotion == null) {
            try { wait(); } catch (InterruptedException e) { Thread.currentThread().interrupt(); }
        }

        board.setPiece(x, y, choixPromotion);

        setChanged();
        notifyObservers(new EvenementMouvement());
    }

    /**
     * Appelé par PromotionDialog pour fournir la pièce choisie.
     * Débloque demanderPromotion().
     */
    public synchronized void setChoixPromotion(Piece piece) {
        this.choixPromotion = piece;
        notify();
    }

    /**
     * Vérifie si un coup est valide selon les règles du jeu
     * 
     * @param coup Le coup à vérifier
     * @return true si le coup est valide, false sinon
     */
    public boolean coupValide(Coup coup) {
        Piece piece = board.getPiece(coup.depart.x, coup.depart.y);
        if (piece == null)
            return false;

        List<Point> mouvementsPossibles = piece.mouvementsValides(coup.depart, board);
        if (!mouvementsPossibles.contains(coup.arrivee))
            return false;

        return !coupLaisseLeRoiEnEchec(coup);
    }

    /**
     * Vérifie si le roi est en échec
     * 
     * @param joueur Le joueur dont on veut vérifier si le roi est en échec
     * @return true si le roi est en échec, false sinon
     */
    public boolean roiEnEchec(Joueur joueur) {
        Point posRoi = null;
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                Piece p = board.getPiece(i, j);
                if (p instanceof King && p.getColor().equals(joueur.getCouleur())) {
                    posRoi = new Point(i, j);
                    break;
                }
            }
        }
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                Piece p = board.getPiece(i, j);
                if (p != null && !p.getColor().equals(joueur.getCouleur())) {
                    if (p.mouvementsValides(new Point(i, j), board).contains(posRoi)) {
                        return true;
                    }
                }
            }
        }

        return false;
    }

    /**
     * Vérifie si le roi est en échec et mat
     * 
     * @param joueur Le joueur dont on veut vérifier si le roi est en échec et mat
     * @return true si le roi est en échec et mat, false sinon
     */
    public boolean roiEnEchecEtMat(Joueur joueur) {
        if (!roiEnEchec(joueur)) {
            return false;
        }
        Point posRoi = null;
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                Piece p = board.getPiece(i, j);
                if (p instanceof King && p.getColor().equals(joueur.getCouleur())) {
                    posRoi = new Point(i, j);
                    break;
                }
            }
        }
        List<Point> mouvementsRoi = board.getPiece(posRoi.x, posRoi.y).mouvementsValides(posRoi, board);
        for (Point move : mouvementsRoi) {
            Board copieBoard = new Board(board);
            copieBoard.setPiece(move.x, move.y, copieBoard.getPiece(posRoi.x, posRoi.y));
            copieBoard.setPiece(posRoi.x, posRoi.y, null);
            Partie partieTest = new Partie(copieBoard, this.contreIA, this.humainEstBlanc);
            if (!partieTest.roiEnEchec(joueur)) {
                return false;
            }
        }
        return true;
    }

    /**
     * Vérifie si le joueur est en pat (aucun coup légal possible mais pas en échec)
     * 
     * @param joueur Le joueur dont on veut vérifier le pat
     * @return true si le joueur est en pat, false sinon
     */
    public boolean pat(Joueur joueur) {
        if (roiEnEchec(joueur)) {
            return false;
        }
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                Piece p = board.getPiece(i, j);
                if (p != null && p.getColor().equals(joueur.getCouleur())) {
                    List<Point> mouvements = p.mouvementsValides(new Point(i, j), board);
                    for (Point move : mouvements) {
                        Board copieBoard = new Board(board);
                        copieBoard.setPiece(move.x, move.y, copieBoard.getPiece(i, j));
                        copieBoard.setPiece(i, j, null);
                        Partie partieTest = new Partie(copieBoard, this.contreIA, this.humainEstBlanc);
                        if (!partieTest.roiEnEchec(joueur)) {
                            return false;
                        }
                    }
                }
            }
        }
        return true;
    }

    /**
     * Vérifie si un coup laisse le roi en échec après son application
     * 
     * @param coup Le coup à vérifier
     * @return true si le coup laisse le roi en échec, false sinon
     */
    public boolean coupLaisseLeRoiEnEchec(Coup coup) {
        // On sauvegarde l'état actuel pour le restaurer
        Piece pieceDepart = board.getPiece(coup.depart.x, coup.depart.y);
        Piece pieceArrivee = board.getPiece(coup.arrivee.x, coup.arrivee.y);

        // Simulation du mouvement
        board.setPiece(coup.arrivee.x, coup.arrivee.y, pieceDepart);
        board.setPiece(coup.depart.x, coup.depart.y, null);

        // On vérifie l'échec sur le joueur qui vient de simuler son coup
        boolean enEchec = roiEnEchec(joueurCourant);

        // Restauration du plateau (Backtracking)
        board.setPiece(coup.depart.x, coup.depart.y, pieceDepart);
        board.setPiece(coup.arrivee.x, coup.arrivee.y, pieceArrivee);

        return enEchec;
    }

    /**
     * Vérifie si la partie est terminée (échec et mat, pat, etc.)
     * 
     * @return true si la partie est terminée, false sinon
     */
    public boolean estTerminee() {
        if (roiEnEchecEtMat(jBlanc)) {
            System.out.println("Échec et mat ! Les noirs gagnent !");
            return true;
        } else if (roiEnEchecEtMat(jNoir)) {
            System.out.println("Échec et mat ! Les blancs gagnent !");
            return true;
        } else if (pat(jBlanc)) {
            System.out.println("Pat ! La partie est nulle !");
            return true;
        } else if (pat(jNoir)) {
            System.out.println("Pat ! La partie est nulle !");
            return true;
        }

        return false;
    }

    /**
     * Demande à Stockfish d'évaluer la position actuelle
     * 
     * @return L'évaluation en centipions (positif = Blancs gagnent)
     */
    public double evaluerPositionAvecStockfish() {
        if (!stockfishActif)
            return 0.0;
        boolean tourBlancs = joueurCourant.isWhite();
        String fen = board.toFEN(tourBlancs);
        System.out.println("FEN généré par le jeu : " + fen);

        moteurStockfish.envoyerCommande("position fen " + fen);
        moteurStockfish.envoyerCommande("go depth 10");
        String reponse = moteurStockfish.lireReponseComplete("bestmove");

        double scoreFinal = 0.0;
        String[] lignes = reponse.split("\n");
        for (String ligne : lignes) {
            if (ligne.contains("score cp")) {
                String[] mots = ligne.split(" ");
                for (int i = 0; i < mots.length; i++) {
                    if (mots[i].equals("cp")) {
                        scoreFinal = Double.parseDouble(mots[i + 1]);
                        if (!tourBlancs) {
                            scoreFinal = -scoreFinal;
                        }
                    }
                }
            } else if (ligne.contains("score mate")) {
                String[] mots = ligne.split(" ");
                for (int i = 0; i < mots.length; i++) {
                    if (mots[i].equals("mate")) {
                        int coupsAvantMat = Integer.parseInt(mots[i + 1]);
                        scoreFinal = (coupsAvantMat > 0) ? 10000.0 : -10000.0;
                        if (!tourBlancs)
                            scoreFinal = -scoreFinal;
                    }
                }
            }
        }
        System.out.println("Score centipions : " + scoreFinal);
        return scoreFinal;
    }
}