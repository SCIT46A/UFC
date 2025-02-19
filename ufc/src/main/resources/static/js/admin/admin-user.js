document.addEventListener("DOMContentLoaded", function () {
    const contentArea = document.getElementById("content");
    const menuItems = document.querySelectorAll(".main-admin-select a");

    menuItems.forEach((menuItem) => {
        menuItem.addEventListener("click", function (event) {
            event.preventDefault();
            const page = this.getAttribute("data-page");

            console.log(`📢 선택된 페이지: ${page}`);
            // ✅ 모든 페이지에서 CSS를 미리 로드
            loadCampaignStyles(); // 캠페인 CSS 항상 로드

            // 캠페인 운영 현황 (카운트 업데이트 필요)
            if (page === "campaign-status") {
                fetchCampaignStatus();
                setTimeout(updateCampaignCounts, 500); // ✅ 딜레이 추가
            }

            // 캠페인 신고 관리
            else if (page === "campaign-report") {
                fetchCampaignReport();
            }
            // 창작자 승인 관리
            else if (page === "creator-approval") {
                fetchCreatorApproval();
            }
            // 유저 신고 관리
            else if (page === "user-report") {
                fetchUserReport();
            }
            // 공지사항
            else if (page === "notice") {
                fetchNotice();
                loadNoticeStyles(); // ✅ 공지사항 CSS 로드
            }
            else if (page === "notice-form") {
                loadNoticeStyles(); // ✅ 공지사항 CSS 로드
            }
            else {
                contentArea.innerHTML = generatePageContent(page);
            }
        });
    });
});


// 1. 캠페인 운영 현황 함수
let allCampaigns = [];
let allCampaignGoals = [];
let allMaterialDonations = [];
function fetchCampaignStatus() {
    Promise.all([
        fetch("/api/campaign-status").then(res => res.json()),
        fetch("/api/campaign-goals").then(res => res.json()),
        fetch("/api/material-donations").then(res => res.json())
    ])
        .then(([campaigns, campaignGoals, materialDonations]) => {
            if (!Array.isArray(campaigns) || !Array.isArray(campaignGoals) || !Array.isArray(materialDonations)) {
                throw new Error("서버에서 예상치 못한 응답을 받았습니다.");
            }

            // ✅ 데이터를 글로벌 변수에 저장
            allCampaigns = campaigns || [];
            allCampaignGoals = campaignGoals || [];
            allMaterialDonations = materialDonations || [];

            console.log("✅ 데이터 로드 완료:", allCampaigns, allCampaignGoals, allMaterialDonations);

            // ✅ 데이터가 로드된 후 테이블 생성
            document.getElementById("content").innerHTML = generateCampaignStatusTable(allCampaigns, allCampaignGoals, allMaterialDonations);

            // ✅ 요소가 생성된 후 updateCampaignCounts 실행 (100ms 지연)
            setTimeout(updateCampaignCounts, 100);
        })
        .catch(error => {
            console.error("❌ 캠페인 현황 데이터 로드 오류:", error);
            document.getElementById("content").innerHTML = `<h2>오류 발생</h2><p>${error.message}</p>`;
        });
}


