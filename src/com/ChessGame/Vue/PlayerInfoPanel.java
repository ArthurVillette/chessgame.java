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
    private static final Color OR          = new Color(212, 175, 55);
    private static final Color BLANC_PIECE = new Color(230, 230, 210);

    private final boolean estBlanc;
    private String nomJoueur;
    private List<Piece> piecesCaptured = new ArrayList<>();
    private int secondesRestantes = -1; // -1 = pas de timer

    // Sous-composants
    private final JLabel labelNom;
    private final JLabel labelTimer;
    private final JPanel piecesPanel;

    /**
     * Constructeur du panneau d'information d'un joueur
     * @param estBlanc true si joueur blanc, false si joueur noir (détermine couleur pion + pièces capturées affichées)
     * @param nomJoueur nom du joueur à afficher
     */
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
        pion.setFont(new Font("Serif", Font.PLAIN, 28));
        pion.setForeground(estBlanc ? BLANC_PIECE : new Color(80, 80, 80));

        labelNom = new JLabel(nomJoueur);
        labelNom.setFont(new Font("Serif", Font.BOLD, 19));
        labelNom.setForeground(OR);

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
        piecesPanel.setPreferredSize(new Dimension(250, 40));

        // ── Droite : timer ───────────────────────────────────────
        labelTimer = new JLabel("");
        labelTimer.setFont(new Font("Monospaced", Font.BOLD, 15));
        labelTimer.setForeground(BEIGE);
        labelTimer.setOpaque(true);
        labelTimer.setBackground(FOND_TIMER);
        labelTimer.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(60, 80, 60), 1, true),
                //BorderFactory.createEmptyBorder(3, 10, 3, 10)
                BorderFactory.createEmptyBorder(4, 12, 4, 12)
        ));
        labelTimer.setHorizontalAlignment(SwingConstants.CENTER);
        labelTimer.setPreferredSize(new Dimension(90, 30));
        labelTimer.setVisible(false);

        add(gauchePanel, BorderLayout.WEST);
        add(piecesPanel, BorderLayout.CENTER);
        add(labelTimer, BorderLayout.EAST);
    }

    /**
     * Ajoute une pièce à la liste des pièces capturées par ce joueur
     * @param piece la pièce capturée à ajouter (sera affichée au centre du panneau)
     */
    public void ajouterPiece(Piece piece) {
        piecesCaptured.add(piece);
        piecesPanel.repaint();
    }



    /**
     * Met à jour le timer du joueur avec le nombre de secondes restantes
     * @param secondes le nombre de secondes restantes pour ce joueur (affiché au format "m:ss", en rouge si <30s)
     */
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



    /**
     * Dessine les pièces capturées par ce joueur au centre du panneau
     * @param g2 le contexte graphique pour dessiner les symboles des pièces capturées et leur valeur totale
     */
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
            g2.setFont(new Font("SansSerif", Font.BOLD, 13));
            g2.setColor(OR);
            g2.drawString("+" + totalValeur, x + 4, y - 1);
        }
    }
}