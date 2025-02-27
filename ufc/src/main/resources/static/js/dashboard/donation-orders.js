function initDonationOrders() {
    console.log("🎁 기부 내역 관리 JS 실행됨");

    // 최초 데이터 로드 (백엔드에서 필터링된 데이터 사용)
    loadDonationOrders();

    // "전체 선택" 체크박스 기능
    document.getElementById("selectAll")?.addEventListener("change", (e) => {
        document.querySelectorAll('tbody input[type="checkbox"]').forEach(checkbox => {
            checkbox.checked = e.target.checked;
        });
    });

    // 필터 변경 시 적용
    document.getElementById('status')?.addEventListener("change", applyFilters);
    document.querySelectorAll(".date-range input[type='date']").forEach(input => {
        input.addEventListener("change", applyFilters);
    });

    // 기간 선택 버튼 기능
    document.querySelectorAll(".period-buttons button").forEach((button) => {
        button.addEventListener("click", () => {
            document.querySelectorAll(".period-buttons button").forEach(btn => btn.classList.remove("active"));
            button.classList.add("active");
            updateDateRange(button.textContent);
            applyFilters();
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

        document.querySelectorAll(".date-range input[type='date']")[0].value = formatDate(startDate);
        document.querySelectorAll(".date-range input[type='date']")[1].value = formatDate(endDate);
    }

    function formatDate(date) {
        return date.toISOString().split("T")[0];
    }
}

// 필터 적용 함수
function applyFilters() {
    const status = document.getElementById('status')?.value;
    const startDate = document.querySelector(".date-range input[type='date']")?.value;
    const endDate = document.querySelector(".date-range input[type='date']:nth-of-type(2)")?.value;

    console.log("📊 필터 적용:", { status, startDate, endDate });

    loadDonationOrders({ status, startDate, endDate });
}

// 기부 내역 가져오기
async function loadDonationOrders(filters = {}) {
    try {
        console.log("🚀 기부 주문 데이터 로딩 중...", filters);

        let queryParams = new URLSearchParams(filters).toString();
        let response = await fetch(`/api/creator/dashboard/donation/orders?${queryParams}`);
        if (!response.ok) throw new Error(`HTTP 오류 발생: ${response.status}`);

        let data = await response.json();
        console.log("✅ 기부 주문 데이터 로드 성공:", data);

        if (!data.donations || data.donations.length === 0) {
            console.warn("⚠ 기부 데이터가 비어 있습니다:", data.donations);
        }

        // 기부 상태별 개수 업데이트
        updateDonationCounts(data.donationCounts);

        // 테이블 데이터 업데이트
        renderDonationOrders(data.donations);
    } catch (error) {
        console.error("❌ 기부 주문 데이터 로딩 실패:", error);
    }
}

// 기부 상태별 개수 업데이트
function updateDonationCounts(counts) {
    document.getElementById("pendingDonationCount").textContent = counts.pending || 0;
    document.getElementById("ongoingDonationCount").textContent = counts.ongoing || 0;
    document.getElementById("rejectedDonationCount").textContent = counts.rejected || 0;
    document.getElementById("approvedDonationCount").textContent = counts.approved || 0;
}

async function renderDonationOrders(donations) {
    const tbody = document.querySelector(".donation-table tbody");

    if (!tbody) {
        console.error("❌ tbody 요소를 찾을 수 없습니다. 클래스 확인 필요!");
        return;
    }

    console.log("📌 기부 데이터 렌더링 시작:", donations);
    tbody.innerHTML = ""; // 기존 목록 초기화

    donations.forEach(donation => {
        const row = document.createElement("tr");

        row.innerHTML = `
            <td><input type="checkbox" class="donation-checkbox" value="${donation.donationId}"></td>
            <td>${donation.campaignTitle}</td>
            <td>${new Date(donation.donatedDate).toLocaleDateString()}</td>
            <td>${donation.userName}</td>
            <td>${donation.materialName}</td>
            <td>${donation.quantity}</td>
            <td>${donation.courierName || "없음"}</td>
            <td>${donation.trackingNumber || "미등록"}</td>
            <td><span class="tracking-status">${donation.trackingStatus}</span></td> <!-- ✅ 조회 중 제거 -->
            <td>
                <span class="status-badge ${getStatusClass(donation.status)}">
                    ${getStatusText(donation.status)}
                </span>
            </td>
            <td>
                <button class="btn btn-primary approve-btn">승인</button>
                <button class="btn btn-danger reject-btn">반려</button>
            </td>
        `;

        tbody.appendChild(row);
    });

    console.log("✅ 기부 데이터 렌더링 완료!");
}

// 기부 상태에 따른 클래스 반환
function getStatusClass(status) {
    return status === "PENDING" ? "pending" :
        status === "APPROVED" ? "approved" : "rejected";
}

// 기부 상태에 따른 텍스트 반환
function getStatusText(status) {
    return status === "PENDING" ? "검수 대기" :
        status === "APPROVED" ? "검수 완료" : "반려(불량)";
}

// 이벤트 위임 (성능 최적화)
document.addEventListener("click", async function (event) {
    let target = event.target;

    if (target.matches(".approve-btn")) {
        console.log("🎉 승인 버튼 클릭");
    }
    if (target.matches(".reject-btn")) {
        console.log("❌ 반려 버튼 클릭");
    }
});

// `fragment` 변경 시 JS 다시 실행 (필요함)
document.addEventListener("reapplyEventListeners", initDonationOrders);
