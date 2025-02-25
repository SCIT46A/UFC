// 1단계 - 모임 이름 입력 검사 (버튼 활성화)
/*
const nameInput = document.querySelector("input.name-form-input");
const nameButton = document.querySelector("button.name-form-button");
nameInput.addEventListener("keyup", (e) => {
    if (nameInput.value) {
        nameButton.classList.remove("disabled");
        return;
    }
    if (!nameButton.classList.contains("disabled")) {
        nameButton.classList.add("disabled");
    }
});

// 다음 버튼 클릭 시 내용 변경(2단계로 이동)

const firstContent = document.querySelector(".name-form-wrap");
const secondContent = document.querySelector(".desc-form-wrap");
nameButton.addEventListener("click", () => {
    firstContent.style.display = "none";
    secondContent.style.display = "block";
});

// 2단계
// 이전 단계 버튼 클릭 시 다시 1단계로

const prevButton = document.querySelector(".desc-form-prev-button");
prevButton.addEventListener("click", () => {
    firstContent.style.display = "block";
    secondContent.style.display = "none";
});

// 소개글 입력 검사
const descButton = document.querySelector(".desc-form-button");

// 키가 입력될 때 마다 확인
document.addEventListener("input", () => {
    // 입력 필드의 값을 매번 가져옴
    let sellerNumber = document.querySelector("#seller_regist_number").value;
    let sellerName = document.querySelector("#seller_regist_name").value;
    let sellerPerson = document.querySelector("#seller_regist_person").value;
    let sellerLocation = document.querySelector("#seller_regist_location").value;

    if (sellerNumber && sellerName && sellerPerson && sellerLocation) {
        // 버튼 활성화
        descButton.classList.remove("disabled");
        descButton.removeAttribute("disabled");
    } else {
        // 버튼 비활성화
        descButton.classList.add("disabled");
        descButton.setAttribute("disabled", "true"); // setAttribute로 수정
    }
});
/*
descInput.addEventListener("keyup", () => {
    if (descInput.value) {
        descButton.classList.remove("disabled");
        return;
    }
    if (!descButton.classList.contains("disabled")) {
        descButton.classList.add("disabled");
    }
});
*/

// 다음 버튼 클릭 시 내용 변경(3단계로 이동)
/*
const thirdContent = document.querySelector(".img-form-wrap");
descButton.addEventListener("click", () => {
    secondContent.style.display = "none";
    thirdContent.style.display = "block";
});

// 3단계
// 이전 단계 버튼 클릭 시 다시 2단계로

const lastPrevButton = document.querySelector(".img-form-prev-button");
lastPrevButton.addEventListener("click", () => {
    secondContent.style.display = "block";
    thirdContent.style.display = "none";
});
*/





// 개설 완료 및 건너뛰기 버튼은 서버 작업 시 연결.

// 아래는 이미지 첨부 부분입니다.
// 이미지 썸네일을 화면에 표시하는 부분은 서버 담당 시 구현합니다.
// 우선은 그대로 표시하겠습니다.

// "모임 프로필 업로드" 버튼 클릭 시 프로필 사진 input 활성화(파일 업로드)
// 및 파일 용량 체크 (프사, 커버 10MB 제한)
function checkFileSize(obj, size) {
    let check = false;
    let sizeInBytes = obj.files[0].size;
    if (sizeInBytes > size) {
        check = false;
    } else {
        check = true;
    }
    return check;
}

function getFileSizeWithExtension(sizeInBytes) {
    let fileSizeExt = new Array("bytes", "kb", "mb", "gb");
    let i = 0;
    let checkSize = sizeInBytes;
    while (checkSize > 900) {
        checkSize /= 1024;
        i++;
    }
    checkSize = Math.round(checkSize * 100) / 100 + "" + fileSizeExt[i];
    return checkSize;
}

const MAX_SIZE = 10; // 10MB

const openProfile = () => {
    document.getElementById("profile-image").click();
};

