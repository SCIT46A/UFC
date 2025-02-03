document.addEventListener("DOMContentLoaded", () => {
    const rewardInfo = document.querySelector(".reward-info");
    const triggerScrollY = 1100; // 스크롤 기준 위치 설정

    window.addEventListener("scroll", () => {
        if (window.scrollY > triggerScrollY) {
            // 기준 위치를 넘으면 스타일 추가
            rewardInfo.style.zIndex = "1";
            rewardInfo.style.position = "fixed";
            rewardInfo.style.width = "354px";
            rewardInfo.style.overflow = "hiddenen auto";
            rewardInfo.style.margin = "0px 0px 0px -1px";
            rewardInfo.style.padding = "0px 0px 0px 1px";
            rewardInfo.style.top = "52px";
            rewardInfo.style.height = "calc(100% - 52px)";
            rewardInfo.style.scrollbarWidth = "none";
        } else {
            // 기준 위치를 넘지 않으면 기본 스타일 유지
            rewardInfo.style = null;
        }
    });

    const reportButtons = document.querySelectorAll(".report-menu"); // 여러 버튼 선택
    const reportMenu = document.querySelector(".ul-add");
    const reportCall = document.querySelector(".ul-add-li");
    const reportModal = document.querySelector(".report");
    const reportClose = document.querySelector(".report-close");

    // 모든 버튼에 클릭 이벤트 추가
    reportButtons.forEach((button) => {
        button.addEventListener("click", (event) => {
            event.stopPropagation(); // 클릭 이벤트 전파 방지

            // 메뉴가 숨겨져 있으면 표시
            if (reportMenu.classList.contains("hiddenen")) {
                reportMenu.classList.remove("hiddenen");
                reportMenu.style.display = "block";

                // 버튼 바로 아래에 위치하도록 설정
                const rect = button.getBoundingClientRect();
                reportMenu.style.top = `${rect.bottom + window.scrollY}px`; // 버튼 바로 아래
                reportMenu.style.left = `${rect.left + window.scrollX}px`; // 버튼 왼쪽 맞춤
            } else {
                // 메뉴가 표시 중이면 숨기기
                reportMenu.classList.add("hiddenen");
                reportMenu.style.display = "none";
            }
        });
    });

    // 메뉴 외부 클릭 시 숨기기
    document.addEventListener("click", (event) => {
        if (!reportMenu.contains(event.target)) {
            reportMenu.classList.add("hiddenen");
            reportMenu.style.display = "none";
        }
    });
    // 메뉴 외부 클릭 시 숨기기
    document.addEventListener("click", (event) => {
        if (!reportMenu.contains(event.target)) {
            reportMenu.classList.add("hiddenen");
            reportMenu.style.display = "none";
        }
    });

    reportCall.addEventListener("click", () => {
        reportModal.classList.remove("hiddenen");
    });

    reportClose.addEventListener("click", () => {
        reportModal.classList.add("hiddenen");
    });
});
