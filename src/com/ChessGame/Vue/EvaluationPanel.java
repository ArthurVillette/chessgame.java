package com.ChessGame.Vue;

import javax.swing.JPanel;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;

/**
 * Panneau affichant la jauge d'avantage (Blancs vs Noirs) basée sur
 * l'évaluation de Stockfish.
 */
public class EvaluationPanel extends JPanel {

    private double pourcentageBlanc = 0.5;

    /**
     * Constructeur de EvaluationPanel
     * Définit la taille préférée du panneau pour s'adapter à la hauteur du plateau
     * d'échecs.
     */
    public EvaluationPanel() {
        setPreferredSize(new Dimension(30, ChessFrame.TILE_SIZE * 8));
    }

    /**
     * Met à jour l'affichage de la barre en fonction du score en centipions.
     * Utilise une courbe sigmoïde pour que la jauge ne dépasse jamais 100% ou 0%.
     * * @param scoreCentipions Le score renvoyé par Stockfish (positif = avantage
     * Blancs)
     */
    // Dans com.ChessGame.Vue.EvaluationPanel

    public void setScore(double scoreCentipions) {
        this.pourcentageBlanc = 1.0 / (1.0 + Math.exp(-0.004 * scoreCentipions));

        double scorePions = scoreCentipions / 100.0;
        String texteAvantage = String.format("%+.1f", scorePions);
        if (scoreCentipions > 9000) {
            texteAvantage = "+M";
        } else if (scoreCentipions < -9000) {
            texteAvantage = "-M";
        }

        this.setToolTipText(texteAvantage);
        repaint();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        int width = getWidth();
        int height = getHeight();

        // Calcul de la hauteur de la zone blanche (les blancs démarrent en bas du
        // plateau)
        int hauteurBlanche = (int) (height * pourcentageBlanc);
        int hauteurNoire = height - hauteurBlanche;

        // 1. Dessiner la partie Noire (en haut)
        g.setColor(new Color(64, 64, 64)); // Un gris très foncé / noir doux
        g.fillRect(0, 0, width, hauteurNoire);

        // 2. Dessiner la partie Blanche (en bas)
        g.setColor(new Color(240, 240, 240)); // Un blanc légèrement cassé pour ne pas éblouir
        g.fillRect(0, hauteurNoire, width, hauteurBlanche);

        // 3. Ligne de démarcation rouge au centre exact (point d'égalité = 0.0)
        g.setColor(Color.RED);
        g.drawLine(0, height / 2, width, height / 2);

        // 4. Petite bordure noire autour de la jauge pour une finition propre
        g.setColor(Color.BLACK);
        g.drawRect(0, 0, width - 1, height - 1);
    }

}
