package com.ChessGame.Model.IA;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

/**
 * Classe utilitaire pour lire les variables d'environnement depuis le fichier
 * .env
 */
public class Config {
    private static Properties properties = new Properties();
    private static boolean charge = false;

    /**
     * Charge le fichier .env en mémoire (exécuté une seule fois)
     */
    private static void chargerFichierEnv() {
        try (FileInputStream fis = new FileInputStream(".env")) {
            properties.load(fis);
            charge = true;
        } catch (IOException e) {
            System.err.println("Fichier .env introuvable ou illisible. Utilisation des valeurs par défaut.");
            charge = true; // On marque comme chargé pour ne pas boucler sur l'erreur
        }
    }

    /**
     * Récupère la valeur d'une clé dans le fichier .env
     * 
     * @param cle             La clé à chercher (ex: "STOCKFISH_PATH")
     * @param valeurParDefaut La valeur à retourner si la clé n'existe pas
     * @return La valeur trouvée ou la valeur par défaut
     */
    public static String get(String cle, String valeurParDefaut) {
        if (!charge) {
            chargerFichierEnv();
        }
        return properties.getProperty(cle, valeurParDefaut);
    }
}