const profileInput = document.getElementById("profile-image");
const sizeErrorMsg = document.querySelector(".img-form-profile-size-error");
const profileImage = document.querySelector(".img-form-thumbnail");
const profileBackground = document.querySelector(".img-form-thumbnail-bg");
const profileDeleteButton = document.querySelector(".profile-delete-wrap");
const profileDeleteIconDefault = document.querySelector(".profile-delete");
const profileDeleteIconHover = document.querySelector(".profile-delete-hover");

// 삭제 버튼 hover 이벤트마다 색상 변경부터
profileDeleteButton.addEventListener("mouseover", () => {
    profileDeleteIconDefault.style.display = "none";
    profileDeleteIconHover.style.display = "block";
});

profileDeleteButton.addEventListener("mouseout", () => {
    profileDeleteIconDefault.style.display = "block";
    profileDeleteIconHover.style.display = "none";
});

profileDeleteButton.addEventListener("click", (e) => {
    e.preventDefault();
    e.stopPropagation();
    sizeErrorMsg.style.display = "none";
    profileInput.value = "";
    profileImage.setAttribute(
        "src",
        "https://event-us.kr/Content/neweventus/image/hostcenter/hostcenter_create_upload_01.png"
    );
    profileBackground.style.display = "none";
    // 삭제 버튼 안 보이게 하기
    profileDeleteButton.style.display = "none";
});

profileInput.addEventListener("change", (e) => {
    if (e.target.value) {
        let checkSize = 1024 * 1024 * MAX_SIZE;
        if (!checkFileSize(profileInput, checkSize)) {
            sizeErrorMsg.style.display = "block";
            e.preventDefault();
            return;
        }
        sizeErrorMsg.style.display = "none";
        // 서버 작업은 여기에 fetch로 작성한 후 썸네일을 받아와 화면에 표시합니다.
        // 우선은 올린 이미지를 그대로 표시하겠습니다.
        let reader = new FileReader();
        reader.onload = (e) => {
            profileImage.setAttribute("src", e.target.result);
            profileBackground.style.display = "block";
            // 삭제 버튼 보이게 하기
            profileDeleteButton.style.display = "inline-block";
        };
        reader.readAsDataURL(e.target.files[0]);
    }
});

// "모임 커버 업로드" 버튼 클릭 시 커버 사진 input 활성화(파일 업로드)
const coverUploadInput = document.getElementById("background-image");
const openCover = () => {
    coverUploadInput.click();
};

// 업로드 시 사이즈 체크 및 썸네일 표시
const coverImage = document.querySelector(".cover-thumbnail");
const coverUploadWrap = document.querySelector(".cover-upload-wrap");
const uploadSpanGray = document.querySelector(".upload-span-gray");
const coverBackground = document.querySelector(".cover-thumbnail-container");
const coverDeleteButton = document.querySelector(".cover-delete-wrap");
const coverDeleteIconDefault = document.querySelector(".cover-delete");
const coverDeleteIconHover = document.querySelector(".cover-delete-hover");

// 삭제 버튼 hover 이벤트마다 색상 변경부터
coverDeleteButton.addEventListener("mouseover", () => {
    coverDeleteIconDefault.style.display = "none";
    coverDeleteIconHover.style.display = "block";
});

coverDeleteButton.addEventListener("mouseout", () => {
    coverDeleteIconDefault.style.display = "block";
    coverDeleteIconHover.style.display = "none";
});

coverDeleteButton.addEventListener("click", (e) => {
    e.preventDefault();
    e.stopPropagation();
    sizeErrorMsg.style.display = "none";
    coverImage.setAttribute(
        "src",
        "https://event-us.kr/Content/neweventus/image/hostcenter/hostcenter_create_upload_01.png"
    );
    coverUploadInput.value = "";
    // 글들 다 보이게
    coverUploadWrap.style.display = "block";
    uploadSpanGray.style.display = "block";
    // 배경 지우기
    coverBackground.style.display = "none";
    // 삭제 버튼 안 보이게 하기
    coverDeleteButton.style.display = "none";
});

