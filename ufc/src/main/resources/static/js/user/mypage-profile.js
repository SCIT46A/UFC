document.addEventListener('DOMContentLoaded', () => {
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
  // profileImgBtnCancle.addEventListener("click", () => {
  //     profileEditBtn.classList.remove("hidden");
  //     profileImgBtnCancle.classList.add("hidden");
  // });

  // const profileNameBtn = document.querySelector(".pro-name-btn");
  // const profileShBtn = document.querySelector(".pro-sh-btn");
  // const profilePhBtn = document.querySelector(".pro-ph-btn");
  // const profileAdBtn = document.querySelector(".pro-ad-btn");
  // // close btb
  // const profileNameBtnCancle = document.querySelector(".pro-name-btn-cancle");
  // const profileShBtnCancle = document.querySelector(".pro-sh-btn-cancle");
  // const profilePhBtnCancle = document.querySelector(".pro-ph-btn-cancle");
  // const profileAdBtnCancle = document.querySelector(".pro-ad-btn-cancle");
  // // 화면

  // 숨겨진곳

  // // 탈퇴버튼
  // const profileRunBtn = document.querySelector(".pro-run");

  // profileImgBtn.addEventListener("click", () => {
  //     profileImg.classList.add("hidden");
  //     profileImgBtn.classList.add("hidden");
  //     // profileImgClose.classList.remove("hidden");
  //     // profileImgBtnCancle.classList.remove("hidden");
  // });

  // profileImgBtnCancle.addEventListener("click", () => {
  //     profileImg.classList.remove("hidden");
  //     profileImgBtn.classList.remove("hidden");
  //     profileImgClose.classList.add("hidden");
  //     profileImgBtnCancle.classList.add("hidden");
  // });

  // profileNameBtn.addEventListener("click", () => {
  //     profileName.classList.add("hidden");
  //     profileNameBtn.classList.add("hidden");
  //     profileNameClose.classList.remove("hidden");
  //     profileNameBtnCancle.classList.remove("hidden");
  // });

  // profileNameBtnCancle.addEventListener("click", () => {
  //     profileName.classList.remove("hidden");
  //     profileNameBtn.classList.remove("hidden");
  //     profileNameClose.classList.add("hidden");
  //     profileNameBtnCancle.classList.add("hidden");
  // });

  // profileShBtn.addEventListener("click", () => {
  //     profileSh.classList.add("hidden");
  //     profileShBtn.classList.add("hidden");
  //     profileShClose.classList.remove("hidden");
  //     profileShBtnCancle.classList.remove("hidden");
  // });

  // profileShBtnCancle.addEventListener("click", () => {
  //     profileSh.classList.remove("hidden");
  //     profileShBtn.classList.remove("hidden");
  //     profileShClose.classList.add("hidden");
  //     profileShBtnCancle.classList.add("hidden");
  // });

  // profilePhBtn.addEventListener("click", () => {
  //     profilePh.classList.add("hidden");
  //     profilePhBtn.classList.add("hidden");
  //     profilePhClose.classList.remove("hidden");
  //     profilePhBtnCancle.classList.remove("hidden");
  // });

  // profilePhBtnCancle.addEventListener("click", () => {
  //     profilePh.classList.remove("hidden");
  //     profilePhBtn.classList.remove("hidden");
  //     profilePhClose.classList.add("hidden");
  //     profilePhBtnCancle.classList.add("hidden");
  // });

  // profileAdBtn.addEventListener("click", () => {
  //     profileAd.classList.add("hidden");
  //     profileAdBtn.classList.add("hidden");
  //     profileAdClose.classList.remove("hidden");
  //     profileAdBtnCancle.classList.remove("hidden");
  // });

  // profileAdBtnCancle.addEventListener("click", () => {
  //     profileAd.classList.remove("hidden");
  //     profileAdBtn.classList.remove("hidden");
  //     profileAdClose.classList.add("hidden");
  //     profileAdBtnCancle.classList.add("hidden");
  // });
});
