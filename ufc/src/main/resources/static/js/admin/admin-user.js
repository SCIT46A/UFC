document.addEventListener("DOMContentLoaded", function () {
    const contentArea = document.getElementById("content");

    setTimeout(() => {
        const loadingScreen = document.getElementById("loading");

        if (!loadingScreen) {
            console.error("🚨 'loading' 요소를 찾을 수 없습니다. 'loading.html'이 포함되었는지 확인하세요!");
            return;
        }

        const menuItems = document.querySelectorAll(".main-admin-select a");

        menuItems.forEach((menuItem) => {
            menuItem.addEventListener("click", function (event) {
                event.preventDefault();
                const page = this.getAttribute("data-page");

                loadCampaignStyles();

                console.log(`📢 선택된 페이지: ${page}`);

                loadingScreen.style.display = "flex"; // 🔥 로딩 화면 표시

                let fetchPromise;

                if (page === "campaign-status") {
                    fetchPromise = fetchCampaignStatus();
                } else if (page === "creator-report") {
                    fetchPromise = fetchCreatorReport();
                } else if (page === "creator-approval") {
                    fetchPromise = fetchCreatorApproval();
                } else if (page === "creator-status") {
                    fetchPromise = fetchCreatorStatus();
                } else if (page === "user-report") {
                    fetchPromise = fetchUserReport();
                } else if (page === "notice") {
                    fetchPromise = fetchNotice();
                    loadNoticeStyles();
                } else {
                    contentArea.innerHTML = generatePageContent(page);
                    fetchPromise = Promise.resolve();
                }

                fetchPromise.finally(() => loadingScreen.style.display = "none"); // 🔥 이제 정상 작동!
            });
        });
    }, 500);
});


// 1. 캠페인 운영 현황 함수
let allCampaigns = [];
let allCampaignGoals = [];
let allMaterialDonations = [];
function fetchCampaignStatus() {
    return Promise.all([
        fetch("/api/admin/campaign-status").then(res => res.json()),
        fetch("/api/admin/campaign-goals").then(res => res.json()),
        fetch("/api/admin/material-donations").then(res => res.json())
    ])
        .then(([campaigns, campaignGoals, materialDonations]) => {
            allCampaigns = campaigns || [];
            allCampaignGoals = campaignGoals || [];
            allMaterialDonations = materialDonations || [];
            document.getElementById("content").innerHTML = generateCampaignStatusTable(allCampaigns, allCampaignGoals, allMaterialDonations);
            setTimeout(updateCampaignCounts, 100);
        });
}

// 캠페인 개수 업데이트 (4개 상태 반영)
function updateCampaignCounts() {
    const ongoingCountElem = document.getElementById("ongoing-count");
    const pendingCountElem = document.getElementById("pending-count");
    const completedCountElem = document.getElementById("completed-count");
    const preparedCountElem = document.getElementById("prepared-count");

    if (!ongoingCountElem || !pendingCountElem || !completedCountElem || !preparedCountElem) {
        console.error("⛔ 캠페인 카운트 요소를 찾을 수 없습니다.");
        return;
    }

    const now = new Date().getTime();
    let ongoingCount = 0, pendingCount = 0, completedCount = 0, preparedCount = 0;

    allCampaigns.forEach(campaign => {

        const startDate = new Date(campaign.startDate).getTime();
        const endDate = new Date(campaign.endDate).getTime();

        if (campaign.campaignStatus === 0) {
            pendingCount++; // 승인 대기
        } else if (startDate > now) {
            preparedCount++; // 펀딩 대기
        } else if (startDate <= now && now <= endDate) {
            ongoingCount++; // 진행 중
        } else if (endDate < now) {
            completedCount++; // 종료됨
        }
    });

    ongoingCountElem.textContent = `${ongoingCount}건`;
    pendingCountElem.textContent = `${pendingCount}건`;
    completedCountElem.textContent = `${completedCount}건`;
    preparedCountElem.textContent = `${preparedCount}건`; // 🔥 추가된 카드 반영

    console.log(`📢 캠페인 개수 업데이트 완료: 진행(${ongoingCount}), 대기(${pendingCount}), 종료(${completedCount}), 펀딩 대기(${preparedCount})`);
}


// 현재 필터 상태 저장
let currentFilter = "ongoing";
// 카드 클릭 시 필터링
function filterCampaigns(type) {
    let filteredCampaigns = [];
    const now = new Date();

    if (!allCampaignGoals || !allMaterialDonations) {
        console.warn("데이터 로딩 중... 필터링을 나중에 다시 실행하세요.");
        return;
    }

    currentFilter = type;

    // 모든 카드의 기존 선택 효과 제거
    document.querySelectorAll(".tracking-card").forEach(card => card.classList.remove("selected-card"));

    // 현재 선택한 카드에 테두리 효과 추가
    const selectedCard = document.getElementById(`${type}-card`);
    if (selectedCard) {
        selectedCard.classList.add("selected-card");
    }

    // 카드를 클릭하면 날짜 필터를 초기화
    resetFilterUI();

    if (type === "pending") {
        filteredCampaigns = allCampaigns.filter(campaign => campaign.campaignStatus === 0);
        document.getElementById("table-container").innerHTML = generatePendingCampaignTable(filteredCampaigns);
        return;
    }

    if (type === "ongoing") {
        filteredCampaigns = allCampaigns.filter(campaign => {
            const startDate = new Date(campaign.startDate);
            const endDate = new Date(campaign.endDate);
            return campaign.campaignStatus === 1 && startDate <= now && now <= endDate;
        });
    } else if (type === "completed") {
        filteredCampaigns = allCampaigns.filter(campaign => {
            const endDate = new Date(campaign.endDate);
            return campaign.campaignStatus === 1 && endDate < now;
        });
    } else if (type === "prepared") {
        filteredCampaigns = allCampaigns.filter(campaign => {
            const startDate = new Date(campaign.startDate);
            return campaign.campaignStatus === 1 && startDate > now;
        });
    }

    document.getElementById("table-container").innerHTML = generateCampaignTable(filteredCampaigns);
    updateCampaignCounts();
}



