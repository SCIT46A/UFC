// 텍스트 에디터
$(document).ready(function () {
    //썸머노트에 값넣기 (차후 값을 넣었을 때 저장하기 위한 코드)
    // $(".presentation-size").summernote("code", "입력된 텍스트를 넣으세요");

    //위와 같이 값을 먼저 넣어준 후 초기화를 시킨다. 그럼 아래와 같이 입력이 된다.
    //초기화
    $(".presentation-size").summernote({
        height: 400, // set editor height
        minHeight: null, // set minimum height of editor
        maxHeight: null, // set maximum height of editor
        focus: false,
        lang: "ko-KR", // 기본 메뉴언어 US->KR로 변경
    });

    //저장버튼 클릭( 행사 게시 클릭 시 조건부로 만들어서 저장할 것)
    $(document).on("click", "#saveBtn", function () {
        saveContent();
    });
});

document.addEventListener("DOMContentLoaded", () => {
    const firstPage = document.querySelector(".cam-in");
    const firstPageBtn = document.querySelector(".cam-in-box-content-btn");
    const cePage = document.querySelector(".cam-ag");
    const cePageBtnNext = document.querySelector(".cam-create-btn");
    const cePageBtnBack = document.querySelector(".cam-back-btn");
    const thPage = document.querySelector(".cam-la");
    const thPageBtnBack = document.querySelector(".cam-la-in-box-top-in-back");
    const listItems = document.querySelectorAll(
        ".cam-la-in-box-top-in-na-all-ul-li"
    );
    const contentBoxes = document.querySelectorAll(".cam-la-in-box-bo");

    firstPageBtn.addEventListener("click", () => {
        firstPage.classList.add("hidden");
        cePage.classList.remove("hidden");
    });

    cePageBtnNext.addEventListener("click", () => {
        cePage.classList.add("hidden");
        thPage.classList.remove("hidden");
    });

    cePageBtnBack.addEventListener("click", () => {
        firstPage.classList.remove("hidden");
        cePage.classList.add("hidden");
    });

    thPageBtnBack.addEventListener("click", () => {
        thPage.classList.add("hidden");
        cePage.classList.remove("hidden");
    });

    listItems.forEach((item) => {
        item.addEventListener("click", function () {
            // 모든 li에서 'check' 클래스 제거
            listItems.forEach((li) => li.classList.remove("check"));

            // 클릭한 li에 'check' 클래스 추가
            this.classList.add("check");

            // 모든 content-box 숨기기
            contentBoxes.forEach((box) => box.classList.add("hidden"));

            // 선택한 li의 data-target 속성에 해당하는 content-box 보이기
            const targetClass = this.getAttribute("data-target");
            document
                .querySelector(`.cam-la-in-box-bo.${targetClass}`)
                .classList.remove("hidden");
        });
    });
});
