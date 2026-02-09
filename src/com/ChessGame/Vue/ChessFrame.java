package com.ChessGame.Vue;
import javax.swing.JFrame;
import com.ChessGame.Model.Board;
import java.awt.BorderLayout;

/**
 * Classe représentant la fenêtre principale du jeu d'échecs
 */
public class ChessFrame extends JFrame {
    public static final int TILE_SIZE = 80;
    private BoardPanel boardPanel;

    /**
     * Constructeur de la fenêtre principale du jeu d'échecs
     * @param board Le modèle du plateau d'échecs à afficher dans la fenêtre
     */
    public ChessFrame(Board board) {
        setTitle("PoissonBloquer");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        this.boardPanel = new BoardPanel(board);
        add(boardPanel, BorderLayout.CENTER);

        pack(); 
        setLocationRelativeTo(null);
    }

    public BoardPanel getBoardPanel() {
        return boardPanel;
    }
}
