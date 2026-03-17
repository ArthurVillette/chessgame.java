package com.ChessGame.Model;

import com.ChessGame.Model.IA.IAClient;

import java.io.FileWriter;
import java.util.List;

/**
 * Processus en arrière-plan qui analyse toute la partie avec Stockfish
 */
public class AnalyseurPartie implements Runnable {
    private List<String> historiqueCoups;
    private String cheminStockfish;

    public AnalyseurPartie(List<String> historiqueCoups, String cheminStockfish) {
        this.historiqueCoups = historiqueCoups;
        this.cheminStockfish = cheminStockfish;
    }

    @Override
    public void run() {
        System.out.println("🔍 Lancement de l'analyse Stockfish en arrière-plan...");

        IAClient stockfishAnalyse = new IAClient();
        if (!stockfishAnalyse.demarrerMoteur(cheminStockfish)) {
            System.err.println("Impossible de lancer l'analyseur.");
            return;
        }

        stockfishAnalyse.envoyerCommande("uci");
        stockfishAnalyse.lireReponseComplete("uciok");
        String numeroPartie = String.format("%04d", new java.io.File("./historique/").listFiles().length + 1);

        try (FileWriter writer = new FileWriter("./historique/analyse_partie_" + numeroPartie + ".txt")) {
            writer.write("=== ANALYSE DE LA PARTIE ===\n");
            writer.write("Score positif = Avantage Blancs | Score négatif = Avantage Noirs\n\n");

            String movesJoues = "";
            double scorePrecedent = 0.0;

            for (int i = 0; i < historiqueCoups.size(); i++) {
                String coup = historiqueCoups.get(i);
                movesJoues += coup + " ";

                stockfishAnalyse.envoyerCommande("position startpos moves " + movesJoues);
                stockfishAnalyse.envoyerCommande("go depth 12");

                String reponse = stockfishAnalyse.lireReponseComplete("bestmove");

                double scoreActuel = 0.0;
                String bestMove = "(Fin de partie)";

                boolean blancVientDeJouer = (i % 2 == 0);

                String[] lignes = reponse.split("\n");
                for (String ligne : lignes) {
                    if (ligne.contains("score cp")) {
                        String[] mots = ligne.split(" ");
                        for (int j = 0; j < mots.length; j++) {
                            if (mots[j].equals("cp")) {
                                double cp = Double.parseDouble(mots[j + 1]) / 100.0;
                                scoreActuel = blancVientDeJouer ? -cp : cp;
                            }
                        }
                    } else if (ligne.contains("score mate")) {
                        String[] mots = ligne.split(" ");
                        for (int j = 0; j < mots.length; j++) {
                            if (mots[j].equals("mate")) {
                                int mateIn = Integer.parseInt(mots[j + 1]);
                                double mateScore = (mateIn > 0) ? 100.0 : -100.0;
                                if (mateIn == 0)
                                    mateScore = -100.0; // Le roi est déjà mat
                                scoreActuel = blancVientDeJouer ? -mateScore : mateScore;
                            }
                        }
                    }
                    if (ligne.startsWith("bestmove")) {
                        bestMove = ligne.split(" ")[1];
                        if (bestMove.equals("(none)"))
                            bestMove = "Échec et Mat";
                    }
                }

                double perte;
                if (blancVientDeJouer) {
                    perte = scorePrecedent - scoreActuel;
                } else {
                    perte = scoreActuel - scorePrecedent;
                }

                String appreciation = "";
                if (perte > 2.5)
                    appreciation = " ❓❓ GAFFE (Blunder)";
                else if (perte > 1.0)
                    appreciation = " ❓ ERREUR (Mistake)";
                else if (perte > 0.5 && !coup.equals(bestMove))
                    appreciation = " ?! IMPRÉCISION";
                else if (coup.equals(bestMove))
                    appreciation = " ⭐ EXCELLENT COUP";
                else
                    appreciation = " ✔️ BON COUP";

                String joueur = blancVientDeJouer ? "Blanc" : "Noir";
                writer.write(joueur + " joue : " + coup + appreciation + "\n");

                String affichageScore = scoreActuel > 0 ? "+" + String.format("%.2f", scoreActuel)
                        : String.format("%.2f", scoreActuel);
                if (Math.abs(scoreActuel) >= 99.0)
                    affichageScore = "MAT";

                writer.write("Évaluation : " + affichageScore + " (Meilleur : " + bestMove + ")\n\n");

                scorePrecedent = scoreActuel;
            }

            writer.write("Fin de l'analyse.");
            System.out.println("✅ Analyse terminée ! Ouvrez le fichier 'analyse_partie.txt'.");

        } catch (Exception e) {
            System.err.println("Erreur durant l'analyse : " + e.getMessage());
        }
    }
}