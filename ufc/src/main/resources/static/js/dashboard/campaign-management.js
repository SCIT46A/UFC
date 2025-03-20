function initCampaignManagement() {
    console.log("📢 캠페인 관리 JS 실행됨");

    // 🔹 필터 적용 이벤트 리스너 추가
    document.getElementById("status")?.addEventListener("change", applyFilters);
    document.querySelector(".date-range input[type='date']")?.addEventListener("change", applyFilters);
    document.querySelector(".date-range input[type='date']:nth-of-type(2)")?.addEventListener("change", applyFilters);

    // 🔹 기간 선택 버튼 이벤트 리스너 추가
    document.querySelectorAll(".period-buttons button").forEach(button => {
        button.addEventListener("click", function () {
            document.querySelectorAll(".period-buttons button").forEach(btn => btn.classList.remove("active"));
            this.classList.add("active");
            updateDateRange(this.textContent);
        });
    });

    // 🔹 캠페인 등록 폼 이벤트 리스너 추가
    document.getElementById("campaignForm")?.addEventListener("submit", handleCampaignFormSubmit);

    // 🔹 모달 닫기 이벤트 리스너 추가
    document.getElementById("campaignModal")?.addEventListener("click", function (event) {
        if (event.target === this) {
            this.style.display = "none";
        }
    });

    // 🔹 캠페인 미리보기 버튼 이벤트 리스너 추가
    document.getElementById("previewCampaignBtn")?.addEventListener("click", previewCampaign);

    // 🔹 캠페인 데이터 불러오기
    fetchCampaigns();
}

// ✅ 필터 적용 함수
function applyFilters() {
    const status = document.getElementById("status")?.value;
    const startDate = document.querySelector(".date-range input[type='date']")?.value;
    const endDate = document.querySelector(".date-range input[type='date']:nth-of-type(2)")?.value;

    console.log("📊 필터 적용:", { status, startDate, endDate });
}

// ✅ 날짜 범위 업데이트 함수
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
    if (!date) return "-"; // ❗ null 또는 undefined 처리

    // ✅ date가 문자열이면 Date 객체로 변환
    if (typeof date === "string") {
        date = new Date(date.trim()); // 🚀 trim() 추가로 공백 제거
    }

    // ✅ date가 숫자 (timestamp)인 경우 Date 객체로 변환
    if (typeof date === "number") {
        date = new Date(date);
    }

    // ✅ Date 객체인지 확인 후 변환
    if (date instanceof Date && !isNaN(date.getTime())) {
        return date.toISOString().split("T")[0]; // YYYY-MM-DD 포맷 반환
    }

    console.warn("❗ 유효하지 않은 날짜 형식:", date);
    return "유효하지 않은 날짜"; // ❗ 변환 실패한 경우
}


function updateCampaignStatusCounts(counts) {
    console.log("📊 캠페인 상태 카드 값 업데이트:", counts);

    const elements = {
        pendingCampaigns: document.querySelector(".status-card:nth-child(1) p"),
        rejectedCampaigns: document.querySelector(".status-card:nth-child(2) p"),
        inProgressCampaigns: document.querySelector(".status-card:nth-child(3) p"),
        closedCampaigns: document.querySelector(".status-card:nth-child(4) p"),
        achievedCampaigns: document.querySelector(".status-card:nth-child(5) p"),
    };

    for (let key in elements) {
        if (elements[key]) {
            elements[key].textContent = counts[key] || 0;
        } else {
            console.warn(`⚠️ 상태 카드 요소 없음: ${key}`);
        }
    }
}


// ✅ 캠페인 목록 가져오기
async function fetchCampaigns() {
    try {
        const response = await fetch("/api/creator/dashboard/campaigns/management");
        if (!response.ok) throw new Error(`HTTP 오류 발생: ${response.status}`);

        const data = await response.json();
        console.log("✅ 서버에서 받은 캠페인 데이터:", data);

        if (!Array.isArray(data)) {
            throw new Error("서버 응답이 올바른 캠페인 목록(JSON 배열)이 아닙니다.");
        }

        // 🔍 startDate와 endDate의 데이터 유형 확인
        data.forEach(campaign => {
            console.log(`📌 캠페인 ${campaign.campaignId} 날짜 확인:`,
                "startDate:", typeof campaign.startDate, campaign.startDate,
                "endDate:", typeof campaign.endDate, campaign.endDate
            );
        });

        updateCampaignStatusCounts(getCampaignCounts(data));
        renderCampaignList(data);
    } catch (error) {
        console.error("❌ 캠페인 데이터를 불러오는 중 오류 발생:", error);
    }
}

