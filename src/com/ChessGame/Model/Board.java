package com.ChessGame.Model;
import java.awt.Color;

/**
 * Classe représentant le plateau d'échecs
 */
public class Board {
    private Piece[][] board;

    /**
     * Constructeur de la classe Board qui initialise le plateau d'échecs avec les pièces dans leur position de départ
     */
    public Board() {
        board = new Piece[8][8];
        initializeBoard();
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
     * @param row la ligne de la case
     * @param col la colonne de la case
     * @return la pièce présente à la position (row, col) ou null si la case est vide
     */
    public Piece getPiece(int row, int col) {
        return board[row][col];
    }
}

