// ✅ `cachedDeliveries` 전역 변수 선언 (중복 방지)
if (typeof cachedRewardDeliveries === "undefined") {
    var cachedRewardDeliveries = [];
}


// ✅ 리워드 배송 관리 페이지 초기화
function initRewardDeliveryManagement() {
    console.log("🚚 리워드 배송 관리 JS 실행됨");

    // ✅ 기존 이벤트 리스너 제거 후 다시 추가
    document.removeEventListener("click", handleButtonClick);
    document.addEventListener("click", handleButtonClick);

    // ✅ 기존 데이터가 있으면 재렌더링, 없으면 다시 불러오기
    if (cachedRewardDeliveries.length > 0) {
        updateRewardDeliveryCounts(getDeliveryCounts(cachedRewardDeliveries));
        renderRewardDeliveries(cachedRewardDeliveries);
    } else {
        loadRewardDeliveries();
    }
}


async function loadRewardDeliveries(filters = {}, forceReload = false) {
    try {
        if (!forceReload && Object.keys(filters).length === 0 && cachedRewardDeliveries.length > 0) {
            console.log("🔄 캐싱된 데이터 사용");
            updateRewardDeliveryCounts(getDeliveryCounts(cachedRewardDeliveries));
            renderRewardDeliveries(cachedRewardDeliveries);
            return;
        }

        console.log("🚀 리워드 배송 데이터 로딩 중...", filters);

        let queryParams = new URLSearchParams(filters).toString();
        let response = await fetch(`/api/creator/dashboard/reward/deliveries?${queryParams}`, {
            method: "GET",
            headers: { "Cache-Control": "no-cache" },
        });

        if (!response.ok) throw new Error(`HTTP 오류 발생: ${response.status}`);

        let data = await response.json();
        console.log("✅ 리워드 배송 데이터 로드 성공:", data);

        if (!data.rewardDeliveries || data.rewardDeliveries.length === 0) {
            console.warn("⚠ 배송 데이터가 비어 있습니다:", data.rewardDeliveries);
        }

        cachedRewardDeliveries = data.rewardDeliveries;

        // ✅ 상태 카드 값 업데이트
        updateRewardDeliveryCounts(data.deliveryCounts);

        // ✅ 테이블 데이터 업데이트
        renderRewardDeliveries(cachedRewardDeliveries);
    } catch (error) {
        console.error("❌ 리워드 배송 데이터 로딩 실패:", error);
    }
}


