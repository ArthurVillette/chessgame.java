import chess.pgn
import csv

FICHIER_PGN = "villette_dataset.pgn"
FICHIER_CSV = "dataset_villette.csv"
VOTRE_PSEUDO = "B0by24"

compteur_parties = 0
compteur_coups = 0

print("Début du nettoyage...")

with open(FICHIER_PGN, "r", encoding="utf-8") as pgn_file, \
     open(FICHIER_CSV, "w", newline="", encoding="utf-8") as csv_file:
    
    writer = csv.writer(csv_file)
    writer.writerow(["FEN", "Coup"])

    while True:
        partie = chess.pgn.read_game(pgn_file)
        if partie is None:
            break

        compteur_parties += 1
        
        # Détermine si vous êtes les Blancs ou les Noirs
        je_joue_blanc = (VOTRE_PSEUDO.lower() in partie.headers.get("White", "").lower())
        je_joue_noir = (VOTRE_PSEUDO.lower() in partie.headers.get("Black", "").lower())

        if not je_joue_blanc and not je_joue_noir:
            continue

        plateau = partie.board()
        
        for move in partie.mainline_moves():
            c_est_mon_tour = (plateau.turn == chess.WHITE and je_joue_blanc) or \
                             (plateau.turn == chess.BLACK and je_joue_noir)
            
            if c_est_mon_tour:
                # On sauvegarde l'état avant le coup, et le coup joué
                writer.writerow([plateau.fen(), move.uci()])
                compteur_coups += 1
            
            plateau.push(move)

        if compteur_parties % 500 == 0:
            print(f"... {compteur_parties} parties analysées ...")

print(f"\nTerminé ! {compteur_coups} coups extraits dans {FICHIER_CSV}.")