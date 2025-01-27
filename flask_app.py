#flask_app.py


# import logging
# from logging.handlers import FileHandler
import json
from flask import Flask, request, jsonify



# Configure logging to write to a file
# handler = FileHandler('flask.log')
# handler.setLevel(logging.INFO)
app = Flask(__name__)
# app.logger.addHandler(handler)
# app.logger.setLevel(logging.INFO)

# app = Flask(__name__)
# Store for matchups and projections
stored_matchups = []  # Stores matchups posted to the server
stored_projections = []  # Stores projections calculated by the model

@app.route('/store_matchups', methods=['POST'])
def store_matchups():
    app.logger.info("Received POST for storing matchups")
    """
    Endpoint to receive and store matchups posted by fetch_daily_matchups.py.
    """
    global stored_matchups
    try:
        matchups = request.get_json()
        if matchups:
            stored_matchups = matchups  # Overwrite stored matchups
            return jsonify({"status": "success", "message": "Matchups stored"}), 200
        else:
            return jsonify({"error": "No data provided"}), 400
    except Exception as e:
        return jsonify({"error": str(e)}), 500

@app.route('/matchups', methods=['GET'])
def get_matchups():
    """
    Endpoint to retrieve stored matchups for Sports_Betting_Model.py.
    """
    if stored_matchups:
        return jsonify(stored_matchups), 200
    else:
        return jsonify({"error": "No processed data found"}), 404

@app.route('/store_projections', methods=['POST'])
def store_projections():
    app.logger.info("Received POST for storing projections")
    try:
        projections = request.get_json()
        if projections:
            app.logger.info(f"Storing projections: {json.dumps(projections, indent=4)}")
            global stored_projections
            stored_projections = projections
            return jsonify({"status": "success", "message": "Projections stored"}), 200
        else:
            return jsonify({"error": "No data provided"}), 400
    except Exception as e:
        app.logger.error(f"Error storing projections: {str(e)}")
        return jsonify({"error": str(e)}), 500

@app.route('/projections', methods=['GET'])
def get_projections():
    app.logger.info(f"GET request for projections: {json.dumps(stored_projections, indent=4)}")
    if stored_projections:
        return jsonify(stored_projections), 200
    else:
        return jsonify({"error": "No projections found"}), 404
    
if __name__ == '__main__':
    app.run(host='0.0.0.0', port=5000, debug=False)