// ✅ 캠페인 개수 업데이트 (4개 상태 반영)
function updateCampaignCounts() {
    const ongoingCountElem = document.getElementById("ongoing-count");
    const pendingCountElem = document.getElementById("pending-count");
    const completedCountElem = document.getElementById("completed-count");
    const preparedCountElem = document.getElementById("prepared-count"); // 🔥 추가된 카드

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
            preparedCount++; // 펀딩 대기 🔥 추가
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


// ✅ 현재 필터 상태 저장
let currentFilter = "ongoing"; // 기본값: 진행 중 캠페인
// ✅ 카드 클릭 시 필터링 (펀딩 대기 추가)
function filterCampaigns(type) {
    let filteredCampaigns = [];
    const now = new Date();

    if (!allCampaignGoals || !allMaterialDonations) {
        console.warn("데이터 로딩 중... 필터링을 나중에 다시 실행하세요.");
        return;
    }

    currentFilter = type;

    // ✅ 모든 카드의 기존 선택 효과 제거
    document.querySelectorAll(".tracking-card").forEach(card => card.classList.remove("selected-card"));

    // ✅ 현재 선택한 카드에 테두리 효과 추가
    const selectedCard = document.getElementById(`${type}-card`);
    if (selectedCard) {
        selectedCard.classList.add("selected-card");
    }

    // ✅ 카드를 클릭하면 날짜 필터를 초기화
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



// ✅ 캠페인 현황 페이지 CSS 로드
function loadCampaignStyles() {
    let existingLink = document.getElementById("campaign-css");
    if (!existingLink) {
        let link = document.createElement("link");
        link.id = "campaign-css";
        link.rel = "stylesheet";
        link.href = "/css/admin/campaignStatus.css";  // ✅ 파일 확장자 추가
        document.head.appendChild(link);
    }
}


// ✅ 캠페인 현황 페이지 테이블 생성 (카드 4개 추가)
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
                        <span>펀딩 대기 캠페인</span> <!-- 🔥 추가된 카드 -->
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

    // ✅ 분기 시작 월과 종료 월 설정
    const quarterStartMonth = (selectedQuarter - 1) * 3;
    const quarterEndMonth = quarterStartMonth + 2;

    // ✅ 선택한 분기의 시작일과 종료일 설정
    const quarterStartDate = new Date(selectedYear, quarterStartMonth, 1);
    const quarterEndDate = new Date(selectedYear, quarterEndMonth + 1, 0, 23, 59, 59);

    console.log(`🔍 검색 범위: ${quarterStartDate.toISOString()} ~ ${quarterEndDate.toISOString()}`);
    console.log(`📌 현재 선택된 필터: ${currentFilter}`);

    // ✅ 첫 검색 시에는 기본 필터를 all로 설정, 하지만 카드 클릭 후에는 유지
    if (currentFilter === "ongoing" && !document.querySelector(".tracking-card.active")) {
        currentFilter = "all";
    }

    console.log(`📌 적용할 필터 상태: ${currentFilter}`);

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

        let matchesDate = (startDate.getFullYear() === selectedYear &&
            startDate.getMonth() + 1 >= quarterStartMonth + 1 &&
            startDate.getMonth() + 1 <= quarterEndMonth + 1);

        return matchesFilter && matchesDate;
    });

    // ✅ 검색 결과를 테이블에 반영
    document.getElementById("table-container").innerHTML = generateCampaignTable(filteredCampaigns, allCampaignGoals, allMaterialDonations);

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

        yearSelect.selectedIndex = 0;  // ✅ 연도 선택 초기화
        quarterSelect.selectedIndex = 0;  // ✅ 분기 선택 초기화
    }, 100);  // 100ms 후 실행하여 요소가 렌더링될 시간을 줌
}


function generateCampaignTable(campaigns) {
    if (campaigns.length === 0) {
        return `<div class="empty-message"><p>🚫 등록된 캠페인이 없습니다.</p></div>`;
    }

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
        const goal = allCampaignGoals.find(goal => goal.campaignId === campaign.campaignId);
        const donations = allMaterialDonations
            .filter(donation => donation.campaignId === campaign.campaignId)
            .reduce((sum, donation) => sum + donation.quantity, 0);

        const goalQuantity = goal ? goal.quantityRequired : 0;
        const fundingPercentage = goalQuantity > 0 ? Math.min(100, (donations / goalQuantity) * 100) : 0;

        let fundingStatusHTML = `
            <div class="progress-bar-container">
                <div class="progress-bar" style="width: ${fundingPercentage}%; background-color: #16A34A;"></div>
                <span class="progress-text">${fundingPercentage.toFixed(1)}%</span>
            </div>
        `;

        // ✅ 대기 중 캠페인일 경우 승인 버튼 표시
        if (!campaign.campaignStatus) {
            fundingStatusHTML = `<button class="approve-btn" onclick="approveCampaign(${campaign.campaignId})">승인</button>`;
        }

        tableHTML += `
            <tr>
                <td>${campaign.campaignId}</td>
                <td>${campaign.title}</td>
                <td>${new Date(campaign.startDate).toLocaleDateString()}</td>
                <td>${new Date(campaign.endDate).toLocaleDateString()}</td>
                <td>${campaign.createdById ? campaign.createdById : "정보 없음"}</td>
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
                    </tr>
                </thead>
                <tbody>
    `;

    campaigns.forEach(campaign => {
        tableHTML += `
            <tr id="campaign-row-${campaign.campaignId}">
                <td><input type="checkbox" class="campaign-checkbox" value="${campaign.campaignId}"></td>
                <td>${campaign.campaignId}</td>
                <td>${campaign.title}</td>
                <td>${new Date(campaign.startDate).toLocaleDateString()}</td>
                <td>${new Date(campaign.endDate).toLocaleDateString()}</td>
                <td>${campaign.createdById || "정보 없음"}</td>
                <td><button class="approve-btn" onclick="approveCampaign(${campaign.campaignId})">승인</button></td>
            </tr>
        `;
    });

    tableHTML += `</tbody></table></div>`;

    return tableHTML;
}