// 캠페인 현황 페이지 CSS 로드
function loadCampaignStyles() {
    let existingLink = document.getElementById("campaign-css");
    if (!existingLink) {
        let link = document.createElement("link");
        link.id = "campaign-css";
        link.rel = "stylesheet";
        link.href = "/css/admin/campaignStatus.css";
        document.head.appendChild(link);
    }
}


// 캠페인 현황 페이지 테이블 생성 (카드 4개 추가)
function generateCampaignStatusTable(campaigns) {
    return `
        <section class="delivery-tracking">
            <div class="tracking-header">
                <span class="text-red">캠페인 운영 현황을 확인하세요!</span>
                <span class="text-green">연도와 분기를 선택하여 캠페인을 검색하세요!</span>
            </div>

            <div class="tracking-grid">
                <div class="tracking-card" id="ongoing-card" onclick="filterCampaigns('ongoing')">
                    <div class="card-content">
                        <span>진행 중인 캠페인</span>
                    </div>
                    <div class="count" id="ongoing-count">0건</div>
                </div>

                <div class="tracking-card" id="pending-card" onclick="filterCampaigns('pending')">
                    <div class="card-content">
                        <span>승인 대기 캠페인</span>
                    </div>
                    <div class="count" id="pending-count">0건</div>
                </div>
                
                
                <div class="tracking-card" id="prepared-card" onclick="filterCampaigns('prepared')"> 
                    <div class="card-content">
                        <span>펀딩 대기 캠페인</span> 
                    </div>
                    <div class="count" id="prepared-count">0건</div>
                </div>

                <div class="tracking-card" id="completed-card" onclick="filterCampaigns('completed')">
                    <div class="card-content">
                        <span>종료된 캠페인</span>
                    </div>
                    <div class="count" id="completed-count">0건</div>
                </div>
            </div>

            ${generateFilterUI()}  
            <div id="table-container">
                ${generateCampaignTable(campaigns)}
            </div>
        </section>
    `;
}
//날짜
function generateFilterUI() {
    const currentYear = new Date().getFullYear();

    return `
        <div class="filter-container">
            <label for="year-select">연도:</label>
            <select id="year-select">
                ${Array.from({ length: 5 }, (_, i) => `<option value="${currentYear - i}">${currentYear - i}</option>`).join('')}
            </select>

            <label for="quarter-select">분기:</label>
            <select id="quarter-select">
                <option value="1">1분기 (1~3월)</option>
                <option value="2">2분기 (4~6월)</option>
                <option value="3">3분기 (7~9월)</option>
                <option value="4">4분기 (10~12월)</option>
            </select>

            <button onclick="filterCampaignsByDate()">검색</button>
        </div>
    `;
}


function filterCampaignsByDate() {
    const selectedYear = parseInt(document.getElementById("year-select").value);
    const selectedQuarter = parseInt(document.getElementById("quarter-select").value);

    // 분기 시작 월과 종료 월 설정
    const quarterStartMonth = (selectedQuarter - 1) * 3;
    const quarterEndMonth = quarterStartMonth + 2;

    // 선택한 분기의 시작일과 종료일 설정
    const quarterStartDate = new Date(selectedYear, quarterStartMonth, 1);
    const quarterEndDate = new Date(selectedYear, quarterEndMonth + 1, 0, 23, 59, 59);

    console.log(`🔍 검색 범위: ${quarterStartDate.toISOString()} ~ ${quarterEndDate.toISOString()}`);
    console.log(`📌 현재 선택된 필터: ${currentFilter}`);


    let filteredCampaigns = allCampaigns.filter(campaign => {
        const startDate = new Date(campaign.startDate);
        const endDate = new Date(campaign.endDate);

        let matchesFilter = (currentFilter === "all");

        if (currentFilter === "ongoing") {
            matchesFilter = (startDate <= new Date() && endDate >= new Date());
        } else if (currentFilter === "pending") {
            matchesFilter = !campaign.campaignStatus;
        } else if (currentFilter === "completed") {
            matchesFilter = (endDate < new Date());
        } else if (currentFilter === "prepared") {
            matchesFilter = (campaign.campaignStatus === 1 && startDate > new Date());
        }


        // ✅ 날짜 필터링을 진행 중인 캠페인에도 정확히 적용
        let matchesDate = (
            startDate.getFullYear() === selectedYear &&
            startDate >= quarterStartDate &&
            startDate <= quarterEndDate
        );

        // 🛠 "ongoing" 캠페인의 경우에도 날짜 필터를 적용
        if (currentFilter === "ongoing") {
            return matchesFilter && matchesDate;
        }

        return matchesFilter && matchesDate;
    });

    // 승인 대기 캠페인 필터 시, 체크박스 포함된 테이블 사용
    if (currentFilter === "pending") {
        document.getElementById("table-container").innerHTML = generatePendingCampaignTable(filteredCampaigns);
    } else {
        document.getElementById("table-container").innerHTML = generateCampaignTable(filteredCampaigns, allCampaignGoals, allMaterialDonations);
    }

    console.log(`✅ 최종 필터링 결과: ${filteredCampaigns.length}개 캠페인 (연도: ${selectedYear}, 분기: ${selectedQuarter}, 필터: ${currentFilter})`);
}


