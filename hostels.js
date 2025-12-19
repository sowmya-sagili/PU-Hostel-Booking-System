// Hostel Data
const hostels = {
  men: [
    { name: "Shastri Bhawan - A", occupancy: 3, facility: "Non AC", washroom: "Common Wash Room", fee: "95000" },
    { name: "Shastri Bhawan - B", occupancy: 4, facility: "Non AC", washroom: "Common Wash Room", fee: "90500" },
    { name: "Shastri Bhawan - C", occupancy: 3, facility: "Non AC", washroom: "Common Wash Room", fee: "95500" },
    { name: "Kalam Bhawan A", occupancy: 9, facility: "Non AC", washroom: "2 Attached Wash Room", fee: "97500" },
    { name: "Kalam Bhawan B", occupancy: 9, facility: "Non AC", washroom: "2 Attached Wash Room", fee: "97500" },
    { name: "Kalam Bhawan C", occupancy: 9, facility: "Non AC", washroom: "2 Attached Wash Room", fee: "97500" },
    { name: "Tagore Bhawan A", occupancy: 3, facility: "Non AC", washroom: "Attached Wash Room", fee: "123500" },
    { name: "Tagore Bhawan B", occupancy: 3, facility: "Non AC", washroom: "Attached Wash Room", fee: "123500" },
    { name: "Tagore Bhawan C", occupancy: 3, facility: "Non AC", washroom: "Attached Wash Room", fee: "123500" },
    { name: "Dhyan Bhawan", occupancy: 5, facility: "AC", washroom: "Common Wash Room", fee: "128000" },
    { name: "Sardar Bhawan - A", occupancy: 4, facility: "Non AC", washroom: "Attached Wash Room", fee: "104500" },
    { name: "Sardar Bhawan - B", occupancy: 3, facility: "Non AC", washroom: "Common Wash Room", fee: "104500" },
    { name: "Sardar Bhawan - C", occupancy: 3, facility: "Non AC", washroom: "Attached Wash Room", fee: "112500" },
    { name: "Milkha Bhawan - A", occupancy: 3, facility: "Non AC", washroom: "Attached Wash Room", fee: "129500" },
    { name: "Atal Bhawan-A1", occupancy: 4, facility: "Non AC", washroom: "Attached Wash Room", fee: "115500" },
    { name: "Atal Bhawan - B", occupancy: 8, facility: "Non AC", washroom: "Common Wash Room", fee: "84500" },
    { name: "Albert Einstein", occupancy: 2, facility: "AC", washroom: "Attached Wash Room", fee: "USD 2000" },
    { name: "Azad Bhavan - A", occupancy: 8, facility: "Non AC", washroom: "Common Wash Room", fee: "90500" },
    { name: "Atal Bhavan A2 Boys", occupancy: 4, facility: "Non AC", washroom: "Attached Wash Room", fee: "115500" },
    { name: "Tilak Bhawan-A", occupancy: 3, facility: "Non AC", washroom: "Attached Wash Room", fee: "129500" },
    { name: "Abraham Lincoln", occupancy: 5, facility: "AC", washroom: "Common Wash Room", fee: "USD 1500" },
    { name: "Saarthi Hostel", occupancy: 4, facility: "Non AC", washroom: "Attached Wash Room", fee: "100000" }
  ],
  women: [
    { name: "Sarojini Bhawan - A", occupancy: 4, facility: "Non AC", washroom: "Common Wash Room", fee: "85500" },
    { name: "Sarojini Bhawan - B", occupancy: 5, facility: "Non AC", washroom: "Common Wash Room", fee: "84500" },
    { name: "Sarojini Bhawan - C", occupancy: 8, facility: "Non AC", washroom: "Common Wash Room", fee: "84500" },
    { name: "Indira Bhawan - A", occupancy: 9, facility: "Non AC", washroom: "Attached Wash Room", fee: "93500" },
    { name: "Indira Bhawan - B", occupancy: 10, facility: "Non AC", washroom: "Attached Wash Room", fee: "93500" },
    { name: "Indira Bhawan - C", occupancy: 9, facility: "Non AC", washroom: "Attached Wash Room", fee: "93500" },
    { name: "Teresa Bhawan - AIBIC A", occupancy: 3, facility: "Non AC", washroom: "Attached Wash Room", fee: "117500" },
    { name: "Teresa Bhawan - AIBIC B", occupancy: 3, facility: "Non AC", washroom: "Attached Wash Room", fee: "117500" },
    { name: "Teresa Bhawan - AIBIC C", occupancy: 3, facility: "Non AC", washroom: "Attached Wash Room", fee: "117500" },
    { name: "Teresa Bhawan - D", occupancy: 4, facility: "Non AC", washroom: "Attached Wash Room", fee: "110500" },
    { name: "Marie Curie", occupancy: 3, facility: "AC", washroom: "Attached Wash Room", fee: "USD 2000" },
    { name: "Kalpana Bhawan - A", occupancy: 2, facility: "AC", washroom: "Common Wash Room", fee: "133000" },
    { name: "Kalpana Bhawan - B", occupancy: 2, facility: "Non AC", washroom: "Common Wash Room", fee: "106500" },
    { name: "Shakuntala Bhawan - A", occupancy: 3, facility: "AC", washroom: "Attached Wash Room", fee: "139000" },
    { name: "Shakuntala Bhawan - B", occupancy: 4, facility: "AC", washroom: "Attached Wash Room", fee: "131000" },
    { name: "Rani Laxmibai Bhavan -", occupancy: 5, facility: "AC", washroom: "Common Wash Room", fee: "128000" }
  ]
};

// Buttons
document.getElementById("btnMen").addEventListener("click", () => showHostelList("men"));
document.getElementById("btnWomen").addEventListener("click", () => showHostelList("women"));

// Show hostel list with details
function showHostelList(type) {
  const hostelList = document.getElementById("hostelList");
  hostelList.innerHTML = ""; // clear

  hostels[type].forEach(h => {
    const card = document.createElement("div");
    card.className = "card";
    card.innerHTML = `
      <h3>${h.name}</h3>
      <p><b>Occupancy:</b> ${h.occupancy} beds</p>
      <p><b>Facility:</b> ${h.facility}</p>
      <p><b>Washroom:</b> ${h.washroom}</p>
      <p><b>Fee:</b> ${h.fee}</p>
      <button>Select</button>
    `;

    card.querySelector("button").onclick = () => {
      localStorage.setItem("selectedHostel", JSON.stringify(h));
      window.location.href = "booking.html";
    };

    hostelList.appendChild(card);
  });
}
