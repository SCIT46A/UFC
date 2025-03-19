$(document).ready(function () {
    // 페이지 로드 시 초기화 함수 호출
    //initializePage();

    let pageStatus = $('.cam-la-in-box-top-in-na-all-ul-li.check').attr('data-target');
    // 0. 약관, 동의
    const startBtn = document.querySelector(".cam-create-btn");
    $(document).on('click', function () {
        if ($('#fi').is(':checked') && $('#ce').is(':checked') && $('#th').is(':checked')) {
            startBtn.classList.add("isActive");
            startBtn.disabled = false;
        } else {
            startBtn.classList.remove("isActive");
            startBtn.disabled = true;
        }
    });

    // 캠페인 생성 동의 체크박스 검사, 시작하기 버튼 활성화
    $('.cam-create-btn').on('click', function () {
        $('.cam-ag-box').addClass('hidden');
        $('.cam-la').removeClass('hidden');
    });
    // 약관, 동의 END

    // 1. 캠페인 기본정보 (캠페인 타이틀 입력 + 태그 추가 + 캠페인 소개 작성)
    let campaignTitle = document.querySelector("input[name='title']");        // 캠페인 제목
    let campaignDescription = document.querySelector(".cam-la-in-box-bo-all-de-div-box-textarea");    // 캠페인 설명
    let imageInput = document.getElementById('campaignImageInput');    // 이미지 입력

    let tagList = [];   // 선택된 태그 목록

    const tagAdd = document.querySelector("#tagAdd");

    let tag = `
        <li>
            <button class="upda-tag-custom">
                <span>+</span>
                <input type="text" name="inputTag"
                        placeholder="직접입력" 
                        style="width: 50px;"
                        oninput="this.style.width = ((this.value.length + 1) * 10) + 'px'"
                >
                <input type="button" class="tagDelete" value="X" style="color: red; background-color: transparent; border: none;">
            </button>
        </li>
    `;
    // 태그 버튼 누르면 선택 표시, 다시 누르면 선택 해제 태그 내용 데이터에 추가
    $(document).on('click', '.upda-tag-big', function () {
        $(this).toggleClass('tag-active');
        const tagName = $(this).text().trim();
        const duplicateTags = $(`.upda-tag-big[data-tag="${tagName}"]`).toArray();

        if ($(this).hasClass('tag-active')) {
            if (!tagList.includes(tagName)) {
                tagList.push(tagName);
            }
        } else {
            tagList = tagList.filter(tag => tag !== tagName);
            // 같은 태그가 2개 이상 있는 경우 가장 나중에 생성된 것을 제거
            if (duplicateTags.length > 1) {
                // 가장 마지막에 생성된 태그 제거
                const lastDuplicateTag = duplicateTags[duplicateTags.length - 1];
                $(lastDuplicateTag).closest('li').remove();

                // 남은 태그들 중 비활성화된 것이 있다면 활성화
                const remainingTags = duplicateTags.slice(0, -1);
                remainingTags.forEach(tag => {
                    if (!$(tag).hasClass('tag-active')) {
                        $(tag).addClass('tag-active');
                    }
                });

                showAlert('중복된 태그가 정리되었습니다.');
            }
        }

        console.log('Current tagList:', tagList);
        checkInfoPageInput();
    });

    $(document).on('keyup', '.upda-tag-custom > input[type="text"]', function (e) {
        // Enter 키 입력 시 태그 저장
        if (e.key === 'Enter') {
            const inputValue = $(this).val().trim();
            // 이미 존재하는 태그인지 HTML에서도 확인
            const existingTags = $('.upda-tag-big').map(function () {
                return $(this).text().trim();
            }).get();

            if (inputValue && !existingTags.includes(inputValue)) {
                tagList.push(inputValue);
                // 입력 필드를 태그로 변환
                const $li = $(this).closest('li');
                $li.html(`
                    <button class="upda-tag-big tag-active" data-tag="${inputValue}">
                        ${inputValue}
                    </button>
                `);
                checkInfoPageInput();
            } else if (inputValue) {
                showAlert('이미 존재하는 태그입니다.');
                $(this).closest('li').remove();
            }
            e.preventDefault(); // 폼 제출 방지
            return;
        }

        // 입력값이 변경될 때마다 중복 체크
        const inputValue = $(this).val().trim();
        if (inputValue.length > 0) {
            // HTML에서 현재 존재하는 모든 태그 확인
            const existingTags = $('.upda-tag-big').map(function () {
                return $(this).text().trim();
            }).get();

            if (existingTags.includes(inputValue)) {
                // 중복된 태그인 경우
                $(this).parent().removeClass('tag-active').addClass('tag-duplicate');
                $(this).css('color', '#ff4444');
                if (!$(this).next('.duplicate-tooltip').length) {
                    $(this).after('<span class="duplicate-tooltip">이미 존재하는 태그입니다</span>');
                }
            } else {
                $(this).parent().addClass('tag-active').removeClass('tag-duplicate');
                $(this).css('color', '');
                $(this).next('.duplicate-tooltip').remove();
            }
        } else {
            $(this).parent().removeClass('tag-active tag-duplicate');
            $(this).css('color', '');
            $(this).next('.duplicate-tooltip').remove();
        }
    });
    let editTag = '';
    $(document).on('focusout', '.upda-tag-custom > input[type="text"]', function () {
        const inputValue = $(this).val().trim();

        // 빈 값이거나 이미 존재하는 태그인 경우 입력 필드 삭제
        if (inputValue === '' || tagList.includes(inputValue)) {
            $(this).closest('li').remove();
            return;
        }

        if ($(this).parent().hasClass('tag-active')) {
            tagList.push(inputValue);
            editTag = inputValue;
            // 입력 필드를 태그로 변환
            const $li = $(this).closest('li');
            $li.html(`
                <button class="upda-tag-big tag-active" data-tag="${inputValue}">
                    ${inputValue}
                </button>
            `);
        }
        checkInfoPageInput();
    });
    $(document).on('focusin', '.upda-tag-custom > input', function () {
        const tagName = $(this).val().trim();
        tagList = tagList.filter(tag => tag !== tagName);
        editTag = '';
        console.log('Focus in - current tagList:', tagList);
    });


    // 태그 추가 버튼 클릭 시 태그 추가 필드 제공
    $(tagAdd).on('click', function () {
        // 현재 입력 중인 태그 필드가 있는지 확인
        const existingEmptyInput = $('.upda-tag-custom > input[type="text"]').filter(function () {
            return $(this).val().trim() === '';
        });

        // 빈 입력 필드가 있으면 새로운 필드를 추가하지 않음
        if (existingEmptyInput.length > 0) {
            existingEmptyInput.first().focus();
            return;
        }

        $(tagAdd).before(tag);
    });

    // 태그 삭제 버튼 클릭 시 추가된 필드 삭제 (이벤트 위임 사용)
    $(document).on('click', '.tagDelete', function (e) {
        e.preventDefault();  // 기본 동작 방지
        e.stopPropagation(); // 이벤트 전파 중지
        const tagName = $(this).prev().val().trim();
        $(this).closest('li').remove();
        tagList = tagList.filter(tag => tag !== tagName);
        checkInfoPageInput();
    });
    // 1. 캠페인 기본정보 (캠페인 타이틀 입력 + 태그 추가 + 캠페인 소개 작성) END

    // 2. 펀딩 일자
    // 펀딩 기간 설정 초기화
    const fundingStartDate = document.getElementById('funding-start-datetime');
    const fundingEndDate = document.getElementById('funding-end-datetime');
    const fundingSendDate = document.getElementById('funding-send-datetime');
    const fundingPeriod = document.getElementById('funding-period');
    const preparePeriod = document.getElementById('prepare-period');

    // 리워드 섹션의 종료일자 input 요소
    const rewardEndDate = document.querySelectorAll('#funding-end-datetime')[1];

    // 오늘 날짜 구하기 (시작시간은 다음 시간으로 설정)
    const today = new Date();

    // 21시 이후인 경우 모레부터 선택 가능하도록 설정
    const minDate = new Date(today);
    if (today.getHours() >= 21) {
        minDate.setDate(today.getDate() + 2);
    } else {
        minDate.setDate(today.getDate() + 1);
    }
    minDate.setHours(0, 0, 0, 0);

    // KST로 변환 (UTC+9)
    const kstOffset = 9 * 60 * 60 * 1000; // 9시간을 밀리초로 변환
    const todayKST = new Date(today.getTime() + kstOffset);

    // KST 시간대로 문자열 변환
    function toKSTString(date) {
        const year = date.getFullYear();
        const month = String(date.getMonth() + 1).padStart(2, '0');
        const day = String(date.getDate()).padStart(2, '0');
        return `${year}-${month}-${day}T00:00`;
    }

    const minDateTime = toKSTString(minDate);

    // 종료일 (시작일 + 30일) 계산
    const defaultEndDate = new Date(minDate);
    defaultEndDate.setDate(minDate.getDate() + 30);

    // 발송일 (종료일 + 7일) 계산
    const defaultSendDate = new Date(defaultEndDate);
    defaultSendDate.setDate(defaultEndDate.getDate() + 7);

    // 시작일 제한 설정 (오늘 이후로만 설정 가능)
    fundingStartDate.setAttribute('min', minDateTime);

    // 기본값 설정
    fundingStartDate.value = toKSTString(minDate);
    fundingEndDate.value = toKSTString(defaultEndDate);
    fundingSendDate.value = toKSTString(defaultSendDate);

    // 초기 기간 표시 업데이트
    fundingPeriod.textContent = '최대 60일';
    preparePeriod.textContent = '최대 60일';

    // 펀딩 일정 페이지 입력 감지
    $(document).on('change', '#funding-start-datetime, #funding-end-datetime, #funding-send-datetime', checkFundingPageInput);

    function checkFundingPageInput() {
        const currentPage = $('.cam-la-in-box-top-in-na-all-ul-li.check').attr('data-target');
        if (currentPage !== 'funding') return;

        const hasStartDate = $('#funding-start-datetime').val() !== '';
        const hasEndDate = $('#funding-end-datetime').val() !== '';
        const hasSendDate = $('#funding-send-datetime').val() !== '';

        if (hasStartDate && hasEndDate && hasSendDate) {
            $('.cam-la-in-box-bottom-in-end-btn').removeAttr('disabled').addClass('isActive');
        } else {
            $('.cam-la-in-box-bottom-in-end-btn').attr('disabled', true).removeClass('isActive');
        }
    }

    // 알림 메시지 표시 함수
    function showAlert(message) {
        const alertDiv = document.createElement('div');
        alertDiv.className = 'date-alert';
        alertDiv.innerHTML = `
            <div class="date-alert-content">
                <span class="date-alert-icon">⚠️</span>
                <span class="date-alert-message">${message}</span>
            </div>
        `;
        document.body.appendChild(alertDiv);

        // 애니메이션을 위한 타이밍
        setTimeout(() => alertDiv.classList.add('show'), 100);

        // 3초 후 제거
        setTimeout(() => {
            alertDiv.classList.remove('show');
            setTimeout(() => alertDiv.remove(), 300);
        }, 3000);
    }

    // 펀딩 기간 계산 함수
    function calculateFundingPeriod() {
        if (!fundingStartDate.value || !fundingEndDate.value) return;

        const startDate = new Date(fundingStartDate.value);
        const endDate = new Date(fundingEndDate.value);
        const diffTime = Math.abs(endDate - startDate);
        const diffDays = Math.ceil(diffTime / (1000 * 60 * 60 * 24));

        if (diffDays > 60) {
            showAlert('펀딩 기간은 최대 60일을 초과할 수 없습니다.');
            const maxEndDate = new Date(startDate);
            maxEndDate.setDate(startDate.getDate() + 60);
            fundingEndDate.value = maxEndDate.toISOString().split('T')[0];
            fundingPeriod.textContent = '60일';
        } else {
            fundingPeriod.textContent = diffDays + '일';
        }
    }

    // 리워드 준비 기간 계산 함수
    function calculatePreparePeriod() {
        if (!fundingEndDate.value || !fundingSendDate.value) return;

        const endDate = new Date(fundingEndDate.value);
        const sendDate = new Date(fundingSendDate.value);
        const diffTime = Math.abs(sendDate - endDate);
        const diffDays = Math.ceil(diffTime / (1000 * 60 * 60 * 24));

        if (diffDays > 60) {
            showAlert('리워드 준비 기간은 최대 60일을 초과할 수 없습니다.');
            const maxSendDate = new Date(endDate);
            maxSendDate.setDate(endDate.getDate() + 60);
            fundingSendDate.value = maxSendDate.toISOString().split('T')[0];
            preparePeriod.textContent = '60일';
        } else {
            preparePeriod.textContent = diffDays + '일';
        }
    }

    // 날짜 입력 시 유효성 검사 및 자동 계산
    fundingStartDate.addEventListener('change', function () {
        const startDate = new Date(this.value);

        // 과거 날짜 체크
        if (startDate < minDate) {
            const alertMsg = today.getHours() >= 21 ?
                '내일 시작은 오후 9시 이전까지만 가능합니다. 모레로 설정해주세요.' :
                '시작일은 내일 이후로 설정해야 합니다.';
            showAlert(alertMsg);
            this.value = '';
            $('.date-from-today').text('');
            checkFundingPageInput();
            return;
        }

        // 오늘로부터 몇 일 후인지 계산
        const calcToday = new Date();
        calcToday.setHours(0, 0, 0, 0);
        const diffTime = startDate - calcToday;
        const diffDays = Math.ceil(diffTime / (1000 * 60 * 60 * 24)) - 1;

        if (diffDays === 0) {
            $('.date-from-today').text('내일부터');
        } else {
            $('.date-from-today').text(`오늘로부터 ${diffDays}일 후`);
        }

        // 종료일 제한 업데이트
        if (fundingEndDate) {
            const minEndDate = new Date(startDate);
            minEndDate.setDate(startDate.getDate() + 1);
            fundingEndDate.min = minEndDate.toISOString().split('T')[0];

            if (new Date(fundingEndDate.value) < startDate) {
                fundingEndDate.value = '';
                checkFundingPageInput();
            }
        }

        // 펀딩 기간 재계산
        calculateFundingPeriod();
    });

    fundingEndDate.addEventListener('change', function () {
        const endDate = new Date(this.value);
        const startDate = new Date(fundingStartDate.value);

        // 시작일이 설정되지 않은 경우
        if (!fundingStartDate.value) {
            showAlert('먼저 시작일을 설정해주세요.');
            this.value = '';
            checkFundingPageInput();
            return;
        }

        // 시작일 이전으로 설정한 경우
        if (endDate < startDate) {
            showAlert('종료일은 시작일 이후로 설정해야 합니다.');
            this.value = '';
            checkFundingPageInput();
            return;
        }

        // 펀딩 기간 재계산
        calculateFundingPeriod();

        // 발송일 제한 업데이트
        if (fundingSendDate) {
            fundingSendDate.setAttribute('min', this.value);

            if (new Date(fundingSendDate.value) < endDate) {
                fundingSendDate.value = '';
                preparePeriod.textContent = '최대 60일';
                checkFundingPageInput();
            }
        }

        // 리워드 준비 기간 재계산
        calculatePreparePeriod();
    });

    fundingSendDate.addEventListener('change', function () {
        const sendDate = new Date(this.value);
        const endDate = new Date(fundingEndDate.value);

        // 종료일이 설정되지 않은 경우
        if (!fundingEndDate.value) {
            showAlert('먼저 종료일을 설정해주세요.');
            this.value = '';
            checkFundingPageInput();
            return;
        }

        // 종료일 이전으로 설정한 경우
        if (sendDate < endDate) {
            showAlert('발송일은 종료일 이후로 설정해야 합니다.');
            this.value = '';
            checkFundingPageInput();
            return;
        }

        // 리워드 준비 기간 재계산
        calculatePreparePeriod();

        checkFundingPageInput();
    });
    // 2. 펀딩 일자 END

    // 3. 리워드 구성
    // 펀딩 계획 페이지 기부품 추가 버튼 클릭 시 기부품 추가 필드 제공

    let fundingItems = []; // [{name: "", amount: 0}] rewardList.funding의 name별 amount를 더하여 표현
    let rewardItems = []; //[{"name": '', "amount": ''}] rewardList.reward의 name별 amount를 더하여 표현
    let rewardList = { "name": "", "amount": 0, "funding": [], "reward": [] };


    // 리워드 입력 필드 포커스 이벤트
    $(document).on('focusin', '.gi-se-input-va, #productionAmount', function () {
        const rewardTitle = $('.gi-se-input-va').val();
        const rewardAmount = $('#productionAmount').val();

        // 입력 시작할 때 리워드 정보 초기화
        rewardList.name = "";
        rewardList.amount = 0;

        // 입력 중에는 버튼 비활성화
        $('#rewardAddBtn').attr('disabled', true).removeClass('isActive');
    });

    // 리워드 입력 필드 포커스 아웃 이벤트
    $(document).on('focusout', '.gi-se-input-va, #productionAmount', function () {
        const rewardTitle = $('.gi-se-input-va').val();
        const rewardAmount = $('#productionAmount').val();

        if (rewardTitle && rewardAmount) {
            // 값 설정
            rewardList.name = rewardTitle;
            rewardList.amount = parseInt(rewardAmount);
            console.log('Added to list:', rewardList);

            // 입력이 완료되면 버튼 상태 체크
            checkRewardPageInput();
        }
    });

    // 기부품 이름 및 수량 입력 시 버튼 활성화
    $(document).on('keyup', '.fundingItemInput, .fundingAmountInput', activeItemFundingBtn);
    function activeItemFundingBtn() {
        let fundingItem = $(this).closest('div').parent().find('.fundingItemInput');
        let fundingAmount = $(this).closest('div').parent().find('.fundingAmountInput');

        if ((fundingItem.val() != '' && fundingAmount.val() != '') && !rewardList.funding.some(item => item.name === fundingItem.val())) {
            $('.cam-la-pay-btn.funding').addClass('isActive');
            $('.cam-la-pay-btn.funding').removeAttr('disabled');
        } else {
            $('.cam-la-pay-btn.funding').removeClass('isActive');
            $('.cam-la-pay-btn.funding').attr('disabled', true);
        }
    }

    $(document).on('keyup', '.rewardItemInput, .rewardAmountInput', activeItemRewardBtn);
    function activeItemRewardBtn() {
        let rewardItem = $(this).closest('div').parent().find('.rewardItemInput');
        let rewardAmount = $(this).closest('div').parent().find('.rewardAmountInput');

        if ((rewardItem.val() != '' && rewardAmount.val() != '') && !rewardList.reward.some(item => item.name === rewardItem.val())) {
            $('.cam-la-pay-btn.reward').addClass('isActive');
            $('.cam-la-pay-btn.reward').removeAttr('disabled');
        } else {
            $('.cam-la-pay-btn.reward').removeClass('isActive');
            $('.cam-la-pay-btn.reward').attr('disabled', true);
        }
    }

    // 기부품 추가 버튼 클릭 시 기부품 추가 
    $(document).on('click', '.cam-la-pay-btn.funding', addFundingItem);
    function addFundingItem() {
        let fundingItem = $(this).closest('div').find('.fundingItemInput');
        let fundingAmount = $(this).closest('div').find('.fundingAmountInput');

        rewardList.funding.push({ name: fundingItem.val(), amount: parseInt(fundingAmount.val()) });

        let fundingTag = `
            <div class="cam-la-pay-box-pe">
                <div class="cam-la-pay-box-pe-fname">${fundingItem.val()}</div>
                <div class="cam-la-pay-box-pe-famount">${fundingAmount.val()}</div>
                <button class="cam-tag-sh-pe-btn">
                    <svg viewBox="0 0 40 40" focusable="false" role="presentation" class="withIcon_icon__1YH1P" aria-hidden="true">
                        <path d="M33.4 8L32 6.6l-12 12-12-12L6.6 8l12 12-12 12L8 33.4l12-12 12 12 1.4-1.4-12-12 12-12z"></path>
                    </svg>
                </button>
            </div>
        `;

        $('.cam-la-pay-box-total.funding').parent().append(fundingTag);
        fundingItem.val('');
        fundingAmount.val('');
        $('.cam-la-pay-btn.funding').removeClass('isActive');
        $('.cam-la-pay-btn.funding').attr('disabled', true);
    }

    // 리워드 추가 버튼 클릭 시 리워드 추가
    $(document).on('click', '.cam-la-pay-btn.reward', addRewardItem);
    function addRewardItem() {
        let rewardItem = $(this).closest('div').find('.rewardItemInput');
        let rewardAmount = $(this).closest('div').find('.rewardAmountInput');

        rewardList.reward.push({ name: rewardItem.val(), amount: parseInt(rewardAmount.val()) });

        let rewardTag = `
            <div class="cam-la-pay-box-pe">
                <div class="cam-la-pay-box-pe-fname">${rewardItem.val()}</div>
                <div class="cam-la-pay-box-pe-famount">${rewardAmount.val()}</div>
                <button class="cam-tag-sh-pe-btn">
                    <svg viewBox="0 0 40 40" focusable="false" role="presentation" class="withIcon_icon__1YH1P" aria-hidden="true">
                        <path d="M33.4 8L32 6.6l-12 12-12-12L6.6 8l12 12-12 12L8 33.4l12-12 12 12 1.4-1.4-12-12 12-12z"></path>
                    </svg>
                </button>
            </div>
        `;

        $('.cam-la-pay-box-total:not(.funding)').parent().append(rewardTag);
        rewardItem.val('');
        rewardAmount.val('');
        $('.cam-la-pay-btn.reward').removeClass('isActive');
        $('.cam-la-pay-btn.reward').attr('disabled', true);
    }

    // 아이템 삭제 버튼 클릭 시 추가된 필드 삭제 (이벤트 위임 사용)
    $(document).on('click', '.cam-tag-sh-pe-btn', function () {
        let itemName = $(this).closest('div').find('.cam-la-pay-box-pe-fname').text();
        let itemAmount = $(this).closest('div').find('.cam-la-pay-box-pe-famount').text();
        if ($(this).closest('div').prev().hasClass('funding')) {
            rewardList.funding = rewardList.funding.filter(item => item.name != itemName || item.amount != itemAmount);
        } else {  //$(this).closest('div').prev().hasClass('reward')
            rewardList.reward = rewardList.reward.filter(item => item.name != itemName || item.amount != itemAmount);
        }
        //console.log(fundingItems);
        $(this).closest('div').remove();  // 또는 $(this).parent().parent().remove();
    });

    $(document).on('click', '#rewardAddBtn', function () {
        console.log('저장 전 : ' + JSON.stringify(rewardList));
        addRewardItemField();
        $('#rewardAddBtn').attr('disabled', true).removeClass('isActive');
        console.log('저장 후 : ' + JSON.stringify(rewardList));
    });

    // 리워드 섹션태그 추가 함수
    function addRewardItemField() {
        // 리워드 섹션태그 변수
        let rewardTitle = rewardList.name;  // 이미 문자열임
        let rewardTargetName = "";
        let rewardTargetAmount = "";
        let rewardSendDate = fundingSendDate.value;
        let rewardSelectedCount = 0;
        let rewardLeftAmount = parseInt($("#productionAmount").val());

        console.log('Adding reward with:', {
            title: rewardTitle,
            amount: rewardLeftAmount,
            funding: rewardList.funding,
            reward: rewardList.reward
        });

        // rewardItems에 새로운 리워드 추가
        rewardItems.push({
            name: rewardTitle,
            amount: rewardLeftAmount,
            funding: [...rewardList.funding],  // 배열 복사
            reward: [...rewardList.reward]     // 배열 복사
        });

        console.log('Current rewardItems:', rewardItems);

        let rewardItemField = `
            <div class="cam-gi-box-le-in-bo-pe">
                <div>
                    <button class="cam-gi-box-le-in-bo-pe-content">
        `;
        for (let i = 0; i < rewardList.funding.length; i++) {
            rewardTargetName = rewardList.funding[i].name;
            rewardTargetAmount = rewardList.funding[i].amount;
            rewardItemField += `
                        <strong>${rewardTargetName} ${rewardTargetAmount}개+</strong>
            `;
        }
        rewardItemField += `
                        <p class="cam-gi-box-le-in-bo-pe-content-title">${rewardTitle}</p>
            `;
        for (let i = 0; i < rewardList.reward.length; i++) {
            rewardTargetName = rewardList.reward[i].name;
            rewardTargetAmount = rewardList.reward[i].amount;
            rewardItemField += `
                        <ul>
                            <li>${rewardTargetName} ${rewardTargetAmount}개</li>
                        </ul>
            `;
        }
        rewardItemField += `
                        <span>예상 발송 시작일: <em>${rewardSendDate}</em></span>
                        <div class="cam-gi-box-le-in-bo-pe-content-div">
                            <em class="cam-gi-em">
                                <div class="cam-gi-em-in">
                                    <svg viewBox="0 0 48 48">
                                        <path fill-rule="evenodd" clip-rule="evenodd" d="M4.28544 5.00257L2.01916 2.73642C1.82521 2.54248 1.82974 2.23083 2.01598 2.02765C2.21448 1.81131 2.5294 1.8394 2.72795 2.02108L2.72969 2.02268L4.99738 4.2905L7.26357 2.02431C7.4575 1.83056 7.7691 1.83508 7.97226 2.02115C8.1886 2.21946 8.16077 2.53473 7.97878 2.73311L7.97723 2.73479L5.70945 5.00257L7.97564 7.26876C8.16953 7.46283 8.16504 7.77425 7.97884 7.97756L7.97724 7.9793L7.97557 7.98097C7.78164 8.17472 7.47008 8.17023 7.26691 7.98417L7.26519 7.98259L4.99738 5.71465L2.73129 7.981C2.53725 8.17469 2.22572 8.17025 2.02253 7.98417L2.01908 7.98101L2.01592 7.97756C1.82971 7.77425 1.82526 7.46279 2.01916 7.26872L4.28544 5.00257Z" fill="#6D6D6D"></path>
                                    </svg>
                                </div>
                                <span class="cam-gi-em-in">${rewardSelectedCount}</span>명이 선택
                            </em>
                            <div class="cam-gi-em-box">
                                <em>${rewardLeftAmount}개 남음</em>
                            </div>
                        </div>
                    </button>
                </div>
                <!-- 삭제버튼 -->
                <button class="cam-gi-de">
                    <div class="cam-gi-de-in">
                        <svg viewBox="0 0 48 48">
                            <path fill-rule="evenodd" clip-rule="evenodd"
                                d="M38.814 42.172C38.814 42.946 38.064 43.574 37.144 43.574H10.856C9.936 43.574 9.186 42.946 9.186 42.172V12.218H38.814V42.172ZM17.564 4.426L30.542 4.524V9.794H17.462L17.564 4.426ZM44.786 9.794H32.968V4.524C32.968 3.13 31.832 2 30.436 2H17.564C16.168 2 15.03 3.13 15.03 4.524V9.794H3.212C2.542 9.794 2 10.336 2 11.006C2 11.676 2.542 12.218 3.212 12.218H6.76V42.172C6.76 44.284 8.598 46 10.856 46H37.144C39.402 46 41.24 44.284 41.24 42.172V12.218H44.786C45.456 12.218 46 11.676 46 11.006C46 10.336 45.456 9.794 44.786 9.794ZM18.857 36.9338C19.527 36.9338 20.069 36.3918 20.069 35.7218V20.0738C20.069 19.4038 19.527 18.8618 18.857 18.8618C18.187 18.8618 17.645 19.4038 17.645 20.0738V35.7218C17.645 36.3918 18.187 36.9338 18.857 36.9338ZM30.3542 35.7218C30.3542 36.3918 29.8122 36.9338 29.1422 36.9338C28.4722 36.9338 27.9302 36.3918 27.9302 35.7218V20.0738C27.9302 19.4038 28.4722 18.8618 29.1422 18.8618C29.8122 18.8618 30.3542 19.4038 30.3542 20.0738V35.7218Z">
                            </path>
                        </svg>
                    </div>
                </button>
            </div>
            
        `;
        $('.cam-gi-box-le-in-bo').append(rewardItemField);

        // 전체 리워드 아이템 리스트에 추가 후 입력받는 리워드 리스트 초기화
        rewardList = {
            name: "",
            amount: 0,
            funding: [],
            reward: []
        };

        // 리워드 입력 폼 초기화
        $('.gi-se-input-va').val('');
        $('#productionAmount').val('');
        $('.rewardItemInput').val('');
        $('.rewardAmountInput').val('');
        $('.fundingItemInput').val('');
        $('.fundingAmountInput').val('');
        $('.cam-la-pay-box-total.funding').nextAll().remove();
        $('.cam-la-pay-box-total.reward').nextAll().remove();
        $('#rewardCount').text(rewardItems.length);
    }

    // 리워드 삭제 버튼 클릭 시 리워드 삭제
    $(document).on('click', '.cam-gi-de', function () {
        console.log('리워드 삭제 전:');
        console.log('rewardItems:', JSON.stringify(rewardItems));

        // 삭제할 리워드의 제목
        const titleToDelete = $(this).parent().find('.cam-gi-box-le-in-bo-pe-content-title').text();
        console.log('삭제하려는 리워드 제목:', titleToDelete);

        // rewardItems 배열에서 해당 리워드 삭제
        const indexToDelete = rewardItems.findIndex(item =>
            Array.isArray(item.name) ? item.name[0] === titleToDelete : item.name === titleToDelete
        );

        console.log('찾은 인덱스:', indexToDelete);
        if (indexToDelete !== -1) {
            rewardItems.splice(indexToDelete, 1);
        }

        $(this).parent().remove();
        $('#rewardCount').text(rewardItems.length);
        console.log('리워드 삭제 후:');
        console.log('rewardItems:', JSON.stringify(rewardItems));
    });
    // 3. 리워드 구성 END

    // 4. 최종 확인

    // 이미지 업로드 후 캠페인 데이터 전송
    async function submitCampaign() {
        try {
            // 캠페인 생성 완료 시 AJAX로 데이터 전송
            let sendData = {};

            sendData.tagList = tagList;     // 캠페인 태그 리스트
            sendData.title = campaignTitle.value;   // 캠페인 제목
            sendData.description = campaignDescription.value;    // 캠페인 설명
            sendData.fundingItems = fundingItems; // 캠페인 목표 총 재료 수량 리스트
            sendData.rewardList = rewardItems; // 캠페인 목표 총 리워드 수량 리스트
            sendData.startDate = new Date(fundingStartDate.value).toISOString(); // 캠페인 시작일
            sendData.endDate = new Date(fundingEndDate.value).toISOString();     // 캠페인 종료일
            sendData.sendDate = new Date(fundingSendDate.value).toISOString();   // 캠페인 발송일
            let userNameElement = document.querySelector(".header-box-top-pe-my-in-name");

            if (userNameElement) {
                sendData.userName = userNameElement.textContent.trim();
            } else if (window.parent !== window) {
                try {
                    sendData.userName = window.parent.document.querySelector(".header-box-top-pe-my-in-name")?.textContent.trim() || "기본값";
                } catch (error) {
                    console.warn("⚠️ [WARNING] iframe에서 부모 페이지 접근 실패:", error);
                    sendData.userName = sessionStorage.getItem("userName") || "기본값";
                }
            } else {
                sendData.userName = sessionStorage.getItem("userName") || "기본값";
            }
            // 헤더의 사용자 이름(검토 필요)

            console.log(JSON.stringify(sendData));

            // 로딩 표시
            showLoading();

            // 이미지 파일 가져오기
            const imageFile = imageInput.files[0];

            if (!imageFile) {
                alert('캠페인 이미지가 등록되지 않았습니다.');
                return;
            } else {
                try {
                    const formData = new FormData();
                    formData.append('file', imageFile);

                    const response = await fetch('/api/image/upload', {
                        method: 'POST',
                        body: formData
                    });

                    if (!response.ok) {
                        throw new Error('이미지 업로드에 실패했습니다.');
                    }

                    const imageUrl = await response.text();
                    sendData.imageId = imageUrl;
                } catch (error) {
                    console.error('Error:', error);
                    throw error;
                }
            }



            console.log(JSON.stringify(sendData));

            // 서버로 캠페인 데이터 전송
            const response = await $.ajax({
                url: '/api/campaign/create',
                type: 'POST',
                contentType: 'application/json',
                data: JSON.stringify(sendData)
            });

            // 성공 시 처리
            if (!response.success) {
                alert('캠페인이 성공적으로 등록되었습니다.');
                window.location.href = '/campaign/' + response;
            } else {
                throw new Error(response.message || '캠페인 등록에 실패했습니다.');
            }

        } catch (error) {
            console.error('Error:', error);
            alert(error.message);
        } finally {
            hideLoading();
        }
    }

    // 로딩 표시/숨김 함수
    function showLoading() {
        // 로딩 UI 표시 로직
        $('.loading-overlay').show();
    }

    function hideLoading() {
        // 로딩 UI 숨김 로직
        $('.loading-overlay').hide();
    }


    $(document).on('change', '.fundingItemInput, .fundingAmountInput, .rewardItemInput, .rewardAmountInput', checkFirstPageInput);
    // 페이지 입력 검사
    function checkFirstPageInput() {
        console.log(tagList.length, campaignTitle.value, campaignDescription.value, imageInput.files.length);
        if (tagList.length > 0 && campaignTitle.value != '' && campaignDescription.value != '' && imageInput.files.length > 0) {
            $('.cam-la-in-box-bottom-in-end-btn').removeAttr('disabled');
        } else {
            $('.cam-la-in-box-bottom-in-end-btn').attr('disabled', true);
        }
    }
    // 하단 버튼 컨트롤러
    $(".cam-la-in-box-bottom-in-end-btn-span").text('다음');
    // 캠페인 기획 페이지 다음 버튼 -> 약관 동의 시작하기는 0.약관 동의에서 처리중
    $(document).on('click', '.cam-la-in-box-bottom-in-end-btn', function () {
        const navTarget = $('.cam-la-in-box-top-in-na-all-ul-li.check');
        let navName = navTarget.attr('data-target');
        if (navName == 'info') {  // info에서 다음 버튼 클릭 시 처리
            navTarget.removeClass('check');
            navTarget.next().addClass('check');
            $('.cam-la-in-box-bo.info').addClass('hidden');
            $('.cam-la-in-box-bo.funding').removeClass('hidden');
            checkCurrentPageInputs('funding');
        } else if (navName == 'funding') {  // funding에서 다음 버튼 클릭 시 처리
            navTarget.removeClass('check');
            navTarget.next().addClass('check');
            $('.cam-la-in-box-bo.funding').addClass('hidden');
            $('.cam-la-in-box-bo.reward').removeClass('hidden');
            checkCurrentPageInputs('reward');
        } else if (navName == 'reward') {  // reward에서 다음 버튼 클릭 시 처리
            console.log('Moving from reward to final page');
            console.log('Current rewardItems before moving:', JSON.stringify(rewardItems, null, 2));
            navTarget.removeClass('check');
            navTarget.next().addClass('check');
            $('.cam-la-in-box-bo.reward').addClass('hidden');
            $('.cam-la-in-box-bo.final').removeClass('hidden');
            $(".cam-la-in-box-bottom-in-end-btn-span").text('캠페인 생성');
            checkCurrentPageInputs('final');
            console.log('Calling updateFinalSummary...');
            updateFinalSummary();
        }
        pageStatus = $('.cam-la-in-box-top-in-na-all-ul-li.check').attr('data-target');
        console.log(pageStatus);
    });
    // 캠페인 생성 버튼 클릭 이벤트
    $('.cam-la-in-box-bottom-in-end-btn').on('click', function () {
        if ($(this).find('.cam-la-in-box-bottom-in-end-btn-span').text() === '캠페인 생성') {
            submitCampaign();
        }
    });

    // 캠페인 기획 페이지 뒤로가기 버튼
    $(document).on('click', '.cam-la-in-box-bottom-in-back', function () {
        // 캠페인 기획 페이지가 보이는 중에 눌리면 약관 동의 페이지가 나오도록
        const navTarget = $('.cam-la-in-box-top-in-na-all-ul-li.check');
        let navName = navTarget.attr('data-target');
        if (navName == 'info') {
            $('.cam-ag-box').removeClass('hidden'); // 약관 동의 페이지
            $('.cam-la').addClass('hidden');
        } else if (navName == 'funding') {
            navTarget.removeClass('check');
            navTarget.prev().addClass('check');
            $('.cam-la-in-box-bo.info').removeClass('hidden');
            $('.cam-la-in-box-bo.funding').addClass('hidden');
        } else if (navName == 'reward') {
            navTarget.removeClass('check');
            navTarget.prev().addClass('check');
            $('.cam-la-in-box-bo.funding').removeClass('hidden');
            $('.cam-la-in-box-bo.reward').addClass('hidden');
        } else if (navName == 'final') {
            navTarget.removeClass('check');
            navTarget.prev().addClass('check');
            $('.cam-la-in-box-bo.reward').removeClass('hidden');
            $('.cam-la-in-box-bo.final').addClass('hidden');
            $(".cam-la-in-box-bottom-in-end-btn-span").text('다음');
        }
        pageStatus = $('.cam-la-in-box-top-in-na-all-ul-li.check').attr('data-target');
        console.log(pageStatus);
    });
    // 하단 버튼 컨트롤러 END

    // 각 페이지별 입력 필드 변경 감지
    // 기본 정보 페이지 입력 감지
    $(document).on('input', 'input[name="title"]', checkInfoPageInput);
    $(document).on('input', '.cam-la-in-box-bo-all-de-div-box-textarea', checkInfoPageInput);
    //$(document).on('click', checkInfoPageInput);
    $(document).on('keydown', checkInfoPageInput);
    $(document).on('change', '#campaignImageInput', function () {
        const previewImage = document.getElementById('previewImage');
        const file = this.files[0];

        if (file) {
            const reader = new FileReader();
            reader.onload = function (e) {
                previewImage.src = e.target.result;
                previewImage.style.display = 'block';
                document.querySelector('.cam-img-re').style.display = 'flex';
                document.querySelector('.cam-img-re-box-sh span').style.display = 'none';
            };
            reader.readAsDataURL(file);
        }

        checkInfoPageInput();
    });
    $(document).on('click', '.upda-tag-big, .tagDelete, .upda-tag-custom', checkInfoPageInput);

    // 페이지별 입력 검사 함수들
    function checkInfoPageInput() {
        const currentPage = $('.cam-la-in-box-top-in-na-all-ul-li.check').attr('data-target');
        if (currentPage !== 'info') return;

        const title = campaignTitle ? campaignTitle.value.trim() : '';
        const desc = $('.cam-la-in-box-bo-all-de-div-box-textarea').val() ? $('.cam-la-in-box-bo-all-de-div-box-textarea').val().trim() : '';
        const hasImage = imageInput && (
            imageInput.files.length > 0 ||
            (document.getElementById('previewImage').style.display === 'block' &&
                document.getElementById('previewImage').src &&
                !document.getElementById('previewImage').src.includes('logo.png'))
        );

        console.log('Info page validation:', {
            tags: tagList.length,
            title: title,
            desc: desc,
            image: hasImage
        });

        if (tagList.length > 0 && title !== '' && desc !== '' && hasImage) {
            $('.cam-la-in-box-bottom-in-end-btn').removeAttr('disabled').addClass('isActive');
        } else {
            $('.cam-la-in-box-bottom-in-end-btn').attr('disabled', true).removeClass('isActive');
        }
    }

    $(document).on('input', '.rewardItemInput, .rewardAmountInput', checkRewardPageInput);
    function checkRewardPageInput() {
        const hasRewardTitle = $('.gi-se-input-va').val() && $('.gi-se-input-va').val().trim() !== '';
        const hasProductionAmount = $('#productionAmount').val() && $('#productionAmount').val() > 0;
        const hasFundingItems = rewardList.funding.length > 0;
        const hasRewardItems = rewardList.reward.length > 0;

        if (hasRewardTitle && hasProductionAmount && hasFundingItems && hasRewardItems) {
            $('#rewardAddBtn').removeAttr('disabled').addClass('isActive');
        } else {
            $('#rewardAddBtn').attr('disabled', true).removeClass('isActive');
        }
        /*
        console.log('Reward page inputs:', {
            title: hasRewardTitle,
            production: hasProductionAmount,
            funding: hasFundingItems,
            reward: hasRewardItems
        });
        */

        console.log(JSON.stringify(rewardList));



        if (rewardList.reward.length > 0) {
            $('.cam-la-in-box-bottom-in-end-btn').removeAttr('disabled').addClass('isActive');
        } else {
            $('.cam-la-in-box-bottom-in-end-btn').attr('disabled', true).removeClass('isActive');
        }
    }

    // 페이지 전환 시 해당 페이지의 입력 상태 확인
    function checkCurrentPageInputs(navName) {
        switch (navName) {
            case 'info':
                checkInfoPageInput();
                break;
            case 'funding':
                checkFundingPageInput();
                break;
            case 'reward':
                checkRewardPageInput();
                break;
            case 'final':
                // 최종 확인 페이지는 항상 활성화
                $('.cam-la-in-box-bottom-in-end-btn').removeAttr('disabled').addClass('isActive');
                break;
        }
    }

    // 최종 확인 페이지 데이터 채우기
    function updateFinalSummary() {
        console.log('Starting updateFinalSummary');
        console.log('rewardItems at start of summary:', JSON.stringify(rewardItems, null, 2));

        // 전체 필요 기부품 계산
        function calculateTotalFundingItems() {
            const totalFunding = {};

            rewardItems.forEach(rewardItem => {
                const multiplier = parseInt(rewardItem.amount); // 문자열을 숫자로 변환

                rewardItem.funding.forEach(fundingItem => {
                    const itemName = fundingItem.name;
                    const itemAmount = parseInt(fundingItem.amount) * multiplier;

                    if (totalFunding[itemName]) {
                        totalFunding[itemName] += itemAmount;
                    } else {
                        totalFunding[itemName] = itemAmount;
                    }
                });
            });

            // fundingItems 업데이트 - 새로운 형식으로 변경
            fundingItems = Object.entries(totalFunding).map(([name, amount]) => ({
                name: name,
                amount: amount
            }));

            return totalFunding;
        }

        // 전체 필요 기부품 표시
        const totalFunding = calculateTotalFundingItems();
        const totalFundingList = $('#totalFundingList');
        totalFundingList.empty();

        fundingItems.forEach(item => {
            totalFundingList.append(`
                <tr>
                    <td>${item.name}</td>
                    <td>${item.amount} 개</td>
                </tr>
            `);
        });

        console.log('Updated fundingItems:', fundingItems);

        // 기본 정보
        $('#final-campaign-title').text($('input[name="title"]').val());
        $('#final-campaign-desc').text($('.cam-la-in-box-bo-all-de-div-box-textarea').val());

        // 태그 표시
        const tagContainer = $('#final-campaign-tags');
        tagContainer.empty();
        tagList.forEach(tag => {
            tagContainer.append(`<span>${tag}</span>`);
        });

        // 펀딩 일정
        const startDate = new Date($('#funding-start-datetime').val());
        const endDate = new Date($('#funding-end-datetime').val());
        const sendDate = new Date($('#funding-send-datetime').val());

        $('#final-funding-period').text(
            `${startDate.toLocaleDateString()} ~ ${endDate.toLocaleDateString()} (${$('#funding-period').text()})`
        );

        $('#final-prepare-period').text(
            `${endDate.toLocaleDateString()} ~ ${sendDate.toLocaleDateString()} (${$('#prepare-period').text()})`
        );

        // 리워드 구성
        const rewardsList = $('#final-rewards-list');
        rewardsList.empty();

        console.log('rewardItems length:', rewardItems.length);
        console.log('rewardItems content:', JSON.stringify(rewardItems, null, 2));

        rewardItems.forEach((reward, index) => {
            console.log('Processing reward item:', JSON.stringify(reward, null, 2));
            console.log('Reward funding array:', JSON.stringify(reward.funding, null, 2));
            console.log('Reward reward array:', JSON.stringify(reward.reward, null, 2));

            const rewardElement = $(`
                <div class="summary-item reward-item">
                    <h4 class="reward-title">리워드 ${index + 1}</h4>
                    <div class="reward-details">
                        <div class="reward-header">
                            <p class="reward-name"><strong>리워드명:</strong> ${reward.name}</p>
                            <p class="reward-amount"><strong>생산 수량:</strong> ${reward.amount}개</p>
                        </div>
                        <div class="reward-items">
                            <div class="funding-items">
                                <p class="section-title"><strong>필요한 기부품</strong></p>
                                <ul>
                                    ${Array.isArray(reward.funding) ?
                    reward.funding.map(item => {
                        console.log('Processing funding item:', item);
                        return `<li><span class="item-name">${item.name}</span> <span class="item-amount">${item.amount}개</span></li>`;
                    }).join('') : '데이터 없음'}
                                </ul>
                            </div>
                            <div class="reward-items-list">
                                <p class="section-title"><strong>리워드 구성</strong></p>
                                <ul>
                                    ${Array.isArray(reward.reward) ?
                    reward.reward.map(item => {
                        console.log('Processing reward item:', item);
                        return `<li><span class="item-name">${item.name}</span> <span class="item-amount">${item.amount}개</span></li>`;
                    }).join('') : '데이터 없음'}
                                </ul>
                            </div>
                        </div>
                    </div>
                </div>
            `);
            console.log('Generated HTML:', rewardElement.html());
            rewardsList.append(rewardElement);
        });
        console.log('Final HTML content:', rewardsList.html());
    }

    // 최종 확인 페이지로 이동할 때 요약 정보 업데이트
    $(document).on('click', '.cam-la-in-box-bottom-in-end-btn', function () {
        if ($('.cam-la-in-box-top-in-na-all-ul-li.check').attr('data-target') === 'reward') {
            console.log('Moving to final page');
            console.log('Current rewardItems:', JSON.stringify(rewardItems, null, 2));
            updateFinalSummary();
        }
    });
}); // ===================================== $(document).ready END =====================================

