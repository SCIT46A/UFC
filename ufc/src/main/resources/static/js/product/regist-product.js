let productTitle = document.querySelector("input[name='title']");        // 상품 제목
let productDescription = document.querySelector(".cam-la-in-box-bo-all-de-div-box-textarea");    // 상품 설명
let imageInput = document.getElementById('productImageInput');    // 이미지 입력
let productPrice = document.querySelector("input[name='price']");  // 상품 가격
let productStock = document.querySelector("input[name='stock']");  // 상품 재고 수량

let tagList = [];   // 선택된 태그 목록

$(document).ready(function () {

    let pageStatus = $('.cam-la-in-box-top-in-na-all-ul-li.check').attr('data-target');
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

// 4. 최종 확인

    // 이미지 업로드 후 캠페인 데이터 전송
    async function submitCampaign() {
        try {
            // 캠페인 생성 완료 시 AJAX로 데이터 전송
            let sendData = {};

            sendData.tagList = tagList;     // 캠페인 태그 리스트
            sendData.title = productTitle.value;   // 상품 제목
            sendData.description = productDescription.value;    // 상품 설명
            sendData.price = parseInt(productPrice.value);     // 상품 가격
            sendData.stock = parseInt(productStock.value);     // 상품 재고 수량
            
            sendData.userName = document.querySelector(".header-box-top-pe-my-in-name").textContent; // 헤더의 사용자 이름(검토 필요)

            console.log(JSON.stringify(sendData));

            // 로딩 표시
            showLoading();

            // 이미지 파일 가져오기
            const imageFile = imageInput.files[0];
            
            if (!imageFile) {
                alert('상품 이미지가 등록되지 않았습니다.');
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
                url: '/api/product/regist',
                type: 'POST',
                contentType: 'application/json',
                data: JSON.stringify(sendData)
            });
            
            // 성공 시 처리
            if (!response.success) {
                alert('상품이 성공적으로 등록되었습니다.');
                window.location.href = '/product/' + response;
            } else {
                throw new Error(response.message || '상품 등록에 실패했습니다.');
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
        console.log(tagList.length, productTitle.value, productDescription.value, productPrice.value, productStock.value, imageInput.files.length);
        if(tagList.length > 0&&productTitle.value != ''&&productDescription.value != ''&&productPrice.value != ''&&productStock.value != ''&&imageInput.files.length > 0){
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
            $('.cam-la-in-box-bo.final').removeClass('hidden'); 
            $('.cam-la-in-box-bottom-in-end-btn-span').text('상품 등록');
            
            // 데이터 업데이트
            updateFinalSummary();
            
            checkCurrentPageInputs('final');
            
            console.log('Moved to final page');
        }
        pageStatus = $('.cam-la-in-box-top-in-na-all-ul-li.check').attr('data-target');
        console.log(pageStatus);
    });
    // 캠페인 생성 버튼 클릭 이벤트
    $('.cam-la-in-box-bottom-in-end-btn').on('click', function() {
        if ($(this).find('.cam-la-in-box-bottom-in-end-btn-span').text() === '상품 등록') {
            submitCampaign();
        }
    });

    // 캠페인 기획 페이지 뒤로가기 버튼
    $(document).on('click','.cam-la-in-box-bottom-in-back', function(){
        // 캠페인 기획 페이지가 보이는 중에 눌리면 약관 동의 페이지가 나오도록
        const navTarget = $('.cam-la-in-box-top-in-na-all-ul-li.check');
        let navName = navTarget.attr('data-target');
        if(navName == 'info'){    
            $('.cam-ag-box').removeClass('hidden'); // 약관 동의 페이지
            $('.cam-la').addClass('hidden');
        }else if(navName == 'final'){
            navTarget.removeClass('check');
            navTarget.prev().addClass('check');
            $('.cam-la-in-box-bo.info').removeClass('hidden');
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
        element.click();
        checkInfoPageInput();
    });
    $(document).on('click', '.upda-tag-big, .tagDelete, .upda-tag-custom', checkInfoPageInput);
    
    // 페이지별 입력 검사 함수들
    function checkInfoPageInput() {
        const currentPage = $('.cam-la-in-box-top-in-na-all-ul-li.check').attr('data-target');
        if(currentPage !== 'info') return;
        
        const title = productTitle ? productTitle.value.trim() : '';
        const desc = $('.cam-la-in-box-bo-all-de-div-box-textarea').val() ? $('.cam-la-in-box-bo-all-de-div-box-textarea').val().trim() : '';
        const price = productPrice ? productPrice.value.trim() : '';
        const stock = productStock ? productStock.value.trim() : '';
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
            price: price,
            stock: stock,
            image: hasImage
        });
        
        if (tagList.length > 0 && title !== '' && desc !== '' && price !== '' && stock !== '' && hasImage) {
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
            case 'final':
                // 최종 확인 페이지는 항상 활성화
                $('.cam-la-in-box-bottom-in-end-btn').removeAttr('disabled').addClass('isActive');
                // 버튼 텍스트 변경
                $(".cam-la-in-box-bottom-in-end-btn-span").text('상품 등록');
                break;
        }
    }

    // 최종 확인 페이지 데이터 채우기
    function updateFinalSummary() {
        console.log('Updating final summary');
        
        // 기본 정보
        $('#final-campaign-title').text($('input[name="title"]').val());
        $('#final-campaign-desc').text($('.cam-la-in-box-bo-all-de-div-box-textarea').val());
        $('#final-product-price').text($('input[name="price"]').val() + '원');
        $('#final-product-stock').text($('input[name="stock"]').val() + '개');
        
        // 이미지 미리보기 표시
        if (previewImage && previewImage.src && previewImage.style.display !== 'none') {
            // 이미지 컨테이너 확인 및 생성
            let imageContainer = $('#final-product-image');
            if (imageContainer.length === 0) {
                // 이미지 컨테이너가 없으면 생성
                $('.final-summary-content').append('<div class="summary-item"><label>상품 이미지</label><div id="final-product-image" class="product-image-preview"></div></div>');
                imageContainer = $('#final-product-image');
            }
            
            // 이미지 표시
            imageContainer.html(`<img src="${previewImage.src}" alt="상품 이미지" style="max-width: 200px; max-height: 200px; border-radius: 8px;">`);
        }
        
        // 태그 표시
        const tagContainer = $('#final-campaign-tags');
        tagContainer.empty();
        tagList.forEach(tag => {
            tagContainer.append(`<span class="tag-item">${tag}</span>`);
        });
        
        // 최종 확인 페이지의 다음 버튼 텍스트를 '상품 등록'으로 변경
        $(".cam-la-in-box-bottom-in-end-btn-span").text('상품 등록');
    }
    
    // 최종 확인 페이지로 이동할 때 요약 정보 업데이트
    $(document).on('click', '.cam-la-in-box-bottom-in-end-btn', function() {
        if ($('.cam-la-in-box-top-in-na-all-ul-li.check').attr('data-target') === 'info') {
            updateFinalSummary();
        }
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
let previewImage, deleteButton, previewContainer;

// 페이지별 입력 검사 함수
function checkInfoPageInput() {
    const currentPage = $('.cam-la-in-box-top-in-na-all-ul-li.check').attr('data-target');
    if(currentPage !== 'info') return;
    
    const title = productTitle ? productTitle.value.trim() : '';
    const desc = $('.cam-la-in-box-bo-all-de-div-box-textarea').val() ? $('.cam-la-in-box-bo-all-de-div-box-textarea').val().trim() : '';
    const price = productPrice ? productPrice.value.trim() : '';
    const stock = productStock ? productStock.value.trim() : '';
    const hasImage = imageInput && (
        imageInput.files.length > 0 || 
        (document.getElementById('previewImage').style.display === 'block' && 
         document.getElementById('previewImage').src && 
         !document.getElementById('previewImage').src.includes('logo.png'))
    );
    
    if (tagList.length > 0 && title !== '' && desc !== '' && price !== '' && stock !== '' && hasImage) {
        $('.cam-la-in-box-bottom-in-end-btn').removeAttr('disabled').addClass('isActive');
    } else {
        $('.cam-la-in-box-bottom-in-end-btn').attr('disabled', true).removeClass('isActive');
    }
}

// DOM 요소 초기화
imageInput = document.getElementById('productImageInput');
previewImage = document.getElementById('previewImage');
deleteButton = document.querySelector('.cam-img-re-box-btn-in');
previewContainer = document.querySelector('.cam-img-re-box-sh');

// 디버깅용 로그
console.log('DOM elements initialized:', {
    imageInput: imageInput ? true : false,
    previewImage: previewImage ? true : false,
    deleteButton: deleteButton ? true : false,
    previewContainer: previewContainer ? true : false
});

// 처음에는 미리보기 숨기기
previewImage.style.display = 'none';
document.querySelector('.cam-img-re').style.display = 'none';

// 이미지 업로드 이벤트
imageInput.addEventListener('change', async function(e) {
    const file = e.target.files[0];
    if (!file) return;
    
    // 파일 처리 및 미리보기 표시
    function processAndDisplayImage(file) {
        return new Promise((resolve, reject) => {
            const reader = new FileReader();
            reader.onload = function(e) {
                previewImage.src = e.target.result;
                previewImage.style.display = 'block';
                document.querySelector('.cam-img-re').style.display = 'flex';
                previewContainer.querySelector('span').style.display = 'none';
                checkInfoPageInput();
                deleteButton.style.display = 'block'; // 삭제 버튼 표시
                resolve();
            };
            reader.onerror = reject;
            reader.readAsDataURL(file);
        });
    }
    
    // 이미지를 1:1 비율로 크롭하는 함수
    function cropTo1x1(file) {
        return new Promise((resolve) => {
            // 여기서는 간단히 원본 파일을 반환
            resolve(file);
        });
    }
    
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
