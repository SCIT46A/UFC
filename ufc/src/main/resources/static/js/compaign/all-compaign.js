document.addEventListener("DOMContentLoaded", () => {
    const perModal = document.querySelector(".main-modal-per");
    const perBtn = document.querySelector(
        ".main-top-in-content-target-box-span-btn"
    );
    const ModalClose = document.querySelector(".main-modal-per-close");
    const reBModal = document.querySelector(".main-modal-per-add");
    const reBtn = document.querySelector(
        ".main-top-in-content-target-box-span-btn-re"
    );

    perBtn.addEventListener("click", () => {
        perModal.style.display = "flex";
        ModalClose.style.display = "block";
    });

    ModalClose.addEventListener("click", () => {
        perModal.style.display = "none";
        reBModal.style.display = "none";
        ModalClose.style.display = "none";
    });

    reBtn.addEventListener("click", () => {
        reBModal.style.display = "flex";
        ModalClose.style.display = "block";
    });
});
