$(function () {
    // 자주 사용하는 DOM 선택자 캐싱
    const $mainBoInBo = $(".main-bo-in-bo");
    const $mainBoInTop = $(".main-bo-in-top");
    const $modalTagSearch = $(".modal-tag-search");
    const $searchBox = $(".main-search-box");
    const $modalReSearchBoxTopAdd = $(".modal-re-search-box-top-add");
    const $mainSearchTag = $(".main-search-tag");




    const pathParts = window.location.pathname.split("/");
    const query = decodeURIComponent(pathParts[pathParts.length - 1]);

    if (!query) return; // 검색어가 없으면 종료

    const pathPartsAdd = window.location.pathname.split("/");
    const type = pathPartsAdd[pathPartsAdd.length - 2]; // URL에서 "type" 가져오기
    const queryAdd = decodeURIComponent(pathPartsAdd[pathPartsAdd.length - 1]); // URL에서 "query" 가져오기


    // ✅ type이 "tag"이면 기본적으로 query를 태그로 추가
    if (type === "tag" && queryAdd) {
        addTag(queryAdd);
    }

    // 디바운스 함수 (직접 구현)
    function debounce(func, delay) {
        let timeout;
        return function (...args) {
            clearTimeout(timeout);
            timeout = setTimeout(() => func.apply(this, args), delay);
        };
    }

    /** ✅ 한글 값을 컨트롤러용 값으로 변환 */
    function getSortType() {
        const selectedSort = document.querySelector(".main-top-in-content-target-box-span-btn-spab-add");
        if (!selectedSort) return "null"; // 기본값 "null"

        const sortMap = {
            "최신순": "latest",
            "마감 임박순": "deadline",
            "좋아요 많은순": "like",
            "전체보기" : "null"
        };

        return sortMap[selectedSort.textContent.trim()] || "like";
    }

    /** ✅ 선택된 기부 필터 가져오기 */
    function getDonationFilter() {
        const selectedDonation = document.querySelector(".main-top-in-content-target-box-span-btn-spab");
        if (!selectedDonation) return "전체보기"; // 기본값

        const donationMap = {
            "전체보기": "all",
            "50% 이하": "below50",
            "51% ~ 100%": "between51to100",
            "100%이상": "above100"
        };

        return donationMap[selectedDonation.textContent.trim()] || "all";
    }


    /** ✅ 선택된 태그 목록 가져오기 */
    function getSelectedTags() {
        return Array.from(document.querySelectorAll(".main-search-tag-pe"))
            .map(tag => tag.textContent.trim());
    }

    /** ✅ 검색 요청 실행 */
    function fetchSearchResults() {
        const sortType = getSortType();
        const donationFilter = getDonationFilter();
        const tagFilters = getSelectedTags();
        const effectiveQuery = (type === "tag" && tagFilters.length === 0) ? "all" : query;

        $.ajax({
            url: `/api/search/${type}/${encodeURIComponent(effectiveQuery)}`,
            method: "GET",
            data: {
                sort: sortType,
                donation: donationFilter,
                tags: tagFilters
            },
            success: (response) => {
                let htmlResult = "";
                $mainBoInTop.html(`<span>${response.length}</span>개의 검색결과가 있습니다.`);
                console.log(response)
                if (response.length > 0) {
                    response.forEach((data) => {
                        if (data.type === "product") {
                            htmlResult += `
                                <div class="main-bo-in-bo-pe" data-id="${data.originalId}" data-type="${data.type}">
                                    <div class="main-bo-in-bo-pe-box">
                                        <a href="/product/${data.originalId}" class="main-bo-in-bo-pe-box-a">
                                            <div class="main-bo-in-bo-pe-box-a-img">
                                                <img alt="" src="/api/image/${data.imageId}" class="main-bo-in-bo-pe-box-a-img-size" />
                                                <div class="main-like-btn ${data.isLiked ? 'liked' : ''}">
                                                    <svg width="40" height="40" viewBox="0 0 40 40" fill="none" xmlns="http://www.w3.org/2000/svg">
                                                        <path
                                                            d="M19.463 15.3087L19.9993 15.7933L20.5356 15.3087L22.2643 13.747C22.2643 13.747 22.2643 13.7469 22.2643 13.7469C23.615 12.5269 25.9852 12.7532 27.3097 14.2118L27.3156 14.2182L27.3216 14.2246C28.9912 15.9843 29.0145 19.0158 27.2167 20.9218L19.9995 27.9864L12.7818 20.9218C10.9839 19.0158 11.0075 15.984 12.6769 14.2246L12.6829 14.2182L12.6888 14.2118C14.0133 12.7532 16.3836 12.5269 17.7343 13.7469C17.7343 13.7469 17.7343 13.7469 17.7344 13.747L19.463 15.3087Z"
                                                            fill="black" fill-opacity="0.25" stroke="white" stroke-width="1.6"
                                                        ></path>
                                                    </svg>
                                                </div>
                                            </div>
                                            <div class="main-bo-in-bo-pe-box-a-title">
                                                <div class="main-bo-in-bo-pe-box-a-title-top">
                                                    <div class="main-bo-in-bo-pe-box-a-title-top-se">
                                                        <div>${data.sellerName}</div>
                                                    </div>
                                                    <div class="main-bo-in-bo-pe-box-a-title-mi">
                                                        <div class="main-bo-in-bo-pe-box-a-title-mi-title">${data.title}</div>
                                                        <div class="main-bo-in-bo-pe-box-a-title-mi-content">${data.description}</div>
                                                    </div>
                                                </div>
                                                <div class="main-funding">
                                                    <div class="main-funding-top-add">
                                                        <div>
                                                            <span class="main-funding-top-per-add">${data.price}원</span>
                                                        </div>
                                                    </div>
                                                </div>
                                            </div>
                                        </a>
                                    </div>
                                </div>
                            `;
                        } else {
                            htmlResult += `
                                <div class="main-bo-in-bo-pe" data-id="${data.originalId}" data-type="${data.type}">
                                    <div class="main-bo-in-bo-pe-box">
                                        <a href="/campaign/${data.originalId}" class="main-bo-in-bo-pe-box-a">
                                            <div class="main-bo-in-bo-pe-box-a-img">
                                                <img alt="" src="/api/image/${data.imageId}" class="main-bo-in-bo-pe-box-a-img-size" />
                                                <div class="main-like-btn ${data.isLiked ? 'liked' : ''}">
                                                    <svg width="40" height="40" viewBox="0 0 40 40" fill="none" xmlns="http://www.w3.org/2000/svg">
                                                        <path
                                                            d="M19.463 15.3087L19.9993 15.7933L20.5356 15.3087L22.2643 13.747C22.2643 13.747 22.2643 13.7469 22.2643 13.7469C23.615 12.5269 25.9852 12.7532 27.3097 14.2118L27.3156 14.2182L27.3216 14.2246C28.9912 15.9843 29.0145 19.0158 27.2167 20.9218L19.9995 27.9864L12.7818 20.9218C10.9839 19.0158 11.0075 15.984 12.6769 14.2246L12.6829 14.2182L12.6888 14.2118C14.0133 12.7532 16.3836 12.5269 17.7343 13.7469C17.7343 13.7469 17.7343 13.7469 17.7344 13.747L19.463 15.3087Z"
                                                            fill="black" fill-opacity="0.25" stroke="white" stroke-width="1.6"
                                                        ></path>
                                                    </svg>
                                                </div>
                                            </div>
                                            <div class="main-bo-in-bo-pe-box-a-title">
                                                <div class="main-bo-in-bo-pe-box-a-title-top">
                                                    <div class="main-bo-in-bo-pe-box-a-title-top-se">
                                                        <div>${data.sellerName}</div>
                                                    </div>
                                                    <div class="main-bo-in-bo-pe-box-a-title-mi">
                                                        <div class="main-bo-in-bo-pe-box-a-title-mi-title">${data.title}</div>
                                                        <div class="main-bo-in-bo-pe-box-a-title-mi-content">${data.description}</div>
                                                    </div>
                                                </div>
                                                <div class="main-funding">
                                                    <div class="main-funding-top">
                                                        <div>
                                                            <span class="main-funding-top-per">${data.donationPercentage}%</span>
                                                            <span class="main-funding-top-pri">${data.donatedQuantity}개 모임</span>
                                                        </div>
                                                        <em>${data.remainingDays}일 남음</em>
                                                    </div>
                                                    <div class="main-funding-bo" data-percentage="${data.donationPercentage}">
                                                        <div class="progress-bar"></div>
                                                    </div>
                                                </div>
                                            </div>
                                        </a>
                                    </div>
                                </div>
                            `;
                        }
                    });
                } else {
                    htmlResult = "<div>검색 결과가 없습니다!</div>";
                }
                $mainBoInBo.html(htmlResult);

                // 진행바 업데이트 (필요한 경우)
                $(".main-funding-bo").each(function () {
                    const percentage = $(this).data("percentage");
                    $(this).find(".progress-bar").css("width", percentage + "%");
                });
                $mainBoInBo.find("img.main-bo-in-bo-pe-box-a-img-size").each(function() {
                    const $img = $(this);
                    const endpoint = $img.attr("src"); // /api/image/{imageId} 엔드포인트
                    $.ajax({
                        url: endpoint,
                        method: "GET",
                        success: function(resultUrl) {
                            if(resultUrl) {
                                $img.attr("src", resultUrl);
                            }
                        },
                        error: function(err) {
                            console.error("이미지 URL 요청 오류:", err);
                        }
                    });
                });
            },
            error: (xhr, status, error) => {
                console.error("검색 오류:", error);
            },
        });
        $(document).ready(function() {
            // 좋아요 버튼 클릭 이벤트 - 문서 로드 시 단 한 번 바인딩
            $(document).on("click", ".main-like-btn", function (event) {
                event.preventDefault();  // a태그 등 기본 동작 막기
                event.stopPropagation(); // 부모로의 이벤트 전파 차단

                var $likeBtn = $(this);
                // AJAX 진행 중이면 중복 클릭 방지
                if ($likeBtn.data("ajaxInProgress")) {
                    return;
                }
                $likeBtn.data("ajaxInProgress", true);

                // 현재 좋아요 상태 (클릭 전 상태)
                var currentState = $likeBtn.hasClass("liked");
                // 부모 요소에 data-id와 data-type이 반드시 있어야 합니다.
                var $parentItem = $likeBtn.closest(".main-bo-in-bo-pe");
                var itemId = $parentItem.data("id");
                var itemType = $parentItem.data("type");



                $.ajax({
                    url: "/api/like/toggle",  // 서버의 좋아요 토글 엔드포인트
                    method: "POST",
                    data: {
                        itemId: itemId,
                        type: itemType,
                        currentState: currentState
                    },
                    success: function (response) {
                        if (response.success) {
                            // 서버가 반환한 새 상태에 따라 클래스 토글
                            if (response.isLiked) {
                                $likeBtn.addClass("liked");
                            } else {
                                $likeBtn.removeClass("liked");
                            }
                        } else {
                            console.error("좋아요 토글 실패:", response.message);
                        }
                    },
                    error: function (err) {
                        console.error("좋아요 토글 AJAX 오류:", err);
                    },
                    complete: function () {
                        // AJAX 요청 완료 후 플래그 제거
                        $likeBtn.removeData("ajaxInProgress");
                    }
                });
            });
        });

    }

    // 검색창 입력 이벤트 (디바운스 적용)
    $searchBox.on("input", debounce(function () {
        const keyword = $(this).val().trim();

        if (keyword === "") {
            $modalReSearchBoxTopAdd.html("<div>검색어를 입력해주세요!</div>");
            return;
        }

        $.ajax({
            url: "/api/searchTagBox",
            method: "GET",
            data: { keyword: keyword },
            success: function (response) {
                $modalReSearchBoxTopAdd.html("");
                if (response.length > 0) {
                    response.forEach((data) => {
                        $modalReSearchBoxTopAdd.append(`
                            <div class="modal-re-search-box-top-tag-add">
                                <div class="modal-re-search-box-top-tag-le">#</div>
                                <div class="modal-re-search-box-top-tag-ri">${data.content}</div>
                            </div>
                        `);
                    });
                } else {
                    $modalReSearchBoxTopAdd.append(`<div>검색 결과가 없습니다.</div>`);
                }
            },
        });
    }, 300)); // 300ms 지연

    // 태그 클릭 시 main-search-tag-pe 요소 추가 및 검색 갱신
    $(document).on("click", ".modal-re-search-box-top-tag-add", function () {
        const value = $(this).find(".modal-re-search-box-top-tag-ri").text().trim();
        // 중복 검사
        if ($(".main-search-tag-pe").filter(function () {
            return $(this).text().trim() === value;
        }).length > 0) {
            return;
        }
        addTag(value);
        fetchSearchResults();
    });

    // 엔터 키 입력 시 태그 추가 및 검색 갱신
    $searchBox.on("keypress", function (event) {
        if (event.which === 13) { // 엔터키
            event.preventDefault();
            const keyword = $(this).val().trim();
            if (!keyword) return;
            let matchedTag = null;
            $(".modal-re-search-box-top-tag-ri").each(function () {
                if ($(this).text().trim() === keyword) {
                    matchedTag = $(this).text().trim();
                    return false; // break
                }
            });
            if (matchedTag) {
                if ($(".main-search-tag-pe").filter(function () {
                    return $(this).text().trim() === matchedTag;
                }).length === 0) {
                    addTag(matchedTag);
                    $(this).val("");
                    fetchSearchResults();
                }
            }
        }
    });

    function addTag(value) {
        const tagDiv = $(`
            <div class="main-search-tag-pe">
                ${value}
                <button class="main-search-tag-pe-btn">
                    <svg xmlns="http://www.w3.org/2000/svg" class="h-4 w-4" viewBox="0 0 20 20" fill="currentColor">
                        <path fill-rule="evenodd" d="M4.293 4.293a1 1 0 011.414 0L10 8.586l4.293-4.293a1 1 0 111.414 1.414L11.414 10l4.293 4.293a1 1 0 01-1.414 1.414L10 11.414l-4.293 4.293a1 1 0 01-1.414-1.414L8.586 10 4.293 5.707a1 1 0 010-1.414z" clip-rule="evenodd"></path>
                    </svg>
                </button>
            </div>
        `);
        $mainSearchTag.append(tagDiv);
    }

    // 초기 이벤트 바인딩은 페이지 로드 시 단 한 번만 실행
    initializeEventListeners();

    // 최초 검색 결과 호출
    fetchSearchResults();

    function initializeEventListeners() {
        // 중복 바인딩 방지 (한 번만 실행)
        if (window.modalEventsInitialized) return;
        window.modalEventsInitialized = true;

        // Lucide 아이콘 생성 (로드되어 있다면)
        if (window.lucide) {
            lucide.createIcons();
        } else {
            console.error("Lucide not loaded");
        }

        // 현재 연도 업데이트
        const yearElement = document.getElementById("current-year");
        if (yearElement) {
            yearElement.textContent = new Date().getFullYear();
        }

        // 모달 열기/닫기 이벤트
        const perModal = document.querySelector(".main-modal-per");
        const perBtn = document.querySelector(".main-top-in-content-target-box-span-btn");
        const ModalClose = document.querySelector(".main-modal-per-close");
        const reBModal = document.querySelector(".main-modal-per-add");
        const reBtn = document.querySelector(".main-top-in-content-target-box-span-btn-re");

        perBtn && perBtn.addEventListener("click", () => {
            perModal.style.display = "flex";
            ModalClose.style.display = "block";
        });

        ModalClose && ModalClose.addEventListener("click", () => {
            perModal.style.display = "none";
            reBModal.style.display = "none";
            ModalClose.style.display = "none";
        });

        reBtn && reBtn.addEventListener("click", () => {
            reBModal.style.display = "flex";
            ModalClose.style.display = "block";
        });

        // **이벤트 위임**: 태그 X 버튼 클릭 시 해당 태그 삭제 후 검색 갱신 (한 번만 바인딩)
        $(document).on("click", ".main-search-tag-pe-btn", function () {
            $(this).closest(".main-search-tag-pe").remove();
            fetchSearchResults();
        });

        // 정렬 및 필터 선택 (기부 퍼센트 필터)
        const choices = document.querySelectorAll(".main-modal-per-in-con-box-choice-in");
        const targetSpan = document.querySelector(".main-top-in-content-target-box-span-btn-spab");
        choices.forEach(choice => {
            choice.addEventListener("click", function () {
                choices.forEach(item => {
                    item.classList.remove("on");
                    item.classList.add("off");
                });
                this.classList.remove("off");
                this.classList.add("on");
                if (targetSpan) targetSpan.textContent = this.textContent.trim();
                $(".main-modal-per").css("display", "none");
                $(".main-modal-per-close").css("display", "none");
                fetchSearchResults();
            });
        });

        // 정렬 및 필터 선택 (태그 필터 정렬)
        const choicesAdd = document.querySelectorAll(".main-modal-per-in-con-box-choice-in-add");
        const targetSpanAdd = document.querySelector(".main-top-in-content-target-box-span-btn-spab-add");
        choicesAdd.forEach(choice => {
            choice.addEventListener("click", function () {
                choicesAdd.forEach(item => {
                    item.classList.remove("on");
                    item.classList.add("off");
                });
                this.classList.remove("off");
                this.classList.add("on");
                if (targetSpanAdd) targetSpanAdd.textContent = this.textContent.trim();
                $(".main-modal-per-add").css("display", "none");
                $(".main-modal-per-close").css("display", "none");
                fetchSearchResults();
            });
        });

        // 검색창 focus 이벤트 처리
        $searchBox.on("focus", function () {
            $modalTagSearch.css("display", "flex");
        });
        $(document).on("click", "#modal-controller-tag-search", function () {
            $modalTagSearch.css("display", "none");
        });
    }
});
