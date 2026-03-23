package com.ChessGame.Model.plateau;

import com.ChessGame.Model.ChessPieces.Bishop;
import com.ChessGame.Model.ChessPieces.Pawn;
import com.ChessGame.Model.jeu.Coup;
import com.ChessGame.Model.ChessPieces.Piece;

import java.awt.Color;
import java.util.ArrayList;

/**
 * Décorateur qui calcule les cases accessibles pour le Pion.
 * Gère : avance, double pas initial, capture diagonale, prise en passant.
 */
public class DecoratorCasesPion extends DecoratorCasesAccessibles {

    public DecoratorCasesPion(DecoratorCasesAccessibles base) {
        super(base);
    }

    @Override
    public ArrayList<Case> getMesCasesAccessibles(Case position) {
        ArrayList<Case> cases = new ArrayList<>();

        int direction = piece.getColor().equals(Color.WHITE) ? -1 : 1;
        int ligneDepart = piece.getColor().equals(Color.WHITE) ? 6 : 1;

        int x = position.x;
        int y = position.y;

        // Avancer d'une case
        if (y + direction >= 0 && y + direction < 8) {
            if (plateau.getPiece(x, y + direction) == null) {
                cases.add(new Case(x, y + direction));

                // Double pas depuis la ligne de départ
                if (y == ligneDepart && plateau.getPiece(x, y + 2 * direction) == null) {
                    cases.add(new Case(x, y + 2 * direction));
                }
            }
        }

        // Capture en diagonale
        for (int dx : new int[]{-1, 1}) {
            int cx = x + dx;
            int cy = y + direction;
            if (cx >= 0 && cx < 8 && cy >= 0 && cy < 8) {
                Piece cible = plateau.getPiece(cx, cy);
                if (cible != null && !cible.getColor().equals(piece.getColor())) {
                    cases.add(new Case(cx, cy));
                }
            }
        }

        // Prise en passant
        Coup dernierCoup = plateau.getDernierCoup();
        if (dernierCoup != null) {
            Piece pieceAdverse = plateau.getPiece(dernierCoup.arrivee.x, dernierCoup.arrivee.y);
            boolean estUnPion        = pieceAdverse instanceof Pawn;
            boolean aAvanceDeDeux    = Math.abs(dernierCoup.arrivee.y - dernierCoup.depart.y) == 2;
            boolean estSurMaLigne    = dernierCoup.arrivee.y == y;
            boolean estACoté         = Math.abs(dernierCoup.arrivee.x - x) == 1;

            if (estUnPion && aAvanceDeDeux && estSurMaLigne && estACoté) {
                cases.add(new Case(dernierCoup.arrivee.x, y + direction));
            }
        }

        return cases;
    }
}