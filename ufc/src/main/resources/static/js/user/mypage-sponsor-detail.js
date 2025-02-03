document.addEventListener("DOMContentLoaded", () => {
    const cancleBtn = document.querySelector(
        ".main-in-mi-content-box-title-ri-btn"
    );
    const cancleModal = document.querySelector(".cancle-modal");
    const cancleCancleBtn = document.querySelector(
        ".cancle-modal-in-box-sh-in-top-btn"
    );
    const cancleCancleBtnadd = document.querySelector(".cancle-modal-btn-none");
    const cancleBtnadd = document.querySelector(".cancle-modal-btn-cancle");

    const addressBtn = document.querySelector(
        ".main-in-mi-content-box-title-ri-btn-add"
    );
    const addressModal = document.querySelector(".address");
    const addressCloseBtn = document.querySelector(
        ".address-in-box-in-wrap-top-btn"
    );
    const addressBtnadd = document.querySelector(".address-btn");

    cancleBtn.addEventListener("click", () => {
        cancleModal.classList.remove("hidden");
    });

    cancleCancleBtn.addEventListener("click", () => {
        cancleModal.classList.add("hidden");
    });

    cancleCancleBtnadd.addEventListener("click", () => {
        cancleModal.classList.add("hidden");
    });

    cancleBtnadd.addEventListener("click", () => {
        cancleModal.classList.add("hidden");
    });

    addressBtn.addEventListener("click", () => {
        addressModal.classList.remove("hidden");
    });

    addressCloseBtn.addEventListener("click", () => {
        addressModal.classList.add("hidden");
    });

    addressBtnadd.addEventListener("click", () => {
        addressModal.classList.add("hidden");
    });
});
