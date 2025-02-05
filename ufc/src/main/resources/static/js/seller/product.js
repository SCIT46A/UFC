document.addEventListener("DOMContentLoaded", function () {
    // Date range buttons
    const dateButtons = document.querySelectorAll(".date-buttons button");
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
                case "6개월":
                    startDate.setMonth(today.getMonth() - 6);
                    break;
                case "1년":
                    startDate.setFullYear(today.getFullYear() - 1);
                    break;
                case "전체":
                    // Clear the date inputs
                    document
                        .querySelectorAll(".date-input")
                        .forEach((input) => {
                            input.value = "";
                        });
                    return;
            }

            const dateInputs = document.querySelectorAll(".date-input");
            if (this.textContent !== "전체") {
                dateInputs[0].value = formatDate(startDate);
                dateInputs[1].value = formatDate(today);
            }
        });
    });

    // Reset button
    const resetButton = document.querySelector(".btn-secondary");
    resetButton.addEventListener("click", function () {
        // Reset text inputs
        document.querySelectorAll('input[type="text"]').forEach((input) => {
            input.value = "";
        });

        // Reset checkboxes
        document
            .querySelectorAll('input[type="checkbox"]')
            .forEach((checkbox) => {
                checkbox.checked = false;
            });

        // Reset date buttons
        dateButtons.forEach((btn) => btn.classList.remove("active"));
        document
            .querySelector(".date-buttons button:last-child")
            .classList.add("active");

        // Reset date inputs
        document.querySelectorAll(".date-input").forEach((input) => {
            input.value = "";
        });

        // Reset selects
        document.querySelectorAll("select").forEach((select) => {
            select.selectedIndex = 0;
        });
    });

    // Select all checkbox
    const selectAll = document.querySelector('thead input[type="checkbox"]');
    if (selectAll) {
        selectAll.addEventListener("change", function () {
            document
                .querySelectorAll('tbody input[type="checkbox"]')
                .forEach((checkbox) => {
                    checkbox.checked = this.checked;
                });
        });
    }
});

// Helper function to format date as YYYY-MM-DD
function formatDate(date) {
    return date.toISOString().split("T")[0];
}
