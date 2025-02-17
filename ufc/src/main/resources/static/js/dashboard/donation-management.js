function initDonationManagement() {
    console.log("🎁 기부 내역 관리 JS 실행됨");

    const statusFilter = document.getElementById("statusFilter");
    const table = document.querySelector("table");
    const inspectionModal = document.getElementById("inspectionModal");
    const shippingModal = document.getElementById("shippingModal");
    const closeBtns = document.getElementsByClassName("close");

    if (statusFilter) {
        statusFilter.addEventListener("change", function () {
            const rows = table.querySelectorAll("tbody tr");
            const selectedStatus = this.value;

            rows.forEach(row => {
                const statusCell = row.querySelector("td:nth-child(3)");
                const status = statusCell.textContent.trim();

                if (selectedStatus === "all" || status === selectedStatus) {
                    row.style.display = "";
                } else {
                    row.style.display = "none";
                }
            });
        });
    }

    document.querySelectorAll(".inspect-btn").forEach(btn => {
        btn.addEventListener("click", function () {
            const donationId = this.getAttribute("data-id");
            document.getElementById("inspectionDonationId").value = donationId;
            inspectionModal.style.display = "block";
        });
    });

    document.querySelectorAll(".ship-btn").forEach(btn => {
        btn.addEventListener("click", function () {
            const donationId = this.getAttribute("data-id");
            document.getElementById("shippingDonationId").value = donationId;
            shippingModal.style.display = "block";
        });
    });

    Array.from(closeBtns).forEach(btn => {
        btn.addEventListener("click", function () {
            inspectionModal.style.display = "none";
            shippingModal.style.display = "none";
        });
    });

    window.onclick = function (event) {
        if (event.target == inspectionModal) {
            inspectionModal.style.display = "none";
        }
        if (event.target == shippingModal) {
            shippingModal.style.display = "none";
        }
    };
}

function approveDonation() {
    const donationId = document.getElementById("inspectionDonationId").value;
    console.log("✅ 기부 승인:", donationId);
    // TODO: 서버에 AJAX 요청 추가
    location.reload();
}

function rejectDonation() {
    const donationId = document.getElementById("inspectionDonationId").value;
    console.log("❌ 기부 거절:", donationId);
    // TODO: 서버에 AJAX 요청 추가
    location.reload();
}

function processShipping() {
    const donationId = document.getElementById("shippingDonationId").value;
    const trackingNumber = document.getElementById("trackingNumber").value;
    const shippingDate = document.getElementById("shippingDate").value;
    console.log("🚚 발송 처리:", { donationId, trackingNumber, shippingDate });
    // TODO: 서버에 AJAX 요청 추가
    location.reload();
}

// 🚀 fragment가 변경될 때마다 JS를 다시 실행하도록 설정
document.addEventListener("reapplyEventListeners", initDonationManagement);
