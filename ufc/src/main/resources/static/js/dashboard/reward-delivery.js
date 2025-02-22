function initRewardDeliveryManagement() {
    console.log("🚚 배송 관리 JS 실행됨");

    // ✅ 배송 데이터 불러오기
    fetchDeliveryData();

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

// ✅ 배송 데이터 가져오기 함수 추가
function fetchDeliveryData() {
    fetch("/api/delivery/reward/list") // 백엔드 API 주소
        .then(response => response.json())
        .then(data => {
            console.log("✅ [DATA FETCHED] 배송 데이터 불러오기 성공!", data);
            updateDeliveryTable(data); // 테이블 업데이트
        })
        .catch(error => {
            console.error("❌ [ERROR] 배송 데이터 불러오기 실패", error);
        });
}

// ✅ 테이블 업데이트 함수 추가
function updateDeliveryTable(deliveries) {
    const tableBody = document.querySelector(".table-container tbody");
    tableBody.innerHTML = ""; // 기존 데이터 초기화

    if (deliveries.length === 0) {
        tableBody.innerHTML = `
            <tr>
                <td colspan="10">
                    <div class="empty-message">
                        <svg xmlns="http://www.w3.org/2000/svg" width="24" height="24" viewBox="0 0 24 24"
                            fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round"
                            stroke-linejoin="round">
                            <circle cx="12" cy="12" r="10" />
                            <line x1="12" y1="8" x2="12" y2="12" />
                            <line x1="12" y1="16" x2="12.01" y2="16" />
                        </svg>
                        데이터가 존재하지 않습니다.
                    </div>
                </td>
            </tr>
        `;
        return;
    }

    deliveries.forEach(delivery => {
        const row = document.createElement("tr");
        row.innerHTML = `
            <td><input type="checkbox" class="delivery-checkbox" value="${delivery.r_delivery_id}"></td>
            <td>${delivery.sellerId || '-'}</td>
            <td>${delivery.orderNumber || '-'}</td>
            <td>${delivery.status || '-'}</td>
            <td>${delivery.productId || '-'}</td>
            <td>${delivery.productName || '-'}</td>
            <td>${delivery.dueDate ? formatDate(delivery.dueDate) : '-'}</td>
            <td>${delivery.deliveryCompany || '-'}</td>
            <td>${delivery.invoice || '-'}</td>
            <td>${delivery.deliveryPolicy || '-'}</td>
        `;
        tableBody.appendChild(row);
    });
}

// 🚀 fragment가 변경될 때마다 JS를 다시 실행하도록 설정
document.addEventListener("reapplyEventListeners", initRewardDeliveryManagement);
