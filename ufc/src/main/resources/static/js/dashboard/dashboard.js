// document.addEventListener("DOMContentLoaded", function () {
//     // 하위 메뉴 토글 기능
//     document.querySelectorAll(".parent-menu").forEach(parent => {
//         parent.addEventListener("click", function () {
//             const targetMenu = document.getElementById(this.getAttribute("data-toggle") + "-menu");
//             if (targetMenu) {
//                 targetMenu.classList.toggle("active"); // 하위 메뉴 열고 닫기
//             }
//         });
//     });

//     // 메뉴 클릭 시 AJAX 로드
//     document.querySelectorAll(".menu-item[data-url]").forEach(item => {
//         item.addEventListener("click", function () {
//             const url = this.getAttribute("data-url");
//             fetch(url)
//                 .then(response => response.text())
//                 .then(html => {
//                     document.querySelector(".content-wrapper").innerHTML = html;
//                 })
//                 .catch(error => console.error("페이지 로딩 오류:", error));
//         });
//     });
// });


document.addEventListener("DOMContentLoaded", function () {
    initMenuEvents(); // ✅ 최초 로딩 시 메뉴 이벤트 등록

    // 메뉴 클릭 시 AJAX로 fragment 로드
    document.querySelectorAll(".menu-item[data-url]").forEach(item => {
        item.addEventListener("click", function () {
            const url = this.getAttribute("data-url");

            fetch(url)
                .then(response => response.text())
                .then(html => {
                    const contentWrapper = document.querySelector(".content-wrapper");
                    contentWrapper.innerHTML = html;
                    initMenuEvents(); // ✅ fragment 로드 후 이벤트 다시 등록
                })
                .catch(error => console.error("페이지 로딩 오류:", error));
        });
    });
});

// ✅ 메뉴 이벤트를 별도 함수로 분리 (fragment 변경 시 재등록 가능하도록)
function initMenuEvents() {
    // 하위 메뉴 토글 기능
    document.querySelectorAll(".parent-menu").forEach(parent => {
        parent.removeEventListener("click", toggleSubMenu); // 중복 등록 방지
        parent.addEventListener("click", toggleSubMenu);
    });

    // fragment 내의 추가된 버튼 이벤트 등록
    document.querySelectorAll(".menu-item[data-url]").forEach(item => {
        item.removeEventListener("click", loadFragmentPage); // 중복 등록 방지
        item.addEventListener("click", loadFragmentPage);
    });
}

// ✅ 하위 메뉴 열고 닫기 함수
function toggleSubMenu() {
    const targetMenu = document.getElementById(this.getAttribute("data-toggle") + "-menu");
    if (targetMenu) {
        targetMenu.classList.toggle("active"); // 하위 메뉴 열고 닫기
    }
}

// ✅ AJAX로 fragment 로드하는 함수
function loadFragmentPage(event) {
    const url = this.getAttribute("data-url");

    fetch(url)
        .then(response => response.text())
        .then(html => {
            const contentWrapper = document.querySelector(".content-wrapper");
            contentWrapper.innerHTML = html;
            initMenuEvents(); // ✅ fragment가 변경되면 다시 이벤트 등록
        })
        .catch(error => console.error("페이지 로딩 오류:", error));

    event.preventDefault(); // 기본 클릭 동작 방지
}