coverUploadInput.addEventListener("change", (e) => {
    if (e.target.value) {
        let checkSize = 1024 * 1024 * MAX_SIZE;
        if (!checkFileSize(coverUploadInput, checkSize)) {
            sizeErrorMsg.style.display = "block";
            e.preventDefault();
            return;
        }
        sizeErrorMsg.style.display = "none";
        // 서버 작업은 여기에 fetch로 작성한 후 썸네일을 받아와 화면에 표시합니다.
        // 우선은 올린 이미지를 그대로 표시하겠습니다.
        let reader = new FileReader();
        reader.onload = (e) => {
            coverImage.setAttribute("src", e.target.result);
            // 글들 다 지우기
            coverUploadWrap.style.display = "none";
            uploadSpanGray.style.display = "none";
            // 배경 투명한 어두운 색으로 삭제 버튼 잘 보이도록 하기
            coverBackground.style.display = "block";
            // 삭제 버튼 보이게 하기
            coverDeleteButton.style.display = "inline-block";
        };
        reader.readAsDataURL(e.target.files[0]);
    }
});

// 드래그 앤 드롭으로 커버 업로드하기

const dragDropBox = document.querySelector(".cover-thumbnail-wrap");
dragDropBox.addEventListener("dragenter", (e) => {
    e.preventDefault();
});
dragDropBox.addEventListener("dragover", (e) => {
    e.preventDefault();
});
dragDropBox.addEventListener("dragleave", (e) => {
    e.preventDefault();
});
dragDropBox.addEventListener("drop", (e) => {
    e.preventDefault();
    let file = e.dataTransfer;
    if (!checkFileSize(file, 1024 * 1024 * MAX_SIZE)) {
        sizeErrorMsg.style.display = "block";
        return;
    }
    sizeErrorMsg.style.display = "none";
    // 서버 작업은 여기에 fetch로 작성한 후 썸네일을 받아와 화면에 표시합니다.
});


// 창작가 프로필 수정 버튼 클릭 시 수정 -> 실제로 수정하면 그 값이 적용되는 것은 아직 구현 x
/*document.addEventListener("DOMContentLoaded", function() {
const seller_edit = document.querySelector(".img-form-button");
seller_edit.addEventListener("click", function() {
    alert("프로필이 수정되었습니다.")
    window.location.href = "../../templates/creator/creator-campaign.html";
});
});*/

// 이벤트 자동 초기화 (새로고침)
window.onload = function () {
    document.querySelector(".club-detail-name").value = "";
    document.querySelector(".company_name").value = "";
    document.querySelector(".club-detail-introduction").value = "";
};


document.querySelector(".img-form-button").addEventListener("click", function() {
    Swal.fire({
        title: '프로필 수정을 진행하겠습니다.',
        text: '정말로 진행 하시겠습니까?',
        icon: 'warning',
        showCancelButton: true,
        confirmButtonColor: '#3085d6',
        cancelButtonColor: '#d33',
        confirmButtonText: '확인',
        cancelButtonText: '취소',
        reverseButtons: true,
    }).then(result => {
        if (result.isConfirmed) { // 만약 확인 버튼을 누르면
            fetch("/creator/create", { // ✅ 서버로 데이터 전송
                method: "POST",
                headers: { "Content-Type": "application/json" },
                body: JSON.stringify({ message: "프로필 수정 완료" })
            })
            .then(response => {
                if (!response.ok) throw new Error("서버 오류 발생");
                return response.text();
            })
            .then(data => {
                Swal.fire('프로필 수정이 완료되었습니다.', '더욱 완성도가 올라갔어요~!', 'success').then(() => {
                    window.location.href = "../../templates/creator/creator-campaign.html"; // ✅ 성공 후 페이지 이동
                });
            })
            .catch(error => {
                Swal.fire('오류 발생!', '서버에 문제가 있습니다.', 'error');
                console.error("❌ 오류 발생:", error);
            });
        }
    });
});

