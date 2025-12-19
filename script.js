// --- Show register form ---
document.getElementById("openRegister").addEventListener("click", function(e) {
    e.preventDefault();
    // Hide login form section
    document.getElementById("loginForm").parentElement.style.display = "none";
    // Show register form section
    document.getElementById("registerCard").style.display = "block";
});

// --- Handle login ---
document.getElementById("loginForm").addEventListener("submit", function(e) {
    e.preventDefault();
    const email = document.getElementById("email").value;
    const password = document.getElementById("password").value;

    // Check if user exists in localStorage
    const storedUser = JSON.parse(localStorage.getItem(email));
    if(storedUser && storedUser.password === password){
        localStorage.setItem("userEmail", email);
        window.location.href = "hostels.html";
    } else {
        alert("Invalid email or password!");
    }
});

// --- Handle registration ---
document.getElementById("registerForm").addEventListener("submit", function(e) {
    e.preventDefault();
    const name = document.getElementById("regName").value;
    const email = document.getElementById("regEmail").value;
    const password = document.getElementById("regPassword").value;

    // Save user in localStorage
    if(localStorage.getItem(email)){
        alert("User already exists. Please login.");
        return;
    }
    localStorage.setItem(email, JSON.stringify({name, email, password}));
    alert("Registration successful! Please login.");

    // Switch back to login form
    document.getElementById("registerCard").style.display = "none";
    document.getElementById("loginForm").parentElement.style.display = "block";

    // Optional: clear register form fields
    document.getElementById("registerForm").reset();
});
