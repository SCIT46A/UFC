document.addEventListener("DOMContentLoaded", function () {
    console.log("✅ [INIT] main-dashboard.js 로드 완료");

    let campaignChart, ordersChart, revenueChart, productChart;

    // ✅ 대시보드 데이터 불러오기
    async function fetchDashboardData() {
        const creatorId = document.getElementById("creatorId")?.value;
        if (!creatorId) {
            console.error("❌ [ERROR] creatorId가 없음");
            return;
        }

        try {
            const response = await fetch(`/api/creator/dashboard/main?creatorId=${creatorId}`);
            if (!response.ok) throw new Error("대시보드 데이터를 가져오는 데 실패했습니다.");

            const data = await response.json();
            console.log("📊 [DATA] 대시보드 데이터 로드 완료:", data);

            if (!data || Object.keys(data).length === 0) {
                console.error("❌ [ERROR] API에서 빈 데이터를 반환했습니다.");
                return;
            }

            updateDashboardStats(data); // 🔹 카드 통계 데이터 업데이트
            updateCharts(data); // 🔹 차트 데이터 업데이트
        } catch (error) {
            console.error("❌ [ERROR] 대시보드 데이터 불러오기 실패:", error);
        }
    }

    // ✅ 대시보드 통계 카드 업데이트
    function updateDashboardStats(data) {
        document.querySelector(".stat-card:nth-child(1) .stat-value").textContent = `₩${data.totalSales.toLocaleString()}`;
        document.querySelector(".stat-card:nth-child(2) .stat-value").textContent = data.newOrders;
        document.querySelector(".stat-card:nth-child(3) .stat-value").textContent = data.activeCampaigns;
        document.querySelector(".stat-card:nth-child(4) .stat-value").textContent = data.campaignLikes;
    }

    // ✅ 차트 업데이트 함수
    function updateCharts(data) {
        if (!data) {
            console.error("❌ [ERROR] 차트 데이터를 받을 수 없음");
            return;
        }

        console.log("🔹 차트 데이터:", data);

        updateCampaignChart(data.campaigns || []);
        updateOrdersChart(data.orders || []);
        updateRevenueChart(data.revenue || []);
        updateProductChart(data.products || []);
    }

    // ✅ 캠페인 달성률 차트 업데이트
    function updateCampaignChart(campaigns) {
        const canvas = document.getElementById("campaignChart");
        if (!canvas) {
            console.error("❌ [ERROR] 캠페인 차트를 찾을 수 없음");
            return;
        }

        const ctx = canvas.getContext("2d");
        if (!ctx) {
            console.error("❌ [ERROR] 캠페인 차트의 2D 컨텍스트를 가져올 수 없음");
            return;
        }

        if (campaignChart) campaignChart.destroy(); // 🔹 기존 차트 제거

        campaignChart = new Chart(ctx, {
            type: "bar",
            data: {
                labels: campaigns.map(c => c.name),
                datasets: [{
                    label: "달성률 (%)",
                    data: campaigns.map(c => c.achievementRate),
                    backgroundColor: "#4361ee",
                    borderRadius: 6,
                    barThickness: 20
                }]
            },
            options: {
                responsive: true,
                maintainAspectRatio: false,
                plugins: {
                    legend: { display: false },
                    tooltip: {
                        callbacks: {
                            label: context => `${context.parsed.y.toFixed(1)}%`
                        }
                    }
                },
                scales: {
                    y: {
                        ticks: {
                            callback: value => `${value}%`
                        }
                    }
                }
            }
        });
    }


    // ✅ 신규 주문 현황 차트 업데이트
    function updateOrdersChart(orders) {
        const ctx = document.getElementById("ordersChart")?.getContext("2d");
        if (!ctx) return;
        if (ordersChart) ordersChart.destroy();

        // x축 레이블: 각 날짜 (쿼리에서 "date" 키 사용)
        const labels = orders.map(o => o.date);

        // 재구조화: 각 상품별로 주문 건수를 날짜별 배열로 구성
        const productDataMap = {};
        orders.forEach(order => {
            order.products.forEach(prod => {
                if (!productDataMap[prod.name]) {
                    productDataMap[prod.name] = Array(labels.length).fill(0);
                }
                const idx = labels.indexOf(order.date);
                if (idx !== -1) {
                    productDataMap[prod.name][idx] = prod.quantity;
                }
            });
        });

        const datasets = Object.keys(productDataMap).map(productName => ({
            label: productName,
            data: productDataMap[productName],
            borderColor: "#4361ee", // 필요에 따라 색상 매핑
            backgroundColor: "rgba(67, 97, 238, 0.1)",
            tension: 0.3,
            fill: false
        }));

        ordersChart = new Chart(ctx, {
            type: "line",
            data: {
                labels,
                datasets
            },
            options: {
                responsive: true,
                maintainAspectRatio: false,
                plugins: { legend: { position: "top" } }
            }
        });
    }

    // ✅ 매출 개요 차트 업데이트
    function updateRevenueChart(revenue) {
        const ctx = document.getElementById("revenueChart")?.getContext("2d");
        if (!ctx) return;

        if (revenueChart) revenueChart.destroy(); // 🔹 기존 차트 제거

        revenueChart = new Chart(ctx, {
            type: "line",
            data: {
                labels: revenue.map(r => r.date),
                datasets: [{
                    label: "매출 (원)",
                    data: revenue.map(r => r.amount),
                    borderColor: "#4361ee",
                    backgroundColor: "rgba(67, 97, 238, 0.1)",
                    tension: 0.3,
                    fill: true
                }]
            },
            options: {
                responsive: true,
                maintainAspectRatio: false,
                plugins: {
                    legend: { display: false },
                    tooltip: {
                        callbacks: {
                            label: context => `₩${context.parsed.y.toLocaleString()}`
                        }
                    }
                },
                scales: {
                    y: {
                        ticks: {
                            callback: value => `₩${value.toLocaleString()}`
                        }
                    }
                }
            }
        });
    }

    // ✅ 상품 판매 비율 차트 업데이트
    function updateProductChart(products) {
        const ctx = document.getElementById("productChart")?.getContext("2d");
        if (!ctx) return;

        if (productChart) productChart.destroy(); // 🔹 기존 차트 제거

        productChart = new Chart(ctx, {
            type: "doughnut",
            data: {
                labels: products.map(p => p.name),
                datasets: [{
                    data: products.map(p => p.percentage),
                    backgroundColor: ["#4361ee", "#f72585", "#4cc9f0", "#4895ef", "#3f37c9"],
                    borderWidth: 0
                }]
            },
            options: {
                responsive: true,
                maintainAspectRatio: false,
                plugins: {
                    legend: { position: "right" },
                    tooltip: {
                        callbacks: {
                            label: context => `${context.label}: ${context.parsed.toFixed(1)}%`
                        }
                    }
                }
            }
        });
    }

    // ✅ 초기화 함수 실행
    function initMainDashboard() {
        console.log("🚀 [INIT] 대시보드 데이터 로드 시작...");
        fetchDashboardData();
    }

    // ✅ initMainDashboard를 글로벌 스코프에 추가
    window.initMainDashboard = initMainDashboard;
});
