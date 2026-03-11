package com.ChessGame;

import com.ChessGame.Controller.JeuController;
import com.ChessGame.Model.Board;
import com.ChessGame.Model.Partie;
import com.ChessGame.Vue.BoardPanel;
import com.ChessGame.Vue.ChessFrame;
import com.ChessGame.Vue.EvaluationPanel;
import com.ChessGame.Vue.MenuDemarrage; // N'oubliez pas l'import du menu !
import com.ChessGame.Controller.ChessController;
import javax.swing.SwingUtilities;

/**
 * Classe principale du jeu d'échecs
 */
public class Main {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {

            MenuDemarrage menu = new MenuDemarrage();
            menu.setVisible(true);
            if (!menu.isStartClicked()) {
                System.out.println("Fermeture du jeu.");
                System.exit(0);
            }

            boolean contreIA = menu.isContreIA();
            boolean humainEstBlanc = menu.isHumainEstBlanc();

            Board board = new Board();
            ChessFrame frame = new ChessFrame(board);

            Partie partie = new Partie(board, contreIA, humainEstBlanc);

            BoardPanel boardPanel = frame.getBoardPanel();
            EvaluationPanel evaluationPanel = new EvaluationPanel();

            partie.addObserver(boardPanel);

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