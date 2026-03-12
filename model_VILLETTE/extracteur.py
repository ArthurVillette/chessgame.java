import requests
import time

# --- CONFIGURATION ---
PSEUDO_CHESSCOM = "B0by24" 
ANNEES = ["2024", "2025", "2026"] 
MOIS = [str(i).zfill(2) for i in range(1, 13)]
FICHIER_SORTIE = "villette_dataset.pgn"

print(f"Début du téléchargement des parties de {PSEUDO_CHESSCOM}...")

with open(FICHIER_SORTIE, "w", encoding="utf-8") as fichier:
    for annee in ANNEES:
        for mois in MOIS:
            url = f"https://api.chess.com/pub/player/{PSEUDO_CHESSCOM}/games/{annee}/{mois}/pgn"
            
            headers = {"User-Agent": "Projet_IA_Villette"}
            
            reponse = requests.get(url, headers=headers)
            
            if reponse.status_code == 200 and reponse.text.strip():
                fichier.write(reponse.text)
                fichier.write("\n\n")
                print(f"✅ {annee}/{mois} : Téléchargé avec succès.")
            else:
                print(f"❌ {annee}/{mois} : Pas de parties trouvées.")
            
            time.sleep(1) 

print(f"\nTerminé ! Toutes les parties sont fusionnées dans {FICHIER_SORTIE}.")