function initSettlementManagement() {
    console.log("💰 정산 관리 JS 실행됨");

    window.applyFilters = function () {
        const status = document.getElementById("status")?.value;
        const period = document.getElementById("period")?.value;
        const year = document.getElementById("year")?.value;

        if (!status || !period || !year) return; // 요소가 없으면 실행 안함

        console.log("📊 필터 적용:", { status, period, year });
    };

    window.downloadExcel = function () {
        console.log("📥 엑셀 다운로드 중");
    };

    window.downloadCSV = function () {
        console.log("📥 CSV 다운로드 중");
    };

    window.viewDetails = function (settlementId) {
        const mockDetails = {
            id: settlementId,
            date: "2024-02-16",
            amount: "50,000",
            status: "정산 예정",
            items: [
                { product: "상품 A", quantity: 2, price: "10,000", total: "20,000" },
                { product: "상품 B", quantity: 3, price: "10,000", total: "30,000" }
            ]
        };

        const detailsHtml = `
            <p><strong>정산 ID:</strong> ${mockDetails.id}</p>
            <p><strong>날짜:</strong> ${mockDetails.date}</p>
            <p><strong>총 금액:</strong> ${mockDetails.amount}원</p>
            <p><strong>상태:</strong> ${mockDetails.status}</p>
            <h3>상품 목록</h3>
            <table>
                <thead>
                    <tr>
                        <th>상품</th>
                        <th>수량</th>
                        <th>가격</th>
                        <th>합계</th>
                    </tr>
                </thead>
                <tbody>
                    ${mockDetails.items.map(item => `
                        <tr>
                            <td>${item.product}</td>
                            <td>${item.quantity}</td>
                            <td>${item.price}원</td>
                            <td>${item.total}원</td>
                        </tr>
                    `).join("")}
                </tbody>
            </table>
        `;

        document.getElementById("settlementDetails").innerHTML = detailsHtml;
        document.getElementById("settlementModal").style.display = "block";
    };

    window.closeModal = function () {
        document.getElementById("settlementModal").style.display = "none";
    };

    window.onclick = function (event) {
        const modal = document.getElementById("settlementModal");
        if (event.target == modal) {
            modal.style.display = "none";
        }
    };
}

// 🚀 fragment가 변경될 때마다 JS를 다시 실행하도록 설정
document.addEventListener("reapplyEventListeners", initSettlementManagement);
