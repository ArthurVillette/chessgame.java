package com.ChessGame.Vue;
import javax.swing.JFrame;
import com.ChessGame.Model.Board;
import java.awt.BorderLayout;

public class ChessFrame extends JFrame {
    public static final int TILE_SIZE = 80;
    private BoardPanel boardPanel;

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
