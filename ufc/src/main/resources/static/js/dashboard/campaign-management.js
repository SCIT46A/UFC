function applyFilters() {
    const status = document.getElementById('status').value;
    const startDate = document.getElementById('startDate').value;
    const endDate = document.getElementById('endDate').value;

    console.log('필터 적용:', { status, startDate, endDate });
    // Implement AJAX call to backend for filtering
}

function openCampaignModal(campaignId = null) {
    const modal = document.getElementById('campaignModal');
    const detailsDiv = document.getElementById('campaignDetails');

    if (campaignId) {
        // Edit existing campaign
        // Fetch campaign details and populate the form
        detailsDiv.innerHTML = `<p>캠페인 ID ${campaignId}의 상세 정보를 불러오는 중...</p>`;
    } else {
        // New campaign registration
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

        document.getElementById('campaignForm').onsubmit = function (e) {
            e.preventDefault();
            // Implement campaign registration logic
            console.log('캠페인 등록:', {
                name: document.getElementById('campaignName').value,
                startDate: document.getElementById('campaignStartDate').value,
                endDate: document.getElementById('campaignEndDate').value,
                description: document.getElementById('campaignDescription').value
            });
            closeCampaignModal();
        };
    }

    modal.style.display = 'block';
}

function closeCampaignModal() {
    document.getElementById('campaignModal').style.display = 'none';
}

function viewCampaignDetails(campaignId) {
    // Fetch campaign details from the server
    // For demonstration, we'll use mock data
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
                <p><strong>상태:</strong> ${mockDetails.status}</p>상태:</strong> ${mockDetails.status}</p>
                <p><strong>제출 수:</strong> ${mockDetails.submissions}</p>
                <p><strong>설명:</strong> ${mockDetails.description}</p>
                <button class="btn btn-primary" onclick="editCampaign('${mockDetails.id}')">캠페인 수정</button>
            `;

    document.getElementById('campaignDetails').innerHTML = detailsHtml;
    document.getElementById('campaignModal').style.display = 'block';
}

function editCampaign(campaignId) {
    // Implement edit functionality
    console.log('캠페인 수정:', campaignId);
    openCampaignModal(campaignId);
}

function previewCampaign() {
    // Implement preview functionality
    console.log('캠페인 미리보기');
    alert('현재 등록된 캠페인의 미리보기 기능은 개발 중입니다.');
}

// Close the modal when clicking outside of it
window.onclick = function (event) {
    const modal = document.getElementById('campaignModal');
    if (event.target == modal) {
        modal.style.display = 'none';
    }
}