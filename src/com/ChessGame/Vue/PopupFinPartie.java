package com.ChessGame.Vue;

import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;

/**
 * Popup stylée fin de partie — thème Poisson Bloqué
 */
public class PopupFinPartie extends JDialog {

    private static final Color FOND_CARTE  = new Color(35, 48, 35);
    private static final Color VERT        = new Color(119, 148, 85);
    private static final Color VERT_HOVER  = new Color(100, 130, 70);
    private static final Color BEIGE       = new Color(235, 235, 208);
    private static final Color GRIS        = new Color(160, 170, 150);
    private static final Color BORDURE     = new Color(60, 80, 55);
    private static final Color OR          = new Color(212, 175, 55);
    private static final Color ROUGE       = new Color(180, 60, 60);

    public enum TypeFin { ECHEC_MAT, PAT, FORFAIT, TIMEOUT }

    /**
     * @param parent   fenêtre parente
     * @param gagnant  nom du gagnant (null si pat)
     * @param type     type de fin
     * @param onRevanche  callback bouton "Revanche" (peut être null)
     */
    public PopupFinPartie(JFrame parent, String gagnant, TypeFin type, Runnable onRevanche) {
        super(parent, true);
        setUndecorated(true);
        setBackground(new Color(0, 0, 0, 0));

        JPanel root = new JPanel(new BorderLayout(0, 0)) {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(FOND_CARTE);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 16, 16);
            }
        };
        root.setOpaque(false);
        root.setBorder(new CompoundBorder(
                BorderFactory.createLineBorder(BORDURE, 1),
                new EmptyBorder(28, 36, 24, 36)
        ));

        // ── Icône + titre ─────────────────────────────────────────
        String icone, titre, sousTitre;
        Color couleurTitre;

        switch (type) {
            case ECHEC_MAT:
                icone = "♚"; titre = gagnant + " gagne !";
                sousTitre = "Victoire par échec et mat";
                couleurTitre = OR; break;
            case PAT:
                icone = "⚖"; titre = "Match nul";
                sousTitre = "Pat — aucun coup légal possible";
                couleurTitre = BEIGE; break;
            case FORFAIT:
                icone = "🏳"; titre = gagnant + " gagne !";
                sousTitre = "L'adversaire a abandonné";
                couleurTitre = OR; break;
            case TIMEOUT:
                icone = "⏱"; titre = gagnant + " gagne !";
                sousTitre = "Victoire au temps";
                couleurTitre = OR; break;
            default:
                icone = "♟"; titre = "Fin de partie";
                sousTitre = ""; couleurTitre = BEIGE;
        }

        JLabel labelIcone = new JLabel(icone, SwingConstants.CENTER);
        labelIcone.setFont(new Font("Serif", Font.PLAIN, 48));
        labelIcone.setForeground(couleurTitre);
        labelIcone.setBorder(new EmptyBorder(0, 0, 12, 0));

        JLabel labelTitre = new JLabel(titre, SwingConstants.CENTER);
        labelTitre.setFont(new Font("Serif", Font.BOLD, 22));
        labelTitre.setForeground(couleurTitre);

        JLabel labelSous = new JLabel(sousTitre, SwingConstants.CENTER);
        labelSous.setFont(new Font("SansSerif", Font.PLAIN, 13));
        labelSous.setForeground(GRIS);
        labelSous.setBorder(new EmptyBorder(6, 0, 20, 0));

        JPanel textPanel = new JPanel(new GridLayout(3, 1, 0, 0));
        textPanel.setOpaque(false);
        textPanel.add(labelIcone);
        textPanel.add(labelTitre);
        textPanel.add(labelSous);

        // ── Boutons ───────────────────────────────────────────────
        JPanel btnPanel = new JPanel(new GridLayout(1, 2, 12, 0));
        btnPanel.setOpaque(false);

        JButton btnFermer = creerBouton("Quitter", ROUGE, new Color(150, 50, 50));
        btnFermer.addActionListener(e -> System.exit(0));

        JButton btnRevanche = creerBouton("Nouvelle partie", VERT, VERT_HOVER);
        if (onRevanche != null) {
            btnRevanche.addActionListener(e -> { dispose(); onRevanche.run(); });
        }
        // toujours actif — même sans callback on ferme juste

        btnPanel.add(btnFermer);
        btnPanel.add(btnRevanche);

        root.add(textPanel, BorderLayout.CENTER);
        root.add(btnPanel,  BorderLayout.SOUTH);

        setContentPane(root);
        this.setMinimumSize(new Dimension(400, 320));
        pack();
        setLocationRelativeTo(parent);
    }

    private JButton creerBouton(String texte, Color fond, Color hover) {
        JButton btn = new JButton(texte) {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(getModel().isRollover() ? hover : fond);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 8, 8);
                super.paintComponent(g);
            }
        };
        btn.setFont(new Font("SansSerif", Font.BOLD, 13));
        btn.setForeground(Color.WHITE);
        btn.setContentAreaFilled(false);
        btn.setBorderPainted(false);
        btn.setFocusPainted(false);
        btn.setPreferredSize(new Dimension(160, 45));
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        return btn;
    }

    /** Affiche la popup (bloquant) */
    public static void afficher(JFrame parent, String gagnant, TypeFin type, Runnable onRevanche) {
        SwingUtilities.invokeLater(() -> {
            PopupFinPartie p = new PopupFinPartie(parent, gagnant, type, onRevanche);
            p.setVisible(true);
        });
    }
}