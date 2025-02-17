{/* <script th:inline="javascript"> */ }
document.addEventListener('DOMContentLoaded', function () {
    const statusFilter = document.getElementById('statusFilter');
    const table = document.querySelector('table');
    const inspectionModal = document.getElementById('inspectionModal');
    const shippingModal = document.getElementById('shippingModal');
    const closeBtns = document.getElementsByClassName('close');

    statusFilter.addEventListener('change', function () {
        const rows = table.querySelectorAll('tbody tr');
        const selectedStatus = this.value;

        rows.forEach(row => {
            const statusCell = row.querySelector('td:nth-child(3)');
            const status = statusCell.textContent.trim();

            if (selectedStatus === 'all' || status === selectedStatus) {
                row.style.display = '';
            } else {
                row.style.display = 'none';
            }
        });
    });

    document.querySelectorAll('.inspect-btn').forEach(btn => {
        btn.addEventListener('click', function () {
            const donationId = this.getAttribute('data-id');
            document.getElementById('inspectionDonationId').value = donationId;
            inspectionModal.style.display = 'block';
        });
    });

    document.querySelectorAll('.ship-btn').forEach(btn => {
        btn.addEventListener('click', function () {
            const donationId = this.getAttribute('data-id');
            document.getElementById('shippingDonationId').value = donationId;
            shippingModal.style.display = 'block';
        });
    });

    Array.from(closeBtns).forEach(btn => {
        btn.addEventListener('click', function () {
            inspectionModal.style.display = 'none';
            shippingModal.style.display = 'none';
        });
    });

    window.onclick = function (event) {
        if (event.target == inspectionModal) {
            inspectionModal.style.display = 'none';
        }
        if (event.target == shippingModal) {
            shippingModal.style.display = 'none';
        }
    }
});

function approveDonation() {
    const donationId = document.getElementById('inspectionDonationId').value;
    // Here you would typically make an AJAX call to your backend
    console.log('Approving donation:', donationId);
    // Update the UI or reload the page after successful approval
    location.reload();
}

function rejectDonation() {
    const donationId = document.getElementById('inspectionDonationId').value;
    // Here you would typically make an AJAX call to your backend
    console.log('Rejecting donation:', donationId);
    // Update the UI or reload the page after successful rejection
    location.reload();
}

function processShipping() {
    const donationId = document.getElementById('shippingDonationId').value;
    const trackingNumber = document.getElementById('trackingNumber').value;
    const shippingDate = document.getElementById('shippingDate').value;
    // Here you would typically make an AJAX call to your backend
    console.log('Processing shipping:', { donationId, trackingNumber, shippingDate });
    // Update the UI or reload the page after successful shipping processing
    location.reload();
}
// </script>