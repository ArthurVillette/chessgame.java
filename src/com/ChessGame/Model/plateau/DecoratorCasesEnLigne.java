package com.ChessGame.Model.plateau;

import java.util.ArrayList;

/**
 * Décorateur qui calcule les cases accessibles en horizontal et vertical.
 * Utilisé par : Tour (seul), Reine (combiné avec Diagonal)
 */
public class DecoratorCasesEnLigne extends DecoratorCasesAccessibles {

    public DecoratorCasesEnLigne(DecoratorCasesAccessibles base) {
        super(base);
    }

    @Override
    public ArrayList<Case> getMesCasesAccessibles(Case position) {
        ArrayList<Case> cases = new ArrayList<>();
        cases.addAll(explorerDirection(position, Direction.HAUT));
        cases.addAll(explorerDirection(position, Direction.BAS));
        cases.addAll(explorerDirection(position, Direction.GAUCHE));
        cases.addAll(explorerDirection(position, Direction.DROITE));
        return cases;
    }
}