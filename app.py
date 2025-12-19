from datetime import datetime
from pathlib import Path
from flask import Flask, jsonify, request, send_from_directory
from werkzeug.utils import secure_filename
import os

from models import db, Student, Hostel, Floor, Room, Bed, Booking, Payment
import smtplib
from email.mime.text import MIMEText
from email.mime.multipart import MIMEMultipart
from email.mime.application import MIMEApplication

from reportlab.platypus import SimpleDocTemplate, Paragraph, Spacer
from reportlab.lib.styles import getSampleStyleSheet
from reportlab.lib.pagesizes import A4

def generate_pdf(booking, filename):
    student = booking.student
    bed = booking.bed
    room = bed.room
    floor = room.floor
    hostel = floor.hostel

    styles = getSampleStyleSheet()
    pdf = SimpleDocTemplate(filename, pagesize=A4)
    content = []

    title = "<b>Hostel Booking Receipt</b>"
    content.append(Paragraph(title, styles['Title']))
    content.append(Spacer(1, 12))

    details = f"""
    <b>Student Name:</b> {student.name}<br/>
    <b>Email:</b> {student.email}<br/>
    <b>Phone:</b> {student.phone}<br/><br/>

    <b>Hostel:</b> {hostel.name}<br/>
    <b>Room:</b> {room.room_number}<br/>
    <b>Bed:</b> {bed.bed_number}<br/>
    """
    content.append(Paragraph(details, styles['Normal']))

    pdf.build(content)


# --- App + DB Setup ---
BASE_DIR = Path(__file__).resolve().parent.parent
FRONTEND_DIR = BASE_DIR / "frontend"

app = Flask(
    __name__,
    static_folder=str(FRONTEND_DIR),
    static_url_path=""
)
# Use a relative path for the database for better portability
db_path = os.path.join(BASE_DIR, "backend", "database", "instance", "hostel.db")
app.config["SQLALCHEMY_DATABASE_URI"] = f"sqlite:///{db_path}"
app.config["SQLALCHEMY_TRACK_MODIFICATIONS"] = False
app.secret_key = "your_secret_key"
app.config["UPLOAD_FOLDER"] = str(BASE_DIR / "uploads")

os.makedirs(app.config["UPLOAD_FOLDER"], exist_ok=True)
db.init_app(app)

with app.app_context():
    db.create_all()

print(f"✅ Database connected successfully at: {db_path}")


def send_booking_email(to_email, booking, action="approved"):
    student = booking.student
    bed = booking.bed
    room = bed.room
    floor = room.floor
    hostel = floor.hostel

    subject = f"Hostel Booking {action.capitalize()}"

    # HTML Email Body
    if action == "approved":
        message_html = f"""
        <h2 style='color:#2E86C1;'>Hostel Booking Approved</h2>
        <p>Hello <b>{student.name}</b>,</p>
        <p>Your hostel booking has been <b style='color:green;'>approved</b>.</p>

        <h3>Booking Details</h3>
        <table border="1" cellpadding="8" style="border-collapse:collapse;">
            <tr><td><b>Name</b></td><td>{student.name}</td></tr>
            <tr><td><b>Email</b></td><td>{student.email}</td></tr>
            <tr><td><b>Phone</b></td><td>{student.phone}</td></tr>
            <tr><td><b>Hostel</b></td><td>{hostel.name}</td></tr>
            <tr><td><b>Room</b></td><td>{room.room_number}</td></tr>
            <tr><td><b>Bed</b></td><td>{bed.bed_number}</td></tr>
        </table>

        <p>Your PDF receipt is attached below.</p>
        <p>Thank you!</p>
        """
    else:
        message_html = f"""
        <h2 style='color:red;'>Hostel Booking Cancelled</h2>
        <p>Hello <b>{student.name}</b>,</p>
        <p>Your hostel booking has been <b style='color:red;'>cancelled</b>.</p>

        <h3>Booking Details</h3>
        <table border="1" cellpadding="8" style="border-collapse:collapse;">
            <tr><td><b>Name</b></td><td>{student.name}</td></tr>
            <tr><td><b>Email</b></td><td>{student.email}</td></tr>
            <tr><td><b>Phone</b></td><td>{student.phone}</td></tr>
            <tr><td><b>Hostel</b></td><td>{hostel.name}</td></tr>
            <tr><td><b>Room</b></td><td>{room.room_number}</td></tr>
            <tr><td><b>Bed</b></td><td>{bed.bed_number}</td></tr>
        </table>

        <p>If this is a mistake, please contact the admin.</p>
        """

    # Create email message
    msg = MIMEMultipart()
    msg['Subject'] = subject
    msg['From'] = "sowmya.projectdesk@gmail.com"
    msg['To'] = to_email
    msg.attach(MIMEText(message_html, "html"))

    # Create PDF receipt
    # Create PDF receipt path inside uploads folder
    pdf_filename = f"booking_{booking.id}.pdf"
    pdf_path = os.path.join(app.config["UPLOAD_FOLDER"], pdf_filename)

