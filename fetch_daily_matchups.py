#fetch_daily_matchups.py

import requests
import pandas as pd
import pickle
from datetime import date

# Your Odds API key
API_KEY = "b56a66864ec19efcb9e5012321195af7"  # Replace with your actual API key
ODDS_API_BASE_URL = "https://api.the-odds-api.com/v4/sports/basketball_ncaab/odds"

# Load the school name mapping
name_map_df = pd.read_csv("school_name_map.csv", encoding="Windows-1252")
name_mapping = dict(zip(name_map_df["Matched_Team"], name_map_df["School"]))

# Function to map Odds API team names to dataset names
def map_team_name(odds_name):
    mapped_name = name_mapping.get(odds_name)
    if not mapped_name:
        print(f"Warning: Team name not found in mapping: {odds_name}")
    return mapped_name

# Function to fetch and process matchups
def fetch_and_process_matchups():
    try:
        today = date.today().isoformat()

        # Query the Odds API
        params = {
            'apiKey': API_KEY,
            'regions': 'us',        # US sportsbooks
            'markets': 'spreads',   # Fetch spreads
            'date': today           # Filter by today's date
        }
        response = requests.get(ODDS_API_BASE_URL, params=params)

        if response.status_code != 200:
            print(f"Error fetching matchups: {response.status_code} - {response.text}")
            return []

        # Parse the API response
        data = response.json()
        matchups = []

        # Process games and apply mapping
        for game in data:
            home_team = map_team_name(game.get('home_team'))
            away_team = map_team_name(game.get('away_team'))

            if not (home_team and away_team):
                # Skip matchups where either team name could not be mapped
                continue

            # Extract DraftKings spreads data
            bookmakers = game.get('bookmakers', [])
            draftkings_data = next(
                (bookmaker for bookmaker in bookmakers if bookmaker.get('title') == "DraftKings"),
                None
            )

            if draftkings_data:
                markets = draftkings_data.get('markets', [])
                spreads_market = next(
                    (market for market in markets if market.get('key') == "spreads"),
                    None
                )

                if spreads_market:
                    # Extract spread outcomes and clean up fields
                    outcomes = [
                        {"team": map_team_name(outcome.get("name")), "spread": outcome.get("point")}
                        for outcome in spreads_market.get('outcomes', [])
                    ]
                    # Filter out invalid teams from outcomes
                    outcomes = [outcome for outcome in outcomes if outcome["team"]]

                    matchups.append({
                        "home_team": home_team,
                        "away_team": away_team,
                        "commence_time": game.get("commence_time"),
                        "draftkings_spreads": outcomes
                    })

        return matchups

    except Exception as e:
        print(f"An error occurred while fetching matchups: {e}")
        return []

# Main function to fetch matchups and post them to Flask
def main():
    matchups = fetch_and_process_matchups()
    if not matchups:
        print("No valid matchups found.")
        return

    # Save matchups locally for debugging or backup
    with open('matchups_data.pkl', 'wb') as file:
        pickle.dump(matchups, file)

    # Post matchups to Flask server
    try:
        print(f"Posting {len(matchups)} matchups to Flask server...")
        response = requests.post('http://localhost:5000/store_matchups', json=matchups)
        if response.status_code == 200:
            print("Matchups successfully stored on Flask server.")
        else:
            print(f"Failed to store matchups: {response.status_code}, {response.text}")
    except requests.exceptions.RequestException as e:
        print(f"Failed to connect to Flask server: {e}")

if __name__ == '__main__':
    main()

