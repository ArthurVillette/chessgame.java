# Création d'un jeu d'échec (PoissonBloqué)

## Prerequie

- Avoir java sur son PC
- Installé Stockfish sur votre PC
- Dans un fichier .env a la racine du projet ajouté le chemin dans votre variable: CHEMIN_STOCKFISH
- pour jouer avec l'ia mettez le chemin en fonction de votre pc pour linux: CHEMIN_VILLETTE= ./lanceur_villette.sh et pour windows CHEMIN_VILLETTE=jsp

## Lancement 

Compilation en linux utilisé cette commande :

```javac $(find src -name "*.java")```
Lancer utilisé :

```  c:; cd 'c:\Users\Arthur\OneDrive\Bureau\cours\polytech\meilleur_Projet_De_Tous_Les_TEMPS_LE_Truc_De_Guedin_De_Fou\chessgame.java'; & 'C:\Program Files\Java\jdk-20\bin\java.exe' '-XX:+ShowCodeDetailsInExceptionMessages' '-cp' 'C:\Users\Arthur\AppData\Roaming\Code\User\workspaceStorage\d81aecfd14b9842ed57dd3468c5445ce\redhat.java\jdt_ws\chessgame.java_789c6e61\bin' 'com.ChessGame.Main'  ```

Pour lancer la javadoc: 

crée un dossier doc: 

```mkdir doc ```

lancer la commande: 

``` javadoc -d doc -sourcepath src -subpackages com.ChessGame ```
