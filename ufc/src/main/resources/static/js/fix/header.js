$(function () {
    function search() {
        const searchText = $(".modal-search-box-in-top-input-in").val().trim();
        if (searchText === "") return; // 빈 값 방지

        $.ajax({
            url: `/campagin/all/${encodeURIComponent(searchText)}`, // URL 인코딩 추가
            method: "GET",
            success: (response) => {
                console.log("검색 성공:", response);

                // 검색 결과 페이지로 이동
                window.location.href = `/campagin/all/${encodeURIComponent(
                    searchText
                )}`;
            },
            error: (xhr, status, error) => {
                console.error("검색 실패:", error);
            },
        });
    }

    // 최초 알림창 확인
    $.ajax({
        url: "/check-alert",
        method: "GET",
        success: function (response) {
            if (response) {
                $(".main-box-sign-on").removeClass("hidden");
            }
        },
        error: function (xhr, status, error) {
            console.error("알람 확인 실패:", error);
        },
    });

    //  로그인정보가 보고 나타내기

    $.ajax({
        url: "/check-login",
        method: "GET",
        success: function (response) {
            console.log(response);
            // 일반유저
            if (response.roles === "USER") {
                $(".header-box-top-pe").append(`
                    <div class="header-box-top-pe-my">
                        <div class="header-box-top-pe-my-in">
                            <!-- 프로필 -->
                            <div class="header-box-top-pe-my-in-pro">
                                <span class="profile-th">
                                    <svg
                                        class="UserProfileButton__AvatarIcon-sc-1amdfbl-5 bAnHGm"
                                        width="48"
                                        height="48"
                                        viewBox="0 0 48 48"
                                        fill="none"
                                        xmlns="http://www.w3.org/2000/svg"
                                    ></svg>
                                </span>
                            </div>
                            <!-- 이름 -->
                            <div class="header-box-top-pe-my-in-name">
                                ${response.userName}
                            </div>
                        </div>
                        <!-- 로그인창모달 -->
                    </div>
                `);
                initializeEventListeners();
            }
            // 판매자
            else if (response.roles === "CREATOR") {
                $(".header-box-top-pe").append(`
                    <div class="header-box-top-pe-my-add">
                        <div class="header-box-top-pe-my-in">
                            <!-- 프로필 -->
                            <div class="header-box-top-pe-my-in-pro">
                                <span class="profile-th">
                                    <svg
                                        class="UserProfileButton__AvatarIcon-sc-1amdfbl-5 bAnHGm"
                                        width="48"
                                        height="48"
                                        viewBox="0 0 48 48"
                                        fill="none"
                                        xmlns="http://www.w3.org/2000/svg"
                                    ></svg>
                                </span>
                            </div>
                            <!-- 이름 -->
                            <div class="header-box-top-pe-my-in-name">
                                ${response.userName}
                            </div>
                        </div>
                        <!-- 로그인창모달 -->
                    </div>
                `);
                initializeEventListeners();
            }
            // 비로그인
            else {
                $(".header-box-top-pe").append(
                    `
                <a class="header-box-top-pe-my" href="/user/login">
                    <div class="header-box-top-pe-my-in">

                        <div class="header-box-top-pe-my-in-name-no">
                            로그인 / 회원가입
                        </div>
                    </div>
                </a>
                `
                );
            }
        },
    });

    // 🔹 이벤트 리스너 초기화 함수 (AJAX 이후 실행)
    function initializeEventListeners() {
        // Lucide Icons Initialization
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

        // Login Modal (AJAX 후 추가된 요소에 이벤트 바인딩)
        $(document).on("click", ".header-box-top-pe-my", function () {
            $(".modal-login").css("display", "flex");
        });

        $(document).on("click", "#modal-controller", function () {
            $(".modal-login").css("display", "none");
        });

        $(document).on("click", ".header-box-top-pe-my-add", function () {
            $(".modal-login-add").css("display", "flex");
        });

        $(document).on("click", "#modal-controller-add", function () {
            $(".modal-login-add").css("display", "none");
        });

        // Category Modal
    }
});
