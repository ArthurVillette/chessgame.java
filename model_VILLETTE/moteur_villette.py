import os
import sys
import json
import numpy as np
import chess

# Désactive les messages d'avertissement de TensorFlow pour ne pas polluer Java
os.environ['TF_CPP_MIN_LOG_LEVEL'] = '3'
import tensorflow as tf

# --- 1. CHARGEMENT ET ÉCHAUFFEMENT ---
try:
    modele = tf.keras.models.load_model("modele_villette.keras")
    with open("vocabulaire_coups.json", "r") as f:
        vocabulaire = json.load(f)
        
    # Remplacez l'ancienne ligne par celle-ci (ajout du dtype) :
    plateau_vide = np.zeros((1, 64), dtype=np.int32)
    _ = modele(plateau_vide, training=False)
except Exception as e:
    print(f"Erreur fatale au chargement : {e}")
    sys.exit(1)

# Dictionnaire des pièces
valeurs_pieces = {
    'P': 1, 'N': 3, 'B': 3, 'R': 5, 'Q': 9, 'K': 100,
    'p': -1, 'n': -3, 'b': -3, 'r': -5, 'q': -9, 'k': -100
}

def fen_vers_vecteur(fen):
    plateau_str = fen.split(' ')[0]
    vecteur = []
    for char in plateau_str:
        if char.isdigit():
            vecteur.extend([0] * int(char))
        elif char in valeurs_pieces:
            vecteur.append(valeurs_pieces[char])
    return np.array(vecteur, dtype=np.int32)

# --- 2. BOUCLE DE COMMUNICATION ---
plateau_actuel = chess.Board()

while True:
    ligne = sys.stdin.readline().strip()
    
    if not ligne:
        continue
        
    if ligne == "uci":
        print("id name VILLETTE")
        print("id author Arthur")
        print("uciok")
        sys.stdout.flush()
        
    elif ligne == "isready":
        print("readyok")
        sys.stdout.flush()
        
    elif ligne.startswith("position fen"):
        fen = ligne.replace("position fen ", "").strip()
        plateau_actuel = chess.Board(fen)
        
    elif ligne.startswith("go"):
        # 1. Traduction du plateau
        vecteur_X = np.array([fen_vers_vecteur(plateau_actuel.fen())])
        
        # 2. Prédiction instantanée (PAS de .predict() ici)
        probabilites = modele(vecteur_X, training=False).numpy()[0]
        
        # 3. Tri des probabilités du plus grand au plus petit
        indices_tries = np.argsort(probabilites)[::-1]
        
        # 4. OPTIMISATION MAJEURE : On génère la liste des coups légaux UNE SEULE FOIS en texte
        coups_legaux_texte = {move.uci() for move in plateau_actuel.legal_moves}
        
        coup_choisi = None
        
        # 5. On cherche le coup préféré de l'IA qui est dans la liste des coups légaux
        for index in indices_tries:
            coup_texte = vocabulaire[str(index)]
            
            if coup_texte in coups_legaux_texte:
                coup_choisi = coup_texte
                break
                
        # Sécurité : Si l'IA est complètement perdue, on joue le premier coup légal possible
        if coup_choisi is None:
            coup_choisi = list(coups_legaux_texte)[0]
            
        print(f"bestmove {coup_choisi}")
        sys.stdout.flush()
        
    elif ligne == "quit":
        break