// Load pending bookings when page opens
fetch("/pending_bookings")
  .then(res => res.json())
  .then(bookings => {
    let table = document.getElementById("bookingTable");

    bookings.forEach(b => {
      let row = document.createElement("tr");
      row.innerHTML = `
        <td>${b.id}</td>
        <td>${b.student}</td>
        <td>${b.email}</td>
        <td>${b.hostel}</td>
        <td>${b.room}</td>
        <td>${b.bed}</td>
        <td id="status-${b.id}">${b.status}</td>
        <td>
          ${b.payment_file
            ? `<a href="${b.payment_file}" target="_blank">
                 <button style="padding:6px 10px; background:#673ab7; color:white; border:none; border-radius:4px;">
                     View File
                 </button>
               </a>`
            : `<span style="color:red;">No File</span>`}
        </td>
        <td>
          <button class="approve-btn" onclick="approveBooking(${b.id})">Approve</button>
          <button class="cancel-btn" onclick="cancelBooking(${b.id})">Cancel</button>
        </td>
      `;
      table.appendChild(row);
    });
  })
  .catch(err => console.error("Error loading bookings:", err));

// Approve booking
function approveBooking(bookingId) {
  fetch(`/approve_booking/${bookingId}`, {
    method: "POST"
  })
  .then(res => res.json())
  .then(data => {
    document.getElementById(`status-${bookingId}`).innerText = "approved";
    alert("Booking Approved & Email Sent!");
  })
  .catch(err => console.error("Error approving:", err));
}


// Cancel booking
function cancelBooking(bookingId) {
  fetch(`/cancel_booking/${bookingId}`, {
    method: "POST"
  })
  .then(res => res.json())
  .then(data => {
    document.getElementById(`status-${bookingId}`).innerText = "cancelled";
    alert("Booking Cancelled!");
  })
  .catch(err => console.error("Error cancelling:", err));
}
