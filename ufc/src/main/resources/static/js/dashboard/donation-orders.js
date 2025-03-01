// ✅ `cachedDonations` 전역 변수 선언을 중복 방지
if (typeof cachedDonations === "undefined") {
    var cachedDonations = []; // 전역 변수로 설정
}


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

    initFilters();
    // 최초 데이터 로드 (백엔드에서 필터링된 데이터 사용)
    loadDonationOrders();
}

// 기부 내역 가져오기
async function loadDonationOrders(filters = {}, forceReload = false) {
    try {
        // 🔹 forceReload가 false이고, 캐싱된 데이터가 있으면 API 호출 없이 캐싱된 데이터 사용
        if (!forceReload && Object.keys(filters).length === 0 && cachedDonations.length > 0) {
            console.log("🔄 캐싱된 데이터 사용 (필터 적용)");
            updateDonationCounts(getDonationCounts(cachedDonations)); // ✅ 상태 카드 값 업데이트
            renderDonationOrders(cachedDonations);
            return;
        }

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

        // 🔹 처음 데이터 로드 시 `cachedDonations`에 저장
        cachedDonations = data.donations;

        // ✅ 상태 카드 값 업데이트
        updateDonationCounts(data.donationCounts);

        // ✅ 테이블 데이터 업데이트
        renderDonationOrders(cachedDonations);
    } catch (error) {
        console.error("❌ 기부 주문 데이터 로딩 실패:", error);
    }
}

// 기부 내역 렌더링
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


            // 🚀 캐시 데이터(`cachedDonations`)도 변경
            let cachedDonation = cachedDonations.find(donation => donation.donationId == donationId);
            if (cachedDonation) {
                cachedDonation.status = isApproved ? "approved" : "rejected"; // 🔹 캐시 데이터 업데이트
            }

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

// "수정" 버튼을 삭제하고 승인/반려 버튼 복구
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

// resetApprovalStatus(donationId)를 호출하여 승인/반려 버튼 복구
function modifyApprovalClickHandler(event) {
    let target = event.target;

    if (target.matches(".modify-approval-btn")) {
        const donationId = target.dataset.id;
        console.log(`🔄 승인 수정 요청: ${donationId}`);
        resetApprovalStatus(donationId); // "승인" / "반려" 버튼 복구
    }
}

// 기부 상태별 개수 업데이트
function updateDonationCounts(counts) {
    document.getElementById("pendingDonationCount").textContent = counts.pending || 0;
    document.getElementById("processingDonationCount").textContent = counts.processing || 0;
    document.getElementById("rejectedDonationCount").textContent = counts.rejected || 0;
    document.getElementById("approvedDonationCount").textContent = counts.approved || 0;

    // ✅ 전체 개수 업데이트
    document.querySelector(".left-section span").textContent = `목록 (총 ${cachedDonations.length}개)`;
}

function getStatusClass(status) {
    return status === "processing" ? "processing" : // 🔹 추가
        status === "pending" ? "pending" :
            status === "approved" ? "approved" : "rejected";
}

function getStatusText(status) {
    return status === "processing" ? "기부 진행 중" : // 🔹 추가
        status === "pending" ? "검수 대기" :
            status === "approved" ? "승인" : "반려";
}


function initFilters() {
    console.log("🛠 필터 초기화 실행됨");

    // 기존 이벤트 리스너 제거 후 다시 등록
    document.querySelector(".btn-primary")?.removeEventListener("click", applyFilters);
    document.querySelector(".btn-primary")?.addEventListener("click", applyFilters);

    document.querySelector(".btn-secondary")?.removeEventListener("click", resetFilters);
    document.querySelector(".btn-secondary")?.addEventListener("click", resetFilters);

    // 상태 필터 변경 시 자동 적용
    document.querySelector("select.search-input")?.removeEventListener("change", applyFilters);
    document.querySelector("select.search-input")?.addEventListener("change", applyFilters);

    // 날짜 입력 필터 변경 시 자동 적용
    document.querySelectorAll(".date-range input[type='date']").forEach(input => {
        input.removeEventListener("change", applyFilters);
        input.addEventListener("change", applyFilters);
    });

    // 기간 버튼 클릭 시 필터 적용
    document.querySelectorAll(".period-buttons button").forEach(button => {
        button.removeEventListener("click", handlePeriodClick);
        button.addEventListener("click", handlePeriodClick);
    });
}

