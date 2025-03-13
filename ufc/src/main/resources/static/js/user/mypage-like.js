document.addEventListener('DOMContentLoaded', () => {
  const selcetModal = document.querySelector('.choice');
  const selcetAddModal = document.querySelector('.choice-add');

  const selectBtn = document.querySelector('.like-top-le-btn');
  const selectBtnAdd = document.querySelector('.like-top-le-btn-add');

  selectBtn.addEventListener('click', (event) => {
    event.stopPropagation(); // 상위 요소로 이벤트 전파 방지
    selcetModal.classList.remove('hidden');
  });

  document.addEventListener('click', (event) => {
    // selectModal과 selectBtn을 제외한 영역을 클릭하면 닫기
    if (!selcetModal.contains(event.target) && event.target !== selectBtn) {
      selcetModal.classList.add('hidden');
    }
  });

  selectBtnAdd.addEventListener('click', (event) => {
    event.stopPropagation(); // 상위 요소로 이벤트 전파 방지
    selcetAddModal.classList.remove('hidden');
  });

  document.addEventListener('click', (event) => {
    // selectModal과 selectBtn을 제외한 영역을 클릭하면 닫기
    if (!selcetAddModal.contains(event.target) && event.target !== selectBtn) {
      selcetAddModal.classList.add('hidden');
    }
  });
});
