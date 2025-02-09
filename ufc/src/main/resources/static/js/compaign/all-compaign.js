$(function () {

    $.ajax({
        url: `/campaign/all/find`, // URL 인코딩 추가
        method: "GET",
        success: (response) => {
            console.log(response)
            response.forEach((data)=>{
                    $(".main-bo-in-bo").append(`
                <div class="main-bo-in-bo-pe">
                    <div class="main-bo-in-bo-pe-box">
                        <a
                            href="/"
                            class="main-bo-in-bo-pe-box-a"
                        >
                            <div
                                class="main-bo-in-bo-pe-box-a-img"
                            >
                                <img
                                    src="https://img.tumblbug.com/eyJidWNrZXQiOiJ0dW1ibGJ1Zy1pbWctYXNzZXRzIiwia2V5IjoiY292ZXIvODg3ODQ0Y2YtZTIwZS00MWRkLTg5MDMtYjJlMWNmOTFkYmI2LzMyNWIzNDU3LTkyNDUtNDg3MS1hYTUxLTgwYmJlZGZlYTU0Yi5qcGVnIiwiZWRpdHMiOnsicmVzaXplIjp7IndpZHRoIjo0NjUsImhlaWdodCI6NDY1LCJ3aXRob3V0RW5sYXJnZW1lbnQiOnRydWV9fX0="
                                    alt=""
                                    class="main-bo-in-bo-pe-box-a-img-size"
                                />
                                <!-- 좋아요부분  -->
                                <div
                                    class="main-like-btn"
                                >
                                    <svg
                                        width="40"
                                        height="40"
                                        viewBox="0 0 40 40"
                                        fill="none"
                                        xmlns="http://www.w3.org/2000/svg"
                                    >
                                        <path
                                            d="M19.463 15.3087L19.9993 15.7933L20.5356 15.3087L22.2643 13.747C22.2643 13.747 22.2643 13.7469 22.2643 13.7469C23.615 12.5269 25.9852 12.7532 27.3097 14.2118L27.3156 14.2182L27.3216 14.2246C28.9912 15.9843 29.0145 19.0158 27.2167 20.9218L19.9995 27.9864L12.7818 20.9218C10.9839 19.0158 11.0075 15.984 12.6769 14.2246L12.6829 14.2182L12.6888 14.2118C14.0133 12.7532 16.3836 12.5269 17.7343 13.7469C17.7343 13.7469 17.7343 13.7469 17.7344 13.747L19.463 15.3087Z"
                                            fill="black"
                                            fill-opacity="0.25"
                                            stroke="white"
                                            stroke-width="1.6"
                                        ></path>
                                        <g
                                            filter="url(#filter0_dd_909_4706012)"
                                        >
                                            <path
                                                fill-rule="evenodd"
                                                clip-rule="evenodd"
                                                d="M19.9992 17.111L17.0789 14.4725C17.0789 14.4725 17.079 14.4726 17.0789 14.4725C16.1903 13.67 14.4337 13.7447 13.4126 14.8691L13.3995 14.8835L13.3861 14.8976C12.0903 16.2633 12.039 18.6949 13.4805 20.2374L19.9994 26.6181L26.518 20.2374C27.9595 18.6949 27.9084 16.2637 26.6123 14.8976L26.5989 14.8835L26.5859 14.8691C25.5648 13.7447 23.8085 13.6699 22.9198 14.4725C22.9198 14.4725 22.9198 14.4724 22.9198 14.4725L19.9992 17.111ZM21.728 13.1533C23.4567 11.5918 26.3291 11.942 27.902 13.6739C29.8776 15.7563 29.8775 19.2796 27.7872 21.4827L19.9995 29.1058L12.2112 21.4827C10.1209 19.2796 10.121 15.7559 12.0965 13.6739C13.6694 11.942 16.5419 11.5917 18.2706 13.1533L19.9993 14.7151L21.728 13.1533Z"
                                                fill="white"
                                            ></path>
                                        </g>
                                    </svg>
                                </div>
                            </div>
                            <div
                                class="main-bo-in-bo-pe-box-a-title"
                            >
                                <!-- 상단 내용 -->
                                <div
                                    class="main-bo-in-bo-pe-box-a-title-top"
                                >
                                    <div
                                        class="main-bo-in-bo-pe-box-a-title-top-se"
                                    >
                                        <div>
                                            판매자는
                                            판매판매
                                        </div>
                                    </div>
                                    <div
                                        class="main-bo-in-bo-pe-box-a-title-mi"
                                    >
                                        <div
                                            class="main-bo-in-bo-pe-box-a-title-mi-title"
                                        >
                                            완벽적중<한국풍
                                            판타지
                                            타로카드:삼라만상>
                                        </div>
                                        <div
                                            class="main-bo-in-bo-pe-box-a-title-mi-content"
                                        >
                                            화려한
                                            자개박으로
                                            꾸며진
                                            나전칠기
                                            컨셉의 한국
                                            전통
                                            판타지풍
                                            타로카드
                                            78장
                                        </div>
                                    </div>
                                    <div
                                        class="main-bo-in-bo-pe-box-a-title-bo"
                                    >
                                        <div
                                            class="main-bo-in-bo-pe-box-a-title-bo-in"
                                        ></div>
                                    </div>
                                </div>
                                <!-- 펀딩쪽 -->
                                <div
                                    class="main-funding"
                                >
                                    <div
                                        class="main-funding-top"
                                    >
                                        <div>
                                            <span
                                                class="main-funding-top-per"
                                                >1234%</span
                                            >
                                            <span
                                                class="main-funding-top-pri"
                                                >12개
                                                모임</span
                                            >
                                        </div>
                                        <em
                                            >28일
                                            남음</em
                                        >
                                    </div>
                                    <div
                                        class="main-funding-bo"
                                    ></div>
                                </div>
                            </div>
                        </a>
                    </div>
                </div>
                `)

            })
            initializeEventListeners();
        },

    });





    function initializeEventListeners() {
        // Lucide Icons Initialization
        if (window.lucide) {
            lucide.createIcons();
        } else {
            console.error("Lucide not loaded");
        }

        // Current Year Update
        const yearElement = document.getElementById("current-year");
        if (yearElement) {
            yearElement.textContent = new Date().getFullYear();
        }
        document.addEventListener("DOMContentLoaded", () => {
            const perModal = document.querySelector(".main-modal-per");
            const perBtn = document.querySelector(
                ".main-top-in-content-target-box-span-btn"
            );
            const ModalClose = document.querySelector(".main-modal-per-close");
            const reBModal = document.querySelector(".main-modal-per-add");
            const reBtn = document.querySelector(
                ".main-top-in-content-target-box-span-btn-re"
            );

            perBtn.addEventListener("click", () => {
                perModal.style.display = "flex";
                ModalClose.style.display = "block";
            });

            ModalClose.addEventListener("click", () => {
                perModal.style.display = "none";
                reBModal.style.display = "none";
                ModalClose.style.display = "none";
            });

            reBtn.addEventListener("click", () => {
                reBModal.style.display = "flex";
                ModalClose.style.display = "block";
            });
        });
    }
});