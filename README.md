# ♟️ Poisson Bloqué - Jeu d'Échecs

Un jeu d'échecs complet développé en Java, incluant la possibilité de jouer en réseau local, contre un adversaire humain, ou contre des intelligences artificielles (Stockfish et Villette).

## 📋 Prérequis

Avant de lancer le projet, assurez-vous d'avoir les éléments suivants sur votre machine :

1. **Java (JDK)** installé sur votre ordinateur.
2. **Stockfish** installé sur votre machine.
3. Un fichier de configuration **`.env`** placé à la racine du projet.

### Configuration du fichier `.env`
Créez un fichier nommé `.env` à la racine de votre projet et ajoutez-y les chemins vers vos moteurs IA. 

Exemple de contenu (à adapter selon votre système) :
```env
# Chemin vers l'exécutable Stockfish
CHEMIN_STOCKFISH=stockfish

# Chemin vers le lanceur de votre IA personnalisée (ex: Linux)
CHEMIN_VILLETTE=./lanceur_villette.sh 
```

#### github
https://github.com/ArthurVillette/chessgame.java