# Generate PDF
    generate_pdf(booking, pdf_path)

# Attach PDF
    with open(pdf_path, "rb") as f:
       pdf_attachment = MIMEApplication(f.read(), _subtype="pdf")
       pdf_attachment.add_header("Content-Disposition", "attachment", filename=pdf_filename)
       msg.attach(pdf_attachment)


    # Send email
    with smtplib.SMTP_SSL("smtp.gmail.com", 465) as smtp:
        smtp.login("sowmya.projectdesk@gmail.com", "uumquvkfqfzhrvfg")
        smtp.send_message(msg)


# --- HTML Routes ---
@app.route("/")
def page_index():
    return app.send_static_file("index.html")

@app.route("/hostels.html")
def page_hostels():
    return app.send_static_file("hostels.html")

@app.route("/booking.html")
def page_booking():
    return app.send_static_file("booking.html")

@app.route("/payment.html")
def page_payment():
    return app.send_static_file("payment.html")

@app.route("/success.html")
def page_success():
    return app.send_static_file("success.html")
@app.route("/admin.html")
def page_admin():
    return app.send_static_file("admin.html")

# --- APIs ---
@app.route("/api/hostels")
def list_hostels():
    try:
        hostels = Hostel.query.all()
        hostels_data = [
            {
                "id": h.id, "name": h.name, "fee": h.fee,
                "occupancy": h.occupancy, "facility": h.facility,
                "washroom": h.washroom, "gender": h.gender
            }
            for h in hostels
        ]
        return jsonify({"hostels": hostels_data})
    except Exception as e:
        print(f"Error in /api/hostels: {e}")
        return jsonify({"error": "Could not fetch hostels"}), 500

@app.route("/api/available_beds", methods=["GET"])
def available_beds():
    hostel_name = request.args.get("hostel")
    floor_raw = request.args.get("floor")
    room_raw = request.args.get("room")

    if not all([hostel_name, floor_raw, room_raw]):
        return jsonify({"success": False, "error": "Hostel, floor, and room are required"}), 400

    try:
        # Helpful debug logging (prints to your server console)
        print(f"[available_beds] Received params -> hostel: {hostel_name!r}, floor: {floor_raw!r}, room: {room_raw!r}")

        # Try to parse numeric versions if possible
        floor_int = None
        room_int = None
        try:
            floor_int = int(floor_raw)
        except Exception:
            pass
        try:
            room_int = int(room_raw)
        except Exception:
            pass

        # 1) Try strict match first (fast path)
        room = Room.query.join(Floor).join(Hostel).filter(
            Hostel.name == hostel_name,
            Floor.floor_no == floor_raw,
            Room.room_number == room_raw
        ).first()

        # 2) If not found, try numeric floor/room match (if DB stores ints)
        if not room and (floor_int is not None or room_int is not None):
            q = Room.query.join(Floor).join(Hostel).filter(Hostel.name == hostel_name)
            if floor_int is not None:
                q = q.filter(Floor.floor_no == floor_int)
            else:
                q = q.filter(Floor.floor_no == floor_raw)
            if room_int is not None:
                q = q.filter(Room.room_number == room_int)
            else:
                q = q.filter(Room.room_number == room_raw)
            room = q.first()

        # 3) If still not found, try common formatted variants (e.g. "Floor 1", "Room 101")
        if not room:
            floor_candidates = {floor_raw}
            room_candidates = {room_raw}
            # add prefixed candidates
            floor_candidates.add(f"Floor {floor_raw}")
            room_candidates.add(f"Room {room_raw}")
            # if numeric parsed, add plain numeric strings
            if floor_int is not None:
                floor_candidates.add(str(floor_int))
                floor_candidates.add(f"Floor {floor_int}")
            if room_int is not None:
                room_candidates.add(str(room_int))
                room_candidates.add(f"Room {room_int}")

            # try all combinations of candidates using OR by iterating
            for f_cand in floor_candidates:
                for r_cand in room_candidates:
                    room = Room.query.join(Floor).join(Hostel).filter(
                        Hostel.name == hostel_name,
                        Floor.floor_no == f_cand,
                        Room.room_number == r_cand
                    ).first()
                    if room:
                        break
                if room:
                    break

        # 4) Last attempt: try partial match on room_number (LIKE) with numeric substring
        if not room:
            try:
                # use raw substring (e.g. '101' will match 'B101', '101-A', etc.)
                substring = str(room_int) if room_int is not None else room_raw
                room = Room.query.join(Floor).join(Hostel).filter(
                    Hostel.name == hostel_name,
                    Room.room_number.like(f"%{substring}%")
                ).first()
            except Exception:
                room = None

        if not room:
            # no room found — return empty beds array (frontend will show "No available beds")
            print(f"[available_beds] No matching room found for hostel={hostel_name!r}, floor={floor_raw!r}, room={room_raw!r}")
            return jsonify({"success": True, "beds": []})

        # Found a room — return available beds
        beds = Bed.query.filter_by(room_id=room.id, is_booked=False).order_by(Bed.bed_number).all()
        beds_data = [{"id": bed.id, "bed_number": bed.bed_number} for bed in beds]
        print(f"[available_beds] Found room id={room.id}. Returning {len(beds_data)} beds.")
        return jsonify({"success": True, "beds": beds_data})
    except Exception as e:
        print(f"Error in /api/available_beds: {e}")
        return jsonify({"success": False, "error": "Could not fetch available beds"}), 500

