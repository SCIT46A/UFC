document.addEventListener('DOMContentLoaded', function () {
    // Period button handling
    const periodButtons = document.querySelectorAll('.period-buttons button');
    periodButtons.forEach(button => {
        button.addEventListener('click', () => {
            periodButtons.forEach(btn => btn.classList.remove('active'));
            button.classList.add('active');
            updateDateRange(button.textContent);
        });
    });


    // Handle "Select All" checkbox in invoice table
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

    function updateDateRange(period) {
        const endDate = new Date();
        let startDate = new Date();

        switch (period) {
            case '오늘':
                break;
            case '1주일':
                startDate.setDate(endDate.getDate() - 7);
                break;
            case '1개월':
                startDate.setMonth(endDate.getMonth() - 1);
                break;
            case '3개월':
                startDate.setMonth(endDate.getMonth() - 3);
                break;
        }

        document.querySelectorAll('input[type="date"]')[0].value = formatDate(startDate);
        document.querySelectorAll('input[type="date"]')[1].value = formatDate(endDate);
    }

    function formatDate(date) {
        return date.toISOString().split('T')[0];
    }
});