async function renderRewardDeliveries(rewardDeliveries) {
    const tbody = document.querySelector("#delivery-table-body");
    if (!tbody) {
        console.error("❌ tbody 요소를 찾을 수 없습니다. 클래스 확인 필요!");
        return;
    }

    tbody.innerHTML = ""; // 기존 목록 초기화

    if (rewardDeliveries.length === 0) {
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

    for (const rewardDelivery of rewardDeliveries) {
        const row = document.createElement("tr");
        let formattedAddress = rewardDelivery.donation?.donorAddress?.replace("#", "\n") || "-";
        let rewardInfo = `${rewardDelivery.rewardName || '-'} (수량: ${rewardDelivery.amount || 0}개)`;

        // ✅ `invoice`에서 택배사 코드와 송장번호 분리
        const [courierId, trackingNumber] = rewardDelivery.invoice ? rewardDelivery.invoice.split("#") : ["", ""];

        // ✅ 배송 상태 확인
        let deliveryStatus = rewardDelivery.deliveryStatus || "발송 대기중";


        // ✅ 송장번호 UI 업데이트 (있으면 `span`, 없으면 `input`)
        let trackingNumberHtml = trackingNumber
            ? `<span>${trackingNumber}</span>`
            : `<input type="text" class="tracking-number-input" placeholder="송장번호 입력" value="">`;

        // ✅ 택배사 UI 업데이트 (있으면 `span`, 없으면 `select`)
        let courierHtml = trackingNumber
            ? `<span>${getCourierName(courierId)}</span>`
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

        // ✅ 발송처리 버튼 UI 업데이트 (있으면 "송장 수정", 없으면 "발송처리")
        let buttonHtml = trackingNumber
            ? `<button class="btn btn-secondary" onclick="editInvoice(${rewardDelivery.rdeliveryId})">송장 수정</button>`
            : `<button class="btn btn-primary" onclick="processDelivery(${rewardDelivery.rdeliveryId})">발송처리</button>`;

        row.innerHTML = `
            <td><input type="checkbox" class="delivery-checkbox" value="${rewardDelivery.rdeliveryId}"></td>
            <td>${rewardDelivery.donation?.campaignTitle || '-'}</td>
            <td>${rewardDelivery.donation?.donationId || '-'}</td>
            <td>${rewardDelivery.donation?.userName || '-'}</td>
            <td>${rewardDelivery.donation?.donorPhone || '-'}</td>
            <td style="white-space: pre-line;">${formattedAddress}</td>
            <td>${rewardInfo}</td>
            <td>${rewardDelivery.donation?.dueDate ? formatDate(new Date(rewardDelivery.donation.dueDate)) : '-'}</td>
            <td>${courierHtml}</td>
            <td>${trackingNumberHtml}</td>
            <td>${buttonHtml}</td>
            <td>${deliveryStatus}</td>
        `;
        tbody.appendChild(row);
    }

    console.log("✅ 리워드 배송 데이터 렌더링 완료!");
}



// ✅ 배송 상태 개수 계산 함수 (추가됨)
function getDeliveryCounts(deliveries) {
    let counts = {
        overdue: 0,
        autoProcess: 0,
        newOrders: 0,
        readyToShip: 0,
        shipmentD1: 0,
        shipmentDday: 0,
        shipped: 0
    };

    deliveries.forEach(delivery => {
        switch (delivery.status) {
            case "overdue": counts.overdue++; break;
            case "autoProcess": counts.autoProcess++; break;
            case "newOrders": counts.newOrders++; break;
            case "readyToShip": counts.readyToShip++; break;
            case "shipmentD1": counts.shipmentD1++; break;
            case "shipmentDday": counts.shipmentDday++; break;
            case "shipped": counts.shipped++; break;
        }
    });

    return counts;
}


function editInvoice(rdeliveryId) {
    const row = document.querySelector(`.delivery-checkbox[value="${rdeliveryId}"]`).closest("tr");

    const courierCell = row.children[8]; // 택배사 셀
    const trackingNumberCell = row.children[9]; // 송장번호 셀
    const buttonCell = row.children[10]; // 버튼 셀

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
            <option value="kr.cvsnet" ${currentCourier === "GS Postbox" ? "selected" : ""}>GS Postbox</option>
        </select>
    `;

    // 송장번호 입력 필드로 변경
    trackingNumberCell.innerHTML = `<input type="text" class="tracking-number-input" value="${currentTrackingNumber}">`;

    // 저장 버튼 추가
    buttonCell.innerHTML = `<button class="btn btn-success" onclick="saveInvoice(${rdeliveryId})">저장</button>`;
}


async function saveInvoice(rdeliveryId) {
    const row = document.querySelector(`.delivery-checkbox[value="${rdeliveryId}"]`).closest("tr");

    const courier = row.querySelector(".courier-select").value;
    const trackingNumber = row.querySelector(".tracking-number-input").value.trim();

    if (!courier || !trackingNumber) {
        alert("🚨 택배사와 송장번호를 입력해주세요!");
        return;
    }

    try {
        // ✅ 1. 🚀 DB에 송장번호 & 택배사 정보 업데이트 (DB 저장 요청 추가!)
        let updateResponse = await fetch(`/api/creator/dashboard/reward/deliveries/${rdeliveryId}`, {
            method: "POST",
            headers: { "Content-Type": "application/json" },
            body: JSON.stringify({ courier, trackingNumber }) // JSON 데이터 전송
        });

        if (!updateResponse.ok) throw new Error(`🚨 송장번호 업데이트 실패: ${updateResponse.status}`);

        console.log(`✅ DB 송장 정보 업데이트 완료: ${rdeliveryId}`);

        // ✅ 2. 🚀 배송 상태 업데이트 요청
        let statusResponse = await fetch(`/api/delivery/status?trackingNumber=${trackingNumber}&courierId=${courier}`, {
            method: "GET",
            headers: { "Content-Type": "application/json" },
        });

        if (!statusResponse.ok) throw new Error(`🚨 배송 상태 조회 실패: ${statusResponse.status}`);

        let deliveryStatus = await statusResponse.json(); // 배송 상태 응답 JSON 처리
        console.log(`🚀 배송 상태 업데이트 완료: ${deliveryStatus}`);

        // ✅ 3. 🚀 UI 업데이트
        row.children[8].innerHTML = `<span>${getCourierName(courier)}</span>`; // 택배사 업데이트
        row.children[9].innerHTML = `<span>${trackingNumber}</span>`; // 송장번호 업데이트
        row.children[10].innerHTML = `<button class="btn btn-secondary" onclick="editInvoice(${rdeliveryId})">송장 수정</button>`; // 버튼 변경
        row.children[11].textContent = deliveryStatus.status || "배송 상태 조회 실패"; // 배송 상태 표시

    } catch (error) {
        console.error("❌ 송장 수정 실패:", error);
    }
}



// ✅ 배송 상태를 API에서 가져오는 함수
async function fetchDeliveryStatus(trackingNumber, courierId) {
    try {
        if (!trackingNumber || !courierId) {
            return "🚨 운송장 정보 없음";
        }

        let response = await fetch(`/api/delivery/status?trackingNumber=${trackingNumber}&courierId=${courierId}`);
        if (!response.ok) throw new Error("🚨 배송 상태 API 호출 실패");

        let data = await response.json();
        return data.status || "배송 정보 없음";
    } catch (error) {
        console.error("🚨 배송 상태 조회 실패:", error);
        return "배송 상태 불명";
    }
}



// async function updateDeliveryStatus(rdeliveryId, row) {
//     try {
//         let response = await fetch(`/api/delivery/status?rdeliveryId=${rdeliveryId}`);
//         if (!response.ok) throw new Error("🚨 배송 상태 조회 실패!");

//         let data = await response.json();
//         row.children[11].textContent = data.status || "배송 정보 없음"; // 배송 상태 업데이트

//     } catch (error) {
//         console.error("🚨 배송 상태 업데이트 실패:", error);
//     }
// }


// ✅ 발송 처리
async function processDelivery(rewardDeliveryId) {
    const row = document.querySelector(`.delivery-checkbox[value="${rewardDeliveryId}"]`)?.closest("tr");
    if (!row) return;

    const courier = row.querySelector(".courier-select").value;
    const trackingNumber = row.querySelector(".tracking-number-input").value.trim();

    if (!courier || !trackingNumber) {
        alert("🚨 택배사와 송장번호를 입력해주세요!");
        return;
    }

    try {
        let response = await fetch(`/api/creator/dashboard/reward/deliveries/${rewardDeliveryId}`, {
            method: "POST",
            headers: { "Content-Type": "application/json" },
            body: JSON.stringify({ courier, trackingNumber })
        });

        if (!response.ok) throw new Error(`HTTP 오류 발생: ${response.status}`);

        console.log(`✅ 배송 처리 완료: ${rewardDeliveryId}`);

        // UI 업데이트
        // 🚀 1) 개별 UI 업데이트 (전체 렌더링 대신 필드만 변경)
        row.querySelector(".tracking-number-input").replaceWith(document.createElement("span"));
        row.querySelector(".courier-select").replaceWith(document.createElement("span"));

        row.children[9].textContent = trackingNumber; // 송장번호 업데이트
        row.children[8].textContent = getCourierName(courier); // 택배사 업데이트

        row.children[10].innerHTML = `<button class="btn btn-secondary" onclick="editInvoice(${rewardDeliveryId})">송장 수정</button>`;

        // 🚀 2) 배송 상태 업데이트
        let deliveryStatus = await fetchDeliveryStatus(trackingNumber, courier);
        row.children[11].textContent = deliveryStatus; // 배송 상태 업데이트
    } catch (error) {
        console.error("❌ 배송 처리 실패:", error);
    }
}

// ✅ 상태 카드 값 업데이트
function updateRewardDeliveryCounts(counts) {
    console.log("📊 배송 상태 업데이트:", counts);

    // ✅ 요소가 존재하는 경우에만 업데이트 (오류 방지)
    document.getElementById("overdueCount").textContent = counts.overdue || 0;
    document.getElementById("autoProcessCount").textContent = counts.autoProcess || 0;
    document.getElementById("newOrdersCount").textContent = counts.newOrders || 0;
    document.getElementById("readyToShipCount").textContent = counts.readyToShip || 0;
    document.getElementById("shipmentD1Count").textContent = counts.shipmentD1 || 0;
    document.getElementById("shipmentDdayCount").textContent = counts.shipmentDday || 0;
}

function formatDate(date) {
    if (!date || isNaN(date.getTime())) return '-';
    return date.toISOString().split("T")[0]; // YYYY-MM-DD 형식 반환
}


// ✅ 상태 카드 값 업데이트 (오류 방지)
function updateRewardDeliveryCounts(counts) {
    console.log("📊 배송 상태 업데이트:", counts);

    // ✅ 요소가 존재하는 경우에만 업데이트 (오류 방지 추가)
    const elements = {
        overdueCount: document.getElementById("overdueCount"),
        newOrdersCount: document.getElementById("newOrdersCount"),
        readyToShipCount: document.getElementById("readyToShipCount"),
        shipmentD1Count: document.getElementById("shipmentD1Count"),
        shipmentDdayCount: document.getElementById("shipmentDdayCount"),
        shippedCount: document.getElementById("shippedCount")
    };

    for (let key in elements) {
        if (elements[key]) {
            elements[key].textContent = counts[key] || 0;
        } else {
            console.warn(`⚠️ 경고: ${key} 요소를 찾을 수 없습니다.`);
        }
    }
}

function openBatchInvoiceModal() {
    let modal = document.getElementById("batchInvoiceModal");
    console.log("✅ batchInvoiceModal을 염");
    modal.style.display = "block"; // 모달 보이게 설정
}


function closeBatchInvoiceModal() {
    let modal = document.getElementById("batchInvoiceModal");
    if (modal) {
        modal.style.display = "none";
    }
}


async function downloadOrderExcel() {
    console.log("📂 발주 내역 다운로드 시작...");

    // ✅ 테이블이 존재하는지 확인
    const table = document.querySelector("#delivery-table-body");
    if (!table) {
        alert("⚠ 다운로드할 데이터가 없습니다. (테이블이 존재하지 않음)");
        return;
    }

    const tableRows = table.querySelectorAll("tr");
    if (tableRows.length === 0) {
        alert("⚠ 다운로드할 데이터가 없습니다.");
        return;
    }

    const wb = XLSX.utils.book_new();

    // 📌 1. 발주 내역 시트 데이터 생성 (순수 데이터만 포함)
    const wsData = [
        ["캠페인 제목", "기부번호", "기부자명", "기부자연락처", "기부자주소", "리워드 내역", "택배사", "송장번호"]
    ];

    tableRows.forEach(row => {
        const columns = row.querySelectorAll("td");

        if (columns.length < 10) {
            console.warn("⚠️ 예상한 테이블 구조와 다름:", row);
            return;
        }

        let donorAddress = columns[5]?.innerText.replace("#", "\n") || "-";
        let rewardInfo = columns[6]?.innerText.replace(/x\d+/, "").trim() || "-";

        let courierElement = columns[8]?.querySelector("select") || columns[8]?.querySelector("span");
        let courier = courierElement?.tagName === "SELECT"
            ? courierElement.options[courierElement.selectedIndex]?.text
            : courierElement?.innerText.trim() || "";
        if (courier === "택배사 선택") courier = "";

        let trackingNumberElement = columns[9]?.querySelector("input") || columns[9]?.querySelector("span");
        let trackingNumber = trackingNumberElement?.innerText.trim() || trackingNumberElement?.value.trim() || "";

        wsData.push([
            columns[1]?.innerText || "-",
            columns[2]?.innerText || "-",
            columns[3]?.innerText || "-",
            columns[4]?.innerText || "-",
            donorAddress,
            rewardInfo,
            courier,
            trackingNumber
        ]);
    });

    const ws = XLSX.utils.aoa_to_sheet(wsData);
    XLSX.utils.book_append_sheet(wb, ws, "발주 내역");


    const today = new Date().toISOString().split("T")[0];
    const fileName = `${today}_발주내역.xlsx`;

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

            // ✅ 1. 헤더 행 찾기
            const headerIndex = jsonData.findIndex(row => row.includes("기부번호"));
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
                donationId: Number(rowData[1]?.toString().trim() || 0),
                invoice: `${getCourierCode(rowData[6]?.toString().trim())}#${rowData[7]?.toString().trim()}`
            })).filter(row => row.donationId && row.invoice);

            if (updateData.length === 0) {
                alert("⚠ 테이블에서 일치하는 데이터를 찾을 수 없습니다.");
                return;
            }

            // ✅ 3. 송장번호 + 배송 상태까지 한 번에 업데이트
            await updateInvoicesInDB(updateData);

            // ✅ 4. **한 번만 렌더링 (최적화)**
            await loadRewardDeliveries({}, true);

            // ✅ 5. 모달 닫기
            closeBatchInvoiceModal();

            // ✅ 6. 사용자에게 성공 메시지 (UI는 이미 업데이트됨)
            alert("✅ 송장번호가 정상적으로 등록되었습니다.");

        } catch (error) {
            console.error("❌ 엑셀 업로드 처리 중 오류 발생:", error);
            alert("❌ 엑셀 업로드 실패! 콘솔을 확인하세요.");
        }
    };

    reader.readAsArrayBuffer(fileInput.files[0]);
}

