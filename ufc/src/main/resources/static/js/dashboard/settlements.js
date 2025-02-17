function applyFilters() {
    const status = document.getElementById('status').value;
    const period = document.getElementById('period').value;
    const year = document.getElementById('year').value;

    // Here you would typically make an AJAX call to your backend
    console.log('필터 적용:', { status, period, year });
    // Reload or update the table based on the filter results
}

function downloadExcel() {
    // Implement Excel download logic
    console.log('엑셀 다운로드 중');
    // You might want to make an AJAX call to a backend endpoint that generates the Excel file
}

function downloadCSV() {
    // Implement CSV download logic
    console.log('CSV 다운로드 중');
    // You might want to make an AJAX call to a backend endpoint that generates the CSV file
}

function viewDetails(settlementId) {
    // Fetch settlement details from the server
    // For demonstration, we'll use mock data
    const mockDetails = {
        id: settlementId,
        date: '2024-02-16',
        amount: '50,000',
        status: '정산 예정',
        items: [
            { product: '상품 A', quantity: 2, price: '10,000', total: '20,000' },
            { product: '상품 B', quantity: 3, price: '10,000', total: '30,000' }
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
                        `).join('')}
                    </tbody>
                </table>
            `;

    document.getElementById('settlementDetails').innerHTML = detailsHtml;
    document.getElementById('settlementModal').style.display = 'block';
}

function closeModal() {
    document.getElementById('settlementModal').style.display = 'none';
}

// Close the modal when clicking outside of it
window.onclick = function (event) {
    const modal = document.getElementById('settlementModal');
    if (event.target == modal) {
        modal.style.display = 'none';
    }
}