package com.ChessGame.Model.plateau;

import java.util.ArrayList;

/**
 * Décorateur qui calcule les cases accessibles pour le Cavalier (sauts en L).
 */
public class DecoratorCasesCavalier extends DecoratorCasesAccessibles {

    public DecoratorCasesCavalier(DecoratorCasesAccessibles base) {
        super(base);
    }

    @Override
    public ArrayList<Case> getMesCasesAccessibles(Case position) {
        ArrayList<Case> cases = new ArrayList<>();

        int[][] sauts = {
                {-2, -1}, {-2, 1},
                {-1, -2}, {-1, 2},
                { 1, -2}, { 1, 2},
                { 2, -1}, { 2, 1}
        };

        for (int[] saut : sauts) {
            Case c = caseAccessibleSiLibre(position.x + saut[0], position.y + saut[1]);
            if (c != null) cases.add(c);
        }

        return cases;
    }
}