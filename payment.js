document.getElementById("paymentForm").addEventListener("submit", async function (e) {
  e.preventDefault();

  const fileInput = document.getElementById("paymentFile");
  if (!fileInput.files.length) {
    alert("Please upload a payment proof file!");
    return;
  }

  const bookingData = JSON.parse(localStorage.getItem("bookingData"));
  if (!bookingData || !bookingData.payment_id) {
    alert("Missing payment reference. Please re-book.");
    return;
  }

  const formData = new FormData();
  formData.append("payment_id", bookingData.payment_id);
  formData.append("file", fileInput.files[0]);

  try {
    const res = await fetch("/api/payment/proof", {
      method: "POST",
      body: formData,
    });

    const data = await res.json();
    console.log("Payment Server Response:", data);

    if (!res.ok || !data.success) {
      alert(data.error || "Proof upload failed.");
      return;
    }

    // Remove the receipt download, just show thank you
    alert("Thank you for your booking! ✅");
    window.location.href = "success.html"; // you can keep this if you have a success page

  } catch (err) {
    console.error("Payment Upload Error:", err);
    alert("A network error occurred while uploading proof.");
  }
});
