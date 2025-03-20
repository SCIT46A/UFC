if (typeof cachedOrders === "undefined") {
    var cachedOrders = [];
}

// 페이지 초기화
function initProductOrders() {
    console.log("📦 주문/발송 관리 JS 실행됨");

    if (cachedOrders.length > 0) {
        updateOrderCountsFromCache();
        renderOrders(cachedOrders);
    } else {
        loadOrders();
    }

    // 필터 이벤트 (예시로 status select)
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

// 주문 데이터 로드
async function loadOrders(filters = {}, forceReload = false) {
    try {
        if (!forceReload && cachedOrders.length > 0) {
            console.log("🔄 캐싱된 데이터 사용");
            updateOrderCountsFromCache();
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

        let data = await response.json();
        console.log("✅ 주문 데이터 로드 성공:", data);

        // 서버에서 받은 데이터 적용
        cachedOrders = data.orders || [];
        // (data.orderCounts를 사용할 수도 있지만, 여기서는 cachedOrders 기반 업데이트)

        updateOrderCountsFromCache();
        renderOrders(cachedOrders);
    } catch (error) {
        console.error("❌ 주문 데이터 로딩 실패:", error);
    }
}

// 주문 테이블 렌더링
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

    orders.forEach(order => {
        const row = document.createElement("tr");

        // 주문 정보 추출
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

        // 송장 및 택배 정보
        let trackingNumber = order.trackingNumber ? order.trackingNumber.trim() : null;
        let courierId = order.courierId ? order.courierId.trim() : null;
        let courierName = order.courierName ? order.courierName.trim() : "";
        let ostatus = deliveryStatus === "미등록" ? paymentStatus : deliveryStatus;

        // 택배사 UI: 송장번호 있으면 span, 없으면 select
        let courierHtml = trackingNumber
            ? `<span>${courierName}</span>`
            : `
                  <select class="courier-select" ${paymentStatus !== "발주 완료" ? "disabled" : ""}>
                      <option value="">택배사 선택</option>
                      <option value="kr.cjlogistics" ${courierId === "kr.cjlogistics" ? "selected" : ""}>CJ대한통운</option>
                      <option value="kr.epost" ${courierId === "kr.epost" ? "selected" : ""}>우체국택배</option>
                      <option value="kr.hanjin" ${courierId === "kr.hanjin" ? "selected" : ""}>한진택배</option>
                      <option value="kr.cupost" ${courierId === "kr.cupost" ? "selected" : ""}>CU편의점택배</option>
                      <option value="kr.cvsnet" ${courierId === "kr.cvsnet" ? "selected" : ""}>GS Postbox</option>
                  </select>
                `;
        // 송장번호 UI
        let trackingNumberHtml = trackingNumber
            ? `<span>${trackingNumber}</span>`
            : `<input type="text" class="tracking-number-input" placeholder="송장번호 입력" value="" ${paymentStatus !== "발주 완료" ? "disabled" : ""}>`;

        // 액션 버튼 결정
        let actionButton = "";
        if (paymentStatus === "결제 완료") {
            actionButton = `<button class="btn btn-primary" onclick="processOrder(${payId})">발주</button>`;
        } else if (paymentStatus === "발주 완료" && !trackingNumber) {
            actionButton = `<button class="btn btn-success" onclick="saveInvoice(${payId})">등록</button>`;
        } else if (paymentStatus === "발주 완료" && trackingNumber) {
            actionButton = `<button class="btn btn-secondary" onclick="editInvoice(${payId})">수정</button>`;
        } else if (paymentStatus === "취소 요청") {
            actionButton = `<button class="btn btn-danger" onclick="approveCancel(${payId})">승인</button>`;
        } else if (paymentStatus === "취소 완료") {
            actionButton = `<span class="text-danger">-</span>`;
        }

        row.innerHTML = `
                <td><input type="checkbox" class="order-checkbox" value="${payId}" data-product-id="${productId}"></td>
                <td>${payId}</td>
                <td>${purchasedDate}</td>
                <td>${productInfo}</td>
                <td>${buyerName}</td>
                <td>${buyerPhone}</td>
                <td>${buyerAddress}</td>
                <td>${courierHtml}</td>
                <td>${trackingNumberHtml}</td>
                <td>${actionButton}</td>
                <td>${ostatus}</td>
            `;
        tbody.appendChild(row);
    });
}

// 예시: 발주 처리 (processOrder)
async function processOrder(payId) {
    try {
        console.log(`🚀 발주 처리 요청: payId=${payId}`);
        let response = await fetch(`/api/creator/dashboard/products/orders/process/${payId}`, {
            method: "POST",
            headers: { "Content-Type": "application/json" }
        });
        if (!response.ok) throw new Error(`🚨 발주 처리 실패: ${response.status}`);

        console.log(`✅ 발주 처리 성공: payId=${payId}`);

        // UI 업데이트: 해당 행의 버튼과 상태 변경
        const row = document.querySelector(`.order-checkbox[value="${payId}"]`)?.closest("tr");
        if (row) {
            row.children[9].innerHTML = `<button class="btn btn-success" onclick="saveInvoice(${payId})">등록</button>`;
            row.children[10].textContent = "발주 완료";

            // 택배사 선택 및 송장번호 입력 필드 활성화
            let courierSelect = row.querySelector(".courier-select");
            let trackingInput = row.querySelector(".tracking-number-input");
            if (courierSelect) courierSelect.removeAttribute("disabled");
            if (trackingInput) trackingInput.removeAttribute("disabled");
        }

        // 클라이언트 캐시(cachedOrders) 업데이트: 해당 주문 상태 변경
        let order = cachedOrders.find(o => o.payId === payId);
        if (order) {
            // 서버에서 DB는 ordered로 업데이트되어 있다면,
            // 클라이언트에서는 "발주 완료"라는 한글 상태를 사용하도록 통일합니다.
            order.status = "발주 완료";
            // 만약 paymentStatus도 사용 중이라면 함께 업데이트
            order.paymentStatus = "ordered";
        }

        // 상태 카드와 주문 목록 UI 재갱신
        updateOrderCountsFromCache();
        renderOrders(cachedOrders);
    } catch (error) {
        console.error("❌ 발주 처리 실패:", error);
    }
}

// 예시: 송장번호 저장 (saveInvoice)
async function saveInvoice(payId) {
    const row = document.querySelector(`.order-checkbox[value="${payId}"]`)?.closest("tr");
    if (!row) {
        console.error("❌ 주문 행을 찾을 수 없습니다.");
        return;
    }
    const courier = row.querySelector(".courier-select")?.value;
    const trackingNumber = row.querySelector(".tracking-number-input")?.value.trim();
    const productId = row.querySelector(`.order-checkbox[value="${payId}"]`).dataset.productId;

    if (!courier || !trackingNumber) {
        alert("🚨 택배사와 송장번호를 입력해주세요!");
        return;
    }

    try {
        console.log(`🔍 송장 등록 요청: payId=${payId}, productId=${productId}, courier=${courier}, trackingNumber=${trackingNumber}`);
        let updateResponse = await fetch(`/api/creator/dashboard/products/orders/invoice/${payId}`, {
            method: "POST",
            headers: { "Content-Type": "application/json" },
            body: JSON.stringify({ courier, trackingNumber, productId })
        });
        if (!updateResponse.ok) throw new Error(`🚨 송장번호 업데이트 실패: ${updateResponse.status}`);

        console.log(`✅ DB 송장 정보 업데이트 완료: ${payId}`);

        // 배송 상태 업데이트 요청 (API에 따라 다름)
        let statusResponse = await fetch(`/api/delivery/status?trackingNumber=${trackingNumber}&courierId=${courier}`, {
            method: "GET",
            headers: { "Content-Type": "application/json" },
        });
        if (!statusResponse.ok) throw new Error(`🚨 배송 상태 조회 실패: ${statusResponse.status}`);
        let deliveryStatus = await statusResponse.json();
        console.log(`🚀 배송 상태 업데이트 완료: ${deliveryStatus.status}`);

        // UI 업데이트: 택배사, 송장번호, 버튼, 배송 상태 변경
        row.children[7].innerHTML = `<span>${getCourierName(courier)}</span>`;
        row.children[8].innerHTML = `<span>${trackingNumber}</span>`;
        row.children[9].innerHTML = `<button class="btn btn-secondary" onclick="editInvoice(${payId})">수정</button>`;
        row.children[10].textContent = deliveryStatus.status || "배송 대기";

        // 캐시 업데이트: 해당 주문의 택배, 송장, 배송 상태 업데이트
        let order = cachedOrders.find(o => o.payId === payId);
        if (order) {
            order.courierId = courier;
            order.courierName = getCourierName(courier);
            order.trackingNumber = trackingNumber;
            order.deliveryStatus = deliveryStatus.status || "배송 대기";
        }

        updateOrderCountsFromCache();
        renderOrders(cachedOrders);

    } catch (error) {
        console.error("❌ 송장 수정 실패:", error);
    }
}

// 예시: 취소 승인 (approveCancel)
async function approveCancel(payId) {
    if (!confirm("🚨 이 주문의 취소 요청을 승인하시겠습니까?")) return;
    try {
        let response = await fetch(`/api/creator/dashboard/products/orders/cancel/${payId}`, {
            method: "POST",
            headers: { "Content-Type": "application/json" },
        });
        if (!response.ok) throw new Error(`🚨 주문 취소 승인 실패: ${response.status}`);

        console.log(`✅ 주문 취소 승인 완료: ${payId}`);
        const row = document.querySelector(`.order-checkbox[value="${payId}"]`)?.closest("tr");
        if (row) {
            row.children[9].innerHTML = `<span class="text-danger">-</span>`;
            row.children[10].textContent = "취소 완료";
        }
        let order = cachedOrders.find(o => o.payId === payId);
        if (order) {
            order.status = "취소 완료";
            // 필요한 경우 paymentStatus 도 업데이트
            order.paymentStatus = "cancelled";
        }
        updateOrderCountsFromCache();
        renderOrders(cachedOrders);
    } catch (error) {
        console.error("❌ 취소 승인 실패:", error);
    }
}

// 선택된 주문 일괄 발주 처리
async function processSelectedOrders() {
    const checkboxes = document.querySelectorAll('.order-checkbox:checked');
    if (checkboxes.length === 0) {
        alert("📌 발주할 주문이 선택되지 않았습니다.");
        return;
    }
    let ordersToProcess = [];
    checkboxes.forEach(checkbox => {
        const row = checkbox.closest("tr");
        const payId = checkbox.value;
        const paymentStatus = row.children[10].textContent.trim();
        if (paymentStatus === "결제 완료") {
            ordersToProcess.push(payId);
        }
    });
    if (ordersToProcess.length === 0) {
        alert("🚨 발주 가능한 주문이 없습니다. (결제 완료 상태의 주문만 발주 가능)");
        return;
    }
    console.log("🚀 발주 요청 목록:", ordersToProcess);
    for (const payId of ordersToProcess) {
        await processOrder(payId);
    }
    document.getElementById("selectAll").checked = false;
    document.querySelectorAll(".order-checkbox").forEach(chk => chk.checked = false);
    alert("✅ 선택된 주문의 발주 처리가 완료되었습니다!");
}

// 파일 다운로드, 업로드 함수 (생략 - 기존 로직 유지)

// 상태 카드 업데이트 함수 (cachedOrders 기준)
function updateOrderCountsFromCache() {
    if (!cachedOrders || cachedOrders.length === 0) {
        console.warn("⚠️ 캐싱된 주문 데이터가 없음!");
        return;
    }

    // 상태 카드에서 사용될 상태 키 매핑 (Korean)
    const statusMap = {
        "결제 완료": "paymentCompleted",
        "발주 완료": "orderCompleted",
        "취소 요청": "cancelRequested",
        "취소 완료": "cancelCompleted",
        "배송 완료": "deliveryCompleted"
    };

    // 카운트 초기화
    let counts = {
        paymentCompleted: 0,
        orderCompleted: 0,
        deliveryCompleted: 0,
        cancelRequested: 0,
        cancelCompleted: 0
    };

    // 각 주문의 상태가 status 프로퍼티가 없으면 paymentStatus를 변환해서 사용
    cachedOrders.forEach(order => {
        // order.status가 있으면 사용하고, 없으면 order.paymentStatus를 변환
        let currentStatus = order.status || mapPaymentStatus(order.paymentStatus);
        let mapped = statusMap[currentStatus];
        if (mapped) {
            counts[mapped]++;
        }
    });

    // 상태 카드 DOM 업데이트
    const statusElements = document.querySelectorAll(".status-grid .status-card .count");
    if (statusElements.length < 5) {
        console.warn("⚠️ 상태 카드 개수 업데이트 실패: HTML 구조 확인 필요!");
        return;
    }
    statusElements[0].textContent = counts.paymentCompleted;  // 결제 완료
    statusElements[1].textContent = counts.orderCompleted;    // 발주 완료
    statusElements[2].textContent = counts.deliveryCompleted; // 배송 완료
    statusElements[3].textContent = counts.cancelRequested;   // 취소 요청
    statusElements[4].textContent = counts.cancelCompleted;   // 취소 완료

    console.log("✅ 상태 카드 업데이트:", counts);
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
    return `${year}-${month}-${day}<br> ${hours}:${minutes}:${seconds}`;
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
        "ordered": "발주 완료"
    };
    return statusMap[status] || "알 수 없음";
}

function applyFilters() {
    const status = document.getElementById('status')?.value;
    const startDate = document.querySelector(".date-range input[type='date']")?.value;
    const endDate = document.querySelector(".date-range input[type='date']:nth-of-type(2)")?.value;
    console.log('📊 필터 적용:', { status, startDate, endDate });
}

function openBatchInvoiceModal() {
    let modal = document.getElementById("batchInvoiceModal");
    if (modal) modal.style.display = "block";
}

function closeBatchInvoiceModal() {
    let modal = document.getElementById("batchInvoiceModal");
    if (modal) modal.style.display = "none";
}

document.getElementById("selectAll")?.addEventListener("change", function (event) {
    const isChecked = event.target.checked;
    document.querySelectorAll(".order-checkbox").forEach(chk => chk.checked = isChecked);
});

document.addEventListener("DOMContentLoaded", () => {
    initProductOrders();
});