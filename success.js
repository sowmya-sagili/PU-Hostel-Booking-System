document.getElementById("downloadProof").addEventListener("click", async function(){
  const { jsPDF } = window.jspdf;
  const doc = new jsPDF();

  // Logo
  const logoUrl = "assets/pu_logo.jpeg";
  const logo = await loadImage(logoUrl);
  doc.addImage(logo, "JPEG", 160, 10, 34, 34);

  // Header
  doc.setFontSize(15);
  doc.setFont("helvetica", "bold");
  doc.text("PARUL UNIVERSITY", 15, 20);
  doc.setFontSize(11);
  doc.text("Parul Institute of Engineering & Technology (First Shift)", 15, 27);
  doc.setFontSize(10);
  doc.text("P.O. Limda, Tal. Waghodia", 15, 33);
  doc.text("Web: https://paruluniversity.ac.in", 15, 39);

  doc.setFontSize(12);
  doc.text("Hostel Booking Receipt", 78, 50);

  // Key-value fields
  const bookingData = JSON.parse(localStorage.getItem("bookingData"));
  const today = new Date().toLocaleDateString();
  const paymentProof = localStorage.getItem("paymentProof") || "Uploaded";
  const studentDetails = [
    ["Student Name", bookingData.studentName||""],
    ["Phone Number", bookingData.phone||""],
    ["Hostel Name", bookingData.hostelName||""],
    ["Room Number", bookingData.room||""],
    ["Bed Number", bookingData.bed||""],
    ["Booking Date", today],
    ["Payment Proof", paymentProof]
  ];

  doc.autoTable({
    startY: 58,
    theme: 'plain',
    styles: { fontSize:10, cellPadding: 2, },
    columnStyles: { 0: { fontStyle:"bold", cellWidth:40 }, 1: { cellWidth:65 } },
    body: studentDetails,
    tableWidth: 120,
    margin: {left: 15}
  });

  // Table of charges
  doc.autoTable({
    startY: doc.lastAutoTable.finalY + 6,
    head: [['Particular', 'Amount (₹)']],
    body: [
      ['Hostel Room Booking Fee', bookingData.amount||''],
    ],
    theme: 'grid',
    styles: { fontSize:11, cellPadding: 3},
    headStyles: { fillColor:[226,226,226],textColor:20 },
    columnStyles: { 1: {halign:'right'}, 0: {halign:'left'} },
    margin: {left:15, right:15},
    tableWidth:'auto'
  });

  // Total (bold, right aligned)
  const amount = bookingData.amount;
  doc.setFont("helvetica", "bold");
  doc.text("Total", 120, doc.lastAutoTable.finalY + 12);
  doc.text(String(amount), 180, doc.lastAutoTable.finalY + 12, {align:'right'});

  // Footer lines
  doc.setFont("helvetica", "normal");
  doc.setFontSize(9);
  doc.text("This is a digitally generated proof of your hostel booking.", 15, 270);
  doc.text("For, Parul Institute of Engineering & Technology (First Shift)", 120, 277);

  // Payment details footer
  doc.setFontSize(9);
  doc.text(`Paid via: ${paymentProof}`, 15, 288);
  doc.text(`Generated on: ${today}`, 150, 288);

  doc.save("Hostel_Booking_Proof.pdf");
});

async function loadImage(url) {
  return new Promise((resolve) => {
    const img = new Image();
    img.crossOrigin = "anonymous";
    img.src = url;
    img.onload = () => {
      const canvas = document.createElement("canvas");
      canvas.width = img.width;
      canvas.height = img.height;
      const ctx = canvas.getContext("2d");
      ctx.drawImage(img, 0, 0);
      resolve(canvas.toDataURL("image/jpeg"));
    };
  });
}