async function updateInvoicesInDB(data) {
    try {
        // 전달된 data는 이미 { donationId, invoice } 형태로 구성됨
        console.log("📡 송장번호 업데이트 요청 데이터:", data);

        let response = await fetch("/api/creator/dashboard/reward/deliveries/batch-update", {
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


// ✅ 리스너 중복 등록 방지 + 페이지 이동 후 재등록
document.addEventListener("reapplyEventListeners", () => {
    console.log("🔄 페이지 이동 감지: 이벤트 리스너 재등록");

    // ✅ 기존 이벤트 리스너 제거 후 다시 추가
    document.removeEventListener("click", handleButtonClick);
    document.addEventListener("click", handleButtonClick);

    // ✅ 기존 데이터가 있으면 재렌더링, 없으면 다시 불러오기
    if (cachedRewardDeliveries.length > 0) {
        updateRewardDeliveryCounts(getDeliveryCounts(cachedRewardDeliveries));
        renderRewardDeliveries(cachedRewardDeliveries);
    } else {
        loadRewardDeliveries({}, true);
    }
});

function handleButtonClick(event) {
    const target = event.target;

    // ✅ 클릭된 버튼이 존재하는지 확인 (예외 처리)
    if (!target) return;

    // ✅ "발송처리" 버튼이 클릭된 경우 실행
    if (target.classList.contains("btn-primary")) {
        const row = target.closest("tr");

        // ✅ 행이 존재하는지 확인 후 진행
        if (!row) {
            console.warn("⚠️ 버튼이 테이블 행 내부에 없습니다.");
            return;
        }

        const deliveryId = row.querySelector(".delivery-checkbox")?.value;

        // ✅ deliveryId가 존재할 경우에만 실행
        if (deliveryId) {
            processDelivery(deliveryId);
        } else {
            console.warn("⚠️ 배송 ID를 찾을 수 없습니다.");
        }
    }
}


// ✅ 페이지가 로드될 때 `initRewardDeliveryManagement()` 실행
document.addEventListener("DOMContentLoaded", () => {
    initRewardDeliveryManagement();
});