@app.route("/api/book", methods=["POST"])
def book_room():
    data = request.get_json() or {}
    print(f"Received booking data: {data}") # Debugging print
    
    bed_id = data.get("bed_id") 
    student_name = data.get("student_name")
    student_email = data.get("student_email")
    
    if not (student_name and student_email and bed_id):
        return jsonify({"success": False, "error": "Missing required fields"}), 400

    try:
        # --- NEW LOGIC STARTS HERE ---
        # First, check if a student with this email has an existing booking
        student = Student.query.filter_by(email=student_email).first()
        if student:
            existing_booking = Booking.query.filter_by(student_id=student.id).first()
            if existing_booking:
                return jsonify({"success": False, "error": "This email address has already been used to book a room."}), 400
        # --- NEW LOGIC ENDS HERE ---

        # Check bed availability
        bed = Bed.query.get(bed_id)
        if not bed:
            return jsonify({"success": False, "error": "Invalid bed selected"}), 400
        if bed.is_booked:
            return jsonify({"success": False, "error": "Sorry, this bed was just booked."}), 400

        # If student is new, create a new record
        if not student:
            student = Student(
                name=student_name, email=student_email,
                phone=data.get("student_phone"), gender=data.get("student_gender"),
                password="hashed_placeholder"
            )
            db.session.add(student)
            db.session.flush()

        # Proceed with booking creation
        bed.is_booked = True
        new_booking = Booking(student_id=student.id, bed_id=bed.id, amount=data.get("amount"),status="pending")
        db.session.add(new_booking)
        db.session.flush()

        new_payment = Payment(booking_id=new_booking.id, amount=data.get("amount"), status="pending")
        db.session.add(new_payment)
        
        db.session.commit()

        return jsonify({
            "success": True, "student_id": student.id,
            "booking_id": new_booking.id, "payment_id": new_payment.id
        })
    except Exception as e:
        db.session.rollback()
        print(f"Error in /api/book: {e}")
        return jsonify({"success": False, "error": "Database error during booking."}), 500

@app.route("/api/payment/proof", methods=["POST"])
def upload_payment_proof():
    
    payment_id = request.form.get("payment_id")
    file = request.files.get("file")

    if not (payment_id and file):
        return jsonify({"success": False, "error": "payment_id and file are required"}), 400
    
    try:
        payment = Payment.query.get(payment_id)
        if not payment:
            return jsonify({"success": False, "error": "Invalid payment_id"}), 404

        filename = f"payment_{payment_id}_{secure_filename(file.filename)}"
        save_path = os.path.join(app.config["UPLOAD_FOLDER"], filename)
        file.save(save_path)

        payment.payment_file = filename
        payment.status = "Uploaded"
        db.session.commit()
        return jsonify({"success": True, "payment_id": payment.id, "payment_file": filename})
    except Exception as e:
        db.session.rollback()
        print(f"Error in /api/payment/proof: {e}")
        return jsonify({"success": False, "error": "Database error during payment."}), 500

@app.route('/approve_booking/<int:booking_id>', methods=['POST'])
def approve_booking(booking_id):
    print("Approving booking:", booking_id)

    booking = Booking.query.get(booking_id)
    print("Booking loaded:", booking)

    booking.status = "approved"
    db.session.commit()

    send_booking_email(booking.student.email, booking, action="approved")

    return jsonify({"success": True})


@app.route('/cancel_booking/<int:booking_id>', methods=['POST'])
def cancel_booking(booking_id):
    booking = Booking.query.get(booking_id)
    booking.status = "cancelled"
    db.session.commit()

    send_booking_email(booking.student.email, booking, action="cancelled")

    return jsonify({"success": True})

@app.route("/pending_bookings")
def pending_bookings():
    bookings = Booking.query.filter_by(status="pending").all()

    response = []
    for b in bookings:
        payment = Payment.query.filter_by(booking_id=b.id).first()

        response.append({
            "id": b.id,
            "student": b.student.name,
            "email": b.student.email,
            "hostel": b.bed.room.floor.hostel.name,
            "room": b.bed.room.room_number,
            "bed": b.bed.bed_number,
            "status": b.status,
            "payment_file": f"/uploads/{payment.payment_file}" if payment and payment.payment_file else None
        })

    return jsonify(response)

@app.route("/uploads/<path:filename>")
def serve_uploads(filename):
    return send_from_directory(app.config["UPLOAD_FOLDER"], filename)

# NEW, CORRECTED LINE

if __name__ == "__main__":
    app.run(host="0.0.0.0", port=5000, debug=True, use_reloader=False)