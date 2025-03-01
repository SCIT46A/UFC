function initDonationOrders() {
    console.log("🎁 기부 내역 관리 JS 실행됨");

    // Fragment 내부 컨테이너 선택 (정확한 클래스 확인)
    const fragmentContainer = document.querySelector(".donation-orders-container");

    if (fragmentContainer) {
        fragmentContainer.removeEventListener("click", handleApprovalClick);
        fragmentContainer.addEventListener("click", handleApprovalClick);
        fragmentContainer.removeEventListener("click", modifyApprovalClickHandler);
        fragmentContainer.addEventListener("click", modifyApprovalClickHandler);
    }

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
        let response = await fetch(`/api/creator/dashboard/donation/orders?${queryParams}`, {
            method: "GET",
            headers: { "Cache-Control": "no-cache" },
        });
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
    document.getElementById("processingDonationCount").textContent = counts.processing || 0;
    document.getElementById("rejectedDonationCount").textContent = counts.rejected || 0;
    document.getElementById("approvedDonationCount").textContent = counts.approved || 0;
}


async function renderDonationOrders(donations) {
    const tbody = document.querySelector(".donation-table tbody");
    if (!tbody) {
        console.error("❌ tbody 요소를 찾을 수 없습니다. 클래스 확인 필요!");
        return;
    }

    tbody.innerHTML = ""; // 기존 목록 초기화

    donations.forEach(donation => {
        const row = document.createElement("tr");

        // 🚨 trackingStatus가 undefined일 경우 대비
        const trackingStatus = donation.trackingStatus ? donation.trackingStatus.trim() : "배송 상태 없음";

        // 🚨 정확한 비교
        const isDelivered = trackingStatus.replace(/\s+/g, "") === "배송완료";
        const isProcessing = donation.status === "processing";
        const isPending = donation.status === "pending";
        const isApproved = donation.status === "approved";
        const isRejected = donation.status === "rejected";


        // 🚀 검수 처리 UI 결정 (배송 완료가 아니면 "-" 표시)
        let inspectionCellContent = "-";

        if (isRejected || isApproved) {
            inspectionCellContent = `
        <button class="btn btn-secondary modify-approval-btn" data-id="${donation.donationId}">수정</button>
        `;
        } else if (isDelivered && isPending) {
            inspectionCellContent = `
        <button class="btn btn-primary approve-btn" data-id="${donation.donationId}">승인</button>
        <button class="btn btn-danger reject-btn" data-id="${donation.donationId}">반려</button>
        `;
        }


        row.innerHTML = `
            <td><input type="checkbox" class="donation-checkbox" value="${donation.donationId}"></td>
            <td>${donation.campaignTitle}</td>
            <td>${new Date(donation.donatedDate).toLocaleDateString()}</td>
            <td>${donation.userName}</td>
            <td>${donation.materialName}</td>
            <td>${donation.quantity}</td>
            <td>${donation.courierName || "없음"}</td>
            <td>${donation.trackingNumber || "미등록"}</td>
            <td><span class="tracking-status">${trackingStatus}</span></td>
            <td>
                <span class="status-badge ${getStatusClass(donation.status)}">
                    ${getStatusText(donation.status)}
                </span>
            </td>
            <td>${inspectionCellContent}</td>  <!-- 🚀 검수 처리 칸 -->
        `;

        tbody.appendChild(row);
    });

    console.log("✅ 기부 데이터 렌더링 완료!");
}




function getStatusClass(status) {
    return status === "pending" ? "pending" :
        status === "approved" ? "approved" : "rejected";
}
function getStatusText(status) {
    return status === "pending" ? "검수 대기" :
        status === "approved" ? "승인" : "반려";
}


