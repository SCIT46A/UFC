// 프로필 수정하기 버튼 클릭 시 판매자 프로필 수정하기 페이지로 이동
const profile_edit = document.querySelector("#share");
profile_edit.addEventListener("click", function() {

    // 페이지 로드 시 기존 데이터 삭제 (항상 빈칸 유지)
    localStorage.removeItem("profileName");
    localStorage.removeItem("profileCompany");
    localStorage.removeItem("profileIntro");
    localStorage.removeItem("coverImage");
    localStorage.removeItem("profileImage");

    window.location.href = "../../templates/creator/creator-edit.html";
});

// 캠페인 제작하기 버튼 클릭 시 intro-campaign 페이지로 이동
const start_campaign = document.querySelector("#create_campaign");
start_campaign.addEventListener("click", function () {
    window.location.href = "../../templates/campaign/intro-campaign.html";
});

// 더보기 버튼 기능
document.addEventListener("DOMContentLoaded", function() {
    const showMoreBtn = document.querySelector(".show-more-btn");
    const hiddenContent = document.querySelector(".hidden-content");

    showMoreBtn.addEventListener("click", function() {
        if (hiddenContent.style.display === "none" || hiddenContent.style.display === ""){
            hiddenContent.style.display = "block";
            showMoreBtn.querySelector("div").textContent = "접기";
        } else {
            hiddenContent.style.display = "none";
            showMoreBtn.querySelector("div").textContent = "더보기";
        }
    });
});

document.addEventListener("DOMContentLoaded", function() {
    const displayName = document.querySelector(".club-detail-name");
    const displayCompany = document.querySelector(".company_name");
    const displayIntro = document.querySelector(".club-detail-introduction");
    const displayCover = document.querySelector(".club-cover-image");
    const displayProfile = document.querySelector(".club-campaign-image");

    // 저장된 데이터 불러오기
    const storedName = localStorage.getItem("profileName");
    const storedCompany = localStorage.getItem("profileCompany");
    const storedIntro = localStorage.getItem("profileIntro");
    const storedCover = localStorage.getItem("coverImage");
    const storedProfile = localStorage.getItem("profileImage");

    displayName.textContent = storedName ? storedName : "이름 없음";
    displayCompany.textContent = storedCompany ? storedCompany : "상호명 없음";
    displayIntro.textContent = storedIntro ? storedIntro : "소개글 없음";
    displayCover.src = storedCover ? storedCover : "";
    if (!storedCover) {
        displayCover.alt = "이미지 없음";
    }
    displayProfile.src = storedProfile ? storedProfile : "";
    if (!storedProfile) {
        displayProfile.alt = "이미지 없음";
    }
});