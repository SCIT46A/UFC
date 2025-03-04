//datepicker
$(function () {
    $(".datepicker").datepicker();
});

$(function () {
    //input을 datepicker로 선언
    $(".datepicker").datepicker({
        dateFormat: "yy-mm-dd", //달력 날짜 형태
        showOtherMonths: true, //빈 공간에 현재월의 앞뒤월의 날짜를 표시
        showMonthAfterYear: true, // 월- 년 순서가아닌 년도 - 월 순서
        changeYear: true, //option값 년 선택 가능
        changeMonth: true, //option값  월 선택 가능
        showOn: "both", //button:버튼을 표시하고,버튼을 눌러야만 달력 표시 ^ both:버튼을 표시하고,버튼을 누르거나 input을 클릭하면 달력 표시
        buttonImage:
            "http://jqueryui.com/resources/demos/datepicker/images/calendar.gif", //버튼 이미지 경로
        buttonImageOnly: true, //버튼 이미지만 깔끔하게 보이게함
        buttonText: "선택", //버튼 호버 텍스트
        yearSuffix: "년", //달력의 년도 부분 뒤 텍스트
        monthNamesShort: [
            "1월",
            "2월",
            "3월",
            "4월",
            "5월",
            "6월",
            "7월",
            "8월",
            "9월",
            "10월",
            "11월",
            "12월",
        ], //달력의 월 부분 텍스트
        monthNames: [
            "1월",
            "2월",
            "3월",
            "4월",
            "5월",
            "6월",
            "7월",
            "8월",
            "9월",
            "10월",
            "11월",
            "12월",
        ], //달력의 월 부분 Tooltip
        dayNamesMin: ["일", "월", "화", "수", "목", "금", "토"], //달력의 요일 텍스트
        dayNames: [
            "일요일",
            "월요일",
            "화요일",
            "수요일",
            "목요일",
            "금요일",
            "토요일",
        ], //달력의 요일 Tooltip
        minDate: "-5Y", //최소 선택일자(-1D:하루전, -1M:한달전, -1Y:일년전)
        maxDate: "+5y", //최대 선택일자(+1D:하루후, -1M:한달후, -1Y:일년후)
    });
});

$(".timepicker").timepicker({
    timeFormat: "HH:mm",
    interval: 60,
    minTime: "00:00",
    maxTime: "23:00pm",
    startTime: "00:00",
    dynamic: false,
    dropdown: true,
    scrollbar: true,
});

// 클릭 시 위치가 아래쪽에 생겨서 변경하기 위한 js
// 클래스가 "datepicker"인 input 요소가 클릭되었을 때 js
jQuery(".datepicker").datepicker({
    beforeShow: function (input) {
        // 클릭 이벤트가 발생한 위치를 가져옵니다.
        var clickX = jQuery(input).offset().left;
        var clickY = jQuery(input).offset().top + jQuery(input).outerHeight();
        // datepicker의 위치를 클릭 이벤트가 발생한 위치로 설정합니다.
        setTimeout(function () {
            jQuery("#ui-datepicker-div").css({
                left: clickX + "px",
                top: clickY - 70 + "px",
            });
        });
    },
});

// 클래스가 "timepicker"인 input 요소가 클릭되었을 때 js
jQuery(document).on("focus", ".timepicker", function () {
    // 클릭된 input 요소를 선택
    var input = jQuery(this);
    // input 요소의 위치와 값을 로그에 출력
    var clickX = jQuery(input).offset().left;
    var clickY = jQuery(input).offset().top + jQuery(input).outerHeight() - 70;
    setTimeout(function () {
        jQuery(".ui-timepicker-container").css({
            left: clickX + "px",
            top: clickY,
        });
    }, 0);
});
