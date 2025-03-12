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

    const courierElement = document.getElementById("courierName");
    const deliveryStatusElement = document.getElementById("deliveryStatus");
    const courierInput = document.getElementById("courierId");
    const trackingInput = document.getElementById("trackingNumber");

    if (!courierElement || !deliveryStatusElement) {
        console.log("⚠️ 필수 요소를 찾을 수 없습니다. (courierName 또는 deliveryStatus)");
        return;
    }

    let courierCode = courierElement.innerText.trim();

    // `#`을 기준으로 택배사 코드와 송장번호 분리
    let splitData = courierCode.split("#");
    let companyCode = splitData[0]; // 택배사 코드
    let trackingNumber = splitData[1] || "송장번호 없음"; // 송장번호

    let courierName = courierMap[companyCode] || "배송을 준비중이니 조금 기다려 주세요 :)";
    courierElement.innerText = `${courierName} (${trackingNumber})`;
    courierInput.value = companyCode;
    trackingInput.value = trackingNumber;



    // 📌 `status` 값이 `deliveryStatus`에 표시되도록 수정
    const statusElement = document.getElementById("status");
    if (statusElement) {
        let statusText = statusElement.innerText.trim();
        console.log("🚀 현재 배송 상태:", statusText); // 🚀 로그 출력
        deliveryStatusElement.innerText = statusText || "배송 상태를 불러오는 중...";
    } else {
        console.log("⚠️ status 요소를 찾을 수 없습니다.");
        deliveryStatusElement.innerText = "배송 정보를 불러오는 중...";
    }

    // 📌 주소 처리
    const addressElement = document.getElementById("address");
    if (addressElement) {
        let address = addressElement.innerText.trim();
        let splitAddress = address.split("#");
        let frontAddress = splitAddress[0];
        let backAddress = splitAddress[1] || "";
        addressElement.innerText = `${frontAddress}, ${backAddress}`;
    }

    // 2초 후 자동 폼 제출
    setTimeout(() => {
        autoSubmitForm();
    }, 2000);

    function autoSubmitForm() {
        const form = document.querySelector("form");
        if (!form) {
            console.log("⚠️ 폼을 찾을 수 없습니다.");
            return;
        }

        

        if (!courierInput || !trackingInput) {
            console.log("⚠️ 입력 필드를 찾을 수 없습니다.");
            return;
        }

        let courierId = courierInput.value.trim();
        let trackingNum = trackingInput.value.trim();

        if (courierId && trackingNum) {
            console.log("📢 배송 조회 요청:", courierId, trackingNum);

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
                console.log("📢 서버 응답 상태 코드:", response.status);
                if (!response.ok) {
                    return response.text().then(errorMessage => {
                        throw new Error(`❌ 서버 오류: ${errorMessage}`);
                    });
                }
                return response.json();
            })
            .then(data => {
                console.log("📦 배송 상태 응답:", data);
                deliveryStatusElement.innerText = data.status;
            })
            .catch(error => {
                console.error("🚨 배송 조회 실패:", error);
                deliveryStatusElement.innerText = "배송 정보를 가져오는 중 오류가 발생했습니다.";
            });
        } else {
            console.log("⚠️ courierId 또는 trackingNum이 비어 있습니다.");
        }
    }
});