async function updateRowAfterInspection(donationId, isApproved) {
    const row = document.querySelector(`.donation-checkbox[value="${donationId}"]`)?.closest("tr");
    if (!row) return;

    // 🚨 승인/반려 버튼 제거
    row.querySelector(".approve-btn")?.remove();
    row.querySelector(".reject-btn")?.remove();

    // 🚀 검수 상태 컬럼 업데이트
    const statusBadge = row.querySelector(".status-badge");
    if (statusBadge) {
        statusBadge.classList.remove("pending", "approved", "rejected");
        statusBadge.classList.add(isApproved ? "approved" : "rejected");
        statusBadge.textContent = isApproved ? "승인" : "반려";
    }

    // 🚀 검수 처리 버튼을 "수정" 버튼으로 변경
    const inspectionStatusCell = row.querySelector("td:last-child");
    inspectionStatusCell.innerHTML = `
        <button class="btn btn-secondary modify-approval-btn" data-id="${donationId}">수정</button>
    `;
    console.log("✅ 검수 상태 업데이트 완료 (리스트 다시 로드 안함):", donationId);

    // 🚀 최신 기부 상태 개수 가져와서 카드 업데이트
    try {
        let response = await fetch(`/api/creator/dashboard/donation/orders/counts`);
        if (!response.ok) throw new Error(`HTTP 오류 발생: ${response.status}`);

        let data = await response.json();
        console.log("📊 최신 기부 상태 개수 데이터:", data);

        if (!data || !data.donationCounts) {
            console.warn("⚠ 서버 응답에 donationCounts가 없습니다. 기본값(0)으로 설정합니다.");
            updateDonationCounts({ processing: 0, pending: 0, rejected: 0, approved: 0 });
        } else {
            updateDonationCounts(data.donationCounts);
        }
    } catch (error) {
        console.error("❌ 기부 상태 개수 업데이트 실패:", error);
    }
}

async function handleApprovalClick(event) {
    let target = event.target;

    if (target.matches(".approve-btn") || target.matches(".reject-btn")) {
        const donationId = target.dataset.id;
        const isApproved = target.matches(".approve-btn");
        const action = isApproved ? "approved" : "rejected";

        console.log("📌 승인/반려 버튼 클릭됨, 재렌더링 여부 확인"); // 추가

        // 🚀 중복 클릭 방지
        target.disabled = true;

        try {
            let response = await fetch(`/api/creator/dashboard/donation/orders/${donationId}/${action}`, {
                method: "POST",
                headers: { "Content-Type": "application/json" },
                body: JSON.stringify({ donationId, status: action }) // ← 여기에 상태값을 전달
            });

            if (!response.ok) throw new Error(`HTTP 오류 발생: ${response.status}`);

            console.log(`✅ 기부 ${isApproved ? "승인" : "반려"} 성공:`, donationId);

            // 🚨 백엔드에서 updateDonationStatus 실행되었는지 확인
            let responseData = await response.json();
            console.log("📌 서버 응답 데이터:", responseData);

            // UI 업데이트
            updateRowAfterInspection(donationId, isApproved);
        } catch (error) {
            console.error(`❌ 기부 ${isApproved ? "승인" : "반려"} 실패:`, error);
            target.disabled = false; // 🚨 실패하면 다시 활성화
        }
    }
}


function resetApprovalStatus(donationId) {
    const row = document.querySelector(`.donation-checkbox[value="${donationId}"]`)?.closest("tr");
    if (!row) return;

    row.querySelector(".modify-approval-btn")?.remove(); // 수정 버튼 삭제

    const inspectionStatusCell = row.querySelector("td:last-child");
    inspectionStatusCell.innerHTML = `
        <button class="btn btn-primary approve-btn" data-id="${donationId}">승인</button>
        <button class="btn btn-danger reject-btn" data-id="${donationId}">반려</button>
    `;

    console.log(`🔄 승인/반려 버튼 복구 완료: ${donationId}`);
}


function modifyApprovalClickHandler(event) {
    let target = event.target;

    if (target.matches(".modify-approval-btn")) {
        const donationId = target.dataset.id;
        console.log(`🔄 승인 수정 요청: ${donationId}`);
        resetApprovalStatus(donationId); // "승인" / "반려" 버튼 복구
    }
}

// ✅ 프래그먼트 변경 감지 시 이벤트 리스너 재적용
document.addEventListener("reapplyEventListeners", initDonationOrders);