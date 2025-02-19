document.addEventListener("DOMContentLoaded", function () {
    // Date buttons
    const dateButtons = document.querySelectorAll(".btn-date");
    dateButtons.forEach((button) => {
        button.addEventListener("click", function () {
            dateButtons.forEach((btn) => btn.classList.remove("active"));
            this.classList.add("active");

            // Update date inputs based on selection
            const today = new Date();
            let startDate = new Date();

            switch (this.textContent) {
                case "오늘":
                    startDate = today;
                    break;
                case "1주일":
                    startDate.setDate(today.getDate() - 7);
                    break;
                case "1개월":
                    startDate.setMonth(today.getMonth() - 1);
                    break;
                case "3개월":
                    startDate.setMonth(today.getMonth() - 3);
                    break;
            }

            const dateInputs = document.querySelectorAll('input[type="date"]');
            dateInputs[0].value = formatDate(startDate);
            dateInputs[1].value = formatDate(today);
        });
    });

    // Tab buttons
    const tabButtons = document.querySelectorAll(".tab-btn");
    tabButtons.forEach((button) => {
        button.addEventListener("click", function () {
            tabButtons.forEach((btn) => btn.classList.remove("active"));
            this.classList.add("active");
        });
    });

    // Select all checkbox
    const selectAllCheckbox = document.getElementById("selectAll");
    if (selectAllCheckbox) {
        selectAllCheckbox.addEventListener("change", function () {
            const checkboxes = document.querySelectorAll(
                'tbody input[type="checkbox"]'
            );
            checkboxes.forEach((checkbox) => {
                checkbox.checked = this.checked;
            });
        });
    }

    // Reset button
    const resetButton = document.querySelector(".btn-secondary");
    resetButton.addEventListener("click", function () {
        // Reset select boxes
        document.querySelectorAll("select").forEach((select) => {
            select.selectedIndex = 0;
        });

        // Reset input
        document.querySelector(".input-box").value = "";

        // Reset date buttons
        dateButtons.forEach((btn) => btn.classList.remove("active"));
        document
            .querySelector(".btn-date:nth-child(4)")
            .classList.add("active");

        // Reset date inputs to default values
        const dateInputs = document.querySelectorAll('input[type="date"]');
        dateInputs[0].value = "2024-11-04";
        dateInputs[1].value = "2025-02-04";
    });
});

// Helper function to format date as YYYY-MM-DD
function formatDate(date) {
    const year = date.getFullYear();
    const month = String(date.getMonth() + 1).padStart(2, "0");
    const day = String(date.getDate()).padStart(2, "0");
    return `${year}-${month}-${day}`;
}
