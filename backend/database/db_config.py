# backend/database/db_config.py
import sys, os
from flask import Flask
from flask_sqlalchemy import SQLAlchemy

# Ensure backend/ is added to Python path (so models.py can be imported)
BASE_DIR = os.path.dirname(os.path.dirname(__file__))   # points to backend/
sys.path.append(BASE_DIR)

from models import db, Student, Hostel, Floor, Room, Bed, Booking, Payment

def create_app():
    app = Flask(__name__)

    # SQLite Database (stored in backend/database/instance/hostel.db)
    db_path = os.path.join(BASE_DIR, "database", "instance", "hostel.db")
    app.config["SQLALCHEMY_DATABASE_URI"] = f"sqlite:///{db_path}"
    app.config['SQLALCHEMY_TRACK_MODIFICATIONS'] = False
    app.secret_key = "your_secret_key"

    db.init_app(app)

    # Create tables
    with app.app_context():
        db.create_all()
        print(f"✅ Database created successfully at {db_path}")

    return app

if __name__ == "__main__":
    app = create_app()
    print("✅ App created and database initialized!")
