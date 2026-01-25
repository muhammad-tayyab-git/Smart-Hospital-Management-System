/* =========================
   ADMIN UI SCRIPT
========================= */

document.addEventListener("DOMContentLoaded", () => {

    /* ========= SIDEBAR TOGGLE ========= */
    const sidebar = document.getElementById("sidebar");
    const sidebarToggle = document.getElementById("sidebarToggle");
    const content = document.getElementById("content");
    const topNavbar = document.getElementById("top-navbar");

    if (sidebarToggle) {
        sidebarToggle.addEventListener("click", () => {
            sidebar.classList.toggle("collapsed");
            content.classList.toggle("collapsed");
            topNavbar.classList.toggle("collapsed");

            // Save state
            localStorage.setItem(
                "sidebarCollapsed",
                sidebar.classList.contains("collapsed")
            );
        });
    }

    /* Restore sidebar state */
    if (localStorage.getItem("sidebarCollapsed") === "true") {
        sidebar.classList.add("collapsed");
        content.classList.add("collapsed");
        topNavbar.classList.add("collapsed");
    }

    /* ========= DARK MODE ========= */
    const darkModeToggle = document.getElementById("darkModeToggle");

    // Restore dark mode
    if (localStorage.getItem("darkMode") === "true") {
        document.body.classList.add("dark-mode");
        if (darkModeToggle) {
            darkModeToggle.textContent = "☀️ Light Mode";
        }
    }

    if (darkModeToggle) {
        darkModeToggle.addEventListener("click", () => {
            document.body.classList.toggle("dark-mode");

            const isDark = document.body.classList.contains("dark-mode");
            localStorage.setItem("darkMode", isDark);

            darkModeToggle.textContent = isDark
                ? "☀️ Light Mode"
                : "🌙 Dark Mode";
        });
    }

});