function resetFilterUI() {
    setTimeout(() => {
        const yearSelect = document.getElementById("year-select");
        const quarterSelect = document.getElementById("quarter-select");

        if (!yearSelect || !quarterSelect) {
            console.warn("⏳ 필터 UI 요소가 아직 생성되지 않았습니다. 실행 중단.");
            return;
        }

        yearSelect.selectedIndex = 0;
        quarterSelect.selectedIndex = 0;
    }, 100);
}


function generateCampaignTable(campaigns) {
    if (campaigns.length === 0) {
        console.log("📢 캠페인이 존재하지 않음.");
        return `<div class="empty-message"><p>🚫 등록된 캠페인이 없습니다.</p></div>`;
    }

    console.log("📢 전체 캠페인 목록:", campaigns);
    console.log("📢 전체 캠페인 목표 목록:", allCampaignGoals);
    console.log("📢 전체 기부 데이터 목록:", allMaterialDonations);

    let tableHTML = `
        <div class="table-container">
            <table>
                <thead>
                    <tr>
                        <th style="width: 8%">캠페인 ID</th>
                        <th style="width: 25%">제목</th>
                        <th style="width: 12%">시작일</th>
                        <th style="width: 12%">종료일</th>
                        <th style="width: 8%">생성자</th>
                        <th style="width: 35%">펀딩 진행률 / 승인</th>
                    </tr>
                </thead>
                <tbody>
    `;

    campaigns.forEach(campaign => {
        console.log(`🔍 처리 중인 캠페인:`, campaign);

        // 안전한 검색을 위해 필터링 후 find 사용
        const goal = allCampaignGoals.find(goal => Number(goal?.campaign?.campaignId) === Number(campaign.campaignId));


        console.log(`🎯 찾은 목표(goal) 정보 (campaignId=${campaign.campaignId}):`, goal);

        // 승인된 기부 데이터 필터링 및 합산
        const donations = allMaterialDonations
            .filter(donation => donation.campaign && donation.campaign.campaignId === Number(campaign.campaignId) && donation.status === "approved")
            .reduce((sum, donation) => sum + donation.quantity, 0);

        console.log(`💰 승인된 기부 총량 (campaignId=${campaign.campaignId}):`, donations);

        const goalQuantity = goal ? goal.quantityRequired : 0;
        const fundingPercentage = goalQuantity > 0 ? Math.min(100, (donations / goalQuantity) * 100) : 0;

        console.log(`📊 펀딩 목표량: ${goalQuantity}, 현재 기부량: ${donations}, 펀딩 퍼센트: ${fundingPercentage}%`);

        let fundingStatusHTML = `
            <div class="progress-bar-container">
                <div class="progress-bar" style="width: ${fundingPercentage}%; background-color: #16A34A;"></div>
                <span class="progress-text">${fundingPercentage.toFixed(1)}%</span>
            </div>
        `;

        // 대기 중 캠페인일 경우 승인 및 거부 버튼 표시
        if (!campaign.campaignStatus) {
            fundingStatusHTML = `
        <button class="approve-btn" onclick="approveCampaign(${campaign.campaignId})">승인</button>
        <button class="reject-btn" onclick="openPopup(${campaign.campaignId})">거부</button>
    `;
        }


        tableHTML += `
            <tr>
                <td>${campaign.campaignId}</td>
                <td>
                    <a href="/campaign/${campaign.campaignId}" target="_blank" rel="noopener noreferrer" style="text-decoration: none; color: blue;">
                        ${campaign.title}
                    </a>
                </td>
                <td>${new Date(campaign.startDate).toLocaleDateString()}</td>
                <td>${new Date(campaign.endDate).toLocaleDateString()}</td>
                <td>${campaign.createdBy ? campaign.createdBy.creatorId : "정보 없음"}</td>
                <td>${fundingStatusHTML}</td>
            </tr>
        `;
    });

    tableHTML += `</tbody></table></div>`;
    return tableHTML;
}


function generatePendingCampaignTable(campaigns) {
    if (campaigns.length === 0) {
        return `<div class="empty-message"><p>🚫 승인 대기 중인 캠페인이 없습니다.</p></div>`;
    }

    let tableHTML = `
        <div class="table-container">
            <div class="table-header">
                <button onclick="approveSelectedCampaigns()" class="bulk-approve-btn">선택된 캠페인 승인</button>
            </div>
            <table>
                <thead>
                    <tr>
                        <th><input type="checkbox" id="select-all" onclick="toggleAllCheckboxes(this)"></th>
                        <th>캠페인 ID</th>
                        <th>제목</th>
                        <th>시작일</th>
                        <th>종료일</th>
                        <th>생성자</th>
                        <th>승인</th>
                        <th>거부</th>
                    </tr>
                </thead>
                <tbody>
    `;

    campaigns.forEach(campaign => {
        tableHTML += `
            <tr id="campaign-row-${campaign.campaignId}">
                <td><input type="checkbox" class="campaign-checkbox" value="${campaign.campaignId}"></td>
                <td>${campaign.campaignId}</td>
              <td>
                    <a href="/campaign/${campaign.campaignId}" target="_blank" rel="noopener noreferrer" style="text-decoration: none; color: blue;">
                        ${campaign.title}
                    </a>
                </td>      
                <td>${new Date(campaign.startDate).toLocaleDateString()}</td>
                <td>${new Date(campaign.endDate).toLocaleDateString()}</td>
                <td>${campaign.createdBy ? campaign.createdBy.creatorId : "정보 없음"}</td>
                <td><button class="approve-btn" onclick="approveCampaign(${campaign.campaignId})">승인</button></td> 
                <td><button class="reject-btn" onclick="openPopup(${campaign.campaignId})">거부</button></td>
            </tr>
        `;
    });

    tableHTML += `</tbody></table></div>`;

    return tableHTML;
}

