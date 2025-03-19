$(document).ready(function () {
    const productId = $("#product-id").val();
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
            url: `/api/product/${productId}`,
            method: "GET",
            success: function (response) {
                // 에디터를 초기화합니다.
                initSummernote('#detail-summernote-editor');
                if (response && response.content) {
                    // 제품에 content 값이 존재하면 해당 값을 로드합니다.
                    $(".content-box-in").html(response.content);
                    // 수정 모드 UI 표시 (예: "수정하기" 버튼 노출)
                    $(".main-board-add-btn-edit").removeClass("hidden");
                    $(".main-board-add-btn-edit-save").addClass("hidden");
                    $(".summer-btn").addClass("hidden")
                } else{
                    $(".summer-btn-edit").addClass("hidden")
                }


            },
            error: function () {
                console.error("게시글 불러오기 실패");
            }
        });
    }


    $(document).on("click", ".summer-btn", function () {
        const content = $(".presentation-size").summernote("code");
        const url = `/api/product/add/${productId}`; // 신규 작성 엔드포인트
        fetch(url, {
            method: "POST",
            headers: { "Content-Type": "application/json" },
            body: JSON.stringify({ content: content}),
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
    // $(document).on("click", ".main-board-add-btn-save", function () {
    //     const content = $(".presentation-size").summernote("code");
    //     const title = $(".main-board-add-btn-input").val() || "Title";
    //     if (!title || title.trim() === "") {
    //         alert("제목을 입력해주세요.");
    //         return;
    //     }
    //     const url = `/api/${campaignId}/board`;
    //     fetch(url, {
    //         method: "POST",
    //         headers: { "Content-Type": "application/json" },
    //         body: JSON.stringify({ content: content, title: title }),
    //         credentials: "include"
    //     })
    //         .then(response => {
    //             if (!response.ok) throw new Error("저장 실패");
    //             return response.json();
    //         })
    //         .then(result => {
    //             console.log("등록 성공:", result);
    //             reLoadBoard();
    //             resetBoardUI();
    //         })
    //         .catch(error => {
    //             console.error("등록 에러:", error);
    //         });
    // });

    /* ============ 메인 및 상세 게시글 수정 모드 진입 (수정하기 버튼) ============ */
    // $(document).on("click", ".main-board-add-btn-edit", function () {
    //     let boardId = $("#board-id").val();
    //     if (!boardId) {
    //         console.error("게시글 ID가 없습니다.");
    //         return;
    //     }
    //     $.ajax({
    //         url: `/api/board/${boardId}`,
    //         method: "GET",
    //         success: function (response) {
    //             if (response) {
    //                 let isFixed = (boardId == fixedMainBoardId);
    //                 if (isFixed) {
    //                     $(".content-box-in").html('<div id="edit-container"><input type="text" id="detail-title-input" value="' + response.title + '" style="width:100%; margin-bottom:10px;" /><textarea id="detail-summernote-editor"></textarea></div>');
    //                 } else {
    //                     $(".content-box-in-target").html('<div id="edit-container"><input type="text" id="detail-title-input" value="' + response.title + '" style="width:100%; margin-bottom:10px;" /><textarea id="detail-summernote-editor"></textarea></div>');
    //                 }
    //                 initSummernote('#detail-summernote-editor');
    //                 $('#detail-summernote-editor').summernote('code', response.content);
    //                 $(".main-board-add-btn-edit").addClass("hidden");
    //                 $(".main-board-add-btn-edit-save").removeClass("hidden");
    //             }
    //         },
    //         error: function () {
    //             console.error("게시글 불러오기 실패");
    //         }
    //     });
    // });

    // 관리자검토전 수정
    $(document).on("click", ".summer-btn-edit", function () {
        $.ajax({
            url: `/api/product/${productId}`,
            method: "GET",
            success: function (response) {
                $(".content-box-in").html(`
                    <p style="color: gray">
                      ※ 게시글을 작성하셔야 관리자에게 캠페인을 승인받을 수 있습니다.
                    </p>
                    <div id="summernote-container">
                        <textarea id="summernote-editor"></textarea>
                    </div>
                `);
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
                $('#summernote-editor').summernote('code', response.content);
                $(".summer-btn-edit").addClass("hidden");
                $(".summer-btn-edit-save").removeClass("hidden");

            },
            error: function (err) {
                console.error(err);
            }
        });
    });

    /* ============ 수정 적용하기 (수정 저장) ============ */
    // $(document).on("click", ".main-board-add-btn-edit-save", function () {
    //     const boardId = $("#board-id").val();
    //     const updatedContent = $("#detail-summernote-editor").summernote("code");
    //     const updatedTitle = $("#detail-title-input").val();
    //     if (!boardId) {
    //         console.error("게시글 ID가 없습니다.");
    //         return;
    //     }
    //     const url = `/api/${campaignId}/board/${boardId}`;
    //     fetch(url, {
    //         method: "POST",
    //         headers: { "Content-Type": "application/json" },
    //         body: JSON.stringify({ content: updatedContent, title: updatedTitle }),
    //         credentials: "include"
    //     })
    //         .then(response => {
    //             if (!response.ok) throw new Error("수정 적용 실패");
    //             return response.json();
    //         })
    //         .then(result => {
    //             console.log("수정 적용 성공:", result);
    //             if (boardId == fixedMainBoardId) {
    //                 $(".content-box-in").html(updatedContent);
    //             } else {
    //                 $(".content-box-in-target").html(updatedContent);
    //             }
    //             $(".main-board-pe-in-title").html(updatedTitle);
    //             $(".main-board-add-btn-edit-save").addClass("hidden");
    //             $(".main-board-add-btn-edit").removeClass("hidden");
    //             reLoadBoard();
    //         })
    //         .catch(error => {
    //             console.error("수정 적용 에러:", error);
    //         });
    // });

    // 관리자 승인전 수정상태 저장
    $(document).on("click", ".summer-btn-edit-save", function () {
        const content = $('#summernote-editor').summernote("code");

        const url = `/api/product/update/${productId}`;
        fetch(url, {
            method: "POST",
            headers: { "Content-Type": "application/json" },
            body: JSON.stringify({ content: content }),
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
                $(".summer-btn-edit-save").addClass("hidden")
                reLoadBoard();
            })
            .catch(error => {
                console.error("수정 저장 에러:", error);
            });
    });



    // /* ============ 게시글 목록(리스트) 클릭 시 상세보기로 전환 ============ */
    // function reBoard() {
    //     const boardItems = document.querySelectorAll(".main-board-pe");
    //     const boardTitle = document.querySelector(".main-board-top-table");
    //     const boardInnerBox = document.querySelector(".main-board-back-div");
    //     const boardAllBox = document.querySelector(".main-board-total-pe");
    //     const boardInnerContent = document.querySelector(".content-board-box");
    //     const boardBackBtn = document.querySelector(".main-board-back");
    //
    //     boardItems.forEach((item) => {
    //         item.addEventListener("click", function () {
    //             const boardId = $(this).data("board-id");
    //             if (!boardId) return;
    //             $.ajax({
    //                 url: `/api/board/${boardId}`,
    //                 method: "GET",
    //                 success: function (response) {
    //                     if (response) {
    //                         $(".content-box-in-target").html(response.content);
    //                         $(".main-board-pe-in-title").html(response.title);
    //                         $(".main-board-pe-in-date").html(response.date);
    //                         $(".content-board-box").removeClass("hidden");
    //                         if ($("#board-id").length) {
    //                             $("#board-id").val(boardId);
    //                         } else {
    //                             $("#campaign-id").after(`<input type="hidden" id="board-id" value="${boardId}">`);
    //                         }
    //                         $(".main-board-add-btn-save").addClass("hidden");
    //                         $(".main-board-add-btn-edit").removeClass("hidden");
    //                         $(".main-board-add-btn-edit-save").addClass("hidden");
    //                         $(".main-board-detail-delete-btn").removeClass("hidden");
    //                     }
    //                 },
    //                 error: function () {
    //                     console.error("게시글 불러오기 실패");
    //                 }
    //             });
    //             $(".main-reply").removeClass("hidden");
    //             replay(boardId);
    //             boardTitle.classList.add("hidden");
    //             boardInnerBox.classList.remove("hidden");
    //             boardAllBox.classList.add("hidden");
    //             boardInnerContent.classList.remove("hidden");
    //         });
    //     });
    //
    //     boardBackBtn.addEventListener("click", () => {
    //         boardTitle.classList.remove("hidden");
    //         boardInnerBox.classList.add("hidden");
    //         boardAllBox.classList.remove("hidden");
    //         boardInnerContent.classList.add("hidden");
    //         $(".main-reply").addClass("hidden");
    //     });
    // }
    //
    // /* ============ 신규 작성 화면 전환 (예: "작성하기" 버튼 클릭 시) ============ */
    // $(".main-board-add-btn").on("click", () => {
    //     const boardTitle = document.querySelector(".main-board-top-table");
    //     const boardInnerBox = document.querySelector(".main-board-back-div");
    //     const boardAllBox = document.querySelector(".main-board-total-pe");
    //     const boardInnerContent = document.querySelector(".content-board-box");
    //     $(".content-box-in-target").html(`
    //         <div class="cam-pro-bo-ri-in-content">
    //             <p class="cam-pro-bo-ri-in-content-title"></p>
    //             <div class="cam-pro-bo-ri-in-content-sum presentation-size"></div>
    //         </div>
    //     `);
    //     $(".main-board-pe-in-title").html(`<input placeholder="제목을 입력해주세요" class="main-board-add-btn-input">`);
    //     $(".content-board-box").removeClass("hidden");
    //     initSummernote(".presentation-size");
    //     boardTitle.classList.add("hidden");
    //     boardInnerBox.classList.remove("hidden");
    //     boardAllBox.classList.add("hidden");
    //     $(".main-board-add-btn-save").removeClass("hidden");
    //     $(".main-board-add-btn-edit").addClass("hidden");
    //     $(".main-board-detail-delete-btn").addClass("hidden");
    // });
    //
    // /* ============ UI 초기화 (신규 작성 후 목록 복귀) ============ */
    // function resetBoardUI() {
    //     const boardTitle = document.querySelector(".main-board-top-table");
    //     const boardInnerBox = document.querySelector(".main-board-back-div");
    //     const boardAllBox = document.querySelector(".main-board-total-pe");
    //     const boardInnerContent = document.querySelector(".content-board-box");
    //     const boardBtnSave = document.querySelector(".main-board-add-btn-save");
    //     boardBtnSave.classList.add("hidden");
    //     boardTitle.classList.remove("hidden");
    //     boardInnerBox.classList.add("hidden");
    //     boardAllBox.classList.remove("hidden");
    //     boardInnerContent.classList.add("hidden");
    // }
    //
    // // 댓글 관련 js
    // function replay(boardId) {
    //     $.ajax({
    //         url: `/api/replys/${boardId}`,
    //         method: "GET",
    //         success: function (response) {
    //             $("#reply-border-target").val(boardId);
    //             $("#reply-data-target").html(`<input type="text" class="main-reply-input-box" placeholder="댓글을 입력해주세요">`);
    //             $(".main-reply-total strong").html(response.length);
    //             if (response.length === 0) {
    //                 $("#reply-data-target").html(`<input type="text" class="main-reply-input-box" placeholder="댓글을 입력해주세요"><div>댓글이 없습니다.</div>`);
    //                 return;
    //             }
    //             response.forEach((data) => {
    //                 let replyhtml = `
    //                 <div class="main-reply-title">
    //                     <div class="main-reply-title-top">
    //                         <div class="main-reply-title-top-pro">
    //                             <img src="${data.replyedBy.photo && data.replyedBy.photo.imageId
    //                     ? `/api/image/${data.replyedBy.photo.imageId}`
    //                     : 'https://assets.tumblbug.com/profile/default_avatar.png'}"
    //                                  alt="프로필 이미지" class="target-img" />
    //                             <div class="main-reply-title-top-pro-name">
    //                                 <div>
    //                                     <div>${data.replyedBy.userName}</div>
    //                                 </div>
    //                             </div>
    //                         </div>
    //                         <div class="main-reply-tag report-menu">
    //                             <div class="main-reply-tag-box">
    //                                 <div class="main-reply-tag-box-btn">
    //                                     <svg viewBox="0 0 48 48">
    //                                         <path fill-rule="evenodd" clip-rule="evenodd" d="M6.4 19C8.83 19 10.8 20.97 10.8 23.4C10.8 25.83 8.83 27.8 6.4 27.8C3.97 27.8 2 25.83 2 23.4C2 20.97 3.97 19 6.4 19ZM24.0001 19C26.4301 19 28.4001 20.97 28.4001 23.4C28.4001 25.83 26.4301 27.8 24.0001 27.8C21.5701 27.8 19.6001 25.83 19.6001 23.4C19.6001 20.97 21.5701 19 24.0001 19ZM45.9997 23.4C45.9997 20.97 44.0307 19 41.5997 19C39.1697 19 37.2007 20.97 37.2007 23.4C37.2007 25.83 39.1697 27.8 41.5997 27.8C44.0307 27.8 45.9997 25.83 45.9997 23.4Z"></path>
    //                                     </svg>
    //                                 </div>
    //                             </div>
    //                         </div>
    //                     </div>
    //                     <div style="height: 20px"></div>
    //                     <div class="main-reply-content">
    //                         ${data.content}
    //                     </div>
    //                 </div>
    //                 `;
    //                 $("#reply-data-target").append(replyhtml);
    //             });
    //             $("#reply-data-target").find("img.target-img").each(function() {
    //                 const $img = $(this);
    //                 const endpoint = $img.attr("src");
    //                 $.ajax({
    //                     url: endpoint,
    //                     method: "GET",
    //                     success: function(resultUrl) {
    //                         if (resultUrl) {
    //                             $img.attr("src", resultUrl);
    //                         }
    //                     },
    //                     error: function(err) {
    //                         console.error("이미지 URL 요청 오류:", err);
    //                     }
    //                 });
    //             });
    //         },
    //         error: function () {
    //             console.error("댓글 불러오기 실패");
    //         }
    //     });
    // }
    //
    // $(document).on('keypress', '.main-reply-input-box', function(e) {
    //     if (e.which === 13) {
    //         e.preventDefault();
    //         const replyContent = $(this).val().trim();
    //         if (!replyContent) return;
    //         const boardId = $("#reply-border-target").val();
    //         $.ajax({
    //             url: '/api/replys/add',
    //             method: 'POST',
    //             contentType: 'application/json',
    //             data: JSON.stringify({
    //                 boardId: boardId,
    //                 content: replyContent
    //             }),
    //             success: function(response) {
    //                 $('.main-reply-input-box').val('');
    //                 replay(boardId);
    //             },
    //             error: function(err) {
    //                 if (err.status === 401) {
    //                     alert("로그인을 해주세요");
    //                     // 현재 페이지 URL을 쿼리 파라미터로 전달
    //                     window.location.href = '/user/login?redirectUrl=' + encodeURIComponent(window.location.href);
    //                 } else {
    //                     console.error("댓글 작성 실패:", err);
    //                 }
    //             }
    //         });
    //     }
    // });
    //
    //
    //
    // function reviews(campaignId) {
    //     $.ajax({
    //         url: `/api/review/${campaignId}`,
    //         method: "GET",
    //         success: function (response) {
    //             $(".main-review-total strong").html(response.length);
    //             if (response.length === 0) {
    //                 $("#review-data-target").html(`<div>리뷰가 없습니다.</div>`);
    //                 return;
    //             }
    //             response.forEach((data) => {
    //                 let replyhtml = `
    //                     <div class="main-review-title">
    //                         <div class="main-reply-title-top">
    //                             <div class="main-reply-title-top-pro">
    //                                 <img src="${data.reviewedBy.photo && data.reviewedBy.photo.imageId
    //                     ? `/api/image/${data.reviewedBy.photo.imageId}`
    //                     : 'https://assets.tumblbug.com/profile/default_avatar.png'}"
    //                                      alt="프로필 이미지" class="target-review-img" />
    //                                 <div class="main-reply-title-top-pro-name">
    //                                     <div>
    //                                         <div>${data.reviewedBy.userName}</div>
    //                                     </div>
    //                                 </div>
    //                                 <div class="main-reply-title-top-pro-count">
    //                                     <div>${data.rated}</div>
    //                                 </div>
    //                             </div>
    //                             <div class="main-reply-tag report-menu">
    //                                 <div class="main-reply-tag-box">
    //                                     <div class="main-reply-tag-box-btn">
    //                                         <svg viewBox="0 0 48 48">
    //                                             <path fill-rule="evenodd" clip-rule="evenodd" d="M6.4 19C8.83 19 10.8 20.97 10.8 23.4C10.8 25.83 8.83 27.8 6.4 27.8C3.97 27.8 2 25.83 2 23.4C2 20.97 3.97 19 6.4 19ZM24.0001 19C26.4301 19 28.4001 20.97 28.4001 23.4C28.4001 25.83 26.4301 27.8 24.0001 27.8C21.5701 27.8 19.6001 25.83 19.6001 23.4C19.6001 20.97 21.5701 19 24.0001 19ZM45.9997 23.4C45.9997 20.97 44.0307 19 41.5997 19C39.1697 19 37.2007 20.97 37.2007 23.4C37.2007 25.83 39.1697 27.8 41.5997 27.8C44.0307 27.8 45.9997 25.83 45.9997 23.4Z"></path>
    //                                         </svg>
    //                                     </div>
    //                                 </div>
    //                             </div>
    //                         </div>
    //                         <div style="height: 20px"></div>
    //                         <div class="main-reply-content">
    //                             <div>${data.content}</div>
    //                         </div>
    //                     </div>
    //                 `;
    //                 $("#review-data-target").append(replyhtml);
    //             });
    //             $("#review-data-target").find("img.target-review-img").each(function() {
    //                 const $img = $(this);
    //                 const endpoint = $img.attr("src");
    //                 $.ajax({
    //                     url: endpoint,
    //                     method: "GET",
    //                     success: function(resultUrl) {
    //                         if (resultUrl) {
    //                             $img.attr("src", resultUrl);
    //                         }
    //                     },
    //                     error: function(err) {
    //                         console.error("이미지 URL 요청 오류:", err);
    //                     }
    //                 });
    //             });
    //         },
    //         error: function () {
    //             console.error("댓글 불러오기 실패");
    //         }
    //     });
    // }
    //
    // // 별점 관련 코드
    // document.querySelectorAll('.star').forEach(function(star) {
    //     let isLocked = false;
    //     let savedValue = null;
    //     star.addEventListener('mousemove', function(e) {
    //         if (isLocked) return;
    //         const rect = this.getBoundingClientRect();
    //         let percent = (e.clientX - rect.left) / rect.width;
    //         percent = Math.round(percent * 20) / 20;
    //         this.querySelector('em').style.width = (percent * 100) + '%';
    //     });
    //     star.addEventListener('mouseleave', function() {
    //         if (isLocked) return;
    //         this.querySelector('em').style.width = '0%';
    //     });
    //     star.addEventListener('click', function(e) {
    //         const rect = this.getBoundingClientRect();
    //         let percent = (e.clientX - rect.left) / rect.width;
    //         percent = Math.round(percent * 10) / 10;
    //         if (!isLocked) {
    //             this.querySelector('em').style.width = (percent * 100) + '%';
    //             savedValue = percent;
    //             isLocked = true;
    //             window.starRatingValue = savedValue;
    //             this.classList.add('locked');
    //             console.log("Saved value:", savedValue);
    //         } else {
    //             isLocked = false;
    //             savedValue = null;
    //             window.starRatingValue = null;
    //             this.querySelector('em').style.width = '0%';
    //             this.classList.remove('locked');
    //             console.log("Reset value");
    //         }
    //     });
    // });
    //
    // // 리뷰 등록 관련 코드
    // $(document).on('keypress', '.main-review-input-box', function(e) {
    //     if (e.which === 13) {
    //         e.preventDefault();
    //         const reviewContent = $(this).val().trim();
    //         if (!reviewContent) return;
    //         if (!window.starRatingValue) {
    //             alert("리뷰를 등록하려면 먼저 별점을 선택해주세요!");
    //             return;
    //         }
    //         $.ajax({
    //             url: '/api/review/add',
    //             method: 'POST',
    //             contentType: 'application/json',
    //             data: JSON.stringify({
    //                 campaignId: campaignId,
    //                 reviewContent: reviewContent,
    //                 rating: window.starRatingValue
    //             }),
    //             success: function(response) {
    //                 $("#review-data-target").html("");
    //                 reviews(campaignId);
    //             },
    //             error: function(err) {
    //                 if (err.status === 409) {
    //                     alert("이미 별점을 주셨습니다");
    //                 } else if (err.status === 401) {
    //                     alert("로그인을 해주세요");
    //                     window.location.href = '/user/login?redirectUrl=' + encodeURIComponent(window.location.href);
    //                 } else {
    //                     console.error("리뷰 등록 실패:", err);
    //                 }
    //             }
    //         });
    //     }
    // });



// --- 개수 증가 버튼 (최대 available까지만 증가; 총합은 선택수 × 요구수량)
    // --- 개수 증가 버튼
    $(document).on("click", ".add-reward", function () {
        let $container = $(this).closest(".reward-choice-my-pe");
        let available = parseInt($container.data("available"));
        let price = parseFloat($container.data("price")); // 상품 기본 가격
        let $countInput = $container.find(".reward-choice-count");
        let count = parseInt($countInput.val());
        console.log("add 버튼 - price:", price, "count:", count, "available:", available);
        if (count < available) {
            count++;
            $countInput.val(count);
            // 선택한 개수 x 가격 계산
            let total = count * price;
            console.log("업데이트할 total:", total);
            $container.find(".reward-choice-quantity").text(total);
        }
    });


    $(document).on("click", ".remove-reward", function () {
        console.log("remove-reward 버튼 클릭");
        let $container = $(this).closest(".reward-choice-my-pe");
        let price = parseFloat($container.data("price")); // 물품 기본 가격
        let $countInput = $container.find(".reward-choice-count");
        let count = parseInt($countInput.val());
        if (count > 1) {
            count--;
            $countInput.val(count);
            // 선택된 수량 x 기본 가격 계산하여 결과 표시
            $container.find(".reward-choice-quantity").text(count * price);
        }
    });





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

        var itemType = "product";
        var currentState = $btn.hasClass("liked");
        // 전역 변수 campaignId를 그대로 사용합니다.
        // 또는 새 변수에 할당:
        var currentCampaignId = productId;

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
        var stock = $(".reward-choice-count").val();
        var productId = $("#product-id").val();



        // hidden input에서 loginUserId 값을 읽어 로그인 상태 확인
        var loginUserId = $("#loginUserId").val();
        if (!loginUserId) {
            alert("로그인을 해주세요");
            var currentUrl = window.location.href;
            window.location.href = '/user/login?redirectUrl=' + encodeURIComponent(currentUrl);
            return;
        }

        // 로그인 상태라면 /campaign/pay 페이지로 이동하며 donationDetails 쿼리 파라미터 전달
        window.location.href = "/product/pay?stock=" + encodeURIComponent(stock) + "&productId=" + encodeURIComponent(productId);
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
    // reviews(campaignId);
    reLoadBoard();
    // reBoard();
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
