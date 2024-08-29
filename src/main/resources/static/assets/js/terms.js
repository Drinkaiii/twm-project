$(document).ready(function () {
    // Scroll到底部自動勾選checkbox、button可用
    $(".content").scroll(function () {
        maxScrollHeight = $(".content").prop("scrollHeight") - $(".content").height() - 0;
        if (maxScrollHeight - $(this).scrollTop() <= 50) {
            $("input#checkbox").prop("checked", true);
            $(".btn.disabled").removeClass("disabled");
        }
    });
    // 手動勾選checkbox，button可用
    $("input#checkbox").click(function () {
        if ($(this).is(":checked")) {
            $(".btn.disabled").removeClass("disabled");
        } else {
            $(".btn").addClass("disabled");
        }
    });
    const nextButton = document.querySelector(".termsBox a");
    const userId = localStorage.getItem('userId');

    nextButton.addEventListener('click',function (){
        if(!nextButton.classList.contains('disabled')){
            fetch(`api/1.0/user/update-authTime?userId=${userId}`)
                .then(response => {
                    if(response.ok){
                        window.location.href = '../chat.html';
                    }else{
                        throw new error("fail to update authTime");
                    }
                })
                .catch(error => {
                    //系統異常，導回登入頁面。
                    //刪除email,id,token。
                })
        }

    })

});