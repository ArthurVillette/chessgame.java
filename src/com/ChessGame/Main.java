package com.ChessGame;

import com.ChessGame.Model.Board;
import com.ChessGame.Vue.ChessFrame;
import com.ChessGame.Controller.ChessController;
import javax.swing.SwingUtilities;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;



public class Main {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            

            Board board = new Board(); 
            ChessFrame frame = new ChessFrame(board);
            ChessController controller = new ChessController(board, frame);
            frame.getBoardPanel().addMouseListener(controller);
            frame.setVisible(true);
            
            System.out.println("Le moteur d'échecs est démarré !");
           
        });
    }
}