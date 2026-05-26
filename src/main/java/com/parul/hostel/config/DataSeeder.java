package com.parul.hostel.config;

import com.parul.hostel.entity.*;
import com.parul.hostel.repository.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Component
@RequiredArgsConstructor
@Slf4j
public class DataSeeder implements CommandLineRunner {

    private final HostelRepository hostelRepository;
    private final FloorRepository floorRepository;
    private final RoomRepository roomRepository;
    private final BedRepository bedRepository;
    private final StudentRepository studentRepository;
    private final org.springframework.security.crypto.password.PasswordEncoder passwordEncoder;

    @Override
    @Transactional
    public void run(String... args) throws Exception {
        // Seed default administrator if not present
        if (studentRepository.findByEmail("admin@parul.com").isEmpty()) {
            Student admin = Student.builder()
                    .name("Administrator")
                    .email("admin@parul.com")
                    .password(passwordEncoder.encode("admin123"))
                    .role("ADMIN")
                    .build();
            studentRepository.save(admin);
            log.info("Admin account seeded successfully: admin@parul.com");
        }

        if (hostelRepository.count() > 0) {
            log.info("Database already seeded with hostels. Skipping seeder.");
            return;
        }

        log.info("Seeding database with hostels, floors, rooms, and beds...");
        long startTime = System.currentTimeMillis();

        // Seed Men's Hostels
        List<Map<String, Object>> menHostels = getMenHostelsData();
        for (Map<String, Object> h : menHostels) {
            seedHostel(h, "men");
        }

        // Seed Women's Hostels
        List<Map<String, Object>> womenHostels = getWomenHostelsData();
        for (Map<String, Object> h : womenHostels) {
            seedHostel(h, "women");
        }

        long duration = System.currentTimeMillis() - startTime;
        log.info("Database seeding completed in {} ms!", duration);
    }

    private void seedHostel(Map<String, Object> h, String gender) {
        Hostel hostel = Hostel.builder()
                .name((String) h.get("name"))
                .occupancy((Integer) h.get("occupancy"))
                .fee((Integer) h.get("fee"))
                .facility((String) h.get("facility"))
                .washroom((String) h.get("washroom"))
                .gender(gender)
                .build();

        hostel = hostelRepository.save(hostel);
        int occ = hostel.getOccupancy();

        // Bulk saving floors, rooms, and beds for optimization
        List<Floor> floors = new ArrayList<>();
        for (int floorNo = 1; floorNo <= 6; floorNo++) {
            Floor floor = Floor.builder()
                    .hostel(hostel)
                    .floorNo(floorNo)
                    .build();
            floors.add(floor);
        }
        floors = floorRepository.saveAll(floors);

        List<Room> rooms = new ArrayList<>();
        for (Floor floor : floors) {
            for (int n = 1; n <= 25; n++) {
                String roomNumber = String.format("%d%02d", floor.getFloorNo(), n);
                Room room = Room.builder()
                        .floor(floor)
                        .roomNumber(roomNumber)
                        .capacity(occ)
                        .build();
                rooms.add(room);
            }
        }
        rooms = roomRepository.saveAll(rooms);

        List<Bed> beds = new ArrayList<>();
        for (Room room : rooms) {
            for (int bedNum = 1; bedNum <= occ; bedNum++) {
                Bed bed = Bed.builder()
                        .room(room)
                        .bedNumber(String.valueOf(bedNum))
                        .isBooked(false)
                        .build();
                beds.add(bed);
            }
        }
        bedRepository.saveAll(beds);
        log.debug("Seeded hostel: {} with {} floors, {} rooms, and {} beds.", hostel.getName(), floors.size(), rooms.size(), beds.size());
    }

