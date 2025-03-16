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
        let ostatus = deliveryStatus === "미등록" ? paymentStatus : deliveryStatus;

        // ✅ 택배사 UI 업데이트 (송장번호가 있으면 span, 없으면 select)
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

        // ✅ 송장번호 UI 업데이트 (송장번호가 있으면 span, 없으면 input)
        let trackingNumberHtml = trackingNumber
            ? `<span>${trackingNumber}</span>`
            : `<input type="text" class="tracking-number-input" placeholder="송장번호 입력" value="" ${paymentStatus !== "발주 완료" ? "disabled" : ""}>`;

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
        <td>${buyerName}</td>
        <td>${buyerPhone}</td>
        <td>${buyerAddress}</td>
        <td>${courierHtml}</td>
        <td>${trackingNumberHtml}</td>
        <td>${actionButton}</td>
        <td>${ostatus}</td>
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
            // ✅ 액션 버튼을 "송장 등록"으로 변경
            row.children[9].innerHTML = `<button class="btn btn-success" onclick="saveInvoice(${payId})">등록</button>`;

            // ✅ 상태를 "발주 완료"로 업데이트
            row.children[10].textContent = "발주 완료";

            // ✅ 택배사 선택 및 송장번호 입력 필드 활성화
            let courierSelect = row.querySelector(".courier-select");
            let trackingInput = row.querySelector(".tracking-number-input");

            if (courierSelect) {
                courierSelect.removeAttribute("disabled"); // 택배사 선택 활성화
            }
            if (trackingInput) {
                trackingInput.removeAttribute("disabled"); // 송장번호 입력 활성화
            }
        }
    } catch (error) {
        console.error("❌ 발주 처리 실패:", error);
    }
}


function editInvoice(payId) {
    const row = document.querySelector(`.order-checkbox[value="${payId}"]`).closest("tr");

    if (!row) {
        console.error("❌ 주문 행을 찾을 수 없습니다.");
        return;
    }

    const courierCell = row.children[7]; // 택배사 셀
    const trackingNumberCell = row.children[8]; // 송장번호 셀
    const buttonCell = row.children[9]; // 버튼 셀

    // 기존 값 가져오기
    const currentCourier = courierCell.innerText.trim();
    const currentTrackingNumber = trackingNumberCell.innerText.trim();

    // 택배사 드롭다운으로 변경
    courierCell.innerHTML = `
        <select class="courier-select">
            <option value="">택배사 선택</option>
            <option value="kr.cjlogistics" ${currentCourier === "CJ대한통운" ? "selected" : ""}>CJ대한통운</option>
            <option value="kr.epost" ${currentCourier === "우체국택배" ? "selected" : ""}>우체국택배</option>
            <option value="kr.hanjin" ${currentCourier === "한진택배" ? "selected" : ""}>한진택배</option>
            <option value="kr.cupost" ${currentCourier === "CU편의점택배" ? "selected" : ""}>CU편의점택배</option>
            <option value="kr.cvsnet" ${currentCourier === "kr.cvsnet" ? "selected" : ""}>GS Postbox</option>
        </select>
    `;

    // 송장번호 입력 필드로 변경
    trackingNumberCell.innerHTML = `<input type="text" class="tracking-number-input" value="${currentTrackingNumber}">`;

    // 저장 버튼 추가
    buttonCell.innerHTML = `<button class="btn btn-success" onclick="saveInvoice(${payId})">저장</button>`;
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
        row.children[7].innerHTML = `<span>${getCourierName(courier)}</span>`; // 택배사 업데이트
        row.children[8].innerHTML = `<span>${trackingNumber}</span>`; // 송장번호 업데이트
        row.children[9].innerHTML = `<button class="btn btn-secondary" onclick="editInvoice(${payId})">수정</button>`; // 버튼 변경
        row.children[10].textContent = deliveryStatus.status || "배송 대기"; // 배송 상태 표시

    } catch (error) {
        console.error("❌ 송장 수정 실패:", error);
    }
}

