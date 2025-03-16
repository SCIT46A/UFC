function initProductManagement() {
    console.log("📦 상품 관리 JS 실행됨");

    let cachedProducts = []; // 🔥 상품 데이터 캐시 (필터링 및 렌더링용)

    // ✅ 기존 이벤트 리스너 제거 (이중 실행 방지)
    document.querySelectorAll(".status-card").forEach(card => {
        card.replaceWith(card.cloneNode(true));
    });
    document.getElementById("selectAll")?.replaceWith(document.getElementById("selectAll").cloneNode(true));

    // ✅ 상태 카드 선택 기능
    document.querySelectorAll(".status-card").forEach(card => {
        card.addEventListener("click", () => {
            document.querySelectorAll(".status-card").forEach(c => c.classList.remove("active"));
            card.classList.add("active");
            filterAndRenderProducts();
        });
    });

    document.getElementById("selectAll")?.addEventListener("change", (e) => {
        document.querySelectorAll('#product-list input[type="checkbox"]').forEach(checkbox => {
            checkbox.checked = e.target.checked;
        });
    });


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

            // ✅ 삭제된 상품(status=4) 필터링
            cachedProducts = data.filter(product => product.status !== "삭제됨" && product.status !== 4);


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
    function renderProductList(products) {
        const productList = document.getElementById("product-list");
        const productCount = document.getElementById("product-count");

        productList.innerHTML = "";
        productCount.innerText = products.length;

        products.forEach(product => {
            let row = document.createElement("tr");
            row.innerHTML = `
                <td><input type="checkbox" value="${product.productId}"></td>
                <td>${product.productId}</td>
                <td class="editable" data-key="itemName" data-id="${product.productId}">${product.itemName}</td>
                <td class="editable" data-key="tags" data-id="${product.productId}">
                ${Array.isArray(product.tags) ? product.tags.join(", ") : product.tags}
                </td>
                <td class="editable" data-key="stockQuantity" data-id="${product.productId}">${product.stockQuantity}</td>
                <td class="editable" data-key="price" data-id="${product.productId}">${product.price}</td>
                <td>${product.createdTime ? formatDate(new Date(product.createdTime)) : '날짜 없음'}</td>
                <td>
                    <button class="btn btn-secondary edit-desc" data-id="${product.productId}" data-desc="${product.itemDescription}">상품 설명</button>
                </td>
                <td>
                    <select class="status-select" data-id="${product.productId}">
                    <option value="0" ${product.status === 0 ? "selected" : ""}>판매대기</option>
                    <option value="1" ${product.status === 1 ? "selected" : ""}>판매중</option>
                    <option value="2" ${product.status === 2 ? "selected" : ""}>품절</option>
                    <option value=3 ${product.status === 3 ? "selected" : ""}>판매중지</option>
                    </select>
                </td>
                <td>
                    <button class="btn btn-secondary delete-btn" data-id="${product.productId}">삭제</button>
                </td>
            `;
            productList.appendChild(row);
        });

        document.querySelectorAll(".editable").forEach(cell => {
            cell.addEventListener("click", (e) => activateInlineEdit(e.target));
        });

        document.querySelectorAll(".delete-btn").forEach(button => {
            button.addEventListener("click", (e) => deleteProduct(e.target.dataset.id));
        });

        document.querySelectorAll(".edit-desc").forEach(button => {
            button.addEventListener("click", (e) => openDescriptionModal(e.target.dataset.id, e.target.dataset.desc));
        });
    }
    document.addEventListener("change", (event) => {
        if (event.target.classList.contains("status-select")) {
            const productId = event.target.dataset.id;  // ✅ 상품 ID 가져오기
            const newStatus = event.target.value;  // ✅ 선택한 상태 값 가져오기

            console.log(`✅ 상품 상태 변경 요청 - 상품 ID: ${productId}, 새로운 상태: ${newStatus}`);

            updateProductStatus(productId, newStatus);
        }
    });


    // ✅ 상품 삭제 기능 (soft delete)
    async function deleteProduct(productId) {
        try {
            const response = await fetch(`/api/creator/dashboard/products/${productId}/delete`, { method: "PATCH" });

            if (!response.ok) throw new Error("상품 삭제 실패");

            // ✅ 삭제된 상품을 화면에서 즉시 제거
            document.querySelector(`tr[data-id='${productId}']`)?.remove();

            // ✅ 캐시된 데이터에서도 삭제
            cachedProducts = cachedProducts.filter(product => product.productId !== parseInt(productId));

            // ✅ 최신 상품 목록 다시 불러오기
            fetchProducts();
        } catch (error) {
            console.error("🚨 상품 삭제 중 오류 발생:", error);
            alert("상품 삭제에 실패했습니다.");
        }
    }


    // ✅ 상품 상태 업데이트
    async function updateProductStatus(productId, newStatus) {
        try {
            console.log(`✅ 상품 상태 변경 요청 - 상품 ID: ${productId}, 새로운 상태 코드: ${newStatus}`);

            const response = await fetch(`/api/creator/dashboard/products/${productId}/status`, {
                method: "PATCH",
                headers: { "Content-Type": "application/json" },
                body: JSON.stringify({ status: parseInt(newStatus, 10) })  // 숫자로 변환하여 전송
            });

            const data = await response.json();

            if (!response.ok) {
                throw new Error(data.error || "상태 업데이트 실패");
            }

            console.log(`✅ 상태 업데이트 성공 - 상태 코드: ${newStatus}`);

        } catch (error) {
            console.error("🚨 상품 상태 변경 중 오류 발생:", error);
        }
    }


    function openProductCreatePage() {
        const url = window.location.origin + "/product/regist";
        window.open(url, "_blank");
    }


    // ✅ 브라우저에서 함수 인식 가능하도록 등록
    window.openProductCreatePage = openProductCreatePage;

    function openDescriptionModal(productId, itemDescription) {
        console.log("✅ openDescriptionModal 실행됨");
        console.log("📌 전달된 productId:", productId);
        console.log("📌 전달된 itemDescription:", itemDescription);

        const modalDesc = document.getElementById("modal-description");
        const modalSave = document.getElementById("modal-save");
        const modalContainer = document.getElementById("description-modal");

        if (!modalDesc || !modalSave || !modalContainer) {
            console.error("🚨 모달 요소를 찾을 수 없습니다.");
            return;
        }

        modalContainer.style.display = "flex";

        setTimeout(() => {  // ✅ 50ms 뒤에 값 설정
            modalDesc.innerText = itemDescription || "";
            console.log("📌 모달에 입력된 값:", modalDesc.innerText);
        }, 50);

        modalSave.dataset.id = productId;
    }

    function closeDescriptionModal() {
        const modalContainer = document.getElementById("description-modal");
        if (modalContainer) {
            modalContainer.style.display = "none";  // ✅ 모달 닫기
        }
    }

    // ✅ 모달 닫기 버튼 이벤트 리스너 추가
    document.getElementById("modal-close")?.addEventListener("click", closeDescriptionModal);


    function activateInlineEdit(cell) {
        if (cell.dataset.key === "tags") {
            // ✅ 태그 입력 창으로 변경
            const tags = cell.innerText.split(",").map(tag => tag.trim());
            const input = document.createElement("input");
            input.type = "text";
            input.value = tags.join(", "); // ✅ 기존 태그 값 유지
            input.dataset.id = cell.dataset.id;
            input.dataset.key = cell.dataset.key;

            input.addEventListener("blur", saveInlineEdit);
            input.addEventListener("keypress", (e) => {
                if (e.key === "Enter") saveInlineEdit(e);
            });

            cell.innerHTML = "";
            cell.appendChild(input);
            input.focus();
        } else {
            // ✅ 기존 방식 그대로
            const input = document.createElement("input");
            input.type = "text";
            input.value = cell.textContent.trim();
            input.dataset.id = cell.dataset.id;
            input.dataset.key = cell.dataset.key;

            input.addEventListener("blur", saveInlineEdit);
            input.addEventListener("keypress", (e) => {
                if (e.key === "Enter") saveInlineEdit(e);
            });

            cell.innerHTML = "";
            cell.appendChild(input);
            input.focus();
        }
    }



    async function saveInlineEdit(e) {
        const input = e.target;
        const productId = input.dataset.id;
        const key = input.dataset.key;
        let newValue = input.value.trim();

        // ✅ parentNode를 미리 저장
        const parent = input.parentNode;

        if (!parent) {
            console.error("🚨 부모 요소가 존재하지 않습니다. (key: " + key + ")");
            return;
        }

        // ✅ 태그 업데이트 처리
        if (key === "tags") {
            let tagsArray = newValue
                .split(",")
                .map(tag => tag.trim())
                .filter(tag => tag.length > 0);  // ✅ 빈 태그 제거

            try {
                const response = await fetch(`/api/creator/dashboard/products/${productId}/tags`, {
                    method: "PATCH",
                    headers: { "Content-Type": "application/json" },
                    body: JSON.stringify({ tags: tagsArray })
                });

                if (!response.ok) throw new Error("태그 업데이트 실패");

                // ✅ 성공적으로 반영되면 화면 업데이트 (렌더링 방지)
                setTimeout(() => {
                    if (document.body.contains(parent)) {
                        parent.innerText = tagsArray.length > 0 ? tagsArray.join(", ") : "태그 없음";
                    } else {
                        console.warn("🚨 태그 업데이트 후에도 parentNode가 없음");
                    }
                }, 50);

            } catch (error) {
                console.error("🚨 태그 저장 중 오류 발생:", error);
            }
            return;
        }

        // ✅ 숫자 값 처리
        if (key === "stockQuantity" || key === "price") {
            newValue = parseInt(newValue, 10);
            if (isNaN(newValue) || newValue < 0) {
                console.error("🚨 유효하지 않은 값:", newValue);
                parent.innerText = "0";
                return;
            }
        }

        // ✅ 기타 문자열 값 처리
        if (key === "itemName") {
            newValue = newValue.trim();
            if (newValue.length === 0) {
                console.error("🚨 상품명이 비어있을 수 없습니다.");
                return;
            }
        }

        // ✅ 서버에 데이터 저장 요청
        try {
            const response = await fetch(`/api/creator/dashboard/products/${productId}`, {
                method: "PATCH",
                headers: { "Content-Type": "application/json" },
                body: JSON.stringify({ [key]: newValue })
            });

            if (!response.ok) throw new Error("데이터 저장 실패");

            // ✅ 렌더링 전에 parentNode 존재 여부 확인 후 업데이트
            setTimeout(() => {
                if (document.body.contains(parent)) {
                    parent.innerText = newValue;
                } else {
                    console.warn("🚨 업데이트 후에도 parentNode가 없음");
                }
            }, 50);

        } catch (error) {
            console.error("🚨 데이터 저장 중 오류 발생:", error);
        } finally {
            setTimeout(() => input.blur(), 50);  // ✅ input이 남아있을 때만 blur 실행
        }
    }




    document.getElementById("modal-save")?.addEventListener("click", async function () {
        const productId = this.dataset.id;
        const newDescription = document.getElementById("modal-description").innerText;
        try {
            await fetch(`/api/creator/dashboard/products/${productId}`, {
                method: "PATCH",
                headers: { "Content-Type": "application/json" },
                body: JSON.stringify({ itemDescription: newDescription })
            });
            fetchProducts();
            closeDescriptionModal(); // ✅ 저장 후 모달 닫기
        } catch (error) {
            console.error("🚨 설명 수정 중 오류 발생:", error);
        }
    });


    fetchProducts(); // 데이터 불러오기 실행
}

// 🚀 fragment가 변경될 때마다 JS를 다시 실행하도록 설정
document.addEventListener("reapplyEventListeners", initProductManagement);
