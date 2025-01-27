#run_sports_model.py

import subprocess
import os
import time

def run_script(script_name):
    try:
        subprocess.run(["python", script_name], check=True)
        print(f"Successfully ran {script_name}")
    except subprocess.CalledProcessError as e:
        print(f"Error running {script_name}: {e}")

def wait_for_server(host="localhost", port=5000, timeout=30):
    import socket
    start_time = time.time()
    while True:
        try:
            with socket.create_connection((host, port), timeout=1):
                print(f"Flask server is now up at {host}:{port}")
                return True
        except (socket.timeout, ConnectionRefusedError):
            if time.time() - start_time > timeout:
                print(f"Timeout: Could not connect to Flask server after {timeout} seconds")
                return False
            time.sleep(1)

if __name__ == "__main__":
    # Start Flask server first
    print("Starting Flask server...")
    flask_process = subprocess.Popen(["python", "flask_app.py"])

    # Wait for the server to be up
    if wait_for_server():
        # Run script to fetch and store matchups
        run_script("fetch_daily_matchups.py")

        # Run script to process data from pickle file
        run_script("Sports_Betting_Model.py")
        
        # Stop the Flask server since we're done processing
        # print("Stopping Flask server...")
        # flask_process.terminate()
    else:
        print("Failed to start Flask server. Aborting other scripts.")
        flask_process.terminate()  # If we couldn't connect, terminate the Flask server process

    # print("Container operations completed.")


# import subprocess
# import os
# import time

# def run_script(script_name):
#     try:
#         subprocess.run(["python", script_name], check=True)
#         print(f"Successfully ran {script_name}")
#     except subprocess.CalledProcessError as e:
#         print(f"Error running {script_name}: {e}")

# def wait_for_server(host="localhost", port=5000, timeout=30):
#     import socket
#     start_time = time.time()
#     while True:
#         try:
#             with socket.create_connection((host, port), timeout=1):
#                 print(f"Flask server is now up at {host}:{port}")
#                 return True
#         except (socket.timeout, ConnectionRefusedError):
#             if time.time() - start_time > timeout:
#                 print(f"Timeout: Could not connect to Flask server after {timeout} seconds")
#                 return False
#             time.sleep(1)

# if __name__ == "__main__":
#     # Start Flask server first
#     print("Starting Flask server...")
#     # Capture stdout and stderr to print later
#     flask_process = subprocess.Popen(["python", "flask_app.py"], stdout=subprocess.PIPE, stderr=subprocess.PIPE, text=True, bufsize=1)

#     # Wait for the server to be up
#     if wait_for_server():
#         # Run script to fetch and store matchups
#         run_script("fetch_daily_matchups.py")

#         # Run script to process data from pickle file
#         run_script("Sports_Betting_Model.py")
        
#         # Keep the script running to keep the server alive
#         print("Server is running. Press Ctrl+C to stop...")
#         try:
#             # Print lines from stdout and stderr as they come
#             for line in flask_process.stdout:
#                 print("Flask stdout:", line.strip())
#             for line in flask_process.stderr:
#                 print("Flask stderr:", line.strip())
#             flask_process.wait()  # Wait for the Flask process to finish
#         except KeyboardInterrupt:
#             print("Interrupted by user. Stopping Flask server...")
#             flask_process.terminate()
#     else:
#         print("Failed to start Flask server. Aborting other scripts.")
#         flask_process.terminate()  # If we couldn't connect, terminate the Flask server process

#     # Here, the Flask server will keep running until manually stopped or the script is closed.

