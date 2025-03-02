// ✅ `cachedDeliveries` 전역 변수 선언 (중복 방지)
if (typeof cachedDeliveries === "undefined") {
    var cachedDeliveries = [];
}

// ✅ 리워드 배송 관리 페이지 초기화
function initRewardDeliveryManagement() {
    console.log("🚚 리워드 배송 관리 JS 실행됨");

    // ✅ Fragment 내부 컨테이너 선택 (정확한 클래스 확인)
    const fragmentContainer = document.querySelector(".reward-delivery-container");

    if (fragmentContainer) {
        fragmentContainer.removeEventListener("click", handleShipmentProcess);
        fragmentContainer.addEventListener("click", handleShipmentProcess);
    }

    //initFilters();
    loadRewardDeliveries();
}

// ✅ 리워드 배송 내역 가져오기
async function loadRewardDeliveries(filters = {}, forceReload = false) {
    try {
        if (!forceReload && Object.keys(filters).length === 0 && cachedDeliveries.length > 0) {
            console.log("🔄 캐싱된 데이터 사용 (필터 적용)");
            updateRewardDeliveryCounts(getDeliveryCounts(cachedDeliveries));
            renderRewardDeliveries(cachedDeliveries);
            return;
        }

        console.log("🚀 리워드 배송 데이터 로딩 중...", filters);

        let queryParams = new URLSearchParams(filters).toString();
        let response = await fetch(`/api/seller/dashboard/reward/deliveries?${queryParams}`, {
            method: "GET",
            headers: { "Cache-Control": "no-cache" },
        });
        if (!response.ok) throw new Error(`HTTP 오류 발생: ${response.status}`);

        let data = await response.json();
        console.log("✅ 리워드 배송 데이터 로드 성공:", data);

        if (!data.deliveries || data.deliveries.length === 0) {
            console.warn("⚠ 배송 데이터가 비어 있습니다:", data.deliveries);
        }

        cachedDeliveries = data.deliveries;

        // ✅ 상태 카드 값 업데이트
        updateRewardDeliveryCounts(data.deliveryCounts);

        // ✅ 테이블 데이터 업데이트
        renderRewardDeliveries(cachedDeliveries);
    } catch (error) {
        console.error("❌ 리워드 배송 데이터 로딩 실패:", error);
    }
}

// ✅ 리워드 배송 내역 렌더링
async function renderRewardDeliveries(deliveries) {
    const tbody = document.querySelector("#delivery-table-body");
    if (!tbody) {
        console.error("❌ tbody 요소를 찾을 수 없습니다. 클래스 확인 필요!");
        return;
    }

    tbody.innerHTML = ""; // 기존 목록 초기화

    if (deliveries.length === 0) {
        tbody.innerHTML = `
            <tr>
                <td colspan="12">
                    <div class="empty-message">
                        <svg xmlns="http://www.w3.org/2000/svg" width="24" height="24" viewBox="0 0 24 24"
                            fill="none" stroke="currentColor" stroke-width="2">
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
            <td><input type="checkbox" class="delivery-checkbox" value="${delivery.id}"></td>
            <td>${delivery.campaignTitle || '-'}</td>
            <td>${delivery.donationId || '-'}</td>
            <td>${delivery.donorName || '-'}</td>
            <td>${delivery.donorPhone || '-'}</td>
            <td>${delivery.donorAddress || '-'}</td>
            <td>${delivery.rewardDetails || '-'}</td>
            <td>${delivery.dueDate ? formatDate(delivery.dueDate) : '-'}</td>
            <td>
                <select class="courier-select">
                    <option value="">택배사 선택</option>
                    <option value="cj">CJ대한통운</option>
                    <option value="lotte">롯데택배</option>
                    <option value="hanjin">한진택배</option>
                </select>
            </td>
            <td><input type="text" class="tracking-number-input" placeholder="송장번호 입력"></td>
            <td><button class="btn btn-primary" onclick="processDelivery(${delivery.id})">발송처리</button></td>
            <td>${delivery.shippingStatus || '-'}</td>
        `;
        tbody.appendChild(row);
    });

    console.log("✅ 리워드 배송 데이터 렌더링 완료!");
}

// ✅ 발송 처리
async function processDelivery(deliveryId) {
    const row = document.querySelector(`.delivery-checkbox[value="${deliveryId}"]`)?.closest("tr");
    if (!row) return;

    const courier = row.querySelector(".courier-select").value;
    const trackingNumber = row.querySelector(".tracking-number-input").value.trim();

    if (!courier || !trackingNumber) {
        alert("🚨 택배사와 송장번호를 입력해주세요!");
        return;
    }

    try {
        let response = await fetch(`/api/seller/dashboard/reward/deliveries/${deliveryId}/ship`, {
            method: "POST",
            headers: { "Content-Type": "application/json" },
            body: JSON.stringify({ courier, trackingNumber })
        });

        if (!response.ok) throw new Error(`HTTP 오류 발생: ${response.status}`);

        console.log(`✅ 배송 처리 완료: ${deliveryId}`);

        // UI 업데이트
        row.querySelector(".tracking-number-input").disabled = true;
        row.querySelector(".courier-select").disabled = true;
        row.querySelector(".btn-primary").textContent = "발송 완료";
        row.querySelector(".btn-primary").disabled = true;
    } catch (error) {
        console.error("❌ 배송 처리 실패:", error);
    }
}

// ✅ 상태 카드 값 업데이트
function updateRewardDeliveryCounts(counts) {
    document.getElementById("pendingDeliveryCount").textContent = counts.pending || 0;
    document.getElementById("shippedDeliveryCount").textContent = counts.shipped || 0;
}

// 🚀 fragment가 변경될 때마다 JS를 다시 실행하도록 설정
document.addEventListener("reapplyEventListeners", initRewardDeliveryManagement);
