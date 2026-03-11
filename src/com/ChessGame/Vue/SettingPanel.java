package com.ChessGame.Vue;

import javax.swing.JCheckBoxMenuItem;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JScrollPane;
import com.ChessGame.Controller.JeuController;

/**
 * Classe représentant la barre de paramètres du jeu d'échecs
 */
public class SettingPanel extends JMenuBar {
    private EvaluationPanel evaluationPanel;
    private JScrollPane scrollPaneHistorique;
    private JFrame parentFrame;
    private JCheckBoxMenuItem itemJauge;
    private JCheckBoxMenuItem itemNotation;

    /**
     * Constructeur de la barre de paramètres du jeu d'échecs
     * 
     * @param parentFrame          La fenêtre principale du jeu d'échecs
     * @param evaluationPanel      Le panneau d'évaluation à afficher ou masquer
     * @param scrollPaneHistorique Le panneau de l'historique des coups à afficher
     *                             ou masquer
     */
    public SettingPanel(JFrame parentFrame, EvaluationPanel evaluationPanel, JScrollPane scrollPaneHistorique) {
        this.parentFrame = parentFrame;
        this.evaluationPanel = evaluationPanel;
        this.scrollPaneHistorique = scrollPaneHistorique;

        JMenu menuAffichage = new JMenu("Paramètres d'affichage");

        this.itemJauge = new JCheckBoxMenuItem("Afficher la jauge d'évaluation", false);
        this.itemNotation = new JCheckBoxMenuItem("Afficher l'historique des coups", true);

        menuAffichage.add(itemJauge);
        menuAffichage.add(itemNotation);

        this.add(menuAffichage);
    }

    /**
     * Getter pour l'item de menu "Afficher la jauge d'évaluation"
     * 
     * @return le JCheckBoxMenuItem correspondant à l'option d'affichage de la jauge
     *         d'évaluation
     */
    public JCheckBoxMenuItem getItemJauge() {
        return itemJauge;
    }

    /**
     * Getter pour l'item de menu "Afficher l'historique des coups"
     * 
     * @return le JCheckBoxMenuItem correspondant à l'option d'affichage de
     *         l'historique des coups
     */
    public JCheckBoxMenuItem getItemNotation() {
        return itemNotation;
    }
}