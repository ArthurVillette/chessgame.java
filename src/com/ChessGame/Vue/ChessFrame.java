package com.ChessGame.Vue;

import javax.swing.*;
import javax.swing.border.*;

import com.ChessGame.Controller.ChessController;
import com.ChessGame.Model.plateau.Board;
import com.ChessGame.Model.ChessPieces.Piece;
import java.awt.*;

/**
 * Classe représentant la fenêtre principale du jeu d'échecs, contenant le plateau, la jauge d'évaluation, l'historique des coups et les informations des joueurs.
 * La classe gère également les interactions avec les boutons de contrôle (pause, abandon, options)
 * et affiche une boîte de dialogue personnalisée pour les options d'affichage.
 */
public class ChessFrame extends JFrame {

    public static final int TILE_SIZE = calculerTileSize();

    // ── Palette ───────────────────────────────────────────────────
    private static final Color FOND         = new Color(28, 32, 28);
    private static final Color FOND_PANNEAU = new Color(40, 46, 40);
    private static final Color FOND_ECHEQUIER = new Color(230, 225, 210);
    private static final Color FOND_HISTO   = new Color(34, 38, 34);
    private static final Color BEIGE        = new Color(235, 235, 208);
    private static final Color BORDURE      = new Color(65, 75, 60);
    private static final Color OR           = new Color(212, 175, 55);
    private static final Color VERT_BTN     = new Color(75, 105, 65);
    private static final Color VERT_HOVER   = new Color(95, 130, 80);
    private static final Color ROUGE_BTN    = new Color(100, 50, 50);
    private static final Color ROUGE_HOVER  = new Color(130, 65, 65);
    private static final Color GRIS_BTN     = new Color(55, 62, 55);
    private static final Color GRIS_TEXTE   = new Color(190, 195, 180);

    // ── Composants ────────────────────────────────────────────────
    private final BoardPanel      boardPanel;
    private final EvaluationPanel evaluationPanel;
    private JTextArea       historiqueArea;
    private JScrollPane     scrollPaneHistorique;
    private final SettingPanel    settingPanel;
    private final PlayerInfoPanel panelJoueurHaut;
    private final PlayerInfoPanel panelJoueurBas;

    // ── Bouton pause (toggle ▶/⏸) ─────────────────────────────────
    private JButton btnPause;
    private boolean enPause = false;

    // ── Callbacks ─────────────────────────────────────────────────
    private Runnable onForfait;
    private Runnable onPause;
    private Runnable onNouvellePartie;
    private final int timerMinutes ;
    //------------------
    private ChessController chessController;
    private Runnable onJaugeToggle;


    // ── TILE_SIZE dynamique ───────────────────────────────────────

    /**
     * Calcule une taille de tuile adaptée à la taille de l'écran, avec des limites pour éviter les tailles extrêmes
     * @return la taille de tuile calculée
     */
    private static int calculerTileSize() {
        Dimension ecran = Toolkit.getDefaultToolkit().getScreenSize();
        int hauteurDispo = (int)(ecran.height * 0.88) - 30;
        int taille = (hauteurDispo - 2*30 - 2*46 - 24) / 8;
        return Math.max(56, Math.min(90, taille));
    }

    // ── Constructeurs ─────────────────────────────────────────────


    /**
     * Constructeur principal de la fenêtre de jeu d'échecs
     * @param board le modèle du plateau d'échecs
     * @param nomBlanc le nom du joueur blanc
     * @param nomNoir le nom du joueur noir
     * @param timerMinutes le temps initial en minutes pour chaque joueur (0 = pas de

     */
    public ChessFrame(Board board, String nomBlanc, String nomNoir, int timerMinutes) {
        setTitle("♟ Poisson Bloqué");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setBackground(FOND);
        setResizable(false);
        this.timerMinutes = timerMinutes;

        JPanel root = new JPanel(new BorderLayout(8, 0));
        root.setBackground(FOND);
        root.setBorder(new EmptyBorder(10, 10, 10, 10));

        // Jauge gauche
        evaluationPanel = new EvaluationPanel();
        evaluationPanel.setVisible(true);
        JPanel jaugeWrapper = new JPanel(new BorderLayout());
        jaugeWrapper.setBackground(FOND);
        jaugeWrapper.setBorder(new EmptyBorder(50, 0, 50, 6));
        jaugeWrapper.add(evaluationPanel, BorderLayout.CENTER);
        root.add(jaugeWrapper, BorderLayout.WEST);

        // Centre
        boardPanel      = new BoardPanel(board);
        boardPanel.setBackground(FOND_ECHEQUIER);
        panelJoueurHaut = new PlayerInfoPanel(false, nomNoir);
        panelJoueurBas  = new PlayerInfoPanel(true,  nomBlanc);

        JPanel centrePanel = new JPanel(new BorderLayout(0, 4));
        centrePanel.setBackground(FOND);
        centrePanel.add(panelJoueurHaut, BorderLayout.NORTH);
        centrePanel.add(boardPanel,      BorderLayout.CENTER);
        centrePanel.add(panelJoueurBas,  BorderLayout.SOUTH);
        root.add(centrePanel, BorderLayout.CENTER);

        // Droite
        root.add(creerPanneauDroit(), BorderLayout.EAST);

        settingPanel = new SettingPanel(this, evaluationPanel, scrollPaneHistorique);
        setJMenuBar(settingPanel);

        setContentPane(root);
        pack();
        setLocationRelativeTo(null);
    }

