document.addEventListener("DOMContentLoaded", function () {
    const userReportTable = document.querySelector(".user-report tbody");

    async function loadUserReports() {
        try {
            // 신고된 유저 목록을 API에서 가져옴
            const response = await fetch("/api/reports/users");
            const reportedUsers = await response.json();

            // 테이블 초기화
            userReportTable.innerHTML = "";

            if (reportedUsers.length === 0) {
                userReportTable.innerHTML = `<tr><td colspan="7" class="empty-state">신고된 유저가 없습니다.</td></tr>`;
            } else {
                reportedUsers.forEach(user => {
                    const row = document.createElement("tr");
                    row.innerHTML = `
                        <td>${user.userId ? user.userId : "-"}</td>
                        <td>${user.reason}</td>
                        <td>${new Date(user.reportedDate).toLocaleDateString()}</td>
                        <td class="status ${user.status === "처리중" ? "pending" : "completed"}">${user.status}</td>
                        <td class="action-buttons">
                            <button class="warn-btn" onclick="updateReportStatus(${user.reportId}, '경고')">경고</button>
                            <button class="ban-btn" onclick="updateReportStatus(${user.reportId}, '정지')">정지</button>
                            <button class="block-btn" onclick="updateReportStatus(${user.reportId}, '차단')">차단</button>
                        </td>
                    `;
                    userReportTable.appendChild(row);
                });
            }
        } catch (error) {
            console.error("신고된 유저 데이터를 불러오는 중 오류 발생:", error);
        }
    }

    // 신고 상태 업데이트 요청
    async function updateReportStatus(reportId, status) {
        try {
            await fetch(`/api/reports/${reportId}/status`, {
                method: "PATCH",
                headers: { "Content-Type": "application/json" },
                body: JSON.stringify({ status })
            });
            alert(`신고 상태가 ${status}로 변경되었습니다.`);
            loadUserReports();  // 데이터 다시 로드
        } catch (error) {
            console.error("상태 변경 오류:", error);
        }
    }

    loadUserReports();
});