function toggleAllCheckboxes(selectAllCheckbox) {
    const checkboxes = document.querySelectorAll(".campaign-checkbox");
    checkboxes.forEach(checkbox => checkbox.checked = selectAllCheckbox.checked);
}
function approveSelectedCampaigns() {
    const selectedCheckboxes = document.querySelectorAll(".campaign-checkbox:checked");

    if (selectedCheckboxes.length === 0) {
        alert("승인할 캠페인을 선택하세요.");
        return;
    }

    selectedCheckboxes.forEach(checkbox => {
        const campaignId = parseInt(checkbox.value);
        approveCampaign(campaignId);
    });
}

let allPendingCampaigns = [];
function approveCampaign(campaignId) {
    fetch(`/api/campaigns/${campaignId}/approve`, {
        method: "PUT",
        headers: { "Content-Type": "application/json" }
    })
        .then(response => response.json())
        .then(data => {
            console.log(`✅ 캠페인 ${campaignId} 승인 완료:`, data);

            // ✅ 승인 완료 후 알림 팝업
            alert("✅ 승인처리 되었습니다.");

            // ✅ 최신 데이터를 가져와 카드 숫자 업데이트!
            fetchCampaignStatus(); // 캠페인 전체 데이터를 다시 불러와서 카드 개수 업데이트
            fetchPendingCampaigns(); // 승인 대기 캠페인 목록 다시 불러오기
        })
        .catch(error => {
            console.error("❌ 캠페인 승인 오류:", error);
        });
}


function fetchPendingCampaigns() {
    fetch("/api/campaigns-pending")
        .then(response => response.json())
        .then(data => {
            allPendingCampaigns = data; // ✅ 데이터를 전역 변수에 저장
            document.getElementById("table-container").innerHTML = generatePendingCampaignTable(allPendingCampaigns);
        })
        .catch(error => {
            console.error("❌ 승인 대기 캠페인 불러오기 오류:", error);
        });
}



// 3. 캠페인 신고 관리
function fetchCampaignReport() {
    fetch("/api/campaign-report")
        .then(response => {
            if (!response.ok) {
                return response.text().then(err => { throw new Error(err); });
            }
            return response.json();
        })
        .then(data => {
            if (!Array.isArray(data)) {  // 🚀 데이터가 배열인지 확인
                throw new Error("서버에서 예상치 못한 응답을 받았습니다.");
            }
            document.getElementById("content").innerHTML = generateCampaignReportTable(data); // ✅ `data`를 함수에 전달
        })
        .catch(error => {
            console.error("❌ 캠페인 신고 관리 목록 로드 오류:", error);
            document.getElementById("content").innerHTML = `<h2>오류 발생</h2><p>${error.message}</p>`;
        });

}

