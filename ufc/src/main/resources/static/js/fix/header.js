document.addEventListener("DOMContentLoaded", () => {
    // Lucide Icons Initialization
    if (window.lucide) {
        lucide.createIcons();
    } else {
        console.error("Lucide not loaded");
    }

    // Current Year Update
    const yearElement = document.getElementById("current-year");
    if (yearElement) {
        yearElement.textContent = new Date().getFullYear();
    }

    // Search Modal
    const searchBtn = document.querySelector(".header-search");
    const searchModal = document.querySelector("#search-modal");
    const searchModalClose = document.querySelector(".modal-search-box-in-top-bu");

    if (searchBtn && searchModal && searchModalClose) {
        searchBtn.addEventListener("click", () => {
            searchModal.classList.remove("hidden");
        });
        searchModalClose.addEventListener("click", () => {
            searchModal.classList.add("hidden");
        });
    }

    // Login Modal
    const loginInfo = document.querySelector(".header-box-top-pe-my");
    const loginModal = document.querySelector(".modal-login");
    const loginClose = document.querySelector("#modal-controller");
    if (loginInfo && loginModal && loginClose) {
        loginInfo.addEventListener("click", () => {
            loginModal.style.display = "flex";
        });
        loginClose.addEventListener("click", () => {
            loginModal.style.display = "none";
        });
    }

    // Category Modal
    const category = document.querySelector(".header-nav-in-me-cate");
    const categoryModal = document.querySelector(".modal-cate");
    const cateClose = document.querySelector(".cate-close");
    if (category && categoryModal && cateClose) {
        category.addEventListener("click", () => {
            categoryModal.classList.remove("hidden");
        });
        cateClose.addEventListener("click", () => {
            categoryModal.classList.add("hidden");
        });
    }
});
