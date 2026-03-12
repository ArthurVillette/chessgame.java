package com.ChessGame.Model;

import com.ChessGame.Model.plateau.*;
import java.awt.Color;

/**
 * La classe Rook représente la tour dans le jeu d'échecs. Elle hérite de la classe Piece et utilise un décorateur pour définir les mouvements en ligne droite (horizontal et vertical).
 * La variable aBouge est utilisée pour suivre si la tour a déjà bougé, ce qui est important pour les règles du roque.
 */
public class Rook extends Piece {
    private boolean aBouge = false;

    public Rook(Color color) {
        super(color, new DecoratorCasesEnLigne(null));
    }

    public boolean aBouge() { return aBouge; }
    public void setABouge()  { this.aBouge = true; }

    @Override public char getSymbol() { return 'r'; }
    @Override public String getImagePath() {
        return color == Color.WHITE ? "/assets/pieces/wr.png" : "/assets/pieces/br.png";
    }
}