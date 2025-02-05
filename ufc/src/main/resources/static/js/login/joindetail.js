document.addEventListener("DOMContentLoaded", function () {
    const checkboxes = document.querySelectorAll(".essential"); // 필수 체크박스들
    const checkBoxAdd = document.querySelector(".add-more");
    const submitButton = document.querySelector("button[type='submit']");
    const hiddenInput = document.querySelector(
        "input[name='check'][type='hidden']"
    );

    // 주소, 상세 주소, 전화번호 입력 필드
    const addressInput = document.querySelector(".address");
    const addressDetailInput = document.querySelector(".address-detail");
    const phoneInput = document.querySelector(".phone");
    const totalAddress = document.querySelector(".total-add");

    // 에러 메시지 요소
    const errorPhone = document.querySelector("#phone-error");
    const errorAdd = document.querySelector("#add-error");

    checkBoxAdd.addEventListener("change", function () {
        hiddenInput.value = this.checked ? "1" : "0";
    });

    function validateInputs() {
        const isAddressFilled = addressInput.value.trim() !== "";
        const isAddressDetailFilled = addressDetailInput.value.trim() !== "";
        const isPhoneFilled = phoneInput.value.trim() !== "";

        // 주소 입력 확인
        if (!isAddressFilled || !isAddressDetailFilled) {
            errorAdd.classList.remove("hidden"); // 에러 표시
        } else {
            errorAdd.classList.add("hidden"); // 에러 숨김
        }

        // 전화번호 입력 확인
        if (!isPhoneFilled) {
            errorPhone.classList.remove("hidden"); // 에러 표시
        } else {
            errorPhone.classList.add("hidden"); // 에러 숨김
        }

        // 모든 필수 입력 값이 채워졌는지 확인하여 버튼 활성화
        submitButton.disabled = !(
            isAddressFilled &&
            isAddressDetailFilled &&
            isPhoneFilled
        );
        totalAddress.value =
            addressInput.value + "#" + addressDetailInput.value;
        console.log(totalAddress.value);
    }

    // 입력 필드 변경 시 유효성 검사
    addressInput.addEventListener("input", validateInputs);
    addressDetailInput.addEventListener("input", validateInputs);
    phoneInput.addEventListener("input", validateInputs);

    // 제출 버튼 클릭 시 유효성 검사 실행
    submitButton.addEventListener("click", function (event) {
        validateInputs(); // 입력값이 비어있는지 최종 확인

        if (submitButton.disabled) {
            event.preventDefault(); // 제출 방지
        }
    });
});

// 다음 주소 API
function execPostCode() {
    new daum.Postcode({
        oncomplete: function (data) {
            document.querySelector(".address").value = data.address;
            document
                .querySelector(".address")
                .dispatchEvent(new Event("input")); // 변경 감지
        },
    }).open();
}

// 전화번호 자동 하이픈 처리
const autoHyphen2 = (target) => {
    target.value = target.value
        .replace(/[^0-9]/g, "")
        .replace(/^(\d{0,3})(\d{0,4})(\d{0,4})$/g, "$1-$2-$3")
        .replace(/(\-{1,2})$/g, "");

    // 변경 감지하여 버튼 활성화 체크
    target.dispatchEvent(new Event("input"));
};
