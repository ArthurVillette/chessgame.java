package com.ChessGame.Model.plateau;

import com.ChessGame.Model.ChessPieces.Piece;

import java.util.ArrayList;

/**
 * Classe abstraite représentant le Décorateur de Cases Accessibles (DCA).
 * C'est le cœur du pattern Décorateur pour les mouvements des pièces.
 * Chaque décorateur concret sait calculer SES propres cases accessibles
 * (getMesCasesAccessibles), et getCasesAccessibles() fusionne automatiquement
 * toute la chaîne via l'attribut "base".
 */
public abstract class DecoratorCasesAccessibles {

    protected Board plateau;
    protected Piece piece;

    private final DecoratorCasesAccessibles base;

    /**
     * Constructeur
     * @param base Le décorateur suivant dans la chaîne (null si dernier)
     */
    public DecoratorCasesAccessibles(DecoratorCasesAccessibles base) {
        this.base = base;
    }

    /**
     * Injecte le plateau et la pièce dans toute la chaîne de décorateurs.
     * Appelé automatiquement par Piece lors de l'initialisation.
     */
    public void init(Board plateau, Piece piece) {
        this.plateau = plateau;
        this.piece = piece;
        if (base != null) {
            base.init(plateau, piece);
        }
    }

    /**
     * Retourne TOUTES les cases accessibles : les miennes + celles de la chaîne.
     */
    public ArrayList<Case> getCasesAccessibles(Case position) {
        ArrayList<Case> retour = getMesCasesAccessibles(position);
        if (base != null) {
            retour.addAll(base.getCasesAccessibles(position));
        }
        return retour;
    }

    /**
     * Retourne uniquement les cases accessibles par CE décorateur.
     * Chaque sous-classe implémente sa propre logique de mouvement.
     */
    public abstract ArrayList<Case> getMesCasesAccessibles(Case position);



    /**
     * Explore une direction en ligne droite jusqu'à rencontrer un obstacle.
     * Utilisé par Tour (HV) et Fou (Diag).
     */
    protected ArrayList<Case> explorerDirection(Case pos, Direction dir) {
        ArrayList<Case> cases = new ArrayList<>();
        int x = pos.x + dir.dx;
        int y = pos.y + dir.dy;

        while (x >= 0 && x < 8 && y >= 0 && y < 8) {
            Case c = new Case(x, y);
            Piece occupant = plateau.getPiece(x, y);

            if (occupant == null) {
                // Case vide : on peut aller là et continuer
                cases.add(c);
            } else if (!occupant.getColor().equals(piece.getColor())) {
                // Pièce adverse : on peut capturer mais on s'arrête
                cases.add(c);
                break;
            } else {
                // Pièce alliée : bloqué
                break;
            }
            x += dir.dx;
            y += dir.dy;
        }
        return cases;
    }

    /**
     * Vérifie si une case est accessible pour un saut (cavalier, roi, pion).
     * Retourne la case si accessible, null sinon.
     */
    protected Case caseAccessibleSiLibre(int x, int y) {
        if (x < 0 || x >= 8 || y < 0 || y >= 8) return null;
        Piece occupant = plateau.getPiece(x, y);
        if (occupant == null || !occupant.getColor().equals(piece.getColor())) {
            return new Case(x, y);
        }
        return null;
    }
}