//거부 사유 입력 팝업
function openPopup(campaignId) {
  let popup = document.getElementById('popup');

    if (!popup) {
        document.body.insertAdjacentHTML("beforeend", `
            <div class="popup-overlay" id="popupOverlay" onclick="closePopup()"></div>
            <div class="popup" id="popup">
                <p>거부 사유 입력:</p>
                <input type="text" id="reasonInput" placeholder="거부 사유">
                <div class="popup-buttons">
                    <button id="confirmButton">확인</button>  
                    <button onclick="closePopup()">취소</button>
                </div>
            </div>
        `);
        popup = document.getElementById('popup'); // 새로 추가한 요소 다시 가져오기
    }

    popup.dataset.campaignId = campaignId;
    document.getElementById('popup').style.display = 'block';
    document.getElementById('popupOverlay').style.display = 'block';

    // ✅ 기존 `onclick="saveReason(${campaignId})"`을 없애고 이벤트 리스너 추가
    document.getElementById('confirmButton').onclick = saveReason;
}



// 팝업 닫기
function closePopup() {
    document.getElementById('popup').style.display = 'none';
    document.getElementById('popupOverlay').style.display = 'none';
}
async function saveReason() {
    let reason = document.getElementById('reasonInput').value.trim();
    let popupElement = document.getElementById('popup');
    let campaignId = popupElement.dataset.campaignId;

    if (!reason) {
        alert("거부 사유를 입력하세요.");
        return;
    }

    const loadingScreen = document.getElementById("loading"); // 🔥 로딩 화면 가져오기
    loadingScreen.style.display = "flex"; // 🔥 로딩 시작

    try {
        let response = await fetch('/api/admin/rejected-reasons', {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify({ campaignId: Number(campaignId), reason })
        });

        let data = await response.json();

        if (!data.success) throw new Error(data.message);

        alert(`✅ 캠페인 ${campaignId} 거부 완료: ${reason}`);
        closePopup();

        await fetchCampaignStatus();
        await fetchPendingCampaigns();
    } catch (error) {
        console.error("❌ 거부 사유 저장 실패:", error);
        alert("거부 사유 저장 중 오류가 발생했습니다.");
    } finally {
        loadingScreen.style.display = "none"; // 🔥 요청이 끝나면 로딩 화면 숨김
    }
}


//토글 박스 다 체크
function toggleAllCheckboxes(selectAllCheckbox) {
    const checkboxes = document.querySelectorAll(".campaign-checkbox");
    checkboxes.forEach(checkbox => checkbox.checked = selectAllCheckbox.checked);
}

//캠페인 하나만 승인
function approveCampaign(campaignId) {
    const loadingScreen = document.getElementById("loading"); // 🔥 로딩 화면 가져오기
    loadingScreen.style.display = "flex"; // 🔥 로딩 시작

    fetch(`/api/admin/campaigns/${campaignId}/approve`, {
        method: "PUT",
        headers: { "Content-Type": "application/json" }
    })
        .then(response => response.json())
        .then(() => {
            alert("✅ 승인처리 되었습니다.");
            return fetchCampaignStatus();
        })
        .then(fetchPendingCampaigns)
        .finally(() => loadingScreen.style.display = "none"); // 🔥 요청이 끝나면 로딩 화면 숨김
}


//캠페인 여러개 승인
function approveSelectedCampaigns() {
    const selectedCheckboxes = document.querySelectorAll(".campaign-checkbox:checked");
    if (selectedCheckboxes.length === 0) {
        alert("승인할 캠페인을 선택하세요.");
        return;
    }

    const loadingScreen = document.getElementById("loading"); // 🔥 로딩 화면 가져오기
    loadingScreen.style.display = "flex"; // 🔥 로딩 시작

    const campaignIds = Array.from(selectedCheckboxes).map(checkbox => checkbox.value);

    fetch(`/api/admin/campaigns-approve`, {
        method: "PATCH",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify({ campaignIds })
    })
        .then(response => response.json())
        .then(() => {
            alert("✅ 승인처리 되었습니다.");
            return fetchCampaignStatus();
        })
        .then(fetchPendingCampaigns)
        .finally(() => loadingScreen.style.display = "none"); // 🔥 요청이 끝나면 로딩 화면 숨김
}


function fetchPendingCampaigns() {
    fetch("/api/admin/campaigns-pending")
        .then(response => response.json())
        .then(data => {
            console.log("📢 승인 대기 캠페인 목록:", data);
            document.getElementById("table-container").innerHTML = generatePendingCampaignTable(data);
        })
}


// 3. 창작자 신고 관리
function fetchCreatorReport() {
    return fetch("/api/admin/campaign-report")  // 🔥 `return` 추가하여 Promise 반환
        .then(response => response.json())
        .then(data => {
            if (!Array.isArray(data)) {
                throw new Error("서버에서 예상치 못한 응답을 받았습니다. (데이터 형식 오류)");
            }
            console.log("🚀 창작자 신고 API 응답 데이터:", data);
            allCampaignReports = data;
            renderCampaignTab('pending');
        })
        .catch(error => {
            console.error("❌ 창작자 신고 관리 목록 로드 오류:", error);
            document.getElementById("content").innerHTML = `
                <h2>오류 발생</h2>
                <p>데이터를 불러오는 중 오류가 발생했습니다.</p>
                <p>🚨 오류 메시지: ${error.message}</p>
            `;
        });
}