async function approveCancel(payId) {
    if (!confirm("🚨 이 주문의 취소 요청을 승인하시겠습니까?")) return;

    try {
        let response = await fetch(`/api/creator/dashboard/products/orders/cancel/${payId}`, {
            method: "POST",
            headers: { "Content-Type": "application/json" },
        });

        if (!response.ok) throw new Error(`🚨 주문 취소 승인 실패: ${response.status}`);

        console.log(`✅ 주문 취소 승인 완료: ${payId}`);

        // ✅ UI 업데이트: 해당 주문의 상태를 "취소 완료"로 변경
        let row = document.querySelector(`.order-checkbox[value="${payId}"]`).closest("tr");
        row.children[9].innerHTML = `<span class="text-danger">-</span>`; // 액션 버튼 제거
        row.children[10].textContent = "취소 완료";
    } catch (error) {
        console.error("❌ 취소 승인 실패:", error);
    }
}


async function processSelectedOrders() {
    const checkboxes = document.querySelectorAll('.order-checkbox:checked');
    if (checkboxes.length === 0) {
        alert("📌 발주할 주문이 선택되지 않았습니다.");
        return;
    }

    let ordersToProcess = [];
    for (const checkbox of checkboxes) {
        const row = checkbox.closest("tr");
        const payId = checkbox.value;
        const paymentStatus = row.children[10].textContent.trim(); // 상태 칼럼 위치 확인

        if (paymentStatus === "결제 완료") {
            ordersToProcess.push(payId);
        }
    }

    if (ordersToProcess.length === 0) {
        alert("🚨 발주 가능한 주문이 없습니다. (결제 완료 상태의 주문만 발주 가능)");
        return;
    }

    console.log("🚀 발주 요청 목록:", ordersToProcess);

    for (const payId of ordersToProcess) {
        try {
            let response = await fetch(`/api/creator/dashboard/products/orders/process/${payId}`, {
                method: "POST",
                headers: { "Content-Type": "application/json" }
            });

            if (!response.ok) throw new Error(`🚨 발주 처리 실패: ${response.status}`);

            console.log(`✅ 발주 처리 성공: payId=${payId}`);

            // ✅ UI 업데이트: "발주 완료"로 상태 변경 & 버튼 변경
            const row = document.querySelector(`.order-checkbox[value="${payId}"]`)?.closest("tr");
            if (row) {
                row.children[9].innerHTML = `<button class="btn btn-success" onclick="saveInvoice(${payId})">등록</button>`;
                row.children[10].textContent = "발주 완료"; // 상태 업데이트

                // 택배사 선택 & 송장 입력 필드 활성화
                let courierSelect = row.querySelector(".courier-select");
                let trackingInput = row.querySelector(".tracking-number-input");

                if (courierSelect) courierSelect.removeAttribute("disabled");
                if (trackingInput) trackingInput.removeAttribute("disabled");
            }
        } catch (error) {
            console.error(`❌ 발주 처리 실패 (payId=${payId}):`, error);
        }
    }

    // ✅ 체크 해제
    document.getElementById("selectAll").checked = false;
    document.querySelectorAll(".order-checkbox").forEach(chk => chk.checked = false);

    alert("✅ 선택된 주문의 발주 처리가 완료되었습니다!");
}


document.getElementById("selectAll")?.addEventListener("change", function (event) {
    const isChecked = event.target.checked;
    document.querySelectorAll(".order-checkbox").forEach(chk => chk.checked = isChecked);
});

