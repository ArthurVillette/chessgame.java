package com.ChessGame.Model.plateau;

import java.awt.Point;

/**
 * Classe représentant une case du plateau d'échecs
 */
public class Case {
    public final int x;
    public final int y;

    public Case(int x, int y) {
        this.x = x;
        this.y = y;
    }

    /**
     * Convertit une Case en Point
     */
    public Point toPoint() {
        return new Point(x, y);
    }

    /**
     * Crée une Case depuis un Point
     */
    public static Case fromPoint(Point p) {
        return new Case(p.x, p.y);
    }

    /**
     * Vérifie si la case est dans les limites du plateau d'échecs
     */
    public boolean estDansLePlateau() {
        return x >= 0 && x < 8 && y >= 0 && y < 8;
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Case)) return false;
        Case c = (Case) o;
        return x == c.x && y == c.y;
    }

    @Override
    public int hashCode() {
        return 31 * x + y;
    }

    @Override
    public String toString() {
        return "Case(" + x + ", " + y + ")";
    }
}