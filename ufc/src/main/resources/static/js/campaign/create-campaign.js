
$(document).ready(function () {
    let campaignTitle = document.querySelector("input[name='title']");
    let campaignDesc = document.querySelector("input[name='shortDescription']");



    /* page 1 캠페인 타이틀 입력 + 태그 추가 + 캠페인 소개 작성성 */
    const tagBtn = document.querySelectorAll(".upda-tag-big");
    const tagAdd = document.querySelector("#tagAdd");
    const tagCustom = document.querySelector(".upda-tag-custom");

    let tagList = [];

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
        console.log(tagList);
    });
    $(document).on('click', '.upda-tag-custom', function(){
        $(this).toggleClass('tag-active');
        const tagName = $(this).text();
        if($(this).hasClass('tag-active')){
            tagList.push(tagName);

        }else{
            tagList = tagList.filter(tag => tag !== tagName);
        }
        console.log(tagList);
    });



    // 태그 추가 버튼 클릭 시 태그 추가 필드 제공
    $(tagAdd).on('click', function(){
        $(tagAdd).after(tag);
    });

    // 태그 삭제 버튼 클릭 시 추가된 필드 삭제 (이벤트 위임 사용)
    $(document).on('click', '.tagDelete', function(){
        $(this).closest('li').remove();  // 또는 $(this).parent().parent().remove();
    });

    // 키보드 입력 이벤트 상시 체크
    $(document).on('keyup', keyupCheck);

    // 다음 버튼 눌림 이벤트 체크
    $(document).on('click', '.next-btn', );


    /* page 2 캠페인 생성 동의 체크박스 */

    const checkBox = document.querySelectorAll(".cam-ag-in-box-top-in-na-all-ul-li");

    checkBox.forEach(item => {
        item.addEventListener("click", function () {
            this.classList.toggle("check");
        });
    });



    // 캠페인 생성 완료 시 AJAX로 데이터 전송
    $(document).on('click', '#saveBtn', function(){
        let data = {
            title: $(title).val(),
            description: $(description).val(),
            tags: $(tags).val()
        }
        $.ajax({
            url: '/campaign/create',
            type: 'POST',
            data: data,
            success: function(response){
                redirect('/creator/dashboard');
            },
            error: function(error){
                console.log(error);
            }
        })
    })

});

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
