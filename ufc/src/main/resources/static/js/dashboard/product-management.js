function initProductManagement() {
    console.log("📦 상품 관리 JS 실행됨");

    let cachedProducts = []; // 🔥 상품 데이터 캐시 (필터링 및 렌더링용)


    // 🔍 검색 입력 필드 찾기
    const searchInput = document.querySelector(".search-row input[placeholder='상품명 또는 상품번호를 입력하세요']");
    const tagInput = document.querySelector(".search-row input[placeholder='태그를 입력하세요']");
    const dateInputs = document.querySelectorAll(".date-range input[type='date']");
    const searchButton = document.querySelector(".search-actions .btn-primary");
    const resetButton = document.querySelector(".search-actions .btn-secondary");
    const sortSelect = document.querySelector(".table-header select");

    // 🔍 검색 버튼 클릭 이벤트 추가
    searchButton.addEventListener("click", () => {
        filterAndSortProducts();
    });

    // 🔄 초기화 버튼 클릭 시 검색 필드 리셋 & 전체 상품 표시
    resetButton.addEventListener("click", () => {
        searchInput.value = "";
        tagInput.value = "";
        dateInputs.forEach(input => input.value = "");

        filterAndSortProducts(); // 전체 목록 다시 표시
    });

    sortSelect.addEventListener("change", () => {
        sortAndRenderProducts(sortSelect.value);
    });



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
            filterAndSortProducts();
        });
    });

    document.getElementById("selectAll")?.addEventListener("change", (e) => {
        document.querySelectorAll('#product-list input[type="checkbox"]').forEach(checkbox => {
            checkbox.checked = e.target.checked;
        });
    });


    // 🔽 정렬 후 UI 업데이트 함수
    function sortAndRenderProducts(sortOption) {
        let sortedProducts = [...cachedProducts]; // 기존 배열 복사

        switch (sortOption) {
            case "최근등록순":
                sortedProducts.sort((a, b) => new Date(b.createdTime) - new Date(a.createdTime));
                break;
            case "오래된등록순":
                sortedProducts.sort((a, b) => new Date(a.createdTime) - new Date(b.createdTime));
                break;
            case "가격높은순":
                sortedProducts.sort((a, b) => b.price - a.price);
                break;
            case "가격낮은순":
                sortedProducts.sort((a, b) => a.price - b.price);
                break;
        }

        renderProductList(sortedProducts); // UI 업데이트
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

    // ✅ 상태별 개수 업데이트 함수
    function updateStatusCounts() {
        const statusCounts = {
            full: cachedProducts.length, // 전체 상품 개수
            "on-sale": cachedProducts.filter(p => p.status == 1).length,
            waiting: cachedProducts.filter(p => p.status == 0).length,
            "out-of-stock": cachedProducts.filter(p => p.status == 2).length,
            stopped: cachedProducts.filter(p => p.status == 3).length
        };

        // ✅ 각 상태 카드에 개수 반영
        document.querySelectorAll(".status-card").forEach(card => {
            const status = card.dataset.status;
            const countElement = card.querySelector(".count");
            if (countElement && statusCounts.hasOwnProperty(status)) {
                countElement.innerText = statusCounts[status];
            }
        });
    }


    // ✅ 상품 데이터 불러오기 (백엔드 API 연동)
    async function fetchProducts(initialLoad = true) {
        if (!initialLoad) {
            updateStatusCounts(); // 🔥 기존 데이터 사용 시 개수 업데이트만 실행
            return;
        }

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

            if (!Array.isArray(data)) {
                console.error("🚨 서버 응답이 배열이 아닙니다:", data);
                throw new Error("서버 응답이 올바른 상품 목록(JSON 배열)이 아닙니다.");
            }

            console.log("✅ 상품 데이터 불러오기 성공:", data);
            renderProductList(cachedProducts); // UI 업데이트
            updateStatusCounts();
        } catch (error) {
            console.error("🚨 상품 데이터를 불러오는 중 오류 발생:", error);
            alert("상품 정보를 불러오지 못했습니다. 관리자에게 문의하세요.");
        }
    }



    // ✅ 상품 목록 필터링 & 렌더링
    // ✅ 필터링 + 정렬을 한 번에 수행하는 함수
    function filterAndSortProducts() {
        const activeStatus = document.querySelector(".status-card.active")?.dataset.status;
        const searchText = searchInput.value.trim().toLowerCase();
        const tagText = tagInput.value.trim().toLowerCase();
        const startDate = dateInputs[0].value ? new Date(dateInputs[0].value) : null;
        const endDate = dateInputs[1].value ? new Date(dateInputs[1].value) : null;
        const sortOption = sortSelect.value;

        let filteredProducts = [...cachedProducts]; // ✅ 기존 데이터 복사 후 필터 적용

        // 🔄 상태 필터 적용
        if (activeStatus && activeStatus !== "full") {
            filteredProducts = filteredProducts.filter(product => product.status == activeStatus);
        }

        // 🔍 상품명 또는 상품번호 검색
        if (searchText) {
            filteredProducts = filteredProducts.filter(product =>
                product.itemName.toLowerCase().includes(searchText) ||
                product.productId.toString().includes(searchText)
            );
        }

        // 🔍 태그 검색
        if (tagText) {
            filteredProducts = filteredProducts.filter(product =>
                product.tags.some(tag => tag.toLowerCase().includes(tagText))
            );
        }

        // 📅 날짜 범위 검색
        if (startDate || endDate) {
            filteredProducts = filteredProducts.filter(product => {
                const productDate = new Date(product.createdTime);
                if (startDate && productDate < startDate) return false;
                if (endDate && productDate > endDate) return false;
                return true;
            });
        }

        // 🔽 정렬 적용 (필터된 데이터에서 정렬 수행)
        switch (sortOption) {
            case "최근등록순":
                filteredProducts.sort((a, b) => new Date(b.createdTime) - new Date(a.createdTime));
                break;
            case "오래된등록순":
                filteredProducts.sort((a, b) => new Date(a.createdTime) - new Date(b.createdTime));
                break;
            case "가격높은순":
                filteredProducts.sort((a, b) => b.price - a.price);
                break;
            case "가격낮은순":
                filteredProducts.sort((a, b) => a.price - b.price);
                break;
        }

        renderProductList(filteredProducts); // 🔥 UI 업데이트
    }

    // ✅ 검색 버튼 클릭 이벤트에 필터 + 정렬 함수 적용
    searchButton.addEventListener("click", () => {
        filterAndSortProducts();
    });

    // ✅ 초기화 버튼 클릭 시 필터 및 정렬 초기화
    resetButton.addEventListener("click", () => {
        searchInput.value = "";
        tagInput.value = "";
        dateInputs.forEach(input => input.value = "");

        filterAndSortProducts(); // 🔥 전체 목록 다시 표시 (필터 해제된 상태로)
    });

    // ✅ 정렬 옵션 변경 시 필터된 상태에서 정렬 유지
    sortSelect.addEventListener("change", () => {
        filterAndSortProducts();
    });

    // ✅ 상태 카드 클릭 시 필터 + 정렬 업데이트
    document.querySelectorAll(".status-card").forEach(card => {
        card.addEventListener("click", () => {
            document.querySelectorAll(".status-card").forEach(c => c.classList.remove("active"));
            card.classList.add("active");
            filterAndSortProducts(); // 🔥 필터 + 정렬 적용
        });
    });




    // ✅ 상품 목록 렌더링
    function renderProductList(products) {
        const productList = document.getElementById("product-list");
        const productCount = document.getElementById("product-count");

        productList.innerHTML = "";
        productCount.innerText = products.length;

        if (products.length === 0) {
            productList.innerHTML = `
            <tr>
                <td colspan="10"> <!-- 🔥 테이블 전체 칸 차지하도록 수정 -->
                    <div class="empty-message">
                        <svg xmlns="http://www.w3.org/2000/svg" width="24" height="24" viewBox="0 0 24 24"
                            fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round"
                            stroke-linejoin="round">
                            <circle cx="12" cy="12" r="10" />
                            <line x1="12" y1="8" x2="12" y2="12" />
                            <line x1="12" y1="16" x2="12.01" y2="16" />
                        </svg>
                        데이터가 존재하지 않습니다.
                    </div>
                </td>
            </tr>
        `;
            return;
        }

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

            // ✅ 삭제된 상품을 캐시 데이터에서 제거
            cachedProducts = cachedProducts.filter(product => product.productId !== parseInt(productId));

            // ✅ UI에서 삭제 후 개수 업데이트
            document.querySelector(`tr[data-id='${productId}']`)?.remove();
            updateStatusCounts(); // 🔥 개수 업데이트
            renderProductList(cachedProducts); // ✅ UI 갱신

        } catch (error) {
            console.error("🚨 상품 삭제 중 오류 발생:", error);
            alert("상품 삭제에 실패했습니다.");
        }
    }



    // ✅ 상품 상태 업데이트
    async function updateProductStatus(productId, newStatus) {
        try {
            const response = await fetch(`/api/creator/dashboard/products/${productId}/status`, {
                method: "PATCH",
                headers: { "Content-Type": "application/json" },
                body: JSON.stringify({ status: parseInt(newStatus, 10) })
            });

            if (!response.ok) throw new Error("상품 상태 업데이트 실패");

            // ✅ 캐시 데이터에서 상태 업데이트
            cachedProducts = cachedProducts.map(product =>
                product.productId == productId ? { ...product, status: parseInt(newStatus, 10) } : product
            );

            updateStatusCounts(); // 🔥 개수 업데이트
            renderProductList(cachedProducts); // ✅ UI 갱신

        } catch (error) {
            console.error("🚨 상품 상태 변경 중 오류 발생:", error);
        }
    }


    function openProductCreatePage() {
        const url = window.location.origin + "/product/regist";
        const newWindow = window.open(url, "_blank");

        // ✅ 새 창이 닫히면 상품 데이터 다시 불러오기
        const timer = setInterval(() => {
            if (newWindow.closed) {
                clearInterval(timer);
                fetchProducts(); // 🔥 상품 목록 다시 불러오기 → 자동으로 개수 업데이트됨
            }
        }, 1000);
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
            const response = await fetch(`/api/creator/dashboard/products/${productId}`, {
                method: "PATCH",
                headers: { "Content-Type": "application/json" },
                body: JSON.stringify({ itemDescription: newDescription })
            });

            if (!response.ok) throw new Error("설명 업데이트 실패");

            // ✅ 캐시 데이터에서 설명 업데이트
            cachedProducts = cachedProducts.map(product =>
                product.productId == productId ? { ...product, itemDescription: newDescription } : product
            );

            renderProductList(cachedProducts); // ✅ UI 갱신
            closeDescriptionModal(); // ✅ 저장 후 모달 닫기

        } catch (error) {
            console.error("🚨 설명 수정 중 오류 발생:", error);
        }
    });



    fetchProducts(); // 데이터 불러오기 실행
}

// 🚀 fragment가 변경될 때마다 JS를 다시 실행하도록 설정
document.addEventListener("reapplyEventListeners", initProductManagement);
