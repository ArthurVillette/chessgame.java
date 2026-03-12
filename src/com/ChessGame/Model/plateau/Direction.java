package com.ChessGame.Model.plateau;

/**
 * Enum représentant les directions de déplacement possibles sur le plateau
 */
public enum Direction {
    HAUT(0, -1),
    BAS(0, 1),
    GAUCHE(-1, 0),
    DROITE(1, 0),
    HAUT_GAUCHE(-1, -1),
    HAUT_DROITE(1, -1),
    BAS_GAUCHE(-1, 1),
    BAS_DROITE(1, 1);

    public final int dx;
    public final int dy;

    Direction(int dx, int dy) {
        this.dx = dx;
        this.dy = dy;
    }
}