document.addEventListener('DOMContentLoaded', () => {
  // 토글 대상 선택 (캠페인/크리에이터 버튼)
  const selectCampaign = document.querySelector('.like-top-campaign');
  const selectCreator = document.querySelector('.like-top-creator');
  const BoxCampaign = document.querySelector('.like-bo-box-in-campaign');
  const BoxCreator = document.querySelector('.like-bo-box-creator');

  selectCampaign.addEventListener('click', () => {
    BoxCampaign.classList.remove('hidden');
    BoxCreator.classList.add('hidden');
  });

  selectCreator.addEventListener('click', () => {
    BoxCampaign.classList.add('hidden');
    BoxCreator.classList.remove('hidden');
  });
  

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
            } else {
                console.error("삭제 실패:", response.message);
            }
        },
        error: function (err) {
            console.error("좋아요 삭제 AJAX 오류:", err);
        }
    });
});

});
