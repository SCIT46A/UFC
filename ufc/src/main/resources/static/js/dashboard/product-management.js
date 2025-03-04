function initProductManagement() {
    console.log("📦 상품 관리 JS 실행됨");

    // ✅ 상태 카드 선택 기능
    document.querySelectorAll(".status-card").forEach(card => {
        card.addEventListener("click", () => {
            document.querySelectorAll(".status-card").forEach(c => c.classList.remove("active"));
            card.classList.add("active");
            console.log(`📊 선택된 상태: ${card.textContent.trim()}`);
        });
    });

    // ✅ "전체 선택" 체크박스 기능 (체크박스가 있는지 확인 후 실행)
    const selectAllCheckbox = document.getElementById("selectAll");
    if (selectAllCheckbox) {
        selectAllCheckbox.addEventListener("change", (e) => {
            document.querySelectorAll('tbody input[type="checkbox"]').forEach(checkbox => {
                checkbox.checked = e.target.checked;
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

    // ✅ 날짜 범위 업데이트
    window.updateDateRange = function (period) {
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
    };

    window.formatDate = function (date) {
        return date.toISOString().split("T")[0];
    };

    // ✅ 상품 수정 기능 (예제 로직)
    window.editProduct = function (id) {
        console.log("✏️ 상품 수정:", id);
    };

    // ✅ 상품 삭제 기능 (예제 로직)
    window.deleteProduct = function (id) {
        if (confirm("정말로 이 상품을 삭제하시겠습니까?")) {
            console.log("🗑️ 상품 삭제:", id);
        }
    };
}

// 🚀 fragment가 변경될 때마다 JS를 다시 실행하도록 설정
document.addEventListener("reapplyEventListeners", initProductManagement);