function activeNextBtn() {
    const nextBtn = document.querySelector(".next-btn");
    if (nextBtn.value.length > 0) {
        nextBtn.classList.add("isActive");
    } else {
        nextBtn.classList.remove("isActive");
    }

}

// 입력창 너비 자동 조절 함수

function adjustWidth(input) {
    input.style.width = '20px'; // 최소 너비 설정
    input.style.width = input.scrollWidth + 'px';
}

document.querySelectorAll('input[name="inputTag"]').forEach(input => {
    input.style.width = '60px'; // 초기 너비
    input.addEventListener('input', function () {
        this.style.width = ((this.value.length + 1) * 10) + 'px';
    });
});

// 전역 변수 선언
let imageInput, previewImage, deleteButton, previewContainer;

// 페이지별 입력 검사 함수
function checkInfoPageInput() {
    const currentPage = $('.cam-la-in-box-top-in-na-all-ul-li.check').attr('data-target');
    if (currentPage !== 'info') return;

    const title = campaignTitle ? campaignTitle.value.trim() : '';
    const desc = $('.cam-la-in-box-bo-all-de-div-box-textarea').val() ? $('.cam-la-in-box-bo-all-de-div-box-textarea').val().trim() : '';
    const hasImage = imageInput && (
        imageInput.files.length > 0 ||
        (document.getElementById('previewImage').style.display === 'block' &&
            document.getElementById('previewImage').src &&
            !document.getElementById('previewImage').src.includes('logo.png'))
    );

    if (tagList.length > 0 && title !== '' && desc !== '' && hasImage) {
        $('.cam-la-in-box-bottom-in-end-btn').removeAttr('disabled').addClass('isActive');
    } else {
        $('.cam-la-in-box-bottom-in-end-btn').attr('disabled', true).removeClass('isActive');
    }
}

