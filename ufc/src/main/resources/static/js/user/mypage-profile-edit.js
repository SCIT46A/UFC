document.addEventListener("DOMContentLoaded", () => {
    // btb
    const profileImgBtn = document.querySelector(".pro-img-btn");
    const profileNameBtn = document.querySelector(".pro-name-btn");
    const profileShBtn = document.querySelector(".pro-sh-btn");
    const profilePhBtn = document.querySelector(".pro-ph-btn");
    const profileAdBtn = document.querySelector(".pro-ad-btn");
    // close btb
    const profileImgBtnCancle = document.querySelector(".pro-img-btn-cancle");
    const profileNameBtnCancle = document.querySelector(".pro-name-btn-cancle");
    const profileShBtnCancle = document.querySelector(".pro-sh-btn-cancle");
    const profilePhBtnCancle = document.querySelector(".pro-ph-btn-cancle");
    const profileAdBtnCancle = document.querySelector(".pro-ad-btn-cancle");
    // 화면
    const profileImg = document.querySelector(".pro-img");
    const profileName = document.querySelector(".pro-name");
    const profileSh = document.querySelector(".pro-sh");
    const profilePh = document.querySelector(".pro-ph");
    const profileAd = document.querySelector(".pro-ad");

    // 숨겨진곳
    const profileImgClose = document.querySelector(".pro-img-close");
    const profileNameClose = document.querySelector(".pro-name-close");
    const profileShClose = document.querySelector(".pro-sh-close");
    const profilePhClose = document.querySelector(".pro-ph-close");
    const profileAdClose = document.querySelector(".pro-ad-close");

    // 탈퇴버튼
    const profileRunBtn = document.querySelector(".pro-run");

    profileImgBtn.addEventListener("click", () => {
        profileImg.classList.add("hidden");
        profileImgBtn.classList.add("hidden");
        profileImgClose.classList.remove("hidden");
        profileImgBtnCancle.classList.remove("hidden");
    });

    profileImgBtnCancle.addEventListener("click", () => {
        profileImg.classList.remove("hidden");
        profileImgBtn.classList.remove("hidden");
        profileImgClose.classList.add("hidden");
        profileImgBtnCancle.classList.add("hidden");
    });

    profileNameBtn.addEventListener("click", () => {
        profileName.classList.add("hidden");
        profileNameBtn.classList.add("hidden");
        profileNameClose.classList.remove("hidden");
        profileNameBtnCancle.classList.remove("hidden");
    });

    profileNameBtnCancle.addEventListener("click", () => {
        profileName.classList.remove("hidden");
        profileNameBtn.classList.remove("hidden");
        profileNameClose.classList.add("hidden");
        profileNameBtnCancle.classList.add("hidden");
    });

    profileShBtn.addEventListener("click", () => {
        profileSh.classList.add("hidden");
        profileShBtn.classList.add("hidden");
        profileShClose.classList.remove("hidden");
        profileShBtnCancle.classList.remove("hidden");
    });

    profileShBtnCancle.addEventListener("click", () => {
        profileSh.classList.remove("hidden");
        profileShBtn.classList.remove("hidden");
        profileShClose.classList.add("hidden");
        profileShBtnCancle.classList.add("hidden");
    });

    profilePhBtn.addEventListener("click", () => {
        profilePh.classList.add("hidden");
        profilePhBtn.classList.add("hidden");
        profilePhClose.classList.remove("hidden");
        profilePhBtnCancle.classList.remove("hidden");
    });

    profilePhBtnCancle.addEventListener("click", () => {
        profilePh.classList.remove("hidden");
        profilePhBtn.classList.remove("hidden");
        profilePhClose.classList.add("hidden");
        profilePhBtnCancle.classList.add("hidden");
    });

    profileAdBtn.addEventListener("click", () => {
        profileAd.classList.add("hidden");
        profileAdBtn.classList.add("hidden");
        profileAdClose.classList.remove("hidden");
        profileAdBtnCancle.classList.remove("hidden");
    });

    profileAdBtnCancle.addEventListener("click", () => {
        profileAd.classList.remove("hidden");
        profileAdBtn.classList.remove("hidden");
        profileAdClose.classList.add("hidden");
        profileAdBtnCancle.classList.add("hidden");
    });
});