function generateCampaignReportTable(campaignReport, selectedTab = 'pending') {

    let tabHTML = `
    <h2>신고된 창작자 목록</h2>
        <p>신고된 창작자 정보를 확인하세요.</p>
        <div class="tab-container">
            <button class="tab-button ${selectedTab === 'pending' ? 'active' : ''}" onclick="renderCampaignTab('pending')">미처리 신고</button>
            <button class="tab-button ${selectedTab === 'resolved' ? 'active' : ''}" onclick="renderCampaignTab('resolved')">처리 완료 신고</button>
        </div>
    `;

    let filteredReports = campaignReport.filter(report =>
        (selectedTab === 'pending' && report.status === "registed") ||
        (selectedTab === 'resolved' && (report.status !== "registed"))
    );


    if (filteredReports.length === 0) {
        let noReportMessage = selectedTab === 'pending'
            ? "<p class='no-report'>✅ 현재 미처리된 신고가 없습니다.</p>"
            : "<p class='no-report'>✔ 처리 완료된 신고가 없습니다.</p>";
        return tabHTML + noReportMessage;
    }

    let tableHTML = `
        <div class="table-container">
            <table>
                <thead>
                    <tr>
                        <th>창작자 ID</th>
                        <th>캠페인 ID</th>
                        <th>캠페인 제목</th>
                        <th>신고 이유</th>
                        <th>신고자 ID</th>
                        <th>신고 날짜</th>
                        <th>조치</th>
                        <th>저장</th>
                    </tr>
                </thead>
                <tbody>
    `;

    filteredReports.forEach(report => {
        let actionColumn = "";
        let saveButtonColumn = "";

        if (report.status === "registed") {
            actionColumn = `
                <select id="action-${report.reportId}">
                    <option value="ok">조치 완료</option>
                    <option value="rejected">보류</option>
                </select>
            `;
            saveButtonColumn = `<button onclick="processUserReport(${report.reportId}, ${report.user.userId})">저장</button>`;

        } else {
            actionColumn = `<span>${report.status === "ok" ? "게시 정지됨" : report.status === "rejected" ? "보류됨" : "처리 완료"}</span>`;
            saveButtonColumn = "-";
        }

        tableHTML += `
        <tr>
            <td>${report.creatorId ? report.creatorId : 'N/A'}</td>
            <td>${report.campaignId}</td>
            <td>${report.title ? report.title : '데이터 없음'}</td>
            <td>${report.reason}</td>
            <td>${report.reportedBy ? report.reportedBy : '데이터 없음'}</td>
            <td>${report.reportedDate ? new Date(report.reportedDate).toLocaleDateString() : '날짜 없음'}</td>
            <td>${actionColumn}</td>
            <td>${saveButtonColumn}</td>
        </tr>
        `;
    });

    tableHTML += `</tbody></table></div>`;

    return tabHTML + tableHTML;
}


function renderCampaignTab(tabType) {
    const container = document.getElementById("content");

    if (!container) {
        console.error("❌ 오류: 'content' 요소를 찾을 수 없습니다.");
        return;
    }

    container.innerHTML = generateCampaignReportTable(allCampaignReports, tabType);
}


// 4. 창작자 승인 대기
// ✅ 창작자 승인 대기 목록 조회
function fetchCreatorApproval() {
    return fetch("/api/admin/creator-approval")
        .then(res => res.json())
        .then(creators => {
            console.log("📢 창작자 목록 응답 데이터:", creators);

            const verificationPromises = creators.map(creator =>
                fetch(`/api/admin/verify/${creator.creatorId}`, {
                    method: "POST",
                    headers: { "Content-Type": "application/json" }
                })
                    .then(res => res.json())
                    .then(verification => {
                        console.log(`📢 창작자 ${creator.creatorId} 검증 응답 데이터:`, verification);
                        const valid = verification.data?.[0]?.valid;
                        creator.verificationStatus = (valid === "01") ? "⭕" : "❌";
                    })
                    .catch(error => {
                        console.error(`❌ 창작자 ${creator.creatorId} 검증 오류:`, error);
                        creator.verificationStatus = "❌";
                    })
            );

            return Promise.all(verificationPromises).then(() => {
                document.getElementById("content").innerHTML = generateCreatorApprovalTable(creators);
            });
        })
        .catch(error => {
            console.error("❌ 창작자 승인 대기 목록 로드 오류:", error);
            document.getElementById("content").innerHTML = `<h2>오류 발생</h2><p>${error.message}</p>`;
        });
}

// ✅ 창작자 승인 대기 테이블 생성 (여러 개 선택 승인 추가)
function generateCreatorApprovalTable(creators) {
    console.log("📢 테이블 렌더링 시작. 현재 creators 데이터:", creators);

    let tabHTML = `
        <h2>창작자 승인 관리</h2>
        <p>창작자 승인 상태를 확인하세요.</p>
        <button onclick="approveSelectedCreators()" class="bulk-approve-btn">선택된 창작자 승인</button>
    `;

    let filteredCreators = creators.filter(creator => !creator.creatorStatus);

    if (filteredCreators.length === 0) {
        return tabHTML + "<p class='no-report'>✅ 현재 승인 대기 중인 창작자가 없습니다.</p>";
    }

    let tableHTML = `
        <div class="table-container">
            <table>
                <thead>
                    <tr>
                        <th><input type="checkbox" id="select-all" onclick="toggleAllCreatorCheckboxes(this)"></th>
                        <th>창작자 번호</th>
                        <th>사업자 이름</th>
                        <th>상호</th>
                        <th>사업자 등록번호</th>
                        <th>진위 여부</th> 
                        <th>승인</th>
                    </tr>
                </thead>
                <tbody>
    `;

    filteredCreators.forEach(creator => {
        let isVerified = creator.verificationStatus === "⭕";

        tableHTML += `
            <tr>
                <td><input type="checkbox" class="creator-checkbox" value="${creator.creatorId}" ${isVerified ? "" : "disabled"}></td>
                <td>${creator.creatorId}</td>
                <td>${creator.bname || "N/A"}</td>
                <td>${creator.companyName || "N/A"}</td>
                <td>${creator.bregistNumber || "N/A"}</td>
                <td>${creator.verificationStatus}</td> 
                <td><button ${isVerified ? "" : "disabled"} onclick="approveCreator(${creator.creatorId})">승인</button></td>
            </tr>
        `;
    });

    tableHTML += `</tbody></table></div>`;

    return tabHTML + tableHTML;
}


