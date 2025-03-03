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

// ✅ 날짜 형식 변환 함수 (YYYY-MM-DD)
// ✅ 날짜 형식 변환 함수 (YYYY-MM-DD)
function formatDate(date) {
    if (!date) return "-"; // ❗ 날짜 값이 없으면 '-' 반환

    // ✅ 문자열일 경우 Date 객체로 변환
    const parsedDate = (typeof date === "string") ? new Date(date) : date;

    // ✅ 날짜 유효성 체크
    if (isNaN(parsedDate.getTime())) return "유효하지 않은 날짜";

    return parsedDate.toISOString().split("T")[0]; // YYYY-MM-DD 포맷 반환
}

// ✅ 캠페인 등록 폼 제출 처리
function handleCampaignFormSubmit(event) {
    event.preventDefault();
    console.log("캠페인 등록:", {
        name: document.getElementById("campaignName").value,
        startDate: document.getElementById("campaignStartDate").value,
        endDate: document.getElementById("campaignEndDate").value,
        description: document.getElementById("campaignDescription").value
    });
    closeCampaignModal();
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

        renderCampaignList(data);
    } catch (error) {
        console.error("❌ 캠페인 데이터를 불러오는 중 오류 발생:", error);
    }
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
                <td colspan="7">
                    <div class="empty-message">
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
            <td>${formatDate(campaign.startDate)}</td>
            <td>${formatDate(campaign.endDate)}</td>
            <td>
                <span class="status-badge ${getStatusClass(campaign)}">
                    ${getStatusText(campaign)}
                </span>
            </td>
            <td>75%(수정 예정)</td>
            <td>
                <button class="btn btn-primary" onclick="viewCampaignDetails('${campaign.campaignId}')">상세 보기</button>
            </td>
        `;

        tbody.appendChild(row);
    });

    console.log("✅ 캠페인 목록 렌더링 완료!");
}

// ✅ 캠페인 상태에 따라 클래스 적용
function getStatusClass(campaign) {
    if (campaign.campaignStatus === false) {
        return "inspection"; // 승인 대기
    }
    if (campaign.campaignStatus === true) {
        if (new Date(campaign.startDate) > new Date()) {
            return "scheduled"; // 대기 중
        }
        if (new Date(campaign.endDate) < new Date()) {
            return campaign.isSuccess ? "achieved" : "unachieved"; // 목표 달성 / 목표 미달성
        }
        return "in-progress"; // 진행 중
    }
    return "";
}

// ✅ 캠페인 상태 텍스트 변환
function getStatusText(campaign) {
    if (campaign.campaignStatus === false) return "승인 대기";
    if (campaign.campaignStatus === true) {
        if (new Date(campaign.startDate) > new Date()) return "대기 중";
        if (new Date(campaign.endDate) < new Date()) {
            return campaign.isSuccess ? "종료: 목표 달성" : "종료: 목표 미달성";
        }
        return "진행 중";
    }
    return "알 수 없음";
}

// ✅ 상세보기 버튼 클릭 시 모달 열기
function viewCampaignDetails(campaignId) {
    console.log("📌 캠페인 상세 보기:", campaignId);
    document.getElementById("campaignDetails").innerHTML = `<p>캠페인 ID ${campaignId}의 상세 정보를 불러오는 중...</p>`;
    document.getElementById("campaignModal").style.display = "block";
}

// ✅ 모달 열기 및 닫기 함수
function openCampaignModal() {
    document.getElementById("campaignModal").style.display = "block";
}

function closeCampaignModal() {
    document.getElementById("campaignModal").style.display = "none";
}

// ✅ 캠페인 미리보기 함수
function previewCampaign() {
    console.log("👀 캠페인 미리보기");
    alert("현재 등록된 캠페인의 미리보기 기능은 개발 중입니다.");
}

// 🚀 페이지 로드 시 캠페인 데이터 불러오기
document.addEventListener("DOMContentLoaded", initCampaignManagement);

// 🚀 fragment 변경 시 JS 재적용
document.addEventListener("reapplyEventListeners", initCampaignManagement);
