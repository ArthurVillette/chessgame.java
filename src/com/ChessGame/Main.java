package com.ChessGame;

import com.ChessGame.Controller.JeuController;
<<<<<<< HEAD
import com.ChessGame.Model.Board;
import com.ChessGame.Model.Partie;
import com.ChessGame.Vue.BoardPanel;
import com.ChessGame.Vue.ChessFrame;
import com.ChessGame.Vue.EvaluationPanel;
import com.ChessGame.Vue.MenuDemarrage;
=======
import com.ChessGame.Model.plateau.Board;
import com.ChessGame.Model.jeu.Partie;
import com.ChessGame.Vue.*;
>>>>>>> main
import com.ChessGame.Controller.ChessController;

import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;

/**
 * Classe principale du jeu d'échecs
 */
public class Main {

    public static void main(String[] args) {

        SwingUtilities.invokeLater(() -> {

<<<<<<< HEAD
            String[] options = {
                    "Jouer Localement (ou vs IA)",
                    "Héberger une partie (Réseau)",
                    "Rejoindre une partie (Réseau)"
            };

            int choix = JOptionPane.showOptionDialog(null,
                    "Choisissez votre mode de jeu :",
                    "Menu Principal - Échecs",
                    JOptionPane.DEFAULT_OPTION,
                    JOptionPane.QUESTION_MESSAGE,
                    null, options, options[0]);

            if (choix == -1) {
                System.out.println("Fermeture du jeu.");
                System.exit(0);
            }

            boolean contreIA = false;
            boolean humainEstBlanc = true;
            boolean enReseau = false;
            boolean estHote = false;
            String ipAdversaire = "localhost";

            if (choix == 0) {
                MenuDemarrage menu = new MenuDemarrage();
                menu.setVisible(true);

                if (!menu.isStartClicked()) {
                    System.out.println("Fermeture du jeu.");
                    System.exit(0);
                }

                contreIA = menu.isContreIA();
                humainEstBlanc = menu.isHumainEstBlanc();
            } else if (choix == 1) {
                enReseau = true;
                estHote = true;
                humainEstBlanc = true;
                JOptionPane.showMessageDialog(null,
                        "Vous allez héberger la partie.\nVotre port est le 5000.\nDonnez votre adresse IP (locale) à votre adversaire !");
            } else if (choix == 2) {
                // MODE CLIENT (Rejoindre)
                enReseau = true;
                estHote = false;
                humainEstBlanc = false;
                ipAdversaire = JOptionPane.showInputDialog(null, "Entrez l'adresse IP de l'hôte (ex: 192.168.1.15) :",
                        "localhost");

                if (ipAdversaire == null || ipAdversaire.trim().isEmpty()) {
                    System.exit(0);
                }
            }

            Board board = new Board();
            ChessFrame frame = new ChessFrame(board);

            Partie partie = new Partie(board, contreIA, humainEstBlanc);

            if (enReseau) {
                partie.activerReseau(estHote, ipAdversaire, 5000);
            }

            BoardPanel boardPanel = frame.getBoardPanel();
            EvaluationPanel evaluationPanel = new EvaluationPanel();

            partie.addObserver(frame.getBoardPanel());

            PromotionDialog promotionDialog = new PromotionDialog(partie);

            ChessController controller = new ChessController(board, frame, partie);
            frame.getBoardPanel().addMouseListener(controller);

            JeuController jeuController = new JeuController(partie, frame.getBoardPanel(), evaluationPanel, frame);
            Thread threadJeu = new Thread(jeuController);
            threadJeu.setDaemon(true);
            threadJeu.start();

            frame.setVisible(true);

            System.out.println("Le jeu d'échecs est démarré avec succès !");

=======
            // 1. Splash screen animé — lance lancerJeu() quand il se termine
            SplashScreen splash = new SplashScreen(() ->
                    SwingUtilities.invokeLater(Main::lancerJeu)
            );
            splash.demarrer();
>>>>>>> main
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
        frame.setChessController(controller);
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
