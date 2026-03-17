package com.ChessGame.Vue;

import javax.swing.*;
import java.awt.*;


/**
 * Splash screen animé "Poisson Bloqué"
 * Barre de progression + texte qui apparaît progressivement
 */
public class SplashScreen extends JWindow {

    private int progression = 0;
    private float alphaLogo = 0f;
    private float alphaSous = 0f;
    private Timer timer;
    private final Runnable onFinish;

    // Palette inspirée échecs : vert profond + or
    private static final Color FOND         = new Color(22, 36, 22);
    private static final Color VERT_CLAIR   = new Color(119, 148, 85);
    private static final Color BEIGE        = new Color(235, 235, 208);
    private static final Color OR           = new Color(212, 175, 55);
    private static final Color BARRE_FOND   = new Color(40, 60, 40);

    public SplashScreen(Runnable onFinish) {
        this.onFinish = onFinish;
        setSize(520, 320);
        setLocationRelativeTo(null);

        JPanel panel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                dessiner((Graphics2D) g);
            }
        };
        panel.setBackground(FOND);
        setContentPane(panel);
    }

    private void dessiner(Graphics2D g2) {
        int w = getWidth();
        int h = getHeight();

        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

        // Fond dégradé
        GradientPaint grad = new GradientPaint(0, 0, FOND, 0, h, new Color(10, 20, 10));
        g2.setPaint(grad);
        g2.fillRect(0, 0, w, h);

        // Motif damier discret en fond
        g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.04f));
        int taille = 30;
        for (int row = 0; row < h / taille + 1; row++) {
            for (int col = 0; col < w / taille + 1; col++) {
                if ((row + col) % 2 == 0) {
                    g2.setColor(Color.WHITE);
                    g2.fillRect(col * taille, row * taille, taille, taille);
                }
            }
        }
        g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1f));

        // Symbole roi ♚ décoratif
        g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.08f));
        g2.setColor(BEIGE);
        g2.setFont(new Font("Serif", Font.BOLD, 200));
        g2.drawString("♚", w / 2 - 100, h / 2 + 80);
        g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1f));

        // Titre principal "Poisson Bloqué"
        g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, alphaLogo));
        g2.setFont(new Font("Serif", Font.BOLD, 48));
        FontMetrics fm = g2.getFontMetrics();
        String titre = "Poisson Bloqué";
        int xTitre = (w - fm.stringWidth(titre)) / 2;
        // Ombre dorée
        g2.setColor(new Color(100, 80, 0, 180));
        g2.drawString(titre, xTitre + 2, 122);
        // Texte principal beige
        g2.setColor(BEIGE);
        g2.drawString(titre, xTitre, 120);

        // Ligne décorative dorée
        g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, alphaLogo));
        g2.setColor(OR);
        g2.setStroke(new BasicStroke(1.5f));
        g2.drawLine(w / 2 - 120, 135, w / 2 + 120, 135);

        // Sous-titre
        g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, alphaSous));
        g2.setFont(new Font("SansSerif", Font.PLAIN, 14));
        g2.setColor(VERT_CLAIR);
        String sousTitre = "Jouez et Affrontez l IA Vilette";
        FontMetrics fm2 = g2.getFontMetrics();
        g2.drawString(sousTitre, (w - fm2.stringWidth(sousTitre)) / 2, 162);

        g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1f));

        // Barre de progression
        int barreX = 80, barreY = 230, barreW = w - 160, barreH = 6;
        // Fond barre
        g2.setColor(BARRE_FOND);
        g2.fillRoundRect(barreX, barreY, barreW, barreH, barreH, barreH);
        // Barre active
        int largeurActive = (int) (barreW * progression / 100.0);
        GradientPaint gradBarre = new GradientPaint(
                barreX, 0, VERT_CLAIR,
                barreX + largeurActive, 0, OR);
        g2.setPaint(gradBarre);
        g2.fillRoundRect(barreX, barreY, largeurActive, barreH, barreH, barreH);

        // Pourcentage
        g2.setColor(new Color(160, 160, 140));
        g2.setFont(new Font("SansSerif", Font.PLAIN, 11));
        g2.drawString(progression + "%", w / 2 - 10, barreY + 22);

        // Message de chargement
        g2.setColor(new Color(120, 140, 100));
        g2.setFont(new Font("SansSerif", Font.ITALIC, 11));
        String msg = getMessageChargement();
        FontMetrics fm3 = g2.getFontMetrics();
        g2.drawString(msg, (w - fm3.stringWidth(msg)) / 2, barreY + 38);
    }

    private String getMessageChargement() {
        if (progression < 30)  return "Initialisation du moteur...";
        if (progression < 60)  return "Chargement des pièces...";
        if (progression < 85)  return "Préparation du plateau...";
        return "Presque prêt !";
    }

    public void demarrer() {
        setVisible(true);

        timer = new Timer(30, null);
        timer.addActionListener(e -> {
            progression = Math.min(100, progression + 1);

            // Fade in du logo entre 0 et 40%
            if (progression <= 40) {
                alphaLogo = Math.min(1f, progression / 30f);
            }
            // Fade in du sous-titre entre 20 et 60%
            if (progression >= 20 && progression <= 60) {
                alphaSous = Math.min(1f, (progression - 20) / 30f);
            }

            repaint();

            if (progression >= 100) {
                timer.stop();
                // Petite pause puis fermeture
                Timer pause = new Timer(400, ev -> {
                    setVisible(false);
                    dispose();
                    onFinish.run();
                });
                pause.setRepeats(false);
                pause.start();
            }
        });
        timer.start();
    }
}