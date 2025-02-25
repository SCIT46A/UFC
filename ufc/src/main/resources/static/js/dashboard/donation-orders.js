function initDonationOrders() {
    console.log("🎁 기부 내역 관리 JS 실행됨");

    //✅ `hidden input`에서 `creatorId` 가져오기
    let hiddenInput = document.getElementById("creatorId");
    let creatorId = hiddenInput ? hiddenInput.value : null;

    console.log("📌 creatorId:", creatorId);

    if (!creatorId) {
        console.error("❌ creatorId가 없습니다. HTML에서 올바르게 설정되었는지 확인하세요.");
        return;
    }

    loadDonationOrders(creatorId);

    // ✅ "전체 선택" 체크박스 기능
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

    // // ✅ 필터 변경 시 적용
    // document.getElementById('status')?.addEventListener("change", applyFilters);
    // document.querySelectorAll(".date-range input[type='date']").forEach(input => {
    //     input.addEventListener("change", applyFilters);
    // });

    // document.querySelectorAll(".period-buttons button").forEach(button => {
    //     button.addEventListener("click", function () {
    //         document.querySelectorAll(".period-buttons button").forEach(btn => btn.classList.remove("active"));
    //         this.classList.add("active");
    //         updateDateRange(this.textContent);
    //     });
    // });
}

async function loadDonationOrders(creatorId, filters = {}) {
    try {
        console.log("🚀 기부 주문 데이터 로딩 중...", filters);

        // 필터 값을 쿼리 파라미터로 전달
        let queryParams = new URLSearchParams(filters).toString();
        let response = await fetch(`/api/creator/dashboard/${creatorId}/donation-orders?${queryParams}`);
        if (!response.ok) throw new Error(`HTTP 오류 발생: ${response.status}`);

        let data = await response.json();
        console.log("✅ 기부 주문 데이터 로드 성공:", data);

        // 🛠 데이터가 비어 있는지 확인
        if (!data.donations || data.donations.length === 0) {
            console.warn("⚠ 기부 데이터가 비어 있습니다:", data.donations);
        }

        updateDonationCounts(data.donationCounts);
        renderDonationOrders(data.donations);
    } catch (error) {
        console.error("❌ 기부 주문 데이터 로딩 실패:", error);
    }
}


// ✅ 필터 적용 함수
function applyFilters() {
    const status = document.getElementById('status')?.value;
    const startDate = document.querySelector(".date-range input[type='date']")?.value;
    const endDate = document.querySelector(".date-range input[type='date']:nth-of-type(2)")?.value;

    console.log("📊 필터 적용:", { status, startDate, endDate });

    // AJAX를 통해 필터링된 데이터를 다시 불러올 수 있음
    let creatorId = document.body.dataset.creatorId;
    if (creatorId) {
        loadDonationOrders(creatorId, { status, startDate, endDate });
    }
}


// ✅ 기부 상태별 개수 업데이트
function updateDonationCounts(counts) {
    document.getElementById("pendingDonationCount").textContent = counts.pending || 0;
    document.getElementById("ongoingDonationCount").textContent = counts.ongoing || 0;
    document.getElementById("rejectedDonationCount").textContent = counts.rejected || 0;
    document.getElementById("approvedDonationCount").textContent = counts.approved || 0;
}

function renderDonationOrders(donations) {
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
            <td>${donation.campaign.title}</td>
            <td>${new Date(donation.donatedDate).toLocaleDateString()}</td>
            <td>${donation.user.userName}</td>
            <td>${donation.material.name}</td>
            <td>${donation.quantity}</td>
            <td>${donation.courierName || "없음"}</td>
            <td>${donation.trackingNumber || "미등록"}</td>
            <td>
                <button data-invoice="${donation.invoice}" class="track-btn">배송조회</button>
            </td>
            <td><span id="tracking-status-${donation.trackingNumber}" class="tracking-status">조회 대기 중...</span></td>
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


// ✅ 기부 상태에 따른 클래스 반환
function getStatusClass(status) {
    return status === "PENDING" ? "pending" :
        status === "APPROVED" ? "approved" : "rejected";
}

// ✅ 기부 상태에 따른 텍스트 반환
function getStatusText(status) {
    return status === "PENDING" ? "검수 대기" :
        status === "APPROVED" ? "검수 완료" : "반려(불량)";
}

// ✅ 배송 조회 기능 (개별 송장 조회)
async function trackDelivery(invoice) {
    try {
        let encodedInvoice = encodeURIComponent(invoice);
        let response = await fetch(`/api/delivery/${encodedInvoice}`);
        if (!response.ok) throw new Error(`HTTP 오류 발생: ${response.status}`);

        let data = await response.json();
        if (!data || !data.trackingData) {
            console.error("❌ 배송 데이터 없음:", data);
            return;
        }

        let trackingData = JSON.parse(data.trackingData);
        let trackingNumber = invoice.split("#")[1];
        let statusElement = document.getElementById(`tracking-status-${trackingNumber}`);

        if (!statusElement) {
            console.error(`❌ ID 'tracking-status-${trackingNumber}' 를 가진 요소가 없습니다.`);
            return;
        }

        let lastEvent = trackingData.data?.track?.lastEvent;
        statusElement.innerHTML = `<b>${lastEvent.status.name}</b> - ${lastEvent.description}`;
    } catch (error) {
        console.error("❌ 배송 조회 중 오류 발생:", error);
    }
}

// ✅ 이벤트 위임 (성능 최적화)
document.addEventListener("click", async function (event) {
    let target = event.target;

    if (target.matches(".track-btn")) {
        trackDelivery(target.getAttribute("data-invoice"));
    }
    if (target.matches(".approve-btn")) {
        console.log("🎉 승인 버튼 클릭");
    }
    if (target.matches(".reject-btn")) {
        console.log("❌ 반려 버튼 클릭");
    }
});

// 🚀 `fragment` 변경 시 JS 다시 실행 (필요함)
document.addEventListener("reapplyEventListeners", initDonationOrders);
