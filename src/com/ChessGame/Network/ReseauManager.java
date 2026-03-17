package com.ChessGame.Network;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.function.Consumer;

/**
 * Gère la connexion réseau entre deux PC (Hôte ou Client)
 */
public class ReseauManager {
    private Socket socket;
    private PrintWriter out;
    private BufferedReader in;
    private Thread threadEcoute;

    /**
     * PC 1 : Héberge la partie
     * 
     * @param port       Le port (ex: 5000)
     * @param onCoupRecu La fonction à exécuter quand l'adversaire joue
     */
    public void hebergerPartie(int port, Consumer<String> onCoupRecu) {
        new Thread(() -> {
            try {
                System.out.println("En attente d'un adversaire sur le port " + port + "...");
                ServerSocket serverSocket = new ServerSocket(port);
                socket = serverSocket.accept();
                System.out.println("Un adversaire s'est connecté !");

                initialiserFlux(onCoupRecu);
            } catch (Exception e) {
                System.err.println("Erreur d'hébergement : " + e.getMessage());
            }
        }).start();
    }

    /**
     * PC 2 : Rejoint la partie
     * 
     * @param ip   L'adresse IP du PC 1 (ex: "192.168.1.15")
     * @param port Le port (ex: 5000)
     */
    public void rejoindrePartie(String ip, int port, Consumer<String> onCoupRecu) {
        new Thread(() -> {
            try {
                System.out.println("Connexion à " + ip + ":" + port + "...");
                socket = new Socket(ip, port);
                System.out.println("Connecté à l'hôte !");

                initialiserFlux(onCoupRecu);
            } catch (Exception e) {
                System.err.println("Erreur de connexion : " + e.getMessage());
            }
        }).start();
    }

    /**
     * Prépare les tuyaux de communication et écoute en boucle
     */
    private void initialiserFlux(Consumer<String> onCoupRecu) throws Exception {
        out = new PrintWriter(socket.getOutputStream(), true);
        in = new BufferedReader(new InputStreamReader(socket.getInputStream()));

        threadEcoute = new Thread(() -> {
            try {
                String message;
                while ((message = in.readLine()) != null) {
                    System.out.println("Réseau - Coup reçu : " + message);
                    onCoupRecu.accept(message);
                }
            } catch (Exception e) {
                System.out.println("Connexion perdue.");
            }
        });
        threadEcoute.start();
    }

    /**
     * Envoie le coup que l'on vient de jouer à l'adversaire
     */
    public void envoyerCoup(String coup) {
        if (out != null) {
            out.println(coup);
        }
    }
}