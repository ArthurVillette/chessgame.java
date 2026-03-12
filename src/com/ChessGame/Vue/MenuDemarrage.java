package com.ChessGame.Vue;

import javax.swing.*;
import java.awt.*;

/**
 * Fenêtre de dialogue permettant de configurer la partie avant de la lancer
 */
public class MenuDemarrage extends JDialog {
    private boolean contreIA = true;
    private boolean humainEstBlanc = true;
    private boolean startClicked = false;

    /**
     * Constructeur qui initialise les composants du menu de démarrage
     */
    public MenuDemarrage() {
        super((JFrame) null, "Nouvelle Partie", true);
        setSize(350, 200);
        setLocationRelativeTo(null); // Centre à l'écran
        setLayout(new GridLayout(4, 1));

        JPanel modePanel = new JPanel();
        modePanel.add(new JLabel("Adversaire : "));
        JRadioButton btnIA = new JRadioButton("IA (Villette)", true);
        JRadioButton btnHumain = new JRadioButton("Humain (Local)");

        ButtonGroup modeGroup = new ButtonGroup();
        modeGroup.add(btnIA);
        modeGroup.add(btnHumain);
        modePanel.add(btnIA);
        modePanel.add(btnHumain);

        JPanel colorPanel = new JPanel();
        colorPanel.add(new JLabel("Votre couleur : "));
        JRadioButton btnBlanc = new JRadioButton("Blancs", true);
        JRadioButton btnNoir = new JRadioButton("Noirs");
        JRadioButton btnRandom = new JRadioButton("Aléatoire");

        ButtonGroup colorGroup = new ButtonGroup();
        colorGroup.add(btnBlanc);
        colorGroup.add(btnNoir);
        colorGroup.add(btnRandom);
        colorPanel.add(btnBlanc);
        colorPanel.add(btnNoir);
        colorPanel.add(btnRandom);

        btnHumain.addActionListener(e -> {
            btnBlanc.setEnabled(false);
            btnNoir.setEnabled(false);
            btnRandom.setEnabled(false);
        });
        btnIA.addActionListener(e -> {
            btnBlanc.setEnabled(true);
            btnNoir.setEnabled(true);
            btnRandom.setEnabled(true);
        });

        JButton btnJouer = new JButton("Lancer la partie !");
        btnJouer.addActionListener(e -> {
            contreIA = btnIA.isSelected();
            humainEstBlanc = btnBlanc.isSelected();
            startClicked = true;
            dispose();
        });

        // Assemblage
        add(new JLabel("Configuration de la partie", SwingConstants.CENTER));
        add(modePanel);
        add(colorPanel);
        add(btnJouer);
    }

    /**
     * Getters pour récupérer les choix de l'utilisateur
     * 
     * @return les choix de configuration pour la partie
     */
    public boolean isContreIA() {
        return contreIA;
    }

    /**
     * Indique si l'utilisateur a choisi de jouer avec les pièces blanches
     * 
     * @return true si l'utilisateur joue avec les blancs, false sinon
     */
    public boolean isHumainEstBlanc() {
        return humainEstBlanc;
    }

    /**
     * Indique si l'utilisateur a cliqué sur le bouton de démarrage
     * 
     * @return true si l'utilisateur a cliqué sur le bouton, false sinon
     */
    public boolean isStartClicked() {
        return startClicked;
    }
}