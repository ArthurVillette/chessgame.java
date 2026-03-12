package com.ChessGame.Model.plateau;

import java.util.ArrayList;

/**
 * Décorateur qui calcule les cases accessibles en diagonale.
 * Utilisé par : Fou (seul), Reine (combiné avec DecoratorCasesEnLigne)
 */
public class DecoratorCasesEnDiagonale extends DecoratorCasesAccessibles {

    public DecoratorCasesEnDiagonale(DecoratorCasesAccessibles base) {
        super(base);
    }

    @Override
    public ArrayList<Case> getMesCasesAccessibles(Case position) {
        ArrayList<Case> cases = new ArrayList<>();
        cases.addAll(explorerDirection(position, Direction.HAUT_GAUCHE));
        cases.addAll(explorerDirection(position, Direction.HAUT_DROITE));
        cases.addAll(explorerDirection(position, Direction.BAS_GAUCHE));
        cases.addAll(explorerDirection(position, Direction.BAS_DROITE));
        return cases;
    }
}