// DOM 요소 초기화
imageInput = document.getElementById('campaignImageInput');
previewImage = document.getElementById('previewImage');
deleteButton = document.querySelector('.cam-img-re-box-btn-in');
previewContainer = document.querySelector('.cam-img-re-box-sh');

// 처음에는 미리보기 숨기기
previewImage.style.display = 'none';
document.querySelector('.cam-img-re').style.display = 'none';

// 이미지 업로드 이벤트
imageInput.addEventListener('change', async function (e) {
    const file = e.target.files[0];
    if (!file) return;

    try {
        // 이미지를 1:1 비율로 크롭
        const croppedFile = await cropTo1x1(file);
        await processAndDisplayImage(croppedFile);
        // 이미지가 업로드되면 삭제 버튼 표시
        deleteButton.style.display = 'block';
    } catch (error) {
        console.error('Error processing image:', error);
        showAlert('이미지 처리 중 오류가 발생했습니다.');
    }
});

// 삭제 버튼 클릭 이벤트
$(deleteButton).on('click', function (e) {
    e.preventDefault();
    previewImage.src = '';
    previewImage.style.display = 'none';
    imageInput.value = '';
    document.querySelector('.cam-img-re').style.display = 'none';
    previewContainer.querySelector('span').style.display = 'flex';
    // 삭제 버튼 숨기기
    this.style.display = 'none';
    checkInfoPageInput();
});
