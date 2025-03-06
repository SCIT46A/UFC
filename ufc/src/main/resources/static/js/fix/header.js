$(function () {
    function search() {
        const searchText = $(".modal-search-box-in-top-input-in").val().trim();

        if (!searchText) return; // 빈 값 방지

        saveSearchQuery(searchText); // ✅ 검색 기록 저장 (선택 사항)

        // ✅ 불필요한 AJAX 요청 제거 → 바로 페이지 이동
        window.location.href = `/search/search/${encodeURIComponent(searchText)}`;
    }


    // 최초 알림창 확인
    $.ajax({
        url: "/api/checkAlert",
        method: "GET",
        success: function (response) {
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
        url: "/api/checkLogin",
        method: "GET",
        success: function (response) {
            // 일반유저
            if (response.roles === "ROLE_USER") {
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
            else if (response.roles === "ROLE_CREATOR") {
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
            else if (response.roles === "ROLE_ADMIN") {
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
                                관리자 ${response.userName} 님
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
        url: "/api/checkTag",
        method: "GET",
        success: function (response) {
            response.forEach((data)=> {
                $(".modal-re-search-box-top").append(
                    `
                <a
                    href="/search/tag/${data.content}"
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

    // 🔹 검색어를 쿠키에 저장하는 함수 (모든 페이지에서 검색 기록 유지)
    function saveSearchQuery(query) {
        let searches = getSearchHistory(); // 기존 검색어 가져오기

        // 중복 검색어 방지
        searches = searches.filter(q => q !== query);
        searches.push(query);

        // 최대 10개까지만 유지
        if (searches.length > 10) {
            searches.shift(); // 가장 오래된 검색어 삭제
        }

        // ✅ 검색어를 쿠키에 저장 (인코딩 처리 + 전체 페이지에서 유지)
        const encodedSearches = encodeURIComponent(JSON.stringify(searches));
        document.cookie = `searchHistory=${encodedSearches}; path=/; max-age=2592000`;

    }




    // 쿠키에서 검색 기록 가져오는 함수
    function getSearchHistory() {
        const cookie = document.cookie
            .split("; ")
            .find(row => row.startsWith("searchHistory="));

        if (!cookie) {

            return [];
        }

        try {
            const decodedHistory = JSON.parse(decodeURIComponent(cookie.split("=")[1]));

            return decodedHistory;
        } catch (e) {
            console.error("🚨 쿠키 파싱 오류:", e);
            return [];
        }
    }



    // 최근 검색어 표시하는 함수
    function displaySearchHistory() {
        const history = getSearchHistory();


        $(".modal-re-search-box-recent-bo").html(""); // 기존 내용 초기화

        if (history.length > 0) {
            history.forEach(query => {
                $(".modal-re-search-box-recent-bo").append(`
                <a class="modal-re-search-box-recent-bo-pe" href="/search/search/${query}">
                    <div class="modal-re-search-box-recent-bo-pe-text">${query}</div>
                    <div class="modal-re-search-box-recent-bo-pe-cancle">
                        <svg width="10" height="10" viewBox="0 0 10 10" fill="none" xmlns="http://www.w3.org/2000/svg">
                            <path fill-rule="evenodd" clip-rule="evenodd" d="M4.28544 5.00257L2.01916 2.73642C1.82521 2.54248 1.82974 2.23083 2.01598 2.02765C2.21448 1.81131 2.5294 1.8394 2.72795 2.02108L2.72969 2.02268L4.99738 4.2905L7.26357 2.02431C7.4575 1.83056 7.7691 1.83508 7.97226 2.02115C8.1886 2.21946 8.16077 2.53473 7.97878 2.73311L7.97723 2.73479L7.97564 5.00257L7.97724 7.26876C8.16953 7.46283 8.16504 7.77425 7.97884 7.97756L7.97724 7.9793L7.97557 7.98097C7.78164 8.17472 7.47008 8.17023 7.26691 7.98417L7.26519 7.98259L4.99738 5.71465L2.73129 7.981C2.53725 8.17469 2.22572 8.17025 2.02253 7.98417L2.01908 7.98101L2.01592 7.97756C1.82971 7.77425 1.82526 7.46279 2.01916 7.26872L4.28544 5.00257Z" fill="#6D6D6D"></path>
                        </svg>
                    </div>
                </a>
            `);
            });
        } else {
            $(".modal-re-search-box-recent-bo").append(`<div>최근 검색 기록이 없습니다.</div>`);
        }
    }


    // 🔹 모든 페이지에서 검색 기록을 자동으로 로드하여 표시
    $(document).ready(function () {
        displaySearchHistory(); // ✅ 모든 페이지에서 검색 기록 표시
    });



    // 특수문자를 이스케이프하는 함수 (정규식용)
    function escapeRegExp(string) {
        if (!string) return "";  // 🔹 string이 없으면 빈 문자열 반환
        return string.replace(/[.*+?^${}()|[\]\\]/g, '\\$&');
    }


    /**
     * text에서 query와 일치하는 부분을 <em> 태그로 감싸서 리턴합니다.
     * (대소문자 구분 없이 검색)
     */
    function highlightText(text, query) {
        if (!text) return "";
        if (!query || query.trim() === "") return text;

        try {
            // 🔹 검색어에서 공백을 제거하고, 공백을 무시하는 정규식 패턴 생성
            const escapedQuery = escapeRegExp(query).replace(/\s+/g, "\\s*");

            // 🔹 띄어쓰기 유무에 관계없이 검색 가능하도록 정규식 수정
            const regex = new RegExp(`(${escapedQuery})`, "gi");

            return text.replace(regex, "<em>$1</em>");
        } catch (error) {
            console.error("🚨 highlightText 오류:", error);
            return text;
        }
    }




    $(document).on("input", ".modal-search-box-in-top-input-in", function () {
        const keyword = $(this).val().trim();

        if (keyword === "") {
            $(".modal-re-search-box-bo-box-in-se").html(""); // 검색창 비우기
            return;
        }



        $.ajax({
            url: "/api/searchBox",
            method: "GET",
            data: { keyword: keyword },
            success: function (response) {
                $(".modal-re-search-box-bo-box-in-se").html("");
                console.log(response)
                if (response.length > 0) {
                    response.forEach((data) => {
                        // ✅ 검색어에 해당하는 부분을 <em> 태그로 감싼 결과 생성
                        const highlightedName = highlightText(data.name, keyword);
                        if(data.type !== "Tag"){
                            $(".modal-re-search-box-bo-box-in-se").append(`
                    <a href="/${data.type}/${data.id}" class="modal-re-search-box-bo-box-in-se-a">
                        <div>
                            <svg viewBox="0 0 48 48">
                                <path fill-rule="evenodd" clip-rule="evenodd"
                                    d="M22.0886 38.8C12.7939 38.8 5.29813 31.3 5.29813 22.1C5.29813 12.8 12.7939 5.4 22.0886 5.4C31.3833 5.4 38.879 12.9 38.879 22.1C38.879 31.3 31.2834 38.8 22.0886 38.8ZM45.4753 43.1L37.28 35C40.3782 31.401 42.0772 26.8 42.0772 22C42.0772 10.9 33.1823 2 22.0886 2C10.9949 2 2 11 2 22C2 33 10.9949 42 22.0886 42C26.8859 42 31.4832 40.3 35.0812 37.3L43.2765 45.5C43.5764 45.8 43.9762 46 44.3759 46C44.7757 46 45.1755 45.8 45.4753 45.5C46.1749 44.901 46.1749 43.8 45.4753 43.1Z">
                                </path>
                            </svg>
                        </div>
                        <span>${highlightedName}</span>  <!-- ✅ 하이라이팅 적용 -->
                    </a>
                `);} else{
                            $(".modal-re-search-box-bo-box-in-se").append(`
                    <a href="/search/tag/${data.name}" class="modal-re-search-box-bo-box-in-se-a">
                        <div>
                            <svg viewBox="0 0 48 48">
                                <path fill-rule="evenodd" clip-rule="evenodd"
                                    d="M22.0886 38.8C12.7939 38.8 5.29813 31.3 5.29813 22.1C5.29813 12.8 12.7939 5.4 22.0886 5.4C31.3833 5.4 38.879 12.9 38.879 22.1C38.879 31.3 31.2834 38.8 22.0886 38.8ZM45.4753 43.1L37.28 35C40.3782 31.401 42.0772 26.8 42.0772 22C42.0772 10.9 33.1823 2 22.0886 2C10.9949 2 2 11 2 22C2 33 10.9949 42 22.0886 42C26.8859 42 31.4832 40.3 35.0812 37.3L43.2765 45.5C43.5764 45.8 43.9762 46 44.3759 46C44.7757 46 45.1755 45.8 45.4753 45.5C46.1749 44.901 46.1749 43.8 45.4753 43.1Z">
                                </path>
                            </svg>
                        </div>
                        <span># ${highlightedName}</span>  <!-- ✅ 하이라이팅 적용 -->
                    </a>
                `);}
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

    // 임시 추가
    fetch("/creator/create", {
        method: "POST",
        headers: {
            "Content-Type": "application/json"
        },
        body: JSON.stringify({
            username: "woo lee",
            role: "creator"
        })
    })
    

    $(document).on("keydown", ".modal-search-box-in-top-input-in", function (event) {
        if ($(".modal-search").css("display") === "flex" && event.key === "Enter") {
            event.preventDefault(); // 기본 엔터 이벤트 방지
            const keyword = $(this).val().trim();
            saveSearchQuery(keyword); // ✅ 검색 기록 저장
            search(); // 검색 실행
        }
    });

    // 🔹 검색 기록을 초기화하는 기능 (추가 가능)
    $(document).on("click", ".modal-re-search-box-recent-bo-pe-cancle", function (event) {
        event.stopPropagation(); // 부모 `a` 태그의 클릭 이벤트 방지
        event.preventDefault();  // `a` 태그의 기본 이동 방지

        let searches = getSearchHistory();
        const queryToRemove = $(this).siblings(".modal-re-search-box-recent-bo-pe-text").text();

        // 검색 기록에서 해당 검색어 제거
        searches = searches.filter(q => q !== queryToRemove);
        document.cookie = `searchHistory=${encodeURIComponent(JSON.stringify(searches))}; path=/; max-age=2592000`;

        // 삭제한 검색어 UI에서 제거
        $(this).closest(".modal-re-search-box-recent-bo-pe").remove();


    });


    $(document).on("click", ".modal-re-search-box-recent-top span", function () {
        // ✅ 검색 기록 쿠키 완전히 삭제 (모든 경로에서 삭제되도록)
        document.cookie = "searchHistory=; path=/; max-age=0; expires=Thu, 01 Jan 1970 00:00:00 UTC";

        // ✅ 검색 기록 UI에서 제거
        $(".modal-re-search-box-recent-bo").html(`<div>최근 검색 기록이 없습니다.</div>`);


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
            displaySearchHistory();
            $(".modal-search").css("display", "flex");
        });

        $(document).on("click", "#modal-controller-search", function () {
            $(".modal-search").css("display", "none");
        });

    }
});
