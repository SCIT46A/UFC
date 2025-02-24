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
    displayProfile.src = storedProfile ? storedProfile : "";
    
    // 커버 이미지가 있으면 표시, 없으면 기본 이미지 설정정
    if (!storedCover) {
        displayCover.src = storedCover;
    } else {
        displayCover.src = "default-cover.jpg"; // 기본 커버 이미지
    };
    
    // 프로필 이미지가 있으면 표시, 없으면 기본 이미지 설정
    if (!storedProfile) {
        displayProfile.src = storedProfile;
    } else {
        displayProfile.src = "default-profile.jpg"; // 기본 이미지
    }
});

// 데이터를 받아와서 프로필에 구현하기
/*
document.addEventListener("DOMContentLoaded", async function () {
    const creatorList = document.getElementById("creatorList");

    try {
        // 승인된 창작가 (creatorStatus = 1) 데이터 가져오기
        const response = await fetch("");
        const creators = await response.json();

        if (creators.length === 0) {
            creatorList.innerHTML = "<p>현재 승인된 창작가가 없습니다.</p>";
        } else {
            creators.forEach(creator => {
                const div = document.createElement("div");
                div.innerHTML =
                    
            })
        }
    }
})*/



// 캠페인 데이터 받아서 더보기 버튼으로 구현
/*
document.addEventListener("DOMContentLoaded", function () {
    const contentList = document.getElementById("contentList");
    const toggleBtn = document.getElementById("toggleBtn");
    const currentCountEl = document.getElementById("currentCount");
    const totalCountEl = document.getElementById("totalCount");

    const maxVisible = 3; // 최초로 보이는 콘텐츠 개수 (3줄)
    const step = 3; // 한 번에 추가할 개수
    let visibleCount = maxVisible; // 현재 보이는 콘텐츠 개수
    const totalItems = 15; // 전체 콘텐츠 개수

    // ⭐ 더미 데이터 생성
    const contents = Array.from({ length: totalItems }, (_, i) => `main-bo-in-bo-pe-box-a ${i + 1}`);

    // 콘텐츠 리스트 동적 추가
    contents.forEach((content, index) => {
        const item = document.createElement("div");
        item.classList.add("content-item");
        item.textContent = content;
        if (index >= maxVisible) {
            item.classList.add("hidden"); // maxVisible 초과 콘텐츠 숨기기
        }
        contentList.appendChild(item);
    });

    // 총 개수 표시
    totalCountEl.textContent = totalItems;
    currentCountEl.textContent = visibleCount;

    // 콘텐츠 개수가 maxVisible보다 많을 경우에만 "더보기" 버튼 보이게 함
    if (totalItems > maxVisible) {
        toggleBtn.classList.remove("hidden-btn");
    }

    // "더보기 / 접기" 버튼 클릭 이벤트
    toggleBtn.addEventListener("click", function () {
        const hiddenItems = document.querySelectorAll(".content-item.hidden");

        if (hiddenItems.length > 0) {
            // ⭐ 더보기 기능
            hiddenItems.forEach((item, index) => {
                if (index < step) {
                    item.classList.remove("hidden"); // 콘텐츠 표시
                    visibleCount++;
                }
            });

            // 업데이트된 개수 표시
            currentCountEl.textContent = visibleCount;

            // 모든 콘텐츠가 표시되면 "접기"로 변경
            if (visibleCount >= totalItems) {
                toggleBtn.textContent = "접기";
            }
        } else {
            // ⭐ 접기 기능
            const allItems = document.querySelectorAll(".content-item");
            allItems.forEach((item, index) => {
                if (index >= maxVisible) {
                    item.classList.add("hidden"); // 초과 콘텐츠 숨기기
                }
            });

            // 개수 초기화
            visibleCount = maxVisible;
            currentCountEl.textContent = visibleCount;

            // 버튼을 다시 "더보기(n/N)"로 변경
            toggleBtn.textContent = `더보기 (${visibleCount}/${totalItems})`;
        }
    });
});
*/