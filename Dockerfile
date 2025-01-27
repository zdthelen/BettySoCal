# Use an official Python runtime as a parent image
FROM python:3.8-slim
RUN apt-get update && apt-get install -y net-tools
# Install curl
RUN apt-get update && apt-get install -y curl
# Set the working directory in the container
WORKDIR /app

# Copy the current directory contents into the container at /app
COPY . /app

# Install any needed packages specified in requirements.txt
COPY requirements.txt ./
RUN pip install --no-cache-dir -r requirements.txt

# Make port 5000 available to the world outside this container
EXPOSE 5000

# Define environment variable
ENV NAME=World


# Run app.py when the container launches
CMD ["python", "flask_app.py"]
# # Run app.py when the container launches
# CMD ["gunicorn", "--bind", "0.0.0.0:5000",  "flask_app:app"]