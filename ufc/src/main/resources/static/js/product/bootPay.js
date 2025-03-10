document
    .getElementById("pay-button")
    .addEventListener("click", async function () {
        // 부트페이 결제 로직
        try {
            const requestData = {
                application_id: "67cef747a37759f9542b31d7",
                price: 1000,
                order_name: "테스트결제",
                order_id: "TEST_ORDER_ID",
                user: {
                    id: "회원아이디",
                    username: "회원이름",
                    phone: "01000000000",
                    email: "test@test.com",
                },
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

            // 결제 성공 시 서버로 POST 요청 전송
            const serverResponse = await fetch("/product/pay", {
                method: "POST",
                headers: {
                    "Content-Type": "application/json",
                },
                body: JSON.stringify({
                    receipt_id: response.receipt_id, // 부트페이 결제 ID
                    order_id: requestData.order_id,  // 주문 ID
                    amount: requestData.price,       // 결제 금액
                }),
            });

            const result = await serverResponse.json();

            if (result.success) {
                alert("결제가 성공적으로 처리되었습니다.");
                // 루트 페이지로 이동
                window.location.href = "/";
            } else {
                alert("결제 검증에 실패했습니다.");
            }
        } catch (error) {
            console.error("결제 오류:", error.message);
        }
    });