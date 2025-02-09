$(function () {
    function search() {
        const searchText = $(".modal-search-box-in-top-input-in").val().trim();
        if (searchText === "") return; // 빈 값 방지

        $.ajax({
            url: `/campaign/all/${encodeURIComponent(searchText)}`, // URL 인코딩 추가
            method: "GET",
            success: (response) => {
                console.log("검색 성공:", response);

                // 검색 결과 페이지로 이동
                window.location.href = `/campaign/all/${encodeURIComponent(
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
            console.log(response);
            if (response && response.length > 0) {  // response가 null이 아니고, 데이터가 있는 경우
                $(".main-box-sign-on").removeClass("hidden");
            }
        },
        error: function (xhr, status, error) {
            console.error("Error:", error); // 에러 발생 시 콘솔에 출력
        }
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
                initializeEventListeners();
            }
        },
    });

    $.ajax({
        url: "/check-tag",
        method: "GET",
        success: function (response) {
            response.forEach((data)=> {
                $(".modal-re-search-box-top").append(
                `
                <a
                    href="/campaign/all/${data.content}"
                    class="modal-re-search-box-top-tag"
                >
                    <div
                        class="modal-re-search-box-top-tag-le"
                    >
                        #
                    </div>
                    <div
                        class="modal-re-search-box-top-tag-ri"
                    >
                        ${data.content}
                    </div>
                </a>
                `);

            })
        },

    });

    // 특수문자를 이스케이프하는 함수 (정규식용)
    function escapeRegExp(string) {
        return string.replace(/[.*+?^${}()|[\]\\]/g, '\\$&');
    }

    /**
     * text에서 query와 일치하는 부분을 <em> 태그로 감싸서 리턴합니다.
     * (대소문자 구분 없이 검색)
     */
    function highlightText(text, query) {
        if (!query) return text;
        const escapedQuery = escapeRegExp(query);
        const regex = new RegExp('(' + escapedQuery + ')', 'gi');
        return text.replace(regex, '<em>$1</em>');
    }

    // 예: src/main/resources/static/js/search.js
    $(document).on("input", ".modal-search-box-in-top-input-in", function () {
        const keyword = $(this).val().trim();

        if (keyword === "") {
            $(".modal-re-search-box-bo-box-in-se").html(""); // 검색창 비우기
            return;
        }

        $.ajax({
            url: "/search-box",
            method: "GET",
            data: { keyword: keyword },
            success: function (response) {
                $(".modal-re-search-box-bo-box-in-se").html("");

                if (response.length > 0) {
                    response.forEach((data) => {
                        // data.title에서 검색어에 해당하는 부분을 <em> 태그로 감싼 결과 생성
                        const highlightedTitle = highlightText(data.title, keyword);
                        $(".modal-re-search-box-bo-box-in-se").append(`
                        <a href="/campaign/detail/${data.campaignId}" class="modal-re-search-box-bo-box-in-se-a">
                            <div>
                                <svg viewBox="0 0 48 48">
                                    <path fill-rule="evenodd" clip-rule="evenodd"
                                        d="M22.0886 38.8C12.7939 38.8 5.29813 31.3 5.29813 22.1C5.29813 12.8 12.7939 5.4 22.0886 5.4C31.3833 5.4 38.879 12.9 38.879 22.1C38.879 31.3 31.2834 38.8 22.0886 38.8ZM45.4753 43.1L37.28 35C40.3782 31.401 42.0772 26.8 42.0772 22C42.0772 10.9 33.1823 2 22.0886 2C10.9949 2 2 11 2 22C2 33 10.9949 42 22.0886 42C26.8859 42 31.4832 40.3 35.0812 37.3L43.2765 45.5C43.5764 45.8 43.9762 46 44.3759 46C44.7757 46 45.1755 45.8 45.4753 45.5C46.1749 44.901 46.1749 43.8 45.4753 43.1Z">
                                    </path>
                                </svg>
                            </div>
                            <span>${highlightedTitle}</span>
                        </a>
                    `);
                    });
                } else {
                    $(".modal-re-search-box-bo-box-in-se").append(`<div>검색 결과가 없습니다.</div>`);
                }
            },
            error: function (xhr, status, error) {
                console.error("검색 오류:", error);
            }
        });
    });




    $(document).on("keydown", ".modal-search-box-in-top-input-in", function (event) {
        if ($(".modal-search").css("display") === "flex" && event.key === "Enter") {
            event.preventDefault(); // 기본 엔터 이벤트 방지
            search(); // 검색 실행
        }
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

        $(document).on("click", ".header-box-top-pe-sign-in", function () {
            $(".modal-alert").css("display", "flex");
        });

        $(document).on("click", "#modal-controller-alert", function () {
            $(".modal-alert").css("display", "none");
        });

        $(document).on("focus", ".modal-search-box-in-top-input-in", function () {
            $(".modal-search").css("display", "flex");
        });

        $(document).on("click", "#modal-controller-search", function () {
            $(".modal-search").css("display", "none");
        });

    }
});
