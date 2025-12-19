// booking.js

window.onload = function () {
  const selectedHostel = JSON.parse(localStorage.getItem("selectedHostel"));
  if (!selectedHostel) {
    alert("No hostel selected! Redirecting to hostel list.");
    window.location.href = "hostels.html";
    return;
  }

  // Define elements using their correct IDs from booking.html
  const hostelNameInput = document.getElementById("hostelName");
  const amountInput = document.getElementById("amount");
  const floorSelect = document.getElementById("floor");
  const roomSelect = document.getElementById("room");
  const bedSelect = document.getElementById("bed");

  hostelNameInput.value = selectedHostel.name || "";
  amountInput.value = selectedHostel.fee || "";

  async function updateAvailableBeds() {
    const hostel = hostelNameInput.value;
    const floor = floorSelect.value;
    const room = roomSelect.value;

    if (!hostel || !floor || !room) return;

    bedSelect.innerHTML = '<option value="">Loading beds...</option>';
    try {
      // This fetch call now has all the correct data
      const res = await fetch(`/api/available_beds?hostel=${hostel}&floor=${floor}&room=${room}`);
      const data = await res.json();
      bedSelect.innerHTML = "";
      if (data.success && data.beds.length > 0) {
        data.beds.forEach(bed => {
          const opt = document.createElement("option");
          opt.value = bed.id;
          opt.textContent = `Bed ${bed.bed_number}`;
          bedSelect.appendChild(opt);
        });
      } else {
        bedSelect.innerHTML = '<option value="" disabled>No available beds</option>';
      }
    } catch (err) {
      console.error("Failed to fetch beds:", err);
      bedSelect.innerHTML = '<option value="" disabled>Error loading beds</option>';
    }
  }

  // Populate floors
  for (let i = 1; i <= 6; i++) {
    const opt = document.createElement("option");
    opt.value = i;
    opt.textContent = `Floor ${i}`;
    floorSelect.appendChild(opt);
  }

  // When floor changes, update rooms, which then updates beds
  floorSelect.addEventListener("change", function () {
    const floor = parseInt(this.value);
    roomSelect.innerHTML = "";
    const start = floor * 100 + 1;
    const end = start + 24;
    for (let i = start; i <= end; i++) {
      const opt = document.createElement("option");
      opt.value = i;
      opt.textContent = `Room ${i}`;
      roomSelect.appendChild(opt);
    }
    roomSelect.dispatchEvent(new Event("change"));
  });

  roomSelect.addEventListener("change", updateAvailableBeds);

  // Initial load
  floorSelect.value = "1";
  floorSelect.dispatchEvent(new Event("change"));
};

// This function now correctly reads all values because the IDs match
document.getElementById("bookingForm").addEventListener("submit", async function (e) {
  e.preventDefault();

  const bedId = document.getElementById("bed").value;
  if (!bedId) {
    alert("Please select an available bed.");
    return;
  }
  
  const payload = {
    student_name: document.getElementById("studentName").value.trim(),
    student_email: document.getElementById("email").value.trim(),
    student_phone: document.getElementById("phone").value.trim(),
    student_gender: document.getElementById("gender").value,
    amount: parseInt(document.getElementById("amount").value, 10),
    bed_id: parseInt(bedId, 10)
  };

  if (!payload.student_name || !payload.student_email) {
    alert("Please fill in all required details.");
    return;
  }

  try {
    const res = await fetch("/api/book", {
      method: "POST",
      headers: { "Content-Type": "application/json" },
      body: JSON.stringify(payload),
    });
    
    const data = await res.json();
    console.log("Server Response:", data);

    if (!res.ok || !data.success) {
      alert(data.error || "Booking failed!");
      document.getElementById("room").dispatchEvent(new Event("change"));
      return;
    }

    const bookingDataForNextStep = {
      studentName: payload.student_name,
      studentEmail: payload.student_email,
      phone: payload.student_phone,
      hostelName: document.getElementById("hostelName").value,
      payment_id: data.payment_id,
    };
    
    localStorage.setItem("bookingData", JSON.stringify(bookingDataForNextStep));
    window.location.href = "payment.html";

  } catch (err) {
    console.error("Booking Error:", err);
    alert("A network error occurred. Please try again.");
  }
});

document.getElementById("btnBack").addEventListener("click", function () {
  window.location.href = "hostels.html";
});