    private List<Map<String, Object>> getMenHostelsData() {
        List<Map<String, Object>> list = new ArrayList<>();
        list.add(Map.of("name", "Shastri Bhawan - A", "occupancy", 3, "facility", "Non AC", "washroom", "Common Wash Room", "fee", 95000));
        list.add(Map.of("name", "Shastri Bhawan - B", "occupancy", 4, "facility", "Non AC", "washroom", "Common Wash Room", "fee", 90500));
        list.add(Map.of("name", "Shastri Bhawan - C", "occupancy", 3, "facility", "Non AC", "washroom", "Common Wash Room", "fee", 95500));
        list.add(Map.of("name", "Kalam Bhawan A", "occupancy", 9, "facility", "Non AC", "washroom", "2 Attached Wash Room", "fee", 97500));
        list.add(Map.of("name", "Kalam Bhawan B", "occupancy", 9, "facility", "Non AC", "washroom", "2 Attached Wash Room", "fee", 97500));
        list.add(Map.of("name", "Kalam Bhawan C", "occupancy", 9, "facility", "Non AC", "washroom", "2 Attached Wash Room", "fee", 97500));
        list.add(Map.of("name", "Tagore Bhawan A", "occupancy", 3, "facility", "Non AC", "washroom", "Attached Wash Room", "fee", 123500));
        list.add(Map.of("name", "Tagore Bhawan B", "occupancy", 3, "facility", "Non AC", "washroom", "Attached Wash Room", "fee", 123500));
        list.add(Map.of("name", "Tagore Bhawan C", "occupancy", 3, "facility", "Non AC", "washroom", "Attached Wash Room", "fee", 123500));
        list.add(Map.of("name", "Dhyan Bhawan", "occupancy", 5, "facility", "AC", "washroom", "Common Wash Room", "fee", 128000));
        list.add(Map.of("name", "Sardar Bhawan - A", "occupancy", 4, "facility", "Non AC", "washroom", "Attached Wash Room", "fee", 104500));
        list.add(Map.of("name", "Sardar Bhawan - B", "occupancy", 3, "facility", "Non AC", "washroom", "Common Wash Room", "fee", 104500));
        list.add(Map.of("name", "Sardar Bhawan - C", "occupancy", 3, "facility", "Non AC", "washroom", "Attached Wash Room", "fee", 112500));
        list.add(Map.of("name", "Milkha Bhawan - A", "occupancy", 3, "facility", "Non AC", "washroom", "Attached Wash Room", "fee", 129500));
        list.add(Map.of("name", "Atal Bhawan-A1", "occupancy", 4, "facility", "Non AC", "washroom", "Attached Wash Room", "fee", 115500));
        list.add(Map.of("name", "Atal Bhawan - B", "occupancy", 8, "facility", "Non AC", "washroom", "Common Wash Room", "fee", 84500));
        list.add(Map.of("name", "Albert Einstein", "occupancy", 2, "facility", "AC", "washroom", "Attached Wash Room", "fee", 2000));
        list.add(Map.of("name", "Azad Bhavan - A", "occupancy", 8, "facility", "Non AC", "washroom", "Common Wash Room", "fee", 90500));
        list.add(Map.of("name", "Atal Bhavan A2 Boys", "occupancy", 4, "facility", "Non AC", "washroom", "Attached Wash Room", "fee", 115500));
        list.add(Map.of("name", "Tilak Bhawan-A", "occupancy", 3, "facility", "Non AC", "washroom", "Attached Wash Room", "fee", 129500));
        list.add(Map.of("name", "Abraham Lincoln", "occupancy", 5, "facility", "AC", "washroom", "Common Wash Room", "fee", 1500));
        list.add(Map.of("name", "Saarthi Hostel", "occupancy", 4, "facility", "Non AC", "washroom", "Attached Wash Room", "fee", 100000));
        return list;
    }

    private List<Map<String, Object>> getWomenHostelsData() {
        List<Map<String, Object>> list = new ArrayList<>();
        list.add(Map.of("name", "Sarojini Bhawan - A", "occupancy", 4, "facility", "Non AC", "washroom", "Common Wash Room", "fee", 85500));
        list.add(Map.of("name", "Sarojini Bhawan - B", "occupancy", 5, "facility", "Non AC", "washroom", "Common Wash Room", "fee", 84500));
        list.add(Map.of("name", "Sarojini Bhawan - C", "occupancy", 8, "facility", "Non AC", "washroom", "Common Wash Room", "fee", 84500));
        list.add(Map.of("name", "Indira Bhawan - A", "occupancy", 9, "facility", "Non AC", "washroom", "Attached Wash Room", "fee", 93500));
        list.add(Map.of("name", "Indira Bhawan - B", "occupancy", 10, "facility", "Non AC", "washroom", "Attached Wash Room", "fee", 93500));
        list.add(Map.of("name", "Indira Bhawan - C", "occupancy", 9, "facility", "Non AC", "washroom", "Attached Wash Room", "fee", 93500));
        list.add(Map.of("name", "Teresa Bhawan - AIBIC A", "occupancy", 3, "facility", "Non AC", "washroom", "Attached Wash Room", "fee", 117500));
        list.add(Map.of("name", "Teresa Bhawan - AIBIC B", "occupancy", 3, "facility", "Non AC", "washroom", "Attached Wash Room", "fee", 117500));
        list.add(Map.of("name", "Teresa Bhawan - AIBIC C", "occupancy", 3, "facility", "Non AC", "washroom", "Attached Wash Room", "fee", 117500));
        list.add(Map.of("name", "Teresa Bhawan - D", "occupancy", 4, "facility", "Non AC", "washroom", "Attached Wash Room", "fee", 110500));
        list.add(Map.of("name", "Marie Curie", "occupancy", 3, "facility", "AC", "washroom", "Attached Wash Room", "fee", 2000));
        list.add(Map.of("name", "Kalpana Bhawan - A", "occupancy", 2, "facility", "AC", "washroom", "Common Wash Room", "fee", 133000));
        list.add(Map.of("name", "Kalpana Bhawan - B", "occupancy", 2, "facility", "Non AC", "washroom", "Common Wash Room", "fee", 106500));
        list.add(Map.of("name", "Shakuntala Bhawan - A", "occupancy", 3, "facility", "AC", "washroom", "Attached Wash Room", "fee", 139000));
        list.add(Map.of("name", "Shakuntala Bhawan - B", "occupancy", 4, "facility", "AC", "washroom", "Attached Wash Room", "fee", 131000));
        list.add(Map.of("name", "Rani Laxmibai Bhavan -", "occupancy", 5, "facility", "AC", "washroom", "Common Wash Room", "fee", 128000));
        return list;
    }
}
