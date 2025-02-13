document.addEventListener("DOMContentLoaded", function () {
    const contentArea = document.getElementById("content");
    const menuItems = document.querySelectorAll(".main-admin-select a");

    menuItems.forEach((menuItem) => {
        menuItem.addEventListener("click", function (event) {
            event.preventDefault();
            const page = this.getAttribute("data-page");

            console.log(`📢 선택된 페이지: ${page}`);

            // 페이지는 서버에서 데이터를 가져와야 함
            //캠페인 운영 현황
            if (page === "campaign-status") {
                fetchCampaignStatus();
                loadCampaignStyles();
            //캠페인 승인 관리
            } else if (page === "campaign-approval") {
                fetchCampaignApproval();
                updateCampaignCounts();
                filterCampaigns();
            //캠페인 신고 관리
            } else if (page === "campaign-report") {
                fetchCampaignReport();
            //창작자 승인 관리
            } else if (page === "creator-approval") {
                fetchCreatorApproval();
            //유저 신고 관리
            } else if (page === "user-report") {
                fetchUserReport();
            // 공지사항
            } else if (page === "notice") {
                fetchNotice();
                loadNoticeStyles(); // ✅ 공지사항 CSS 로드
            } else if (page === "notice-form") {
                loadNoticeForm();
                loadNoticeStyles(); // ✅ 공지사항 CSS 로드
            } else {
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

            console.log("✅ 첫 화면 데이터 로드 완료!");
            console.log("📢 전체 캠페인:", allCampaigns);
            console.log("📢 캠페인 목표:", allCampaignGoals);
            console.log("📢 기부 데이터:", allMaterialDonations);

            // ✅ 모든 데이터를 전달하여 테이블 생성
            document.getElementById("content").innerHTML = generateCampaignStatusTable(allCampaigns, allCampaignGoals, allMaterialDonations);

            // ✅ 캠페인 개수 업데이트
            updateCampaignCounts();

            // ✅ 테이블 업데이트 (진행률 바 정상 출력 확인용)
            setTimeout(() => {
                document.getElementById("table-container").innerHTML = generateCampaignTable(allCampaigns, allCampaignGoals, allMaterialDonations);
            }, 100);

        })
        .catch(error => {
            console.error("❌ 캠페인 현황 데이터 로드 오류:", error);
            document.getElementById("content").innerHTML = `<h2>오류 발생</h2><p>${error.message}</p>`;
        });
}



// 캠페인 구분 별로 건수 카운트
function updateCampaignCounts() {
    if (!allCampaigns || allCampaigns.length === 0) {
        console.warn("📢 캠페인 데이터가 없습니다. updateCampaignCounts() 실행 취소.");
        return;
    }

    const now = new Date().getTime(); // ✅ 현재 시간을 UTC 밀리초로 변환
    console.log(`✅ 현재 UTC 시간: ${new Date(now).toISOString()}`);

    let ongoingCount = 0;
    let pendingCount = 0;
    let completedCount = 0;

    allCampaigns.forEach(campaign => {
        const startDate = new Date(campaign.startDate).getTime();
        const endDate = new Date(campaign.endDate).getTime();

        console.log(`✅ 캠페인 [ID: ${campaign.campaignId}] 시작: ${new Date(startDate).toISOString()}, 종료: ${new Date(endDate).toISOString()}, 현재: ${new Date(now).toISOString()}`);

        if (!campaign.campaignStatus) {
            pendingCount++;
        } else if (startDate <= now && now <= endDate) {
            console.log(`🎯 진행 중인 캠페인 발견! ID: ${campaign.campaignId}`);
            ongoingCount++;
        } else if (endDate < now) {
            completedCount++;
        }
    });

    // ✅ HTML에서 캠페인 개수 업데이트
    document.getElementById("ongoing-count").textContent = ongoingCount;
    document.getElementById("pending-count").textContent = pendingCount;
    document.getElementById("completed-count").textContent = completedCount;

    console.log(`📢 캠페인 개수 업데이트 완료: 진행(${ongoingCount}), 대기(${pendingCount}), 종료(${completedCount})`);
}




// 진행 중, 대기 중, 종료된 캠페인 구분
function filterCampaigns(type) {
    let filteredCampaigns = [];
    const now = new Date();

    if (!allCampaignGoals || !allMaterialDonations) {
        console.warn("⏳ 데이터 로딩 중... 필터링을 나중에 다시 실행하세요.");
        return;
    }

    if (type === "all") {
        filteredCampaigns = allCampaigns;
    } else if (type === "ongoing") {
        filteredCampaigns = allCampaigns.filter(campaign => {
            const startDate = new Date(campaign.startDate);
            const endDate = new Date(campaign.endDate);
            return startDate <= now && now <= endDate;
        });

        document.getElementById("table-container").innerHTML = generateOngoingCampaignTable(
            filteredCampaigns, allCampaignGoals, allMaterialDonations
        );

        console.log(`✅ 진행 중인 캠페인 개수: ${filteredCampaigns.length}`);
        return;
    } else if (type === "pending") {
        filteredCampaigns = allCampaigns.filter(campaign => campaign.campaignStatus === false || campaign.campaignStatus == null);
    } else if (type === "completed") {
        filteredCampaigns = allCampaigns.filter(campaign => {
            const endDate = new Date(campaign.endDate);
            return endDate < now;
        });

        document.getElementById("table-container").innerHTML = generateCompletedCampaignTable(
            filteredCampaigns, allCampaignGoals, allMaterialDonations
        );

        console.log(`✅ 종료된 캠페인 개수: ${filteredCampaigns.length}`);
        return;
    }

    console.log(`📢 필터링된 캠페인 개수 (${type}): ${filteredCampaigns.length}`);
    console.log("📢 필터링된 캠페인 목록:", filteredCampaigns);

    if (filteredCampaigns.length === 0) {
        document.getElementById("table-container").innerHTML = `<div class="empty-message"><p>🚫 등록된 캠페인이 없습니다.</p></div>`;
    } else {
        document.getElementById("table-container").innerHTML = generateCampaignTable(filteredCampaigns, allCampaignGoals, allMaterialDonations);
    }

    // ✅ 선택한 박스 활성화 스타일 적용
    document.querySelectorAll(".tracking-card").forEach(card => card.classList.remove("active"));
    document.getElementById(`${type}-card`).classList.add("active");
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
    let tableHTML = `
        <section class="delivery-tracking">
            <div class="tracking-header">
                <span class="text-red">캠페인 운영 현황을 확인하세요!</span>
                <span class="text-green">클릭하시면 캠페인 내역을 확인할 수 있어요!</span>
            </div>

            <div class="tracking-grid">
                <div class="tracking-card" id="ongoing-card" onclick="filterCampaigns('ongoing')">
                    <div class="card-content">
                        <div class="icon-wrapper gray">
                            <svg width="24" height="24" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                                <circle cx="12" cy="12" r="10"></circle>
                                <line x1="12" y1="8" x2="12" y2="12"></line>
                                <line x1="12" y1="16" x2="12.01" y2="16"></line>
                            </svg>
                        </div>
                        <span>진행 중인 캠페인</span>
                    </div>
                    <div class="count" id="ongoing-count">0<span>건</span></div>
                </div>

                <div class="tracking-card" id="pending-card" onclick="filterCampaigns('pending')">
                    <div class="card-content">
                        <div class="icon-wrapper gray">
                            <svg width="24" height="24" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                                <circle cx="12" cy="12" r="10"></circle>
                                <polyline points="12 6 12 12 16 14"></polyline>
                            </svg>
                        </div>
                        <span>대기 중인 캠페인</span>
                    </div>
                    <div class="count" id="pending-count">0<span>건</span></div>
                </div>

                <div class="tracking-card" id="completed-card" onclick="filterCampaigns('completed')">
                    <div class="card-content">
                        <div class="icon-wrapper green">
                            <svg width="24" height="24" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                                <rect x="3" y="3" width="18" height="18" rx="2" ry="2"></rect>
                                <line x1="16" y1="8" x2="8" y2="8"></line>
                                <line x1="16" y1="12" x2="8" y2="12"></line>
                                <line x1="16" y1="16" x2="8" y2="16"></line>
                            </svg>
                        </div>
                        <span>종료된 캠페인</span>
                    </div>
                    <div class="count" id="completed-count">0<span>건</span></div>
                </div>
            </div>
        </section>

        <div id="table-container">
            ${generateCampaignTable(campaigns)}
        </div>
    `;

    return tableHTML;
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
    fetch("/api/campaigns-pending")  // 🔥 백엔드와 일치하는 API 엔드포인트
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
            document.getElementById("content").innerHTML = generateCampaignApprovalTable(data); // ✅ 데이터 전달
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

// ✅ 공지사항 CSS 로드 함수 추가
function loadNoticeStyles() {
    let existingLink = document.getElementById("notice-css");
    if (!existingLink) {
        let link = document.createElement("link");
        link.id = "notice-css";
        link.rel = "stylesheet";
        link.href = "/css/admin/admin-notice.css"; // ✅ 공지사항 전용 CSS 경로
        document.head.appendChild(link);
    }
}

// ✅ 공지사항 생성 폼 로드
function loadNoticeForm() {
    document.getElementById("content").innerHTML = `
        <h2>공지사항 작성</h2>
        <form id="notice-form">
            <label for="title">제목:</label>
            <input type="text" id="title" name="title" required>

            <label for="content">내용:</label>
            <textarea id="content" name="content" required></textarea>

            <button type="button" onclick="submitNotice()">등록</button>
        </form>
    `;
}

// ✅ 공지사항 등록 요청
function submitNotice() {
    const title = document.getElementById("title").value;
    const content = document.getElementById("content").value;

    fetch("/api/notices/create", {
        method: "POST",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify({ title, content })
    })
        .then(response => {
            if (!response.ok) {
                return response.text().then(err => { throw new Error(err); });
            }
            return response.json();
        })
        .then(data => {
            alert("공지사항이 등록되었습니다!");
            fetchNotice();
        })
        .catch(error => {
            console.error("❌ 공지사항 등록 오류:", error);
            alert("공지사항 등록 중 오류가 발생했습니다.");
        });
}


//공지사항
function fetchNotice() {
    loadNoticeStyles();  // ✅ CSS 로드 추가
    fetch("/api/notices")
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
            document.getElementById("content").innerHTML = generateNoticeTable(data); // ✅ `data`를 함수에 전달
        })
        .catch(error => {
            console.error("❌ 공지사항 목록 로드 오류:", error);
            document.getElementById("content").innerHTML = `<h2>오류 발생</h2><p>${error.message}</p>`;
        });
}

//공지사항 동적 생성
function generateNoticeTable(notices) {
    if (!notices || notices.length === 0) {
        return `<h2>공지사항</h2><p>등록된 공지사항이 없습니다.</p>`;
    }

    let tableHTML = `
    <div class="notice-container">
        <h2>공지사항</h2>
        <button onclick="location.href='/admin/notice-form'">공지사항 등록</button>
        <div class="table-container">
            <table class="notice-table">  <!-- ✅ CSS 적용할 클래스 추가 -->
                <thead>
                    <tr>
                        <th>공지 번호</th>
                        <th>제목</th>
                        <th>내용</th>
                        <th>등록 날짜</th>
                    </tr>
                </thead>
                <tbody>
`;

    notices.forEach(notice => {
        tableHTML += `
        <tr>
            <td>${notice.noticeId}</td>
            <td>${notice.title}</td>
            <td>${notice.content}</td>
            <td>${new Date(notice.noticedDate).toLocaleDateString()}</td>
        </tr>
    `;
    });

    tableHTML += `</tbody></table></div></div>`;  // ✅ .notice-container 닫는 태그 추가
    return tableHTML;
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
