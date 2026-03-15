package com.ChessGame;

import com.ChessGame.Controller.JeuController;
import com.ChessGame.Model.plateau.Board;
import com.ChessGame.Model.jeu.Partie;
import com.ChessGame.Vue.*;
import com.ChessGame.Controller.ChessController;

import javax.swing.SwingUtilities;

/**
 * Classe principale du jeu d'échecs
 */
public class Main {

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {

            // 1. Splash screen animé — lance lancerJeu() quand il se termine
            SplashScreen splash = new SplashScreen(() ->
                    SwingUtilities.invokeLater(Main::lancerJeu)
            );
            splash.demarrer();
        });
    }

    /**
     * Lance le menu de démarrage puis initialise la partie
     * (même logique que ton Main original), relancable depuis nouvelle partie
     */
    private static void lancerJeu() {
        MenuDemarrage menu = new MenuDemarrage();
        menu.setVisible(true);

        if (!menu.isStartClicked()) {
            System.out.println("Fermeture du jeu.");
            System.exit(0);
        }

        boolean contreIA       = menu.isContreIA();
        boolean humainEstBlanc = menu.isHumainEstBlanc();
        String  nomJoueur      = menu.getNomJoueur();   // ← nom du joueur
        int     timerMinutes   = menu.getTimerMinutes();
        String nomBlanc, nomNoir;
        if (!contreIA) {
            // Humain vs Humain : joueur 1 = Blancs, joueur 2 = Noirs
            nomBlanc = nomJoueur;
            nomNoir  = "Joueur 2";
        } else if (humainEstBlanc) {
            nomBlanc = nomJoueur;
            nomNoir  = "Villette IA";
        } else {
            nomBlanc = "Villette IA";
            nomNoir  = nomJoueur;
        }
        Board board            = new Board();
        ChessFrame frame       = new ChessFrame(board, nomBlanc, nomNoir, timerMinutes);
        Partie partie          = new Partie(board, contreIA, humainEstBlanc);

        BoardPanel boardPanel           = frame.getBoardPanel();
        //EvaluationPanel evaluationPanel = new EvaluationPanel();

        partie.addObserver(frame.getBoardPanel());
        frame.getBoardPanel().setPartie(partie);
        PromotionDialog promotionDialog = new PromotionDialog(partie);

        ChessController controller = new ChessController(board, frame, partie);
        frame.getBoardPanel().addMouseListener(controller);

        // Timer initialisé depuis le choix du menu
        JeuController jeuController = new JeuController(
                partie, frame.getBoardPanel(),
                frame.getEvaluationPanel(), frame,
                nomBlanc, nomNoir, timerMinutes   // ← passe timer + noms
        );



        // Callback "Nouvelle partie" → ferme cette fenêtre et relance tout
        frame.setOnNouvellePartie(() -> {
            frame.dispose();
            SwingUtilities.invokeLater(Main::lancerJeu);
        });
        // Passer ce callback aussi au JeuController (pour les popups)
        jeuController.setOnNouvellePartie(() -> {
            frame.dispose();
            SwingUtilities.invokeLater(Main::lancerJeu);
        });

        // Si timer > 0, afficher immédiatement le temps initial
        if (timerMinutes > 0) {
            frame.setTempsJoueur(true,  timerMinutes * 60);
            frame.setTempsJoueur(false, timerMinutes * 60);
        }

        //JeuController jeuController = new JeuController(partie, boardPanel, evaluationPanel, frame);
        Thread threadJeu = new Thread(jeuController);
        threadJeu.setDaemon(true);
        threadJeu.start();

        // Retourner l'échiquier si le joueur joue les Noirs
        if (!humainEstBlanc) {
            frame.getBoardPanel().setRetourne(true);
        }

        frame.setVisible(true);
        System.out.println("Le jeu d'échecs est démarré avec succès !");
    }}
