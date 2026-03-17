import pandas as pd
import numpy as np
import json
import tensorflow as tf
from tensorflow.keras import layers, models

FICHIER_CSV = "dataset_villette.csv"

print("1. Chargement des données...")
donnees = pd.read_csv(FICHIER_CSV)
fens = donnees['FEN'].values
coups = donnees['Coup'].values

print(f"-> {len(donnees)} positions chargées.")

print("2. Traduction des données pour l'IA...")


valeurs_pieces = {
    'P': 1, 'N': 3, 'B': 3.5, 'R': 5, 'Q': 9, 'K': 100,
    'p': -1, 'n': -3, 'b': -3.5, 'r': -5, 'q': -9, 'k': -100
}

def fen_vers_vecteur(fen):
    # On ne garde que la partie du plateau (avant le premier espace)
    plateau_str = fen.split(' ')[0]
    vecteur = []
    for char in plateau_str:
        if char.isdigit():
            # Si c'est un chiffre (ex: '4'), on ajoute autant de zéros (cases vides)
            vecteur.extend([0] * int(char))
        elif char in valeurs_pieces:
            vecteur.append(valeurs_pieces[char])
        elif char != '/':
            pass # On ignore les slashs
    return np.array(vecteur)

# On transforme tous les FENs en matrices (X)
X = np.array([fen_vers_vecteur(fen) for fen in fens])

# On crée un vocabulaire de tous vos coups uniques pour la sortie (Y)
coups_uniques = sorted(list(set(coups)))
dictionnaire_coups = {coup: i for i, coup in enumerate(coups_uniques)}
dictionnaire_inverse = {i: coup for coup, i in dictionnaire_coups.items()} # Pour plus tard

# On sauvegarde ce dictionnaire pour que VILLETTE puisse reparler en texte plus tard !
with open("vocabulaire_coups.json", "w") as f:
    json.dump(dictionnaire_inverse, f)

# On transforme tous vos coups en numéros
Y = np.array([dictionnaire_coups[coup] for coup in coups])

print(f"-> Nombre de coups uniques trouvés dans votre style : {len(coups_uniques)}")


# --- ÉTAPE 4 : CRÉATION ET ENTRAÎNEMENT DU RÉSEAU DE NEURONES ---
print("3. Création du cerveau VILLETTE...")

modele = models.Sequential([
    layers.Input(shape=(64,)),                # Entrée : Les 64 cases
    layers.Dense(512, activation='relu'),     # Couche de réflexion 1
    layers.Dropout(0.3),                      # Oubli partiel pour éviter le par-coeur
    layers.Dense(512, activation='relu'),     # Couche de réflexion 2
    layers.Dropout(0.3),
    layers.Dense(len(coups_uniques), activation='softmax') # Sortie : Probabilité du coup
])

modele.compile(optimizer='adam',
               loss='sparse_categorical_crossentropy',
               metrics=['accuracy'])

print("4. Début de l'entraînement (Deep Learning) !...")
# On fait passer toutes vos parties 15 fois (epochs) dans le réseau
modele.fit(X, Y, epochs=15, batch_size=128, validation_split=0.1)

print("5. Sauvegarde de l'IA...")
modele.save("modele_villette.keras")
print("✅ Entraînement terminé ! VILLETTE est née.")