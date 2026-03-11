package com.ChessGame.Vue;

import javax.swing.JFrame;
import javax.swing.JTextArea;
import javax.swing.JScrollPane;
import javax.swing.BorderFactory;
import com.ChessGame.Model.Board;
import java.awt.BorderLayout;
import java.awt.Font;

/**
 * Classe représentant la fenêtre principale du jeu d'échecs
 */
public class ChessFrame extends JFrame {
    public static final int TILE_SIZE = 80;
    private BoardPanel boardPanel;
    private JTextArea historiqueArea;
    private EvaluationPanel evaluationPanel;

    /**
     * Constructeur de la fenêtre principale du jeu d'échecs
     * 
     * @param board Le modèle du plateau d'échecs à afficher dans la fenêtre
     */
    public ChessFrame(Board board) {
        setTitle("PoissonBloquer");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        this.evaluationPanel = new EvaluationPanel();
        add(evaluationPanel, BorderLayout.WEST);

        this.boardPanel = new BoardPanel(board);
        add(boardPanel, BorderLayout.CENTER);

        historiqueArea = new JTextArea(20, 15);
        historiqueArea.setEditable(false);
        historiqueArea.setFont(new Font("Monospaced", Font.PLAIN, 14));

        JScrollPane scrollPane = new JScrollPane(historiqueArea);
        scrollPane.setBorder(BorderFactory.createTitledBorder("Historique"));
        add(scrollPane, BorderLayout.EAST);

        pack();
        setLocationRelativeTo(null);
    }

    public BoardPanel getBoardPanel() {
        return boardPanel;
    }

    /**
     * Ajoute le texte du coup à l'historique
     */
    public void ajouterCoup(String texte) {
        historiqueArea.append(texte);
        historiqueArea.setCaretPosition(historiqueArea.getDocument().getLength());
    }

    public void mettreAJourJauge(double scoreCentipions) {
        evaluationPanel.setScore(scoreCentipions);
    }

}