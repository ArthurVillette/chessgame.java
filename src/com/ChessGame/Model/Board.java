package com.ChessGame.Model;

import java.awt.*;

/**
 * Classe représentant le plateau d'échecs
 */
public class Board {
    private Piece[][] board;

    /**
     * Constructeur de la classe Board qui initialise le plateau d'échecs avec les
     * pièces dans leur position de départ
     */
    public Board() {
        board = new Piece[8][8];
        initializeBoard();
    }

    /**
     * Constructeur de copie pour créer une nouvelle instance de Board à partir
     * d'une instance existante
     * 
     * @param source L'instance de Board à copier
     */
    public Board(Board source) {
        this.board = new Piece[8][8];
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                this.board[i][j] = source.board[i][j];
            }
        }
    }

    /**
     * Initialise le plateau d'échecs avec les pièces dans leur position de départ
     */
    private void initializeBoard() {
        // Initialisation des pièces blanches
        board[0][7] = new Rook(Color.WHITE);
        board[1][7] = new Knight(Color.WHITE);
        board[2][7] = new Bishop(Color.WHITE);
        board[3][7] = new Queen(Color.WHITE);
        board[4][7] = new King(Color.WHITE);
        board[5][7] = new Bishop(Color.WHITE);
        board[6][7] = new Knight(Color.WHITE);
        board[7][7] = new Rook(Color.WHITE);

        for (int lin = 0; lin < 8; lin++) {
            board[lin][6] = new Pawn(Color.WHITE);
        }
        // Initialisation des pions noirs
        board[0][0] = new Rook(Color.BLACK);
        board[1][0] = new Knight(Color.BLACK);
        board[2][0] = new Bishop(Color.BLACK);
        board[3][0] = new Queen(Color.BLACK);
        board[4][0] = new King(Color.BLACK);
        board[5][0] = new Bishop(Color.BLACK);
        board[6][0] = new Knight(Color.BLACK);
        board[7][0] = new Rook(Color.BLACK);
        for (int lin = 0; lin < 8; lin++) {
            board[lin][1] = new Pawn(Color.BLACK);
        }
    }

    /**
     * Récupère la pièce à une position donnée sur le plateau
     * 
     * @param row la ligne de la case
     * @param col la colonne de la case
     * @return la pièce présente à la position (row, col) ou null si la case est
     *         vide
     */
    public Piece getPiece(int row, int col) {
        return board[row][col];
    }

    /**
     * Place une pièce à une position donnée sur le plateau
     * 
     * @param row   la ligne de la case
     * @param col   la colonne de la case
     * @param piece la pièce à placer à la position (row, col)
     */
    public void setPiece(int row, int col, Piece piece) {
        board[row][col] = piece;
    }

    /**
     * Convertit l'état actuel du plateau en chaîne FEN (Forsyth-Edwards Notation)
     * 
     * @param tourBlancs true si c'est aux blancs de jouer, false pour les noirs
     * @return La chaîne FEN représentant la position
     */
    public String toFEN(boolean tourBlancs) {
        StringBuilder fen = new StringBuilder();

        for (int y = 0; y < 8; y++) {
            int casesVides = 0;
            for (int x = 0; x < 8; x++) {
                Piece p = getPiece(x, y);
                if (p == null) {
                    casesVides++;
                } else {
                    if (casesVides > 0) {
                        fen.append(casesVides);
                        casesVides = 0;
                    }

                    char lettre = p.getSymbol();

                    if (p.getColor() == java.awt.Color.WHITE) {
                        lettre = Character.toUpperCase(lettre);
                    }
                    fen.append(lettre);
                }
            }
            if (casesVides > 0) {
                fen.append(casesVides);
            }
            if (y < 7) {
                fen.append("/");
            }
        }
        fen.append(tourBlancs ? " w " : " b ");

        // 3. Roque, En passant, Demi-coups, Coups (Simplifié pour l'instant)
        // "KQkq" signifie que tout le monde peut roquer. "-" signifie pas de prise en
        // passant.
        fen.append("- - 0 1");

        return fen.toString();
    }

}
