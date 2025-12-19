import requests

url = "http://127.0.0.1:5000/api/book"

data = {
    "student_name": "Sowmya Sagili",
    "student_email": "sowmya@example.com",
    "student_phone": "9876543210",
    "student_gender": "Female",
    "bed_id": 5,   # ⚠️ make sure bed with ID=1 exists in DB (via seed_hostels.py)
    "amount": 95000
}

response = requests.post(url, json=data)
print("Booking Response:", response.json())
