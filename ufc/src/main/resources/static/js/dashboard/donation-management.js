function initDonationManagement() {
    console.log("🎁 기부 내역 관리 JS 실행됨");

    // ✅ "전체 선택" 체크박스 기능 (체크박스가 있는지 확인 후 실행)
    const selectAllCheckbox = document.getElementById("selectAll");
    if (selectAllCheckbox) {
        selectAllCheckbox.addEventListener("change", (e) => {
            document.querySelectorAll('tbody input[type="checkbox"]').forEach(checkbox => {
                checkbox.checked = e.target.checked;
            });
        });
    }

    // ✅ 일괄 송장 등록 모달 기능
    window.openBatchInvoiceModal = function () {
        document.getElementById("batchInvoiceModal").classList.add("active");
    };

    window.closeBatchInvoiceModal = function () {
        document.getElementById("batchInvoiceModal").classList.remove("active");
    };

    window.processBatchInvoice = function () {
        alert("송장번호가 일괄 등록되었습니다.");
        closeBatchInvoiceModal();
    };

    // ✅ 기부 상태 변경 기능
    window.changeDonationStatus = function () {
        const selectedDonations = Array.from(document.querySelectorAll('tbody input[type="checkbox"]:checked'))
            .map(checkbox => checkbox.value);

        if (selectedDonations.length === 0) {
            alert("기부를 선택해주세요.");
            return;
        }

        console.log("📋 선택된 기부 상태 변경:", selectedDonations);
    };

    // ✅ 송장번호 업데이트 기능
    window.updateTrackingNumber = function (button) {
        const input = button.previousElementSibling;
        const trackingNumber = input.value;

        if (!trackingNumber) {
            alert("송장번호를 입력해주세요.");
            return;
        }

        console.log("🚚 업데이트된 송장번호:", trackingNumber);
        alert("송장번호가 저장되었습니다.");
    };

    // ✅ 기부 상세 보기 기능
    window.viewDonationDetail = function (button) {
        const row = button.closest("tr");
        const donationNumber = row.cells[2]?.textContent.trim();

        if (donationNumber) {
            console.log("📄 기부 상세 조회:", donationNumber);
        }
    };

    // ✅ 기부 상태 업데이트 기능
    window.updateDonationStatus = function (button) {
        const row = button.closest("tr");
        const donationNumber = row.cells[2]?.textContent.trim();

        if (donationNumber) {
            console.log("🎁 기부 상태 업데이트:", donationNumber);
        }
    };

    // ✅ 선택된 기부 일괄 처리 기능
    window.processSelectedDonations = function () {
        const selectedDonations = Array.from(document.querySelectorAll('tbody input[type="checkbox"]:checked'))
            .map(checkbox => checkbox.value);

        if (selectedDonations.length === 0) {
            alert("처리할 기부를 선택해주세요.");
            return;
        }

        console.log("🎁 선택된 기부 일괄 처리:", selectedDonations);
    };

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
}

function applyFilters() {
    const status = document.getElementById('status')?.value;
    const startDate = document.querySelector(".date-range input[type='date']")?.value;
    const endDate = document.querySelector(".date-range input[type='date']:nth-of-type(2)")?.value;

    console.log('📊 필터 적용:', { status, startDate, endDate });
}

// 🚀 fragment가 변경될 때마다 JS를 다시 실행하도록 설정
document.addEventListener("reapplyEventListeners", initDonationManagement);
