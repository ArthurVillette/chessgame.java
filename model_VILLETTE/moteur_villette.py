import os
import sys
import json
import numpy as np
import chess

os.environ['TF_CPP_MIN_LOG_LEVEL'] = '3'
os.environ['OMP_NUM_THREADS'] = '1'
os.environ['TF_NUM_INTRAOP_THREADS'] = '1'
os.environ['TF_NUM_INTEROP_THREADS'] = '1'

import tensorflow as tf
tf.config.threading.set_intra_op_parallelism_threads(1)
tf.config.threading.set_inter_op_parallelism_threads(1)

# --- 1. CHARGEMENT ET PRÉPARATION ---
try:
    modele = tf.keras.models.load_model("modele_villette.keras")
    with open("vocabulaire_coups.json", "r") as f:
        vocabulaire = json.load(f)
        
    @tf.function(reduce_retracing=True)
    def predire_coup_rapide(x):
        return modele(x, training=False)

    plateau_vide = tf.zeros((1, 64), dtype=tf.float32)
    _ = predire_coup_rapide(plateau_vide)
except Exception as e:
    print(f"Erreur fatale au chargement : {e}", file=sys.stderr)
    sys.exit(1)

valeurs_pieces = {
    'P': 1.0, 'N': 3.0, 'B': 3.5, 'R': 5.0, 'Q': 9.0, 'K': 100.0,
    'p': -1.0, 'n': -3.0, 'b': -3.5, 'r': -5.0, 'q': -9.0, 'k': -100.0
}

def fen_vers_vecteur(fen):
    plateau_str = fen.split(' ')[0]
    vecteur = []
    for char in plateau_str:
        if char.isdigit():
            vecteur.extend([0.0] * int(char))
        elif char in valeurs_pieces:
            vecteur.append(valeurs_pieces[char])
    return np.array(vecteur, dtype=np.float32)

# --- 2. ÉVALUATION STRICTEMENT MATÉRIELLE ---
# On retire les PST. On laisse le Réseau de Neurones gérer la position !
def evaluer_materiel(board, couleur_ia):
    score = 0.0
    valeurs = {chess.PAWN: 1.0, chess.KNIGHT: 3.0, chess.BISHOP: 3.5, chess.ROOK: 5.0, chess.QUEEN: 9.0}
    for piece_type in valeurs:
        score += len(board.pieces(piece_type, couleur_ia)) * valeurs[piece_type]
        score -= len(board.pieces(piece_type, not couleur_ia)) * valeurs[piece_type]
    return score

# --- 3. MOTEUR TACTIQUE (BOUCLIER ANTI-GAFFE + QUIESCENCE) ---
def score_tactique_coup(board, move, couleur_ia):
    board.push(move)
    if board.is_checkmate():
        board.pop()
        return 10000.0

    pire_score_pour_moi = 10000.0
    
    for reponse in board.legal_moves:
        board.push(reponse)
        if board.is_checkmate():
            score = -10000.0
        else:
            # Évaluation matérielle de base
            meilleur_score_recapture = evaluer_materiel(board, couleur_ia)
            
            # Quiescence : Si l'adversaire a mangé, on regarde si on peut reprendre !
            for recapture in board.legal_moves:
                if board.is_capture(recapture):
                    board.push(recapture)
                    score_apres_recapture = evaluer_materiel(board, couleur_ia)
                    board.pop()
                    if score_apres_recapture > meilleur_score_recapture:
                        meilleur_score_recapture = score_apres_recapture
                        
            score = meilleur_score_recapture
            
        board.pop()
        
        if score < pire_score_pour_moi:
            pire_score_pour_moi = score
            
    board.pop()
    return pire_score_pour_moi if pire_score_pour_moi != 10000.0 else evaluer_materiel(board, couleur_ia)

# --- 4. BOUCLE PRINCIPALE ---
plateau_actuel = chess.Board()

while True:
    ligne = sys.stdin.readline().strip()
    if not ligne: continue
        
    if ligne == "uci":
        print("id name VILLETTE V5_STYLE_PUR")
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
        couleur_ia = plateau_actuel.turn
        
        # Le réseau de neurones propose ses idées
        vecteur_numpy = np.array([fen_vers_vecteur(plateau_actuel.fen())], dtype=np.float32)
        tenseur_X = tf.convert_to_tensor(vecteur_numpy, dtype=tf.float32)
        
        probabilites = predire_coup_rapide(tenseur_X)[0].numpy()
        indices_tries = np.argsort(probabilites)[::-1]
        
        coups_legaux_objets = list(plateau_actuel.legal_moves)
        coups_legaux_texte = {m.uci(): m for m in coups_legaux_objets}
        
        coups_candidats = []
        probas_candidats = []
        
        for index in indices_tries:
            coup_texte = vocabulaire[str(index)]
            if coup_texte in coups_legaux_texte:
                coups_candidats.append(coups_legaux_texte[coup_texte])
                probas_candidats.append(probabilites[index])
                if len(coups_candidats) >= 15:
                    break
                    
        if not coups_candidats:
            coups_candidats = coups_legaux_objets[:15]
            probas_candidats = [0.1] * len(coups_candidats)
            
        meilleur_coup = coups_candidats[0]
        meilleur_score = -99999.0
        
        for i, coup in enumerate(coups_candidats):
            # Tactique pure (Matériel + Mat)
            score_tactique = score_tactique_coup(plateau_actuel, coup, couleur_ia)
            
            # --- LE RETOUR DU STYLE ---
            # Bonus multiplicateur à 2.0.
            # L'IA jouera exactement votre style, sauf si le coup perd plus d'un pion bêtement.
            bonus_instinct = probas_candidats[i] * 2.0
            score_final = score_tactique + bonus_instinct
            
            if score_final > meilleur_score:
                meilleur_score = score_final
                meilleur_coup = coup
                
        print(f"bestmove {meilleur_coup.uci()}")
        sys.stdout.flush()
        
    elif ligne == "quit":
        break