async function downloadOrderExcel() {
    console.log("📂 주문 내역 다운로드 시작...");

    const table = document.querySelector("#order-table-body");
    if (!table) {
        alert("⚠ 다운로드할 데이터가 없습니다. (테이블이 존재하지 않음)");
        return;
    }

    const selectedCheckboxes = document.querySelectorAll(".order-checkbox:checked");
    let rowsToDownload = [];

    if (selectedCheckboxes.length > 0) {
        // ✅ 선택된 주문만 다운로드
        selectedCheckboxes.forEach(checkbox => {
            const row = checkbox.closest("tr");
            rowsToDownload.push(row);
        });
    } else {
        // ✅ 선택된 항목이 없으면 전체 다운로드
        rowsToDownload = Array.from(table.querySelectorAll("tr"));
    }

    if (rowsToDownload.length === 0) {
        alert("⚠ 다운로드할 데이터가 없습니다.");
        return;
    }

    const wb = XLSX.utils.book_new();
    const wsData = [
        ["주문번호", "주문일자", "고객명", "연락처", "주소", "상품명", "수량", "단가", "총액", "택배사", "송장번호"]
    ];

    let orderMap = new Map(); // 주문별 데이터를 그룹화

    rowsToDownload.forEach(row => {
        const columns = row.querySelectorAll("td");

        let orderNumber = columns[1]?.textContent.trim() || "-";
        let orderDate = columns[2]?.textContent.trim() || "-";
        let customerName = columns[4]?.textContent.trim() || "-";
        let customerPhone = columns[5]?.textContent.trim() || "-";
        let customerAddress = columns[6]?.textContent.trim() || "-";
        let courierName = columns[7]?.querySelector("span")?.textContent.trim() || ""; // 택배사
        let trackingNumber = columns[8]?.querySelector("span")?.textContent.trim() || ""; // 송장번호

        // ✅ 주문 내역을 <br> 태그 기준으로 나누기
        let orderDetails = columns[3]?.innerHTML.split("<br>").map(line => line.trim()) || [];

        let productName = orderDetails[0]?.replace("상품명  : ", "") || "-";
        let quantity = orderDetails[1]?.replace("수량    : ", "").replace("개", "").trim() || "-";
        let unitPrice = orderDetails[2]?.replace("단가    : ", "").replace("원", "").trim() || "-";
        let totalPrice = orderDetails[3]?.replace("총액    : ", "").replace("원", "").trim() || "-";

        // ✅ 주문번호 별로 그룹화하여 같은 주문번호는 한 번만 저장
        if (!orderMap.has(orderNumber)) {
            orderMap.set(orderNumber, {
                orderDate,
                customerName,
                customerPhone,
                customerAddress,
                courierName,
                trackingNumber,
                products: []
            });
        }

        orderMap.get(orderNumber).products.push([productName, quantity, unitPrice, totalPrice]);
    });

    // ✅ 주문번호별로 데이터를 합쳐서 엑셀 시트에 추가
    orderMap.forEach((order, orderNumber) => {
        let firstRow = true;
        order.products.forEach(product => {
            let row = [
                firstRow ? orderNumber : "",
                firstRow ? order.orderDate : "",
                firstRow ? order.customerName : "",
                firstRow ? order.customerPhone : "",
                firstRow ? order.customerAddress : "",
                ...product,
                firstRow ? order.courierName : "",
                firstRow ? order.trackingNumber : ""
            ];
            wsData.push(row);
            firstRow = false;
        });
    });

    const ws = XLSX.utils.aoa_to_sheet(wsData);

    // ✅ 셀 병합 (주문번호, 주문일자, 고객정보, 배송정보)
    let merges = [];
    let rowIndex = 1;
    orderMap.forEach((order, orderNumber) => {
        if (order.products.length > 1) {
            let mergeEndRow = rowIndex + order.products.length - 1;
            merges.push(
                { s: { r: rowIndex, c: 0 }, e: { r: mergeEndRow, c: 0 } }, // 주문번호
                { s: { r: rowIndex, c: 1 }, e: { r: mergeEndRow, c: 1 } }, // 주문일자
                { s: { r: rowIndex, c: 2 }, e: { r: mergeEndRow, c: 2 } }, // 고객명
                { s: { r: rowIndex, c: 3 }, e: { r: mergeEndRow, c: 3 } }, // 연락처
                { s: { r: rowIndex, c: 4 }, e: { r: mergeEndRow, c: 4 } }, // 주소
                { s: { r: rowIndex, c: 9 }, e: { r: mergeEndRow, c: 9 } }, // 택배사
                { s: { r: rowIndex, c: 10 }, e: { r: mergeEndRow, c: 10 } } // 송장번호
            );
        }
        rowIndex += order.products.length;
    });

    ws["!merges"] = merges;
    XLSX.utils.book_append_sheet(wb, ws, "주문 내역");

    const today = new Date().toISOString().split("T")[0];
    const fileName = `${today}_주문내역.xlsx`;

    XLSX.writeFile(wb, fileName);
    console.log(`✅ ${fileName} 엑셀 다운로드 완료!`);
}


