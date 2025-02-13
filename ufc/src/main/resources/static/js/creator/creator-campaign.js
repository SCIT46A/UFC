// 프로필 수정하기 버튼 클릭 시 판매자 프로필 수정하기 페이지로 이동
const profile_edit = document.querySelector("#share");
profile_edit.addEventListener("click", function() {
    window.location.href = "../../templates/creator/creator-edit.html";
});

// 캠페인 제작하기 버튼 클릭 시 intro-campaign 페이지로 이동
const start_campaign = document.querySelector("#create_campaign");
start_campaign.addEventListener("click", function () {
    window.location.href = "../../templates/campaign/intro-campaign.html";
});

// 더보기(n/N) 버튼 구현
/* document.addEventListener("DOMContentLoaded", function() {
    const more_btn = document.querySelector(".show-more-btn");
    const itemList = document.querySelector("#item-list");

    more_btn.addEventListener("click", function () {
        let currentPage = parseInt(more_btn.getAttribute("data-page"));

        fetch(`/items/more?page=${currentPage}`)
            .then(response => response.json())
            .then(data => {
                if (data.length === 0) {
                    more_btn.innerText = "더 이상 항목이 없습니다.";
                    more_btn.disabled = true;
                    return;
                }

                data.forEach(item => {
                    const li = document.createElement("li");
                    li.textContent = item;
                    itemList.appendChild(li);
                });

                more_btn.setAttribute("data-page", currentPage + 1);
            })
            .catch(error => console.error("에러 발생:", error));
        })
    })
*/
