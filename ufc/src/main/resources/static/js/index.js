$(function () {

    // 공통 함수: 지정된 셀렉터 내의 모든 이미지에 대해 최종 이미지 URL을 업데이트합니다.
    function updateImageUrls(selector) {
        $(selector).each(function() {
            const $img = $(this);
            const endpoint = $img.attr("src"); // 예: /api/image/{imageId} 엔드포인트
            $.ajax({
                url: endpoint,
                method: "GET",
                success: function(resultUrl) {
                    if (resultUrl) {
                        $img.attr("src", resultUrl);
                    }
                },
                error: function(err) {
                    console.error("이미지 URL 요청 오류:", err);
                }
            });
        });
    }

    // 최상단 섹션: 낮은 기부율 캠페인
    $.ajax({
        url: "/api/lowertDonation",
        method: "GET",
        success: function (response) {
            let htmlResult = "";
            console.log(response);
            // 반복문 매개변수 이름을 campaign으로 통일
            response.forEach((campaign) => {
                let goalsHtml = "";
                if (campaign.goals && campaign.goals.length > 0) {
                    campaign.goals.forEach((goal) => {
                        goalsHtml += `
                        <div class="goal mb-4">
                            <div class="progress mb-2">
                                <div class="progress-bar" style="width: ${goal.donationPercentage}%"></div>
                            </div>
                            <div class="flex justify-between text-sm text-gray-500">
                                <span>${goal.donationPercentage}% 달성</span>
                                <span>목표: ${goal.goalTitle} (${goal.requiredQuantity}개)</span>
                            </div>
                        </div>
                    `;
                    });
                } else {
                    goalsHtml = `<div class="text-sm text-gray-500 mb-4">목표 정보가 없습니다.</div>`;
                }

                htmlResult += `
                <div class="bg-white rounded-lg shadow-lg overflow-hidden target-project-main mb-6">
                    <img alt="" src="/api/image/${campaign.imageId}" class="w-full h-48 object-cover campaign-image">
                    <div class="p-6">
                        <h1 class="target-project-main-seller text-gray-600 mb-4">
                            ${campaign.sellerName}
                        </h1>
                        <h3 class="text-xl font-semibold mb-2">
                            ${campaign.campaignTitle}
                        </h3>
                        <p class="text-gray-600 mb-4">
                            ${campaign.campaignDescription}
                        </p>
                        ${goalsHtml}
                        <div class="flex justify-between items-center">
                            <span class="text-sm text-gray-500">후원자: ${campaign.campaignDonors ? campaign.campaignDonors : 0}</span>
                            <a class="btn-outline" href="/campaign/${campaign.campaignId}">
                                후원하기
                            </a>
                        </div>
                    </div>
                </div>
            `;
            });
            $(".topTarget").html(htmlResult);
            updateImageUrls(".topTarget img.campaign-image");
        },
        error: function (err) {
            console.error("최상단 AJAX 오류:", err);
        }
    });



    // 중간 인기 캠페인
    $.ajax({
        url: "/api/likeTopCampaign",
        method: "GET",
        success: function (response) {
            let htmlResult = "";
            if (response.length < 6) {
                document.querySelector(".main-resent-all-add-btn-ri").classList.add("hidden");
            }
            response.forEach((data) => {
                let tagHtml = "";
                if (data.tags && data.tags.length > 0) {
                    data.tags.forEach((tagItem) => {
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
                            <a class="main-resent-pe-all-in-img" href="/campaign/${data.originalId}">
                                <img alt="" src="/api/image/${data.imageId}" class="main-resent-pe-all-in-img-in">
                            </a>
                            <div></div>
                            <div class="main-resent-pe-all-content">
                                <div class="main-resent-content-box">
                                    <div class="main-resent-content-box-se">
                                        <div class="main-resent-content-box-se-in">
                                            ${data.sellerName}
                                        </div>
                                    </div>
                                    <div class="main-resent-content-box-content">
                                        <div class="main-resent-content-box-content-a">
                                            ${data.title}
                                        </div>
                                    </div>
                                    <div class="main-resent-per">
                                        ${data.donationPercentage}% 달성!
                                    </div>
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
                `;
            });
            $(".main-resent-all").html(htmlResult);
            updateImageUrls("img.main-resent-pe-all-in-img-in");
        },
        error: function (err) {
            console.error("중간 인기 캠페인 AJAX 오류:", err);
        }
    });

    // 하단 판매 인기순
    $.ajax({
        url: "/api/likeTopProduct",
        method: "GET",
        success: function (response) {
            let htmlResult = "";
            if (response.length < 6) {
                document.querySelector(".main-resent-all-add-btn-add-ri").classList.add("hidden");
            }
            response.forEach((data) => {
                let tagHtml = "";
                if (data.tags && data.tags.length > 0) {
                    data.tags.forEach((tagItem) => {
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
                            <a class="main-resent-pe-all-in-img" href="/product/${data.originalId}">
                                <img alt="" src="/api/image/${data.imageId}" class="main-resent-pe-all-in-img-in">
                            </a>
                            <div></div>
                            <div class="main-resent-pe-all-content">
                                <div class="main-resent-content-box">
                                    <div class="main-resent-content-box-se">
                                        <div class="main-resent-content-box-se-in">
                                            ${data.sellerName}
                                        </div>
                                    </div>
                                    <div class="main-resent-content-box-content">
                                        <div class="main-resent-content-box-content-a">
                                            ${data.title}
                                        </div>
                                    </div>
                                    <div class="main-resent-per">
                                        ${data.price}원
                                    </div>
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
                `;
            });
            $(".main-resent-all-add").html(htmlResult);
            updateImageUrls("img.main-resent-pe-all-in-img-in");
        },
        error: function (err) {
            console.error("하단 판매 인기순 AJAX 오류:", err);
        }
    });

    // 초기 이벤트 바인딩은 페이지 로드 시 단 한 번만 실행
    initializeEventListeners();

    function initializeEventListeners() {
        if (typeof lucide !== "undefined" && lucide.createIcons) {
            lucide.createIcons();
        }

        const currentYearElement = document.getElementById("current-year");
        if (currentYearElement) {
            currentYearElement.textContent = new Date().getFullYear();
        }

        document.querySelectorAll('a[href^="#"]').forEach((anchor) => {
            anchor.addEventListener("click", (e) => {
                e.preventDefault();
                const target = document.querySelector(anchor.getAttribute("href"));
                if (target) {
                    target.scrollIntoView({ behavior: "smooth" });
                }
            });
        });

        const mobileMenuButton = document.getElementById("mobile-menu-button");
        const mobileMenu = document.getElementById("mobile-menu");
        if (mobileMenuButton && mobileMenu) {
            mobileMenuButton.addEventListener("click", () => {
                mobileMenu.classList.toggle("hidden");
            });
        }

        const slider = document.querySelector(".slider");
        const slides = document.querySelectorAll(".image-container");
        const prevBtn = document.querySelector(".main-container-count-control-l");
        const nextBtn = document.querySelector(".main-container-count-control-r");
        const pageCount = document.querySelector(".main-container-count-point");
        if (slider && slides.length > 0 && prevBtn && nextBtn && pageCount) {
            let currentIndex = 0;
            const totalSlides = slides.length;
            function moveSlide(index) {
                const slideWidth = 584; // 슬라이드 너비 (px)
                slider.style.transform = `translateX(-${index * slideWidth}px)`;
                pageCount.innerText = index + 1;
            }
            function nextSlide() {
                currentIndex = (currentIndex + 1) % totalSlides;
                moveSlide(currentIndex);
            }
            function prevSlide() {
                currentIndex = (currentIndex - 1 + totalSlides) % totalSlides;
                moveSlide(currentIndex);
            }
            let slideInterval = setInterval(nextSlide, 4000);
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

        const campaignRightBtn = document.querySelector(".main-resent-all-add-btn-ri");
        const campaignLeftBtn = document.querySelector(".main-resent-all-add-btn-le");
        const campaignView = document.querySelector("#campaign-target");
        campaignRightBtn.addEventListener("click", () => {
            campaignView.classList.add("target-slide");
            campaignLeftBtn.classList.remove("hidden");
            campaignRightBtn.classList.add("hidden");
        });
        campaignLeftBtn.addEventListener("click", () => {
            campaignView.classList.remove("target-slide");
            campaignRightBtn.classList.remove("hidden");
            campaignLeftBtn.classList.add("hidden");
        });

        const productRightBtn = document.querySelector(".main-resent-all-add-btn-add-ri");
        const productLeftBtn = document.querySelector(".main-resent-all-add-btn-add-le");
        const productView = document.querySelector("#product-target");
        productRightBtn.addEventListener("click", () => {
            productView.classList.add("target-slide");
            productLeftBtn.classList.remove("hidden");
            productRightBtn.classList.add("hidden");
        });
        productLeftBtn.addEventListener("click", () => {
            productView.classList.remove("target-slide");
            productRightBtn.classList.remove("hidden");
            productLeftBtn.classList.add("hidden");
        });
    }
});
