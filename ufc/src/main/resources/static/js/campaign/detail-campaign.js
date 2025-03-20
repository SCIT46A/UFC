$(document).ready(function () {
    const campaignId = $("#campaign-id").val();
    const campaignStatus = $("#campaign-status").val();

    $(".main-review").addClass("hidden");

    let fixedMainBoardId = null; // 고정(0번째) 게시글 ID

    $(".main-mi-na-in-box-pe.campaign").on("click", () => {
        $(".main-reply").removeClass("hidden");
        $(".main-review").addClass("hidden");
        replay(fixedMainBoardId);
    });

    $(".main-mi-na-in-box-pe.board").on("click", () => {
        $(".main-reply").addClass("hidden");
        $(".main-board-top-table").removeClass("hidden");
        $(".main-board-back-div").addClass("hidden");
        $(".main-board-total-pe").removeClass("hidden");
        $(".content-board-box").addClass("hidden");
        $(".main-review").addClass("hidden");
    });

    $(".main-mi-na-in-box-pe.review").on("click", () => {
        $(".main-reply").addClass("hidden");
        $(".main-board-top-table").removeClass("hidden");
        $(".main-board-back-div").addClass("hidden");
        $(".main-board-total-pe").removeClass("hidden");
        $(".content-board-box").addClass("hidden");
        $(".main-review").removeClass("hidden");
    });

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
                            return fetch(`/api/image/${imageId}/board`, { credentials: "include" });
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
                        $(".main-reply").addClass("hidden");
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
                }
                if (response.length == 0) {
                    $(".summer-btn-edit").addClass("hidden");
                    $(".summer-btn").removeClass("hidden");
                    $(".summer-btn-edit-save").addClass("hidden");
                    $(".main-reply").addClass("hidden");
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
                    let isFixed = (boardId == fixedMainBoardId);
                    if (isFixed) {
                        $(".content-box-in").html('<div id="edit-container"><input type="text" id="detail-title-input" value="' + response.title + '" style="width:100%; margin-bottom:10px;" /><textarea id="detail-summernote-editor"></textarea></div>');
                    } else {
                        $(".content-box-in-target").html('<div id="edit-container"><input type="text" id="detail-title-input" value="' + response.title + '" style="width:100%; margin-bottom:10px;" /><textarea id="detail-summernote-editor"></textarea></div>');
                    }
                    initSummernote('#detail-summernote-editor');
                    $('#detail-summernote-editor').summernote('code', response.content);
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
                    $(".content-box-in").html(`
                        <p style="color: gray">
                          ※ 게시글을 작성하셔야 관리자에게 캠페인을 승인받을 수 있습니다.
                        </p>
                        <div id="summernote-container">
                            <textarea id="summernote-editor"></textarea>
                        </div>
                    `);
                    if ($("#board-id").length) {
                        $("#board-id").val(response[0].cboardId);
                    } else {
                        $("#campaign-id").after(`<input type="hidden" id="board-id" value="${response[0].cboardId}">`);
                    }
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
                                        return fetch(`/api/image/${imageId}/board`, { credentials: 'include' });
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
                    $('#summernote-editor').summernote('code', response[0].content);
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
        const title = "Title";
        const boardId = $("#board-id").val();
        if (!boardId || boardId.trim() === "") {
            console.error("수정할 board id가 없습니다.");
            return;
        }
        const url = `/api/${campaignId}/board/${boardId}`;
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
                            $(".content-box-in-target").html(response.content);
                            $(".main-board-pe-in-title").html(response.title);
                            $(".main-board-pe-in-date").html(response.date);
                            $(".content-board-box").removeClass("hidden");
                            if ($("#board-id").length) {
                                $("#board-id").val(boardId);
                            } else {
                                $("#campaign-id").after(`<input type="hidden" id="board-id" value="${boardId}">`);
                            }
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
                $(".main-reply").removeClass("hidden");
                replay(boardId);
                boardTitle.classList.add("hidden");
                boardInnerBox.classList.remove("hidden");
                boardAllBox.classList.add("hidden");
                boardInnerContent.classList.remove("hidden");
            });
        });

        boardBackBtn.addEventListener("click", () => {
            boardTitle.classList.remove("hidden");
            boardInnerBox.classList.add("hidden");
            boardAllBox.classList.remove("hidden");
            boardInnerContent.classList.add("hidden");
            $(".main-reply").addClass("hidden");
        });
    }

    /* ============ 신규 작성 화면 전환 (예: "작성하기" 버튼 클릭 시) ============ */
    $(".main-board-add-btn").on("click", () => {
        const boardTitle = document.querySelector(".main-board-top-table");
        const boardInnerBox = document.querySelector(".main-board-back-div");
        const boardAllBox = document.querySelector(".main-board-total-pe");
        const boardInnerContent = document.querySelector(".content-board-box");
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
    function replay(boardId) {
        $.ajax({
            url: `/api/replys/${boardId}`,
            method: "GET",
            success: function (response) {
                $("#reply-border-target").val(boardId);
                $("#reply-data-target").html(`<input type="text" class="main-reply-input-box" placeholder="댓글을 입력해주세요">`);
                $(".main-reply-total strong").html(response.length);
                if (response.length === 0) {
                    $("#reply-data-target").html(`<input type="text" class="main-reply-input-box" placeholder="댓글을 입력해주세요"><div>댓글이 없습니다.</div>`);
                    return;
                }
                response.forEach((data) => {
                    let replyhtml = `
                    <div class="main-reply-title">
                        <div class="main-reply-title-top">
                            <div class="main-reply-title-top-pro">
                                <img src="${data.replyedBy.photo && data.replyedBy.photo.imageId
                        ? `/api/image/${data.replyedBy.photo.imageId}`
                        : 'https://assets.tumblbug.com/profile/default_avatar.png'}" 
                                     alt="프로필 이미지" class="target-img" />
                                <div class="main-reply-title-top-pro-name">
                                    <div>
                                        <div>${data.replyedBy.userName}</div>
                                    </div>
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
                            ${data.content}
                        </div>
                    </div>
                    `;
                    $("#reply-data-target").append(replyhtml);
                });
                $("#reply-data-target").find("img.target-img").each(function() {
                    const $img = $(this);
                    const endpoint = $img.attr("src");
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
            },
            error: function () {
                console.error("댓글 불러오기 실패");
            }
        });
    }

    $(document).on('keypress', '.main-reply-input-box', function(e) {
        if (e.which === 13) {
            e.preventDefault();
            const replyContent = $(this).val().trim();
            if (!replyContent) return;
            const boardId = $("#reply-border-target").val();
            $.ajax({
                url: '/api/replys/add',
                method: 'POST',
                contentType: 'application/json',
                data: JSON.stringify({
                    boardId: boardId,
                    content: replyContent
                }),
                success: function(response) {
                    $('.main-reply-input-box').val('');
                    replay(boardId);
                },
                error: function(err) {
                    if (err.status === 401) {
                        alert("로그인을 해주세요");
                        // 현재 페이지 URL을 쿼리 파라미터로 전달
                        window.location.href = '/user/login?redirectUrl=' + encodeURIComponent(window.location.href);
                    } else {
                        console.error("댓글 작성 실패:", err);
                    }
                }
            });
        }
    });



    function reviews(campaignId) {
        $.ajax({
            url: `/api/review/${campaignId}`,
            method: "GET",
            success: function (response) {
                $(".main-review-total strong").html(response.length);
                if (response.length === 0) {
                    $("#review-data-target").html(`<div>리뷰가 없습니다.</div>`);
                    return;
                }
                response.forEach((data) => {
                    let replyhtml = `
                        <div class="main-review-title">
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
                $("#review-data-target").find("img.target-review-img").each(function() {
                    const $img = $(this);
                    const endpoint = $img.attr("src");
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
            },
            error: function () {
                console.error("댓글 불러오기 실패");
            }
        });
    }

    // 별점 관련 코드
    document.querySelectorAll('.star').forEach(function(star) {
        let isLocked = false;
        let savedValue = null;
        star.addEventListener('mousemove', function(e) {
            if (isLocked) return;
            const rect = this.getBoundingClientRect();
            let percent = (e.clientX - rect.left) / rect.width;
            percent = Math.round(percent * 20) / 20;
            this.querySelector('em').style.width = (percent * 100) + '%';
        });
        star.addEventListener('mouseleave', function() {
            if (isLocked) return;
            this.querySelector('em').style.width = '0%';
        });
        star.addEventListener('click', function(e) {
            const rect = this.getBoundingClientRect();
            let percent = (e.clientX - rect.left) / rect.width;
            percent = Math.round(percent * 10) / 10;
            if (!isLocked) {
                this.querySelector('em').style.width = (percent * 100) + '%';
                savedValue = percent;
                isLocked = true;
                window.starRatingValue = savedValue;
                this.classList.add('locked');
                console.log("Saved value:", savedValue);
            } else {
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
        if (e.which === 13) {
            e.preventDefault();
            const reviewContent = $(this).val().trim();
            if (!reviewContent) return;
            if (!window.starRatingValue) {
                alert("리뷰를 등록하려면 먼저 별점을 선택해주세요!");
                return;
            }
            $.ajax({
                url: '/api/review/add',
                method: 'POST',
                contentType: 'application/json',
                data: JSON.stringify({
                    campaignId: campaignId,
                    reviewContent: reviewContent,
                    rating: window.starRatingValue
                }),
                success: function(response) {
                    $("#review-data-target").html("");
                    reviews(campaignId);
                },
                error: function(err) {
                    if (err.status === 409) {
                        alert("이미 별점을 주셨습니다");
                    } else if (err.status === 401) {
                        alert("로그인을 해주세요");
                        window.location.href = '/user/login?redirectUrl=' + encodeURIComponent(window.location.href);
                    } else {
                        console.error("리뷰 등록 실패:", err);
                    }
                }
            });
        }
    });



    // 오른쪽에 뜨는 리워드 나오게
    // 공통: 리워드 선택(추가) 함수 (중복 체크 및 최대수량 데이터 포함)
    // --- addRewardChoice 함수 (네 개 인자 사용)
    // 총합 업데이트 함수
    // 총합 업데이트 함수 (각 항목별 이름과 총 개수를 표시)
    function updateDonationTotal() {
        // 항목들을 고유 키(예: basename, materialId, rewardId의 조합)로 그룹화합니다.
        let items = [];
        $(".reward-choice-my-pe").each(function () {
            let baseName = $(this).data("basename") || "";
            let count = parseInt($(this).find(".reward-choice-count").val()) || 0;
            let required = parseInt($(this).data("required")) || 1;
            let materialId = $(this).data("material-id") || "";
            let rewardId = $(this).data("reward-id") || "";
            let total = count * required;
            // 그룹핑을 위해 세 가지 값의 조합을 키로 사용합니다.
            let key = baseName + "|" + materialId + "|" + rewardId;
            let existing = items.find(item => item.key === key);
            if (existing) {
                // count와 total을 누적합니다.
                existing.count += count;
                existing.total += total;
            } else {
                items.push({
                    key: key,
                    name: baseName,
                    count: count,
                    required: required,
                    total: total,
                    materialId: materialId,
                    rewardId: rewardId,
                    campaignId : campaignId
                });
            }
        });
        // 화면에 보여줄 텍스트: 예) "병뚜껑 40개, ..."
        let displayText = items.map(item => `${item.name} ${item.total}개`).join(", ");
        // 숨겨진 p 태그에는 상세 정보(각 항목의 이름, count, required, total, materialId, rewardId)를 JSON 문자열로 저장
        let valueText = JSON.stringify(items);
        $(".reward-btn-in span b").text(displayText);
        $(".reward-btn-in p").text(valueText);
    }


// --- addRewardChoice 함수 (네 개 인자 사용)
//
    function addRewardChoice(displayName, baseName, required, available, materialId, rewardId) {
        // 이미 같은 항목이 추가되어 있으면 아무 작업도 하지 않음 (displayName 기준)
        if ($(".reward-choice-my-pe-box-top-in").filter(function() {
            return $(this).text().trim() === displayName;
        }).length > 0) {
            return;
        }
        let defaultCount = 1;
        let total = defaultCount * required;
        let newRewardHtml = `
        <div class="reward-choice-my-pe" data-available="${available}" data-required="${required}" data-basename="${baseName}" data-material-id="${materialId}" data-reward-id="${rewardId}">
            <div class="reward-choice-my-pe-box">
                <div class="reward-choice-my-pe-box-top">
                    <ul>
                        <li>
                            <div class="reward-choice-my-pe-box-top-in">
                                ${displayName}
                            </div>
                        </li>
                    </ul>
                </div>
                <div class="reward-choice-my-pe-box-bo">
                    <div class="reward-choice-my-pe-box-bo-le">
                        <button class="reward-choice-btn remove-reward">
                            <div class="reward-choice-btn-le">
                                <svg viewBox="0 0 48 48">
                                    <path d="M6 24.1C6 22.9402 6.9402 22 8.1 22H39.9C41.0598 22 42 22.9402 42 24.1C42 25.2598 41.0598 26.2 39.9 26.2H8.1C6.9402 26.2 6 25.2598 6 24.1Z"></path>
                                </svg>
                            </div>
                        </button>
                        <input type="text" class="reward-choice-count" value="${defaultCount}" readonly="">
                        <button class="reward-choice-btn add-reward">
                            <div class="reward-choice-btn-le">
                                <svg viewBox="0 0 48 48">
                                    <path fill-rule="evenodd" clip-rule="evenodd" d="M43.7104 21.8549H25.99V4.09524C25.99 2.89796 24.9945 2 23.9005 2C22.8054 2 21.81 2.89796 21.81 4.09524V21.9546H4.0905C2.89593 21.8549 2 22.8526 2 23.9501C2 25.0476 2.89593 26.0454 4.0905 26.0454H21.9095V43.9048C21.9095 45.0023 22.8054 46 23.999 46C25.095 46 26.0905 45.102 26.0905 43.9048V26.0454H43.9085C45.0045 26.0454 46 25.1474 46 23.9501C45.8009 22.8526 44.904 21.8549 43.7104 21.8549Z"></path>
                                </svg>
                            </div>
                        </button>
                    </div>
                    <div class="reward-choice-pri">
                        ${baseName} <span class="reward-choice-quantity">${total}</span> 개
                    </div>
                </div>
            </div>
            <button class="reward-choice-close">
                <div class="reward-choice-close-in">
                    <svg width="10" height="10" viewBox="0 0 10 10" fill="none" xmlns="http://www.w3.org/2000/svg">
                        <path fill-rule="evenodd" clip-rule="evenodd" d="M4.28544 5.00257L2.01916 2.73642C1.82521 2.54248 1.82974 2.23083 2.01598 2.02765C2.21448 1.81131 2.5294 1.8394 2.72795 2.02108L2.72969 2.02268L4.99738 4.2905L7.26357 2.02431C7.4575 1.83056 7.7691 1.83508 7.97226 2.02115C8.1886 2.21946 8.16077 2.53473 7.97878 2.73311L7.97723 2.73479L5.70945 5.00257L7.97564 7.26876C8.16953 7.46283 8.16504 7.77425 7.97884 7.97756L7.97724 7.9793L7.97557 7.98097C7.78164 8.17472 7.47008 8.17023 7.26691 7.98417L7.26519 7.98259L4.99738 5.71465L2.73129 7.981C2.53725 8.17469 2.22572 8.17025 2.02253 7.98417L2.01908 7.98101L2.01592 7.97756C1.82971 7.77425 1.82526 7.46279 2.01916 7.26872L4.28544 5.00257Z" fill="#6D6D6D"></path>
                    </svg>
                </div>
            </button>
        </div>
    `;
        $(".reward-choice-my").append(newRewardHtml);
        updateDonationTotal(); // 추가 후 총합 업데이트
    }

// --- Select 이벤트 (옵션 선택 시 "기부하기" 붙여 추가)
    $("select").change(function () {
        let baseName = $(this).val();
        if (!baseName) return;
        let displayName = baseName + " 기부하기";
        let available = 100; // 기본 최대 수량
        let materialId = $(this).find("option:selected").data("material-id");
        // 여기서는 요구수량을 1로 처리 (필요시 변경)
        addRewardChoice(displayName, baseName, 1, available,materialId , null);
        $(this).prop('selectedIndex', 0);
    });

// --- 리워드 리스트 클릭 이벤트 (상단에 추가)
// data 속성으로 저장된 target 정보를 이용하여 추가
    $(document).on("click", ".reward-info-in-box", function () {
        let rewardName = $(this).data("reward-name");
        let targetName = $(this).data("reward-target-name");
        let required = $(this).data("required");
        let available = $(this).data("available");
        // 여기서는 targetName을 baseName으로 사용 (하단 표시에는 "기부하기" 없이)
        let materialId = $(this).data("target-id");
        let rewardId = $(this).data("reward_id");
        addRewardChoice(rewardName, targetName, required, available, materialId, rewardId);

    });

// --- 개수 증가 버튼 (최대 available까지만 증가; 총합은 선택수 × 요구수량)
    $(document).on("click", ".add-reward", function () {
        let $container = $(this).closest(".reward-choice-my-pe");
        let available = parseInt($container.data("available"));
        let required = parseInt($container.data("required"));
        let $countInput = $container.find(".reward-choice-count");
        let count = parseInt($countInput.val());
        if (count < available) {
            count++;
            $countInput.val(count);
            $container.find(".reward-choice-quantity").text(count * required);
            updateDonationTotal();
        }
    });

// --- 개수 감소 버튼 (최소 1개 유지)
    $(document).on("click", ".remove-reward", function () {
        let $container = $(this).closest(".reward-choice-my-pe");
        let required = parseInt($container.data("required"));
        let $countInput = $container.find(".reward-choice-count");
        let count = parseInt($countInput.val());
        if (count > 1) {
            count--;
            $countInput.val(count);
            $container.find(".reward-choice-quantity").text(count * required);
            updateDonationTotal();
        }
    });

// --- 삭제 버튼: 해당 리워드 항목 삭제
    $(document).on("click", ".reward-choice-close", function () {
        $(this).closest(".reward-choice-my-pe").remove();
        updateDonationTotal();
    });

// --- rewards() 함수 (리워드 목록 표시)
    function rewards(campaignId) {
        $.ajax({
            url: `/api/reward/${campaignId}`,
            method: "GET",
            success: function (response) {
                $(".reward-target").html("");
                response.forEach((data) => {
                    let rewardMaterialsHtml = data.rewardMaterials.map((dataAdd) => `
                    <div class="reward-info-pr-in-de">
                        ${dataAdd.material.name} ${dataAdd.quantityRequired}개
                    </div>
                `).join("");
                    let rewardItemHtml = data.rewardItems.map((dataAdd) => `
                    <li class="reward-ul-li">
                        <span>${dataAdd.item.name}</span>
                        <span>${dataAdd.quantity}개</span>
                    </li>
                `).join("");
                    // target 정보는 첫 번째 재료를 기준으로 설정
                    let targetName = data.rewardMaterials.length > 0 ? data.rewardMaterials[0].material.name : "";
                    let required = data.rewardMaterials.length > 0 ? data.rewardMaterials[0].quantityRequired : 1;
                    let targetId = data.rewardMaterials.length > 0 ? data.rewardMaterials[0].material.materialId : "";
                    let rewardhtml = `
                        <div class="reward-info-in-box" 
                             data-reward-name="${data.rewardName}" 
                             data-available="${data.amount}" 
                             data-reward-target-name="${targetName}" 
                             data-required="${required}"
                             data-reward_id="${data.rewardId}"
                             data-target-id="${targetId}">
                            <div class="reward-info-in-box-in">
                                <div class="reward-info-in-box-in-warp">
                                    <section class="reward-info-in-box-in-warp-section">
                                        <div class="reward-info-in-box-in-warp-section-in">
                                            <div class="reward-info-in-box-in-warp-section-in-top">
                                                <div class="reward-info-in-box-in-warp-section-in-top-in">
                                                    <div class="reward-info-in-box-in-warp-section-in-top-in-img">
                                                        <svg viewBox="0 0 48 48">
                                                            <path fill-rule="evenodd" clip-rule="evenodd"
                                                                d="M41.6 8L18.9 30.8L6.2 19L2 23.5L19.1 39.4L46 12.4L41.6 8Z">
                                                            </path>
                                                        </svg>
                                                    </div>
                                                    ${data.amount}개 남음
                                                </div>
                                            </div>
                                            <div class="reward-info-pr">
                                                <div class="reward-info-pr-in">
                                                    ${rewardMaterialsHtml}
                                                </div>
                                                <div class="reward-info-pr-in-bo">
                                                    ${data.rewardName}
                                                </div>
                                            </div>
                                            <ul class="reward-ul">
                                                ${rewardItemHtml}
                                            </ul>
                                        </div>
                                    </section>
                                </div>
                            </div>
                        </div>
                        `;

                    $(".reward-target").append(rewardhtml);
                });
            },
            error: function () {
                console.error("리워드 불러오기 실패");
            }
        });
    }

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

    $.ajax({
        url: "/api/likeTopCampaign",
        method: "GET",
        success: function (response) {
            let htmlResult = "";
            if (response.length < 6) {
                document.querySelector(".main-resent-all-add-btn-ri").classList.add("hidden");
            }
            console.log(response)
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
                                        ${parseFloat(data.donationPercentage).toFixed(2)}% 달성!
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
        if (campaignRightBtn && campaignLeftBtn && campaignView) {
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
        }

        const productRightBtn = document.querySelector(".main-resent-all-add-btn-add-ri");
        const productLeftBtn = document.querySelector(".main-resent-all-add-btn-add-le");
        const productView = document.querySelector("#product-target");
        if (productRightBtn && productLeftBtn && productView) {
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
    }



    // 좋아요 버튼 만들기
    $(document).on("click", ".reward-in-se-in-bo-btn-ri", function(event) {
        event.preventDefault();
        event.stopPropagation();

        var $btn = $(this);
        if ($btn.data("ajaxInProgress")) {
            return;
        }
        $btn.data("ajaxInProgress", true);

        // 버튼의 data-creator-id 값을 가져옵니다.
        var creatorId = $btn.data("creatorId");
        var itemType = "creator";
        var currentState = $btn.hasClass("liked");

        $.ajax({
            url: "/api/like/toggle",
            method: "POST",
            data: {
                itemId: creatorId, // creatorId가 전송됩니다.
                type: itemType,
                currentState: currentState
            },
            success: function(response) {
                if (response.success) {
                    if (response.isLiked) {
                        // 좋아요가 true이면 'liked'와 'unlike' 클래스를 추가하여 스타일 변경
                        $btn.addClass("liked");
                        $btn.addClass("unlike");
                    } else {
                        // 좋아요가 false이면 두 클래스를 모두 제거
                        $btn.removeClass("liked");
                        $btn.removeClass("unlike");
                    }
                } else {
                    console.error("좋아요 토글 실패:", response.message);
                }
            },
            error: function(err) {
                if (err.status === 401) {
                    alert("로그인이 필요합니다.");
                    // 현재 페이지 URL을 쿼리 파라미터로 전달하여 로그인 페이지로 이동
                    window.location.href = '/user/login?redirectUrl=' + encodeURIComponent(window.location.href);
                } else {
                    console.error("좋아요 토글 AJAX 오류:", err);
                }
            },
            complete: function() {
                $btn.removeData("ajaxInProgress");
            }
        });
    });


    $(document).on("click", ".main-funding-in-box-le-in-in-like", function(event) {
        event.preventDefault();
        event.stopPropagation();

        var $btn = $(this);
        if ($btn.data("ajaxInProgress")) {
            return;
        }
        $btn.data("ajaxInProgress", true);

        var itemType = "campaign";
        var currentState = $btn.hasClass("liked");
        // 전역 변수 campaignId를 그대로 사용합니다.
        // 또는 새 변수에 할당:
        var currentCampaignId = campaignId;

        $.ajax({
            url: "/api/like/toggle",
            method: "POST",
            data: {
                itemId: currentCampaignId,
                type: itemType,
                currentState: currentState
            },
            success: function(response) {
                if (response.success) {
                    if (response.isLiked) {
                        $btn.addClass("liked");
                        $btn.addClass("likeFill");
                    } else {
                        $btn.removeClass("liked");
                        $btn.removeClass("likeFill");
                    }
                } else {
                    console.error("좋아요 토글 실패:", response.message);
                }
            },
            error: function(err) {
                if (err.status === 401) {
                    alert("로그인이 필요합니다.");
                    // 현재 페이지 URL을 쿼리 파라미터로 전달하여 로그인 페이지로 이동
                    window.location.href = '/user/login?redirectUrl=' + encodeURIComponent(window.location.href);
                } else {
                    console.error("좋아요 토글 AJAX 오류:", err);
                }
            },
            complete: function() {
                $btn.removeData("ajaxInProgress");
            }
        });
    });

    $(document).on("click", ".reward-btn-in", function(event) {
        event.preventDefault();

        // 기부 정보 텍스트(예: "a 10개, b 20개") 가져오기
        var donationDetails = $(".reward-btn-in p").text().trim();

        // 기부 항목이 선택되지 않았다면 경고
        if (!donationDetails) {
            alert("기부할 항목을 먼저 선택해 주세요.");
            return;
        }

        // hidden input에서 loginUserId 값을 읽어 로그인 상태 확인
        var loginUserId = $("#loginUserId").val();
        if (!loginUserId) {
            alert("로그인을 해주세요");
            var currentUrl = window.location.href;
            window.location.href = '/user/login?redirectUrl=' + encodeURIComponent(currentUrl);
            return;
        }

        // 로그인 상태라면 /campaign/pay 페이지로 이동하며 donationDetails 쿼리 파라미터 전달
        window.location.href = "/campaign/pay?donationDetails=" + encodeURIComponent(donationDetails);
    });



    $(document).on("click", ".reward-in-se-in-bo-btn-le", function(event) {
        event.preventDefault();

        // hidden input에서 loginUserId 값을 읽어 로그인 상태 확인
        var loginUserId = $("#loginUserId").val();
        if (!loginUserId) {
            alert("로그인을 해주세요");
            var currentUrl = window.location.href;
            window.location.href = '/user/login?redirectUrl=' + encodeURIComponent(currentUrl);
        }

    });







    // 기본 실행해야 하는 것들
    initializeEventListeners()
    rewards(campaignId);
    reviews(campaignId);
    reLoadBoard();
    reBoard();
});

// 기타 (탭, 리포트 등)
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
    const boardBox = document.querySelector(".main-board");

    campaignTab.addEventListener("click", () => {
        removeCheckClass();
        campaignTab.classList.add("check");
        contentBox.classList.remove("hidden");
        boardBox.classList.add("hidden");
    });

    boardTab.addEventListener("click", () => {
        removeCheckClass();
        boardTab.classList.add("check");
        contentBox.classList.add("hidden");
        boardBox.classList.remove("hidden");
    });

    replyTab.addEventListener("click", () => {
        removeCheckClass();
        replyTab.classList.add("check");
        contentBox.classList.add("hidden");
        boardBox.classList.add("hidden");
    });
});