async function uploadInvoiceExcel() {
    const fileInput = document.getElementById("invoiceExcelUpload");
    if (!fileInput.files.length) {
        alert("⚠ 업로드할 엑셀 파일을 선택하세요.");
        return;
    }

    const reader = new FileReader();
    reader.onload = async function (event) {
        try {
            const data = new Uint8Array(event.target.result);
            const workbook = XLSX.read(data, { type: "array" });
            const sheet = workbook.Sheets[workbook.SheetNames[0]];
            let jsonData = XLSX.utils.sheet_to_json(sheet, { header: 1 });

            console.log("📊 업로드된 엑셀 원본 데이터:", jsonData);

            // ✅ 1. 헤더 찾기 (주문번호, 택배사, 송장번호)
            const headerIndex = jsonData.findIndex(row => row.includes("주문번호"));
            if (headerIndex === -1) {
                alert("⚠ 올바른 엑셀 파일이 아닙니다. 양식을 확인하세요.");
                return;
            }

            // ✅ 2. 데이터 필터링 (빈 행 제거)
            let validData = jsonData.slice(headerIndex + 1).filter(row => row.some(value => value));

            if (validData.length === 0) {
                alert("⚠ 데이터가 없습니다. 올바른 파일을 업로드하세요.");
                return;
            }

            console.log("📊 필터링된 데이터:", validData);

            let updateData = validData.map(rowData => ({
                id: Number(rowData[0]?.toString().trim() || 0), // 주문번호
                courier: rowData[9]?.toString().trim(), // 택배사
                trackingNumber: rowData[10]?.toString().trim() // 송장번호
            })).filter(row => row.id && row.trackingNumber);

            if (updateData.length === 0) {
                alert("⚠ 올바른 데이터가 없습니다. 주문번호, 택배사, 송장번호를 입력하세요.");
                return;
            }

            // ✅ 3. 송장번호 업데이트 요청
            await updateInvoicesInDB(updateData);

            // ✅ 4. UI 데이터 갱신
            await loadOrders({}, true);

            // ✅ 5. 모달 닫기
            closeBatchInvoiceModal();

            // ✅ 6. 성공 메시지
            alert("✅ 송장번호가 정상적으로 등록되었습니다.");

        } catch (error) {
            console.error("❌ 엑셀 업로드 처리 중 오류 발생:", error);
            alert("❌ 엑셀 업로드 실패! 콘솔을 확인하세요.");
        }
    };

    reader.readAsArrayBuffer(fileInput.files[0]);
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

function openBatchInvoiceModal() {
    let modal = document.getElementById("batchInvoiceModal");
    modal.style.display = "block"; // 모달 열기
}

function closeBatchInvoiceModal() {
    let modal = document.getElementById("batchInvoiceModal");
    if (modal) {
        modal.style.display = "none"; // 모달 닫기
    }
}


// 🚀 페이지가 로드될 때 실행
document.addEventListener("DOMContentLoaded", () => {
    initProductOrders();
});