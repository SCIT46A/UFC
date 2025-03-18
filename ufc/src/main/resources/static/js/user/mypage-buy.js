document.addEventListener("DOMContentLoaded", () => {
    const courierMap = {
        "kr.cjlogistics": "CJ대한통운",
        "kr.cupost": "CU편의점택배",
        "kr.cvsnet": "GS Postbox",
        "kr.epantos": "LX판토스",
        "kr.daesin": "대신택배",
        "kr.cway": "우리택배",
        "kr.epost.ems": "우체국 EMS",
        "kr.epost": "우체국택배",
        "kr.ilyanglogis": "일양로지스",
        "kr.chunilps": "천일택배",
        "kr.coupangls": "쿠팡 로지스틱스 서비스",
        "kr.hanjin": "한진택배",
        "kr.honamlogis": "호남택배",
        "kr.homepick": "홈픽"
    };



    const courierElements = document.querySelectorAll(".courierName");
    const deliveryStatusElements = document.querySelectorAll(".deliveryStatus");
    const courierInputs = document.querySelectorAll(".courierId");
    const trackingInputs = document.querySelectorAll(".trackingNumber");

    if (!courierElements.length || !deliveryStatusElements.length) {
        console.warn("⚠️ 필수 요소를 찾을 수 없습니다. (courierName 또는 deliveryStatus)");
        return;
    }


    courierElements.forEach((courierElement, index) => {
        let courierCode = courierElement.innerText.trim();
        if (courierCode) {
            let splitData = courierCode.split("#");
            let companyCode = splitData[0];
            let trackingNumber = splitData[1] || "송장번호 없음";

            let courierName = courierMap[companyCode] || "배송을 준비중이니 조금 기다려 주세요 :)";
            courierElement.innerText = `${courierName} (${trackingNumber})`;

            if (courierInputs[index]) courierInputs[index].value = companyCode;
            if (trackingInputs[index]) trackingInputs[index].value = trackingNumber;
        }
    });

    deliveryStatusElements.forEach((statusElement, index) => {
        let statusText = statusElement.innerText.trim();
        console.log(`🚀 현재 배송 상태 [${index}]:`, statusText);
        statusElement.innerText = statusText || "배송 상태를 불러오는 중...";
    });

    // ⏳ 2초 후 자동으로 배송 조회 실행
    setTimeout(() => {
        autoSubmitForms();
    }, 2000);

    function autoSubmitForms() {
        const forms = document.querySelectorAll("form");
        forms.forEach((form, index) => {
            const courierId = courierInputs[index]?.value.trim();
            const trackingNum = trackingInputs[index]?.value.trim();
            const deliveryStatusElement = deliveryStatusElements[index];

            if (courierId && trackingNum) {
                console.log(`📢 배송 조회 요청 [${index}]:`, courierId, trackingNum);

                fetch(form.action, {
                    method: "POST",
                    body: new URLSearchParams({
                        courierId: courierId,
                        trackingNumber: trackingNum
                    }),
                    headers: {
                        "Content-Type": "application/x-www-form-urlencoded"
                    }
                })
                    .then(response => {
                        console.log(`📢 서버 응답 상태 코드 [${index}]:`, response.status);
                        if (!response.ok) {
                            return response.text().then(errorMessage => {
                                throw new Error(`❌ 서버 오류: ${errorMessage}`);
                            });
                        }
                        return response.json();
                    })
                    .then(data => {
                        console.log(`📦 배송 상태 응답 [${index}]:`, data);
                        if (deliveryStatusElement) {
                            deliveryStatusElement.innerText = data.status || "배송 상태를 가져올 수 없습니다.";
                        }
                    })
                    .catch(error => {
                        console.error(`🚨 배송 조회 실패 [${index}]:`, error);
                        if (deliveryStatusElement) {
                            deliveryStatusElement.innerText = "배송 정보를 가져오는 중 오류가 발생했습니다.";
                        }
                    });
            } else {
                console.warn(`⚠️ courierId 또는 trackingNum이 비어 있습니다. [${index}]`);
            }
        });
    }

    const deleteLink = document.querySelector(".delete-btn a");
  
  deleteLink.addEventListener("click", function(e) {
    // 확인창을 띄웁니다.
    if (!confirm("정말로 삭제하시겠습니까?")) {
      // 취소를 누르면 링크 실행을 막습니다.
      e.preventDefault();
    }
  });    
});
