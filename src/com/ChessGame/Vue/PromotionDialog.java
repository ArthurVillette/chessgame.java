package com.ChessGame.Vue;

import com.ChessGame.Model.ChessPieces.*;
import com.ChessGame.Model.jeu.*;

import javax.swing.*;
import java.awt.*;
import java.util.Observable;
import java.util.Observer;

/**
 * Vue responsable de la popup de promotion.
 * S'enregistre comme Observer sur Partie.
 * Quand elle reçoit un EvenementPromotion, elle affiche la popup
 * et renvoie le choix à Partie via setChoixPromotion().
 */
public class PromotionDialog implements Observer {

    private Partie partie;

    public PromotionDialog(Partie partie) {
        this.partie = partie;
        partie.addObserver(this);
    }

    /**
     * Reçoit les notifications de Partie.
     * Réagit uniquement aux EvenementPromotion.
     */
    @Override
    public void update(Observable o, Object arg) {
        if (!(arg instanceof EvenementPromotion)) return;

        EvenementPromotion evt = (EvenementPromotion) arg;


        SwingUtilities.invokeLater(() -> afficherPopup(evt));
    }

    /**
     * Affiche la popup avec les 4 pièces au choix (images).
     * Appelle partie.setChoixPromotion() quand le joueur clique.
     */
    private void afficherPopup(EvenementPromotion evt) {
        Color couleur = evt.couleur;
        String prefix = couleur.equals(Color.WHITE) ? "w" : "b";

        JDialog dialog = new JDialog();
        dialog.setTitle("Promotion — Choisissez une pièce");
        dialog.setModal(true);
        dialog.setLayout(new FlowLayout(FlowLayout.CENTER, 15, 15));
        dialog.setResizable(false);

        // Définition des 4 options
        String[] symboles = {"q", "r", "b", "n"};
        String[] labels   = {"Reine", "Tour", "Fou", "Cavalier"};

        for (int i = 0; i < symboles.length; i++) {
            final int index = i;
            String cheminImage = "assets/pieces/" + prefix + symboles[i] + ".png";
            ImageIcon icone = chargerIcone(cheminImage, 64, 64);

            JButton btn = new JButton(labels[i], icone);
            btn.setHorizontalTextPosition(SwingConstants.CENTER);
            btn.setVerticalTextPosition(SwingConstants.BOTTOM);
            btn.setPreferredSize(new Dimension(90, 110));
            btn.setFocusPainted(false);

            btn.addActionListener(e -> {
                Piece choix = creerPiece(index, couleur);
                dialog.dispose();
                // Débloquer le thread du jeu dans Partie
                partie.setChoixPromotion(choix);
            });

            dialog.add(btn);
        }

        dialog.pack();
        dialog.setLocationRelativeTo(null);
        dialog.setDefaultCloseOperation(JDialog.DO_NOTHING_ON_CLOSE); // forcer un choix
        dialog.setVisible(true);
    }

    /**
     * Crée la pièce correspondant au bouton cliqué.
     */
    private Piece creerPiece(int index, Color couleur) {
        switch (index) {
            case 0: return new Queen(couleur);
            case 1: return new Rook(couleur);
            case 2: return new Bishop(couleur);
            case 3: return new Knight(couleur);
            default: return new Queen(couleur);
        }
    }

    /**
     * Charge et redimensionne une image depuis le disque.
     */
    private ImageIcon chargerIcone(String chemin, int w, int h) {
        try {
            java.awt.image.BufferedImage img = javax.imageio.ImageIO.read(new java.io.File(chemin));
            Image scaled = img.getScaledInstance(w, h, Image.SCALE_SMOOTH);
            return new ImageIcon(scaled);
        } catch (Exception e) {
            System.out.println("Image manquante : " + chemin);
            return new ImageIcon();
        }
    }
}