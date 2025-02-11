document.addEventListener("DOMContentLoaded", function () {
    const content = document.getElementById("content");

    const menuItems = document.querySelectorAll(".main-admin-select a");

    document.addEventListener("DOMContentLoaded", function () {
        loadPage();
    });

    // 현재 URL의 쿼리스트링에서 page 값 가져오기
    function loadPage() {
        const params = new URLSearchParams(window.location.search);
        const page = params.get("page");


        // 모든 메뉴에서 active 클래스 제거 후, 해당 페이지 메뉴에 active 추가
        menuItems.forEach(item => {
            item.classList.remove("active");
            if (item.dataset.page === page) {
                item.classList.add("active");
            }
        });


        // 페이지에 따른 내용 변경
        let pageContent = "<h2>관리 시스템</h2><p>좌측 메뉴에서 항목을 선택하세요.</p>";

        if (page === "campaign-status") {
            pageContent = `
        <h2>캠페인 운영 현황</h2>
        <p>캠페인의 진행 상황을 확인하세요.</p>
        <table border="1">
            <tr>
                <th></th>
                <th>건수</th>
                <th>상세보기</th>
            </tr>
            <tr>
                <td>펀딩 승인 신청 건수</td>
                <td>0건</td>
                <td><button>상세보기</button></td>
            </tr>
            <tr>
                <td>펀딩 중인 캠페인</td>
                <td>0건</td>
                <td><button>상세보기</button></td>
            </tr>
            <tr>
                <td>펀딩 완료된 캠페인</td>
                <td>0건</td>
                <td><button>상세보기</button></td>
            </tr>
            <tr>
                <td>리워드 배송 완료된 캠페인</td>
                <td>0건</td>
                <td><button>상세보기</button></td>
            </tr>
        </table>
    `;
            <!--펀딩 승인 대기 캠페인-->
        } else if (page === "campaign-approval") {
            pageContent = `
        <div class="container">
        <h2>캠페인 승인 관리</h2>
        <p>펀딩 승인 대기 캠페인 목록을 확인하세요.</p>

        <!-- 상태별 필터 버튼 -->
        <div class="filter-buttons">
            <button class="filter-btn" data-status="all">전체</button>
            <button class="filter-btn" data-status="승인대기">승인대기</button>
            <button class="filter-btn" data-status="진행중">진행중</button>
            <button class="filter-btn" data-status="리워드 발송 대기">리워드 발송 대기</button>
            <button class="filter-btn" data-status="완료">완료</button>
            <button class="filter-btn" data-status="실패">실패</button>
        </div>

        <table class="campaign-table">
            <thead>
                <tr>
                    <th>캠페인 명</th>
                    <th>소개</th>
                    <th>상태</th>
                    <th>상세보기</th>
                    <th>승인</th>
                    <th>거절</th>
                </tr>
            </thead>
            <tbody>
                <tr data-status="승인대기">
                    <td>캠페인A</td>
                    <td>청바지를 받아서 가방으로 리사이클링</td>
                    <td class="status pending">승인대기</td>
                    <td><button class="detail-btn">상세보기</button></td>
                    <td><button class="approve-btn">승인</button></td>
                    <td><button class="reject-btn">거절</button></td>
                </tr>
                <tr data-status="진행중">
                    <td>캠페인B</td>
                    <td>병뚜껑을 받아서 키링으로 리사이클링</td>
                    <td class="status ongoing">진행중</td>
                    <td><button class="detail-btn">상세보기</button></td>
                    <td><button class="approve-btn">승인</button></td>
                    <td><button class="reject-btn">거절</button></td>
                </tr>
                <tr data-status="리워드 발송 대기">
                    <td>캠페인C</td>
                    <td>헌수건을 받아서 가방으로 제작</td>
                    <td class="status reward-pending">리워드 발송 대기</td>
                    <td><button class="detail-btn">상세보기</button></td>
                    <td><button class="approve-btn">승인</button></td>
                    <td><button class="reject-btn">거절</button></td>
                </tr>
            </tbody>
        </table>
    </div>
    `;
        } else if (page === "campaign-report") {
            pageContent = `
        <h2>캠페인 신고 관리</h2>
        <p>신고된 캠페인을 관리하세요.</p>
        <table border="1">
            <tr>
                <th>캠페인 명</th>
                <th>소개</th>
                <th>신고 사유</th>
                <th>캠페인 정지</th>
                <th>처리 여부</th>
            </tr>
            <tr>
                <td>캠페인A</td>
                <td>청바지를 받아서 가방으로 리사이클링</td>
                <td>신고 이유!!!!!</td>
                <td><button>정지</button></td>
                <td><button>처리완료</button></td>
            </tr>
            <tr>
                <td>캠페인B</td>
                <td>병뚜껑을 받아서 키링으로 리사이클링</td>
                <td>다 이유가 있어요</td>
                <td><button>정지</button></td>
                <td><button>처리완료</button></td>
            </tr>
        </table>
    `;
        } else if (page === "creator-approval") {
            pageContent = `
        <h2>창작자 승인 대기</h2>
        <p>창작자 승인 요청을 검토하세요.</p>
        <table border="1">
            <tr>
                <th>창작자 이름</th>
                <th>사업자 등록 여부</th>
                <th>상세보기</th>
                <th>승인</th>
                <th>거절</th>
            </tr>
            <tr>
                <td>창작자A</td>
                <td>O</td>
                <td><button>상세보기</button></td>
                <td><button>승인</button></td>
                <td><button>거절</button></td>
            </tr>
            <tr>
                <td>창작자B</td>
                <td>X</td>
                <td><button>상세보기</button></td>
                <td><button>승인</button></td>
                <td><button>거절</button></td>
            </tr>
        </table>
    `;
        } else if (page === "user-report") {
            pageContent = `
        <h2>유저 신고 관리</h2>
        <p>유저 신고 목록을 확인하세요.</p>
        <table border="1">
            <tr>
                <th>신고자</th>
                <th>신고 대상</th>
                <th>사유</th>
                <th>관리</th>
            </tr>
            <tr>
                <td>유저A</td>
                <td>유저B</td>
                <td>욕설</td>
                <td><button>조치</button></td>
            </tr>
        </table>
    `;
        } else if (page === "notice") {
            pageContent = `
        <h2>공지사항 관리</h2>
        <p>공지사항을 등록 및 수정하세요.</p>
        <div class="notice">
            <div class="notice-top">
                <!--전체 글 개수-->
                <div class="notice-count">
                    <p>게시글 갯수 : 0건</p>
                </div>
                <!--검색 창-->
                <div class="notice-search">
                    <select>
                        <option value="">제목</option>
                        <option value="">작성자</option>
                        <option value="">내용</option>
                        <option value="">날짜</option>
                    </select>
                    <input type="text">
                    <button>검색</button>
                </div>
            </div>
            <div class="notice-list">
                <table border="1">
                    <tr>
                        <th>번호</th>
                        <th>제목</th>
                        <th>작성자</th>
                        <th>조회수</th>
                        <th>작성일</th>
                    </tr>
                    <tr>
                        <td>1</td>
                        <td>캠페인 승인 과정</td>
                        <td>관리자A</td>
                        <td>20</td>
                        <td>2025.02.05</td>
                    </tr>
                    <tr>
                        <td>2</td>
                        <td>창작자 전환 신청 방법</td>
                        <td>관리자A</td>
                        <td>50</td>
                        <td>2025.02.15</td>
                    </tr>
                </table>
            </div>
        </div>
    `;
        }

        content.innerHTML = pageContent;
    }

    // URL 변경 감지
    window.onpopstate = function () {
        loadPage();
    };

    // 처음 로딩 시 실행
    loadPage();

    // 좌측 메뉴 클릭 이벤트 추가
    menuItems.forEach(link => {
        link.addEventListener("click", function (event) {
            event.preventDefault(); // 기본 이동 방지
            const page = this.getAttribute("data-page");

            // URL 변경 (history.pushState 사용)
            history.pushState(null, "", "?page=" + page);

            // 페이지 내용 로드
            loadPage();
        });
    });
});

