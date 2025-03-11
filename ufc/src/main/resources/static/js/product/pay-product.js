$(document).ready(function () {

    function updateFullAddress() {
        // 메인 주소와 상세주소 값을 가져와 결합 (상세주소가 비어있더라도 '#'는 항상 추가)
        var mainAddress = $('.address').val().trim();
        var detailAddress = $('.detail-address').val().trim();
        var fullAddress = mainAddress + '#' + detailAddress;

        // hidden 필드 업데이트
        $('.total-add').val(fullAddress);
        console.log("Updated full address:", fullAddress);
    }



    $(document).ready(function () {
        // 상세주소 입력 변화 시 fullAddress 업데이트
        $('.detail-address').on('keyup change', function(){
            updateFullAddress();
        });
    });

    $(document).ready(function () {
        // 이전에 작성한 checkFields 함수 및 이벤트 바인딩은 그대로 사용

        function checkFields() {
            var valid = true;

            // 텍스트 입력 필드 (inner-box-input-box 클래스)를 검사
            $('.inner-box-input-box').each(function () {
                if ($(this).val().trim() === "") {
                    valid = false;
                }
            });

            // 택배사 선택 필드 검사 (invoice-select-box)
            if ($('.invoice-select-box').val() === "") {
                valid = false;
            }

            // 체크박스 검사 (개인정보 및 후원 유의사항 체크)
            if (!$('#person').is(':checked') || !$('#check-a').is(':checked')) {
                valid = false;
            }

            // 모든 조건이 충족되면 버튼 활성화 및 스타일 변경, 아니라면 다시 비활성화
            if (valid) {
                $('.reward-last-btn').removeAttr('disabled').css({
                    'background': '#16A34A',
                    'border-color': '#16A34A',
                    'cursor': 'pointer'
                });
            } else {
                $('.reward-last-btn').attr('disabled', 'disabled').removeAttr('style');
            }
        }

        // 페이지 로드 시 한 번 검사
        checkFields();

        // 입력 필드, 선택박스, 체크박스 변화 시 검사
        $('.inner-box-input-box, .invoice-select-box').on('keyup change', function () {
            checkFields();
        });
        $('#person, #check-a').on('change', function () {
            checkFields();
        });

        // 기부하기 버튼 클릭 시 JSON으로 POST 보내고, 성공 시 리다이렉트
        $('.reward-last-btn').on('click', function (e) {
            e.preventDefault();

            // 버튼이 활성화된 상태에서만 진행
            if ($(this).attr('disabled')) {
                return;
            }



            $.ajax({
                url: '/api/pay/last',  // 실제 POST 받을 엔드포인트로 변경
                type: 'POST',
                contentType: 'application/json',
                data: JSON.stringify(formData),
                success: function (response) {
                    // 처리 완료 후 리다이렉트
                    window.location.href = '/user/donation';  // 성공 후 이동할 페이지 URL
                },
                error: function (error) {
                    console.error("Error sending data:", error);
                    // 에러 처리: 사용자에게 메시지 출력 등
                }
            });
        });
    });

    window.execPostCode = function() {
        new daum.Postcode({
            oncomplete: function (data) {
                document.querySelector('.address').value = data.address;
                document.querySelector('.address').dispatchEvent(new Event('input')); // 변경 감지
                updateFullAddress();
            },
        }).open();
    }




});






// 전화번호 자동 하이픈 처리
const autoHyphen2 = (target) => {
    target.value = target.value
        .replace(/[^0-9]/g, '')
        .replace(/^(\d{0,3})(\d{0,4})(\d{0,4})$/g, '$1-$2-$3')
        .replace(/(\-{1,2})$/g, '');

    // 변경 감지하여 버튼 활성화 체크
    target.dispatchEvent(new Event('input'));
};