function handlePeriodClick(event) {
    document.querySelectorAll(".period-buttons button").forEach(btn => btn.classList.remove("active"));
    event.target.classList.add("active");
    updateDateRange(event.target.textContent);
    applyFilters();
}

function applyFilters() {
    if (!cachedDonations || cachedDonations.length === 0) {
        console.warn("⚠️ 캐시된 기부 데이터가 없습니다.");
        return;
    }

    // 캠페인 제목과 기부자 이름 모두 검색하는 하나의 텍스트 입력 필드
    const searchText = document.querySelector(".search-input[type='text']").value.trim().toLowerCase();
    const status = document.querySelector("select.search-input")?.value;
    const startDate = document.querySelector(".date-range input[type='date']:nth-of-type(1)")?.value;
    const endDate = document.querySelector(".date-range input[type='date']:nth-of-type(2)")?.value;

    let filteredDonations = cachedDonations.filter(donation => {
        let matchesSearch = true;
        let matchesStatus = true;
        let matchesDate = true;

        // 검색어가 입력되어 있다면 캠페인 제목 또는 기부자 이름에서 검색어가 포함되어 있는지 확인
        if (searchText) {
            const normalizedSearch = searchText.replace(/\s+/g, "");
            const normalizedTitle = donation.campaignTitle.replace(/\s+/g, "").toLowerCase();
            const normalizedDonor = donation.userName.replace(/\s+/g, "").toLowerCase();
            matchesSearch =
                normalizedTitle.includes(normalizedSearch) ||
                normalizedDonor.includes(normalizedSearch);
        }

        if (status) {
            matchesStatus = donation.status === status;
        }

        if (startDate && endDate) {
            const donationDate = new Date(donation.donatedDate).toISOString().split("T")[0];
            matchesDate = donationDate >= startDate && donationDate <= endDate;
        }

        return matchesSearch && matchesStatus && matchesDate;
    });

    // 현재 선택된 정렬 기준을 적용
    const sortOrder = document.getElementById("sortOrder").value;
    filteredDonations = sortDonations(filteredDonations, sortOrder);

    renderDonationOrders(filteredDonations);
}


function resetFilters() {
    document.querySelector(".search-input[type='text']").value = "";

    const statusSelect = document.querySelector("select.search-input");
    if (statusSelect) {
        statusSelect.value = "";
    }

    document.querySelectorAll(".date-range input[type='date']").forEach(input => input.value = "");

    document.querySelectorAll(".period-buttons button").forEach(button => button.classList.remove("active"));
    document.querySelector(".period-buttons button:first-child").classList.add("active");

    applyFilters(); // 초기화 후 필터 다시 적용
}

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

function sortDonations(donations, sortOrder) {
    const sorted = [...donations]; // 원본 배열을 복사
    switch (sortOrder) {
        case "latest":
            sorted.sort((a, b) => new Date(b.donatedDate) - new Date(a.donatedDate));
            break;
        case "oldest":
            sorted.sort((a, b) => new Date(a.donatedDate) - new Date(b.donatedDate));
            break;
        case "campaignTitleAsc":
            sorted.sort((a, b) => a.campaignTitle.localeCompare(b.campaignTitle));
            break;
        case "campaignTitleDesc":
            sorted.sort((a, b) => b.campaignTitle.localeCompare(a.campaignTitle));
            break;
    }
    return sorted;
}

document.getElementById("sortOrder")?.addEventListener("change", function () {
    applyFilters();
});


