document.addEventListener('DOMContentLoaded', () => {
  // 토글 대상 선택 (캠페인/크리에이터 버튼)
  const selectCampaign = document.querySelector('.like-top-campaign');
  const selectCreator = document.querySelector('.like-top-creator');
  const BoxCampaign = document.querySelector('.like-bo-box-in-campaign');
  const BoxCreator = document.querySelector('.like-bo-box-creator');
  const sortBtn = document.querySelector('.like-top-ri-in');

  selectCampaign.addEventListener('click', () => {
    BoxCampaign.classList.remove('hidden');
    BoxCreator.classList.add('hidden');
    sortBtn.classList.remove('hidden');
    selectCreator.classList.remove('current-like');
    selectCampaign.classList.add('current-like');
  });

  selectCreator.addEventListener('click', () => {
    BoxCampaign.classList.add('hidden');
    BoxCreator.classList.remove('hidden');
    sortBtn.classList.add('hidden');
    selectCreator.classList.add('current-like');
    selectCampaign.classList.remove('current-like');

  });

  
   const urlParams = new URLSearchParams(window.location.search);
  if (urlParams.get('tab') === 'creator') {
    // 크리에이터 탭 활성화
    selectCreator.click();
  }

  $(document).on("click", ".main-like-btn", function (event) {
    event.preventDefault();
    event.stopPropagation();

    // data-like-id 값을 읽어옵니다.
    var likeId = $(this).data("like-id");
    if (!likeId) {
      console.error("likeId가 없습니다.");
      return;
    }

    $.ajax({
      url: "/api/like/delete",
      method: "POST",
      data: { likeId: likeId },
      success: function (response) {
        if (response.success) {
          alert(response.message);
          // 삭제 후 UI 업데이트 (예: 해당 좋아요 아이콘 제거)
          $(this).remove();
        } else {
          console.error("삭제 실패:", response.message);
        }
      },
      error: function (err) {
        console.error("좋아요 삭제 AJAX 오류:", err);
      }
    });
  });


  $(document).ready(function () {
    $('.like-top-sorting').on('change', function () {
      var sort = $(this).val();
      window.location.href = '/user/like?sort=' + sort;
    });
  });

  
  
  function daysLeft(endDatetime) {
    const now = new Date();
    const end = new Date(endDatetime);
    const diffMs = end.getTime() - now.getTime();
  
    // 이미 종료되었으면
    if (diffMs < 0) {
      return "마감됨";
    }
  
    // 남은 일수(실수) 계산
    const diffDays = diffMs / (1000 * 60 * 60 * 24);
  
    // 하루 미만이면 오늘 마감
    if (diffDays < 1) {
      return "오늘 마감";
    }
  
    // 남은 일수가 정확히 1일이면
    if (Math.floor(diffDays) === 1) {
      return "하루 남음";
    }
  
    // 그 외에는 정수로 남은 일수를 표시
    return Math.floor(diffDays) + "일 남음";
  }

  // 사용 예시:
  const dateSpans = document.querySelectorAll('.enddate');
  dateSpans.forEach(function (span) {
    const datetime = span.textContent.trim();
    if (datetime) {
      const humanReadable = daysLeft(datetime);
      span.textContent = humanReadable;
    }
  });
});