function getCampaignCounts(campaigns) {
    let counts = {
        pendingCampaigns: 0,
        rejectedCampaigns: 0,
        inProgressCampaigns: 0,
        closedCampaigns: 0,
        achievedCampaigns: 0
    };

    campaigns.forEach(campaign => {
        switch (campaign.campaignStatus) {
            case 0: counts.pendingCampaigns++; break; // 승인 대기
            case 1: {
                const today = new Date();
                const endDate = new Date(campaign.endDate);
                const donationRate = campaign.donationPercentage || 0;

                if (endDate < today) {
                    counts.closedCampaigns++; // 종료된 캠페인
                    if (donationRate >= 100) counts.achievedCampaigns++; // 목표 달성
                } else {
                    counts.inProgressCampaigns++; // 진행 중
                }
                break;
            }
            case 2: counts.rejectedCampaigns++; break; // 승인 거부
        }
    });

    return counts;
}



// ✅ 캠페인 목록 테이블 렌더링
function renderCampaignList(campaigns) {
    const tbody = document.getElementById("campaign-table-body");
    if (!tbody) {
        console.error("❌ tbody 요소를 찾을 수 없습니다. ID 확인 필요!");
        return;
    }

    tbody.innerHTML = ""; // 기존 테이블 초기화


    if (campaigns.length === 0) {
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

    campaigns.forEach(campaign => {
        const row = document.createElement("tr");

        row.innerHTML = `
            <td>${campaign.campaignId}</td>
            <td>${campaign.title}</td>
            <td>${formatDate(campaign.startDate ?? "-")}</td>  <!-- 🛠️ 예외처리 추가 -->
            <td>${formatDate(campaign.endDate ?? "-")}</td>    <!-- 🛠️ 예외처리 추가 -->
            <td>
                <span class="status-badge ${getStatusClass(campaign)}">
                    ${getStatusText(campaign)}
                </span>
            </td>
            <td>${campaign.donationPercentage ? `${campaign.donationPercentage.toFixed(1)}%` : '0%'}</td>
            <td>
                <div style="display: flex; flex-direction: column; align-items: flex-start; gap: 8px;">
                    <button class="btn btn-primary" onclick="${campaign.campaignStatus === 1
                ? `viewCampaignDetails('${campaign.campaignId}')`
                : `openCampaignUpdatePage('${campaign.campaignId}')`}">
                        ${campaign.campaignStatus === 1 ? '캠페인 보기' : '캠페인 수정'}
                    </button>
                    ${campaign.campaignStatus === 2
                ? `<button style="min-width: 89.89px; text-align: center;" class="btn btn-danger" onclick="showRejectionReason('${campaign.rejectedReason}')">
                            거절 사유 
                        </button>`
                : ''}
                </div>
            </td>
        `;

        tbody.appendChild(row);
    });

    console.log("✅ 캠페인 목록 렌더링 완료!");
}


// ✅ 캠페인 상태에 따라 CSS 클래스 반환
function getStatusClass(campaign) {
    if (campaign.campaignStatus === 0) {
        return "inspection"; // ✅ 승인 대기
    }
    if (campaign.campaignStatus === 1) {
        const today = new Date();
        const startDate = new Date(campaign.startDate);
        const endDate = new Date(campaign.endDate);
        const donationRate = campaign.donationPercentage.toFixed(1);

        if (startDate > today) {
            return "scheduled"; // ✅ 대기 중
        }
        if (endDate < today) {
            return donationRate < 100 ? "unachieved" : "achieved"; // ✅ 종료된 경우 목표 달성 여부 체크
        }
        return donationRate < 100 ? "in-progress" : "in-progress-achieved"; // ✅ 진행 중인데 목표 달성
    }
    if (campaign.campaignStatus === 2) {
        return "rejected"; // ✅ 승인 거부
    }
    return "";
}

// ✅ UI에서 보여줄 상태 텍스트 반환
function getStatusText(campaign) {
    if (campaign.campaignStatus === 0) return "승인 대기";

    if (campaign.campaignStatus === 1) {
        const today = new Date();
        const startDate = new Date(campaign.startDate);
        const endDate = new Date(campaign.endDate);
        const donationRate = campaign.donationPercentage.toFixed(1);

        if (startDate > today) return "대기 중";
        if (endDate < today) {
            return donationRate < 100 ? "종료: 목표 미달성" : "종료: 목표 달성";
        }
        return donationRate < 100 ? "진행 중" : "진행 중 : 목표 달성"; // ✅ 진행 중인데 목표 달성한 경우 추가
    }
    if (campaign.campaignStatus === 2) {
        return "승인 거부";
    }
    return "알 수 없음";
}

// ✅ 상세보기 버튼 클릭 시 모달 열기
function viewCampaignDetails(campaignId) {
    console.log("📌 캠페인 상세 보기:", campaignId);
    const url = window.location.origin + "/campaign/" + campaignId;
    // 새 창(팝업) 설정 (너비 1200px, 높이 800px)
    const popup = window.open(url, "_blank", "width=1200,height=800,scrollbars=yes,resizable=yes");
    // 창이 차단되었을 경우 처리
    if (!popup) {
        alert("팝업이 차단되었습니다. 팝업 차단을 해제해주세요.");
    }
}

function openCampaignUpdatePage(campaignId) {
    console.log("📌 캠페인 상세 보기:", campaignId);
    const url = window.location.origin + "/campaign/update/" + campaignId;
    // 새 창(팝업) 설정 (너비 1200px, 높이 800px)
    const popup = window.open(url, "_blank", "width=1200,height=800,scrollbars=yes,resizable=yes");
    // 창이 차단되었을 경우 처리
    if (!popup) {
        alert("팝업이 차단되었습니다. 팝업 차단을 해제해주세요.");
    }
}

function openCampaignCreatePage() {
    const url = window.location.origin + "/campaign/create";
    window.open(url, "_blank");
}


// ✅ 모달 HTML 추가
document.body.insertAdjacentHTML("beforeend", `
    <div id="rejectionModal" class="modal">
        <div class="modal-content">
            <span class="close" onclick="closeModal()">&times;</span>
            <h3>거절 사유</h3>
            <p id="rejectionReasonText"></p>
        </div>
    </div>
`);

// ✅ 모달 스타일 추가
if (!window.campaignModalStyleInitialized) {
    window.campaignModalStyleInitialized = true;

    const modalStyle = `
        .modal { display: none; position: fixed; z-index: 1000; left: 0; top: 0; width: 100%; height: 100%; background-color: rgba(0,0,0,0.5); }
        .modal-content { background-color: white; padding: 20px; margin: 15% auto; width: 50%; border-radius: 8px; box-shadow: 0 4px 8px rgba(0,0,0,0.2); }
        .close { float: right; font-size: 20px; cursor: pointer; }
    `;

    const styleSheet = document.createElement("style");
    styleSheet.innerText = modalStyle;
    document.head.appendChild(styleSheet);
}


// ✅ 모달 열기 함수
function showRejectionReason(reason) {
    document.getElementById("rejectionReasonText").textContent = reason || "거절 사유가 제공되지 않았습니다.";
    document.getElementById("rejectionModal").style.display = "block";
}

// ✅ 모달 닫기 함수
function closeModal() {
    document.getElementById("rejectionModal").style.display = "none";
}



//✅ 모달 열기 및 닫기 함수
function openCampaignModal() {
    document.getElementById("campaignModal").style.display = "block";
}

function closeCampaignModal() {
    document.getElementById("campaignModal").style.display = "none";
}

// 🚀 페이지 로드 시 캠페인 데이터 불러오기
document.addEventListener("DOMContentLoaded", initCampaignManagement);

// 🚀 fragment 변경 시 JS 재적용
document.addEventListener("reapplyEventListeners", initCampaignManagement);
