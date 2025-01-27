# Sports Betting Model

# Sports Betting Model
import pandas as pd
from scipy.stats import pearsonr
import json
import requests
import pickle

# Load datasets globally (to avoid reloading each time)
cbb_basic = pd.read_csv('cbb_basic.csv')
cbb_opp = pd.read_csv('cbb_opp.csv')

# Calculate per-game stats and turnover ratio for cbb_basic
cbb_basic['FGAPG'] = cbb_basic['FGA'] / cbb_basic['G']
cbb_basic['FGMPG'] = cbb_basic['FGAPG'] * cbb_basic['FG%']
cbb_basic['3PAPG'] = cbb_basic['3PA'] / cbb_basic['G']
cbb_basic['3PMPG'] = cbb_basic['3PAPG'] * cbb_basic['3P%']
cbb_basic['FTAPG'] = cbb_basic['FTA'] / cbb_basic['G']
cbb_basic['FTMPG'] = cbb_basic['FTAPG'] * cbb_basic['FT%']

# Create team_basic_stats dataframe
team_basic_stats = cbb_basic[['School', 'SOS', 'FGMPG', '3PMPG', 'FTMPG']]

# Calculate per-game stats and turnover ratio for cbb_opp
cbb_opp['oFGAPG'] = cbb_opp['FGA'] / cbb_opp['G']
cbb_opp['oFGMPG'] = cbb_opp['oFGAPG'] * cbb_opp['FG%']
cbb_opp['o3PAPG'] = cbb_opp['3PA'] / cbb_opp['G']
cbb_opp['o3PMPG'] = cbb_opp['o3PAPG'] * cbb_opp['3P%']
cbb_opp['oFTAPG'] = cbb_opp['FTA'] / cbb_opp['G']
cbb_opp['oFTMPG'] = cbb_opp['oFTAPG'] * cbb_opp['FT%']
cbb_opp['oFG%'] = cbb_opp['FG%']
cbb_opp['o3P%'] = cbb_opp['3P%']
cbb_opp['oFT%'] = cbb_opp['FT%']
cbb_opp['oSOS'] = cbb_opp['SOS']

# Create opponent_basic_stats dataframe
opponent_basic_stats = cbb_opp[['School', 'oSOS', 'oFGMPG', 'o3PMPG', 'oFTMPG']]

# Function to calculate combined stats for two teams
def calculate_team_matchup_stats(team1, team2):
    try:
        team1_basic = team_basic_stats[team_basic_stats['School'] == team1].iloc[0]
        team2_basic = team_basic_stats[team_basic_stats['School'] == team2].iloc[0]

        team1_opp = opponent_basic_stats[opponent_basic_stats['School'] == team1].iloc[0]
        team2_opp = opponent_basic_stats[opponent_basic_stats['School'] == team2].iloc[0]

        # Calculate adjusted stats
        team_a_score = ((team1_basic['SOS'] + team2_opp['oSOS']) / 2) + \
                       (team1_basic['FGMPG'] + team2_opp['oFGMPG']) + \
                       (((team1_basic['3PMPG'] + team2_opp['o3PMPG']) / 2) * 3) + \
                       ((team1_basic['FTMPG'] + team2_opp['oFTMPG']) / 2)
        team_b_score = ((team2_basic['SOS'] + team1_opp['oSOS']) / 2) + \
                       (team2_basic['FGMPG'] + team1_opp['oFGMPG']) + \
                       (((team2_basic['3PMPG'] + team1_opp['o3PMPG']) / 2) * 3) + \
                       ((team2_basic['FTMPG'] + team1_opp['oFTMPG']) / 2)

        return team_a_score, team_b_score

    except IndexError:
        return None, None

# Function to fetch matchups from the pickle file
def fetch_matchups_from_pickle():
    try:
        with open('matchups_data.pkl', 'rb') as file:
            return pickle.load(file)
    except (FileNotFoundError, EOFError) as e:
        print(f"Error loading matchups data: {e}")
        return []

# Function to process fetched matchups
def process_matchups(matchups):
    results = []
    for matchup in matchups:
        home_team = matchup['home_team']
        away_team = matchup['away_team']
        spreads = matchup.get('draftkings_spreads', [])

        if not spreads:
            continue

        team_a_score, team_b_score = calculate_team_matchup_stats(home_team, away_team)

        if team_a_score is None or team_b_score is None:
            continue

        results.append({
            "home_team": home_team,
            "away_team": away_team,
            "home_score_projection": team_a_score,
            "away_score_projection": team_b_score,
            "spreads": spreads
        })
    return results

# Main logic
if __name__ == "__main__":
    matchups = fetch_matchups_from_pickle()

    if not matchups:
        print("No matchups retrieved. Exiting program.")
        exit()

    processed_results = process_matchups(matchups)

    if not processed_results:
        print("No matchups processed. Exiting program.")
        exit()

    # Output results as JSON
    output_json = json.dumps(processed_results, indent=4)
    print(output_json)

    # Optionally post the processed data to another endpoint
    try:
        response = requests.post('http://localhost:5000/store_projections', json=processed_results)
        print(json.dumps(processed_results, indent=4))
        if response.status_code == 200:
            print("Data stored successfully to /store_projections")
        else:
            print(f"Failed to store data to /store_projections: {response.text}")
    except requests.exceptions.RequestException as e:
        print(f"Failed to connect to server: {str(e)}")

