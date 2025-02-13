$(document).ready(function () {
    
// 0. 약관, 동의
    const startBtn = document.querySelector(".cam-create-btn");
    $(document).on('click', function(){
        if($('#fi').is(':checked')&&$('#ce').is(':checked')&&$('#th').is(':checked')){
            startBtn.classList.add("isActive");
            startBtn.disabled = false;
        }else{
            startBtn.classList.remove("isActive");
            startBtn.disabled = true;
        }
    });

    // 캠페인 생성 동의 체크박스 검사, 시작하기 버튼 활성화
    $('.cam-create-btn').on('click', function(){
        $('.cam-ag-box').addClass('hidden');
        $('.cam-la').removeClass('hidden');
    });
// 약관, 동의 END

// 1. 캠페인 기본정보 (캠페인 타이틀 입력 + 태그 추가 + 캠페인 소개 작성)

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
    $(document).on('click', '.upda-tag-big', function(){
        $(this).toggleClass('tag-active');
        const tagName = $(this).text();
        if($(this).hasClass('tag-active')){
            tagList.push(tagName);

        }else{
            tagList = tagList.filter(tag => tag !== tagName);
        }
        //console.log(tagList);
    });

    $(document).on('keyup', '.upda-tag-custom > input', function(){
        if($(this).val().length > 0){
            $(this).parent().addClass('tag-active');
        }else{
            $(this).parent().removeClass('tag-active');
        }
    });
    let editTag = '';
    $(document).on('focusout', '.upda-tag-custom > input', function(){
        const inputValue = $(this).val().trim();
        
        // 빈 값이거나 이미 존재하는 태그인 경우 입력 필드 삭제
        if(inputValue === '' || tagList.includes(inputValue)) {
            $(this).closest('li').remove();
            return;
        }
        
        if($(this).parent().hasClass('tag-active')){
            tagList.push(inputValue);
            editTag = inputValue;
        }
        console.log(tagList);
    });
    $(document).on('focusin', '.upda-tag-custom > input', function(){
        const tagName = $(this).val().trim();
        tagList = tagList.filter(tag => tag !== tagName);
        editTag = '';
        console.log('Focus in - current tagList:', tagList);
    });


    // 태그 추가 버튼 클릭 시 태그 추가 필드 제공
    $(tagAdd).on('click', function(){
        // 현재 입력 중인 태그 필드가 있는지 확인
        const existingEmptyInput = $('.upda-tag-custom > input[type="text"]').filter(function() {
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
    $(document).on('click', '.tagDelete', function(){
        const tagName = $(this).prev().val().trim();
        $(this).closest('li').remove();  // 또는 $(this).parent().parent().remove();
        tagList = tagList.filter(tag => tag !== tagName);
        console.log('Tag deleted - current tagList:', tagList);
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
    // KST로 변환 (UTC+9)
    const kstOffset = 9 * 60 * 60 * 1000; // 9시간을 밀리초로 변환
    const todayKST = new Date(today.getTime() + kstOffset);
    
    const tomorrow = new Date(today);
    tomorrow.setDate(today.getDate() + 1);
    tomorrow.setHours(0, 0, 0, 0);  // 다음날 00:00:00으로 설정
    
    // KST 시간대로 문자열 변환
    function toKSTString(date) {
        const year = date.getFullYear();
        const month = String(date.getMonth() + 1).padStart(2, '0');
        const day = String(date.getDate()).padStart(2, '0');
        return `${year}-${month}-${day}T00:00`;
    }
    
    const minDateTime = toKSTString(tomorrow);
    
    // 종료일 (시작일 + 30일) 계산
    const defaultEndDate = new Date(tomorrow);
    defaultEndDate.setDate(tomorrow.getDate() + 30);
    
    // 발송일 (종료일 + 7일) 계산
    const defaultSendDate = new Date(defaultEndDate);
    defaultSendDate.setDate(defaultEndDate.getDate() + 7);
    
    // 시작일 제한 설정 (오늘 이후로만 설정 가능)
    fundingStartDate.setAttribute('min', minDateTime);
    
    // 기본값 설정
    fundingStartDate.value = toKSTString(tomorrow);
    fundingEndDate.value = toKSTString(defaultEndDate);
    fundingSendDate.value = toKSTString(defaultSendDate);
    
    // 초기 기간 표시 업데이트
    fundingPeriod.textContent = '30일';
    preparePeriod.textContent = '7일';
    
    // 날짜 입력 시 유효성 검사 및 자동 계산
    fundingStartDate.addEventListener('change', function() {
        const startDate = new Date(this.value);
        
        // 과거 날짜 체크
        if (startDate < tomorrow) {
            showAlert('시작일은 내일 이후로 설정해야 합니다.');
            this.value = '';
            return;
        }
        
        // 종료일 제한 업데이트
        if (fundingEndDate) {
            fundingEndDate.setAttribute('min', this.value);
            
            // 이미 설정된 종료일이 시작일보다 이전인 경우
            if (new Date(fundingEndDate.value) < startDate) {
                fundingEndDate.value = '';
            }
        }
    });
    
    fundingEndDate.addEventListener('change', function() {
        const endDate = new Date(this.value);
        const startDate = new Date(fundingStartDate.value);
        
        // 시작일이 설정되지 않은 경우
        if (!fundingStartDate.value) {
            showAlert('먼저 시작일을 설정해주세요.');
            this.value = '';
            return;
        }
        
        // 시작일 이전으로 설정한 경우
        if (endDate < startDate) {
            showAlert('종료일은 시작일 이후로 설정해야 합니다.');
            this.value = '';
            return;
        }
        
        // 펀딩 기간 계산 및 표시 (일 수)
        const diffTime = Math.abs(endDate - startDate);
        const diffDays = Math.ceil(diffTime / (1000 * 60 * 60 * 24));
        
        // 60일 초과 체크
        if (diffDays > 60) {
            showAlert('펀딩 기간은 최대 60일을 초과할 수 없습니다.');
            this.value = '';
            return;
        }
        
        fundingPeriod.textContent = diffDays + '일';
        
        // 발송일 제한 업데이트
        if (fundingSendDate) {
            fundingSendDate.setAttribute('min', this.value);
            
            // 이미 설정된 발송일이 종료일보다 이전인 경우
            if (new Date(fundingSendDate.value) < endDate) {
                fundingSendDate.value = '';
            }
        }
    });
    
    fundingSendDate.addEventListener('change', function() {
        const sendDate = new Date(this.value);
        const endDate = new Date(fundingEndDate.value);
        
        // 종료일이 설정되지 않은 경우
        if (!fundingEndDate.value) {
            showAlert('먼저 종료일을 설정해주세요.');
            this.value = '';
            return;
        }
        
        // 종료일 이전으로 설정한 경우
        if (sendDate < endDate) {
            showAlert('발송 예정일은 종료일 이후로 설정해야 합니다.');
            this.value = '';
            return;
        }
        
        // 준비 기간 계산 및 표시 (일 수)
        const diffTime = Math.abs(sendDate - endDate);
        const diffDays = Math.ceil(diffTime / (1000 * 60 * 60 * 24));
        
        // 60일 초과 체크
        if (diffDays > 60) {
            showAlert('리워드 준비 기간은 최대 60일을 초과할 수 없습니다.');
            // 60일 후의 날짜로 자동 설정
            const maxSendDate = new Date(endDate);
            maxSendDate.setDate(endDate.getDate() + 60);
            this.value = maxSendDate.toISOString().slice(0, 16);
            preparePeriod.textContent = '60일';
            return;
        }
        
        preparePeriod.textContent = diffDays + '일';
    });
    
    // 알림 표시 함수
    function showAlert(message) {
        const alertBox = document.createElement('div');
        alertBox.className = 'date-alert';
        alertBox.innerHTML = `
            <div class="date-alert-content">
                <span class="date-alert-icon">⚠️</span>
                <span class="date-alert-message">${message}</span>
            </div>
        `;
        
        document.body.appendChild(alertBox);
        
        // 애니메이션 효과를 위한 지연
        setTimeout(() => alertBox.classList.add('show'), 100);
        
        // 3초 후 알림 제거
        setTimeout(() => {
            alertBox.classList.remove('show');
            setTimeout(() => alertBox.remove(), 300);
        }, 3000);
    }
// 2. 펀딩 일자 END

// 3. 리워드 구성
    // 펀딩 계획 페이지 기부품 추가 버튼 클릭 시 기부품 추가 필드 제공
    let fundingItems = []; //[{"name": '', "amount": ''}] rewardList.funding의 name별 amount를 더하여 표현(1.1배 증가 예정)
    let rewardList = {"reward": [], "funding": []}; //{"reward": [{"name": "", "amount": 0}], "funding": [{"name": "", "amount": 0}]}

    // 기부품 이름 및 수량 입력 시 버튼 활성화
    $(document).on('keyup', '.fundingItemInput, .fundingAmountInput', activeItemFundingBtn);
    function activeItemFundingBtn(){
        let fundingItem = $(this).closest('div').parent().find('.fundingItemInput');
        let fundingAmount = $(this).closest('div').parent().find('.fundingAmountInput');
        
        if((fundingItem.val() != '' && fundingAmount.val() != '') && !rewardList.funding.some(item => item.name === fundingItem.val())){
            $('.cam-la-pay-btn.funding').addClass('isActive');
            $('.cam-la-pay-btn.funding').removeAttr('disabled');
        }else{
            $('.cam-la-pay-btn.funding').removeClass('isActive');
            $('.cam-la-pay-btn.funding').attr('disabled', true);
        }
    }
    
    $(document).on('keyup', '.rewardItemInput, .rewardAmountInput', activeItemRewardBtn);
    function activeItemRewardBtn(){
        let rewardItem = $(this).closest('div').parent().find('.rewardItemInput');
        let rewardAmount = $(this).closest('div').parent().find('.rewardAmountInput');
        
        if((rewardItem.val() != '' && rewardAmount.val() != '') && !rewardList.reward.some(item => item.name === rewardItem.val())){
            $('.cam-la-pay-btn.reward').addClass('isActive');
            $('.cam-la-pay-btn.reward').removeAttr('disabled');
        }else{
            $('.cam-la-pay-btn.reward').removeClass('isActive');
            $('.cam-la-pay-btn.reward').attr('disabled', true);
        }
    }
    
    // 기부품 추가 버튼 클릭 시 기부품 추가 
    $(document).on('click', '.cam-la-pay-btn.funding', addFundingItem);
    function addFundingItem(){
        let fundingItem = $(this).closest('div').find('.fundingItemInput');
        let fundingAmount = $(this).closest('div').find('.fundingAmountInput');
        
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
        rewardList.funding.push({name: fundingItem.val(), amount: fundingAmount.val()});
        console.log('Funding items:', rewardList.funding);
        $('.cam-la-pay-box-total.funding').parent().append(fundingTag);
        fundingItem.val('');
        fundingAmount.val('');
        $('.cam-la-pay-btn.funding').removeClass('isActive');
        $('.cam-la-pay-btn.funding').attr('disabled', true);
    }
    
    // 리워드 추가 버튼 클릭 시 리워드 추가
    $(document).on('click', '.cam-la-pay-btn.reward', addRewardItem);
    function addRewardItem(){
        let rewardItem = $(this).closest('div').find('.rewardItemInput');
        let rewardAmount = $(this).closest('div').find('.rewardAmountInput');
        
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
        rewardList.reward.push({name: rewardItem.val(), amount: rewardAmount.val()});
        console.log('Reward items:', rewardList.reward);
        $('.cam-la-pay-box-total:not(.funding)').parent().append(rewardTag);
        rewardItem.val('');
        rewardAmount.val('');
        $('.cam-la-pay-btn.reward').removeClass('isActive');
        $('.cam-la-pay-btn.reward').attr('disabled', true);
    }

    // 아이템 삭제 버튼 클릭 시 추가된 필드 삭제 (이벤트 위임 사용)
    $(document).on('click', '.cam-tag-sh-pe-btn', function(){
        let itemName = $(this).closest('div').find('.cam-la-pay-box-pe-fname').text();
        let itemAmount = $(this).closest('div').find('.cam-la-pay-box-pe-famount').text();
        if($(this).closest('div').prev().hasClass('funding')){
            rewardList.funding = rewardList.funding.filter(item => item.name != itemName || item.amount != itemAmount);
        }else{  //$(this).closest('div').prev().hasClass('reward')
            rewardList.reward = rewardList.reward.filter(item => item.name != itemName || item.amount != itemAmount);
        }
        //console.log(fundingItems);
        $(this).closest('div').remove();  // 또는 $(this).parent().parent().remove();
    });


    // 리워드 태그 변수
    let rewardTitle = $('.gi-se-input-va');
    let rewardTargetName = '';
    let rewardTargetAmount = '';
    let rewardSendDate = '';
    let rewardSelectedCount = 0; // 기본값을 0으로 설정
    let rewardLeftAmount = $("#productionAmount").val(); // 기본값을 0으로 설정

    // 리워드 태그 추가 함수
    function addRewardItemField() {
        const rewardItemField = `
            <div class="cam-gi-box-le-in-bo-pe">
                <div>
                    <button class="cam-gi-box-le-in-bo-pe-content">
                        <strong>${rewardTargetName} ${rewardTargetAmount}개+</strong>
                        <p>${rewardTitle}</p>
                        <ul>
                            <li>${rewardItem} ${rewardAmount}개</li>
                        </ul>
                        <span>예상 발송 시작일: <em>${rewardSendDate}</em></span>
                        <div class="cam-gi-box-le-in-bo-pe-content-div">
                            <em class="cam-gi-em">
                                <div class="cam-gi-em-in">
                                    <svg viewBox="0 0 48 48">
                                        <path fill-rule="evenodd" clip-rule="evenodd" d="M4.28544 5.00257L2.01916 2.73642C1.82521 2.54248 1.82974 2.23083 2.01598 2.02765C2.21448 1.81131 2.5294 1.8394 2.72795 2.02108L2.72969 2.02268L4.99738 4.2905L7.26357 2.02431C7.4575 1.83056 7.7691 1.83508 7.97226 2.02115C8.1886 2.21946 8.16077 2.53473 7.97878 2.73311L7.97723 2.73479L5.70945 5.00257L7.97564 7.26876C8.16953 7.46283 8.16504 7.77425 7.97884 7.97756L7.97724 7.9793L7.97557 7.98097C7.78164 8.17472 7.47008 8.17023 7.26691 7.98417L7.26519 7.98259L4.99738 5.71465L2.73129 7.981C2.53725 8.17469 2.22572 8.17025 2.02253 7.98417L2.01908 7.98101L2.01592 7.97756C1.82971 7.77425 1.82526 7.46279 2.01916 7.26872L4.28544 5.00257Z" fill="#6D6D6D"></path>
                                    </svg>
                                </div>
                                ${rewardSelectedCount}명이 선택
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
    }
// 3. 리워드 구성 END

    // 캠페인 생성 완료 시 AJAX로 데이터 전송
    let sendData = {};
    
    // Cloudflare Images 업로더 인스턴스 생성
    const imageUploader = new CloudflareImageUploader();
    
    // 이미지 업로드 후 캠페인 데이터 전송
    async function submitCampaign() {
        try {
            sendData.tagList = tagList;     // 캠페인 태그 리스트트
            sendData.title = document.querySelector("input[name='title']").value;        // 캠페인 제목
            sendData.content = document.querySelector("input[name='shortDescription']").value;    // 캠페인 설명
            sendData.goalAmount = goalAmount; // 캠페인 목표 총 재료 수량 리스트
            sendData.rewardList = rewardList; // 캠페인 목표 총 리워드 수량 리스트
            sendData.startDate = fundingStartDate.value; // 캠페인 시작일
            sendData.endDate = fundingEndDate.value;     // 캠페인 종료일
            sendData.sendDate = fundingSendDate.value;   // 캠페인 발송일
            
            // 이미지 파일 가져오기
            const imageInput = document.querySelector('input[name="image"]');
            const imageFile = imageInput.files[0];
            
            if (!imageFile) {
                alert('캠페인 이미지를 선택해주세요.');
                return;
            }
            
            // 로딩 표시
            showLoading();
            
            // Cloudflare에 이미지 업로드
            const uploadResult = await imageUploader.uploadImage(imageFile);
            
            if (!uploadResult.success) {
                throw new Error('이미지 업로드에 실패했습니다.');
            }
            
            // 캠페인 데이터에 이미지 URL 추가
            sendData.imageUrl = uploadResult.imageUrl;
            sendData.imageId = uploadResult.imageId;
            
            // 서버로 캠페인 데이터 전송
            const response = await $.ajax({
                url: '/api/campaigns',
                type: 'POST',
                contentType: 'application/json',
                data: JSON.stringify(sendData)
            });
            
            // 성공 시 처리
            if (response.success) {
                alert('캠페인이 성공적으로 등록되었습니다.');
                window.location.href = '/campaigns/' + response.campaignId;
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
    
    // 캠페인 생성 버튼 클릭 이벤트
    $('.cam-la-in-box-bottom-in-end-btn').on('click', function() {
        if ($(this).find('.cam-la-in-box-bottom-in-end-btn-span').text() === '캠페인 생성') {
            submitCampaign();
        }
    });

    // 하단 버튼 컨트롤러
    $(".cam-la-in-box-bottom-in-end-btn-span").text('다음');
    // 캠페인 기획 페이지 다음 버튼
    $(document).on('click','.cam-la-in-box-bottom-in-end-btn', function(){
        const navTarget = $('.cam-la-in-box-top-in-na-all-ul-li.check');
        let navName = navTarget.attr('data-target');
        if(navName == 'info'){    
            navTarget.removeClass('check');
            navTarget.next().addClass('check');
            $('.cam-la-in-box-bo.info').addClass('hidden');
            $('.cam-la-in-box-bo.funding').removeClass('hidden'); 
        }else if(navName == 'funding'){
            navTarget.removeClass('check');
            navTarget.next().addClass('check');
            $('.cam-la-in-box-bo.funding').addClass('hidden');
            $('.cam-la-in-box-bo.reward').removeClass('hidden'); 
        }else if(navName == 'reward'){
            navTarget.removeClass('check');
            navTarget.next().addClass('check');
            $('.cam-la-in-box-bo.reward').addClass('hidden');
            $('.cam-la-in-box-bo.final').removeClass('hidden');
            $(".cam-la-in-box-bottom-in-end-btn-span").text('캠페인 생성');
        }
        window.location.href = '#header';
    });

    // 캠페인 기획 페이지 뒤로가기 버튼
    $(document).on('click','.cam-la-in-box-bottom-in-back', function(){
        // 캠페인 기획 페이지가 보이는 중에 눌리면 약관 동의 페이지가 나오도록
        const navTarget = $('.cam-la-in-box-top-in-na-all-ul-li.check');
        let navName = navTarget.attr('data-target');
        if(navName == 'info'){    
            $('.cam-ag-box').removeClass('hidden'); // 약관 동의 페이지
            $('.cam-la').addClass('hidden');
        }else if(navName == 'funding'){
            navTarget.removeClass('check');
            navTarget.prev().addClass('check');
            $('.cam-la-in-box-bo.info').removeClass('hidden');
            $('.cam-la-in-box-bo.funding').addClass('hidden'); 
        }else if(navName == 'reward'){
            navTarget.removeClass('check');
            navTarget.prev().addClass('check');
            $('.cam-la-in-box-bo.funding').removeClass('hidden');
            $('.cam-la-in-box-bo.reward').addClass('hidden'); 
        }else if(navName == 'final'){
            navTarget.removeClass('check');
            navTarget.prev().addClass('check');
            $('.cam-la-in-box-bo.reward').removeClass('hidden');
            $('.cam-la-in-box-bo.final').addClass('hidden');
            $(".cam-la-in-box-bottom-in-end-btn-span").text('다음');
        }
    });
// 하단 버튼 컨트롤러 END
}); // ===================================== $(document).ready END =====================================

function activeNextBtn(){
    const nextBtn = document.querySelector(".next-btn");
    if(nextBtn.value.length > 0){
        nextBtn.classList.add("isActive");
    }else{
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
    input.addEventListener('input', function() {
        this.style.width = ((this.value.length + 1) * 10) + 'px';
    });
});

document.addEventListener("DOMContentLoaded", () => {
    const firstPage = document.querySelector(".cam-in");
    const firstPageBtn = document.querySelector(".cam-in-box-content-btn");
    const cePage = document.querySelector(".cam-ag");
    const cePageBtnNext = document.querySelector(".cam-create-btn");
    const cePageBtnBack = document.querySelector(".cam-back-btn");
    const thPage = document.querySelector(".cam-la");
    const thPageBtnBack = document.querySelector(".cam-la-in-box-top-in-back");
    const listItems = document.querySelectorAll(
        ".cam-la-in-box-top-in-na-all-ul-li"
    );
    const contentBoxes = document.querySelectorAll(".cam-la-in-box-bo");

    firstPageBtn.addEventListener("click", () => {
        firstPage.classList.add("hidden");
        cePage.classList.remove("hidden");
    });


    cePageBtnNext.addEventListener("click", () => {
        cePage.classList.add("hidden");
        thPage.classList.remove("hidden");
    });

    cePageBtnBack.addEventListener("click", () => {
        firstPage.classList.remove("hidden");
        cePage.classList.add("hidden");
    });

    thPageBtnBack.addEventListener("click", () => {
        thPage.classList.add("hidden");
        cePage.classList.remove("hidden");
    });

    listItems.forEach((item) => {
        item.addEventListener("click", function () {
            // 모든 li에서 'check' 클래스 제거
            listItems.forEach((li) => li.classList.remove("check"));

            // 클릭한 li에 'check' 클래스 추가
            this.classList.add("check");

            // 모든 content-box 숨기기
            contentBoxes.forEach((box) => box.classList.add("hidden"));

            // 선택한 li의 data-target 속성에 해당하는 content-box 보이기
            const targetClass = this.getAttribute("data-target");
            document
                .querySelector(`.cam-la-in-box-bo.${targetClass}`)
                .classList.remove("hidden");
        });
    });
});

// 이미지 미리보기 기능
document.addEventListener('DOMContentLoaded', function() {
    const imageInput = document.getElementById('campaignImageInput');
    const previewImage = document.getElementById('previewImage');
    const deleteButton = document.querySelector('.cam-img-re-box-btn-in');
    const previewContainer = document.querySelector('.cam-img-re-box-sh');
    const loadingSpinner = document.createElement('div');
    loadingSpinner.className = 'loading-spinner';
    loadingSpinner.style.display = 'none';
    previewContainer.appendChild(loadingSpinner);

    // 기본 이미지 설정
    const defaultImagePath = 'https://upda.store/images/fix/logo.png';
    previewImage.src = defaultImagePath;
    
    // 처음에는 미리보기 숨기기
    previewImage.style.display = 'none';
    document.querySelector('.cam-img-re').style.display = 'none';

    // 에러 메시지 컨테이너 생성
    const errorContainer = document.createElement('div');
    errorContainer.className = 'error-container';
    document.body.appendChild(errorContainer);

    // 파일 입력 이벤트
    imageInput.addEventListener('change', function(e) {
        const file = e.target.files[0];
        handleImageFile(file);
    });

    // 삭제 버튼 클릭 이벤트
    deleteButton.addEventListener('click', function(e) {
        e.preventDefault();  // 기본 동작 방지
        resetImage();
    });

    // 파일 유효성 검사
    function validateFile(file) {
        const validTypes = ['image/jpeg', 'image/png', 'image/gif'];
        
        if (!file) {
            showError('파일을 선택해주세요.');
            return false;
        }

        // 파일 형식 체크
        if (!validTypes.includes(file.type)) {
            showError('JPG, PNG, GIF 형식의 이미지만 업로드 가능합니다.');
            return false;
        }

        // 파일 크기 체크 (5MB)
        if (file.size > 5 * 1024 * 1024) {
            showError('파일 크기는 5MB 이하여야 합니다.');
            return false;
        }

        return true;
    }

    // 에러 메시지 표시
    function showError(message) {
        loadingSpinner.style.display = 'none';  // 스피너 즉시 중단
        
        const errorBox = document.createElement('div');
        errorBox.className = 'error-box';
        
        const errorIcon = document.createElement('span');
        errorIcon.className = 'error-icon';
        errorIcon.innerHTML = '⚠️';
        
        const errorMessage = document.createElement('span');
        errorMessage.className = 'error-message';
        errorMessage.textContent = message;
        
        errorBox.appendChild(errorIcon);
        errorBox.appendChild(errorMessage);
        
        // 기존 에러 메시지 제거
        while (errorContainer.firstChild) {
            errorContainer.removeChild(errorContainer.firstChild);
        }
        
        errorContainer.appendChild(errorBox);
        
        // 3초 후 에러 메시지 자동 제거
        setTimeout(() => {
            errorBox.classList.add('fade-out');
            setTimeout(() => {
                if (errorContainer.contains(errorBox)) {
                    errorContainer.removeChild(errorBox);
                }
            }, 300);
        }, 3000);
    }

    // 이미지 파일 처리 함수 수정
    function handleImageFile(file) {
        if (!file) return;

        // 로딩 시작
        loadingSpinner.style.display = 'block';
        
        // 파일 유효성 검사 (파일 형식만 체크)
        if (!validateFileType(file)) {
            resetImage();
            return;
        }

        // 이미지 압축 및 처리
        const reader = new FileReader();
        reader.onload = function(e) {
            const img = new Image();
            
            img.onload = function() {
                // 이미지 크기 검증
                if (img.width < 600 || img.height < 600) {
                    showError('이미지 크기는 600x600 픽셀 이상이어야 합니다.');
                    resetImage();
                    return;
                }

                // 5MB 이상인 경우 자동 압축
                if (file.size > 5 * 1024 * 1024) {
                    compressImage(img, file.type, 5 * 1024 * 1024);
                    return;
                }

                // 이미지 표시
                displayImage(img);
            };
            img.src = e.target.result;
        };
        reader.readAsDataURL(file);
    }

    // 파일 형식만 체크하는 함수
    function validateFileType(file) {
        const validTypes = ['image/jpeg', 'image/png', 'image/gif'];
        if (!validTypes.includes(file.type)) {
            showError('JPG, PNG, GIF 형식의 이미지만 업로드 가능합니다.');
            return false;
        }
        return true;
    }

    // 이미지 압축 함수
    function compressImage(img, fileType, maxSize) {
        const canvas = document.createElement('canvas');
        let width = img.width;
        let height = img.height;
        let quality = 0.9;
        
        canvas.width = width;
        canvas.height = height;
        const ctx = canvas.getContext('2d');
        ctx.drawImage(img, 0, 0, width, height);

        // 압축 품질을 조절하면서 maxSize 이하가 될 때까지 반복
        let dataUrl = canvas.toDataURL(fileType, quality);
        while (dataUrl.length > maxSize && quality > 0.1) {
            quality -= 0.1;
            dataUrl = canvas.toDataURL(fileType, quality);
        }

        // 압축된 이미지 표시
        previewImage.src = dataUrl;
        previewImage.style.display = 'block';
        loadingSpinner.style.display = 'none';
        
        // 업로드 영역 숨기기
        document.querySelector('.cam-la-in-box-bo-all-de-div-box-img').style.display = 'none';
    }

    // 이미지 표시 함수 수정
    function displayImage(img) {
        const aspectRatio = img.width / img.height;
        if (aspectRatio !== 1) {
            // 1:1 비율로 자동 크롭
            const size = Math.min(img.width, img.height);
            const canvas = document.createElement('canvas');
            canvas.width = size;
            canvas.height = size;
            const ctx = canvas.getContext('2d');
            ctx.drawImage(img, 
                (img.width - size) / 2, 
                (img.height - size) / 2, 
                size, size, 
                0, 0, size, size
            );
            previewImage.src = canvas.toDataURL();
        } else {
            previewImage.src = img.src;
        }

        // 미리보기 표시
        previewImage.style.display = 'block';
        document.querySelector('.cam-img-re').style.display = 'flex';
        loadingSpinner.style.display = 'none';
        
        // 업로드 영역 숨기기
        document.querySelector('.cam-la-in-box-bo-all-de-div-box-img').style.display = 'none';
    }

    // 이미지 리셋 함수 수정
    function resetImage() {
        imageInput.value = '';
        previewImage.src = defaultImagePath;
        document.querySelector('.cam-img-re').style.display = 'none';
        loadingSpinner.style.display = 'none';
        
        // 업로드 영역 다시 표시
        document.querySelector('.cam-la-in-box-bo-all-de-div-box-img').style.display = 'flex';
    }

    // 삭제 버튼 아이콘 복구
    if (deleteButton) {
        deleteButton.innerHTML = `
            <div>
                <svg viewBox="0 0 48 48">
                    <path fill-rule="evenodd" clip-rule="evenodd" 
                        d="M38.814 42.172C38.814 42.946 38.064 43.574 37.144 43.574H10.856C9.936 43.574 9.186 42.946 9.186 42.172V12.218H38.814V42.172ZM17.564 4.426L30.542 4.524V9.794H17.462L17.564 4.426ZM44.786 9.794H32.968V4.524C32.968 3.13 31.832 2 30.436 2H17.564C16.168 2 15.03 3.13 15.03 4.524V9.794H3.212C2.542 9.794 2 10.336 2 11.006C2 11.676 2.542 12.218 3.212 12.218H6.76V42.172C6.76 44.284 8.598 46 10.856 46H37.144C39.402 46 41.24 44.284 41.24 42.172V12.218H44.786C45.456 12.218 46 11.676 46 11.006C46 10.336 45.456 9.794 44.786 9.794ZM18.857 36.9338C19.527 36.9338 20.069 36.3918 20.069 35.7218V20.0738C20.069 19.4038 19.527 18.8618 18.857 18.8618C18.187 18.8618 17.645 19.4038 17.645 20.0738V35.7218C17.645 36.3918 18.187 36.9338 18.857 36.9338ZM30.3542 35.7218C30.3542 36.3918 29.8122 36.9338 29.1422 36.9338C28.4722 36.9338 27.9302 36.3918 27.9302 35.7218V20.0738C27.9302 19.4038 28.4722 18.8618 29.1422 18.8618C29.8122 18.8618 30.3542 19.4038 30.3542 20.0738V35.7218Z">
                    </path>
                </svg>
            </div>
        `;
    }
});
