package com.ChessGame.Vue;

import javax.swing.JFrame;
import javax.swing.JTextArea;
import javax.swing.JScrollPane;
import javax.swing.BorderFactory;
import com.ChessGame.Model.Board;
import java.awt.BorderLayout;
import java.awt.Font;
import com.ChessGame.Controller.JeuController;
import java.util.Set;

/**
 * Classe représentant la fenêtre principale du jeu d'échecs
 */
public class ChessFrame extends JFrame {
    public static final int TILE_SIZE = 80;
    private BoardPanel boardPanel;
    private JTextArea historiqueArea;
    private EvaluationPanel evaluationPanel;
    private JScrollPane scrollPaneHistorique;
    private SettingPanel settingPanel;

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
        this.evaluationPanel.setVisible(false);
        add(evaluationPanel, BorderLayout.WEST);

        this.boardPanel = new BoardPanel(board);
        add(boardPanel, BorderLayout.CENTER);

        historiqueArea = new JTextArea(20, 15);
        historiqueArea.setEditable(false);
        historiqueArea.setFont(new Font("Monospaced", Font.PLAIN, 14));

        JScrollPane scrollPane = new JScrollPane(historiqueArea);
        scrollPane.setBorder(BorderFactory.createTitledBorder("Historique"));
        add(scrollPane, BorderLayout.EAST);
        this.scrollPaneHistorique = scrollPane;

        this.settingPanel = new SettingPanel(this, evaluationPanel, scrollPaneHistorique);
        setJMenuBar(settingPanel);

        pack();
        setLocationRelativeTo(null);
    }

    /**
     * Getter pour le panneau de jeu
     * 
     * @return le BoardPanel utilisé pour afficher le plateau d'échecs
     */
    public BoardPanel getBoardPanel() {
        return boardPanel;
    }

    /**
     * Ajoute un coup à l'historique des coups affiché dans la fenêtre
     * 
     * @param texte le texte représentant le coup à ajouter à l'historique
     */
    public void ajouterCoup(String texte) {
        historiqueArea.append(texte);
        historiqueArea.setCaretPosition(historiqueArea.getDocument().getLength());
    }

    /**
     * Met à jour la jauge d'évaluation affichée dans la fenêtre en fonction du
     * score
     * 
     * @param scoreCentipions le score d'évaluation en centipions à afficher dans la
     *                        jauge d'évaluation
     */
    public void mettreAJourJauge(double scoreCentipions) {
        evaluationPanel.setScore(scoreCentipions);
    }

    /**
     * Getter pour le panneau d'évaluation
     * 
     * @return le EvaluationPanel utilisé pour afficher la jauge d'évaluation
     */
    public EvaluationPanel getEvaluationPanel() {
        return evaluationPanel;
    }

    /**
     * Getter pour le panneau de l'historique des coups
     * 
     * @return le JScrollPane contenant le JTextArea de l'historique des coups
     */
    public JScrollPane getScrollPaneHistorique() {
        return scrollPaneHistorique;
    }

    /**
     * Getter pour la barre de paramètres
     * 
     * @return le SettingPanel utilisé pour afficher les options d'affichage
     */
    public SettingPanel getSettingPanel() {
        return settingPanel;
    }

}