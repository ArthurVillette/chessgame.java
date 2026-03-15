package com.ChessGame.Vue;

import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import java.awt.event.*;

public class MenuDemarrage extends JDialog {

    private boolean contreIA       = false; // defaut : humain vs humain
    private boolean humainEstBlanc = true;
    private boolean startClicked   = false;
    private String  nomJoueur      = "Joueur";
    private int     timerMinutes   = 0;

    private static final Color FOND       = new Color(20, 24, 20);
    private static final Color FOND_CARTE = new Color(32, 38, 32);
    private static final Color FOND_INPUT = new Color(28, 34, 28);
    private static final Color VERT       = new Color(95, 135, 65);
    private static final Color VERT_HOVER = new Color(75, 108, 50);
    private static final Color VERT_SEL   = new Color(80, 118, 55);
    private static final Color BEIGE      = new Color(232, 232, 205);
    private static final Color GRIS       = new Color(140, 152, 132);
    private static final Color BORDURE    = new Color(52, 64, 48);
    private static final Color OR         = new Color(200, 165, 45);

    // Section couleur (a masquer si humain vs humain)
    private JPanel sectionCouleur;

    public MenuDemarrage() {
        super((JFrame) null, "Poisson Bloque", true);
        setUndecorated(true);
        setSize(440, 540);
        setLocationRelativeTo(null);
        setResizable(false);

        JPanel root = new JPanel(new BorderLayout()) {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                // Fond avec leger degrade
                GradientPaint gp = new GradientPaint(0, 0, new Color(24, 30, 24), 0, getHeight(), new Color(16, 20, 16));
                g2.setPaint(gp);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 16, 16);
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
        p.setBorder(new EmptyBorder(20, 24, 8, 24));

        JPanel textes = new JPanel();
        textes.setLayout(new BoxLayout(textes, BoxLayout.Y_AXIS));
        textes.setOpaque(false);

        JLabel icone = new JLabel("♟");
        icone.setFont(new Font("Serif", Font.PLAIN, 28));
        icone.setForeground(OR);

        JLabel titre = new JLabel("Poisson Bloque");
        titre.setFont(new Font("Serif", Font.BOLD, 20));
        titre.setForeground(BEIGE);

        JLabel sous = new JLabel("Configurer une nouvelle partie");
        sous.setFont(new Font("SansSerif", Font.PLAIN, 11));
        sous.setForeground(GRIS);

        // Ligne icone + titre
        JPanel ligneTitre = new JPanel(new FlowLayout(FlowLayout.LEFT, 6, 0));
        ligneTitre.setOpaque(false);
        ligneTitre.add(icone);
        ligneTitre.add(titre);

        textes.add(ligneTitre);
        textes.add(Box.createVerticalStrut(2));
        textes.add(sous);

        JButton fermer = new JButton("x");
        fermer.setFont(new Font("SansSerif", Font.BOLD, 13));
        fermer.setForeground(GRIS);
        fermer.setContentAreaFilled(false);
        fermer.setBorderPainted(false);
        fermer.setFocusPainted(false);
        fermer.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        fermer.addActionListener(e -> System.exit(0));
        fermer.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e) { fermer.setForeground(new Color(200,70,70)); }
            public void mouseExited(MouseEvent e)  { fermer.setForeground(GRIS); }
        });

        p.add(textes,  BorderLayout.WEST);
        p.add(fermer,  BorderLayout.EAST);

        JPanel wrap = new JPanel(new BorderLayout());
        wrap.setOpaque(false);
        wrap.add(p, BorderLayout.CENTER);
        // Separateur or discret
        JPanel sep = new JPanel() {
            @Override protected void paintComponent(Graphics g) {
                g.setColor(BORDURE);
                g.fillRect(0, 0, getWidth(), 1);
            }
        };
        sep.setPreferredSize(new Dimension(0, 1));
        sep.setOpaque(false);
        wrap.add(sep, BorderLayout.SOUTH);
        return wrap;
    }

    private JPanel construireCorps() {
        JPanel corps = new JPanel();
        corps.setOpaque(false);
        corps.setLayout(new BoxLayout(corps, BoxLayout.Y_AXIS));
        corps.setBorder(new EmptyBorder(14, 24, 6, 24));

        // ── Nom ──────────────────────────────────────────────────
        corps.add(sectionLabel("VOTRE NOM"));
        corps.add(Box.createVerticalStrut(5));
        JTextField champNom = champTexte("Joueur");
        corps.add(champNom);
        champNom.addFocusListener(new FocusAdapter() {
            public void focusLost(FocusEvent e) {
                String v = champNom.getText().trim();
                nomJoueur = v.isEmpty() ? "Joueur" : v;
            }
        });

        corps.add(Box.createVerticalStrut(14));

        // ── Adversaire ───────────────────────────────────────────
        corps.add(sectionLabel("ADVERSAIRE"));
        corps.add(Box.createVerticalStrut(5));
        JPanel modePanel = new JPanel(new GridLayout(1, 2, 8, 0));
        modePanel.setOpaque(false);
        modePanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));

        // Defaut : Humain local (contreIA = false)
        JToggleButton btnIA     = toggle("IA (Villette)", false);
        JToggleButton btnHumain = toggle("Humain local",  true);
        ButtonGroup grpMode = new ButtonGroup();
        grpMode.add(btnIA); grpMode.add(btnHumain);
        modePanel.add(btnIA); modePanel.add(btnHumain);
        corps.add(modePanel);

        corps.add(Box.createVerticalStrut(14));

        // ── Couleur (visible seulement si contre IA) ─────────────
        sectionCouleur = new JPanel();
        sectionCouleur.setOpaque(false);
        sectionCouleur.setLayout(new BoxLayout(sectionCouleur, BoxLayout.Y_AXIS));
        sectionCouleur.setVisible(false); // cache par defaut (mode humain)

        sectionCouleur.add(sectionLabel("VOTRE COULEUR"));
        sectionCouleur.add(Box.createVerticalStrut(5));
        JPanel colorPanel = new JPanel(new GridLayout(1, 2, 8, 0));
        colorPanel.setOpaque(false);
        colorPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));

        JToggleButton btnBlanc = toggle("Blancs", true);
        JToggleButton btnNoir  = toggle("Noirs",  false);
        ButtonGroup grpColor = new ButtonGroup();
        grpColor.add(btnBlanc); grpColor.add(btnNoir);
        colorPanel.add(btnBlanc); colorPanel.add(btnNoir);
        sectionCouleur.add(colorPanel);
        sectionCouleur.add(Box.createVerticalStrut(14));

        corps.add(sectionCouleur);

        // Listeners adversaire -> montrer/cacher couleur
        btnIA.addActionListener(e -> {
            contreIA = true;
            sectionCouleur.setVisible(true);
            revalidate(); repaint();
        });
        btnHumain.addActionListener(e -> {
            contreIA = false;
            sectionCouleur.setVisible(false);
            revalidate(); repaint();
        });

        btnBlanc.addActionListener(e -> { if (btnBlanc.isSelected()) humainEstBlanc = true; });
        btnNoir.addActionListener(e ->  { if (btnNoir.isSelected())  humainEstBlanc = false; });

        // ── Timer ─────────────────────────────────────────────────
        corps.add(sectionLabel("CONTROLE DU TEMPS"));
        corps.add(Box.createVerticalStrut(5));
        JPanel timerPanel = new JPanel(new GridLayout(1, 5, 6, 0));
        timerPanel.setOpaque(false);
        timerPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));
        String[] labelsT  = {"Sans", "1 min", "3 min", "5 min", "10 min"};
        int[]    valeursT = {0, 1, 3, 5, 10};
        ButtonGroup grpTimer = new ButtonGroup();
        for (int i = 0; i < labelsT.length; i++) {
            final int val = valeursT[i];
            JToggleButton b = toggle(labelsT[i], i == 0);
            b.addActionListener(e -> { if (b.isSelected()) timerMinutes = val; });
            grpTimer.add(b);
            timerPanel.add(b);
        }
        corps.add(timerPanel);

        return corps;
    }

    private JPanel construireBoutonJouer() {
        JPanel p = new JPanel(new BorderLayout());
        p.setOpaque(false);
        p.setBorder(new EmptyBorder(10, 24, 22, 24));

        JButton btn = new JButton("Lancer la partie") {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                GradientPaint gp = new GradientPaint(0, 0,
                        getModel().isRollover() ? VERT_HOVER : VERT,
                        0, getHeight(),
                        getModel().isRollover() ? new Color(60, 90, 40) : VERT_SEL);
                g2.setPaint(gp);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 10, 10);
                super.paintComponent(g);
            }
        };
        btn.setFont(new Font("SansSerif", Font.BOLD, 14));
        btn.setForeground(Color.WHITE);
        btn.setContentAreaFilled(false);
        btn.setBorderPainted(false);
        btn.setFocusPainted(false);
        btn.setPreferredSize(new Dimension(0, 44));
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btn.addActionListener(e -> { startClicked = true; dispose(); });
        p.add(btn);
        return p;
    }

    private JLabel sectionLabel(String texte) {
        JLabel l = new JLabel(texte);
        l.setFont(new Font("SansSerif", Font.BOLD, 9));
        l.setForeground(new Color(120, 135, 110));
        l.setAlignmentX(Component.LEFT_ALIGNMENT);
        return l;
    }

    private JTextField champTexte(String placeholder) {
        JTextField f = new JTextField(placeholder) {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(FOND_INPUT);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 7, 7);
                super.paintComponent(g);
            }
        };
        f.setFont(new Font("SansSerif", Font.PLAIN, 13));
        f.setForeground(BEIGE);
        f.setCaretColor(VERT);
        f.setOpaque(false);
        f.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BORDURE, 1, true),
                new EmptyBorder(7, 11, 7, 11)));
        f.setMaximumSize(new Dimension(Integer.MAX_VALUE, 38));
        f.setAlignmentX(Component.LEFT_ALIGNMENT);
        return f;
    }

    private JToggleButton toggle(String texte, boolean sel) {
        JToggleButton btn = new JToggleButton(texte, sel) {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                if (!isEnabled()) {
                    g2.setColor(new Color(26, 30, 26));
                } else if (isSelected()) {
                    GradientPaint gp = new GradientPaint(0, 0, VERT, 0, getHeight(), VERT_SEL);
                    g2.setPaint(gp);
                } else if (getModel().isRollover()) {
                    g2.setColor(new Color(44, 52, 42));
                } else {
                    g2.setColor(FOND_CARTE);
                }
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 7, 7);
                // Bordure
                g2.setColor(isSelected() ? new Color(70, 105, 45) : BORDURE);
                g2.setStroke(new BasicStroke(1f));
                g2.drawRoundRect(0, 0, getWidth()-1, getHeight()-1, 7, 7);
                super.paintComponent(g);
            }
        };
        btn.setFont(new Font("SansSerif", Font.PLAIN, 12));
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