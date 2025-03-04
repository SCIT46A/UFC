document.addEventListener('DOMContentLoaded', function () {
  const checkboxes = document.querySelectorAll('.essential'); // 필수 체크박스들
  const checkBoxAdd = document.querySelector('.add-more');
  const submitButton = document.querySelector("button[type='submit']");
  const hiddenInput = document.querySelector(
    "input[name='check'][type='hidden']"
  );

  // 주소, 상세 주소, 전화번호 입력 필드
  const addressInput = document.querySelector('.address');
  const addressDetailInput = document.querySelector('.address-detail');
  const phoneInput = document.querySelector('.phone');
  const totalAddress = document.querySelector('.total-add');

  // 에러 메시지 요소
  const errorPhone = document.querySelector('#phone-error');
  const errorAdd = document.querySelector('#add-error');

  checkBoxAdd.addEventListener("change", function () {
      hiddenInput.value = this.checked ? "1" : "0";
      validateInputs(); // 체크 변경 시 유효성 검사
  });

    function validateInputs() {
        const isAddressFilled = addressInput.value.trim() !== "";
        const isAddressDetailFilled = addressDetailInput.value.trim() !== "";
        const isPhoneFilled = phoneInput.value.trim() !== "";
        const isCheckboxChecked = [...checkboxes].every(
            (checkbox) => checkbox.checked
        ); // 필수 체크박스 체크 여부 확인

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

        // 모든 필수 입력 값이 채워졌는지 확인하여 버튼 활성화 및 스타일 변경
        if (
            isAddressFilled &&
            isAddressDetailFilled &&
            isPhoneFilled &&
            isCheckboxChecked
        ) {
            submitButton.disabled = false;
            submitButton.style.backgroundColor = "#16A34A"; // 배경 초록색
            submitButton.style.cursor = "pointer";

            // 마우스 이벤트 핸들러 추가 전 기존 핸들러 제거
            submitButton.onmouseover = function () {
                submitButton.style.backgroundColor = "#4ade80"; // 호버 시 연한 초록색
            };
            submitButton.onmouseleave = function () {
                submitButton.style.backgroundColor = "#16A34A"; // 원래 색 복구
            };
        } else {
            submitButton.disabled = true;
            submitButton.style.backgroundColor = "#F2F4F5"; // 기본 스타일로 초기화
            submitButton.style.cursor = "default";

            // 버튼이 비활성화되면 호버 효과도 제거
            submitButton.onmouseover = null;
            submitButton.onmouseleave = null;
        }

        // 주소 합치기
        totalAddress.value =
            addressInput.value + "#" + addressDetailInput.value;
    }

    // 입력 필드 및 체크박스 변경 시 유효성 검사 실행
    addressInput.addEventListener("input", validateInputs);
    addressDetailInput.addEventListener("input", validateInputs);
    phoneInput.addEventListener("input", validateInputs);
    checkboxes.forEach((checkbox) =>
        checkbox.addEventListener("change", validateInputs)
    );

    // 모든 필수 입력 값이 채워졌는지 확인하여 버튼 활성화
    submitButton.disabled = !(
      isAddressFilled &&
      isAddressDetailFilled &&
      isPhoneFilled
    );
    totalAddress.value = addressInput.value + '#' + addressDetailInput.value;
    console.log(totalAddress.value);

  // 입력 필드 변경 시 유효성 검사
  addressInput.addEventListener('input', validateInputs);
  addressDetailInput.addEventListener('input', validateInputs);
  phoneInput.addEventListener('input', validateInputs);

  // 제출 버튼 클릭 시 유효성 검사 실행
  submitButton.addEventListener('click', function (event) {
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
      document.querySelector('.address').value = data.address;
      document.querySelector('.address').dispatchEvent(new Event('input')); // 변경 감지
    },
  }).open();
}

// 전화번호 자동 하이픈 처리
const autoHyphen2 = (target) => {
  target.value = target.value
    .replace(/[^0-9]/g, '')
    .replace(/^(\d{0,3})(\d{0,4})(\d{0,4})$/g, '$1-$2-$3')
    .replace(/(\-{1,2})$/g, '');

  // 변경 감지하여 버튼 활성화 체크
  target.dispatchEvent(new Event('input'));
};
