$(function () {
    const pathParts = window.location.pathname.split("/");
    const query = decodeURIComponent(pathParts[pathParts.length - 1]);

    if (!query) return; // 검색어가 없으면 종료

    $.ajax({
        url: `/api/search/tag/${encodeURIComponent(query)}`,
        method: "GET",
        success: (response) => {
            console.log(response);
            $(".main-bo-in-bo").html(""); // 기존 결과 초기화

            if (response.length > 0) {
                response.forEach((data) => {
                    $(".main-bo-in-bo").append(`
                        <div class="main-bo-in-bo-pe">
                            <div class="main-bo-in-bo-pe-box">
                                <a href="/" class="main-bo-in-bo-pe-box-a">
                                    <div class="main-bo-in-bo-pe-box-a-img">
                                        <img src="https://img.tumblbug.com/eyJidWNrZXQiOiJ0dW1ibGJ1Zy1pbWctYXNzZXRzIiwia2V5IjoiY292ZXIvODg3ODQ0Y2YtZTIwZS00MWRkLTg5MDMtYjJlMWNmOTFkYmI2LzMyNWIzNDU3LTkyNDUtNDg3MS1hYTUxLTgwYmJlZGZlYTU0Yi5qcGVnIiwiZWRpdHMiOnsicmVzaXplIjp7IndpZHRoIjo0NjUsImhlaWdodCI6NDY1LCJ3aXRob3V0RW5sYXJnZW1lbnQiOnRydWV9fX0=" alt="" class="main-bo-in-bo-pe-box-a-img-size" />
                                        <div class="main-like-btn">
                                            <svg width="40" height="40" viewBox="0 0 40 40" fill="none" xmlns="http://www.w3.org/2000/svg">
                                                <path d="M19.463 15.3087L19.9993 15.7933L20.5356 15.3087L22.2643 13.747C22.2643 13.747 22.2643 13.7469 22.2643 13.7469C23.615 12.5269 25.9852 12.7532 27.3097 14.2118L27.3156 14.2182L27.3216 14.2246C28.9912 15.9843 29.0145 19.0158 27.2167 20.9218L19.9995 27.9864L12.7818 20.9218C10.9839 19.0158 11.0075 15.984 12.6769 14.2246L12.6829 14.2182L12.6888 14.2118C14.0133 12.7532 16.3836 12.5269 17.7343 13.7469C17.7343 13.7469 17.7343 13.7469 17.7344 13.747L19.463 15.3087Z" fill="black" fill-opacity="0.25" stroke="white" stroke-width="1.6"></path>
                                            </svg>
                                        </div>
                                    </div>
                                    <div class="main-bo-in-bo-pe-box-a-title">
                                        <div class="main-bo-in-bo-pe-box-a-title-top">
                                            <div class="main-bo-in-bo-pe-box-a-title-top-se">
                                                <div>판매자는 판매판매</div>
                                            </div>
                                            <div class="main-bo-in-bo-pe-box-a-title-mi">
                                                <div class="main-bo-in-bo-pe-box-a-title-mi-title">완벽적중<한국풍 판타지 타로카드:삼라만상></div>
                                                <div class="main-bo-in-bo-pe-box-a-title-mi-content">화려한 자개박으로 꾸며진 나전칠기 컨셉의 한국 전통 판타지풍 타로카드 78장</div>
                                            </div>
                                            <div class="main-bo-in-bo-pe-box-a-title-bo">
                                                <div class="main-bo-in-bo-pe-box-a-title-bo-in"></div>
                                            </div>
                                        </div>
                                        <div class="main-funding">
                                            <div class="main-funding-top">
                                                <div>
                                                    <span class="main-funding-top-per">1234%</span>
                                                    <span class="main-funding-top-pri">12개 모임</span>
                                                </div>
                                                <em>28일 남음</em>
                                            </div>
                                            <div class="main-funding-bo"></div>
                                        </div>
                                    </div>
                                </a>
                            </div>
                        </div>
                    `);
                });
            } else {
                $(".main-bo-in-bo").html("<div>검색 결과가 없습니다!</div>");
            }

            initializeEventListeners(); // ✅ 한 번만 호출
        },
        error: (xhr, status, error) => {
            console.error("검색 오류:", error);
        },
    });

    function initializeEventListeners() {
        if (window.lucide) {
            lucide.createIcons();
        } else {
            console.error("Lucide not loaded");
        }

        // Current Year Update
        const yearElement = document.getElementById("current-year");
        if (yearElement) {
            yearElement.textContent = new Date().getFullYear();
        }

        // ✅ 중복 이벤트 등록 방지
        if (!window.modalEventsInitialized) {
            window.modalEventsInitialized = true;

            const perModal = document.querySelector(".main-modal-per");
            const perBtn = document.querySelector(
                ".main-top-in-content-target-box-span-btn"
            );
            const ModalClose = document.querySelector(".main-modal-per-close");
            const reBModal = document.querySelector(".main-modal-per-add");
            const reBtn = document.querySelector(
                ".main-top-in-content-target-box-span-btn-re"
            );

            perBtn?.addEventListener("click", () => {
                perModal.style.display = "flex";
                ModalClose.style.display = "block";
            });

            ModalClose?.addEventListener("click", () => {
                perModal.style.display = "none";
                reBModal.style.display = "none";
                ModalClose.style.display = "none";
            });

            reBtn?.addEventListener("click", () => {
                reBModal.style.display = "flex";
                ModalClose.style.display = "block";
            });
        }

        $(document).on("focus", ".main-search-box", function () {
            $(".modal-tag-search").css("display", "flex");
        });

        $(document).on("click", "#modal-controller-tag-search", function () {
            $(".modal-tag-search").css("display", "none");
        });

        // ✅ X 버튼을 클릭하면 해당 태그 삭제
        document.addEventListener("click", function (event) {
            if (event.target.closest(".main-search-tag-pe-btn")) {
                const tagElement = event.target.closest(".main-search-tag-pe");
                if (tagElement) {
                    tagElement.remove(); // ✅ 해당 태그만 삭제
                }
            }
        });
    }
});
