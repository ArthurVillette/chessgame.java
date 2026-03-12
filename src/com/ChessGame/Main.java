package com.ChessGame;

import com.ChessGame.Controller.JeuController;
import com.ChessGame.Model.Board;
import com.ChessGame.Model.Partie;
import com.ChessGame.Vue.BoardPanel;
import com.ChessGame.Vue.ChessFrame;
import com.ChessGame.Vue.EvaluationPanel;
import com.ChessGame.Controller.ChessController;
import com.ChessGame.Vue.PromotionDialog;

import javax.swing.SwingUtilities;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

/**
 * Classe principale du jeu d'échecs
 */
public class Main {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {

            Board board = new Board();
            ChessFrame frame = new ChessFrame(board);
            Partie partie = new Partie(board);
            //BoardPanel boardPanel = new BoardPanel(board);
            EvaluationPanel evaluationPanel = new EvaluationPanel();

            //partie.addObserver(boardPanel);
            partie.addObserver(frame.getBoardPanel());

            PromotionDialog promotionDialog = new PromotionDialog(partie);

            ChessController controller = new ChessController(board, frame, partie);
            frame.getBoardPanel().addMouseListener(controller);

            JeuController jeuController = new JeuController(partie, frame.getBoardPanel(), evaluationPanel, frame);
            Thread threadJeu = new Thread(jeuController);
            threadJeu.setDaemon(true);
            threadJeu.start();
            frame.setVisible(true);

            System.out.println("Le moteur d'échecs est démarré !");

        });
    }
}