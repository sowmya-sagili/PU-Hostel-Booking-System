
from app import app, db, Hostel, Floor, Room, Bed

# REPLACE your seed_hostels() with this version
def seed_hostels():
    with app.app_context():
        # Clear old data (keep order due to FKs)
        Bed.query.delete()
        Room.query.delete()
        Floor.query.delete()
        Hostel.query.delete()
        db.session.commit()

        # Men’s hostels
        men_hostels = [
            {"name": "Shastri Bhawan - A", "occupancy": 3, "facility": "Non AC", "washroom": "Common Wash Room", "fee": 95000},
            {"name": "Shastri Bhawan - B", "occupancy": 4, "facility": "Non AC", "washroom": "Common Wash Room", "fee": 90500},
            {"name": "Shastri Bhawan - C", "occupancy": 3, "facility": "Non AC", "washroom": "Common Wash Room", "fee": 95500},
            {"name": "Kalam Bhawan A", "occupancy": 9, "facility": "Non AC", "washroom": "2 Attached Wash Room", "fee": 97500},
            {"name": "Kalam Bhawan B", "occupancy": 9, "facility": "Non AC", "washroom": "2 Attached Wash Room", "fee": 97500},
            {"name": "Kalam Bhawan C", "occupancy": 9, "facility": "Non AC", "washroom": "2 Attached Wash Room", "fee": 97500},
            {"name": "Tagore Bhawan A", "occupancy": 3, "facility": "Non AC", "washroom": "Attached Wash Room", "fee": 123500},
            {"name": "Tagore Bhawan B", "occupancy": 3, "facility": "Non AC", "washroom": "Attached Wash Room", "fee": 123500},
            {"name": "Tagore Bhawan C", "occupancy": 3, "facility": "Non AC", "washroom": "Attached Wash Room", "fee": 123500},
            {"name": "Dhyan Bhawan", "occupancy": 5, "facility": "AC", "washroom": "Common Wash Room", "fee": 128000},
            {"name": "Sardar Bhawan - A", "occupancy": 4, "facility": "Non AC", "washroom": "Attached Wash Room", "fee": 104500},
            {"name": "Sardar Bhawan - B", "occupancy": 3, "facility": "Non AC", "washroom": "Common Wash Room", "fee": 104500},
            {"name": "Sardar Bhawan - C", "occupancy": 3, "facility": "Non AC", "washroom": "Attached Wash Room", "fee": 112500},
            {"name": "Milkha Bhawan - A", "occupancy": 3, "facility": "Non AC", "washroom": "Attached Wash Room", "fee": 129500},
            {"name": "Atal Bhawan-A1", "occupancy": 4, "facility": "Non AC", "washroom": "Attached Wash Room", "fee": 115500},
            {"name": "Atal Bhawan - B", "occupancy": 8, "facility": "Non AC", "washroom": "Common Wash Room", "fee": 84500},
            {"name": "Albert Einstein", "occupancy": 2, "facility": "AC", "washroom": "Attached Wash Room", "fee": 2000},
            {"name": "Azad Bhavan - A", "occupancy": 8, "facility": "Non AC", "washroom": "Common Wash Room", "fee": 90500},
            {"name": "Atal Bhavan A2 Boys", "occupancy": 4, "facility": "Non AC", "washroom": "Attached Wash Room", "fee": 115500},
            {"name": "Tilak Bhawan-A", "occupancy": 3, "facility": "Non AC", "washroom": "Attached Wash Room", "fee": 129500},
            {"name": "Abraham Lincoln", "occupancy": 5, "facility": "AC", "washroom": "Common Wash Room", "fee": 1500},
            {"name": "Saarthi Hostel", "occupancy": 4, "facility": "Non AC", "washroom": "Attached Wash Room", "fee": 100000}
        ]

        # Women’s hostels
        women_hostels = [
            {"name": "Sarojini Bhawan - A", "occupancy": 4, "facility": "Non AC", "washroom": "Common Wash Room", "fee": 85500},
            {"name": "Sarojini Bhawan - B", "occupancy": 5, "facility": "Non AC", "washroom": "Common Wash Room", "fee": 84500},
            {"name": "Sarojini Bhawan - C", "occupancy": 8, "facility": "Non AC", "washroom": "Common Wash Room", "fee": 84500},
            {"name": "Indira Bhawan - A", "occupancy": 9, "facility": "Non AC", "washroom": "Attached Wash Room", "fee": 93500},
            {"name": "Indira Bhawan - B", "occupancy": 10, "facility": "Non AC", "washroom": "Attached Wash Room", "fee": 93500},
            {"name": "Indira Bhawan - C", "occupancy": 9, "facility": "Non AC", "washroom": "Attached Wash Room", "fee": 93500},
            {"name": "Teresa Bhawan - AIBIC A", "occupancy": 3, "facility": "Non AC", "washroom": "Attached Wash Room", "fee": 117500},
            {"name": "Teresa Bhawan - AIBIC B", "occupancy": 3, "facility": "Non AC", "washroom": "Attached Wash Room", "fee": 117500},
            {"name": "Teresa Bhawan - AIBIC C", "occupancy": 3, "facility": "Non AC", "washroom": "Attached Wash Room", "fee": 117500},
            {"name": "Teresa Bhawan - D", "occupancy": 4, "facility": "Non AC", "washroom": "Attached Wash Room", "fee": 110500},
            {"name": "Marie Curie", "occupancy": 3, "facility": "AC", "washroom": "Attached Wash Room", "fee": 2000},
            {"name": "Kalpana Bhawan - A", "occupancy": 2, "facility": "AC", "washroom": "Common Wash Room", "fee": 133000},
            {"name": "Kalpana Bhawan - B", "occupancy": 2, "facility": "Non AC", "washroom": "Common Wash Room", "fee": 106500},
            {"name": "Shakuntala Bhawan - A", "occupancy": 3, "facility": "AC", "washroom": "Attached Wash Room", "fee": 139000},
            {"name": "Shakuntala Bhawan - B", "occupancy": 4, "facility": "AC", "washroom": "Attached Wash Room", "fee": 131000},
            {"name": "Rani Laxmibai Bhavan -", "occupancy": 5, "facility": "AC", "washroom": "Common Wash Room", "fee": 128000}
        ]

        def add_hostel_with_floors_and_rooms(h, gender):
            hostel = Hostel(**h, gender=gender)
            db.session.add(hostel)
            db.session.flush()

            occ = int(h["occupancy"])  # beds per room = occupancy

            # Floors 1..6, rooms 01..25 -> 101..125, 201..225, ..., 601..625
            for floor_no in range(1, 7):
                floor = Floor(hostel_id=hostel.id, floor_no=floor_no)
                db.session.add(floor)
                db.session.flush()

                for n in range(1, 26):
                    room_number = f"{floor_no}{n:02d}"
                    room = Room(floor_id=floor.id, room_number=room_number, capacity=occ)
                    db.session.add(room)
                    db.session.flush()

                    for bed_num in range(1, occ + 1):
                        bed = Bed(room_id=room.id, bed_number=str(bed_num), is_booked=False)
                        db.session.add(bed)

        for h in men_hostels:
            add_hostel_with_floors_and_rooms(h, "men")
        for h in women_hostels:
            add_hostel_with_floors_and_rooms(h, "women")

        db.session.commit()
        print("✅ Hostels, Floors, Rooms, and Beds seeded successfully!")
        
if __name__ == "__main__":
    seed_hostels()