    // ── Panneau droit ─────────────────────────────────────────────

    /**
     * Crée le panneau droit de la fenêtre, contenant la barre d'actions et l'historique des coups
     * @return le panneau droit construit
     */
    private JPanel creerPanneauDroit() {
        JPanel p = new JPanel(new BorderLayout(0, 6));
        p.setBackground(FOND);
        p.setPreferredSize(new Dimension(230, 0));
        p.add(creerBarreActions(), BorderLayout.NORTH);

        JPanel carte = new JPanel(new BorderLayout());
        carte.setBackground(FOND_PANNEAU);
        carte.setBorder(BorderFactory.createLineBorder(BORDURE, 1, true));

        JLabel titre = new JLabel("  ♟  Historique des coups");
        titre.setFont(new Font("Serif", Font.BOLD, 16));
        titre.setForeground(OR);
        titre.setOpaque(true);
        titre.setBackground(FOND_PANNEAU);
        titre.setBorder(new CompoundBorder(
                BorderFactory.createMatteBorder(0, 0, 1, 0, BORDURE),
                new EmptyBorder(8, 12, 8, 12)));

        historiqueArea = new JTextArea();
        historiqueArea.setEditable(false);
        historiqueArea.setFont(new Font("Monospaced", Font.PLAIN, 13));
        historiqueArea.setForeground(BEIGE);
        historiqueArea.setBackground(FOND_HISTO);
        historiqueArea.setBorder(new EmptyBorder(8, 12, 8, 8));

        scrollPaneHistorique = new JScrollPane(historiqueArea);
        scrollPaneHistorique.setBorder(BorderFactory.createEmptyBorder());
        scrollPaneHistorique.setBackground(FOND_HISTO);
        scrollPaneHistorique.getViewport().setBackground(FOND_HISTO);
        scrollPaneHistorique.getVerticalScrollBar().setPreferredSize(new Dimension(5, 0));

        JPanel footer = new JPanel(new BorderLayout());
        footer.setBackground(FOND_PANNEAU);
        footer.setBorder(new CompoundBorder(
                BorderFactory.createMatteBorder(1, 0, 0, 0, BORDURE),
                new EmptyBorder(7, 12, 7, 12)));
        footer.setPreferredSize(new Dimension(0, 36));
        JLabel lf = new JLabel("♔  Poisson Bloqué");
        lf.setFont(new Font("Serif", Font.ITALIC, 12));
        lf.setForeground(OR);
        footer.add(lf);

        carte.add(titre,                BorderLayout.NORTH);
        carte.add(scrollPaneHistorique, BorderLayout.CENTER);
        carte.add(footer,               BorderLayout.SOUTH);
        p.add(carte, BorderLayout.CENTER);
        return p;
    }

    // ── Barre d'actions ───────────────────────────────────────────

