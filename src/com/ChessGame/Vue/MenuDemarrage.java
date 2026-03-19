package com.ChessGame.Vue;

import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import java.awt.event.*;

/**
 * Menu de démarrage du jeu Poisson Bloqué.
 * Dimensionné pour correspondre à la ChessFrame et parfaitement centré.
 */
public class MenuDemarrage extends JDialog {

    private boolean contreIA       = false;
    private boolean humainEstBlanc = true;
    private boolean startClicked   = false;
    private String  nomJoueur      = "Joueur";
    private int     timerMinutes   = 0;

    // Palette de couleurs identique à ChessFrame
    private static final Color FOND         = new Color(22, 28, 22);
    private static final Color FOND_PANNEAU = new Color(37, 43, 37);
    private static final Color FOND_INPUT   = new Color(26, 30, 26);
    private static final Color VERT         = new Color(55, 90, 55);
    private static final Color VERT_HOVER   = new Color(75, 115, 70);
    private static final Color BEIGE        = new Color(232, 222, 195);
    private static final Color GRIS         = new Color(190, 185, 165);
    private static final Color BORDURE      = new Color(80, 70, 45);
    private static final Color OR           = new Color(201, 162, 39);

    private JPanel sectionCouleur;

    public MenuDemarrage() {
        super((JFrame) null, "Poisson Bloqué", true);
        setUndecorated(true);

        // On définit une taille cohérente avec la fenêtre de jeu principale
        setSize(500, 650);
        setLocationRelativeTo(null);

        // Panneau principal avec coins arrondis et bordure Or
        JPanel root = new JPanel(new BorderLayout()) {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(FOND);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 20, 20);
                g2.setColor(BORDURE);
                g2.setStroke(new BasicStroke(2));
                g2.drawRoundRect(1, 1, getWidth()-3, getHeight()-3, 20, 20);
            }
        };
        root.setOpaque(false);
        root.setBorder(new EmptyBorder(30, 40, 30, 40));

        root.add(construireEntete(),      BorderLayout.NORTH);
        root.add(construireCorps(),       BorderLayout.CENTER);
        root.add(construireBoutonJouer(), BorderLayout.SOUTH);

        setContentPane(root);
        setBackground(new Color(0,0,0,0));
    }

    private JPanel construireEntete() {
        JPanel p = new JPanel();
        p.setLayout(new BoxLayout(p, BoxLayout.Y_AXIS));
        p.setOpaque(false);

        JLabel titre = new JLabel("POISSON BLOQUÉ");
        titre.setFont(new Font("Serif", Font.BOLD, 34));
        titre.setForeground(OR);
        titre.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel sous = new JLabel("CONFIGURATION DE LA PARTIE");
        sous.setFont(new Font("SansSerif", Font.BOLD, 12));
        sous.setForeground(GRIS);
        sous.setAlignmentX(Component.CENTER_ALIGNMENT);
        sous.setBorder(new EmptyBorder(5, 0, 25, 0));

        p.add(titre);
        p.add(sous);
        return p;
    }

    private JPanel construireCorps() {
        JPanel corps = new JPanel();
        corps.setOpaque(false);
        corps.setLayout(new BoxLayout(corps, BoxLayout.Y_AXIS));

        // --- Section Nom ---
        corps.add(sectionLabel("NOM DU JOUEUR"));
        JTextField champNom = champTexte("Joueur");
        corps.add(champNom);
        champNom.addFocusListener(new FocusAdapter() {
            @Override
            public void focusLost(FocusEvent e) {
                String v = champNom.getText().trim();
                nomJoueur = v.isEmpty() ? "Joueur" : v;
            }
        });
        corps.add(Box.createVerticalStrut(30));

        // --- Section Adversaire ---
        corps.add(sectionLabel("ADVERSAIRE"));
        JPanel modePanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 0));
        modePanel.setOpaque(false);

        JToggleButton btnIA     = toggle("IA (Villette)", false, new Dimension(160, 45));
        JToggleButton btnHumain = toggle("Humain Local",  true,  new Dimension(160, 45));
        ButtonGroup grpMode = new ButtonGroup();
        grpMode.add(btnIA); grpMode.add(btnHumain);
        modePanel.add(btnIA); modePanel.add(btnHumain);
        corps.add(modePanel);
        corps.add(Box.createVerticalStrut(30));

        // --- Section Couleur (Seulement pour IA) ---
        sectionCouleur = new JPanel();
        sectionCouleur.setOpaque(false);
        sectionCouleur.setLayout(new BoxLayout(sectionCouleur, BoxLayout.Y_AXIS));
        sectionCouleur.setVisible(false);

        sectionCouleur.add(sectionLabel("VOTRE COULEUR"));
        JPanel colorPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 0));
        colorPanel.setOpaque(false);
        JToggleButton btnBlanc = toggle("Blancs ♔", true,  new Dimension(130, 40));
        JToggleButton btnNoir  = toggle("Noirs ♚",  false, new Dimension(130, 40));
        ButtonGroup grpColor = new ButtonGroup();
        grpColor.add(btnBlanc); grpColor.add(btnNoir);
        colorPanel.add(btnBlanc); colorPanel.add(btnNoir);
        sectionCouleur.add(colorPanel);
        sectionCouleur.add(Box.createVerticalStrut(30));
        corps.add(sectionCouleur);

        // --- Section Timer ---
        corps.add(sectionLabel("CADENCE DE JEU"));
        JPanel timerPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 8, 0));
        timerPanel.setOpaque(false);
        String[] labelsT  = {"∞", "1m", "3m", "5m", "10m"};
        int[]    valeursT = {0, 1, 3, 5, 10};
        ButtonGroup grpTimer = new ButtonGroup();
        for (int i = 0; i < labelsT.length; i++) {
            final int val = valeursT[i];
            JToggleButton b = toggle(labelsT[i], i == 0, new Dimension(65, 40));
            b.addActionListener(e -> timerMinutes = val);
            grpTimer.add(b);
            timerPanel.add(b);
        }
        corps.add(timerPanel);

        // Logic des boutons
        btnIA.addActionListener(e -> { contreIA = true; sectionCouleur.setVisible(true); revalidate(); repaint(); });
        btnHumain.addActionListener(e -> { contreIA = false; sectionCouleur.setVisible(false); revalidate(); repaint(); });
        btnBlanc.addActionListener(e -> humainEstBlanc = true);
        btnNoir.addActionListener(e ->  humainEstBlanc = false);

        return corps;
    }

    private JPanel construireBoutonJouer() {
        JPanel p = new JPanel(new FlowLayout(FlowLayout.CENTER));
        p.setOpaque(false);
        p.setBorder(new EmptyBorder(30, 0, 10, 0));

        JButton btn = new JButton("LANCER LA PARTIE") {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(getModel().isRollover() ? VERT_HOVER : VERT);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 15, 15);
                super.paintComponent(g);
            }
        };
        btn.setFont(new Font("SansSerif", Font.BOLD, 18));
        btn.setForeground(Color.WHITE);
        btn.setContentAreaFilled(false);
        btn.setBorderPainted(false);
        btn.setFocusPainted(false);
        btn.setPreferredSize(new Dimension(350, 60));
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btn.addActionListener(e -> { startClicked = true; dispose(); });
        p.add(btn);
        return p;
    }

    private JLabel sectionLabel(String texte) {
        JLabel l = new JLabel(texte);
        l.setFont(new Font("SansSerif", Font.BOLD, 12));
        l.setForeground(OR);
        l.setAlignmentX(Component.CENTER_ALIGNMENT);
        l.setBorder(new EmptyBorder(0, 0, 10, 0));
        return l;
    }

    private JTextField champTexte(String placeholder) {
        JTextField f = new JTextField(placeholder);
        f.setFont(new Font("SansSerif", Font.PLAIN, 16));
        f.setForeground(BEIGE);
        f.setBackground(FOND_INPUT);
        f.setHorizontalAlignment(JTextField.CENTER);
        f.setCaretColor(OR);
        f.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BORDURE, 1, true),
                new EmptyBorder(10, 15, 10, 15)));
        f.setMaximumSize(new Dimension(350, 50));
        f.setAlignmentX(Component.CENTER_ALIGNMENT);
        return f;
    }

    private JToggleButton toggle(String texte, boolean sel, Dimension dim) {
        JToggleButton btn = new JToggleButton(texte, sel) {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(isSelected() ? VERT : FOND_PANNEAU);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 12, 12);
                g2.setColor(isSelected() ? OR : BORDURE);
                g2.drawRoundRect(0, 0, getWidth()-1, getHeight()-1, 12, 12);
                super.paintComponent(g);
            }
        };
        btn.setFont(new Font("SansSerif", Font.BOLD, 13));
        btn.setForeground(sel ? Color.WHITE : GRIS);
        btn.setPreferredSize(dim);
        btn.setContentAreaFilled(false);
        btn.setBorderPainted(false);
        btn.setFocusPainted(false);
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btn.addItemListener(e -> btn.setForeground(btn.isSelected() ? Color.WHITE : GRIS));
        return btn;
    }

    public boolean isContreIA()       { return contreIA; }
    public boolean isHumainEstBlanc() { return humainEstBlanc; }
    public boolean isStartClicked()   { return startClicked; }
    public String  getNomJoueur()     { return nomJoueur; }
    public int     getTimerMinutes()  { return timerMinutes; }
}