// ✅ 창작자 승인 처리 (단일 승인)
function approveCreator(creatorId) {
    fetch(`/api/admin/${creatorId}/approve`, {
        method: "PUT",
        headers: { "Content-Type": "application/json" }
    })
        .then(res => {
            if (!res.ok) {
                throw new Error(`HTTP error! Status: ${res.status}`);
            }
            return res.json();
        })
        .then(data => {
            console.log("✅ 승인 완료:", data);
            fetchCreatorApproval(); // ✅ 승인 후 목록 갱신
        })
        .catch(error => {
            console.error(`❌ 승인 요청 오류:`, error);
        });
}

// ✅ 여러 창작자 한 번에 승인
function approveSelectedCreators() {
    const selectedCheckboxes = document.querySelectorAll(".creator-checkbox:checked");
    if (selectedCheckboxes.length === 0) {
        alert("승인할 창작자를 선택하세요.");
        return;
    }

    const creatorIds = Array.from(selectedCheckboxes).map(checkbox => checkbox.value);

    fetch(`/api/admin/creators-approve`, {
        method: "PATCH",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify({ creatorIds })
    })
        .then(res => res.json())
        .then(() => {
            alert("✅ 선택된 창작자가 승인되었습니다.");
            fetchCreatorApproval(); // ✅ 승인 후 목록 갱신
        })
        .catch(error => {
            console.error("❌ 다중 승인 오류:", error);
        });
}

function toggleAllCreatorCheckboxes(source) {
    const checkboxes = document.querySelectorAll(".creator-checkbox");

    checkboxes.forEach(checkbox => {
        if (!checkbox.disabled) { // ❌ 비활성화된 항목 제외
            checkbox.checked = source.checked;
        }
    });
}
// 창작자 현황 리스트
function fetchCreatorStatus() {
    return fetch("/api/admin/creator-status")  // 🔥 `return` 추가하여 Promise 반환
        .then(res => res.json())
        .then(creators => {
            console.log("📢 전체 창작자 현황 데이터:", creators);

            // ✅ 승인된 창작자만 필터링
            const approvedCreators = creators.filter(creator => creator.creatorStatus === true);

            console.log("✅ 승인된 창작자 목록:", approvedCreators);
            document.getElementById("content").innerHTML = generateCreatorStatusTable(approvedCreators);
        })
        .catch(error => {
            console.error("❌ 창작자 현황 데이터 로드 오류:", error);
            document.getElementById("content").innerHTML = `<h2>오류 발생</h2><p>${error.message}</p>`;
        });
}


// ✅ 창작자 현황 테이블 생성
function generateCreatorStatusTable(creators) {
    if (!creators || creators.length === 0) {
        return `<h2>창작자 현황</h2><p>현재 등록된 창작자가 없습니다.</p>`;
    }

    let tableHTML = `
        <h2>창작자 현황</h2>
        <p>현재 등록된 창작자 목록을 확인하세요.</p>
        <div class="table-container">
            <table>
                <thead>
                    <tr>
                        <th>창작자 번호</th>
                        <th>이름</th>
                        <th>상호</th>
                      
                    </tr>
                </thead>
                <tbody>
    `;

    creators.forEach(creator => {
        const creatorStatus = creator.creatorStatus ? "⭕ 승인됨" : "❌ 미승인";

        tableHTML += `
            <tr>
                <td>${creator.creatorId}</td>
                <td>${creator.bname || "N/A"}</td>
                <td>${creator.companyName || "N/A"}</td>
              
            </tr>
        `;
    });

    tableHTML += `</tbody></table></div>`;
    return tableHTML;
}

//5. 유저 신고 관리
// 유저 신고 관리
let allUserReports = [];

function fetchUserReport() {
    return fetch("/api/admin/user-reports")
        .then(response => response.json())
        .then(data => {
            allUserReports = data;
            renderTab('pending');  // ✅ 데이터가 로드된 후 실행
        })
        .catch(error => {
            console.error("❌ 유저 신고 목록 로드 오류:", error);
        });
}


// ✅ 새벽 4시에 자동으로 정지 해제하는 함수
function clearUserSuspensions() {
    console.log("🚀 새벽 4시: 정지된 유저 상태 확인 중...");

    fetch("/api/admin/user-unban")
        .then(response => response.json())
        .then(data => {
            if (data.success) {
                console.log("✅ 정지된 유저가 해제되었습니다.");
            } else {
                console.warn("⚠️ 정지 해제 중 문제가 발생했습니다:", data.message);
            }
        })
        .catch(error => {
            console.error("❌ 유저 정지 해제 API 호출 오류:", error);
        });
}

// ✅ 매일 새벽 4시에 `clearUserSuspensions()` 실행
function scheduleUnbanTask() {
    const now = new Date();
    const targetTime = new Date();

    targetTime.setHours(4, 0, 0, 0); // 새벽 4시 설정

    if (now > targetTime) {
        // 현재 시간이 이미 4시 이후라면 다음 날 실행하도록 설정
        targetTime.setDate(targetTime.getDate() + 1);
    }

    const timeUntilExecution = targetTime - now;

    console.log(`⏳ 다음 유저 정지 해제 실행 시간: ${targetTime}`);

    setTimeout(() => {
        clearUserSuspensions(); // 처음 실행
        setInterval(clearUserSuspensions, 24 * 60 * 60 * 1000); // 매 24시간마다 실행
    }, timeUntilExecution);
}

