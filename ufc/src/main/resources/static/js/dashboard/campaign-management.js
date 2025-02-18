function initCampaignManagement() {
    console.log("📢 캠페인 관리 JS 실행됨");

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
        return date.toISOString().split("T")[0];
    }

    document.getElementById('campaignForm')?.addEventListener("submit", function (e) {
        e.preventDefault();
        console.log("캠페인 등록:", {
            name: document.getElementById('campaignName').value,
            startDate: document.getElementById('campaignStartDate').value,
            endDate: document.getElementById('campaignEndDate').value,
            description: document.getElementById('campaignDescription').value
        });
        closeCampaignModal();
    });

    document.getElementById('campaignModal')?.addEventListener("click", function (event) {
        if (event.target === this) {
            this.style.display = "none";
        }
    });

    document.getElementById('previewCampaignBtn')?.addEventListener("click", previewCampaign);
}

function applyFilters() {
    const status = document.getElementById('status')?.value;
    const startDate = document.querySelector(".date-range input[type='date']")?.value;
    const endDate = document.querySelector(".date-range input[type='date']:nth-of-type(2)")?.value;

    console.log('📊 필터 적용:', { status, startDate, endDate });
}

function openCampaignModal(campaignId = null) {
    const modal = document.getElementById('campaignModal');
    const detailsDiv = document.getElementById('campaignDetails');

    if (campaignId) {
        detailsDiv.innerHTML = `<p>캠페인 ID ${campaignId}의 상세 정보를 불러오는 중...</p>`;
    } else {
        detailsDiv.innerHTML = `
            <form id="campaignForm">
                <div class="filter-group">
                    <label for="campaignName">캠페인 이름:</label>
                    <input type="text" id="campaignName" name="campaignName" required>
                </div>
                <div class="filter-group">
                    <label for="campaignStartDate">시작일:</label>
                    <input type="date" id="campaignStartDate" name="campaignStartDate" required>
                </div>
                <div class="filter-group">
                    <label for="campaignEndDate">종료일:</label>
                    <input type="date" id="campaignEndDate" name="campaignEndDate" required>
                </div>
                <div class="filter-group">
                    <label for="campaignDescription">설명:</label>
                    <textarea id="campaignDescription" name="campaignDescription" rows="4"></textarea>
                </div>
                <button type="submit" class="btn btn-primary">캠페인 등록</button>
            </form>
        `;
    }
    modal.style.display = "block";
}

function closeCampaignModal() {
    document.getElementById('campaignModal').style.display = "none";
}

function viewCampaignDetails(campaignId) {
    const mockDetails = {
        id: campaignId,
        name: '여름 신제품 홍보',
        startDate: '2024-06-01',
        endDate: '2024-08-31',
        status: '진행 중',
        submissions: 150,
        description: '여름 신제품 출시에 맞춰 인플루언서를 통한 홍보 캠페인'
    };

    const detailsHtml = `
        <p><strong>캠페인 ID:</strong> ${mockDetails.id}</p>
        <p><strong>이름:</strong> ${mockDetails.name}</p>
        <p><strong>시작일:</strong> ${mockDetails.startDate}</p>
        <p><strong>종료일:</strong> ${mockDetails.endDate}</p>
        <p><strong>상태:</strong> ${mockDetails.status}</p>
        <p><strong>제출 수:</strong> ${mockDetails.submissions}</p>
        <p><strong>설명:</strong> ${mockDetails.description}</p>
        <button class="btn btn-primary" onclick="editCampaign('${mockDetails.id}')">캠페인 수정</button>
    `;

    document.getElementById('campaignDetails').innerHTML = detailsHtml;
    document.getElementById('campaignModal').style.display = "block";
}

function editCampaign(campaignId) {
    console.log("✏️ 캠페인 수정:", campaignId);
    openCampaignModal(campaignId);
}

function previewCampaign() {
    console.log("👀 캠페인 미리보기");
    alert("현재 등록된 캠페인의 미리보기 기능은 개발 중입니다.");
}

// 🚀 fragment가 변경될 때마다 JS를 다시 실행하도록 설정
document.addEventListener("reapplyEventListeners", initCampaignManagement);
