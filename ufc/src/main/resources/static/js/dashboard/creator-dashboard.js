document.addEventListener("DOMContentLoaded", function () {
    const contentWrapper = document.querySelector(".content-wrapper");

    console.log("✅ [INIT] dashboard.js 로드 완료");


    // ✅ 기존 프로필 이미지 업로드 기능 제거 후, 새 코드 적용
    const profileContainer = document.getElementById("profileContainer");
    const fileInput = document.getElementById("profileUpload");
    const profileImg = document.getElementById("profileImg");
    const creatorId = document.getElementById("creatorId")?.value; // ✅ 크리에이터 ID 가져오기

    if (profileContainer && fileInput) {
        // 🔹 프로필 이미지 클릭 시 파일 선택 창 열기
        profileContainer.addEventListener("click", function () {
            fileInput.click();
        });

        // 🔹 파일 선택 후 미리보기 & API 호출
        fileInput.addEventListener("change", async function (event) {
            const file = event.target.files[0];
            if (!file) return;

            // ✅ 미리보기 기능
            const reader = new FileReader();
            reader.onload = function (e) {
                profileImg.src = e.target.result; // 미리보기 설정
            };
            reader.readAsDataURL(file);

            try {
                // ✅ 1. Cloudflare에 이미지 업로드
                const formData = new FormData();
                formData.append("file", file);

                const uploadResponse = await fetch("/api/image/upload", {
                    method: "POST",
                    body: formData,
                    headers: { "Accept": "application/json" }
                });

                if (!uploadResponse.ok) throw new Error("이미지 업로드에 실패했습니다.");

                const imageIdText = await uploadResponse.text();
                const responseData = { imageId: imageIdText };

                const imageId = responseData.imageId;

                const dbResponse = await fetch("/creator/profile/image", {
                    method: "PATCH",
                    headers: { "Content-Type": "application/json" },
                    body: JSON.stringify({
                        creatorId: creatorId,
                        profileImgId: imageId
                    })
                });

                if (!dbResponse.ok) throw new Error("프로필 이미지 저장에 실패했습니다.");
            } catch (error) {
                console.error("❌ 오류 발생:", error.message);
            }
        });
    }
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

        // ✅ `creatorId`를 유지
        const creatorId = document.getElementById("creatorId")?.value || "";
        const fullUrl = creatorId ? `${url}?creatorId=${creatorId}` : url;

        fetch(fullUrl)
            .then(response => {
                if (!response.ok) throw new Error(`❌ [ERROR] HTTP 오류: ${response.status}`);
                return response.text();
            })
            .then(html => {
                console.log(`✅ [FRAGMENT] ${url} 로드 완료!`);
                contentWrapper.innerHTML = html;

                // ✅ `creatorId` hidden input 유지
                if (creatorId) {
                    const input = document.createElement("input");
                    input.type = "hidden";
                    input.id = "creatorId";
                    input.value = creatorId;
                    contentWrapper.appendChild(input);
                }

                executePageScripts(url, creatorId); // ✅ 해당 fragment의 JS 실행
            })
            .catch(error => console.error("❌ [ERROR] 페이지 로딩 오류:", error));
    }

    function executePageScripts(url, creatorId) {
        const scriptMapping = {
            "/creator/dashboard/products/management": { script: "/js/dashboard/product-management.js", init: "initProductManagement" },
            "/creator/dashboard/products/orders": { script: "/js/dashboard/product-orders.js", init: "initProductOrders" },
            "/creator/dashboard/settlements": { script: "/js/dashboard/settlements.js", init: "initSettlementManagement" },
            "/creator/dashboard/campaigns/management": { script: "/js/dashboard/campaign-management.js", init: "initCampaignManagement" },
            "/creator/dashboard/campaigns/donation/orders": { script: "/js/dashboard/donation-orders.js", init: "initDonationOrders" },
            "/creator/dashboard/campaigns/reward/delivery": { script: "/js/dashboard/reward-delivery.js", init: "initRewardDeliveryManagement" },
            "/creator/dashboard/inquiries": { script: "/js/dashboard/inquiries.js", init: "initInquiriesManagement" }
        };


        // ✅ iframe을 사용한 페이지는 스크립트 실행 필요 없음
        if (["/creator/dashboard/products/register", "/creator/dashboard/campaigns/register"].includes(url)) {
            console.log(`⚠️ [SKIP] ${url}은 iframe으로 로드되므로 init 함수 실행 안 함.`);
            return;
        }

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
                    window[init]();
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

    function loadFragment(url) {
        console.log(`🔄 [FRAGMENT] ${url} 로드 시작...`);

        const contentWrapper = document.querySelector(".content-wrapper");

        const iframeMapping = {
            "/creator/dashboard/products/register": "/product/regist?iframe=true",
            "/creator/dashboard/campaigns/register": "/campaign/create?iframe=true"
        };

        const iframePages = Object.keys(iframeMapping);

        if (iframePages.includes(url)) {
            console.log(`🔄 [IFRAME] ${url}을 iframe으로 로드`);

            contentWrapper.innerHTML = "";

            const iframe = document.createElement("iframe");
            iframe.src = iframeMapping[url];
            iframe.width = "100%";
            iframe.height = "1000px"; // 필요에 따라 높이 조절
            iframe.style.border = "none";

            // ✅ iframe 내부에서는 `footer.js` 실행 방지
            iframe.onload = function () {
                console.log("✅ [IFRAME LOADED] " + url);
                iframe.contentWindow.postMessage({ hideFooter: true }, "*");
            };

            contentWrapper.appendChild(iframe);
        } else {
            fetch(url)
                .then(response => {
                    if (!response.ok) throw new Error(`❌ [ERROR] HTTP 오류: ${response.status}`);
                    return response.text();
                })
                .then(html => {
                    console.log(`✅ [FRAGMENT] ${url} 로드 완료!`);
                    contentWrapper.innerHTML = html;
                    executePageScripts(url);
                })
                .catch(error => console.error("❌ [ERROR] 페이지 로딩 오류:", error));
        }
    }

    window.addEventListener("message", function (event) {
        if (event.data.hideFooter) {
            console.log("⚠️ [INFO] iframe에서 footer 숨기기");
            const footer = document.querySelector("footer");
            if (footer) {
                footer.style.display = "none"; // ✅ footer를 숨김
            }
        }
    });
});