    /**
     * Crée la barre d'actions en haut à droite, contenant les boutons Pause/Play, Abandonner et Options
     * @return le JPanel contenant la barre d'actions construite
     */
    private JPanel creerBarreActions() {
        JPanel barre = new JPanel(new GridLayout(1, 3, 6, 0));
        barre.setBackground(FOND);
        barre.
                setPreferredSize(new Dimension(0, 40));

        // ▶/⏸ Pause — toggle
        btnPause = creerBtnIcone("⏸", VERT_BTN, VERT_HOVER);
        btnPause.setToolTipText("Pause / Reprendre");
        btnPause.addActionListener(e -> togglePauseUI());

        // 🏳 Abandonner
        JButton btnForfait = creerBtnIcone("🏳", ROUGE_BTN, ROUGE_HOVER);
        btnForfait.setToolTipText("Abandonner");
        btnForfait.addActionListener(e -> {
            int rep = JOptionPane.showConfirmDialog(this,
                    "Voulez-vous vraiment abandonner ?", "Abandon",
                    JOptionPane.YES_NO_OPTION);
            if (rep == JOptionPane.YES_OPTION && onForfait != null) onForfait.run();
        });

        // ⚙ Options — ouvre dialog ET met en pause
        JButton btnOpts = creerBtnIcone("⚙", VERT_BTN, VERT_HOVER);
        btnOpts.setToolTipText("Options");
        btnOpts.addActionListener(e -> {
            // Met en pause automatiquement si pas déjà en pause
            boolean etaitEnPause = enPause;
            if (!enPause) togglePauseUI();
            afficherDialogParams();
            if (!etaitEnPause && enPause) togglePauseUI();
        });

        barre.add(btnPause);
        barre.add(btnForfait);
        barre.add(btnOpts);
        return barre;
    }

    /** Toggle pause : change l'icône ⏸ ↔ ▶ et notifie le controller */
    private void togglePauseUI() {
        enPause = !enPause;
        btnPause.setText(enPause ? "▶" : "⏸");
        btnPause.setToolTipText(enPause ? "Reprendre" : "Pause");
        if (chessController != null) chessController.setEnPause(enPause);
        if (onPause != null) onPause.run();
    }

