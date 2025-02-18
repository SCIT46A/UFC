document.addEventListener("DOMContentLoaded", function () {
    const contentWrapper = document.querySelector(".content-wrapper");

    console.log("✅ [INIT] dashboard.js 로드 완료");

    // ✅ 메뉴 클릭 이벤트 (이벤트 위임 적용)
    document.addEventListener("click", function (event) {
        const menuItem = event.target.closest(".menu-item[data-url]");
        if (menuItem) {
            event.preventDefault();
            const url = menuItem.getAttribute("data-url");
            console.log(`📌 [MENU CLICK] 메뉴 클릭 감지 → ${url}`);

            loadFragment(url);

            // 현재 선택된 메뉴 active 처리
            document.querySelectorAll(".menu-item").forEach(menu => menu.classList.remove("active"));
            menuItem.classList.add("active");
        }
    });

    // ✅ 하위 메뉴 토글 기능 (이벤트 위임 적용)
    document.addEventListener("click", function (event) {
        const parentMenu = event.target.closest(".parent-menu");
        if (parentMenu) {
            const targetMenu = document.getElementById(parentMenu.getAttribute("data-toggle") + "-menu");
            if (targetMenu) {
                targetMenu.classList.toggle("active");
                console.log(`🔄 [MENU] 하위 메뉴 토글: ${parentMenu.getAttribute("data-toggle")}`);
            }
        }
    });

    function loadFragment(url) {
        console.log(`🔄 [FRAGMENT] ${url} 로드 시작...`);

        fetch(url)
            .then(response => {
                if (!response.ok) throw new Error(`❌ [ERROR] HTTP 오류: ${response.status}`);
                return response.text();
            })
            .then(html => {
                console.log(`✅ [FRAGMENT] ${url} 로드 완료!`);
                contentWrapper.innerHTML = html;
                executePageScripts(url); // ✅ 해당 fragment의 JS 실행
            })
            .catch(error => console.error("❌ [ERROR] 페이지 로딩 오류:", error));
    }

    function executePageScripts(url) {
        const scriptMapping = {
            "/dashboard/delivery": { script: "/js/dashboard/delivery.js", init: "initDeliveryManagement" },
            "/dashboard/products/register": { script: "/js/dashboard/product-management.js", init: "initProductManagement" },
            "/dashboard/products/management": { script: "/js/dashboard/product-management.js", init: "initProductManagement" },
            "/dashboard/products/orders": { script: "/js/dashboard/product-orders.js", init: "initProductOrders" },
            "/dashboard/settlements": { script: "/js/dashboard/settlements.js", init: "initSettlementManagement" },
            "/dashboard/campaigns/register": { script: "/js/dashboard/campaign-management.js", init: "initCampaignManagement" },
            "/dashboard/campaigns/management": { script: "/js/dashboard/campaign-management.js", init: "initCampaignManagement" },
            "/dashboard/campaigns/donation/management": { script: "/js/dashboard/donation-management.js", init: "initDonationManagement" },
            "/dashboard/inquiries": { script: "/js/dashboard/inquiries.js", init: "initInquiriesManagement" }
        };

        // 기존 동적 스크립트 태그 제거
        document.querySelectorAll(".dynamic-script").forEach(script => script.remove());

        if (scriptMapping[url]) {
            const { script, init } = scriptMapping[url];

            // ✅ 스크립트 로딩 후 해당 JS의 초기화 함수 실행
            const newScript = document.createElement("script");
            newScript.src = script;
            newScript.defer = true;
            newScript.classList.add("dynamic-script");

            newScript.onload = function () {
                console.log(`✅ [SCRIPT LOADED] ${script} 실행 완료`);
                if (typeof window[init] === "function") {
                    console.log(`⚡ [INIT CALL] ${init} 실행`);
                    window[init](); // ✅ JS 파일에서 선언된 초기화 함수 실행
                } else {
                    console.warn(`⚠️ [WARNING] ${init} 함수가 정의되지 않음.`);
                }
            };

            newScript.onerror = function () {
                console.error(`❌ [ERROR] ${script} 로딩 실패`);
            };

            document.body.appendChild(newScript);
        } else {
            console.log(`⚠️ [WARNING] ${url}에 대한 매핑된 JS 없음`);
        }
    }
});
