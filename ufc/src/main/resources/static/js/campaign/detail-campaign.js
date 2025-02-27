$(document).ready(function () {
    const campaignId = $("#campaign-id").val();
    const campaignStatus = $("#campaign-status").val();
    let fixedMainBoardId = null; // 고정(0번째) 게시글 ID

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
                }
                // 목록에는 고정 게시글 제외한 추가 게시글만 표시
                if (response) {
                    const otherBoards = response.filter(board => board.cboardId != fixedMainBoardId);
                    $(".board-count-target").html(otherBoards.length);
                    $(".main-board-total-pe").empty();
                    if (otherBoards.length === 0) {
                        $(".main-board-total-pe").append(`<div class="no-posts">작성된 게시글이 없습니다.</div>`);
                    } else {
                        otherBoards.forEach((data) => {
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
            },
            error: function (err) {
                console.error(err);
            }
        });
    }

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

    $(".main-board-back-in").on("click", ()=>{
        console.log("as")
        resetBoardUI();
        console.log("42")
    })

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
    const replyTab = document.querySelector(".main-mi-na-in-box-pe.reply");
    const navItems = [campaignTab, boardTab, replyTab];

    function removeCheckClass() {
        navItems.forEach((nav) => nav.classList.remove("check"));
    }

    const contentBox = document.querySelector(".content-box");
    const replyBox = document.querySelector(".main-reply");
    const boardBox = document.querySelector(".main-board");

    campaignTab.addEventListener("click", () => {
        removeCheckClass();
        campaignTab.classList.add("check");
        contentBox.classList.remove("hidden");
        replyBox.classList.add("hidden");
        boardBox.classList.add("hidden");
    });

    boardTab.addEventListener("click", () => {
        removeCheckClass();
        boardTab.classList.add("check");
        contentBox.classList.add("hidden");
        boardBox.classList.remove("hidden");
        replyBox.classList.add("hidden");
    });

    replyTab.addEventListener("click", () => {
        removeCheckClass();
        replyTab.classList.add("check");
        contentBox.classList.add("hidden");
        boardBox.classList.add("hidden");
        replyBox.classList.remove("hidden");
    });
});
