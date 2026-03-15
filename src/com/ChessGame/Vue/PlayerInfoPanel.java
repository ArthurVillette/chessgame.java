package com.ChessGame.Vue;

import com.ChessGame.Model.ChessPieces.Piece;
import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Bande joueur style chess.com :
 * [●] Nom joueur    ♟♟♟ +2    [timer]
 */
public class PlayerInfoPanel extends JPanel {

    // ── Palette cohérente ──────────────────────────────────────────
    private static final Color FOND        = new Color(38, 52, 38);
    private static final Color FOND_TIMER  = new Color(22, 36, 22);
    private static final Color BEIGE       = new Color(235, 235, 208);
    private static final Color GRIS_CLAIR  = new Color(160, 170, 150);
    private static final Color VERT_VIF    = new Color(119, 148, 85);
    private static final Color OR          = new Color(212, 175, 55);
    private static final Color BLANC_PIECE = new Color(230, 230, 210);
    private static final Color NOIR_PIECE  = new Color(60, 60, 60);

    private final boolean estBlanc;
    private String nomJoueur;
    private List<Piece> piecesCaptured = new ArrayList<>();
    private int secondesRestantes = -1; // -1 = pas de timer

    // Sous-composants
    private JLabel labelNom;
    private JLabel labelTimer;
    private JPanel piecesPanel;

    public PlayerInfoPanel(boolean estBlanc, String nomJoueur) {
        this.estBlanc = estBlanc;
        this.nomJoueur = nomJoueur;

        setBackground(FOND);
        setPreferredSize(new Dimension(0, 46));
        setLayout(new BorderLayout(8, 0));
        setBorder(BorderFactory.createEmptyBorder(6, 10, 6, 10));

        // ── Gauche : pion coloré + nom ──────────────────────────
        JPanel gauchePanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 6, 0));
        gauchePanel.setOpaque(false);

        // Pion indicateur de couleur
        JLabel pion = new JLabel(estBlanc ? "♔" : "♚");
        pion.setFont(new Font("Serif", Font.PLAIN, 18));
        pion.setForeground(estBlanc ? BLANC_PIECE : new Color(80, 80, 80));

        labelNom = new JLabel(nomJoueur);
        labelNom.setFont(new Font("Serif", Font.BOLD, 14));
        labelNom.setForeground(BEIGE);

        gauchePanel.add(pion);
        gauchePanel.add(labelNom);

        // ── Centre : pièces capturées ────────────────────────────
        piecesPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                dessinerPiecesCaptured((Graphics2D) g);
            }
        };
        piecesPanel.setOpaque(false);
        piecesPanel.setPreferredSize(new Dimension(250, 30));

        // ── Droite : timer ───────────────────────────────────────
        labelTimer = new JLabel("");
        labelTimer.setFont(new Font("Monospaced", Font.BOLD, 15));
        labelTimer.setForeground(BEIGE);
        labelTimer.setOpaque(true);
        labelTimer.setBackground(FOND_TIMER);
        labelTimer.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(60, 80, 60), 1, true),
                BorderFactory.createEmptyBorder(3, 10, 3, 10)
        ));
        labelTimer.setHorizontalAlignment(SwingConstants.CENTER);
        labelTimer.setPreferredSize(new Dimension(90, 30));
        labelTimer.setVisible(false);

        add(gauchePanel, BorderLayout.WEST);
        add(piecesPanel, BorderLayout.CENTER);
        add(labelTimer, BorderLayout.EAST);
    }

    // ── API publique ─────────────────────────────────────────────────

    public void ajouterPiece(Piece piece) {
        piecesCaptured.add(piece);
        piecesPanel.repaint();
    }

    public void setPiecesCaptured(List<Piece> pieces) {
        this.piecesCaptured = new ArrayList<>(pieces);
        piecesPanel.repaint();
    }

    public void setTemps(int secondes) {
        this.secondesRestantes = secondes;
        int min = secondes / 60;
        int sec = secondes % 60;
        labelTimer.setText(String.format("%d:%02d", min, sec));
        labelTimer.setVisible(true);
        // Rouge si <30s
        labelTimer.setForeground(secondes < 30 ? new Color(220, 80, 80) : BEIGE);
        labelTimer.repaint();
    }

    public void setNom(String nom) {
        this.nomJoueur = nom;
        labelNom.setText(nom);
    }

    // ── Dessin des pièces capturées ──────────────────────────────────

    private void dessinerPiecesCaptured(Graphics2D g2) {
        g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

        // Les pièces affichées ici sont celles capturées PAR ce joueur
        // → si joueur blanc, il a capturé des pièces noires → on affiche symboles noirs
        String[] symboles = estBlanc
                ? new String[]{"♟","♝","♞","♜","♛"}   // pièces noires capturées par Blancs
                : new String[]{"♙","♗","♘","♖","♕"};  // pièces blanches capturées par Noirs
        char[]   types   = {'p','b','n','r','q'};
        int[]    valeurs = { 1,  3,  3,  5,  9 };

        int[] comptes = new int[5];
        int totalValeur = 0;
        for (Piece p : piecesCaptured) {
            char sym = Character.toLowerCase(p.getSymbol());
            for (int i = 0; i < types.length; i++) {
                if (sym == types[i]) {
                    comptes[i]++;
                    totalValeur += valeurs[i];
                    break;
                }
            }
        }

        g2.setFont(new Font("Serif", Font.BOLD, 17));
        FontMetrics fm = g2.getFontMetrics();
        int x = 4;
        int y = (piecesPanel.getHeight() + fm.getAscent() - fm.getDescent()) / 2;

        // Couleurs contrastées sur fond vert foncé + ombre pour lisibilité
        // Pièces blanches capturées (joueur noir) → blanc cassé bien visible
        // Pièces noires capturées (joueur blanc)  → ombre claire derrière symbole sombre
        Color couleurPiece = estBlanc ? new Color(210, 210, 190) : new Color(75, 75, 65);
        Color couleurOmbre = estBlanc ? new Color(20, 35, 20)    : new Color(215, 215, 195);

        for (int i = 0; i < types.length; i++) {
            if (comptes[i] > 0) {
                for (int j = 0; j < comptes[i]; j++) {
                    // Ombre décalée d1px → contraste garanti sur fond vert
                    g2.setColor(couleurOmbre);
                    g2.drawString(symboles[i], x + 1, y + 1);
                    g2.setColor(couleurPiece);
                    g2.drawString(symboles[i], x, y);
                    x += 15;
                }
                x += 3;
            }
        }

        if (totalValeur > 0) {
            g2.setFont(new Font("SansSerif", Font.BOLD, 11));
            g2.setColor(OR);
            g2.drawString("+" + totalValeur, x + 4, y - 1);
        }
    }
}