    private JButton creerBtnIcone(String icone, Color fond, Color hover) {
        JButton btn = new JButton(icone) {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(getModel().isRollover() ? hover : fond);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 8, 8);
                super.paintComponent(g);
            }
        };
        btn.setFont(new Font("SansSerif", Font.PLAIN, 16));
        btn.setForeground(new Color(225, 235, 215));
        btn.setContentAreaFilled(false);
        btn.setBorderPainted(false);
        btn.setFocusPainted(false);
        btn.setHorizontalAlignment(SwingConstants.CENTER);
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        return btn;
    }

    // ── Dialog options ────────────────────────────────────────────

    /**
     * Affiche une boîte de dialogue personnalisée pour les options d'affichage, permettant de basculer la visibilité de la jauge d'évaluation et de l'historique des coups. La boîte de dialogue est stylisée avec des couleurs et des boutons personnalisés,
     * et applique les changements immédiatement après validation.
     */
    private void afficherDialogParams() {
        JDialog dialog = new JDialog(this, "Options", true);
        dialog.setUndecorated(true);

        JPanel p = new JPanel(new BorderLayout(0, 16)) {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(FOND_PANNEAU);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 14, 14);
            }
        };
        p.setOpaque(false);
        p.setBorder(new CompoundBorder(
                BorderFactory.createLineBorder(BORDURE, 1),
                new EmptyBorder(20, 24, 18, 24)));

        JLabel titreLabel = new JLabel("⚙  Options d'affichage");
        titreLabel.setFont(new Font("Serif", Font.BOLD, 15));
        titreLabel.setForeground(BEIGE);

        JPanel checkPanel = new JPanel(new GridLayout(2, 1, 0, 10));
        checkPanel.setOpaque(false);
        JCheckBox cbJauge = creerCB("Afficher la jauge d'évaluation", evaluationPanel.isVisible());
        JCheckBox cbHisto = creerCB("Afficher l'historique des coups",
                scrollPaneHistorique == null || scrollPaneHistorique.isVisible());
        checkPanel.add(cbJauge);
        checkPanel.add(cbHisto);

        JPanel btnPanel = new JPanel(new GridLayout(1, 2, 10, 0));
        btnPanel.setOpaque(false);

        JButton btnAnnuler = creerBtnDialog("Annuler", GRIS_BTN, BORDURE);
        btnAnnuler.addActionListener(e -> dialog.dispose());

        JButton btnOK = creerBtnDialog("✓  OK", VERT_BTN, VERT_HOVER);
        btnOK.setForeground(Color.WHITE);
        btnOK.addActionListener(e -> {
            evaluationPanel.setVisible(cbJauge.isSelected());

            pack();

            settingPanel.getItemJauge().setSelected(cbJauge.isSelected());
            if (onJaugeToggle != null) onJaugeToggle.run();
            if (scrollPaneHistorique != null)
                scrollPaneHistorique.setVisible(cbHisto.isSelected());
            settingPanel.getItemNotation().setSelected(cbHisto.isSelected());
            pack();
            dialog.dispose();
        });

        btnPanel.add(btnAnnuler);
        btnPanel.add(btnOK);

        JPanel corps = new JPanel(new GridLayout(2, 1, 0, 14));
        corps.setOpaque(false);
        corps.add(checkPanel);
        corps.add(btnPanel);

        p.add(titreLabel, BorderLayout.NORTH);
        p.add(corps,      BorderLayout.CENTER);

        dialog.setContentPane(p);
        dialog.pack();
        dialog.setMinimumSize(new Dimension(300, 0));
        dialog.setLocationRelativeTo(this);
        dialog.setVisible(true);
    }

    private JCheckBox creerCB(String texte, boolean val) {
        JCheckBox cb = new JCheckBox(texte, val);
        cb.setFont(new Font("SansSerif", Font.PLAIN, 13));
        cb.setForeground(GRIS_TEXTE);
        cb.setOpaque(false);
        cb.setFocusPainted(false);
        return cb;
    }

    private JButton creerBtnDialog(String texte, Color fond, Color hover) {
        JButton btn = new JButton(texte) {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(getModel().isRollover() ? hover : fond);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 8, 8);
                super.paintComponent(g);
            }
        };
        btn.setFont(new Font("SansSerif", Font.PLAIN, 13));
        btn.setForeground(GRIS_TEXTE);
        btn.setContentAreaFilled(false);
        btn.setBorderPainted(false);
        btn.setFocusPainted(false);
        btn.setPreferredSize(new Dimension(0, 36));
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        return btn;
    }

    // ── API publique ──────────────────────────────────────────────
    public BoardPanel      getBoardPanel()           { return boardPanel; }
    public EvaluationPanel getEvaluationPanel()      { return evaluationPanel; }
    public JScrollPane     getScrollPaneHistorique() { return scrollPaneHistorique; }
    public SettingPanel    getSettingPanel()          { return settingPanel; }

    public void setOnForfait(Runnable r)        { this.onForfait = r; }
    public void setOnPause(Runnable r)          { this.onPause = r; }
    public void setOnNouvellePartie(Runnable r) { this.onNouvellePartie = r; }

    /**
     * Ajoute un texte à l'historique des coups,
     * en scrollant automatiquement vers le bas pour afficher le dernier coup ajouté.
     * @param texte le texte à ajouter à l'historique (ex : "1. e4 e5 2. Nf3 Nc6")
     */
    public void ajouterCoup(String texte) {
        historiqueArea.append(texte);
        historiqueArea.setCaretPosition(historiqueArea.getDocument().getLength());
    }

    public void mettreAJourJauge(double score) { evaluationPanel.setScore(score); }

    /**
     * Ajoute une pièce capturée à l'affichage du joueur qui a capturé (parLesBlancs = true → joueur bas, sinon joueur haut)
     * @param piece la pièce capturée à ajouter à l'affichage
     * @param parLesBlancs true si la pièce a été capturée par les blancs (afficher dans panelJoueurBas), false si capturée par les noirs (afficher dans panelJoueurHaut)
     */
    public void ajouterPieceCaptured(Piece piece, boolean parLesBlancs) {
        if (parLesBlancs) panelJoueurBas.ajouterPiece(piece);
        else              panelJoueurHaut.ajouterPiece(piece);
    }

    /**
     * Met à jour le timer d'un joueur (en secondes), converti en mm:ss.
     * Le timer doit être initialisé au départ via le
     * @param estBlanc true pour mettre à jour le timer du joueur blanc (panelJoueurBas), false pour le joueur noir (panelJoueurHaut)
     * @param secondes le nombre de secondes restantes à afficher pour ce joueur
     */
    public void setTempsJoueur(boolean estBlanc, int secondes) {
        if (estBlanc) panelJoueurBas.setTemps(secondes);
        else          panelJoueurHaut.setTemps(secondes);
    }

    // Setter
    public void setChessController(ChessController c) { this.chessController = c; }
    public void setOnJaugeToggle(Runnable r) { this.onJaugeToggle = r; }


}