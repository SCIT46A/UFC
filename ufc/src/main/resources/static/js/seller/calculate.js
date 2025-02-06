document.addEventListener("DOMContentLoaded", function () {
    // Tab switching
    const tabs = document.querySelectorAll(".tab");
    tabs.forEach((tab) => {
        tab.addEventListener("click", function () {
            tabs.forEach((t) => t.classList.remove("active"));
            this.classList.add("active");
        });
    });

    // Period button selection
    const periodButtons = document.querySelectorAll(".period-buttons button");
    periodButtons.forEach((button) => {
        if (button.textContent !== "조회") {
            button.addEventListener("click", function () {
                // Remove active class from all period buttons except 조회
                periodButtons.forEach((btn) => {
                    if (btn.textContent !== "조회") {
                        btn.classList.remove("active");
                    }
                });
                this.classList.add("active");

                // Update date range based on selection
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
                    case "1분기":
                        startDate.setMonth(today.getMonth() - 3);
                        break;
                    case "1년":
                        startDate.setFullYear(today.getFullYear() - 1);
                        break;
                    case "11월":
                        startDate = new Date(today.getFullYear(), 10, 1); // November
                        break;
                }

                const dateInputs = document.querySelectorAll(".date-input");
                dateInputs[0].value = formatDate(startDate);
                dateInputs[1].value = formatDate(today);
            });
        }
    });

    // Search button click
    const searchButton = document.querySelector(
        ".period-buttons button:last-child"
    );
    searchButton.addEventListener("click", function () {
        // Add your search logic here
        console.log("Searching...");
    });

    // Excel download buttons
    const excelButtons = document.querySelectorAll(".excel-btn");
    excelButtons.forEach((button) => {
        button.addEventListener("click", function () {
            // Add your excel download logic here
            console.log("Downloading excel...");
        });
    });
});

// Helper function to format date as YYYY-MM-DD
function formatDate(date) {
    return date.toISOString().split("T")[0];
}