/*
// 창작가 프로필 수정 취소 클릭 시 창작가 캠페인 사이트로 이동
document.addEventListener("DOMContentLoaded", function() {
    const seller_edit_cancel = document.querySelector(".img-form-prev-button");
        seller_edit_cancel.addEventListener("click", function() {
            let cancel_edit = confirm ("작성된 내용이 사라집니다. 정말로 취소하시겠습니까?");
                if (!cancel_edit) {
                    return;
                }
                window.location.href = "../../templates/creator/creator-campaign.html";
            });
    });*/

// 프로필 수정 값이 DB로 전송되고 creator-campaign페이지에도 출력되게 함
/*
async function updateProfile() {
    const ProfileData = {
        creatorId: document.querySelector(".club-detail-name").value,
        companyName: document.querySelector(".company_name").value,
        intro: document.querySelector(".club-detail-introduction").value
    };

    const response = await fetch("/creator/update", {
        method: "POST",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify(profileData)
    });

    if (response.ok) {

    }
}
*/

// 브라우저에서만 값 저장장
document.addEventListener("DOMContentLoaded", function() {
    const newName = document.querySelector(".club-detail-name");
    const newCompany = document.querySelector(".company_name");
    const newIntro = document.querySelector(".club-detail-introduction");
    const newCover = document.querySelector(".cover-thumbnail-wrap");
    const newProfile = document.querySelector(".img-form-thumbnail-wrap");
    const saveProfile = document.querySelector(".img-form-button");

    // 기존 데이터 불러오기 (수정 시 이전 값 유지)
    if (localStorage.getItem("profileName")) {
        newName.value = localStorage.getItem("profileName");
    }

    if (localStorage.getItem("profileCompany")) {
        newCompany.value = localStorage.getItem("profileCompany");
    }

    if (localStorage.getItem("profileIntro")) {
        newIntro.value = localStorage.getItem("profileIntro");
    }

    // 저장 버튼 클릭 시 데이터 저장
    saveProfile.addEventListener("click", function() {
        localStorage.setItem("profileName", newName.value);
        localStorage.setItem("profileCompany", newCompany.value);
        localStorage.setItem("profileIntro", newIntro.value);

        // 이미지 파일 저장
        const coverFile = newCover.files[0];
        if (coverFile) {
            const reader = new FileReader();
            reader.onload = function(e) {
                localStorage.setItem("coverImage", e.target.result);
            };
            reader.readAsDataURL(coverFile);
        }

        const profileFile = newProfile.files[0];
        if (profileFile) {
            const reader = new FileReader();
            reader.onload = function(e) {
                localStorage.setItem("profileImage", e.target.result);
            };
            reader.readAsDataURL(profileFile);
        }

        alert("프로필이 저장되었습니다.");
        window.location.href = "creator-campaign.html"; // 페이지 이동
    });
});

document.querySelector(".img-form-prev-button").addEventListener("click", function() {
    Swal.fire({
        title: '정말 취소하시겠습니까?',
        text: '지금까지 작성한 내용이 사라질 수 있습니다.',
        icon: 'warning',
        showCancelButton: true,
        confirmButtonColor: '#d33',  // 취소 버튼은 빨간색 느낌으로
        cancelButtonColor: '#3085d6',
        confirmButtonText: '네, 취소할게요',
        cancelButtonText: '아니요, 계속 작성할래요',
        reverseButtons: true
    }).then((result) => {
        if (result.isConfirmed) {
            // ✅ 취소 확정 시 실행할 코드
            Swal.fire({
                title: '취소되었습니다.',
                text: '작성이 취소되었습니다.',
                icon: 'info',
                timer: 2000,  // 2초 후 자동 닫힘
                showConfirmButton: false
            }).then(() => {
                window.location.href = "/ufc/src/main/resources/templates/creator/creator-campaign.html"; // ✅ 취소 후 홈으로 이동 (경로 변경 가능)
            });
        } else {
            Swal.fire('계속 작성해주세요!', '취소되지 않았습니다.', 'info');
        }
    });
});

// 새로고침할 때 입력값 초기화
window.onload = function () {
    sessionStorage.clear(); // 세션 스토리지 데이터 삭제 (브라우저 탭 닫으면 자동 삭제됨)
    localStorage.clear();
};