document.addEventListener('DOMContentLoaded', function () {
  // profile edit Btn
  const profileEditBtn = document.querySelector('.pro-edit-btn');
  const profileEditBtnCancle = document.querySelector('.pro-edit-btn-save');

  // 원래 보이던 정보
  const profileImg = document.querySelector('.pro-img');
  const profileName = document.querySelector('.pro-name');
  const profileSh = document.querySelector('.pro-sh');
  const profilePh = document.querySelector('.pro-ph');
  const profileAd = document.querySelector('.pro-ad');

  //수정 칸
  const profileImgClose = document.querySelector('.pro-img-close');
  const profileNameClose = document.querySelector('.pro-name-close');
  const profileShClose = document.querySelector('.pro-sh-close');
  const profilePhClose = document.querySelector('.pro-ph-close');
  const profileAdClose = document.querySelector('.pro-ad-close');

  //버튼 이벤트트
  profileEditBtn.addEventListener('click', () => {
    profileEditBtn.classList.add('hidden');
    profileEditBtnCancle.classList.remove('hidden');
    profileImg.classList.add('hidden');
    profileName.classList.add('hidden');
    profileSh.classList.add('hidden');
    profilePh.classList.add('hidden');
    profileAd.classList.add('hidden');
    profileImgClose.classList.remove('hidden');
    profileNameClose.classList.remove('hidden');
    profileShClose.classList.remove('hidden');
    profilePhClose.classList.remove('hidden');
    profileAdClose.classList.remove('hidden');
  });
  // const addressInput = document.querySelector('.address');
  // const addressDetailInput = document.querySelector('.address-detail');
  // const totalAddress = document.querySelector('.total-add');
  // addressDetailInput.addEventListener('input', addressAdd);

  // function addressAdd() {
  //   if ((addressInput.value = null)) {
  //     totalAddress.value = addressDetailInput.value;
  //   } else {
  //     totalAddress.value = addressInput.value + addressDetailInput.value;
  //   }
  // }

  profileEditBtnCancle.addEventListener('click', () => {
    profileEditBtn.classList.remove('hidden');
    profileEditBtnCancle.classList.add('hidden');
    profileImg.classList.remove('hidden');
    profileName.classList.remove('hidden');
    profileSh.classList.remove('hidden');
    profilePh.classList.remove('hidden');
    profileAd.classList.remove('hidden');
    profileImgClose.classList.add('hidden');
    profileNameClose.classList.add('hidden');
    profileShClose.classList.add('hidden');
    profilePhClose.classList.add('hidden');
    profileAdClose.classList.add('hidden');
    addressAdd;
  });
});

// 다음 주소 API
function execPostCode() {
  new daum.Postcode({
    oncomplete: function (data) {
      document.querySelector('.address').value = data.address;
      document.querySelector('.address').dispatchEvent(new Event('input')); // 변경 감지
    },
  }).open();
}

const autoHyphen2 = (target) => {
  target.value = target.value
    .replace(/[^0-9]/g, '')
    .replace(/^(\d{0,3})(\d{0,4})(\d{0,4})$/g, '$1-$2-$3')
    .replace(/(\-{1,2})$/g, '');

  // 변경 감지하여 버튼 활성화 체크
  target.dispatchEvent(new Event('input'));
};

window.addEventListener('DOMContentLoaded', () => {
  const addressInput = document.querySelector('.address');
  const addressDetailInput = document.querySelector('.address-detail');
  const totalAddress = document.querySelector('.total-add');

  // 페이지 로드 시 기본 주소값 설정
  totalAddress.value =
    (addressInput.value || '') + (addressDetailInput.value || '');
});

document.querySelector('.pro-edit-btn-save').addEventListener('click', () => {
  const addressInput = document.querySelector('.address');
  const addressDetailInput = document.querySelector('.address-detail');
  const totalAddress = document.querySelector('.total-add');

  totalAddress.value =
    (addressInput.value || '') + '#' + (addressDetailInput.value || '');
});