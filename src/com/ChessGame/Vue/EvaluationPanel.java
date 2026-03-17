package com.ChessGame.Vue;

import javax.swing.JPanel;
import java.awt.*;

/**
 * Panneau affichant la jauge d'avantage (Blancs vs Noirs) basée sur
 * l'évaluation de Stockfish.
 */
public class EvaluationPanel extends JPanel {

    private double pourcentageBlanc = 0.5;
    //RT
    private final String texteScore = "0.0";

    private static final Color FOND_FENETRE = new Color(22, 36, 22);
    private static final Color NOIR_PIECE   = new Color(35, 35, 35);
    private static final Color BLANC_PIECE  = new Color(232, 232, 214);
    private static final Color BORDURE      = new Color(70, 95, 65);
    private static final Color OR           = new Color(212, 175, 55);

    /**
     * Constructeur de EvaluationPanel
     * Définit la taille préférée du panneau pour s'adapter à la hauteur du plateau
     * d'échecs.
     */

        public  EvaluationPanel() {
        setPreferredSize(new Dimension(20, ChessFrame.TILE_SIZE * 8 + 2 * BoardPanel.MARGE));
        setBackground(FOND_FENETRE);
        setToolTipText("0.0");
        }



    /**
         * Met à jour l'affichage de la barre en fonction du score en centipions.
         * Utilise une courbe sigmoïde pour que la jauge ne dépasse jamais 100% ou 0%.
         * * @param scoreCentipions Le score renvoyé par Stockfish (positif = avantage
         * Blancs)
         */

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


    /**
     * Surcharge de la méthode paintComponent pour dessiner la jauge d'avantage.
     * @param g the <code>Graphics</code> object to protect
     */

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

        int w = getWidth();
        int h = getHeight();
        int arc = 5;

        int hauteurBlanche = (int) (h * pourcentageBlanc);
        int hauteurNoire   = h - hauteurBlanche;

        // Fond noir (haut)
        g2.setColor(NOIR_PIECE);
        g2.fillRoundRect(0, 0, w, h, arc, arc);

        // Zone blanche (bas) — coins carrés là où ça touche le noir
        if (hauteurBlanche > 0) {
            g2.setColor(BLANC_PIECE);
            g2.fillRect(0, hauteurNoire, w, hauteurBlanche);
            // Remettre les coins arrondis du bas
            g2.fillRoundRect(0, h - arc * 2, w, arc * 2, arc, arc);
        }

        // Ligne dorée au centre (égalité)
        g2.setColor(OR);
        g2.setStroke(new BasicStroke(1.2f));
        g2.drawLine(1, h / 2, w - 1, h / 2);

        // Bordure
        g2.setColor(BORDURE);
        g2.setStroke(new BasicStroke(1f));
        g2.drawRoundRect(0, 0, w - 1, h - 1, arc, arc);

        // Score texte si déséquilibré
        if (Math.abs(pourcentageBlanc - 0.5) > 0.06) {
            g2.setFont(new Font("SansSerif", Font.BOLD, 8));
            FontMetrics fm = g2.getFontMetrics();
            int tw = fm.stringWidth(texteScore);
            boolean blancsGagnent = pourcentageBlanc > 0.5;
            int ty = blancsGagnent ? h - 6 : 13;
            g2.setColor(blancsGagnent ? NOIR_PIECE : BLANC_PIECE);
            g2.drawString(texteScore, (w - tw) / 2, ty);
        }
    }
}
