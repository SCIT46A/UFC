$(document).ready(function () {
    const campaignId = $("#campaign-id").val();
    const campaignStatus = $("#campaign-status").val();

    $(".main-review").addClass("hidden")

    let fixedMainBoardId = null; // 고정(0번째) 게시글 ID
    $(".main-mi-na-in-box-pe.campaign").on("click",()=>{
        $(".main-reply").removeClass("hidden")
        $(".main-review").addClass("hidden")
        replay(fixedMainBoardId)
    })

    $(".main-mi-na-in-box-pe.board").on("click",()=> {
        $(".main-reply").addClass("hidden")
        $(".main-board-top-table").removeClass("hidden");
        $(".main-board-back-div").addClass("hidden");
        $(".main-board-total-pe").removeClass("hidden");
        $(".content-board-box").addClass("hidden");
        $(".main-review").addClass("hidden")
    })
    $(".main-mi-na-in-box-pe.review").on("click",()=> {
        $(".main-reply").addClass("hidden")
        $(".main-board-top-table").removeClass("hidden");
        $(".main-board-back-div").addClass("hidden");
        $(".main-board-total-pe").removeClass("hidden");
        $(".content-board-box").addClass("hidden");
        $(".main-review").removeClass("hidden")
    })
    /* ============ 공통 Summernote 초기화 함수 ============ */
    function initSummernote(selector) {
        $(selector).summernote({
            height: 600,
            minHeight: 600,
            maxHeight: null,
            lang: "ko-KR",
            callbacks: {
                onImageUpload: function (files) {
                    const file = files[0];
                    const formData = new FormData();
                    formData.append("file", file, file.name);
                    fetch("/api/image/upload", {
                        method: "POST",
                        credentials: "include",
                        body: formData
                    })
                        .then(response => {
                            if (!response.ok) throw new Error("이미지 업로드 실패");
                            return response.text();
                        })
                        .then(imageId => {
                            return fetch(`/api/image/${imageId}`, { credentials: "include" });
                        })
                        .then(response => {
                            if (!response.ok) throw new Error("이미지 URL 요청 실패");
                            return response.text();
                        })
                        .then(imageUrl => {
                            $(selector).summernote("insertImage", imageUrl);
                        })
                        .catch(error => {
                            console.error("이미지 URL 불러오기 에러:", error);
                        });
                },
                onKeyup: function () {
                    const $editable = $(this).next('.note-editor').find('.note-editable');
                    $editable.css('height', 'auto');
                }
            }
        });
    }

    /* ============ 신규 작성 시 기본 에디터 초기화 ============ */
    initSummernote(".presentation-size");

    /* ============ 게시글 로드 및 목록 표시 ============ */
    function reLoadBoard() {
        $.ajax({
            url: `/api/campaign/board/${campaignId}`,
            method: "GET",
            success: function (response) {
                if (response && response.length > 0) {
                    // 최초 로드시 0번째 게시글(고정 게시글) 설정
                    if (!fixedMainBoardId) {
                        fixedMainBoardId = response[0].cboardId;
                    }
                    // 고정 게시글은 항상 메인에 표시 (내용 업데이트)
                    const mainBoard = response.find(board => board.cboardId == fixedMainBoardId);
                    if (mainBoard) {
                        $(".content-box-in").html(mainBoard.content);
                        if ($("#board-id").length) {
                            $("#board-id").val(mainBoard.cboardId);
                        } else {
                            $("#campaign-id").after(`<input type="hidden" id="board-id" value="${mainBoard.cboardId}">`);
                        }
                    }
                    replay(mainBoard.cboardId);
                }
                // 목록에는 고정 게시글 제외한 추가 게시글만 표시
                if (response) {
                    const otherBoards = response.filter(board => board.cboardId != fixedMainBoardId);
                    $(".board-count-target").html(otherBoards.length);
                    $(".main-board-total-pe").empty();
                    if (otherBoards.length === 0) {
                        $(".main-board-total-pe").append(`<div class="no-posts">작성된 게시글이 없습니다.</div>`);
                        $(".summer-btn").addClass("hidden");
                        $(".summer-btn-edit-save").addClass("hidden");
                        $(".main-reply").addClass("hidden")
                    } else {
                        otherBoards.forEach((data) => {
                            $(".summer-btn-edit").addClass("hidden");
                            const htmldata = `
                            <div class="main-board-pe" data-board-id="${data.cboardId}">
                                <div class="main-board-pe-in">
                                    <p class="main-board-pe-in-title-add">${data.title}</p>
                                    <p class="main-board-pe-in-date-add">${data.createdDate}</p>
                                </div>
                            </div>
                            `;
                            $(".main-board-total-pe").append(htmldata);
                        });
                    }
                    reBoard(); // 목록 클릭 이벤트 재바인딩
                } if (response.length ==0){
                    $(".summer-btn-edit").addClass("hidden");
                    $(".summer-btn").removeClass("hidden");
                    $(".summer-btn-edit-save").addClass("hidden");
                    $(".main-reply").addClass("hidden")
                }
            },
            error: function (err) {
                console.error(err);
            }
        });
    }

    $(document).on("click", ".summer-btn", function () {
        const content = $(".presentation-size").summernote("code");
        const title = "Title"; // 제목 처리 (필요하면 값 가져오기)
        const url = `/api/${campaignId}/board`; // 신규 작성 엔드포인트
        fetch(url, {
            method: "POST",
            headers: { "Content-Type": "application/json" },
            body: JSON.stringify({ content: content, title: title }),
            credentials: "include"
        })
            .then(response => {
                if (!response.ok) {
                    throw new Error("저장 실패");
                }
                return response.json();
            })
            .then(result => {
                console.log("저장 성공:", result);
                $(".summer-btn-edit").removeClass("hidden");
                reLoadBoard();
            })
            .catch(error => {
                console.error("저장 에러:", error);
            });
    });

    /* ============ 신규 작성 - 등록하기 ============ */
    // 신규 작성 화면은 별도의 "작성하기" 버튼(예: .main-board-add-btn)을 통해 호출되어야 함.
    // 여기서는 작성 완료 후 "등록하기" 버튼(.main-board-add-btn-save)을 사용합니다.
    $(document).on("click", ".main-board-add-btn-save", function () {
        const content = $(".presentation-size").summernote("code");
        const title = $(".main-board-add-btn-input").val() || "Title";
        if (!title || title.trim() === "") {
            alert("제목을 입력해주세요.");
            return;
        }
        const url = `/api/${campaignId}/board`;
        fetch(url, {
            method: "POST",
            headers: { "Content-Type": "application/json" },
            body: JSON.stringify({ content: content, title: title }),
            credentials: "include"
        })
            .then(response => {
                if (!response.ok) throw new Error("저장 실패");
                return response.json();
            })
            .then(result => {
                console.log("등록 성공:", result);
                reLoadBoard();
                resetBoardUI();
            })
            .catch(error => {
                console.error("등록 에러:", error);
            });
    });

    /* ============ 메인 및 상세 게시글 수정 모드 진입 (수정하기 버튼) ============ */
    $(document).on("click", ".main-board-add-btn-edit", function () {
        let boardId = $("#board-id").val();
        if (!boardId) {
            console.error("게시글 ID가 없습니다.");
            return;
        }
        $.ajax({
            url: `/api/board/${boardId}`,
            method: "GET",
            success: function (response) {
                if (response) {
                    // 수정할 컨테이너 결정: 고정 게시글이면 .content-box-in, 나머지이면 .content-board-box
                    let isFixed = (boardId == fixedMainBoardId);
                    if (isFixed) {
                        // 메인(고정) 게시글 수정
                        $(".content-box-in").html('<div id="edit-container"><input type="text" id="detail-title-input" value="' + response.title + '" style="width:100%; margin-bottom:10px;" /><textarea id="detail-summernote-editor"></textarea></div>');
                    } else {
                        // 상세 게시글 수정
                        $(".content-box-in-target").html('<div id="edit-container"><input type="text" id="detail-title-input" value="' + response.title + '" style="width:100%; margin-bottom:10px;" /><textarea id="detail-summernote-editor"></textarea></div>');
                    }
                    initSummernote('#detail-summernote-editor');
                    $('#detail-summernote-editor').summernote('code', response.content);
                    // 버튼 전환: 수정하기 버튼 숨기고 수정 적용하기 버튼 노출
                    $(".main-board-add-btn-edit").addClass("hidden");
                    $(".main-board-add-btn-edit-save").removeClass("hidden");
                }
            },
            error: function () {
                console.error("게시글 불러오기 실패");
            }
        });
    });

    // 관리자검토전 수정
    $(document).on("click", ".summer-btn-edit", function () {
        $.ajax({
            url: `/api/campaign/board/${campaignId}`,
            method: "GET",
            success: function (response) {
                if (response && response.length > 0) {
                    // 편집 모드용 에디터 영역으로 변경
                    $(".content-box-in").html(`
                        <p style="color: gray">
                          ※ 게시글을 작성하셔야 관리자에게 캠페인을 승인받을 수 있습니다.
                        </p>
                        <div id="summernote-container">
                            <textarea id="summernote-editor"></textarea>
                        </div>
                    `);
                    // hidden board id 설정
                    if ($("#board-id").length) {
                        $("#board-id").val(response[0].cboardId);
                    } else {
                        $("#campaign-id").after(`<input type="hidden" id="board-id" value="${response[0].cboardId}">`);
                    }
                    // 초기화: 편집용 Summernote 에디터 (#summernote-editor)
                    $('#summernote-editor').summernote({
                        height: 600,
                        lang: "ko-KR",
                        callbacks: {
                            onImageUpload: function (files) {
                                const file = files[0];
                                const formData = new FormData();
                                formData.append("file", file, file.name);
                                fetch("/api/image/upload", {
                                    method: "POST",
                                    credentials: "include",
                                    body: formData
                                })
                                    .then(response => {
                                        if (!response.ok) throw new Error("이미지 업로드 실패");
                                        return response.text();
                                    })
                                    .then(imageId => {
                                        return fetch(`/api/image/${imageId}`, { credentials: 'include' });
                                    })
                                    .then(response => {
                                        if (!response.ok) throw new Error("이미지 URL 요청 실패");
                                        return response.text();
                                    })
                                    .then(imageUrl => {
                                        $('#summernote-editor').summernote("insertImage", imageUrl);
                                    })
                                    .catch(error => {
                                        console.error("이미지 URL 불러오기 에러:", error);
                                    });
                            }
                        }
                    });
                    // 기존 board 내용을 편집용 에디터에 세팅
                    $('#summernote-editor').summernote('code', response[0].content);
                    // 버튼 전환: 수정하기 버튼 숨기고, 수정 저장 버튼 보이기
                    $(".summer-btn-edit").addClass("hidden");
                    $(".summer-btn-edit-save").removeClass("hidden");
                }
            },
            error: function (err) {
                console.error(err);
            }
        });
    });

    /* ============ 수정 적용하기 (수정 저장) ============ */
    $(document).on("click", ".main-board-add-btn-edit-save", function () {
        const boardId = $("#board-id").val();
        const updatedContent = $("#detail-summernote-editor").summernote("code");
        const updatedTitle = $("#detail-title-input").val();
        if (!boardId) {
            console.error("게시글 ID가 없습니다.");
            return;
        }
        const url = `/api/${campaignId}/board/${boardId}`;
        fetch(url, {
            method: "POST",
            headers: { "Content-Type": "application/json" },
            body: JSON.stringify({ content: updatedContent, title: updatedTitle }),
            credentials: "include"
        })
            .then(response => {
                if (!response.ok) throw new Error("수정 적용 실패");
                return response.json();
            })
            .then(result => {
                console.log("수정 적용 성공:", result);
                // 수정 후 수정모드 해제 및 UI 업데이트
                if (boardId == fixedMainBoardId) {
                    $(".content-box-in").html(updatedContent);
                } else {
                    $(".content-box-in-target").html(updatedContent);
                }
                $(".main-board-pe-in-title").html(updatedTitle);
                $(".main-board-add-btn-edit-save").addClass("hidden");
                $(".main-board-add-btn-edit").removeClass("hidden");
                reLoadBoard();
            })
            .catch(error => {
                console.error("수정 적용 에러:", error);
            });
    });

    // 관리자 승인전 수정상태 저장
    $(document).on("click", ".summer-btn-edit-save", function () {
        const content = $('#summernote-editor').summernote("code");
        const title = "Title"; // 제목 처리 (필요하면 값 가져오기)
        const boardId = $("#board-id").val();
        if (!boardId || boardId.trim() === "") {
            console.error("수정할 board id가 없습니다.");
            return;
        }
        const url = `/api/${campaignId}/board/${boardId}`; // 수정 엔드포인트 (POST 방식)
        fetch(url, {
            method: "POST",
            headers: { "Content-Type": "application/json" },
            body: JSON.stringify({ content: content, title: title }),
            credentials: "include"
        })
            .then(response => {
                if (!response.ok) {
                    throw new Error("수정 저장 실패");
                }
                return response.json();
            })
            .then(result => {
                console.log("수정 저장 성공:", result);
                $(".summer-btn-edit").removeClass("hidden");
                reLoadBoard();
            })
            .catch(error => {
                console.error("수정 저장 에러:", error);
            });

    });

    /* ============ 상세보기 게시글 삭제 ============ */
    $(document).on("click", ".main-board-detail-delete-btn", function () {
        if (!confirm("정말로 삭제하시겠습니까?")) return;
        const boardId = $("#board-id").val();
        if (!boardId) {
            console.error("게시글 ID가 없습니다.");
            return;
        }
        const url = `/api/board/delete/${boardId}`;
        fetch(url, {
            method: "GET",
            credentials: "include"
        })
            .then(response => {
                if (!response.ok) throw new Error("삭제 실패");
                return response.json();
            })
            .then(result => {
                console.log("삭제 성공:", result);
                reLoadBoard();
                // 상세보기 화면에서 목록으로 복귀
                const boardTitle = document.querySelector(".main-board-top-table");
                const boardInnerBox = document.querySelector(".main-board-back-div");
                const boardAllBox = document.querySelector(".main-board-total-pe");
                const boardInnerContent = document.querySelector(".content-board-box");
                boardTitle.classList.remove("hidden");
                boardInnerBox.classList.add("hidden");
                boardAllBox.classList.remove("hidden");
                boardInnerContent.classList.add("hidden");
            })
            .catch(error => {
                console.error("삭제 에러:", error);
            });
    });

    /* ============ 게시글 목록(리스트) 클릭 시 상세보기로 전환 ============ */
    function reBoard() {
        const boardItems = document.querySelectorAll(".main-board-pe");
        const boardTitle = document.querySelector(".main-board-top-table");
        const boardInnerBox = document.querySelector(".main-board-back-div");
        const boardAllBox = document.querySelector(".main-board-total-pe");
        const boardInnerContent = document.querySelector(".content-board-box");
        const boardBackBtn = document.querySelector(".main-board-back");

        boardItems.forEach((item) => {
            item.addEventListener("click", function () {
                const boardId = $(this).data("board-id");
                if (!boardId) return;
                $.ajax({
                    url: `/api/board/${boardId}`,
                    method: "GET",
                    success: function (response) {
                        if (response) {
                            // 상세보기 내용 업데이트
                            $(".content-box-in-target").html(response.content);
                            $(".main-board-pe-in-title").html(response.title);
                            $(".main-board-pe-in-date").html(response.date);
                            $(".content-board-box").removeClass("hidden");
                            if ($("#board-id").length) {
                                $("#board-id").val(boardId);
                            } else {
                                $("#campaign-id").after(`<input type="hidden" id="board-id" value="${boardId}">`);
                            }
                            // 버튼 노출: 신규 작성/수정 적용 버튼은 숨기고, 수정하기와 삭제 버튼 노출
                            $(".main-board-add-btn-save").addClass("hidden");
                            $(".main-board-add-btn-edit").removeClass("hidden");
                            $(".main-board-add-btn-edit-save").addClass("hidden");
                            $(".main-board-detail-delete-btn").removeClass("hidden");
                        }
                    },
                    error: function () {
                        console.error("게시글 불러오기 실패");
                    }
                });
                // UI 전환: 목록 -> 상세보기
                $(".main-reply").removeClass("hidden")
                replay(boardId);
                boardTitle.classList.add("hidden");
                boardInnerBox.classList.remove("hidden");
                boardAllBox.classList.add("hidden");
                boardInnerContent.classList.remove("hidden");
            });
        });

        // 뒤로 가기 버튼 클릭 시, 목록 화면으로 전환
        boardBackBtn.addEventListener("click", () => {
            boardTitle.classList.remove("hidden");
            boardInnerBox.classList.add("hidden");
            boardAllBox.classList.remove("hidden");
            boardInnerContent.classList.add("hidden");
            $(".main-reply").addClass("hidden")

        });
    }

    /* ============ 신규 작성 화면 전환 (예: "작성하기" 버튼 클릭 시) ============ */
    $(".main-board-add-btn").on("click", () => {
        const boardTitle = document.querySelector(".main-board-top-table");
        const boardInnerBox = document.querySelector(".main-board-back-div");
        const boardAllBox = document.querySelector(".main-board-total-pe");
        const boardInnerContent = document.querySelector(".content-board-box");
        // 신규 작성 시 제목 입력란 및 에디터 영역 설정
        $(".content-box-in-target").html(`
            <div class="cam-pro-bo-ri-in-content">
                <p class="cam-pro-bo-ri-in-content-title"></p>
                <div class="cam-pro-bo-ri-in-content-sum presentation-size"></div>
            </div>
        `);
        $(".main-board-pe-in-title").html(`<input placeholder="제목을 입력해주세요" class="main-board-add-btn-input">`);
        $(".content-board-box").removeClass("hidden");
        initSummernote(".presentation-size");
        boardTitle.classList.add("hidden");
        boardInnerBox.classList.remove("hidden");
        boardAllBox.classList.add("hidden");
        $(".main-board-add-btn-save").removeClass("hidden");
        $(".main-board-add-btn-edit").addClass("hidden");
        $(".main-board-detail-delete-btn").addClass("hidden");
    });

    /* ============ UI 초기화 (신규 작성 후 목록 복귀) ============ */
    function resetBoardUI() {
        const boardTitle = document.querySelector(".main-board-top-table");
        const boardInnerBox = document.querySelector(".main-board-back-div");
        const boardAllBox = document.querySelector(".main-board-total-pe");
        const boardInnerContent = document.querySelector(".content-board-box");
        const boardBtnSave = document.querySelector(".main-board-add-btn-save");
        boardBtnSave.classList.add("hidden");
        boardTitle.classList.remove("hidden");
        boardInnerBox.classList.add("hidden");
        boardAllBox.classList.remove("hidden");
        boardInnerContent.classList.add("hidden");
    }

    // 댓글 관련 js

    // 게시글에 맞는 댓글 가져오기
    function replay(boardId) {
        $.ajax({
            url: `/api/replys/${boardId}`,
            method: "GET",
            success: function (response) {
                $("#reply-border-target").val(boardId)
                $("#reply-data-target").html(`<input type="text" class="main-reply-input-box" placeholder="댓글을 입력해주세요">`);
                $(".main-reply-total strong").html(response.length)
                if(response.length === 0){
                    $("#reply-data-target").html(`<input type="text" class="main-reply-input-box" placeholder="댓글을 입력해주세요"><div>댓글이 없습니다.</div>`);
                    return
                }
                response.forEach((data) => {
                    let replyhtml = `
                    <div
                                class="main-reply-title"
                        >
                            <!-- 상단 이미지랑 등등 -->
                            <div
                                    class="main-reply-title-top"
                            >
                                <div
                                        class="main-reply-title-top-pro"
                                >
                                    <img src="${data.replyedBy.photo && data.replyedBy.photo.imageId
                                        ? `/api/image/${data.replyedBy.photo.imageId}`
                                        : 'https://assets.tumblbug.com/profile/default_avatar.png'}" 
                                         alt="프로필 이미지" class="target-img" />

                                    <div
                                            class="main-reply-title-top-pro-name"
                                    >
                                        <div>
                                            <div>${data.replyedBy.userName}</div>
                                        </div>
                                    </div>
                                </div>
                                <div
                                        class="main-reply-tag report-menu"
                                >
                                    <div
                                            class="main-reply-tag-box"
                                    >
                                        <div
                                                class="main-reply-tag-box-btn"
                                        >
                                            <svg
                                                    viewBox="0 0 48 48"
                                            >
                                                <path
                                                        fill-rule="evenodd"
                                                        clip-rule="evenodd"
                                                        d="M6.4 19C8.83 19 10.8 20.97 10.8 23.4C10.8 25.83 8.83 27.8 6.4 27.8C3.97 27.8 2 25.83 2 23.4C2 20.97 3.97 19 6.4 19ZM24.0001 19C26.4301 19 28.4001 20.97 28.4001 23.4C28.4001 25.83 26.4301 27.8 24.0001 27.8C21.5701 27.8 19.6001 25.83 19.6001 23.4C19.6001 20.97 21.5701 19 24.0001 19ZM45.9997 23.4C45.9997 20.97 44.0307 19 41.5997 19C39.1697 19 37.2007 20.97 37.2007 23.4C37.2007 25.83 39.1697 27.8 41.5997 27.8C44.0307 27.8 45.9997 25.83 45.9997 23.4Z"
                                                ></path>
                                            </svg>
                                        </div>
                                    </div>
                                </div>
                            </div>
                            <div
                                    style="height: 20px"
                            ></div>
                            <div
                                    class="main-reply-content"
                            >
                                ${data.content}
                            </div>
                        </div>
                `;
                    $("#reply-data-target").append(replyhtml);
                });
                // 댓글 HTML 추가 후 이미지 갱신 실행
                $("#reply-data-target").find("img.target-img").each(function() {
                    const $img = $(this);
                    const endpoint = $img.attr("src"); // 이 값은 /api/image/{imageId} 형식임
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
            error: function () {
                console.error("댓글 불러오기 실패");
            }
        });
    }

    // 댓글 작성하기
    $(document).on('keypress', '.main-reply-input-box', function(e) {
        if (e.which === 13) {  // 엔터 키 코드
            e.preventDefault();
            const replyContent = $(this).val().trim();
            if (!replyContent) return; // 빈 값이면 동작하지 않음

            // 예시로 boardId 값을 data 속성 또는 별도 변수에서 가져옴
            const boardId = $("#reply-border-target").val(); // 또는 적절한 방식으로 boardId를 가져오기

            $.ajax({
                url: '/api/replys/add',  // 댓글 생성 엔드포인트 (POST 방식)
                method: 'POST',
                contentType: 'application/json',
                data: JSON.stringify({
                    boardId: boardId,
                    content: replyContent
                }),
                success: function(response) {
                    // 댓글 작성 성공 시 input 초기화 및 댓글 목록 갱신 등 처리
                    $('.main-reply-input-box').val('');
                    // 예를 들어, 기존의 reply 목록을 다시 불러옴
                    replay(boardId);
                },
                error: function(err) {
                    console.error("댓글 작성 실패:", err);
                }
            });
        }
    });

    // 리뷰 불러오기

    function reviews(campaignId) {
        $.ajax({
            url: `/api/review/${campaignId}`,
            method: "GET",
            success: function (response) {
                $(".main-review-total strong").html(response.length)
                if(response.length === 0){
                    $("#review-data-target").html(`<div>리뷰가 없습니다.</div>`);
                    return
                }
                response.forEach((data) => {
                    let replyhtml = `
                        <div class="main-review-title">
                            <!-- 상단 이미지랑 등등 -->
                            <div class="main-reply-title-top">
                                <div class="main-reply-title-top-pro">
                                    <img src="${data.reviewedBy.photo && data.reviewedBy.photo.imageId
                                            ? `/api/image/${data.reviewedBy.photo.imageId}`
                                            : 'https://assets.tumblbug.com/profile/default_avatar.png'}" 
                                         alt="프로필 이미지" class="target-review-img" />
                                    <div class="main-reply-title-top-pro-name">
                                        <div>
                                            <div>${data.reviewedBy.userName}</div>
                                        </div>
                                    </div>
                                    <div class="main-reply-title-top-pro-count">
                                        <div>${data.rated}</div>
                                    </div>
                                </div>
                                <div class="main-reply-tag report-menu">
                                    <div class="main-reply-tag-box">
                                        <div class="main-reply-tag-box-btn">
                                            <svg viewBox="0 0 48 48">
                                                <path fill-rule="evenodd" clip-rule="evenodd" d="M6.4 19C8.83 19 10.8 20.97 10.8 23.4C10.8 25.83 8.83 27.8 6.4 27.8C3.97 27.8 2 25.83 2 23.4C2 20.97 3.97 19 6.4 19ZM24.0001 19C26.4301 19 28.4001 20.97 28.4001 23.4C28.4001 25.83 26.4301 27.8 24.0001 27.8C21.5701 27.8 19.6001 25.83 19.6001 23.4C19.6001 20.97 21.5701 19 24.0001 19ZM45.9997 23.4C45.9997 20.97 44.0307 19 41.5997 19C39.1697 19 37.2007 20.97 37.2007 23.4C37.2007 25.83 39.1697 27.8 41.5997 27.8C44.0307 27.8 45.9997 25.83 45.9997 23.4Z"></path>
                                            </svg>
                                        </div>
                                    </div>
                                </div>
                            </div>
                            <div style="height: 20px"></div>
                            <div class="main-reply-content">
                                <div>${data.content}</div>
                            </div>
                        </div>
                `;
                    $("#review-data-target").append(replyhtml);
                });
                // 댓글 HTML 추가 후 이미지 갱신 실행
                $("#review-data-target").find("img.target-review-img").each(function() {
                    const $img = $(this);
                    const endpoint = $img.attr("src"); // 이 값은 /api/image/{imageId} 형식임
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
            error: function () {
                console.error("댓글 불러오기 실패");
            }
        });
    }

    // 별점 관련 코드
    document.querySelectorAll('.star').forEach(function(star) {
        // 별마다 별도의 잠금 상태와 저장된 값을 관리합니다.
        let isLocked = false;
        let savedValue = null;

        star.addEventListener('mousemove', function(e) {
            if (isLocked) return;  // 이미 잠금 상태면 변경하지 않음
            const rect = this.getBoundingClientRect();
            let percent = (e.clientX - rect.left) / rect.width;
            percent = Math.round(percent * 20) / 20;  // 5% 단위 반올림
            this.querySelector('em').style.width = (percent * 100) + '%';
        });

        star.addEventListener('mouseleave', function() {
            if (isLocked) return;
            this.querySelector('em').style.width = '0%';
        });

        star.addEventListener('click', function(e) {
            const rect = this.getBoundingClientRect();
            let percent = (e.clientX - rect.left) / rect.width;
            percent = Math.round(percent * 10) / 10;  // 10단위 반올림 (원하는 단위에 맞게 조절)
            if (!isLocked) {
                // 클릭 시 잠금 상태가 아니라면 현재 위치의 값을 저장 및 고정
                this.querySelector('em').style.width = (percent * 100) + '%';
                savedValue = percent; // 예: 0.7 (70%) 값 저장
                isLocked = true;
                // 전역 변수에 저장해서 리뷰 등록 시 사용
                window.starRatingValue = savedValue;
                this.classList.add('locked');
                console.log("Saved value:", savedValue);
            } else {
                // 이미 잠금 상태이면 클릭 시 해제하여 초기 상태로 복구
                isLocked = false;
                savedValue = null;
                window.starRatingValue = null;
                this.querySelector('em').style.width = '0%';
                this.classList.remove('locked');
                console.log("Reset value");
            }
        });
    });

// 리뷰 등록 관련 코드
    $(document).on('keypress', '.main-review-input-box', function(e) {
        if (e.which === 13) {  // 엔터 키 감지
            e.preventDefault();
            const reviewContent = $(this).val().trim();
            if (!reviewContent) return;  // 내용이 없으면 처리 중단

            // 별점이 등록되어 있지 않으면 리뷰 등록 불가 처리 (알림 처리 등)
            if (!window.starRatingValue) {
                alert("리뷰를 등록하려면 먼저 별점을 선택해주세요!");
                return;
            }

            $.ajax({
                url: '/api/review/add',  // 리뷰 등록 엔드포인트 (POST 방식)
                method: 'POST',
                contentType: 'application/json',
                data: JSON.stringify({
                    campaignId: campaignId,            // 캠페인 ID
                    reviewContent: reviewContent,        // 리뷰 내용
                    rating: window.starRatingValue       // 선택된 별점 값
                }),
                success: function(response) {
                    // 리뷰 등록 성공 시 input 초기화 및 리뷰 목록 갱신 처리
                    $("#review-data-target").html("");
                    // 예를 들어, 기존의 리뷰 목록을 다시 불러오는 함수 호출
                    reviews(campaignId)
                },
                error: function(err) {
                    console.error("리뷰 등록 실패:", err);
                }
            });
        }
    });



    // 오른쪽에 뜨는 리워드 나오게
    function rewards(campaignId) {
        $.ajax({
            url: `/api/reward/${campaignId}`,
            method: "GET",
            success: function (response) {
                console.log(response);
            },
            error: function () {
                console.error("댓글 불러오기 실패");
            }
        });
    }

    rewards(27);


    reviews(campaignId);
    reLoadBoard();
    reBoard();
});

/* ============ 기타 (탭, 리포트 등) ============ */
document.addEventListener("DOMContentLoaded", () => {
    const reportButtons = document.querySelectorAll(".report-menu");
    const reportMenu = document.querySelector(".ul-add");
    const reportCall = document.querySelector(".ul-add-li");
    const reportModal = document.querySelector(".report");
    const reportClose = document.querySelector(".report-close");

    reportButtons.forEach((button) => {
        button.addEventListener("click", (event) => {
            event.stopPropagation();
            if (reportMenu.classList.contains("hidden")) {
                reportMenu.classList.remove("hidden");
                reportMenu.style.display = "block";
                const rect = button.getBoundingClientRect();
                reportMenu.style.top = `${rect.bottom + window.scrollY}px`;
                reportMenu.style.left = `${rect.left + window.scrollX}px`;
            } else {
                reportMenu.classList.add("hidden");
                reportMenu.style.display = "none";
            }
        });
    });

    document.addEventListener("click", (event) => {
        if (!reportMenu.contains(event.target)) {
            reportMenu.classList.add("hidden");
            reportMenu.style.display = "none";
        }
    });

    reportCall.addEventListener("click", () => {
        reportModal.classList.remove("hidden");
    });

    reportClose.addEventListener("click", () => {
        reportModal.classList.add("hidden");
    });

    const campaignTab = document.querySelector(".main-mi-na-in-box-pe.campaign");
    const boardTab = document.querySelector(".main-mi-na-in-box-pe.board");
    const replyTab = document.querySelector(".main-mi-na-in-box-pe.review");
    const navItems = [campaignTab, boardTab, replyTab];

    function removeCheckClass() {
        navItems.forEach((nav) => nav.classList.remove("check"));
    }

    const contentBox = document.querySelector(".content-box");
    // const replyBox = document.querySelector(".main-reply");
    const boardBox = document.querySelector(".main-board");

    campaignTab.addEventListener("click", () => {
        removeCheckClass();
        campaignTab.classList.add("check");
        contentBox.classList.remove("hidden");
        // replyBox.classList.add("hidden");
        boardBox.classList.add("hidden");
    });

    boardTab.addEventListener("click", () => {
        removeCheckClass();
        boardTab.classList.add("check");
        contentBox.classList.add("hidden");
        boardBox.classList.remove("hidden");
        // replyBox.classList.add("hidden");
    });

    replyTab.addEventListener("click", () => {
        removeCheckClass();
        replyTab.classList.add("check");
        contentBox.classList.add("hidden");
        boardBox.classList.add("hidden");
        // replyBox.classList.remove("hidden");
    });
});

