window.onload = function() {
  // Grab backend-confirmed booking data
  const bookingData = JSON.parse(localStorage.getItem("bookingData"));
  if (!bookingData) {
    alert("No booking found.");
    window.location.href = "booking.html";
    return;
  }
  // Fill details on the page as needed
  document.getElementById("studentNameDisplay").textContent = bookingData.studentName;
  document.getElementById("hostelNameDisplay").textContent = bookingData.hostelName;
  document.getElementById("roomDisplay").textContent = bookingData.room;
  // ...and so on for other fields

  // Optionally, store proof if needed for the PDF
  localStorage.setItem("paymentProof", bookingData.paymentProof || "Uploaded");
}
