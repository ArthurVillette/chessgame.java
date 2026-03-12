package com.ChessGame.Model;

import java.awt.*;
import java.util.ArrayList;
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
    private List<String> historiqueCoups = new ArrayList<>();
    private boolean fichiersGeneres = false;

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

    /**
     * Applique un coup sur le plateau de jeu, en gérant les règles spéciales
     * (prise en passant, roque, promotion) et en notifiant les observateurs
     * 
     * @param coup Le coup à appliquer sur le plateau
     **/
    public void appliquerCoup(Coup coup) {
        Piece piece = board.getPiece(coup.depart.x, coup.depart.y);
        if (piece == null)
            return;

        // --- PRISE EN PASSANT ---
        if (piece instanceof Pawn) {
            boolean captureEnDiagonale = (coup.arrivee.x != coup.depart.x);
            boolean caseArriveeVide = (board.getPiece(coup.arrivee.x, coup.arrivee.y) == null);
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
                if (tour instanceof Rook)
                    ((Rook) tour).setABouge();
            } else if (deltaX == -2) {
                Piece tour = board.getPiece(0, y);
                board.setPiece(3, y, tour);
                board.setPiece(0, y, null);
                if (tour instanceof Rook)
                    ((Rook) tour).setABouge();
            }
            ((King) piece).setABouge();
        }

        if (piece instanceof Rook)
            ((Rook) piece).setABouge();

        // Déplacer la pièce
        board.setPiece(coup.arrivee.x, coup.arrivee.y, piece);
        board.setPiece(coup.depart.x, coup.depart.y, null);
        board.setDernierCoup(coup);

        char colDep = (char) ('a' + coup.depart.x);
        int ligDep = 8 - coup.depart.y;
        char colArr = (char) ('a' + coup.arrivee.x);
        int ligArr = 8 - coup.arrivee.y;

        String coupTexte = "" + colDep + ligDep + colArr + ligArr;
        historiqueCoups.add(coupTexte);

        // --- PROMOTION ---
        if (piece instanceof Pawn) {
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

        // Bloquer le thread du jeu jusqu'au choix du joueur
        while (choixPromotion == null) {
            try {
                wait();
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }

        // Placer la pièce choisie sur le plateau
        board.setPiece(x, y, choixPromotion);

        // Notifier le mouvement pour que BoardPanel repeigne avec la nouvelle pièce
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
     * * @param joueur Le joueur dont on veut vérifier si le roi est en échec et mat
     * 
     * @return true si le roi est en échec et mat, false sinon
     */
    public boolean roiEnEchecEtMat(Joueur joueur) {
        if (!roiEnEchec(joueur)) {
            return false;
        }
        for (int x = 0; x < 8; x++) {
            for (int y = 0; y < 8; y++) {
                Piece piece = board.getPiece(x, y);
                if (piece != null && piece.getColor().equals(joueur.getCouleur())) {
                    Point posDepart = new Point(x, y);
                    List<Point> mouvementsPossibles = piece.mouvementsValides(posDepart, board);

                    for (Point arrivee : mouvementsPossibles) {
                        Piece pieceMangee = board.getPiece(arrivee.x, arrivee.y);

                        board.setPiece(arrivee.x, arrivee.y, piece);
                        board.setPiece(posDepart.x, posDepart.y, null);

                        boolean sauveLeRoi = !roiEnEchec(joueur);

                        board.setPiece(posDepart.x, posDepart.y, piece);
                        board.setPiece(arrivee.x, arrivee.y, pieceMangee);
                        if (sauveLeRoi) {
                            return false;
                        }
                    }
                }
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

        for (int x = 0; x < 8; x++) {
            for (int y = 0; y < 8; y++) {
                Piece piece = board.getPiece(x, y);

                if (piece != null && piece.getColor().equals(joueur.getCouleur())) {
                    Point posDepart = new Point(x, y);
                    List<Point> mouvementsPossibles = piece.mouvementsValides(posDepart, board);
                    for (Point arrivee : mouvementsPossibles) {
                        Piece pieceMangee = board.getPiece(arrivee.x, arrivee.y);
                        board.setPiece(arrivee.x, arrivee.y, piece);
                        board.setPiece(posDepart.x, posDepart.y, null);
                        boolean coupLegalTrouve = !roiEnEchec(joueur);

                        board.setPiece(posDepart.x, posDepart.y, piece);
                        board.setPiece(arrivee.x, arrivee.y, pieceMangee);
                        if (coupLegalTrouve) {
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
            genererFichiersFinDePartie("0-1");
            return true;
        } else if (roiEnEchecEtMat(jNoir)) {
            System.out.println("Échec et mat ! Les blancs gagnent !");
            genererFichiersFinDePartie("1-0");
            return true;
        } else if (pat(jBlanc)) {
            System.out.println("Pat ! La partie est nulle !");
            genererFichiersFinDePartie("1/2-1/2");
            return true;
        } else if (pat(jNoir)) {
            System.out.println("Pat ! La partie est nulle !");
            genererFichiersFinDePartie("1/2-1/2");
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

    /**
     * Génère la fiche lisible (.txt) et le fichier d'importation web (.pgn)
     * pour la partie terminée, en utilisant l'historique des coups joués.
     * 
     * @param resultat Le résultat final de la partie ("1-0", "0-1", "1/2-1/2")
     */
    private void genererFichiersFinDePartie(String resultat) {
        if (fichiersGeneres)
            return;
        fichiersGeneres = true;

        try {
            String numeroPartie = String.format("%04d", new java.io.File("./historique/").listFiles().length + 1);
            java.io.FileWriter txtWriter = new java.io.FileWriter("./historique/fiche_coups_" + numeroPartie + ".txt");
            txtWriter.write("=== HISTORIQUE DE LA PARTIE ===\n\n");
            for (int i = 0; i < historiqueCoups.size(); i++) {
                if (i % 2 == 0) {
                    txtWriter.write(((i / 2) + 1) + ". Blanc : " + historiqueCoups.get(i) + " \t");
                } else {
                    txtWriter.write("Noir : " + historiqueCoups.get(i) + "\n");
                }
            }
            txtWriter.write("\nRésultat final : " + resultat);
            txtWriter.close();

            java.io.FileWriter pgnWriter = new java.io.FileWriter(
                    "./historique/partie_export_" + numeroPartie + ".pgn");
            java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("yyyy.MM.dd");
            String date = sdf.format(new java.util.Date());

            String nomBlanc = humainEstBlanc ? "Humain" : (contreIA ? "VILLETTE" : "Humain Noir");
            String nomNoir = !humainEstBlanc ? "Humain" : (contreIA ? "VILLETTE" : "Humain Noir");

            // En-têtes standards du format PGN
            pgnWriter.write("[Event \"Partie Locale vs Villette\"]\n");
            pgnWriter.write("[Site \"Mon Ordinateur\"]\n");
            pgnWriter.write("[Date \"" + date + "\"]\n");
            pgnWriter.write("[Round \"1\"]\n");
            pgnWriter.write("[White \"" + nomBlanc + "\"]\n");
            pgnWriter.write("[Black \"" + nomNoir + "\"]\n");
            pgnWriter.write("[Result \"" + resultat + "\"]\n\n");

            // Écriture des coups à la suite
            for (int i = 0; i < historiqueCoups.size(); i++) {
                if (i % 2 == 0)
                    pgnWriter.write(((i / 2) + 1) + ". ");
                pgnWriter.write(historiqueCoups.get(i) + " ");
            }
            pgnWriter.write(resultat);
            pgnWriter.close();

            System.out.println(
                    "✅ Fichiers générés avec succès : 'fiche_coups_" + numeroPartie + ".txt' et 'partie_export_"
                            + numeroPartie + ".pgn'");

        } catch (Exception e) {
            System.err.println("Erreur lors de la sauvegarde : " + e.getMessage());
        }
    }
}