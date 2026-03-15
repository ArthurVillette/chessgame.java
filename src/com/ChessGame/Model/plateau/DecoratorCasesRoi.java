package com.ChessGame.Model.plateau;

import com.ChessGame.Model.ChessPieces.King;
import com.ChessGame.Model.ChessPieces.Rook;
import com.ChessGame.Model.ChessPieces.Piece;

import java.util.ArrayList;

/**
 * Décorateur qui calcule les cases accessibles pour le Roi.
 * Gère : 1 pas dans toutes les directions + roque petit et grand.
 */
public class DecoratorCasesRoi extends DecoratorCasesAccessibles {

    public DecoratorCasesRoi(DecoratorCasesAccessibles base) {
        super(base);
    }

    @Override
    public ArrayList<Case> getMesCasesAccessibles(Case position) {
        ArrayList<Case> cases = new ArrayList<>();

        // Mouvements normaux : 1 pas dans chaque direction
        for (Direction dir : Direction.values()) {
            Case c = caseAccessibleSiLibre(position.x + dir.dx, position.y + dir.dy);
            if (c != null) cases.add(c);
        }

        // Roque
        King roi = (King) piece;
        if (!roi.aBouge()) {
            int y = position.y;

            // Petit roque (côté roi, droite)
            Piece tourDroite = plateau.getPiece(7, y);
            if (tourDroite instanceof Rook && !((Rook) tourDroite).aBouge()
                    && plateau.getPiece(5, y) == null
                    && plateau.getPiece(6, y) == null) {
                cases.add(new Case(6, y)); // case d'arrivée du roi
            }

            // Grand roque (côté dame, gauche)
            Piece tourGauche = plateau.getPiece(0, y);
            if (tourGauche instanceof Rook && !((Rook) tourGauche).aBouge()
                    && plateau.getPiece(1, y) == null
                    && plateau.getPiece(2, y) == null
                    && plateau.getPiece(3, y) == null) {
                cases.add(new Case(2, y)); // case d'arrivée du roi
            }
        }

        return cases;
    }
}