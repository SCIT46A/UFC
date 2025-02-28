document.addEventListener('DOMContentLoaded', () => {
  //버튼 3점을 (누르면 두칸이나옴)
  const reportButtons = document.querySelectorAll('.report-menu'); // 여러 버튼 선택
  //버튼 2개짜리
  const reportMenu = document.querySelector('.ul-add');
  //버튼 두개 버튼의 안에있는 메뉴
  const editCall = document.querySelector('.editbtn');
  const deleteCall = document.querySelector('.deletebtn');
  //모달 전체 창
  const editModal = document.querySelector('.edit');
  const deleteModal = document.querySelector('.delete');
  //모달 닫기 버튼
  const editClose = document.querySelector('.edit-close');
  const deleteClose = document.querySelector('.delete-close');
  //모달 닫기 버튼 중 취소버튼
  const deleteBtnClose = document.querySelector('.btn-delete-cancel');
  const EditBtnClose = document.querySelector('.btn-edit-cancel');
  //전체화면 검은색
  const blackCtn = document.querySelector('.black-ctn');

  // 모든 버튼에 클릭 이벤트 추가
  document.body.addEventListener('click', (event) => {
  if(event.target.classList.contains('report-menu'))
  {
    blackCtn.classList.remove('hidden');
    reportMenu.classList.contains('hidden');
    reportMenu.style.display = 'block';
  }
})

  reportButtons.forEach((button) => {
    button.addEventListener('click', (event) => {
      event.stopPropagation(); // 클릭 이벤트 전파 방지

      // 메뉴가 숨겨져 있으면 표시
      if (reportMenu.classList.contains('hidden')) {
        reportMenu.classList.remove('hidden');
        reportMenu.style.display = 'block';

        // 버튼 바로 아래에 위치하도록 설정
        const rect = button.getBoundingClientRect();
        reportMenu.style.top = `${rect.top + window.scrollY}px`; // 버튼 바로 아래
        reportMenu.style.left = `${rect.left + window.scrollX}px`; // 버튼 왼쪽 맞춤
      } else {
        // 메뉴가 표시 중이면 숨기기
        reportMenu.classList.add('hidden');
        reportMenu.style.display = 'none';
      }
    });
  });

  // 메뉴 외부 클릭 시 숨기기
  document.addEventListener('click', (event) => {
    if (!reportMenu.contains(event.target)) {
      reportMenu.classList.add('hidden');
      reportMenu.style.display = 'none';
    }
  });

  // 메뉴 항목 클릭 이벤트 핸들러 수정
  editCall.addEventListener('click', (event) => {
    event.stopPropagation(); // 이벤트 전파 중지
    editModal.classList.remove('hidden');
    editMenu.classList.add('hidden'); // 메뉴 닫기
    blackCtn.classList.remove('hidden');
    editMenu.style.display = 'none';
  });

  deleteCall.addEventListener('click', (event) => {
    event.stopPropagation(); // 이벤트 전파 중지
    // deleteModal.classList.remove('hidden');
    editMenu.classList.add('hidden'); // 메뉴 닫기
    blackCtn.classList.remove('hidden');
    editMenu.style.display = 'none';
  });

  // 모달 닫기 버튼에 이벤트 전파 중지 추가
  editClose.addEventListener('click', (event) => {
    event.stopPropagation();
    editModal.classList.add('hidden');
  });

  deleteClose.addEventListener('click', (event) => {
    event.stopPropagation();
    deleteModal.classList.add('hidden');
  });

  deleteBtnClose.addEventListener('click', (event) => {
    event.stopPropagation();
    deleteModal.classList.add('hidden');
  });
  
  EditBtnClose.addEventListener('click', (event) => {
    event.stopPropagation();
    editModal.classList.add('hidden');
  });

    // 수정 모달 열기
  document.querySelectorAll(".edit-btn-open").forEach((btn) => {
    btn.addEventListener("click", function () {
      let index = this.getAttribute("data-index");
      document.getElementById(`edit-modal-${index}`).classList.remove("hidden");
    });
  });

  // 수정 모달 닫기
  document.querySelectorAll(".edit-close").forEach((btn) => {
    btn.addEventListener("click", function () {
      this.closest(".edit").classList.add("hidden");
    });
  });

  // 삭제 모달 열기
  document.querySelectorAll(".delete-btn-open").forEach((btn) => {
    btn.addEventListener("click", function () {
      let index = this.getAttribute("data-index");
      document.getElementById(`delete-modal-${index}`).classList.remove("hidden");
    });
  });

  // 삭제 모달 닫기
  document.querySelectorAll(".delete-close, .btn-delete-cancel").forEach((btn) => {
    btn.addEventListener("click", function () {
      this.closest(".delete").classList.add("hidden");
    });
  });

  // 모달 외부 클릭 시 닫기
  document.addEventListener('click', (event) => {
    if (
      !editModal.contains(event.target) &&
      !reportMenu.contains(event.target)
    ) {
      editModal.classList.add('hidden');
      reportMenu.classList.add('hidden');
      reportMenu.style.display = 'none';
    }
    if (
      !deleteModal.contains(event.target) &&
      !reportMenu.contains(event.target)
    ) {
      deleteModal.classList.add('hidden');
      reportMenu.classList.add('hidden');
      reportMenu.style.display = 'none';
    }
  });

  //   function timeForToday(datetime) {
  //     const today = new Date();
  //     const date = new Date(LocalDateTime.parse(datetime));

  //     let gap = Math.floor((today.getTime() - date.getTime()) / 1000 / 60);

  //     if (gap < 1) {
  //       return '방금 전';
  //     }

  //     if (gap < 60) {
  //       return `${gap}분 전`;
  //     }

  //     gap = Math.floor(gap / 60);

  //     if (gap < 24) {
  //       return `${gap}시간 전`;
  //     }

  //     gap = Math.floor(gap / 24);

  //     if (gap < 31) {
  //       return `${gap}일 전`;
  //     }

  //     gap = Math.floor(gap / 31);

  //     if (gap < 12) {
  //       return `${gap}개월 전`;
  //     }

  //     gap = Math.floor(gap / 12);

  //     return `${gap}년 전`;
  //   }
});