// 유저 신고 관리 페이지 테이블 동적 생성
function generateUserReportTable(userReports, selectedTab = 'pending') {
    let tabHTML = `
    <h2>유저 신고 목록</h2>
        <p>신고된 유저 정보를 확인하고 처리할 수 있습니다.</p>
        <div class="tab-container">
            <button class="tab-button ${selectedTab === 'pending' ? 'active' : ''}" onclick="renderTab('pending')">미처리 신고</button>
            <button class="tab-button ${selectedTab === 'resolved' ? 'active' : ''}" onclick="renderTab('resolved')">처리 완료된 신고</button>
        </div>
    `;

    // ✅ 선택된 탭에 따라 필터링
    let filteredReports = userReports.filter(report =>
        (selectedTab === 'pending' && report.status === "registed") ||
        (selectedTab === 'resolved' && (report.status === "ok" || report.status === "rejected"))
    );

    // ✅ 신고가 없는 경우 안내 메시지 표시
    if (filteredReports.length === 0) {
        let noReportMessage = selectedTab === 'pending'
            ? "<p class='no-report'>✅ 현재 미처리된 신고가 없습니다.</p>"
            : "<p class='no-report'>✔ 처리 완료된 신고가 없습니다.</p>";
        return tabHTML + noReportMessage;
    }

    let tableHTML = `
        <div class="table-container">
            <table>
                <thead>
                    <tr>
                        <th>신고 번호</th>
                        <th>유저 ID</th>
                        <th>신고 사유</th>
                        <th>신고 날짜</th>
                        <th>신고자 ID</th>
                        <th>계정 상태</th>
                        <th>정지 기간</th>
                        <th>저장</th>
                    </tr>
                </thead>
                <tbody>
    `;

    filteredReports.forEach(report => {
        let banCount = allUserReports.filter(r => r.userId === report.userId && r.status === "ok").length;

        let accountStatus = "";
        let actionColumn = "";
        let saveButtonColumn = "";

        // 정지 남은 기간 계산
        let banEndDate = report.userUpdatedAt ? new Date(report.userUpdatedAt) : null;
        let today = new Date();
        today.setHours(0, 0, 0, 0);
        if (banEndDate) banEndDate.setHours(0, 0, 0, 0);
        let daysRemaining = banEndDate ? Math.ceil((banEndDate - today) / (1000 * 60 * 60 * 24)) : null;

        if (report.status === "registed") {
            accountStatus = `<span style="color: #CE201B;">🚨 신고</span>`;
            actionColumn = `
            <select id="action-${report.reportId}">
                <option value="3">3일 정지</option>
                <option value="5">5일 정지</option>
                <option value="100">영구 정지</option>
                <option value="rejected">보류</option>
            </select>
        `;
            saveButtonColumn = `<button onclick="processUserReport(${report.reportId}, ${report.user?.userId})">저장</button>`;


        } else if (report.status === "ok") {
            accountStatus = `<span style="color: green;">✅ 조치 완료</span>`;

            // 정지 남은 기간 표시
            if (daysRemaining !== null && daysRemaining > 0) {
                actionColumn = `<span style="color: red;">정지 ${daysRemaining}일 남음</span>`;
            } else {
                actionColumn = `<span style="color: gray;">정지 기간 만료</span>`;
            }

            saveButtonColumn = "-";

        } else if (report.status === "rejected") {
            accountStatus = `<span style="color: blue;">✔ 활동 중</span>`;
            actionColumn = "보류";
            saveButtonColumn = "-";
        }

        tableHTML += `
        <tr>
            <td>${report.reportId}</td>
            <td>${report.user.userId} (${banCount}회 정지됨)</td>  
            <td>${report.reason}</td>
            <td>${new Date(report.reportedDate).toLocaleDateString()}</td>
            <td>${report.reportedBy.userId}</td>
            <td>${accountStatus}</td>
            <td>${actionColumn}</td>
            <td>${saveButtonColumn}</td>
        </tr>
    `;
    });

    tableHTML += `</tbody></table></div>`;

    return tabHTML + tableHTML;
}



// ✅ 페이지가 로드되면 자동으로 실행
document.addEventListener("DOMContentLoaded", function () {
    scheduleUnbanTask();
});

function renderTab(tabType) {
    if (!allUserReports.length) {
        console.error("❌ 신고 데이터가 아직 로드되지 않았습니다.");
        return;
    }


    let filteredReports = [];

    if (tabType === 'pending') {
        filteredReports = allUserReports.filter(report => report.status === "registed");
    } else if (tabType === 'resolved') {
        filteredReports = allUserReports.filter(report => report.status === "ok" || report.status === "rejected");
    }

    document.getElementById("content").innerHTML = generateUserReportTable(filteredReports, tabType);
}


