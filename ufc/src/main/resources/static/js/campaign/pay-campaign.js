$(document).ready(function () {
    var rewardData = JSON.parse($("#rewardDataContainer").attr("data-reward"));
    console.log("Reward Data:", rewardData);

    rewardData.forEach(function(rewardItem) {
        $.ajax({
            url: '/api/reward/target/' + rewardItem.rewardId,
            method: 'GET',
            success: function(response) {
                console.log(response);

                // rewardItems 배열이 존재하고 항목이 있을 때 각 item의 name을 li로 만들어줍니다.
                var rewardItemsHtml = "";
                if(response.rewardItems && response.rewardItems.length > 0) {
                    response.rewardItems.forEach(function(item) {
                        rewardItemsHtml += `<li><span>${item.item.name}</span></li>`;
                    });
                }

                var rewardTargetHtml = `
                <div class="inner-box-in-contentr-in-ri-in">
                    <!-- 상품 제목 -->
                    <span>${response.rewardName} x ${rewardItem.count}</span>
                    <!-- 상품 구성품: rewardItems를 반복 -->
                    <ul class="inner-box-ul">
                        ${rewardItemsHtml}
                    </ul>
                    <!-- 도착 예정일 -->
                </div>
                `;

                $(".reward-target").append(rewardTargetHtml);
                $("#donationForm").append(`<input type="hidden" class="reward-target-item" data-reward-total="${rewardItem.count}" data-reward-id="${response.rewardId}">`)
            },
            error: function(error) {
                console.error("Reward retrieval error:", error);
            }
        });
    });


    // 송장번호 불러오기
    $.ajax({
        url: '/api/invoice',
        method: 'GET',
        success: function(response) {
            response.forEach((data)=>{
                selectBox = `
                        <option
                    value="${data.courierId}"
            >
                ${data.courierName}
            </option>
            `
                $(".invoice-select-box").append(selectBox);
            })

        },
        error: function(error) {
            console.error("Reward retrieval error:", error);
        }
    });

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


    function updateInvoiceData() {
        // 택배사 선택값과 송장번호 입력값을 가져옵니다.
        var courier = $('.invoice-select-box').val().trim();
        var tracking = $('input[placeholder="송장번호를 입력해주세요."]').val().trim();
        // 둘 중 하나라도 값이 있다면 '#'을 구분자로 결합합니다.
        var fullInvoice = courier + (tracking ? '#' + tracking : '');
        // hidden 필드에 업데이트
        $('.total-invoce').val(fullInvoice);
        console.log("Updated invoice data:", fullInvoice);
    }

    $(document).ready(function () {
        // 택배사 선택값이 바뀔 때마다 updateInvoiceData() 호출
        $('.invoice-select-box').on('change', updateInvoiceData);
        // 송장번호 입력 필드 값 변경 시에도 updateInvoiceData() 호출
        $('input[placeholder="송장번호를 입력해주세요."]').on('keyup change', updateInvoiceData);

        // 페이지 로드 시 초기값 업데이트
        updateInvoiceData();
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

            // 폼 데이터를 수집 (각 input의 클래스나 id에 맞게 조정)
// donationItem에 담긴 데이터 수집
            var donationItems = [];
            $('input[name="donationItem"]').each(function() {
                donationItems.push({
                    donationMaterialId: $(this).attr('data-donation-material-id'),
                    donationTotal: $(this).attr('data-donation-total')
                });
            });

// 폼 데이터 객체 생성 (기존 입력 필드 값과 donationItems 배열 추가)
// donationItem 데이터 수집
            var donationItems = [];
            $('input[name="donationItem"]').each(function() {
                donationItems.push({
                    donationMaterialId: $(this).attr('data-donation-material-id'),
                    donationTotal: $(this).attr('data-donation-total'),
                    donationReward: $(this).attr('data-reward-id')
                });
            });

// reward-target-item 데이터 수집
            var rewardItems = [];
            $('.reward-target-item').each(function() {
                rewardItems.push({
                    rewardId: $(this).attr('data-reward-id'),
                    rewardTotal: $(this).attr('data-reward-total')
                });
            });

// 폼 데이터 객체 생성 (기존 입력값과 함께 donationItems, rewardItems 배열 추가)
            var formData = {
                campaignId : $(".campaign-id").val(),
                username: $(".username").val(),
                userPhone: $(".phoneNumber").val(),
                useraddress: $(".total-add").val(),
                invoice: $(".total-invoce").val(),
                donationItems: donationItems,
                rewardItems: rewardItems
            };

            console.log(formData);




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
