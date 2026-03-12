package com.ChessGame.Model;

import com.ChessGame.Model.plateau.*;
import java.awt.Color;

/**
 * Classe représentant le roi dans le jeu d'échecs, avec des fonctionnalités spécifiques telles que le suivi du déplacement pour le roque
 * et l'utilisation d'un décorateur pour définir les mouvements possibles du roi
 */
public class King extends Piece {
    private boolean aBouge = false;

    public King(Color color) {
        super(color, new DecoratorCasesRoi(null));
    }

    public boolean aBouge() { return aBouge; }
    public void setABouge()  { this.aBouge = true; }

    @Override public char getSymbol() { return 'k'; }
    @Override public String getImagePath() {
        return color == Color.WHITE ? "/assets/pieces/wk.png" : "/assets/pieces/bk.png";
    }
}