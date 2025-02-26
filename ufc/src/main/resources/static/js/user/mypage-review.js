document.addEventListener('DOMContentLoaded', () => {
  const reportButtons = document.querySelectorAll('.report-menu'); // 여러 버튼 선택
  const reportMenu = document.querySelector('.ul-add');
  const reportCall = document.querySelector('.edit');
  const reportModal = document.querySelector('.report');
  const reportClose = document.querySelector('.report-close');
  const deleteCall = document.querySelector('.deletebtn');
  const deleteModal = document.querySelector('.delete');
  const deleteClose = document.querySelector('.delete-close');

  // 모든 버튼에 클릭 이벤트 추가
  reportButtons.forEach((button) => {
    button.addEventListener('click', (event) => {
      event.stopPropagation(); // 클릭 이벤트 전파 방지

      // 메뉴가 숨겨져 있으면 표시
      if (reportMenu.classList.contains('hidden')) {
        reportMenu.classList.remove('hidden');
        reportMenu.style.display = 'block';

        // 버튼 바로 아래에 위치하도록 설정
        const rect = button.getBoundingClientRect();
        reportMenu.style.top = `${rect.bottom + window.scrollY}px`; // 버튼 바로 아래
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
  reportCall.addEventListener('click', (event) => {
    event.stopPropagation(); // 이벤트 전파 중지
    reportModal.classList.remove('hidden');
    reportMenu.classList.add('hidden'); // 메뉴 닫기
    reportMenu.style.display = 'none';
  });

  deleteCall.addEventListener('click', (event) => {
    event.stopPropagation(); // 이벤트 전파 중지
    deleteModal.classList.remove('hidden');
    reportMenu.classList.add('hidden'); // 메뉴 닫기
    reportMenu.style.display = 'none';
  });

  // 모달 닫기 버튼에 이벤트 전파 중지 추가
  reportClose.addEventListener('click', (event) => {
    event.stopPropagation();
    reportModal.classList.add('hidden');
  });

  deleteClose.addEventListener('click', (event) => {
    event.stopPropagation();
    deleteModal.classList.add('hidden');
  });

  // 모달 외부 클릭 시 닫기
  document.addEventListener('click', (event) => {
    if (
      !reportModal.contains(event.target) &&
      !reportMenu.contains(event.target)
    ) {
      reportModal.classList.add('hidden');
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
