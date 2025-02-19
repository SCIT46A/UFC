document.addEventListener("DOMContentLoaded", () => {
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
            if (reportMenu.classList.contains("hidden")) {
                reportMenu.classList.remove("hidden");
                reportMenu.style.display = "block";

                // 버튼 바로 아래에 위치하도록 설정
                const rect = button.getBoundingClientRect();
                reportMenu.style.top = `${rect.bottom + window.scrollY}px`; // 버튼 바로 아래
                reportMenu.style.left = `${rect.left + window.scrollX}px`; // 버튼 왼쪽 맞춤
            } else {
                // 메뉴가 표시 중이면 숨기기
                reportMenu.classList.add("hidden");
                reportMenu.style.display = "none";
            }
        });
    });

    // 메뉴 외부 클릭 시 숨기기
    document.addEventListener("click", (event) => {
        if (!reportMenu.contains(event.target)) {
            reportMenu.classList.add("hidden");
            reportMenu.style.display = "none";
        }
    });
    // 메뉴 외부 클릭 시 숨기기
    document.addEventListener("click", (event) => {
        if (!reportMenu.contains(event.target)) {
            reportMenu.classList.add("hidden");
            reportMenu.style.display = "none";
        }
    });

    reportCall.addEventListener("click", () => {
        reportModal.classList.remove("hidden");
    });

    reportClose.addEventListener("click", () => {
        reportModal.classList.add("hidden");
    });

    const campaignTab = document.querySelector(
        ".main-mi-na-in-box-pe.campaign"
    );
    const boardTab = document.querySelector(".main-mi-na-in-box-pe.board");
    const replyTab = document.querySelector(".main-mi-na-in-box-pe.reply");

    const navItems = [campaignTab, boardTab, replyTab];

    function removeCheckClass() {
        navItems.forEach((nav) => nav.classList.remove("check"));
    }

    const contentBox = document.querySelector(".content-box");
    const replyBox = document.querySelector(".main-reply");
    const boardBox = document.querySelector(".main-board");

    // 캠페인 소개 클릭 이벤트
    campaignTab.addEventListener("click", () => {
        removeCheckClass();
        campaignTab.classList.add("check");
        contentBox.classList.remove("hidden");
        replyBox.classList.add("hidden");
        boardBox.classList.add("hidden");
    });

    // 게시판 클릭 이벤트
    boardTab.addEventListener("click", () => {
        removeCheckClass();
        boardTab.classList.add("check");
        contentBox.classList.add("hidden");
        boardBox.classList.remove("hidden");
        replyBox.classList.add("hidden");
    });

    // 댓글 클릭 이벤트
    replyTab.addEventListener("click", () => {
        removeCheckClass();
        replyTab.classList.add("check");
        contentBox.classList.add("hidden");
        boardBox.classList.add("hidden");
        replyBox.classList.remove("hidden");
    });

    // 게시판 부분에서 열기
    const boardContent = document.querySelectorAll(".main-board-pe");
    const boardTitle = document.querySelector(".main-board-total");
    const boardInnerBox = document.querySelector(".main-board-back-div");
    const boradAllBox = document.querySelector(".main-board-total-pe");
    const boradInnerContent = document.querySelector(".content-board-box");

    boardContent.forEach((data) => {
        data.addEventListener("click", () => {
            boardTitle.classList.add("hidden");
            boardInnerBox.classList.remove("hidden");
            boradAllBox.classList.add("hidden");
            boradInnerContent.classList.remove("hidden");
        });
    });

    // 원복
    const boradBackBtn = document.querySelector(".main-board-back");
    boradBackBtn.addEventListener("click", () => {
        boardTitle.classList.remove("hidden");
        boardInnerBox.classList.add("hidden");
        boradAllBox.classList.remove("hidden");
        boradInnerContent.classList.add("hidden");
    });
});