// ✅ 신고 처리 요청 (저장 버튼 클릭 시 실행)
function processUserReport(reportId, userId) {
    console.log(`📢 처리할 신고 ID: ${reportId}, 유저 ID: ${userId}`);

    if (!userId) {
        console.error(`❌ 오류: 신고 ID ${reportId}의 유저 ID가 없습니다.`);
        alert("오류: 유저 ID가 없습니다.");
        return;
    }

    const actionElement = document.getElementById(`action-${reportId}`);

    if (!actionElement) {
        console.error(`❌ 오류: 신고 ID ${reportId}에 대한 정지 기간 선택 요소를 찾을 수 없습니다.`);
        alert("오류: 정지 기간 선택 필드를 찾을 수 없습니다.");
        return;
    }

    const selectedValue = actionElement.value.trim();

    if (!selectedValue) {
        alert("정지 기간을 선택해주세요.");
        return;
    }

    // 선택된 값에 따른 분기 처리
    let isRejected = false;
    let banDays = 0;

    if (selectedValue === "rejected") {
        isRejected = true;
        // rejected인 경우 banDays는 서버에서 무시하도록 하거나 null/0로 전송
    } else {
        banDays = parseInt(selectedValue, 10);
    }

    console.log(`✅ 유저 ${userId} 조치: ${isRejected ? "보류" : banDays + "일 정지"}`);

    fetch("/api/admin/user-suspend", {
        method: "POST",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify({
            userId,
            banDays,
            reportId,
            isRejected
        })
    })
        .then(response => {
            if (!response.ok) {
                return response.text().then(errorText => {
                    throw new Error(`서버 오류: ${errorText}`);
                });
            }
            return response.json();
        })
        .then(data => {
            if (!data.success) throw new Error(data.message);
            alert("✅ 유저 정지 완료");
            fetchUserReport();
        })
        .catch(error => {
            alert(`❌ 유저 정지 처리 중 오류 발생: ${error.message}`);
            console.error(error);
        });
}


// ✅ 버튼 클릭 시 실행될 함수들
function updateReportStatus(userId, action) {
    alert(`유저 ${userId}에게 ${action} 조치를 했습니다.`);
}

// ✅ 공지사항 스타일 로드 (이미 로드된 경우 중복 방지)
function loadNoticeStyles() {
    if (!document.getElementById("notice-css")) {
        let link = document.createElement("link");
        link.id = "notice-css";
        link.rel = "stylesheet";
        link.href = "/css/admin/admin-notice.css"; // ✅ 공지사항 전용 CSS 경로
        document.head.appendChild(link);
    }
}
// ✅ 공지사항 데이터 가져와서 화면에 표시
function fetchNotice() {
    return fetch("/api/admin/notices")  // 🔥 `return` 추가하여 Promise 반환
        .then(response => {
            if (!response.ok) return response.text().then(err => { throw new Error(err); });
            return response.json();
        })
        .then(data => {
            if (!Array.isArray(data)) throw new Error("🚨 서버에서 예상치 못한 응답을 받았습니다.");
            document.getElementById("content").innerHTML = generateNoticeTable(data); // ✅ 리스트 갱신
        })
        .catch(error => {
            console.error("❌ 공지사항 목록 로드 오류:", error);
            document.getElementById("content").innerHTML = `<h2>오류 발생</h2><p>${error.message}</p>`;
        });
}


// ✅ 공지사항 테이블 생성 (제목 클릭 시 팝업)
function generateNoticeTable(notices) {
    let tableHTML = `
    <div class="notice-container">
        <h2>공지사항</h2>
        <div class="table-container">
            <table class="notice-table">
                <thead>
                    <tr>
                        <th style="width: 10%">번호</th>
                        <th style="width: 60%">제목</th>
                        <th style="width: 30%">등록 날짜</th>
                    </tr>
                </thead>
                <tbody>
                <!-- 공지사항 팝업 (페이지 어디든 위치 가능, body 내부여야 함) -->
<div id="notice-popup" class="popup" style="display: none;">
    <div class="popup-content">
        <span class="close" onclick="closeNoticePopup()">&times;</span>
        <h3 id="popup-title"></h3>
        <p id="popup-content"></p>
    </div>
</div>

    `;

    if (notices.length === 0) {
        tableHTML += `<tr><td colspan="3">🚫 등록된 공지사항이 없습니다.</td></tr>`;
    } else {
        notices.forEach(notice => {
            tableHTML += `
            <tr>
                <td>${notice.noticeId}</td>
                <td onclick="openNoticePopup('${encodeURIComponent(notice.title)}', '${encodeURIComponent(notice.content)}')">${notice.title}</td>
                <td>${new Date(notice.noticedDate).toLocaleDateString()}</td>
            </tr>
            `;
        });
    }

    tableHTML += `</tbody></table></div>`;

    // ✅ 공지사항 입력 폼을 하단에 추가
    tableHTML += `
        <div class="notice-input-container">
            <h3>📢 새 공지사항 작성</h3>
            <input type="text" id="notice-title" placeholder="제목을 입력하세요" required>
            <textarea id="notice-content" placeholder="내용을 입력하세요" required></textarea>
            <button onclick="submitNotice()">등록</button>
        </div>
    </div>`;

    return tableHTML;
}

// ✅ 공지사항 팝업 열기
function openNoticePopup(title, content) {
    document.getElementById("popup-title").textContent = decodeURIComponent(title);
    document.getElementById("popup-content").innerHTML = decodeURIComponent(content).replace(/\n/g, '<br>');
    document.getElementById("notice-popup").style.display = "block";
}

// ✅ 공지사항 팝업 닫기
function closeNoticePopup() {
    document.getElementById("notice-popup").style.display = "none";
}

// ✅ 공지사항 등록 요청
function submitNotice() {
    const titleInput = document.getElementById("notice-title");
    const contentInput = document.getElementById("notice-content");

    const title = titleInput.value.trim();
    const content = contentInput.value.trim();

    if (!title || !content) {
        alert("제목과 내용을 모두 입력하세요.");
        return;
    }

    fetch("/api/admin/notices/create", {
        method: "POST",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify({ title, content })
    })
        .then(response => {
            if (!response.ok) return response.text().then(err => { throw new Error(err); });
            return response.json();
        })
        .then(() => {
            alert("📢 공지사항이 등록되었습니다!");

            return fetchNotice();
        })
        .then(() => {
            // ✅ 공지사항 목록이 업데이트된 후 입력 필드 초기화
            titleInput.value = "";
            contentInput.value = "";
        })
        .catch(error => {
            console.error("❌ 공지사항 등록 오류:", error);
            alert("공지사항 등록 중 오류가 발생했습니다.");
        });
}


