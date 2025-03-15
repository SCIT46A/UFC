// ✅ `cachedOrders` 전역 변수 선언 (중복 방지)
if (typeof cachedOrders === "undefined") {
    var cachedOrders = [];
}

// ✅ 주문/발송 관리 페이지 초기화
function initProductOrders() {
    console.log("📦 주문/발송 관리 JS 실행됨");

    // ✅ 기존 데이터가 있으면 재렌더링, 없으면 다시 불러오기
    if (cachedOrders.length > 0) {
        updateOrderCounts(getOrderCounts(cachedOrders));
        renderOrders(cachedOrders);
    } else {
        loadOrders();
    }

    // 🔹 필터 적용
    document.getElementById('status')?.addEventListener("change", applyFilters);
    document.querySelector(".date-range input[type='date']")?.addEventListener("change", applyFilters);
    document.querySelector(".date-range input[type='date']:nth-of-type(2)")?.addEventListener("change", applyFilters);

    document.querySelectorAll(".period-buttons button").forEach(button => {
        button.addEventListener("click", function () {
            document.querySelectorAll(".period-buttons button").forEach(btn => btn.classList.remove("active"));
            this.classList.add("active");
            updateDateRange(this.textContent);
        });
    });
}

// ✅ 주문 데이터 로드
async function loadOrders(filters = {}, forceReload = false) {
    try {
        if (!forceReload && Object.keys(filters).length === 0 && cachedOrders.length > 0) {
            console.log("🔄 캐싱된 데이터 사용");
            updateOrderCounts(getOrderCounts(cachedOrders));
            renderOrders(cachedOrders);
            return;
        }

        console.log("🚀 주문 데이터 로딩 중...", filters);
        let queryParams = new URLSearchParams(filters).toString();
        let response = await fetch(`/api/creator/dashboard/products/orders?${queryParams}`, {
            method: "GET",
            headers: { "Cache-Control": "no-cache" },
        });

        if (!response.ok) throw new Error(`HTTP 오류 발생: ${response.status}`);

        let orders = await response.json();
        console.log("✅ 주문 데이터 로드 성공:", orders);

        cachedOrders = orders.orders || [];

        updateOrderCounts(orders.orderCounts);
        renderOrders(cachedOrders);
    } catch (error) {
        console.error("❌ 주문 데이터 로딩 실패:", error);
    }
}

