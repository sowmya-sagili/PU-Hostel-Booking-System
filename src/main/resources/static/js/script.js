// --- Show register form ---
document.getElementById("openRegister").addEventListener("click", function(e) {
    e.preventDefault();
    // Hide login form section
    document.getElementById("loginForm").parentElement.style.display = "none";
    // Show register form section
    document.getElementById("registerCard").style.display = "block";
});

// --- Handle login ---
document.getElementById("loginForm").addEventListener("submit", async function(e) {
    e.preventDefault();
    const email = document.getElementById("email").value.trim();
    const password = document.getElementById("password").value.trim();

    try {
        const res = await fetch("/api/auth/login", {
            method: "POST",
            headers: { "Content-Type": "application/json" },
            body: JSON.stringify({ email, password })
        });
        const data = await res.json();
        
        if (res.ok && data.success) {
            localStorage.setItem("userEmail", data.email);
            localStorage.setItem("userName", data.name);
            
            if (data.role === "ADMIN") {
                window.location.href = "/admin.html";
            } else {
                window.location.href = "/hostels.html";
            }
        } else {
            alert(data.error || "Invalid email or password!");
        }
    } catch (err) {
        console.error("Login error:", err);
        alert("A network error occurred. Please try again.");
    }
});

// --- Handle registration ---
document.getElementById("registerForm").addEventListener("submit", async function(e) {
    e.preventDefault();
    const name = document.getElementById("regName").value.trim();
    const email = document.getElementById("regEmail").value.trim();
    const password = document.getElementById("regPassword").value.trim();

    try {
        const res = await fetch("/api/auth/register", {
            method: "POST",
            headers: { "Content-Type": "application/json" },
            body: JSON.stringify({ name, email, password })
        });
        const data = await res.json();

        if (res.ok && data.success) {
            alert("Registration successful! Please login.");
            // Switch back to login form
            document.getElementById("registerCard").style.display = "none";
            document.getElementById("loginForm").parentElement.style.display = "block";
            document.getElementById("registerForm").reset();
        } else {
            alert(data.error || "Registration failed!");
        }
    } catch (err) {
        console.error("Registration error:", err);
        alert("A network error occurred. Please try again.");
    }
});
