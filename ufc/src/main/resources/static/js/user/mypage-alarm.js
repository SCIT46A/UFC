document.addEventListener("DOMContentLoaded", () => {
    const deleteBtn = document.querySelectorAll(
        ".al-in-box-warp-pe-delete-btn"
    );
    const deleteCancleBtn = document.querySelector(".delete-cancle-btn");
    const deleteBtnAdd = document.querySelector(".delete-btn");

    const deleteModal = document.querySelector(".delete");

    deleteBtn.forEach((e) => {
        e.addEventListener("click", () => {
            deleteModal.classList.remove("hidden");
        });
    });

    deleteCancleBtn.addEventListener("click", () => {
        deleteModal.classList.add("hidden");
    });

    deleteBtnAdd.addEventListener("click", () => {
        deleteModal.classList.add("hidden");
    });
});
