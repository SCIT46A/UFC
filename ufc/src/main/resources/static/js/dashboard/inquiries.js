function initInquiriesManagement() {
    console.log("📩 문의 관리 JS 실행됨");

    function selectInquiry(inquiryId, element) {
        // Remove active class from all items
        document.querySelectorAll(".inquiry-item").forEach(item => {
            item.classList.remove("active");
        });

        // Add active class to selected item
        element.classList.add("active");

        const chatContainer = document.getElementById("chatContainer");

        // Add active class for mobile view
        chatContainer.classList.add("active");

        // Clear empty state
        chatContainer.classList.remove("empty");

        // Update chat container with selected chat
        chatContainer.innerHTML = `
            <div class="chat-header">
                <button class="back-button" onclick="closeChat()">←</button>
                <div class="chat-title">6536****</div>
            </div>
            <div class="chat-messages" id="chatMessages">
                <div class="message">
                    <div class="message-content">유아 용품 리워드 순위 작업 후 쿠팡 무료 테스트도 가능합니다</div>
                    <div class="message-time">오전 11:33</div>
                </div>
                <div class="status-message">6536****님이 채팅방을 나가셨습니다.</div>
            </div>
            <div class="chat-input">
                <div class="chat-actions">
                    <button class="chat-action-btn">😊</button>
                    <button class="chat-action-btn">📎</button>
                </div>
                <textarea placeholder="메시지를 입력하세요..."></textarea>
                <button class="send-btn">전송</button>
            </div>
            <div class="status-message">
                메시지를 전송하시면 진행중 상태로 변경되고 읽음 처리됩니다.
            </div>
        `;
    }

    function closeChat() {
        const chatContainer = document.getElementById("chatContainer");
        chatContainer.classList.remove("active");
        document.querySelectorAll(".inquiry-item").forEach(item => {
            item.classList.remove("active");
        });

        // Reset to empty state
        chatContainer.classList.add("empty");
        chatContainer.innerHTML = "채팅방을 선택해주세요";
    }

    // Handle tab navigation
    document.querySelectorAll(".tab").forEach(tab => {
        tab.addEventListener("click", function () {
            document.querySelectorAll(".tab").forEach(t => t.classList.remove("active"));
            this.classList.add("active");
        });
    });

    // ✅ 이벤트 리스너가 fragment 변경 후에도 유지되도록 설정
    document.querySelectorAll(".inquiry-item").forEach(item => {
        item.addEventListener("click", function () {
            selectInquiry(this.getAttribute("data-id"), this);
        });
    });

    window.selectInquiry = selectInquiry;
    window.closeChat = closeChat;
}

// 🚀 fragment가 변경될 때마다 JS를 다시 실행하도록 설정
document.addEventListener("reapplyEventListeners", initInquiriesManagement);
