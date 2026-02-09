package com.ChessGame.Model;
import java.awt.Color; 

public abstract class Piece {
    protected Color color; 
    protected char symbol;

    public Piece(Color color, char symbol) {
        this.color = color;
        this.symbol = symbol;
    }

    public Color getColor() {
        return color;
    }

    public char getSymbol() {
        return symbol;
    }

    public String getImagePath() {
        String colorP = (color == Color.WHITE) ? "w" : "b";
        return "/assets/pieces/" + colorP + symbol + ".png";

}
}