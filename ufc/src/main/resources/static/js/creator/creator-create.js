// 1단계 - 모임 이름 입력 검사 (버튼 활성화)

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

document.querySelector(".img-form-button").addEventListener("click", function () {
    Swal.fire({
        title: "창작가 개설을 진행하시겠습니까?",
        text: "새로운 창작의 세계로 나아갈 준비가 되셨나요?",
        icon: "warning",
        showCancelButton: true,
        confirmButtonColor: "#3085d6",
        cancelButtonColor: "#d33",
        confirmButtonText: "네, 개설합니다!",
        cancelButtonText: "아니요, 다시 생각해볼게요",
        reverseButtons: true
    }).then(result => {
        let profileImageInput = document.getElementById('profile-image');    // 이미지 입력
        let backImageInput = document.getElementById('background-image');    // 이미지 입력

        const intro = document.getElementById("seller_intro");
        const bRegistNumber = document.getElementById("seller_regist_number");
        const bName = document.getElementById("seller_regist_person");
        const companyName = document.getElementById("seller_regist_name");
        const address = document.getElementById("seller_regist_location");

        // 값 초기화
        // ✅ 입력값이 없으면 기본값 설정
        const creatorData = {
            intro: intro ? intro.value : "소개 없음",
            registNumber: bRegistNumber ? bRegistNumber.value : "000-00-00000",
            bizName: bName ? bName.value : "대표자 없음",
            companyName: companyName ? companyName.value : "회사 없음",
            address: address ? address.value : "주소 없음" // ✅ 주소가 null이면 기본값 설정
        };
        
        // 이미지 파일 가져오기
        Promise.all([
            uploadImage(profileImageInput.files[0]), // 프로필 이미지 업로드
            uploadImage(backImageInput.files[0])      // 배경 이미지 업로드
        ]).then(images => {
            creatorData.profileImg = images[0]; // 프로필 이미지 URL
            creatorData.backImg = images[1];    // 배경 이미지 URL
        
            console.log("🔹이미지 업로드 후 전송할 데이터:", creatorData);  // ✅ 데이터 확인
        
            if (result.isConfirmed) {
                fetch("/creator/create", {
                    method: "POST",
                    headers: { 
                        "Content-Type": "application/json" // JSON 형식으로 설정
                    },
                    body: JSON.stringify(creatorData) // JSON 데이터 전송
                })
                .then(response => {
                    console.log("✅ 서버 응답:", response);
                    Swal.fire("개설 완료!", "창작가 개설 신청이 성공적으로 완료되었습니다.", "success").then(() => {
                        window.location.href = "/";
                    });
                    return response.text();
                })
                // .then(data => {
                //     console.log("✅ 서버 응답:", data);
                //     Swal.fire("개설 완료!", "창작가 개설 신청이 성공적으로 완료되었습니다.", "success").then(() => {
                //         window.location.href = "/";
                //     });
                // })
                .catch(error => {
                    console.error("❌ 오류 발생:", error);
                    Swal.fire("오류 발생!", `서버에서 오류가 발생했습니다: ${error.message}`, "error");
                });
            } else {
                Swal.fire("취소되었습니다.", "언제든 다시 돌아와 주세요!", "info");
            }
        }).catch(error => {
            console.error("❌ 이미지 업로드 오류:", error);
            Swal.fire("오류 발생!", `이미지 업로드 중 오류가 발생했습니다: ${error.message}`, "error");
        });
    });
});

async function uploadImage(imageFile){
    if (!imageFile) {
        alert('이미지가 등록되지 않았습니다.');
        return;
    }else{
        try {
            let formData = new FormData();
            formData.append('file', imageFile);

            const response = await fetch('/api/image/upload', {
                method: 'POST',
                body: formData
            });

            if (!response.ok) {
                throw new Error('이미지 업로드에 실패했습니다.');
            }

            return response.text();
            
        } catch (error) {
            console.error('Error:', error);
            throw error;
        }
        
    }
    
}

// 취소하기 버튼 클릭 시 메인 홈페이지로 이동
document.querySelector(".img-form-skip").addEventListener("click", function() {
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
                window.location.href = "/"; // ✅ 취소 후 홈으로 이동 (경로 변경 가능)
            });
        } else {
            Swal.fire('계속 작성해주세요!', '취소되지 않았습니다.', 'info');
        }
    });
});

function creator_nickname() {
    document.querySelector('.display_name').textContent = document.querySelector('.name-form-input').value;
}

function update_seller_regist_name() {
    document.querySelector('.display_seller_name').textContent = document.getElementById('seller_regist_name').value;
}

function creator_intro() {
    document.querySelector('.display_creator_intro').textContent = document.querySelector('.desc-form-input').value;
}

document.getElementById("seller_regist_number").addEventListener("input", function (e) {
    let value = e.target.value.replace(/\D/g, "");  // 숫자만 남기기
    if (value.length > 10) value - value.slice(0, 10);  // 10자리 제한

    // 형식 맞추기: xxx-xx-xxxxx
    let formattedValue = "";
    if (value.length > 0) formattedValue += value.substring(0, 3);
    if (value.length > 3) formattedValue += "-" + value.substring(3, 5);
    if (value.length > 5) formattedValue += "-" + value.substring(5);

    e.target.value = formattedValue;
});

// 숫자만 입력 가능하도록 키 입력 필터링
document.getElementById("seller_regist_number").addEventListener("keydown", function (e) {
    if (!/[\d]/.test(e.key) && e.key !== "Backspace" && e.key !== "Tab") {
        e.preventDefault();
    }
});