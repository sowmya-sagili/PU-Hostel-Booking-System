# from app import db, app

# with app.app_context():
#     db.create_all()
#     print("✅ Database tables created successfully!")

from flask import Flask
from models import db

# This script is for one-time setup.
# It creates the database file and all the tables based on your models.

# --- Configuration ---
# MUST be the same URI as in your app.py
DATABASE_URI = "sqlite:///C:/Users/sagil/Desktop/parul/backend/database/instance/hostel.db"

# --- Script ---
app = Flask(__name__)
app.config["SQLALCHEMY_DATABASE_URI"] = DATABASE_URI
app.config["SQLALCHEMY_TRACK_MODIFICATIONS"] = False

db.init_app(app)

with app.app_context():
    # This creates all the tables defined in models.py
    db.create_all()

print(f"✅ Database tables created successfully for: {DATABASE_URI}")
