package com.ChessGame.Vue;

import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import java.awt.event.*;

/**
 * Classe représentant le menu de démarrage du jeu d'échecs,
 * permettant au joueur de choisir son nom,
 */
public class MenuDemarrage extends JDialog {

    private boolean contreIA       = false;
    private boolean humainEstBlanc = true;
    private boolean startClicked   = false;
    private String  nomJoueur      = "Joueur";
    private int     timerMinutes   = 0;

    private static final Color FOND       = new Color(20, 24, 20);
    private static final Color FOND_CARTE = new Color(32, 38, 32);
    private static final Color FOND_INPUT = new Color(25, 30, 25);
    private static final Color VERT       = new Color(119, 148, 85);
    private static final Color VERT_HOVER = new Color(135, 165, 95);
    private static final Color BEIGE      = new Color(235, 235, 208);
    private static final Color GRIS       = new Color(160, 170, 150);
    private static final Color BORDURE    = new Color(60, 75, 55);
    private static final Color OR         = new Color(212, 175, 55);

    private JPanel sectionCouleur;

    /**
     * Constructeur du menu de démarrage, avec options pour :
     * - Nom du joueur
     * - Jouer contre IA ou humain local
     * - Choix de la couleur (si contre IA)
     * - Choix du timer
     */
    public MenuDemarrage() {
        super((JFrame) null, "Poisson Bloqué", true);
        setUndecorated(true);
        setSize(460, 600); // Légèrement plus grand pour laisser respirer
        setLocationRelativeTo(null);
        setResizable(false);

        JPanel root = new JPanel(new BorderLayout()) {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(FOND);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 20, 20);
            }
        };
        root.setOpaque(false);
        root.setBorder(BorderFactory.createLineBorder(BORDURE, 1));

        root.add(construireEntete(),      BorderLayout.NORTH);
        root.add(construireCorps(),       BorderLayout.CENTER);
        root.add(construireBoutonJouer(), BorderLayout.SOUTH);

        setContentPane(root);
        setBackground(new Color(0,0,0,0));
    }

    private JPanel construireEntete() {
        JPanel p = new JPanel(new BorderLayout());
        p.setOpaque(false);
        p.setBorder(new EmptyBorder(30, 30, 15, 30));

        JLabel titre = new JLabel("POISSON BLOQUÉ");
        titre.setFont(new Font("Serif", Font.BOLD, 26));
        titre.setForeground(OR);
        titre.setHorizontalAlignment(SwingConstants.CENTER);

        JLabel sous = new JLabel("NOUVELLE PARTIE");
        sous.setFont(new Font("SansSerif", Font.BOLD, 12));
        sous.setForeground(GRIS);
        sous.setHorizontalAlignment(SwingConstants.CENTER);
        sous.setBorder(new EmptyBorder(5, 0, 0, 0));

        JPanel wrapTitre = new JPanel(new GridLayout(2, 1));
        wrapTitre.setOpaque(false);
        wrapTitre.add(titre);
        wrapTitre.add(sous);

        p.add(wrapTitre, BorderLayout.CENTER);
        return p;
    }

    private JPanel construireCorps() {
        JPanel corps = new JPanel();
        corps.setOpaque(false);
        corps.setLayout(new BoxLayout(corps, BoxLayout.Y_AXIS));
        corps.setBorder(new EmptyBorder(10, 35, 10, 35));

        // ── Nom ──────────────────────────────────────────────────
        corps.add(sectionLabel("NOM DU JOUEUR"));
        corps.add(Box.createVerticalStrut(8));
        JTextField champNom = champTexte("Joueur");
        corps.add(champNom);
        champNom.addFocusListener(new FocusAdapter() {
            public void focusLost(FocusEvent e) {
                String v = champNom.getText().trim();
                nomJoueur = v.isEmpty() ? "Joueur" : v;
            }
        });

        corps.add(Box.createVerticalStrut(20));

        // ── Adversaire ───────────────────────────────────────────
        corps.add(sectionLabel("ADVERSAIRE"));
        corps.add(Box.createVerticalStrut(8));
        JPanel modePanel = new JPanel(new GridLayout(1, 2, 10, 0));
        modePanel.setOpaque(false);
        modePanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 45));

        JToggleButton btnIA     = toggle("IA (Villette)", false);
        JToggleButton btnHumain = toggle("Humain Local",  true);
        ButtonGroup grpMode = new ButtonGroup();
        grpMode.add(btnIA); grpMode.add(btnHumain);
        modePanel.add(btnIA); modePanel.add(btnHumain);
        corps.add(modePanel);

        corps.add(Box.createVerticalStrut(20));

        // ── Couleur (Invisible par défaut) ──────────────────────
        sectionCouleur = new JPanel();
        sectionCouleur.setOpaque(false);
        sectionCouleur.setLayout(new BoxLayout(sectionCouleur, BoxLayout.Y_AXIS));
        sectionCouleur.setVisible(false);

        sectionCouleur.add(sectionLabel("VOTRE COULEUR"));
        sectionCouleur.add(Box.createVerticalStrut(8));
        JPanel colorPanel = new JPanel(new GridLayout(1, 2, 10, 0));
        colorPanel.setOpaque(false);
        colorPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 45));

        JToggleButton btnBlanc = toggle("Blancs ♔", true);
        JToggleButton btnNoir  = toggle("Noirs ♚",  false);
        ButtonGroup grpColor = new ButtonGroup();
        grpColor.add(btnBlanc); grpColor.add(btnNoir);
        colorPanel.add(btnBlanc); colorPanel.add(btnNoir);
        sectionCouleur.add(colorPanel);
        sectionCouleur.add(Box.createVerticalStrut(20));

        corps.add(sectionCouleur);

        btnIA.addActionListener(e -> { contreIA = true; sectionCouleur.setVisible(true); revalidate(); repaint(); });
        btnHumain.addActionListener(e -> { contreIA = false; sectionCouleur.setVisible(false); revalidate(); repaint(); });
        btnBlanc.addActionListener(e -> { humainEstBlanc = true; });
        btnNoir.addActionListener(e ->  { humainEstBlanc = false; });

        // ── Timer ─────────────────────────────────────────────────
        corps.add(sectionLabel("CADENCE DE JEU"));
        corps.add(Box.createVerticalStrut(8));
        JPanel timerPanel = new JPanel(new GridLayout(1, 5, 5, 0));
        timerPanel.setOpaque(false);
        timerPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));

        String[] labelsT  = {"∞", "1m", "3m", "5m", "10m"};
        int[]    valeursT = {0, 1, 3, 5, 10};
        ButtonGroup grpTimer = new ButtonGroup();
        for (int i = 0; i < labelsT.length; i++) {
            final int val = valeursT[i];
            JToggleButton b = toggle(labelsT[i], i == 0);
            b.addActionListener(e -> timerMinutes = val);
            grpTimer.add(b);
            timerPanel.add(b);
        }
        corps.add(timerPanel);

        return corps;
    }

    private JPanel construireBoutonJouer() {
        JPanel p = new JPanel(new BorderLayout());
        p.setOpaque(false);
        p.setBorder(new EmptyBorder(20, 35, 30, 35));

        JButton btn = new JButton("LANCER LA PARTIE") {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(getModel().isRollover() ? VERT_HOVER : VERT);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 12, 12);
                super.paintComponent(g);
            }
        };
        btn.setFont(new Font("SansSerif", Font.BOLD, 15));
        btn.setForeground(Color.WHITE);
        btn.setContentAreaFilled(false);
        btn.setBorderPainted(false);
        btn.setFocusPainted(false);
        btn.setPreferredSize(new Dimension(0, 50));
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btn.addActionListener(e -> { startClicked = true; dispose(); });
        p.add(btn);
        return p;
    }

    private JLabel sectionLabel(String texte) {
        JLabel l = new JLabel(texte);
        l.setFont(new Font("SansSerif", Font.BOLD, 11));
        l.setForeground(OR);
        return l;
    }

    private JTextField champTexte(String placeholder) {
        JTextField f = new JTextField(placeholder);
        f.setFont(new Font("SansSerif", Font.PLAIN, 14));
        f.setForeground(BEIGE);
        f.setBackground(FOND_INPUT);
        f.setCaretColor(OR);
        f.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BORDURE, 1, true),
                new EmptyBorder(10, 15, 10, 15)));
        f.setMaximumSize(new Dimension(Integer.MAX_VALUE, 45));
        return f;
    }

    private JToggleButton toggle(String texte, boolean sel) {
        JToggleButton btn = new JToggleButton(texte, sel) {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(isSelected() ? VERT : FOND_CARTE);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 10, 10);
                g2.setColor(isSelected() ? VERT_HOVER : BORDURE);
                g2.drawRoundRect(0, 0, getWidth()-1, getHeight()-1, 10, 10);
                super.paintComponent(g);
            }
        };
        btn.setFont(new Font("SansSerif", Font.BOLD, 12));
        btn.setForeground(sel ? Color.WHITE : GRIS);
        btn.setContentAreaFilled(false);
        btn.setBorderPainted(false);
        btn.setFocusPainted(false);
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btn.addItemListener(e -> btn.setForeground(btn.isSelected() ? Color.WHITE : GRIS));
        return btn;
    }

    public boolean isContreIA()      { return contreIA; }
    public boolean isHumainEstBlanc(){ return humainEstBlanc; }
    public boolean isStartClicked()  { return startClicked; }
    public String  getNomJoueur()    { return nomJoueur; }
    public int     getTimerMinutes() { return timerMinutes; }
}