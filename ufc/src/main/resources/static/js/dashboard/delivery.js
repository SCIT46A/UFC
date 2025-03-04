function initDeliveryManagement() {
    console.log("🚚 배송 관리 JS 실행됨");

    // ✅ "전체 선택" 체크박스 기능
    const selectAllCheckbox = document.getElementById("selectAll");
    const deliveryCheckboxes = document.querySelectorAll(".delivery-checkbox");

    if (selectAllCheckbox) {
        selectAllCheckbox.addEventListener("change", function () {
            deliveryCheckboxes.forEach((checkbox) => {
                checkbox.checked = this.checked;
            });
        });

        // 개별 체크박스 클릭 시 "전체 선택" 상태 업데이트
        deliveryCheckboxes.forEach((checkbox) => {
            checkbox.addEventListener("change", function () {
                selectAllCheckbox.checked = [...deliveryCheckboxes].every((cb) => cb.checked);
            });
        });
    }

    // ✅ 기간 선택 버튼 기능
    const periodButtons = document.querySelectorAll(".period-buttons button");
    periodButtons.forEach((button) => {
        button.addEventListener("click", () => {
            periodButtons.forEach((btn) => btn.classList.remove("active"));
            button.classList.add("active");
            updateDateRange(button.textContent);
        });
    });

    function updateDateRange(period) {
        const endDate = new Date();
        let startDate = new Date();

        switch (period) {
            case "오늘":
                break;
            case "1주일":
                startDate.setDate(endDate.getDate() - 7);
                break;
            case "1개월":
                startDate.setMonth(endDate.getMonth() - 1);
                break;
            case "3개월":
                startDate.setMonth(endDate.getMonth() - 3);
                break;
        }

        document.querySelectorAll("input[type='date']")[0].value = formatDate(startDate);
        document.querySelectorAll("input[type='date']")[1].value = formatDate(endDate);
    }

    function formatDate(date) {
        return date.toISOString().split("T")[0];
    }
}

// 🚀 fragment가 변경될 때마다 JS를 다시 실행하도록 설정
document.addEventListener("reapplyEventListeners", initDeliveryManagement);
