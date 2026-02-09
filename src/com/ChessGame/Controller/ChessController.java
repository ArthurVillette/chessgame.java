package com.ChessGame.Controller;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import com.ChessGame.Model.Board;
import com.ChessGame.Vue.ChessFrame;
import com.ChessGame.Vue.BoardPanel;

public class ChessController extends MouseAdapter {
    private Board model;
    private ChessFrame view;
    private BoardPanel boardPanel;

    /**
     * Constructeur du contrôleur d'échecs
     * @param model Le modèle du plateau d'échecs
     * @param view La vue principale de l'application
     */
    public ChessController(Board model, ChessFrame view) {
        this.model = model;
        this.view = view;
        this.boardPanel = view.getBoardPanel();
    }

   @Override
    public void mousePressed(MouseEvent e) {
    int x = e.getX() / boardPanel.TILE_SIZE; 
    int y = e.getY() / boardPanel.TILE_SIZE;

    boardPanel.setSelection(x, y);
}
}
    
