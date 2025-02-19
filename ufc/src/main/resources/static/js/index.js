$(function () {

    // 최상단
    $.ajax({
        url: "/api/lowertDonation",
        method: "GET",
        success: function (response) {
            console.log(response)
            let htmlResult = "";
            response.forEach((data)=>{
                htmlResult += `
                <div class="bg-white rounded-lg shadow-lg overflow-hidden target-project-main">
                    <img src="https://img.tumblbug.com/eyJidWNrZXQiOiJ0dW1ibGJ1Zy1pbWctYXNzZXRzIiwia2V5IjoiY292ZXIvZTAxOGRjMDQtOTBkNS00NjE5LTgzZTQtYjAyN2IxNjQ0ODAwLzJlMzY3OGM2LWEzY2YtNGQ4ZS1iOTE0LTk0MjZmNjUwOWEyOS5qcGVnIiwiZWRpdHMiOnsicmVzaXplIjp7IndpZHRoIjo0NjUsImhlaWdodCI6NDY1LCJ3aXRob3V0RW5sYXJnZW1lbnQiOnRydWV9fX0=" alt="플라스틱 병으로 만든 패션 액세서리" class="w-full h-48 object-cover">
                    <div class="p-6">
                        <h1 class="target-project-main-seller text-gray-600 mb-4">
                            판매자는 판매판매
                        </h1>
                        <h3 class="text-xl font-semibold mb-2">
                            한국문학 불안을 1
                        </h3>
                        <p class="text-gray-600 mb-4">
                            글씨를 못쓴다고요? ㅋ 난 아닌데
                        </p>
                        <div>
                            <div class="progress mb-2">
                                <div class="progress-bar" style="width: 33%"></div>
                            </div>
                            <div class="flex justify-between text-sm text-gray-500 mb-4">
                                <span>33% 달성</span>
                                <span>목표: 조관익mk.1 3명</span>
                            </div>
                        </div>
                        <div>
                            <div class="progress mb-2">
                                <div class="progress-bar" style="width: 66%"></div>
                            </div>
                            <div class="flex justify-between text-sm text-gray-500 mb-4">
                                <span>66% 달성</span>
                                <span>목표: 조관익mk.2 3명</span>
                            </div>
                        </div>
                        <div class="flex justify-between items-center">
                            <span class="text-sm text-gray-500">240명의
                                후원자</span>
                            <button class="btn-outline">
                                후원하기
                            </button>
                        </div>
                    </div>
                </div>
                `
            })
            $(".topTarget").html(htmlResult);
        },
    });

    // 중간 인기 캠페인

    $.ajax({
        url: "/api/likeTopCampaign",
        method: "GET",
        success: function (response) {
            let htmlResult = "";
            response.forEach((data)=>{
                let tagHtml = "";
                // 2) 태그 배열이 존재하고, 0개 이상일 때 반복문 실행
                if (data.tags && data.tags.length > 0) {
                    data.tags.forEach((tagItem) => {
                        // 3) 태그 하나당 <div> 블록 생성
                        tagHtml += `
                    <div class="main-resent-tag-in-pe">
                      ${tagItem}
                    </div>
                  `;
                    });
                }
                htmlResult += `
                <div class="main-resent-pe">
                    <div class="main-resent-pe-all">
                        <div class="main-resent-pe-all-in">
                            <!-- 이미지 -->
                            <div class="main-resent-pe-all-in-img">
                                <img src="https://img.tumblbug.com/eyJidWNrZXQiOiJ0dW1ibGJ1Zy1pbWctYXNzZXRzIiwia2V5IjoiY292ZXIvMjRiMDQzOGItNTdkZi00ZGY1LTg4ODgtODhkZjA5YWY2M2U0LzE3ZTg5MjllLTM1MzktNDA4ZC1hZDY5LWYzMTNmYjE4Y2Q3My5wbmciLCJlZGl0cyI6eyJyZXNpemUiOnsid2lkdGgiOjQ2NSwiaGVpZ2h0Ijo0NjUsIndpdGhvdXRFbmxhcmdlbWVudCI6dHJ1ZX19fQ==" alt="" class="main-resent-pe-all-in-img-in">
                            </div>
                            <!-- 좋아요 나중에 추가 -->
                            <div></div>
                            <!-- 내용 -->
                            <div class="main-resent-pe-all-content">
                                <div class="main-resent-content-box">
                                    <!-- 판매자 -->
                                    <div class="main-resent-content-box-se">
                                        <div class="main-resent-content-box-se-in">
                                            ${data.sellerName}
                                        </div>
                                    </div>
                                    <!-- 내용 -->
                                    <div class="main-resent-content-box-content">
                                        <div class="main-resent-content-box-content-a">
                                            ${data.title}
                                        </div>
                                    </div>
                                    <!-- 달성 -->
                                    <div class="main-resent-per">
                                        ${data.donationPercentage}% 달성!
                                    </div>
                                    <!-- 태그 -->
                                    <div class="main-resent-tag">
                                        <div class="main-resent-tag-in">
                                            ${tagHtml}
                                        </div>
                                    </div>
                                </div>
                            </div>
                        </div>
                    </div>
                </div>
                `
            })
            $(".main-resent-all").html(htmlResult);
        },
    });


    // 하단 판매 인기순
    $.ajax({
        url: "/api/likeTopProduct",
        method: "GET",
        success: function (response) {
            let htmlResult = "";
            response.forEach((data)=>{
                let tagHtml = "";
                // 2) 태그 배열이 존재하고, 0개 이상일 때 반복문 실행
                if (data.tags && data.tags.length > 0) {
                    data.tags.forEach((tagItem) => {
                        // 3) 태그 하나당 <div> 블록 생성
                        tagHtml += `
                    <div class="main-resent-tag-in-pe">
                      ${tagItem}
                    </div>
                  `;
                    });
                }
                htmlResult += `
                <div class="main-resent-pe">
                    <div class="main-resent-pe-all">
                        <div class="main-resent-pe-all-in">
                            <!-- 이미지 -->
                            <div class="main-resent-pe-all-in-img">
                                <img src="https://img.tumblbug.com/eyJidWNrZXQiOiJ0dW1ibGJ1Zy1pbWctYXNzZXRzIiwia2V5IjoiY292ZXIvMjRiMDQzOGItNTdkZi00ZGY1LTg4ODgtODhkZjA5YWY2M2U0LzE3ZTg5MjllLTM1MzktNDA4ZC1hZDY5LWYzMTNmYjE4Y2Q3My5wbmciLCJlZGl0cyI6eyJyZXNpemUiOnsid2lkdGgiOjQ2NSwiaGVpZ2h0Ijo0NjUsIndpdGhvdXRFbmxhcmdlbWVudCI6dHJ1ZX19fQ==" alt="" class="main-resent-pe-all-in-img-in">
                            </div>
                            <!-- 좋아요 나중에 추가 -->
                            <div></div>
                            <!-- 내용 -->
                            <div class="main-resent-pe-all-content">
                                <div class="main-resent-content-box">
                                    <!-- 판매자 -->
                                    <div class="main-resent-content-box-se">
                                        <div class="main-resent-content-box-se-in">
                                            ${data.sellerName}
                                        </div>
                                    </div>
                                    <!-- 내용 -->
                                    <div class="main-resent-content-box-content">
                                        <div class="main-resent-content-box-content-a">
                                            ${data.title}
                                        </div>
                                    </div>
                                    <!-- 달성 -->
                                    <div class="main-resent-per">
                                        ${data.price}원
                                    </div>
                                    <!-- 태그 -->
                                    <div class="main-resent-tag">
                                        <div class="main-resent-tag-in">
                                            <!-- 각각의 태그 -->
                                            ${tagHtml}
                                        </div>
                                    </div>
                                </div>
                            </div>
                        </div>
                    </div>
                </div>
                `
            })
            $(".main-resent-all-add").html(htmlResult);
        },
    });


    // 초기 이벤트 바인딩은 페이지 로드 시 단 한 번만 실행
    initializeEventListeners();


    function initializeEventListeners() {
        if (typeof lucide !== "undefined" && lucide.createIcons) {
            lucide.createIcons();
        }

        // 현재 연도를 푸터에 삽입
        const currentYearElement = document.getElementById("current-year");
        if (currentYearElement) {
            currentYearElement.textContent = new Date().getFullYear();
        }

        // 앵커 링크에 대한 부드러운 스크롤 효과
        document.querySelectorAll('a[href^="#"]').forEach((anchor) => {
            anchor.addEventListener("click", (e) => {
                e.preventDefault();
                const target = document.querySelector(anchor.getAttribute("href"));
                if (target) {
                    target.scrollIntoView({ behavior: "smooth" });
                }
            });
        });

        // 모바일 메뉴 토글 (요소 존재 여부 확인)
        const mobileMenuButton = document.getElementById("mobile-menu-button");
        const mobileMenu = document.getElementById("mobile-menu");

        if (mobileMenuButton && mobileMenu) {
            mobileMenuButton.addEventListener("click", () => {
                mobileMenu.classList.toggle("hidden");
            });
        }

        // 🌟 메인페이지 슬라이드 기능
        const slider = document.querySelector(".slider");
        const slides = document.querySelectorAll(".image-container");
        const prevBtn = document.querySelector(".main-container-count-control-l");
        const nextBtn = document.querySelector(".main-container-count-control-r");
        const pageCount = document.querySelector(".main-container-count-point");

        if (slider && slides.length > 0 && prevBtn && nextBtn && pageCount) {
            let currentIndex = 0;
            const totalSlides = slides.length;

            // 슬라이드 이동 함수
            function moveSlide(index) {
                const slideWidth = 584; // 개별 슬라이드 너비 (px 단위)
                slider.style.transform = `translateX(-${index * slideWidth}px)`;
                pageCount.innerText = index + 1;
            }

            // 다음 슬라이드 이동
            function nextSlide() {
                currentIndex = (currentIndex + 1) % totalSlides;
                moveSlide(currentIndex);
            }

            // 이전 슬라이드 이동
            function prevSlide() {
                currentIndex = (currentIndex - 1 + totalSlides) % totalSlides;
                moveSlide(currentIndex);
            }

            // 자동 슬라이드 (4초마다 이동)
            let slideInterval = setInterval(nextSlide, 4000);

            // 좌우 버튼 클릭 이벤트 추가
            nextBtn.addEventListener("click", () => {
                clearInterval(slideInterval);
                nextSlide();
                slideInterval = setInterval(nextSlide, 4000);
            });

            prevBtn.addEventListener("click", () => {
                clearInterval(slideInterval);
                prevSlide();
                slideInterval = setInterval(nextSlide, 4000);
            });
        }


    }
});
