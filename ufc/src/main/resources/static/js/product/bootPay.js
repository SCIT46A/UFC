document.getElementById("pay-button").addEventListener("click", async function () {
    const productId   = document.querySelector('.product-id').value;
    const productStock= document.querySelector('.product-stock').value;
    const totalPay    = document.querySelector('.total-pay').value;
    const userId      = document.querySelector('.user-id').value;
    const productName = document.querySelector(".product-name").value;
    // 부트페이 결제 로직
    try {
        const requestData = {
            application_id: "67cef747a37759f9542b31d7",
            price: totalPay,
            order_name: "UPDA" + productName,
            order_id: productName,
            extra: {
                open_type: "popup",
                popup: {
                    width: 800,
                    height: 600,
                },
                card_quota: "0,2,3",
                escrow: false,
                separately_confirmed: true,
            },
        };

        // 부트페이 결제 요청
        const response = await Bootpay.requestPayment(requestData);

        const payload = {
            receipt_id: response.receipt_id,   // 부트페이 결제 ID
            order_id: requestData.order_id,      // 주문 ID
            amount: requestData.price,           // 결제 금액
            product_id: productId,               // 상품 ID
            stock: productStock,                 // 수량
            total_pay: totalPay,                 // 총 결제 금액
            user_id: userId                      // 유저 ID
        };

        // 결제 성공 시 서버로 POST 요청 전송 (payload 객체 자체를 보내도록 함)
        const serverResponse = await fetch("/product/pay", {
            method: "POST",
            headers: {
                "Content-Type": "application/json",
            },
            body: JSON.stringify(payload)
        });

        const result = await serverResponse.json();

// 🔍 응답 로그 확인
        console.log("🔍 서버 응답: ", result);

        if (result.success) {
            alert("✅ 결제가 성공적으로 처리되었습니다.");
            window.location.href = "/user/buy";
        } else {
            alert("❌ 결제 검증에 실패했습니다. 서버 응답: " + JSON.stringify(result));
        }
    } catch (error) {
        console.error("결제 오류:", error.message);
    }
});
