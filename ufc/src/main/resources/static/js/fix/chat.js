$(document).ready(function () {

    // ===============================
    // 전역 변수 및 유틸리티 함수
    // ===============================
    let stompClient = null;
    let subscription = null;
    let Chat__lastLoadedId = 0;
    var userId = null;
    var activeChatRoomId = null;      // 현재 활성 채팅방 ID
    var activeChatUserName = null;    // 현재 활성 채팅방 상대방 이름
    var activeChatUserImage = null;   // 현재 활성 채팅방 상대방 이미지

    const chatBtn = $("#chat-toggle");
    const chatRoomBtn = $("#chat-window");
    const chatInnerBtn = $(".chat-inner-view");
    const chatRoomCloseBtn = $(".chat-room-close");
    const chatInnerCloseBtn = $(".top-out-button-box");

    // POST 요청 유틸리티 함수 (기타 전송용)
    function fetchPost(url, data) {
        return fetch(url, {
            method: "POST",
            headers: {
                "Content-Type": "application/json",
                Accept: "application/json",
            },
            body: JSON.stringify(data),
        }).then((response) => response.json());
    }

    // GET 요청 유틸리티 함수
    function fetchGet(url, data) {
        let query = Object.keys(data)
            .map((k) => encodeURIComponent(k) + "=" + encodeURIComponent(data[k]))
            .join("&");
        return fetch(url + "?" + query, {
            method: "GET",
            headers: {
                "Content-Type": "application/json",
                Accept: "application/json",
            },
        }).then((response) => response.json());
    }

    // ===============================
    // 세션에서 본인 정보 가져오기 (필수)
    // ===============================
    $.ajax({
        url: "/api/findme",
        method: "GET",
        async: false,
        success: function (response) {
            // hidden input에서 읽어온 값은 문자열이므로 정수형으로 변환
            userId = parseInt(response, 10);
        },
        error: function (xhr, status, error) {
            console.error("Error:", error);
        }
    });

    // ===============================
    // 메시지 렌더링 함수 (기존 메시지와 새 메시지 동일 형식)
    // ===============================
    function renderMessage(msg, chatUserName, chatImgUrl) {
        let messageHtml = "";
        // msg.senderId가 숫자형일 경우 비교
        console.log(msg)
        if (msg.senderId.userId === userId) {
            // 내 메시지 HTML
            messageHtml = `
            <div class="middle-message-wrap">
                <div class="middle-message-line"></div>
                <div class="client-message-container">
                    <div class="client-message-box">
                        <div class="client-message-section">
                            <div class="client-message-all-warp">
                                <div class="client-message-all-container">
                                    <div class="client-message-left-margin"></div>
                                    <div class="client-message-content-wrap">
                                        <div class="client-message-content-container">
                                            <div id="client-message-text">${msg.content}</div>
                                        </div>
                                    </div>
                                </div>
                            </div>
                        </div>
                    </div>
                </div>
            </div>`;
        }else if (msg.senderId === userId) {
            // 내 메시지 HTML
            messageHtml = `
            <div class="middle-message-wrap">
                <div class="middle-message-line"></div>
                <div class="client-message-container">
                    <div class="client-message-box">
                        <div class="client-message-section">
                            <div class="client-message-all-warp">
                                <div class="client-message-all-container">
                                    <div class="client-message-left-margin"></div>
                                    <div class="client-message-content-wrap">
                                        <div class="client-message-content-container">
                                            <div id="client-message-text">${msg.content}</div>
                                        </div>
                                    </div>
                                </div>
                            </div>
                        </div>
                    </div>
                </div>
            </div>`;
        }
        else {
            // 상대방 메시지 HTML
            messageHtml = `
            <div class="middle-message-wrap">
                <div class="middle-message-line"></div>
                <div class="server-message-container">
                    <div class="server-message-box">
                        <div class="server-message-section">
                            <div class="server-message-name-box">
                                <div class="server-message-name">${chatUserName}</div>
                            </div>
                            <div class="server-message-all-wrap">
                                <div class="server-messageall-container">
                                    <div class="server-message-all-section">
                                        <div class="server-message-icon-section">
                                            <div class="server-message-icon-wrap">
                                                <div class="server-message-icon-box">
                                                    <img class="server-message-icon-img target-img" width="30" height="30" src="${
                chatImgUrl ? `/api/image/${chatImgUrl}` : 'https://assets.tumblbug.com/profile/default_avatar.png'
            }" alt="">
                                                </div>
                                            </div>
                                        </div>
                                        <div class="server-message-content-wrap">
                                            <div class="server-message-content-container">
                                                <div class="server-message-text-wrap">
                                                    <div>
                                                        <div class="server-message-text-container">
                                                            <div id="server-message-text">${msg.content}</div>
                                                        </div>
                                                    </div>
                                                </div>
                                            </div>
                                            <div class="server-message-right-margin"></div>
                                        </div>
                                    </div>
                                </div>
                            </div>
                        </div>
                    </div>
                </div>
            </div>`;
        }
        return messageHtml;
    }

    // ===============================
    // 이미지 업데이트 함수 (새로 추가된 컨테이너 내 이미지 처리)
    // ===============================
    function updateNewImages(container) {
        $(container).find("img.target-img").each(function () {
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
    }

    // ===============================
    // 채팅방 목록 불러오기
    // ===============================
    function chatRoom(){
        $.ajax({
            url: "/api/chatRoom/all",
            method: "GET",
            success: function (response) {
                console.log("채팅방 목록:", response);
                $(".chat-window-inner").empty();
                response.forEach((data) => {
                    let chatRoomhtml = "";
                    if (data.creator !== null) {
                        chatRoomhtml = `
                            <div class="chat-window-inner-pe" data-id="${data.id}" data-image="${data.creator.proImgUrl.imageId}" data-username="${data.creator.bname}">
                                <div class="chat-window-inner-pe-box">
                                    <img src="/api/image/${data.creator.proImgUrl.imageId}" alt="" class="chat-window-inner-pe-box-img target-img">
                                    <div class="chat-window-inner-pe-box-ri">
                                        <div class="chat-window-inner-pe-box-ri-top">${data.creator.bname}</div>
                                        <div class="chat-window-inner-pe-box-ri-bo">${timeForToday_time(data.createdTime)}</div>
                                    </div>
                                </div>
                            </div>`;
                    } else if (data.user1.userId === userId) {
                        chatRoomhtml = `
                            <div class="chat-window-inner-pe" data-id="${data.id}" data-image="${data.user2.photo && data.user2.photo.imageId ? data.user2.photo.imageId : null}" data-username="${data.user2.userName}">
                                <div class="chat-window-inner-pe-box">
                                    <img src="${data.user2.photo && data.user2.photo.imageId ? `/api/image/${data.user2.photo.imageId}` : 'https://assets.tumblbug.com/profile/default_avatar.png'}" alt="" class="chat-window-inner-pe-box-img target-img">
                                    <div class="chat-window-inner-pe-box-ri">
                                        <div class="chat-window-inner-pe-box-ri-top">${data.user2.userName}</div>
                                        <div class="chat-window-inner-pe-box-ri-bo">${timeForToday_time(data.createdTime)}</div>
                                    </div>
                                </div>
                            </div>`;
                    } else if (data.user2.userId === userId) {
                        chatRoomhtml = `
                            <div class="chat-window-inner-pe" data-id="${data.id}" data-image="${data.user1.photo && data.user1.photo.imageId ? data.user1.photo.imageId : null}" data-username="${data.user1.userName}">
                                <div class="chat-window-inner-pe-box">
                                    <img src="${data.user1.photo && data.user1.photo.imageId ? `/api/image/${data.user1.photo.imageId}` : 'https://assets.tumblbug.com/profile/default_avatar.png'}" alt="" class="chat-window-inner-pe-box-img target-img">
                                    <div class="chat-window-inner-pe-box-ri">
                                        <div class="chat-window-inner-pe-box-ri-top">${data.user1.userName}</div>
                                        <div class="chat-window-inner-pe-box-ri-bo">${timeForToday_time(data.createdTime)}</div>
                                    </div>
                                </div>
                            </div>`;
                    }
                    $(".chat-window-inner").append(chatRoomhtml);
                });
                // 이미지 로드 처리 (필요 시)
                $(".chat-window-inner").find("img.target-img").each(function () {
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
            error: function (xhr, status, error) {
                console.error("채팅방 목록 로딩 에러:", error);
            }
        });
    }
    chatRoom();

    // ===============================
    // 웹소켓 연결 및 구독 함수
    // ===============================
    function connectWebsocket(chatRoomId) {
        const socket = new SockJS("/ws");
        stompClient = Stomp.over(socket);
        stompClient.connect({}, function(frame) {
            console.log("WebSocket Connected: " + frame);
            subscription = stompClient.subscribe(`/topic/chat/room/${chatRoomId}`, function(message) {
                console.log("새 메시지 수신:", message.body);
                const receivedMessage = JSON.parse(message.body);
                let messageHtml = "";
                // senderId가 객체 형태로 되어 있으므로 userId 프로퍼티를 비교합니다.
                console.log(receivedMessage)
                if (receivedMessage.senderId.userId === userId) {
                    messageHtml = renderMessage(receivedMessage, activeChatUserName, null);
                } else {
                    messageHtml = renderMessage(receivedMessage, activeChatUserName, activeChatUserImage);
                }
                $(".message-list").append(messageHtml);
                $('.middle-main-message-wrap').scrollTop($('.middle-main-message-wrap')[0].scrollHeight);
                // 새 메시지에 대해서만 이미지 업데이트 실행
                updateNewImages($(".message-list").children().last());
            });

        }, function(error) {
            console.error("WebSocket 연결 실패:", error);
        });
    }

    // ===============================
    // 채팅방 클릭 시: 기존 메시지 로드 및 웹소켓 연결
    // ===============================
    $(document).on('click', '.chat-window-inner-pe', function(){
        const chatRoomId = $(this).data("id");
        const chatImgUrl = $(this).data("image");
        const chatUserName = $(this).data("username");

        // 활성 채팅방 정보 전역 변수에 저장 (메시지 전송 시 사용)
        activeChatRoomId = chatRoomId;
        activeChatUserName = chatUserName;
        activeChatUserImage = chatImgUrl;

        $.ajax({
            url: `/api/chatRoom/${chatRoomId}/messages`,
            method: "GET",
            success: function(response){
                chatInnerBtn.removeClass("hidden");
                chatRoomBtn.addClass("hidden");
                $(".message-list").html("");
                $(".top-information-main-text").html(chatUserName);
                console.log("기존 메시지:", response);
                if(response.length === 0){
                    $(".message-list").html(`<div>아직 메시지가 없습니다.</div>`);
                } else {
                    let allMessagesHtml = "";
                    response.forEach((msg) => {
                        let messageHtml = "";
                        if (msg.senderId.userId === userId) {
                            messageHtml = renderMessage(msg, chatUserName, null);
                        } else {
                            // 전역 변수 activeChatUserImage 사용
                            messageHtml = renderMessage(msg, chatUserName, activeChatUserImage);
                        }
                        allMessagesHtml += messageHtml;
                    });
                    $(".message-list").append(allMessagesHtml);
                    // 렌더링된 메시지 내 이미지 업데이트 호출 추가
                    updateNewImages($(".message-list"));
                }
                $('.middle-main-message-wrap').scrollTop($('.middle-main-message-wrap')[0].scrollHeight);
                // 웹소켓 연결 및 구독
                connectWebsocket(chatRoomId);
            },
            error: function(xhr, status, error){
                console.error("메시지 로딩 에러:", error);
            }
        });
    });

    // ===============================
    // 메시지 전송 함수 (엔터키 혹은 버튼 클릭 시 호출)
    // ===============================
    function sendMessage() {
        var message = $("#message_input").val().trim();
        if(message.length === 0) {
            $("#message_input").focus();
            return;
        }
        if(!activeChatRoomId) {
            console.error("활성 채팅방이 지정되지 않았습니다.");
            return;
        }
        console.log($(".message-list"))
        console.log($(".message-list").innerHTML)
        console.log($(".message-list"));
        console.log($(".message-list").html());

        if ($(".message-list").html() === `<div>아직 메시지가 없습니다.</div>`) {
            $(".message-list").html("")
        }

        var data = {
            chatRoomId: activeChatRoomId,
            senderId: userId,
            content: message
        };
        fetchPost("/chat/writeMessage", data)
            .then(function(response) {
                console.log("메시지 전송 응답:", response);
            })
            .catch(function(error) {
                console.error("메시지 전송 오류:", error);
            });
        $("#message_input").val("").focus();
    }

    // ===============================
    // 메시지 전송 이벤트: 엔터키 처리
    // ===============================
    $("#message_input").keypress(function(event) {
        if(event.which === 13 && !event.shiftKey) {
            event.preventDefault();
            sendMessage();

        }
    });

    // ===============================
    // 메시지 전송 이벤트: 버튼 클릭 처리
    // ===============================
    $(document).on('click', '.bottom-message-send-button', function(){
        sendMessage();

    });

    // ---------------------
    // 캠페인용: 채팅방 생성 및 활성화
    $(".reward-in-se-in-bo-btn-le").on("click", function() {
        const $btn = $(this); // 클릭한 버튼 요소
        const targetUserId = $btn.data("userid");
        const chatUserName = $btn.data("username");
        const chatImgUrl = $btn.data("image");

        if (targetUserId === userId) {
            alert("본인입니다");
            return;
        }

        // 전역 변수 업데이트 (채팅방 클릭 시와 동일하게)
        activeChatUserName = chatUserName;
        activeChatUserImage = chatImgUrl;

        // 채팅방 생성 요청 (GET 방식으로 파라미터 전송)
        $.ajax({
            url: "/api/chatRoom/add",
            method: "GET",
            data: {
                loginUserId: targetUserId,
                userId: userId
            },
            success: function(response) {
                console.log("채팅방 생성 응답:", response);
                // 응답으로 받은 채팅방 ID를 activeChatRoomId에 저장
                activeChatRoomId = response.id;
                // 생성된 채팅방에 대한 메시지 불러오기
                $.ajax({
                    url: `/api/chatRoom/${activeChatRoomId}/messages`,
                    method: "GET",
                    success: function(response) {
                        chatInnerBtn.removeClass("hidden");
                        chatRoomBtn.addClass("hidden");
                        chatBtn.addClass("hidden");
                        if(subscription) {
                            subscription.unsubscribe();
                            subscription = null;
                        }
                        if(stompClient) {
                            stompClient.disconnect(() => {
                                console.log("WebSocket disconnected");
                            });
                            stompClient = null;
                        }
                        $(".message-list").html("");
                        $(".top-information-main-text").html(chatUserName);
                        console.log("기존 메시지:", response);
                        if(response.length === 0) {
                            $(".message-list").html("<div>아직 메시지가 없습니다.</div>");
                        } else {
                            let allMessagesHtml = "";
                            response.forEach((msg) => {
                                let messageHtml = "";
                                if (msg.senderId.userId === userId) {
                                    messageHtml = renderMessage(msg, chatUserName, null);
                                } else {
                                    // 전역 변수 activeChatUserImage 사용
                                    messageHtml = renderMessage(msg, chatUserName, activeChatUserImage);
                                }
                                allMessagesHtml += messageHtml;
                            });
                            $(".message-list").append(allMessagesHtml);
                        }
                        $('.middle-main-message-wrap').scrollTop($('.middle-main-message-wrap')[0].scrollHeight);
                        // 웹소켓 연결 및 구독
                        connectWebsocket(activeChatRoomId);
                        chatRoom();

                    },
                    error: function(xhr, status, error) {
                        console.error("메시지 로딩 에러:", error);
                    }
                });
            },
            error: function(err) {
                console.error("채팅방 생성 AJAX 오류:", err);
            }
        });
    });


    // ===============================
    // 모달창(채팅창) 열기/닫기
    // ===============================
    chatBtn.on("click", () => {
        console.log(userId);
        if (isNaN(userId)) {
            window.location.href = '/user/login?redirectUrl=' + encodeURIComponent(window.location.href);
            return;
        }
        console.log(userId);
        chatRoomBtn.removeClass("hidden");
        chatBtn.addClass("hidden");
        chatRoom();
    });


    chatRoomCloseBtn.on("click", () => {
        chatBtn.removeClass("hidden");
        chatRoomBtn.addClass("hidden");
        chatRoom()
    });

    chatInnerCloseBtn.on("click", () => {
        chatInnerBtn.addClass("hidden");
        chatRoomBtn.removeClass("hidden");
        // 웹소켓 구독 해제 및 연결 종료
        if(subscription) {
            subscription.unsubscribe();
            subscription = null;
        }
        if(stompClient) {
            stompClient.disconnect(() => {
                console.log("WebSocket disconnected");
            });
            stompClient = null;
        }
        chatRoom()
    });

    function timeForToday_time(datetime) {
        const today = new Date();
        const date = new Date(datetime);

        let gap = Math.floor((today.getTime() - date.getTime()) / 1000 / 60);

        if (gap < 1) {
            return "방금 전";
        }

        if (gap < 60) {
            return `${gap}분 전`;
        }

        gap = Math.floor(gap / 60);

        if (gap < 24) {
            return `${gap}시간 전`;
        }

        gap = Math.floor(gap / 24);

        if (gap < 31) {
            return `${gap}일 전`;
        }

        gap = Math.floor(gap / 31);

        if (gap < 12) {
            return `${gap}개월 전`;
        }

        gap = Math.floor(gap / 12);

        return `${gap}년 전`;
    }

});
