package com.ChessGame.Model;
import java.awt.Point;

/**
 * Classe représentant un coup de jeu, avec une position de départ et une position d'arrivée
 */
public class Coup {
    public Point depart;
    public Point arrivee;

    /**
     * Constructeur de la classe Coup
     * @param depart La position de départ du coup (coordonnées x et y)
     * @param arrivee La position d'arrivée du coup (coordonnées x et y)
     */
    public Coup(Point depart, Point arrivee) {
        this.depart = depart;
        this.arrivee = arrivee;
    }
}