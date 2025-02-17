// Status card selection
document.querySelectorAll('.status-card').forEach(card => {
    card.addEventListener('click', () => {
        document.querySelectorAll('.status-card').forEach(c => c.classList.remove('active'));
        card.classList.add('active');
        // Add your filter logic here
    });
});

// Select all checkbox
document.getElementById('selectAll').addEventListener('change', (e) => {
    document.querySelectorAll('tbody input[type="checkbox"]')
        .forEach(checkbox => checkbox.checked = e.target.checked);
});

// Period button selection
document.querySelectorAll('.period-buttons button').forEach(button => {
    button.addEventListener('click', () => {
        document.querySelectorAll('.period-buttons button')
            .forEach(b => b.classList.remove('active'));
        button.classList.add('active');

        const today = new Date();
        const startDate = document.querySelector('.date-range input[type="date"]:first-child');
        const endDate = document.querySelector('.date-range input[type="date"]:last-child');

        endDate.valueAsDate = today;

        switch (button.textContent) {
            case '오늘':
                startDate.valueAsDate = today;
                break;
            case '1주일':
                startDate.valueAsDate = new Date(today.setDate(today.getDate() - 7));
                break;
            case '1개월':
                startDate.valueAsDate = new Date(today.setMonth(today.getMonth() - 1));
                break;
            case '3개월':
                startDate.valueAsDate = new Date(today.setMonth(today.getMonth() - 3));
                break;
        }
    });
});

function editProduct(id) {
    // Add your edit logic here
    console.log('Edit product:', id);
}

function deleteProduct(id) {
    if (confirm('정말로 이 상품을 삭제하시겠습니까?')) {
        // Add your delete logic here
        console.log('Delete product:', id);
    }
}