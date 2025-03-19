document.addEventListener('DOMContentLoaded', function () {
  // profile edit Btn
  const profileEditBtn = document.querySelector('.pro-edit-btn');
  const profileEditBtnCancle = document.querySelector('.pro-edit-btn-save');
  const profileResetBtn = document.querySelector('.pro-edit-btn-reset');
  
  //default img Btn
  const defaultImageBtn = document.querySelector('.default-img');

  // 원래 보이던 정보
  const profileImg = document.querySelector('.pro-img');
  const profileName = document.querySelector('.pro-name');
  const profileSh = document.querySelector('.pro-sh');
  const profilePh = document.querySelector('.pro-ph');
  const profileAd = document.querySelector('.pro-ad');

  //수정 칸
  const profileImgClose = document.querySelector('.pro-img-close');
  const profileNameClose = document.querySelector('.pro-name-close');
  const profileShClose = document.querySelector('.pro-sh-close');
  const profilePhClose = document.querySelector('.pro-ph-close');
  const profileAdClose = document.querySelector('.pro-ad-close');

  //버튼 이벤트트
  profileEditBtn.addEventListener('click', () => {
    profileEditBtn.classList.add('hidden');
    profileEditBtnCancle.classList.remove('hidden');
    profileResetBtn.classList.remove('hidden');
    profileImg.classList.add('hidden');
    profileName.classList.add('hidden');
    profileSh.classList.add('hidden');
    profilePh.classList.add('hidden');
    profileAd.classList.add('hidden');
    profileImgClose.classList.remove('hidden');
    profileNameClose.classList.remove('hidden');
    profileShClose.classList.remove('hidden');
    profilePhClose.classList.remove('hidden');
    profileAdClose.classList.remove('hidden');
    defaultImageBtn.classList.add('hidden');
  });


  profileEditBtnCancle.addEventListener('click', () => {
    profileEditBtn.classList.remove('hidden');
    profileEditBtnCancle.classList.add('hidden');
    profileResetBtn.classList.add('hidden');
    profileImg.classList.remove('hidden');
    profileName.classList.remove('hidden');
    profileSh.classList.remove('hidden');
    profilePh.classList.remove('hidden');
    profileAd.classList.remove('hidden');
    profileImgClose.classList.add('hidden');
    profileNameClose.classList.add('hidden');
    profileShClose.classList.add('hidden');
    profilePhClose.classList.add('hidden');
    profileAdClose.classList.add('hidden');
    profileResetBtn.classList.add('hidden');
    defaultImageBtn.classList.remove('hidden');
    addressAdd;
  });


  


  //프로필 이미지 가져오기
  const profileImage = document.querySelector('.main-in-box-pro-le-in-img')
  let profileImageId = profileImage.getAttribute('src');
  console.log(profileImageId);

  profileResetBtn.addEventListener('click', () => {
    window.location.reload();
  });


  function extractImageId(imageUrl) {
    let regex = /https:\/\/imagedelivery\.net\/[^\/]+\/([^\/]+)\/public/;
    let match = imageUrl.match(regex);
    return match ? match[1] : null;
  }

  let imageId = extractImageId(profileImageId);
  //프로필 이미지 넣기
  const profileTarget = document.querySelector('#imageInputField-add');
  profileTarget.setAttribute('value', imageId);


  defaultImageBtn.addEventListener('click', () => {
    // hidden input의 값을 기본 이미지 URL로 변경
    profileTarget.value = '/images/user/default_avatar.png';
    // 미리보기 이미지 업데이트 (화면에 보이는 이미지)
    const previewImg = document.querySelector('#previewImage img');
    if (previewImg) {
      previewImg.src = '/images/user/default_avatar.png';
    }
    console.log(profileTarget.value);
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

const autoHyphen2 = (target) => {
  target.value = target.value
    .replace(/[^0-9]/g, '')
    .replace(/^(\d{0,3})(\d{0,4})(\d{0,4})$/g, '$1-$2-$3')
    .replace(/(\-{1,2})$/g, '');

  // 변경 감지하여 버튼 활성화 체크
  target.dispatchEvent(new Event('input'));
};

window.addEventListener('DOMContentLoaded', () => {
  const addressInput = document.querySelector('.address');
  const addressDetailInput = document.querySelector('.address-detail');
  const totalAddress = document.querySelector('.total-add');

  // 페이지 로드 시 기본 주소값 설정
  totalAddress.value =
    (addressInput.value || '') + (addressDetailInput.value || '');
});

document.querySelector('.pro-edit-btn-save').addEventListener('click', () => {
  const addressInput = document.querySelector('.address');
  const addressDetailInput = document.querySelector('.address-detail');
  const totalAddress = document.querySelector('.total-add');

  totalAddress.value =
    (addressInput.value || '') + '#' + (addressDetailInput.value || '');
    
  
});

const imageInput = document.getElementById('ch-img-btn');
// const imageUploader = new CloudflareImageUploader();
let sendData = {};

document.getElementById('ch-img-btn').addEventListener('change', uploadImage);



async function uploadImage() {
  const imageFile = imageInput.files[0];
  try {
    const formData = new FormData();
    formData.append('file', imageFile);
    
    // 파일 업로드 요청 (이미지 업로드 후 imageId 반환)
    const uploadResponse = await fetch('/api/image/upload', {
      method: 'POST',
      body: formData,
      headers: { 'Accept': 'application/json' }
    });
    if (!uploadResponse.ok) {
      throw new Error('이미지 업로드에 실패했습니다.');
    }
    
    // 서버에서 반환한 imageId (UUID 문자열)
    const imageId = await uploadResponse.text();
    console.log("업로드된 imageId:", imageId);
    
    // hidden input에 imageId 저장 (네스티드 바인딩: photo.imageId)
    document.getElementById('imageInputField-add').value = imageId;
    
    // 기본 URL을 설정하여 이미지 태그에 할당
    const basicUrl = "/api/image/" + imageId;
    $("#target-img").attr("src", basicUrl);
    $("#imageIdField").val(""); // photo.id는 서버에서 설정할 것이므로 비워두거나 생략
    sendData.imageId = imageId;
    console.log("전송할 imageId:", sendData.imageId);
    
    // 기본 URL로 최종 이미지 URL 요청 (서버가 최종 URL을 반환)
    const urlResponse = await fetch(basicUrl, {
      credentials: "include",
      headers: { "Accept": "text/plain" }
    });
    if (!urlResponse.ok) {
      throw new Error("이미지 URL 요청 실패");
    }
    const finalImageUrl = await urlResponse.text();
    console.log("업로드된 최종 이미지 URL:", finalImageUrl);
    
    // 화면에 최종 이미지 URL 적용
    $(".main-in-content-in-pe-content-in-img").attr("src", finalImageUrl);
    
    // (선택) 모든 대상 이미지 업데이트 (예전 방식)
    $("img.target-img").each(function() {
      const $img = $(this);
      const endpoint = $img.attr("src", basicUrl).attr("src");
      $.ajax({
        url: endpoint,
        method: "GET",
        headers: { "Accept": "text/plain" },
        success: function(resultUrl) {
          console.log("AJAX 응답 최종 이미지 URL:", resultUrl);
          if (resultUrl) {
            $img.attr("src", resultUrl);
          }
        },
        error: function(err) {
          console.error("이미지 URL 요청 오류:", err);
        }
      });
    });
    
  } catch (error) {
    console.error('Error:', error);
    throw error;
  }




}











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