// 캠페인 신고 관리 동적 생성
function generateCampaignReportTable(campaignReport) {
    if (!campaignReport || campaignReport.length === 0) {
        return `<h2>신고된 캠페인 목록</h2><p>등록된 신고가 없습니다.</p>`;
    }

    let tableHTML = `
        <h2>신고된 캠페인 목록</h2>
        <p>신고된 캠페인 정보를 확인하세요.</p>
        <div class="table-container">
            <table>
                <thead>
                    <tr>
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

    campaignReport.forEach(report => {
        tableHTML += `
            <tr>
                <td>${report.campaignId}</td>
                <td>${report.title}</td>
                <td>${report.reason}</td>
                <td>${report.reporterId}</td>
                <td>${new Date(report.date).toLocaleDateString()}</td>
                <td>
                    ${report.status === 'registed' ? `
                        <select id="action-${report.reportId}">
                            <option value="3">3일 정지</option>
                            <option value="5">5일 정지</option>
                            <option value="100">영구 정지</option>
                            <option value="rejected">반려</option>
                        </select>
                    ` : report.status === 'ok' ? "처리 완료" : "반려됨"}
                </td>
                <td>
                    ${report.status === 'registed' ? `<button onclick="processUserReport(${report.reportId})">저장</button>` : "-"}
                </td>
            </tr>
        `;
    });

    tableHTML += `</tbody></table></div>`;
    return tableHTML;
}


// 4. 창작자 승인 대기

// ✅ 창작자 승인 대기 목록 조회
function fetchCreatorApproval() {
    fetch("/api/creator-approval")
        .then(response => {
            if (!response.ok) {
                return response.text().then(err => {
                    throw new Error("서버 오류 발생: " + err);
                });
            }
            return response.json();
        })
        .then(data => {
            console.log("📢 API 원본 응답 데이터:", data);

            // 🔥 `ownUser.creators` 삭제 후 JSON 무결성 확인
            const cleanedData = data.map(creator => {
                if (creator.ownUser) {
                    // 🔥 ownUser가 비어 있으면 undefined 방지
                    if (creator.ownUser.creators) {
                        delete creator.ownUser.creators;
                    }
                } else {
                    creator.ownUser = null;  // JSON 형식 유지
                }
                return creator;
            });

            console.log("🚀 순환 참조 제거된 데이터:", cleanedData);

            document.getElementById("content").innerHTML = generateCreatorApprovalTable(cleanedData);
        })
        .catch(error => {
            console.error("❌ 창작자 승인 대기 목록 로드 오류:", error);
            document.getElementById("content").innerHTML = `<h2>오류 발생</h2><p>${error.message}</p>`;
        });
}


// 창작자 승인 대기 테이블 생성
function generateCreatorApprovalTable(creators) {
    if (!creators || creators.length === 0) {
        return `<h2>창작자 승인 대기</h2><p>승인 대기 중인 창작자가 없습니다.</p>`;
    }

    let tableHTML = `
        <h2>창작자 승인 대기</h2>
        <div class="table-container">
            <table>
                <thead>
                    <tr>
                      
                        <th>창작자 번호</th>
                        <th>사업자 이름</th>
                        <th>상호</th>
                        <th>사업자 등록번호</th>
                        <th>승인여부</th>
                        <th>승인</th>
                    </tr>
                </thead>
                <tbody>
    `;

    creators.forEach(creator => {
        const creatorStatus = creator.creatorStatus ? "승인됨" : "대기 중";  // 🔥 Boolean을 텍스트로 변환

        tableHTML += `
            <tr>
                
                <td>${creator.creatorId}</td>
                <td>${creator.bName || "N/A"}</td>
                <td>${creator.companyName || "N/A"}</td>
                <td>${creator.bRegistNumber || "N/A"}</td>
                <td>${creatorStatus}</td>
                <td><button onclick="approveCreator(${creator.creatorId})">승인</button></td>
            </tr>
        `;
    });

    tableHTML += `</tbody></table></div>`;
    return tableHTML;
}


//5. 유저 신고 관리


// 유저 신고 관리
function fetchUserReport() {
    fetch("/api/user-reports")
        .then(response => response.json())
        .then(data => {
            console.log("🚀 API 응답 데이터:", data); // ✅ userUpdatedAt 값 확인
            document.getElementById("content").innerHTML = generateUserReportTable(data);
        })
        .catch(error => {
            console.error("❌ 유저 신고 목록 로드 오류:", error);
        });

}

// 유저 신고 관리 페이지 테이블 동적 생성
function generateUserReportTable(userReports) {
    if (!userReports || userReports.length === 0) {
        return `<h2>유저 신고 목록</h2><p>등록된 신고가 없습니다.</p>`;
    }

    let tableHTML = `
        <h2>유저 신고 목록</h2>
        <p>신고된 유저 정보를 확인하고 처리할 수 있습니다.</p>
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
                        <th>조치</th>
                        <th>사유</th>
                        <th>저장</th>
                    </tr>
                </thead>
                <tbody>
    `;

    userReports.forEach(report => {
        let accountStatus = "";
        let actionColumn = "";
        let reasonColumn = "";
        let saveButtonColumn = "";

        let banEndDate = report.userUpdatedAt ? new Date(report.userUpdatedAt) : null;
        let today = new Date();

        // ✅ 날짜 비교를 위한 "연-월-일" 포맷으로 변환
        today.setHours(0, 0, 0, 0);
        if (banEndDate) {
            banEndDate.setHours(0, 0, 0, 0);
        }

        let daysRemaining = banEndDate ? Math.ceil((banEndDate - today) / (1000 * 60 * 60 * 24)) : null;

        console.log("🚀 Debug:", {
            reportId: report.reportId,
            updatedAt: report.updatedAt,
            banEndDate: banEndDate,
            today: today,
            daysRemaining: daysRemaining
        });

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
            reasonColumn = `<input type="text" id="reason-${report.reportId}" placeholder="사유 입력">`;
            saveButtonColumn = `<button onclick="processUserReport(${report.reportId})">저장</button>`;

        } else if (report.status === "ok") {
            accountStatus = `<span style="color: green;">✅ 조치 완료</span>`;

            if (daysRemaining !== null && daysRemaining > 0) {
                actionColumn = `<span style="color: red;">정지 ${daysRemaining}일 남음</span>`;
            } else {
                actionColumn = `<span style="color: gray;">정지 기간 만료</span>`;
            }

            reasonColumn = `<span>${report.statusReason ? report.statusReason : "-"}</span>`;

            saveButtonColumn = "-";

        } else if (report.status === "rejected") {
            accountStatus = `<span style="color: blue;">✔ 활동 중</span>`;
            actionColumn = "보류";
            reasonColumn = "-";
            saveButtonColumn = "-";
        }

        tableHTML += `
        <tr>
            <td>${report.reportId}</td>
            <td>${report.userId}</td>
            <td>${report.reason}</td>
            <td>${new Date(report.reportedDate).toLocaleDateString()}</td>
            <td>${report.reportedById}</td>
            <td>${accountStatus}</td>
            <td>${actionColumn}</td>
            <td>${reasonColumn}</td>
            <td>${saveButtonColumn}</td>
        </tr>
    `;
    });

    tableHTML += `</tbody></table></div>`;
    return tableHTML;
}


// ✅ 신고 처리 요청 (저장 버튼 클릭 시 실행)
function processUserReport(reportId) {
    const action = document.getElementById(`action-${reportId}`).value;
    const reason = document.getElementById(`reason-${reportId}`).value.trim();

    if (!action || action === "rejected") {
        alert("조치를 선택해주세요.");
        return;
    }

    if (!reason) {
        alert("사유를 입력해주세요.");
        return;
    }

    fetch("/api/user-reports/action", {
        method: "POST",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify({ reportId, action, reason }) // ✅ 사유 포함
    })
        .then(response => response.json())
        .then(data => {
            if (!data.success) throw new Error(data.message);
            alert(data.message);
            fetchUserReport(); // ✅ 신고 목록 갱신
        })
        .catch(error => {
            console.error("❌ 유저 신고 처리 오류:", error);
            alert("신고 처리 중 오류가 발생했습니다.");
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
    loadNoticeStyles(); // ✅ CSS 로드 추가

    fetch("/api/notices")
        .then(response => {
            if (!response.ok) return response.text().then(err => { throw new Error(err); });
            return response.json();
        })
        .then(data => {
            if (!Array.isArray(data)) throw new Error("🚨 서버에서 예상치 못한 응답을 받았습니다.");

            document.getElementById("content").innerHTML = generateNoticeTable(data); // ✅ 리스트 렌더링
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
    const title = document.getElementById("notice-title").value.trim();
    const content = document.getElementById("notice-content").value.trim();

    if (!title || !content) {
        alert("제목과 내용을 모두 입력하세요.");
        return;
    }

    fetch("/api/notices/create", {
        method: "POST",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify({ title, content })
    })
        .then(response => {
            if (!response.ok) return response.text().then(err => { throw new Error(err); });
            return response.json();
        })
        .then(() => {
            alert("공지사항이 등록되었습니다!");
            fetchNotice(); // ✅ 공지사항 목록 갱신
        })
        .catch(error => {
            console.error("❌ 공지사항 등록 오류:", error);
            alert("공지사항 등록 중 오류가 발생했습니다.");
        });

    // ✅ 입력 필드 초기화
    document.getElementById("notice-title").value = "";
    document.getElementById("notice-content").value = "";
}


// ✅ 다른 페이지 컨텐츠를 동적으로 생성하는 함수
function generatePageContent(page) {
    switch (page) {
        case "campaign-status":
            return `<h2>캠페인 운영 현황</h2><p>현재 진행 중인 캠페인을 확인하세요.</p>`;
        case "campaign-approval":
            return `<h2>캠페인 승인 관리</h2><p>승인 대기 중인 캠페인을 검토하세요.</p>`;
        case "campaign-report":
            return `<h2>캠페인 신고 관리</h2><p>신고된 캠페인을 검토하고 조치하세요.</p>`;
        case "creator-approval":
            return `<h2>창작자 승인 대기</h2><p>새로운 창작자 요청을 승인하세요.</p>`;
        case "notice":
            return `<h2>공지사항 관리</h2><p>새로운 공지사항을 등록하거나 수정할 수 있습니다.</p>`;
        default:
            return `<h2>관리 시스템</h2><p>좌측 메뉴에서 항목을 선택하세요.</p>`;
    }
}