// ✅ 주문 테이블 렌더링
function renderOrders(orders) {
    const tbody = document.querySelector("#order-table-body");
    if (!tbody) {
        console.error("❌ tbody 요소를 찾을 수 없습니다.");
        return;
    }

    tbody.innerHTML = "";

    if (orders.length === 0) {
        tbody.innerHTML = `
            <tr>
                <td colspan="12">
                    <div class="empty-message">
                        <svg xmlns="http://www.w3.org/2000/svg" width="24" height="24" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
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

    for (const order of orders) {
        const row = document.createElement("tr");

        // ✅ 백엔드 데이터 필드와 일치하도록 수정
        let payId = order.payId;
        let productId = order.productId;
        let purchasedDate = order.purchasedDate ? formatDateTime(order.purchasedDate) : "-";
        let buyerName = order.buyerName || "-";
        let buyerPhone = order.buyerPhone || "-";
        let buyerAddress = order.buyerAddress ? order.buyerAddress.replace("#", " ") : "-";
        let formattedPrice = (order.productPrice * order.quantity).toLocaleString();
        let unitPrice = order.productPrice.toLocaleString();
        let productInfo = `상품명  : ${order.itemName || "-"}<br>
                       수량    : ${order.quantity || "0"}개<br>
                       단가    : ${unitPrice}원<br>
                       총액    : ${formattedPrice}원`;
        let paymentStatus = mapPaymentStatus(order.paymentStatus) || "-";
        let deliveryStatus = order.deliveryStatus || "발송 대기중";

        // ✅ `invoice` 값에서 `courierId`와 `trackingNumber`를 분리
        let trackingNumber = order.trackingNumber ? order.trackingNumber.trim() : null;
        let courierId = order.courierId ? order.courierId.trim() : null;
        let courierName = order.courierName ? order.courierName.trim() : "";

        // ✅ 택배사 UI 업데이트 (송장번호가 있으면 span, 없으면 select)
        let courierHtml = trackingNumber
            ? `<span>${courierName}</span>`
            : `
            <select class="courier-select">
                <option value="">택배사 선택</option>
                <option value="kr.cjlogistics" ${courierId === "kr.cjlogistics" ? "selected" : ""}>CJ대한통운</option>
                <option value="kr.epost" ${courierId === "kr.epost" ? "selected" : ""}>우체국택배</option>
                <option value="kr.hanjin" ${courierId === "kr.hanjin" ? "selected" : ""}>한진택배</option>
                <option value="kr.cupost" ${courierId === "kr.cupost" ? "selected" : ""}>CU편의점택배</option>
                <option value="kr.cvsnet" ${courierId === "kr.cvsnet" ? "selected" : ""}>GS Postbox</option>
            </select>
        `;

        // ✅ 송장번호 UI 업데이트 (송장번호가 있으면 span, 없으면 input)
        let trackingNumberHtml = trackingNumber
            ? `<span>${trackingNumber}</span>`
            : `<input type="text" class="tracking-number-input" placeholder="송장번호 입력" value="">`;

        // ✅ 액션 버튼 로직 수정
        let actionButton = "";

        // 1️⃣ 결제 완료 상태일 경우 -> "발주" 버튼 표시
        if (paymentStatus === "결제 완료") {
            actionButton = `<button class="btn btn-primary" onclick="processOrder(${payId})">발주</button>`;
        }
        // 2️⃣ 발주가 완료 상태이고 송장이 없을 경우 -> "송장 등록" 버튼 표시
        else if (paymentStatus === "발주 완료" && !trackingNumber) {
            actionButton = `<button class="btn btn-success" onclick="saveInvoice(${payId})">등록</button>`;
        }
        // 3️⃣ 송장이 있으면 "송장 수정" 버튼 표시 (단, 배송 중/완료이면 수정 불가)
        else if (paymentStatus === "발주 완료" && trackingNumber) {
            actionButton = `<button class="btn btn-secondary" onclick="editInvoice(${payId})">수정</button>`;
        }
        // 5️⃣ 취소된 주문이면 텍스트만 표시
        else if (paymentStatus === "취소 요청") {
            actionButton = `<button class="btn btn-danger" onclick="approveCancel(${payId})">승인</button>`;
        }
        // 6️⃣ 취소된 주문이면 텍스트만 표시
        else if (paymentStatus === "취소 완료") {
            actionButton = `<span class="text-danger">주문 취소</span>`;
        }


        row.innerHTML = `
        <td><input type="checkbox" class="order-checkbox" value="${payId}" data-product-id="${productId}"></td>
        <td>${payId}</td>
        <td>${purchasedDate}</td>
        <td>${productInfo}</td>
        <td>${paymentStatus}</td>
        <td>${buyerName}</td>
        <td>${buyerPhone}</td>
        <td>${buyerAddress}</td>
        <td>${courierHtml}</td>
        <td>${trackingNumberHtml}</td>
        <td>${actionButton}</td>
        <td>${deliveryStatus}</td>
    `;

        tbody.appendChild(row);
    }

}

async function processOrder(payId) {
    try {
        console.log(`🚀 발주 처리 요청: payId=${payId}`);

        let response = await fetch(`/api/creator/dashboard/products/orders/process/${payId}`, {
            method: "POST",
            headers: { "Content-Type": "application/json" }
        });

        if (!response.ok) throw new Error(`🚨 발주 처리 실패: ${response.status}`);

        console.log(`✅ 발주 처리 성공: payId=${payId}`);

        // ✅ UI 업데이트: "발주" → "송장 등록"
        const row = document.querySelector(`.order-checkbox[value="${payId}"]`)?.closest("tr");
        if (row) {
            row.children[10].innerHTML = `<button class="btn btn-success" onclick="saveInvoice(${payId})">등록</button>`;
            row.children[11].textContent = "발주 완료"; // 상태 업데이트
        }
    } catch (error) {
        console.error("❌ 발주 처리 실패:", error);
    }
}




async function saveInvoice(payId) {
    const row = document.querySelector(`.order-checkbox[value="${payId}"]`).closest("tr");

    if (!row) {
        console.error("❌ 주문 행을 찾을 수 없습니다.");
        return;
    }

    const courier = row.querySelector(".courier-select")?.value;
    const trackingNumber = row.querySelector(".tracking-number-input")?.value.trim();
    const productId = row.querySelector(`.order-checkbox[value="${payId}"]`).dataset.productId; // 🔹 productId 가져오기


    if (!courier || !trackingNumber) {
        alert("🚨 택배사와 송장번호를 입력해주세요!");
        return;
    }

    try {
        // ✅ 1. 🚀 DB에 송장번호 & 택배사 정보 업데이트 (API 변경)

        console.log(`🔍 송장 등록 요청: payId=${payId}, productId=${productId}, courier=${courier}, trackingNumber=${trackingNumber}`);

        let updateResponse = await fetch(`/api/creator/dashboard/products/orders/invoice/${payId}`, {
            method: "POST",
            headers: { "Content-Type": "application/json" },
            body: JSON.stringify({ courier, trackingNumber, productId }) // JSON 데이터 전송
        });

        if (!updateResponse.ok) throw new Error(`🚨 송장번호 업데이트 실패: ${updateResponse.status}`);

        console.log(`✅ DB 송장 정보 업데이트 완료: ${payId}`);

        // ✅ 2. 🚀 배송 상태 업데이트 요청 (API 검토 필요)
        let statusResponse = await fetch(`/api/delivery/status?trackingNumber=${trackingNumber}&courierId=${courier}`, {
            method: "GET",
            headers: { "Content-Type": "application/json" },
        });

        if (!statusResponse.ok) throw new Error(`🚨 배송 상태 조회 실패: ${statusResponse.status}`);

        let deliveryStatus = await statusResponse.json(); // 배송 상태 응답 JSON 처리
        console.log(`🚀 배송 상태 업데이트 완료: ${deliveryStatus.status}`);

        // ✅ 3. 🚀 UI 업데이트 (주문 테이블 컬럼 위치 확인)
        row.children[8].innerHTML = `<span>${getCourierName(courier)}</span>`; // 택배사 업데이트
        row.children[9].innerHTML = `<span>${trackingNumber}</span>`; // 송장번호 업데이트
        row.children[10].innerHTML = `<button class="btn btn-secondary" onclick="editInvoice(${payId})">수정</button>`; // 버튼 변경
        row.children[11].textContent = deliveryStatus.status || "배송 상태 조회 실패"; // 배송 상태 표시

    } catch (error) {
        console.error("❌ 송장 수정 실패:", error);
    }
}


async function updateInvoicesInDB(data) {
    try {
        console.log("📡 송장번호 업데이트 요청 데이터:", data);

        let response = await fetch("/api/creator/dashboard/products/orders/invoice/batch-update", {
            method: "POST",
            headers: { "Content-Type": "application/json" },
            body: JSON.stringify(data)
        });

        if (!response.ok) {
            let errorText = await response.text();
            throw new Error(`HTTP 오류 발생: ${response.status} - ${errorText}`);
        }

        console.log("✅ 송장번호 DB 업데이트 완료!");

    } catch (error) {
        console.error("❌ 송장번호 업데이트 실패:", error);
        alert(`❌ 송장번호 업데이트 실패: ${error.message}`);
    }
}



// ✅ 주문 상태 개수 계산 함수
function getOrderCounts(orders) {
    let counts = {
        completed: 0,
        pending: 0,
        cancelled: 0,
        ordered: 0
    };

    orders.forEach(order => {
        counts[order.status] = (counts[order.status] || 0) + 1;
    });

    return counts;
}

// ✅ 상태 카드 값 업데이트
function updateOrderCounts(counts) {
    const elements = {
        paymentCompleted: document.getElementById("paymentCompletedCount"),
        deliveryReady: document.getElementById("deliveryReadyCount"),
        inDelivery: document.getElementById("inDeliveryCount"),
        deliveryCompleted: document.getElementById("deliveryCompletedCount"),
        cancelRequested: document.getElementById("cancelRequestedCount"),
    };

    for (let key in elements) {
        if (elements[key]) {
            elements[key].textContent = counts[key] || 0;
        } else {
            console.warn(`⚠️ ${key} 요소를 찾을 수 없습니다. HTML 구조를 확인하세요.`);
        }
    }
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
    if (!date || isNaN(date.getTime())) return '-';
    return date.toISOString().split("T")[0]; // YYYY-MM-DD 형식 반환
}

function formatDateTime(date) {
    if (!date || isNaN(new Date(date).getTime())) return '-';

    const d = new Date(date);
    const year = d.getFullYear();
    const month = String(d.getMonth() + 1).padStart(2, "0");
    const day = String(d.getDate()).padStart(2, "0");
    const hours = String(d.getHours()).padStart(2, "0");
    const minutes = String(d.getMinutes()).padStart(2, "0");
    const seconds = String(d.getSeconds()).padStart(2, "0");

    return `${year}-${month}-${day}<br> ${hours}:${minutes}:${seconds}`; // YYYY-MM-DD HH:MM:SS
}

function getCourierCode(courierName) {
    const couriers = {
        "CJ대한통운": "kr.cjlogistics",
        "우체국택배": "kr.epost",
        "한진택배": "kr.hanjin",
        "CU편의점택배": "kr.cupost",
        "GS Postbox": "kr.cvsnet"
    };
    return couriers[courierName] || "";
}

function getCourierName(courierId) {
    const couriers = {
        "kr.cjlogistics": "CJ대한통운",
        "kr.epost": "우체국택배",
        "kr.hanjin": "한진택배",
        "kr.cupost": "CU편의점택배",
        "kr.cvsnet": "GS Postbox"
    };
    return couriers[courierId] || "알 수 없음";
}

function mapPaymentStatus(status) {
    const statusMap = {
        "completed": "결제 완료",
        "pending": "취소 요청",
        "cancelled": "취소 완료",
        "ordered": "발주 완료",
        default: "알 수 없음"
    };
    return statusMap[status] || "알 수 없음";
}


function applyFilters() {
    const status = document.getElementById('status')?.value;
    const startDate = document.querySelector(".date-range input[type='date']")?.value;
    const endDate = document.querySelector(".date-range input[type='date']:nth-of-type(2)")?.value;

    console.log('📊 필터 적용:', { status, startDate, endDate });
}

// 🚀 페이지가 로드될 때 실행
document.addEventListener("DOMContentLoaded", () => {
    initProductOrders();
});