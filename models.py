from flask_sqlalchemy import SQLAlchemy
from datetime import datetime

db = SQLAlchemy()


# --- Student / User Model ---
class Student(db.Model):  
    id = db.Column(db.Integer, primary_key=True)
    name = db.Column(db.String(100), nullable=False)
    email = db.Column(db.String(100), unique=True, nullable=False)  # login
    password = db.Column(db.String(200), nullable=False)  # hashed password
    phone = db.Column(db.String(20))
    gender = db.Column(db.String(10))
    created_at = db.Column(db.DateTime, default=datetime.utcnow)

# --- Hostel ---
class Hostel(db.Model):
    id = db.Column(db.Integer, primary_key=True)
    name = db.Column(db.String(100))
    occupancy = db.Column(db.Integer)
    fee = db.Column(db.Integer)
    gender = db.Column(db.String(10))
    facility = db.Column(db.String(100))      # make sure exists
    washroom = db.Column(db.String(100))      # make sure exists


# --- Floor ---
class Floor(db.Model):
    hostel = db.relationship("Hostel")
    id = db.Column(db.Integer, primary_key=True)
    hostel_id = db.Column(db.Integer, db.ForeignKey('hostel.id'), nullable=False)
    floor_no = db.Column(db.Integer, nullable=False)

# --- Room ---
class Room(db.Model):
    floor = db.relationship("Floor")
    id = db.Column(db.Integer, primary_key=True)
    floor_id = db.Column(db.Integer, db.ForeignKey('floor.id'), nullable=False)
    room_number = db.Column(db.String(10), nullable=False)
    capacity = db.Column(db.Integer, nullable=False)

# --- Bed ---
class Bed(db.Model):
    room = db.relationship("Room")
    id = db.Column(db.Integer, primary_key=True)
    room_id = db.Column(db.Integer, db.ForeignKey('room.id'), nullable=False)
    bed_number = db.Column(db.String(10), nullable=False)
    is_booked = db.Column(db.Boolean, default=False)  # True = locked

# --- Booking ---
class Booking(db.Model):
    id = db.Column(db.Integer, primary_key=True)
    student_id = db.Column(db.Integer, db.ForeignKey('student.id'), nullable=False)
    bed_id = db.Column(db.Integer, db.ForeignKey('bed.id'), unique=True, nullable=False)  # lock per bed
    booked_on = db.Column(db.DateTime, default=datetime.utcnow)
    amount = db.Column(db.Integer, nullable=False)
    status = db.Column(db.String(20), default="pending")  # Pending / Confirmed / Cancelled
    bed = db.relationship("Bed")
    student = db.relationship("Student")
    @property
    def room(self):
        return self.bed.room

    @property
    def hostel(self):
        return self.bed.room.floor.hostel


# --- Payment ---
class Payment(db.Model):
    id = db.Column(db.Integer, primary_key=True)
    booking_id = db.Column(db.Integer, db.ForeignKey('booking.id'), nullable=False)
    amount = db.Column(db.Integer, nullable=False)
    payment_file = db.Column(db.String(200))  # file path or filename
    status = db.Column(db.String(20), default="Pending")  # Pending / Verified / Rejected
    verified_by = db.Column(db.String(100))
    verified_on = db.Column(db.DateTime)