// ✅ 상태 카드 값 업데이트를 위한 카운트 계산 함수
function getDonationCounts(donations) {
    return donations.reduce((acc, donation) => {
        acc[donation.status] = (acc[donation.status] || 0) + 1;
        return acc;
    }, { pending: 0, processing: 0, rejected: 0, approved: 0 });
}

// 전체 승인 처리 함수 (선택된 항목들을 승인 처리)
async function bulkApproveSelectedDonations() {
    const checkboxes = document.querySelectorAll('.donation-checkbox:checked');
    if (checkboxes.length === 0) {
        console.warn("승인할 기부 내역이 선택되지 않았습니다.");
        return;
    }
    // 선택된 각 기부 항목에 대해 승인 처리
    for (const checkbox of checkboxes) {
        const donationId = checkbox.value;
        const donation = cachedDonations.find(d => d.donationId == donationId);
        // 기부 상태가 pending인 경우에만 승인 대상
        if (donation && donation.status === "pending") {
            // 배송 상태 확인 (배송완료여야 승인 가능)
            let trackingStatus = donation.trackingStatus ? donation.trackingStatus.trim() : "배송 상태 없음";
            const isDelivered = trackingStatus.replace(/\s+/g, "") === "배송완료";
            if (isDelivered) {
                try {
                    let response = await fetch(`/api/creator/dashboard/donation/orders/${donationId}/approved`, {
                        method: "POST",
                        headers: { "Content-Type": "application/json" },
                        body: JSON.stringify({ donationId, status: "approved" })
                    });
                    if (!response.ok) {
                        console.error(`Donation ${donationId} 승인 실패: HTTP 오류 ${response.status}`);
                        continue;
                    }
                    // 캐시 데이터 업데이트
                    donation.status = "approved";
                    // UI 업데이트 (개별 행에 대해 승인 처리된 상태 적용)
                    updateRowAfterInspection(donationId, true);
                } catch (error) {
                    console.error(`Donation ${donationId} 승인 실패:`, error);
                }
            }
        }
    }
    // 승인 처리가 끝난 후 전체 체크박스 및 개별 체크박스 모두 해제
    document.getElementById("selectAll").checked = false;
    document.querySelectorAll(".donation-checkbox").forEach(chk => chk.checked = false);
    console.log("전체 승인 처리 완료.");
}

// "전체 승인" 버튼 클릭 시 bulkApproveSelectedDonations 함수 호출
document.querySelector(".table-header > button.btn-secondary")?.addEventListener("click", bulkApproveSelectedDonations);

// 전체 체크박스(select all) 변경 시, 모든 행을 체크 처리하고, 
// 검수대기 상태(pending)인 항목들을 자동으로 승인 처리
document.getElementById("selectAll")?.addEventListener("change", function (event) {
    const isChecked = event.target.checked;
    const checkboxes = document.querySelectorAll(".donation-checkbox");
    checkboxes.forEach(chk => {
        chk.checked = isChecked;
    });
});

// 데이터 갱신 버튼 클릭 시, 데이터 갱신
document.querySelector(".data-refresh-btn")?.addEventListener("click", function () {
    // forceReload 매개변수를 true로 하여 새 데이터를 불러옵니다.
    loadDonationOrders({}, true);
});

// ✅ 프래그먼트 변경 후 `cachedDonations`를 이용해 데이터 갱신
document.addEventListener("reapplyEventListeners", () => {
    console.log("🔄 프래그먼트 변경 감지: 상태 카드 값 갱신");

    initFilters();

    // ✅ 기존 데이터로 상태 카드 값 다시 적용
    if (cachedDonations.length > 0) {
        updateDonationCounts(getDonationCounts(cachedDonations));
        renderDonationOrders(cachedDonations);
    } else {
        // ✅ 캐싱된 데이터가 없으면 다시 불러오기
        loadDonationOrders({}, true);
    }
});