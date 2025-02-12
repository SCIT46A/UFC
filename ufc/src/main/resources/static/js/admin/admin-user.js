document.addEventListener("DOMContentLoaded", function () {
    const contentArea = document.getElementById("content");
    const menuItems = document.querySelectorAll(".main-admin-select a");

    menuItems.forEach((menuItem) => {
        menuItem.addEventListener("click", function (event) {
            event.preventDefault();
            const page = this.getAttribute("data-page");

            console.log(`📢 선택된 페이지: ${page}`);

            // 페이지는 서버에서 데이터를 가져와야 함
            if (page === "user-report") {
                fetchUserReport();
            } else if (page === "campaign-status") {
                fetchCampaignStatus();
            } else if (page === "campaign-approval") {
                fetchCampaignApproval();
            } else if (page === "campaign-report") {
                fetchCampaignReport();
            } else if (page === "creator-approval") {
                fetchCreatorApproval();
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
                        <th>창작자 전환 관리자 승인여부</th>
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



// 캠페인 신고 관리
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




// 캠페인 승인 관리
function fetchCampaignApproval() {
    fetch("/api/campaign-approval")
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
            document.getElementById("content").innerHTML = generateCampaignApprovalTable(data); // ✅ `data`를 함수에 전달
        })
        .catch(error => {
            console.error("❌ 캠페인 승인 관리 목록 로드 오류:", error);
            document.getElementById("content").innerHTML = `<h2>오류 발생</h2><p>${error.message}</p>`;
        });
}

// 캠페인 승인 관리 페이지 테이블 동적 생성
function generateCampaignApprovalTable(campaigns) {
// ❌❌❌❌❌❌❌❌추가 해야해요❌❌❌❌❌❌❌❌❌ ApiController, service, repository 다 추가해야함
}

// 캠페인 운영 현황 함수
function fetchCampaignStatus() {
    fetch("/api/campaign-status")
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
            document.getElementById("content").innerHTML = generateCampaignStatusTable(data); // ✅ `data`를 함수에 전달
        })
        .catch(error => {
            console.error("❌ 캠페인 현황 목록 로드 오류:", error);
            document.getElementById("content").innerHTML = `<h2>오류 발생</h2><p>${error.message}</p>`;
        });
}


// 캠페인 현황 페이지 테이블 동적 생성
function generateCampaignStatusTable(campaigns) {
    if (!campaigns || campaigns.length === 0) {
        return `<h2>캠페인 운영 현황</h2><p>등록된 캠페인이 없습니다.</p>`;
    }

    let tableHTML = `
        <h2>캠페인 운영 현황</h2>
        <p>현재 등록된 캠페인 목록을 확인하세요.</p>
        <div class="table-container">
            <table>
                <thead>
                    <tr>
                        <th>캠페인 ID</th>
                        <th>제목</th>
                        <th>시작일</th>
                        <th>종료일</th>
                        <th>생성일</th>
                        <th>생성자</th>
                        <th>성공 여부</th>
                    </tr>
                </thead>
                <tbody>
    `;

    campaigns.forEach(campaign => {
        tableHTML += `
            <tr>
                <td>${campaign.campaignId}</td>
                <td>${campaign.title}</td>
                <td>${new Date(campaign.startDate).toLocaleDateString()}</td>
                <td>${new Date(campaign.endDate).toLocaleDateString()}</td>
                <td>${new Date(campaign.createdDate).toLocaleDateString()}</td>
                <td>${campaign.createdById ? campaign.createdById : "정보 없음"}</td>
                <td>${campaign.isSuccess ? "✅ 성공" : "❌ 실패"}</td>
            </tr>
        `;
    });

    tableHTML += `</tbody></table></div>`;
    return tableHTML;
}

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
