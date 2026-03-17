package com.ChessGame.Model.jeu;

import com.ChessGame.Model.ChessPieces.Bishop;
import com.ChessGame.Model.ChessPieces.King;
import com.ChessGame.Model.ChessPieces.Piece;
import com.ChessGame.Model.ChessPieces.Rook;
import com.ChessGame.Model.ChessPieces.Queen;
import com.ChessGame.Model.ChessPieces.Knight;
import com.ChessGame.Model.IA.Config;
import com.ChessGame.Model.IA.IAClient;
import com.ChessGame.Model.IA.JoueurIA;
import com.ChessGame.Model.plateau.Board;
import com.ChessGame.Model.AnalyseurPartie;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Observable;

import com.ChessGame.Network.ReseauManager;

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
    private List<String> historiquePGN = new ArrayList<>();
    private ReseauManager reseauManager;
    private boolean estEnReseau = false;

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

    public Joueur getJoueurBlanc() {
        return jBlanc;
    }

    public Joueur getJoueurNoir() {
        return jNoir;
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
     * Applique un coup sur le plateau de jeu
     * 
     * @param coup Le coup à appliquer
     */
    public void appliquerCoup(Coup coup) {
        Piece piece = board.getPiece(coup.depart.x, coup.depart.y);
        if (piece == null)
            return;

        char colDep = (char) ('a' + coup.depart.x);
        int ligDep = 8 - coup.depart.y;
        char colArr = (char) ('a' + coup.arrivee.x);
        int ligArr = 8 - coup.arrivee.y;
        historiqueCoups.add("" + colDep + ligDep + colArr + ligArr);

        historiquePGN.add(genererNotationPGN(coup));
        if (estEnReseau && joueurCourant instanceof Joueur) {
            reseauManager.envoyerCoup(historiqueCoups.get(historiqueCoups.size() - 1));
        }

        // --- PRISE EN PASSANT ---
        if (piece instanceof Bishop.Pawn) {
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

        // --- PROMOTION ---
        if (piece instanceof Bishop.Pawn) {
            int lignePromotion = piece.getColor().equals(Color.WHITE) ? 0 : 7;
            if (coup.arrivee.y == lignePromotion) {

                demanderPromotion(coup.arrivee.x, coup.arrivee.y, piece.getColor());

                String lettrePromo = "q";
                if (choixPromotion instanceof Rook)
                    lettrePromo = "r";
                else if (choixPromotion instanceof Bishop)
                    lettrePromo = "b";
                else if (choixPromotion instanceof Knight)
                    lettrePromo = "n";

                int indexCoups = historiqueCoups.size() - 1;
                historiqueCoups.set(indexCoups, historiqueCoups.get(indexCoups) + lettrePromo);

                int indexPGN = historiquePGN.size() - 1;
                historiquePGN.set(indexPGN, historiquePGN.get(indexPGN) + "=" + lettrePromo.toUpperCase());

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
            try {
                wait();
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
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
     * * @param resultat Le résultat final de la partie ("1-0", "0-1", "1/2-1/2")
     */
    public void genererFichiersFinDePartie(String resultat) {
        if (fichiersGeneres)
            return;
        fichiersGeneres = true;

        try {
            String numeroPartie = String.format("%04d", new java.io.File("./historique/").listFiles().length + 1);
            java.io.FileWriter txtWriter = new java.io.FileWriter("./historique/fiche_coups_" + numeroPartie + ".txt");
            txtWriter.write("=== HISTORIQUE DE LA PARTIE ===\n\n");

            // MODIFICATION 1 : On utilise historiquePGN pour que le .txt soit lisible par
            // un humain
            for (int i = 0; i < historiquePGN.size(); i++) {
                if (i % 2 == 0) {
                    txtWriter.write(((i / 2) + 1) + ". Blanc : " + historiquePGN.get(i) + " \t");
                } else {
                    txtWriter.write("Noir : " + historiquePGN.get(i) + "\n");
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

            // MODIFICATION 2 : On utilise historiquePGN pour que Chess.com puisse lire le
            // fichier
            for (int i = 0; i < historiquePGN.size(); i++) {
                if (i % 2 == 0)
                    pgnWriter.write(((i / 2) + 1) + ". ");
                pgnWriter.write(historiquePGN.get(i) + " ");
            }
            pgnWriter.write(resultat);
            pgnWriter.close();

            System.out.println(
                    "✅ Fichiers générés avec succès : 'fiche_coups_" + numeroPartie + ".txt' et 'partie_export_"
                            + numeroPartie + ".pgn'");
            String cheminStockfish = Config.get("CHEMIN_STOCKFISH", "stockfish");
            AnalyseurPartie analyseur = new AnalyseurPartie(historiqueCoups, cheminStockfish);
            new Thread(analyseur).start();

        } catch (Exception e) {
            System.err.println("Erreur lors de la sauvegarde : " + e.getMessage());
        }
    }

    /**
     * Génère la vraie notation PGN internationale pour les sites d'échecs
     * 
     * @param coup Le coup à convertir en notation PGN
     * @return La notation PGN du coup
     */
    private String genererNotationPGN(Coup coup) {
        Piece piece = board.getPiece(coup.depart.x, coup.depart.y);
        Piece cible = board.getPiece(coup.arrivee.x, coup.arrivee.y);

        char rawSymbol = piece.getSymbol();
        boolean estUnPion = Character.toLowerCase(rawSymbol) == 'p';
        String symbol = estUnPion ? "" : String.valueOf(Character.toUpperCase(rawSymbol));

        // 1. Gestion du Roque
        if (Character.toLowerCase(rawSymbol) == 'k') {
            if (coup.arrivee.x - coup.depart.x == 2)
                return "O-O";
            if (coup.arrivee.x - coup.depart.x == -2)
                return "O-O-O";
        }

        // 2. GESTION DE L'AMBIGUÏTÉ (Deux pièces identiques ciblent la même case)
        String desambiguation = "";
        if (!estUnPion && Character.toLowerCase(rawSymbol) != 'k') {
            boolean autrePeutVenir = false;
            boolean memeColonne = false;
            boolean memeLigne = false;

            for (int x = 0; x < 8; x++) {
                for (int y = 0; y < 8; y++) {
                    if (x == coup.depart.x && y == coup.depart.y)
                        continue;

                    Piece autre = board.getPiece(x, y);
                    // Si c'est la même pièce (ex: un autre Cavalier Blanc)
                    if (autre != null && autre.getSymbol() == rawSymbol && autre.getColor().equals(piece.getColor())) {
                        // S'il peut aussi aller sur la case d'arrivée
                        if (autre.mouvementsValides(new java.awt.Point(x, y), board).contains(coup.arrivee)) {
                            autrePeutVenir = true;
                            if (x == coup.depart.x)
                                memeColonne = true;
                            if (y == coup.depart.y)
                                memeLigne = true;
                        }
                    }
                }
            }

            if (autrePeutVenir) {
                if (!memeColonne) {
                    desambiguation += (char) ('a' + coup.depart.x); // ex: Nfe5
                } else if (!memeLigne) {
                    desambiguation += (8 - coup.depart.y); // ex: N3e5
                } else {
                    desambiguation += (char) ('a' + coup.depart.x) + "" + (8 - coup.depart.y);
                }
            }
        }

        boolean isCapture = (cible != null);

        // 3. Gestion de la Prise en passant
        if (estUnPion && coup.depart.x != coup.arrivee.x && cible == null) {
            isCapture = true;
        }

        char colDepart = (char) ('a' + coup.depart.x);
        char colArrivee = (char) ('a' + coup.arrivee.x);
        int ligneArrivee = 8 - coup.arrivee.y;

        String notation = symbol + desambiguation;

        if (isCapture) {
            if (estUnPion)
                notation += colDepart; // ex: "e" pour "exd5"
            notation += "x";
        }
        notation += "" + colArrivee + ligneArrivee;

        // 5. GESTION DES ÉCHECS ET DES MATS (+ et #)
        // On simule le coup à l'avance pour voir si le roi adverse tremble
        board.setPiece(coup.arrivee.x, coup.arrivee.y, piece);
        board.setPiece(coup.depart.x, coup.depart.y, null);

        Joueur adversaire = piece.getColor().equals(java.awt.Color.WHITE) ? jNoir : jBlanc;

        if (roiEnEchecEtMat(adversaire)) {
            notation += "#";
        } else if (roiEnEchec(adversaire)) {
            notation += "+";
        }

        // On annule la simulation et on remet les pièces à leur place
        board.setPiece(coup.depart.x, coup.depart.y, piece);
        board.setPiece(coup.arrivee.x, coup.arrivee.y, cible);

        return notation;
    }

    /**
     * PC 1 : Héberge la partie et attend la connexion du PC 2
     * 
     * @param reseauManager Le gestionnaire de réseau pour communiquer avec l'autre
     *                      joueur
     */
    public void recevoirCoupReseau(String coupTexte) {
        // coupTexte ressemble à "e2e4" ou "e7e8q"
        int depX = coupTexte.charAt(0) - 'a';
        int depY = 8 - Character.getNumericValue(coupTexte.charAt(1));
        int arrX = coupTexte.charAt(2) - 'a';
        int arrY = 8 - Character.getNumericValue(coupTexte.charAt(3));

        java.awt.Point depart = new java.awt.Point(depX, depY);
        java.awt.Point arrivee = new java.awt.Point(arrX, arrY);

        Coup coup = new Coup(depart, arrivee);

        if (coupTexte.length() == 5) {
            char promo = coupTexte.charAt(4);
            Piece nouvellePiece = null;
            if (promo == 'q')
                nouvellePiece = new Queen(joueurCourant.getCouleur());
            this.choixPromotion = nouvellePiece;
        }

        // On applique le coup reçu
        appliquerCoup(coup);
        passerTour();
    }

    /**
     * Active le mode réseau après la création de la partie
     */
    public void activerReseau(boolean estHote, String ip, int port) {
        this.estEnReseau = true;
        this.reseauManager = new ReseauManager();

        if (estHote) {
            System.out.println("Démarrage du Serveur...");
            // L'hôte écoute sur le port. Quand un message arrive, on joue le coup.
            reseauManager.hebergerPartie(port, coupTexte -> recevoirCoupReseau(coupTexte));
        } else {
            System.out.println("Connexion au Serveur...");
            // Le client se connecte à l'IP. Quand un message arrive, on joue le coup.
            reseauManager.rejoindrePartie(ip, port, coupTexte -> recevoirCoupReseau(coupTexte));
        }
    }
}