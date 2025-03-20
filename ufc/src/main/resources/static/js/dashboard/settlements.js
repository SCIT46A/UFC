function initSettlementManagement() {
    console.log("💰 정산 관리 JS 실행됨");

    function waitForElement(selector, callback, timeout = 5000) {
        const startTime = Date.now();

        function check() {
            const element = document.querySelector(selector);
            if (element) {
                callback(element);
                return;
            }

            if (Date.now() - startTime >= timeout) {
                console.error(`🚨 waitForElement: ${selector}가 ${timeout / 1000}초 내에 생성되지 않음`);
                return;
            }

            requestAnimationFrame(check);
        }

        check();
    }

    document.querySelectorAll(".period-buttons button").forEach(button => {
        button.addEventListener("click", function () {
            document.querySelectorAll(".period-buttons button").forEach(btn => btn.classList.remove("active"));
            this.classList.add("active");

            const period = this.textContent.trim();
            updateDateRange(period);
            loadSettlementList();
        });
    });

    document.querySelector(".search-actions .btn-primary").addEventListener("click", function () {
        loadSettlementList();
    });

    document.querySelector(".search-actions .btn-secondary").addEventListener("click", function () {
        document.getElementById("status").value = "all";
        document.querySelectorAll(".date-range input").forEach(input => input.value = "");

        // ✅ 활성화된 기간 버튼도 초기화
        document.querySelectorAll(".period-buttons button").forEach(button => button.classList.remove("active"));
        document.querySelector(".period-buttons button:first-child").classList.add("active"); // '오늘' 버튼 활성화

        // ✅ 초기화 후 다시 정산 목록 불러오기
        loadSettlementList();
    });


    function downloadExcel() {
        const status = document.getElementById("status")?.value || "all";
        const startDateInput = document.querySelector(".date-range input[type='date']");
        const endDateInput = document.querySelector(".date-range input[type='date']:nth-of-type(2)");

        let startDate = startDateInput?.value;
        let endDate = endDateInput?.value;

        let queryParams = new URLSearchParams();
        if (status !== "all") queryParams.append("status", status);
        if (startDate) queryParams.append("startDate", startDate);
        if (endDate) queryParams.append("endDate", endDate);

        window.location.href = `/api/creator/dashboard/settlements/download/excel?${queryParams.toString()}`;
    }


    /**
     * ✅ 정산 목록 불러오기
     */
    function loadSettlementList() {
        const status = document.getElementById("status")?.value || "all";
        const startDateInput = document.querySelector(".date-range input[type='date']");
        const endDateInput = document.querySelector(".date-range input[type='date']:nth-of-type(2)");

        let startDate = startDateInput?.value;
        let endDate = endDateInput?.value;

        // 🚀 선택된 날짜가 없으면 필터에서 제외
        let queryParams = new URLSearchParams();
        if (status !== "all") queryParams.append("status", status);
        if (startDate) queryParams.append("startDate", startDate);
        if (endDate) queryParams.append("endDate", endDate);

        console.log("📢 [JavaScript] API 요청 -", queryParams.toString());

        fetch(`/api/creator/dashboard/settlements?${queryParams.toString()}`)
            .then(response => response.json())
            .then(data => {
                if (!Array.isArray(data)) {
                    console.error("🚨 API 응답이 배열이 아닙니다!", data);
                    return;
                }
                renderSettlementTable(data);
            })
            .catch(error => console.error("❌ 정산 목록 로드 오류:", error));
    }


    /**
     * ✅ 정산 목록 테이블 렌더링
     */
    function renderSettlementTable(settlements) {
        const tbody = document.querySelector(".table-container tbody");
        tbody.innerHTML = ""; // 기존 내용 삭제

        // ✅ 전체 개수 업데이트
        document.querySelector(".table-header .left-section span").textContent = `목록 (총 ${settlements.length}개)`;

        if (settlements.length === 0) {
            tbody.innerHTML = `
            <tr>
                <td colspan="6">
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

        settlements.forEach(settlement => {
            const row = document.createElement("tr");
            row.dataset.payId = settlement.payId;
            row.innerHTML = `
            <td><input type="checkbox" class="order-checkbox" value="${settlement.payId}"></td>
            <td>${settlement.payId}</td>
            <td>${new Date(settlement.purchasedDate).toLocaleDateString()}</td>
            <td>${formatCurrency(settlement.settlementAmount)}</td>
            <td>${getSettlementBadge(settlement.settlementStatus)}</td>
            <td>
                <button class="btn btn-primary view-details-btn" data-pay-id="${settlement.payId}">상세 보기</button>
            </td>
        `;
            tbody.appendChild(row);
        });
    }


    /**
     * ✅ 정산 상태에 따라 뱃지 스타일 반환
     */
    function getSettlementBadge(status) {
        const statusMap = {
            "PENDING": '<span class="badge pending">정산 대기</span>',
            "SCHEDULED": '<span class="badge scheduled">정산 예정</span>',
            "COMPLETED": '<span class="badge completed">정산 완료</span>'
        };
        return statusMap[status] || '<span class="badge">알 수 없음</span>';
    }

    /**
     * ✅ 통화 포맷 (예: 50,000원)
     */
    function formatCurrency(amount) {
        return new Intl.NumberFormat("ko-KR").format(amount) + "원";
    }

    /**
     * ✅ 정산 상세 정보 가져오기 (전역 등록)
     */
    window.viewDetails = function (payId) {
        console.log("📢 정산 상세 조회 - ID:", payId);

        fetch(`/api/creator/dashboard/settlements/details/${payId}`)
            .then(response => response.json())
            .then(settlement => {
                waitForElement("#settlementDetails", (detailsContainer) => {
                    detailsContainer.innerHTML = `
                    <p><strong>결제 ID:</strong> ${settlement.payId}</p>
                    <p><strong>결제일:</strong> ${new Date(settlement.purchasedDate).toLocaleDateString()}</p>
                    <p><strong>정산 예정일:</strong> ${new Date(settlement.scheduledSettlementDate).toLocaleDateString()}</p>
                    <p><strong>총 금액:</strong> ${formatCurrency(settlement.totalAmount)}</p>
                    <p><strong>수수료:</strong> ${formatCurrency(settlement.feeAmount)}</p>
                    <p><strong>정산 금액:</strong> ${formatCurrency(settlement.settlementAmount)}</p>
                    <p><strong>상태:</strong> ${getSettlementBadge(settlement.settlementStatus)}</p>
                `;

                    // ✅ 모달이 존재하는지 확인 후 열기
                    const modal = document.getElementById("settlementModal");
                    if (modal) {
                        modal.style.display = "flex";
                    } else {
                        console.error("🚨 `settlementModal` 요소를 찾을 수 없음");
                    }
                });
            })
            .catch(error => console.error("❌ 정산 상세 정보 로드 오류:", error));
    };


    /**
     * ✅ 날짜 선택 기능 (오늘, 1주일, 1개월, 3개월)
     */
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

        document.querySelector(".date-range input:nth-child(1)").value = formatDate(startDate);
        document.querySelector(".date-range input:nth-child(2)").value = formatDate(endDate);
    }

    function formatDate(date) {
        return date.toISOString().split("T")[0];
    }

    /**
     * ✅ 이벤트 위임 (버블링 방지 + 상세보기 버튼 클릭 시 실행)
     */
    document.querySelector(".table-container tbody").addEventListener("click", function (event) {
        if (event.target.classList.contains("view-details-btn")) {
            event.stopPropagation();
            const payId = event.target.dataset.payId;
            viewDetails(payId);
        }
    });

    /**
     * ✅ 모달 닫기
     */
    window.closeModal = function () {
        const modal = document.getElementById("settlementModal");
        if (modal) {
            modal.style.display = "none";
        } else {
            console.warn("🚨 `settlementModal`이 존재하지 않아 닫을 수 없음.");
        }
    };


    /**
     * ✅ fragment 변경 시 이벤트 핸들러 유지
     */
    document.removeEventListener("reapplyEventListeners", initSettlementManagement);
    document.addEventListener("reapplyEventListeners", () => {
        console.log("🔄 Fragment 변경 감지됨 - `initSettlementManagement` 다시 실행");
        initSettlementManagement();
    });


    // ✅ 페이지 로드 시 정산 목록 불러오기
    loadSettlementList();
}

// ✅ 페이지가 로드될 때 실행
document.addEventListener("DOMContentLoaded", initSettlementManagement);
