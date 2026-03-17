package com.ChessGame;

import com.ChessGame.Controller.JeuController;
import com.ChessGame.Model.Board;
import com.ChessGame.Model.Partie;
import com.ChessGame.Vue.BoardPanel;
import com.ChessGame.Vue.ChessFrame;
import com.ChessGame.Vue.EvaluationPanel;
import com.ChessGame.Vue.MenuDemarrage;
import com.ChessGame.Controller.ChessController;
import com.ChessGame.Vue.PromotionDialog;

import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;

/**
 * Classe principale du jeu d'échecs
 */
public class Main {
    public static void main(String[] args) {

        SwingUtilities.invokeLater(() -> {

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

        });
    }
}