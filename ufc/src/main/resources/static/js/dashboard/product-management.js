function initProductManagement() {
    console.log("📦 상품 관리 JS 실행됨");

    let cachedProducts = []; // 🔥 상품 데이터 캐시 (필터링 및 렌더링용)

    // ✅ 상태 카드 선택 기능
    document.querySelectorAll(".status-card").forEach(card => {
        card.addEventListener("click", () => {
            document.querySelectorAll(".status-card").forEach(c => c.classList.remove("active"));
            card.classList.add("active");
            console.log(`📊 선택된 상태: ${card.textContent.trim()}`);
            filterAndRenderProducts(); // ✅ 상태 변경 시 상품 목록 필터링
        });
    });

    // ✅ "전체 선택" 체크박스 기능
    const selectAllCheckbox = document.getElementById("selectAll");
    if (selectAllCheckbox) {
        selectAllCheckbox.addEventListener("change", (e) => {
            document.querySelectorAll('#product-list input[type="checkbox"]').forEach(checkbox => {
                checkbox.checked = e.target.checked;
            });
        });
    }

    // ✅ 기간 선택 버튼 기능
    const periodButtons = document.querySelectorAll(".period-buttons button");
    periodButtons.forEach((button) => {
        button.addEventListener("click", () => {
            periodButtons.forEach((btn) => btn.classList.remove("active"));
            button.classList.add("active");
            updateDateRange(button.textContent);
        });
    });

    // ✅ 날짜 범위 업데이트
    window.updateDateRange = function (period) {
        const endDate = new Date();
        let startDate = new Date();

        switch (period) {
            case "오늘":
                break;
            case "1주일":
                startDate.setDate(endDate.getDate() - 7);
                break;
            case "1개월":
                startDate.setMonth(endDate.getMonth() - 1);
                break;
            case "3개월":
                startDate.setMonth(endDate.getMonth() - 3);
                break;
        }

        document.querySelectorAll("input[type='date']")[0].value = formatDate(startDate);
        document.querySelectorAll("input[type='date']")[1].value = formatDate(endDate);
    };

    window.formatDate = function (date) {
        return date.toISOString().split("T")[0];
    };

    // ✅ 상품 데이터 불러오기 (백엔드 API 연동)
    async function fetchProducts() {
        try {
            const response = await fetch("/api/creator/dashboard/products");

            // ✅ 응답이 JSON인지 확인
            const contentType = response.headers.get("content-type");
            if (!contentType || !contentType.includes("application/json")) {
                throw new Error("서버 응답이 JSON 형식이 아닙니다.");
            }

            const data = await response.json();

            // ✅ 데이터가 배열인지 확인 (배열이 아니면 빈 배열로 초기화)
            if (!Array.isArray(data)) {
                console.error("🚨 서버 응답이 배열이 아닙니다:", data);
                throw new Error("서버 응답이 올바른 상품 목록(JSON 배열)이 아닙니다.");
            }

            console.log("✅ 상품 데이터 불러오기 성공:", data);
            renderProductList(data); // UI 업데이트
        } catch (error) {
            console.error("🚨 상품 데이터를 불러오는 중 오류 발생:", error);
            alert("상품 정보를 불러오지 못했습니다. 관리자에게 문의하세요.");
        }
    }


    // ✅ 상품 목록 필터링 & 렌더링
    function filterAndRenderProducts() {
        const activeStatus = document.querySelector(".status-card.active")?.dataset.status;
        const filteredProducts = activeStatus && activeStatus !== "full"
            ? cachedProducts.filter(product => product.status === activeStatus)
            : cachedProducts;

        renderProductList(filteredProducts);
    }

    // ✅ 상품 목록 렌더링
    function renderProductList(products = cachedProducts) {
        const productList = document.getElementById("product-list");
        const productCount = document.getElementById("product-count");

        productList.innerHTML = ""; // 기존 목록 비우기
        productCount.innerText = products.length; // 상품 개수 업데이트

        products.forEach(product => {
            let row = document.createElement("tr");

            row.innerHTML = `
                <td><input type="checkbox" value="${product.productId}"></td>
                <td>${product.productId}</td>
                <td>${product.itemName}</td>
                <td><span class="category-tag">${product.tags}</span></td>
                <td>${product.stockQuantity}</td>
                <td>${product.createdTime ? formatDate(new Date(product.createdTime)) : '날짜 없음'}</td>
                <td>
                    <select class="status-select" data-id="${product.id}">
                        <option value="판매중" ${product.status === "판매중" ? "selected" : ""}>판매중</option>
                        <option value="판매대기" ${product.status === "판매대기" ? "selected" : ""}>판매대기</option>
                        <option value="품절" ${product.status === "품절" ? "selected" : ""}>품절</option>
                        <option value="판매중지" ${product.status === "판매중지" ? "selected" : ""}>판매중지</option>
                    </select>
                </td>
                <td>
                    <button class="btn btn-secondary delete-btn" data-id="${product.productId}">삭제</button>
                </td>
            `;

            productList.appendChild(row);
        });

        // ✅ 삭제 버튼 이벤트 리스너 추가
        document.querySelectorAll(".delete-btn").forEach(button => {
            button.addEventListener("click", (e) => {
                const productId = e.target.dataset.id;
                deleteProduct(productId);
            });
        });

        // ✅ 상태 변경 이벤트 리스너 추가
        document.querySelectorAll(".status-select").forEach(select => {
            select.addEventListener("change", (e) => {
                const productId = e.target.dataset.id;
                const newStatus = e.target.value;
                updateProductStatus(productId, newStatus);
            });
        });
    }

    // ✅ 상품 삭제 기능
    async function deleteProduct(productId) {
        try {
            await fetch(`/api/creator/dashboard/products/${productId}`, { method: "DELETE" });
            cachedProducts = cachedProducts.filter(product => product.id !== productId);
            filterAndRenderProducts();
        } catch (error) {
            console.error("🚨 상품 삭제 중 오류 발생:", error);
        }
    }

    // ✅ 상품 상태 업데이트
    async function updateProductStatus(productId, newStatus) {
        try {
            await fetch(`/api/creator/dashboard/products/${productId}/status`, {
                method: "PATCH",
                headers: { "Content-Type": "application/json" },
                body: JSON.stringify({ status: newStatus })
            });

            // 캐시 데이터 업데이트
            cachedProducts = cachedProducts.map(product =>
                product.id === productId ? { ...product, status: newStatus } : product
            );

            console.log(`✅ 상품 ${productId} 상태 변경됨: ${newStatus}`);
        } catch (error) {
            console.error("🚨 상품 상태 변경 중 오류 발생:", error);
        }
    }

    fetchProducts(); // 데이터 불러오기 실행
}


function openProductCreatePage() {
    const url = window.location.origin + "/product/regist";
    window.open(url, "_blank");
}

// 🚀 fragment가 변경될 때마다 JS를 다시 실행하도록 설정
document.addEventListener("reapplyEventListeners", initProductManagement);
