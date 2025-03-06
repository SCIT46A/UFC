$(document).ready(function () {
    // 페이지 로드 시 초기화 함수 호출
    document.querySelector('.cam-la-in-box-bottom').setAttribute('data-target', 'info');

    let pageStatus = $('.cam-la-in-box-top-in-na-all-ul-li.check').attr('data-target');

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
    $(document).on('click', '.upda-tag-big', function(){
        $(this).toggleClass('tag-active');
        const tagName = $(this).text().trim();
        const duplicateTags = $(`.upda-tag-big[data-tag="${tagName}"]`).toArray();
        
        if($(this).hasClass('tag-active')){
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

    $(document).on('keyup', '.upda-tag-custom > input[type="text"]', function(e){
        // Enter 키 입력 시 태그 저장
        if (e.key === 'Enter') {
            const inputValue = $(this).val().trim();
            // 이미 존재하는 태그인지 HTML에서도 확인
            const existingTags = $('.upda-tag-big').map(function() {
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
        if(inputValue.length > 0){
            // HTML에서 현재 존재하는 모든 태그 확인
            const existingTags = $('.upda-tag-big').map(function() {
                return $(this).text().trim();
            }).get();
            
            if(existingTags.includes(inputValue)) {
                // 중복된 태그인 경우
                $(this).parent().removeClass('tag-active').addClass('tag-duplicate');
                $(this).css('color', '#ff4444');
                if(!$(this).next('.duplicate-tooltip').length) {
                    $(this).after('<span class="duplicate-tooltip">이미 존재하는 태그입니다</span>');
                }
            } else {
                $(this).parent().addClass('tag-active').removeClass('tag-duplicate');
                $(this).css('color', '');
                $(this).next('.duplicate-tooltip').remove();
            }
        }else{
            $(this).parent().removeClass('tag-active tag-duplicate');
            $(this).css('color', '');
            $(this).next('.duplicate-tooltip').remove();
        }
    });
    let editTag = '';
    $(document).on('focusout', '.upda-tag-custom > input[type="text"]', function(){
        const inputValue = $(this).val().trim();
        
        // 빈 값이거나 이미 존재하는 태그인 경우 입력 필드 삭제
        if(inputValue === '' || tagList.includes(inputValue)) {
            $(this).closest('li').remove();
            return;
        }
        
        if($(this).parent().hasClass('tag-active')){
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
    $(document).on('click', '.tagDelete', function(e){
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

    // 오늘 날짜 구하기
    const today = new Date();
    
    // min 날짜 설정 (내일부터 선택 가능)
    const minDate = new Date(today);
    minDate.setDate(today.getDate() + 1);
    minDate.setHours(0, 0, 0, 0);
    
    // 날짜 형식 변환 함수 (YYYY-MM-DD)
    function formatDate(date) {
        const year = date.getFullYear();
        const month = String(date.getMonth() + 1).padStart(2, '0');
        const day = String(date.getDate()).padStart(2, '0');
        return `${year}-${month}-${day}`;
    }
    
    // 기존 날짜 값 유지 함수
    function initDateFields() {
        // 각 입력 필드에 이미 값이 있는지 확인
        if (fundingStartDate && fundingStartDate.value) {
            console.log("시작일 값 있음:", fundingStartDate.value);
            const startDate = new Date(fundingStartDate.value);
            
            // 종료일과 발송일이 유효한지 확인
            if (fundingEndDate && fundingEndDate.value) {
                const endDate = new Date(fundingEndDate.value);
                calculateFundingPeriod();
                
                // 발송일이 있으면 계산
                if (fundingSendDate && fundingSendDate.value) {
                    calculatePreparePeriod();
                }
            }
        } else {
            // 값이 없으면 최소 날짜 설정
            const minDateStr = formatDate(minDate);
            fundingStartDate.setAttribute('min', minDateStr);
            
            // 기본값 (선택사항)
            // fundingStartDate.value = minDateStr;
        }
        
        // 입력 필드에 이벤트 리스너 추가
        $(document).on('change', '#funding-start-datetime, #funding-end-datetime, #funding-send-datetime', checkFundingPageInput);
    }
    
    // 페이지 로드 시 초기화
    window.addEventListener('DOMContentLoaded', function() {
        initDateFields();
        
        // 디버그 정보 출력
        console.log("시작일:", fundingStartDate ? fundingStartDate.value : "없음");
        console.log("종료일:", fundingEndDate ? fundingEndDate.value : "없음");
        console.log("발송일:", fundingSendDate ? fundingSendDate.value : "없음");
    });
    
    // 펀딩 일정 페이지 입력 감지
    function checkFundingPageInput() {
        const currentPage = $('.cam-la-in-box-top-in-na-all-ul-li.check').attr('data-target');
        if(currentPage !== 'funding') return;
        
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
            fundingEndDate.value = formatDate(maxEndDate);
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
            fundingSendDate.value = formatDate(maxSendDate);
            preparePeriod.textContent = '60일';
        } else {
            preparePeriod.textContent = diffDays + '일';
        }
    }
    
    // 날짜 입력 시 유효성 검사 및 자동 계산
    fundingStartDate.addEventListener('change', function() {
        const startDate = new Date(this.value);
        
        // 과거 날짜 체크 (새 값이 설정될 때만)
        if (this.value && startDate < minDate) {
            showAlert('시작일은 내일 이후로 설정해야 합니다.');
            this.value = '';
            $('.date-from-today').text('');
            checkFundingPageInput();
            return;
        }
        
        // 오늘로부터 몇 일 후인지 계산
        if (this.value) {
            const calcToday = new Date();
            calcToday.setHours(0, 0, 0, 0);
            const diffTime = startDate - calcToday;
            const diffDays = Math.ceil(diffTime / (1000 * 60 * 60 * 24));
            
            if (diffDays === 1) {
                $('.date-from-today').text('내일부터');
            } else {
                $('.date-from-today').text(`오늘로부터 ${diffDays}일 후`);
            }
            
            // 종료일 제한 업데이트
            if (fundingEndDate) {
                const minEndDate = new Date(startDate);
                minEndDate.setDate(startDate.getDate() + 1);
                const minEndDateStr = formatDate(minEndDate);
                fundingEndDate.setAttribute('min', minEndDateStr);
                
                // 종료일이 시작일보다 빠르면 리셋
                if (fundingEndDate.value && new Date(fundingEndDate.value) <= startDate) {
                    fundingEndDate.value = '';
                    checkFundingPageInput();
                }
            }
        }
        
        // 펀딩 기간 재계산
        calculateFundingPeriod();
    });
    
    fundingEndDate.addEventListener('change', function() {
        if (!this.value) return;
        
        const endDate = new Date(this.value);
        const startDate = fundingStartDate.value ? new Date(fundingStartDate.value) : null;
        
        // 시작일이 설정되지 않은 경우
        if (!startDate) {
            showAlert('먼저 시작일을 설정해주세요.');
            this.value = '';
            checkFundingPageInput();
            return;
        }
        
        // 시작일 이전으로 설정한 경우
        if (endDate <= startDate) {
            showAlert('종료일은 시작일 이후로 설정해야 합니다.');
            this.value = '';
            checkFundingPageInput();
            return;
        }
        
        // 펀딩 기간 재계산
        calculateFundingPeriod();
        
        // 발송일 제한 업데이트
        if (fundingSendDate) {
            const minSendDate = new Date(endDate);
            minSendDate.setDate(endDate.getDate() + 1);
            fundingSendDate.setAttribute('min', formatDate(minSendDate));
            
            // 발송일이 종료일보다 빠르면 리셋
            if (fundingSendDate.value && new Date(fundingSendDate.value) <= endDate) {
                fundingSendDate.value = '';
                preparePeriod.textContent = '최대 60일';
                checkFundingPageInput();
            } else if (fundingSendDate.value) {
                // 리워드 준비 기간 재계산
                calculatePreparePeriod();
            }
        }
    });
    
    fundingSendDate.addEventListener('change', function() {
        if (!this.value) return;
        
        const sendDate = new Date(this.value);
        const endDate = fundingEndDate.value ? new Date(fundingEndDate.value) : null;
        
        // 종료일이 설정되지 않은 경우
        if (!endDate) {
            showAlert('먼저 종료일을 설정해주세요.');
            this.value = '';
            checkFundingPageInput();
            return;
        }
        
        // 종료일 이전으로 설정한 경우
        if (sendDate <= endDate) {
            showAlert('발송일은 종료일 이후로 설정해야 합니다.');
            this.value = '';
            checkFundingPageInput();
            return;
        }
        
        // 리워드 준비 기간 재계산
        calculatePreparePeriod();
    });
// 2. 펀딩 일자 END

// 3. 리워드 구성
    // 펀딩 계획 페이지 기부품 추가 버튼 클릭 시 기부품 추가 필드 제공

    let fundingItems = []; // [{name: "", amount: 0}] rewardList.funding의 name별 amount를 더하여 표현
    let rewardItems = []; //[{"name": '', "amount": ''}] rewardList.reward의 name별 amount를 더하여 표현
    let rewardList = {"name":"", "amount":0, "funding": [], "reward": []};
    // 임시 리워드 목록 저장 (Thymeleaf에서 가져온 데이터)
    let tmpRewardList = [];

    // 페이지 로드 시 Thymeleaf 데이터를 자바스크립트로 가져오기
    $(document).ready(function() {
        // 리워드 데이터 초기화
        initializeRewardData();
        
        // 리워드 버튼 클릭 이벤트 설정
        setupRewardButtonEvents();
    });
    
    // Thymeleaf에서 리워드 데이터 가져와서 초기화
    function initializeRewardData() {
        // 각 리워드 요소를 순회하면서 데이터 추출
        $('.cam-gi-box-le-in-bo-pe').each(function(index) {
            const rewardElement = $(this);
            
            // 리워드 ID 설정 (없으면 새로 생성)
            const rewardId = rewardElement.data('reward-id') || 'reward-' + index;
            rewardElement.attr('data-reward-id', rewardId);
            
            // 리워드 이름
            const name = rewardElement.find('.cam-gi-box-le-in-bo-pe-content-title').text().trim();
            
            // 리워드 수량
            const amountText = rewardElement.find('.cam-gi-em-box em').text();
            const amount = parseInt(amountText.replace('개 남음', '').trim());
            
            // 재료(funding) 항목 추출
            const funding = [];
            rewardElement.find('.cam-gi-box-le-in-bo-pe-content-funding').each(function() {
                const fundingTexts = $(this).find('b');
                if (fundingTexts.length >= 2) {
                    const fundingName = fundingTexts.eq(0).text().trim();
                    const fundingAmount = parseInt(fundingTexts.eq(1).text().trim());
                    funding.push({
                        name: fundingName,
                        amount: fundingAmount
                    });
                }
            });
            
            // 리워드 항목(reward) 추출
            const reward = [];
            rewardElement.find('.cam-gi-box-le-in-bo-pe-content-reward').each(function() {
                const spans = $(this).find('span');
                if (spans.length >= 2) {
                    const itemName = spans.eq(0).text().trim();
                    const itemAmount = parseInt(spans.eq(1).text().trim());
                    reward.push({
                        name: itemName,
                        amount: itemAmount
                    });
                }
            });
            
            // 리워드 객체 생성
            const rewardData = {
                id: rewardId,
                name: name,
                amount: amount,
                funding: funding,
                reward: reward
            };
            
            // 임시 리워드 목록에 추가
            tmpRewardList.push(rewardData);
        });
        
        console.log('초기화된 리워드 목록:', tmpRewardList);
        
        // 초기 리워드 개수 업데이트
        updateRewardCount();
    }
    
    // 리워드 버튼 클릭 이벤트 설정
    function setupRewardButtonEvents() {
        // 리워드 항목 버튼 클릭 이벤트
        $(document).on('click', '.cam-gi-box-le-in-bo-pe-content', function() {
            const rewardItem = $(this).closest('.cam-gi-box-le-in-bo-pe');
            const rewardId = rewardItem.data('reward-id');
            
            // ID로 리워드 데이터 찾기
            const rewardData = tmpRewardList.find(r => r.id === rewardId);
            
            if (rewardData) {
                // 입력 폼에 데이터 채우기
                loadRewardToForm(rewardData);
                
                // 버튼 요소 제거 및 애니메이션 효과
                rewardItem.fadeOut(300, function() {
                    $(this).remove();
                    // 리워드 개수 업데이트
                    updateRewardCount();
                });
                
                // 리워드 데이터를 rewardList에 복사
                rewardList = {
                    name: rewardData.name,
                    amount: rewardData.amount,
                    funding: [...rewardData.funding],
                    reward: [...rewardData.reward]
                };
            }
        });
    }
    
    // 리워드 정보를 폼에 로드하는 함수
    function loadRewardToForm(rewardData) {
        // 리워드 이름과 수량 설정
        $('.gi-se-input-va[placeholder="멋진 이름을 붙여주세요!"]').val(rewardData.name);
        $('.gi-se-input-va[placeholder="0"]').val(rewardData.amount);
        
        // 기존 재료 항목 초기화
        $('.funding-item-container').empty();
        
        // 재료 항목 추가
        rewardData.funding.forEach(item => {
            addFundingItem(item.name, item.amount);
        });
        
        // 기존 리워드 항목 초기화
        $('.reward-item-container').empty();
        
        // 리워드 항목 추가
        rewardData.reward.forEach(item => {
            addRewardItem(item.name, item.amount);
        });
        
        console.log('폼에 로드된 리워드:', rewardData);
    }
    
    // 리워드 개수 업데이트 함수
    function updateRewardCount() {
        const count = $('.cam-gi-box-le-in-bo-pe').length;
        $('#rewardCount').text(count);
    }
    
    // 재료 항목 추가 함수
    function addFundingItem(name, amount) {
        const fundingItemTemplate = `
            <div class="gi-se-input-box funding-item">
                <input type="text" class="gi-se-input-va" placeholder="ex: 손코팅 엽서" value="${name || ''}" />
                <input type="number" class="gi-se-input-va" placeholder="0" value="${amount || ''}" />
                <button class="gi-se-del">삭제</button>
            </div>
        `;
        
        $('.funding-item-container').append(fundingItemTemplate);
    }
    
    // 리워드 항목 추가 함수
    function addRewardItem(name, amount) {
        const rewardItemTemplate = `
            <div class="gi-se-input-box reward-item">
                <input type="text" class="gi-se-input-va" placeholder="ex: 뱃지" value="${name || ''}" />
                <input type="number" class="gi-se-input-va" placeholder="0" value="${amount || ''}" />
                <button class="gi-se-del">삭제</button>
            </div>
        `;
        
        $('.reward-item-container').append(rewardItemTemplate);
    }
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
            sendData.userName = document.querySelector(".header-box-top-pe-my-in-name").textContent; // 헤더의 사용자 이름(검토 필요)

            console.log(JSON.stringify(sendData));

            // 로딩 표시
            showLoading();

            // 이미지 파일 가져오기
            const imageFile = imageInput.files[0];
            
            if (!imageFile) {
                alert('캠페인 이미지가 등록되지 않았습니다.');
                return;
            }else{
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
                window.location.href = '/campaign/' + response.campaignId;
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
    function checkFirstPageInput(){
        console.log(tagList.length, campaignTitle.value, campaignDescription.value, imageInput.files.length);
        if(tagList.length > 0&&campaignTitle.value != ''&&campaignDescription.value != ''&&imageInput.files.length > 0){
            $('.cam-la-in-box-bottom-in-end-btn').removeAttr('disabled');
        }else{
            $('.cam-la-in-box-bottom-in-end-btn').attr('disabled', true);
        }
    }
    // 하단 버튼 컨트롤러
    $(".cam-la-in-box-bottom-in-end-btn-span").text('다음');
    // 캠페인 기획 페이지 다음 버튼 -> 약관 동의 시작하기는 0.약관 동의에서 처리중
    $(document).on('click','.cam-la-in-box-bottom-in-end-btn', function(){
        const navTarget = $('.cam-la-in-box-top-in-na-all-ul-li.check');
        let navName = navTarget.attr('data-target');
        if(navName == 'info'){  // info에서 다음 버튼 클릭 시 처리
            navTarget.removeClass('check');
            navTarget.next().addClass('check');
            $('.cam-la-in-box-bo.info').addClass('hidden');
            $('.cam-la-in-box-bo.funding').removeClass('hidden'); 
            checkCurrentPageInputs('funding');
        }else if(navName == 'funding'){  // funding에서 다음 버튼 클릭 시 처리
            navTarget.removeClass('check');
            navTarget.next().addClass('check');
            $('.cam-la-in-box-bo.funding').addClass('hidden');
            $('.cam-la-in-box-bo.reward').removeClass('hidden'); 
            checkCurrentPageInputs('reward');
        }else if(navName == 'reward'){  // reward에서 다음 버튼 클릭 시 처리
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
    $('.cam-la-in-box-bottom-in-end-btn').on('click', function() {
        if ($(this).find('.cam-la-in-box-bottom-in-end-btn-span').text() === '캠페인 수정') {
            submitCampaign();
        }
    });

    // 캠페인 기획 페이지 뒤로가기 버튼
    $(document).on('click','.cam-la-in-box-bottom-in-back', function(){
        // 캠페인 기획 페이지가 보이는 중에 눌리면 약관 동의 페이지가 나오도록
        const navTarget = $('.cam-la-in-box-top-in-na-all-ul-li.check');
        let navName = navTarget.attr('data-target');
        if(navName == 'info'){    
            //$('.cam-la').addClass('hidden');
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
        pageStatus = $('.cam-la-in-box-top-in-na-all-ul-li.check').attr('data-target');
        console.log(pageStatus);
    });
// 하단 버튼 컨트롤러 END

    // 각 페이지별 입력 필드 변경 감지
    // 기본 정보 페이지 입력 감지
    $(document).on('input', 'input[name="title"]', checkInfoPageInput);
    $(document).on('input', '.cam-la-in-box-bo-all-de-div-box-textarea', checkInfoPageInput);
    $(document).on('click', checkInfoPageInput);
    $(document).on('keydown', checkInfoPageInput);
    $(document).on('change', '#campaignImageInput', function() {
        const previewImage = document.getElementById('previewImage');
        const file = this.files[0];
        
        if (file) {
            const reader = new FileReader();
            reader.onload = function(e) {
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
        if(currentPage !== 'info') return;
        
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
        
        

        if(rewardList.reward.length > 0){
            $('.cam-la-in-box-bottom-in-end-btn').removeAttr('disabled').addClass('isActive');
        } else {
            $('.cam-la-in-box-bottom-in-end-btn').attr('disabled', true).removeClass('isActive');
        }
    }
    
    // 페이지 전환 시 해당 페이지의 입력 상태 확인
    function checkCurrentPageInputs(navName) {
        switch(navName) {
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
                const multiplier = parseInt(rewardItem.amount); // 해당 리워드의 생산 수량
                
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
            
            // Object를 배열로 변환
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
    $(document).on('click', '.cam-la-in-box-bottom-in-end-btn', function() {
        if ($('.cam-la-in-box-top-in-na-all-ul-li.check').attr('data-target') === 'reward') {
            console.log('Moving to final page');
            console.log('Current rewardItems:', JSON.stringify(rewardItems, null, 2));
            updateFinalSummary();
        }
    });

    // 리워드 추가 버튼 클릭 시
    $('.reward-add-btn').on('click', function() {
        // 리워드 이름과 수량 유효성 검사
        const rewardName = $('.gi-se-input-va[placeholder="멋진 이름을 붙여주세요!"]').val();
        const rewardAmount = $('.gi-se-input-va[placeholder="0"]').val();
        
        if (!rewardName || !rewardAmount || rewardAmount <= 0) {
            showAlert('리워드 이름과 수량을 올바르게 입력해주세요.');
            return;
        }
        
        // 리워드 추가 처리...
        
        // 리워드 개수 업데이트
        updateRewardCount();
        
        // 리워드 폼 초기화
        clearRewardForm();
    });
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

// 전역 변수 선언
let imageInput, previewImage, deleteButton, previewContainer;

// 페이지별 입력 검사 함수
function checkInfoPageInput() {
    const currentPage = $('.cam-la-in-box-top-in-na-all-ul-li.check').attr('data-target');
    if(currentPage !== 'info') return;
    
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

// 처음에 미리보기 보이기
previewImage.style.display = 'flex';
document.querySelector('.cam-img-re').style.display = 'flex';

// 이미지 업로드 이벤트
imageInput.addEventListener('change', async function(e) {
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
$(deleteButton).on('click', function(e) {
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
