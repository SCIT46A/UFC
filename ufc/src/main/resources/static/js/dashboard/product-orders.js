// Select all checkbox functionality
document.getElementById('selectAll').addEventListener('change', (e) => {
    document.querySelectorAll('tbody input[type="checkbox"]')
        .forEach(checkbox => checkbox.checked = e.target.checked);
});

// Batch invoice modal
function openBatchInvoiceModal() {
    document.getElementById('batchInvoiceModal').classList.add('active');
}

function closeBatchInvoiceModal() {
    document.getElementById('batchInvoiceModal').classList.remove('active');
}

function processBatchInvoice() {
    // Add your batch invoice processing logic here
    alert('송장번호가 일괄 등록되었습니다.');
    closeBatchInvoiceModal();
}

// Order status change
function changeOrderStatus() {
    const selectedOrders = Array.from(document.querySelectorAll('tbody input[type="checkbox"]:checked'))
        .map(checkbox => checkbox.value);

    if (selectedOrders.length === 0) {
        alert('주문을 선택해주세요.');
        return;
    }

    // Add your status change logic here
    console.log('Selected orders:', selectedOrders);
}

// Update tracking number
function updateTrackingNumber(button) {
    const input = button.previousElementSibling;
    const trackingNumber = input.value;

    if (!trackingNumber) {
        alert('송장번호를 입력해주세요.');
        return;
    }

    // Add your tracking number update logic here
    console.log('Updated tracking number:', trackingNumber);
    alert('송장번호가 저장되었습니다.');
}

// View order detail
function viewOrderDetail(button) {
    const row = button.closest('tr');
    const orderNumber = row.cells[2].textContent;

    // Add your order detail view logic here
    console.log('View order:', orderNumber);
}

// Update delivery status
function updateDeliveryStatus(button) {
    const row = button.closest('tr');
    const orderNumber = row.cells[2].textContent;

    // Add your delivery status update logic here
    console.log('Update delivery status:', orderNumber);
}

// Process selected orders
function processSelectedOrders() {
    const selectedOrders = Array.from(document.querySelectorAll('tbody input[type="checkbox"]:checked'))
        .map(checkbox => checkbox.value);

    if (selectedOrders.length === 0) {
        alert('처리할 주문을 선택해주세요.');
        return;
    }

    // Add your batch processing logic here
    console.log('Process orders:', selectedOrders);
}