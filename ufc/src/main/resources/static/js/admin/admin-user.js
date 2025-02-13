document.addEventListener("DOMContentLoaded", function () {
    const contentArea = document.getElementById("content");
    const menuItems = document.querySelectorAll(".main-admin-select a");

    menuItems.forEach((menuItem) => {
        menuItem.addEventListener("click", function (event) {
            event.preventDefault();
            const page = this.getAttribute("data-page");

            console.log(`📢 선택된 페이지: ${page}`);

            // 캠페인 운영 현황 (카운트 업데이트 필요)
            if (page === "campaign-status") {
                fetchCampaignStatus();
                loadCampaignStyles();
                setTimeout(updateCampaignCounts, 500); // ✅ 딜레이 추가
            }
            // 캠페인 승인 관리
            else if (page === "campaign-approval") {
                fetchCampaignApproval();
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
                throw new Error("🚨 서버에서 예상치 못한 응답을 받았습니다.");
            }

            // ✅ 데이터를 글로벌 변수에 저장
            allCampaigns = campaigns || [];
            allCampaignGoals = campaignGoals || [];
            allMaterialDonations = materialDonations || [];

            console.log("✅ 데이터 로드 완료:", allCampaigns, allCampaignGoals, allMaterialDonations);

            // ✅ 처음 페이지에서도 모든 데이터 전달
            document.getElementById("content").innerHTML = generateCampaignStatusTable(allCampaigns, allCampaignGoals, allMaterialDonations);

            // ✅ 요소가 생성된 후 updateCampaignCounts 실행 (100ms 지연)
            setTimeout(updateCampaignCounts, 100);
        })
        .catch(error => {
            console.error("❌ 캠페인 현황 데이터 로드 오류:", error);
            document.getElementById("content").innerHTML = `<h2>오류 발생</h2><p>${error.message}</p>`;
        });
}

let retryCount = 0;
function updateCampaignCounts() {
    const ongoingCountElem = document.getElementById("ongoing-count");
    const pendingCountElem = document.getElementById("pending-count");
    const completedCountElem = document.getElementById("completed-count");

    if (!ongoingCountElem || !pendingCountElem || !completedCountElem) {
        if (retryCount < 5) {  // 최대 5번까지만 재시도
            retryCount++;
            console.warn(`⏳ 캠페인 카운트 요소가 아직 생성되지 않았습니다. 500ms 후 재시도... (시도: ${retryCount}/5)`);
            setTimeout(updateCampaignCounts, 500);
        } else {
            console.error("🚨 캠페인 카운트 요소를 찾을 수 없습니다. 실행 중단.");
        }
        return;
    }

    if (!allCampaigns || allCampaigns.length === 0) {
        console.warn("📢 캠페인 데이터가 없습니다.");
        return;
    }

    const now = new Date().getTime();
    let ongoingCount = 0;
    let pendingCount = 0;
    let completedCount = 0;

    allCampaigns.forEach(campaign => {
        const startDate = new Date(campaign.startDate).getTime();
        const endDate = new Date(campaign.endDate).getTime();

        if (!campaign.campaignStatus) {
            pendingCount++;
        } else if (startDate <= now && now <= endDate) {
            ongoingCount++;
        } else if (endDate < now) {
            completedCount++;
        }
    });

    ongoingCountElem.textContent = `${ongoingCount}건`;
    pendingCountElem.textContent = `${pendingCount}건`;
    completedCountElem.textContent = `${completedCount}건`;

    console.log(`📢 캠페인 개수 업데이트 완료: 진행(${ongoingCount}), 대기(${pendingCount}), 종료(${completedCount})`);
}


// ✅ 현재 필터 상태 저장
let currentFilter = "ongoing"; // 기본값: 진행 중 캠페인

// 📌 진행 중, 대기 중, 종료된 캠페인 필터링
function filterCampaigns(type) {
    let filteredCampaigns = [];
    const now = new Date();

    if (!allCampaignGoals || !allMaterialDonations) {
        console.warn("⏳ 데이터 로딩 중... 필터링을 나중에 다시 실행하세요.");
        return;
    }

    currentFilter = type; // ✅ 현재 필터 상태 업데이트

    document.querySelectorAll(".tracking-card").forEach(card => card.classList.remove("active"));
    const selectedCard = document.getElementById(`${type}-card`);

    if (selectedCard) {
        selectedCard.classList.add("active");
    } else {
        console.warn(`🚨 ${type}-card 요소가 없음`);
        return;
    }

    if (type === "ongoing") {
        filteredCampaigns = allCampaigns.filter(campaign => {
            const startDate = new Date(campaign.startDate);
            const endDate = new Date(campaign.endDate);
            return startDate <= now && now <= endDate;
        });

        document.getElementById("table-container").innerHTML = generateOngoingCampaignTable(
            filteredCampaigns, allCampaignGoals, allMaterialDonations
        );

        return;
    }

    resetFilterUI();

    if (type === "pending") {
        filteredCampaigns = allCampaigns.filter(campaign => !campaign.campaignStatus);
    } else if (type === "completed") {
        filteredCampaigns = allCampaigns.filter(campaign => {
            const endDate = new Date(campaign.endDate);
            return endDate < now;
        });
    }

    document.getElementById("table-container").innerHTML = generateCampaignTable(filteredCampaigns, allCampaignGoals, allMaterialDonations);
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


// 캠페인 현황 페이지 테이블 동적 생성
function generateCampaignStatusTable(campaigns, campaignGoals, materialDonations) {
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
                        <span>대기 중인 캠페인</span>
                    </div>
                    <div class="count" id="pending-count">0건</div>
                </div>

                <div class="tracking-card" id="completed-card" onclick="filterCampaigns('completed')">
                    <div class="card-content">
                        <span>종료된 캠페인</span>
                    </div>
                    <div class="count" id="completed-count">0건</div>
                </div>
            </div>

            ${generateFilterUI()}  <!-- ✅ 연도/분기 필터 추가 -->
            <div id="table-container">
                ${generateCampaignTable(campaigns, campaignGoals, materialDonations)}
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

// 📌 다른 버튼(진행/대기/종료)을 클릭할 때 필터 초기화
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
    }, 500);  // 500ms 후 실행하여 요소가 렌더링될 시간을 줌
}

// 📌 연도 & 분기를 기준으로 캠페인 필터링
// 📌 연도 & 분기를 기준으로 캠페인 필터링 (현재 선택된 상태 반영)
function filterCampaignsByDate() {
    const selectedYear = parseInt(document.getElementById("year-select").value);
    const selectedQuarter = parseInt(document.getElementById("quarter-select").value);

    const quarterStartMonth = (selectedQuarter - 1) * 3 + 1;
    const quarterEndMonth = quarterStartMonth + 2;

    const now = new Date();

    // ✅ 현재 필터 상태를 반영한 캠페인 목록 가져오기
    let filteredCampaigns = [];

    if (currentFilter === "ongoing") {
        filteredCampaigns = allCampaigns.filter(campaign => {
            const startDate = new Date(campaign.startDate);
            const endDate = new Date(campaign.endDate);
            return startDate <= now && now <= endDate;
        });
    } else if (currentFilter === "pending") {
        filteredCampaigns = allCampaigns.filter(campaign => !campaign.campaignStatus);
    } else if (currentFilter === "completed") {
        filteredCampaigns = allCampaigns.filter(campaign => {
            const endDate = new Date(campaign.endDate);
            return endDate < now;
        });
    } else {
        // 기본적으로 전체 목록
        filteredCampaigns = allCampaigns;
    }

    // ✅ 선택된 날짜 범위에 맞게 필터링 적용
    const dateFilteredCampaigns = filteredCampaigns.filter(campaign => {
        const startDate = new Date(campaign.startDate);
        const campaignYear = startDate.getFullYear();
        const campaignMonth = startDate.getMonth() + 1;

        return campaignYear === selectedYear && campaignMonth >= quarterStartMonth && campaignMonth <= quarterEndMonth;
    });

    // ✅ 필터링된 캠페인 테이블 렌더링
    document.getElementById("table-container").innerHTML = generateCampaignTable(dateFilteredCampaigns, allCampaignGoals, allMaterialDonations);

    console.log(`🔎 검색 결과: ${dateFilteredCampaigns.length}개 캠페인 (연도: ${selectedYear}, 분기: ${selectedQuarter}, 필터: ${currentFilter})`);
}




function generateCampaignTable(campaigns, campaignGoals = [], materialDonations = []) {
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
                        <th style="width: 35%">펀딩 진행률</th>
                    </tr>
                </thead>
                <tbody>
    `;

    campaigns.forEach(campaign => {
        const goal = campaignGoals.find(goal => goal.campaignId === campaign.campaignId);
        const donations = materialDonations
            .filter(donation => donation.campaignId === campaign.campaignId)
            .reduce((sum, donation) => sum + donation.quantity, 0);

        const goalQuantity = goal ? goal.quantityRequired : 0;
        const fundingPercentage = goalQuantity > 0 ? Math.min(100, (donations / goalQuantity) * 100) : 0;

        console.log(`📢 캠페인 [ID: ${campaign.campaignId}] 목표: ${goalQuantity}, 기부된 수량: ${donations}, 진행률: ${fundingPercentage}%`);

        let fundingStatusHTML = `
            <div class="progress-bar-container">
                <div class="progress-bar" style="width: ${fundingPercentage}%; background-color: #16A34A;"></div>
                <span class="progress-text">${fundingPercentage.toFixed(1)}%</span>
            </div>
        `;

        if (!campaign.campaignStatus) {
            fundingStatusHTML = `<span class="pending-text">승인 대기중</span>`;
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

// ✅ 종료된 캠페인 테이블 생성 (펀딩 진행률 → 펀딩 결과)
function generateCompletedCampaignTable(campaigns, campaignGoals, materialDonations) {
    if (campaigns.length === 0) {
        return `<div class="empty-message"><p>🚫 종료된 캠페인이 없습니다.</p></div>`;
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
                        <th style="width: 35%">펀딩 결과</th>
                    </tr>
                </thead>
                <tbody>
    `;

    campaigns.forEach(campaign => {
        const goal = campaignGoals.find(goal => goal.campaignId === campaign.campaignId);
        const donations = materialDonations
            .filter(donation => donation.campaignId === campaign.campaignId)
            .reduce((sum, donation) => sum + donation.quantity, 0);

        const goalQuantity = goal ? goal.quantityRequired : 0;
        const fundingPercentage = goalQuantity > 0 ? (donations / goalQuantity) * 100 : 0;
        const displayPercentage = Math.min(100, fundingPercentage); // 100% 이상일 경우 바 크기는 100% 유지
        const extraPercentage = fundingPercentage > 100 ? `${fundingPercentage.toFixed(1)}%` : `${displayPercentage.toFixed(1)}%`;

        tableHTML += `
            <tr>
                <td>${campaign.campaignId}</td>
                <td>${campaign.title}</td>
                <td>${new Date(campaign.startDate).toLocaleDateString()}</td>
                <td>${new Date(campaign.endDate).toLocaleDateString()}</td>
                <td>${campaign.createdById ? campaign.createdById : "정보 없음"}</td>
                <td>
                    <div class="progress-bar-container">
                        <div class="progress-bar" style="width: ${displayPercentage}%; background-color: #16A34A;"></div>
                        <span class="progress-text">${extraPercentage}</span>
                    </div>
                </td>
            </tr>
        `;
    });

    tableHTML += `</tbody></table></div>`;
    return tableHTML;
}

// ✅ 진행 중인 캠페인 테이블 생성
function generateOngoingCampaignTable(campaigns, campaignGoals, materialDonations) {
    if (campaigns.length === 0) {
        return `<div class="empty-message"><p>🚫 진행 중인 캠페인이 없습니다.</p></div>`;
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
                        <th style="width: 35%">펀딩 진행률</th>
                    </tr>
                </thead>
                <tbody>
    `;

    campaigns.forEach(campaign => {
        const goal = campaignGoals.find(goal => goal.campaignId === campaign.campaignId);
        const donations = materialDonations
            .filter(donation => donation.campaignId === campaign.campaignId)
            .reduce((sum, donation) => sum + donation.quantity, 0);

        const goalQuantity = goal ? goal.quantityRequired : 0;
        const fundingPercentage = goalQuantity > 0 ? Math.min(100, (donations / goalQuantity) * 100) : 0;

        tableHTML += `
            <tr>
                <td>${campaign.campaignId}</td>
                <td>${campaign.title}</td>
                <td>${new Date(campaign.startDate).toLocaleDateString()}</td>
                <td>${new Date(campaign.endDate).toLocaleDateString()}</td>
                <td>${campaign.createdById ? campaign.createdById : "정보 없음"}</td>
                <td>
                    <div class="progress-bar-container">
                        <div class="progress-bar" style="width: ${fundingPercentage}%; background-color: #16A34A;"></div>
                        <span class="progress-text">${fundingPercentage.toFixed(1)}%</span>
                    </div>
                </td>
            </tr>
        `;
    });

    tableHTML += `</tbody></table></div>`;
    return tableHTML;
}



// 2. 캠페인 승인 관리

// ✅ 캠페인 승인 대기 목록 조회
function fetchCampaignApproval() {
    fetch("/api/campaigns-pending")
        .then(response => {
            if (!response.ok) {
                return response.text().then(err => { throw new Error(err); });
            }
            return response.json();
        })
        .then(data => {
            if (!Array.isArray(data)) {
                throw new Error("서버에서 예상치 못한 응답을 받았습니다.");
            }
            document.getElementById("content").innerHTML = generateCampaignApprovalTable(data);

            console.log("✅ 캠페인 승인 대기 목록이 로드됨");


        })
        .catch(error => {
            console.error("❌ 캠페인 승인 대기 목록 로드 오류:", error);
            document.getElementById("content").innerHTML = `<h2>오류 발생</h2><p>${error.message}</p>`;
        });
}




// ✅ 캠페인 승인 요청
function approveCampaign(campaignId) {
    fetch(`/api/campaigns-pending/${campaignId}/approve`, {  // ✅ 올바른 엔드포인트 사용
        method: "PUT",
    })
        .then(response => {
            if (!response.ok) {
                return response.text().then(err => { throw new Error(err); });
            }
            return response.text();
        })
        .then(message => {
            alert(message);
            fetchCampaignApproval();  // ✅ 승인 후 목록 갱신
        })
        .catch(error => {
            console.error("❌ 캠페인 승인 오류:", error);
            alert("승인 중 오류가 발생했습니다.");
        });
}
// ✅ 캠페인 승인 대기 테이블 생성
function generateCampaignApprovalTable(campaigns) {
    if (!campaigns || campaigns.length === 0) {
        return `<h2>캠페인 승인 대기</h2><p>승인 대기 중인 캠페인이 없습니다.</p>`;
    }

    let tableHTML = `
        <h2>캠페인 승인 대기</h2>
        <div class="table-container">
            <table>
                <thead>
                    <tr>
                        <th>캠페인 ID</th>
                        <th>제목</th>
                        <th>시작일</th>
                        <th>종료일</th>
                        <th>생성일</th>
                        <th>생성자 ID</th>
                        <th>승인</th>
                    </tr>
                </thead>
                <tbody>
    `;

    campaigns.forEach(campaign => {
        tableHTML += `
            <tr>
                <td>${campaign.campaignId}</td>
                <td>${campaign.title}</td>
                <td>${campaign.startDate ? new Date(campaign.startDate).toLocaleDateString() : "N/A"}</td>
                <td>${campaign.endDate ? new Date(campaign.endDate).toLocaleDateString() : "N/A"}</td>
                <td>${campaign.createdDate ? new Date(campaign.createdDate).toLocaleDateString() : "N/A"}</td>
                <td>${campaign.createdById || "N/A"}</td>
                <td>
                    ${campaign.campaignStatus
            ? "✔ 승인 완료"
            : `<button class="approve-btn" onclick="approveCampaign(${campaign.campaignId})">승인</button>`
        }
                </td>
            </tr>
        `;
    });

    tableHTML += `</tbody></table></div>`;
    return tableHTML;
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
    fetch("/api/user-reports")  // 🚀 API 엔드포인트 호출 (서버에서 JSON 데이터를 반환)
        .then(response => response.json())
        .then(data => {
            document.getElementById("content").innerHTML = generateUserReportTable(data);
        })
        .catch(error => {
            console.error("❌ 유저 신고 목록 로드 오류:", error);
            document.getElementById("content").innerHTML = `<h2>오류 발생</h2><p>유저 신고 데이터를 불러오지 못했습니다.</p>`;
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
        const userStatus = report.userStatus ? "✅ 활동 중" : "❌ 정지 중";

        tableHTML += `
            <tr>
                <td>${report.reportId}</td>
                <td>${report.userId}</td>
                <td>${report.reason}</td>
                <td>${new Date(report.reportedDate).toLocaleDateString()}</td>
                <td>${report.reportedById}</td>
                <td>${userStatus}</td>
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
                    ${report.status === 'registed' ? `<input type="text" id="reason-${report.reportId}" placeholder="사유 입력">` : "-"}
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

// ✅ 유저 신고 처리 함수
function processUserReport(reportId) {
    const action = document.getElementById(`action-${reportId}`).value;
    const reason = document.getElementById(`reason-${reportId}`).value;

    fetch("/api/user-reports/action", {
        method: "POST",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify({ reportId, action, reason })
    })
        .then(response => response.text())
        .then(data => {
            alert(data);
            fetchUserReport(); // ✅ 업데이트 후 목록 새로고침
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
