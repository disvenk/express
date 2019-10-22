var token = sessionStorage.getItem('token');
var userStatus = sessionStorage.getItem('userStatus');
if(!token){

}else{
    // if(userStatus == 1){
    //     window.location.href="../homePage/selectSchoolHtml";
    // }
    // else{
    //     window.location.href="../homePage/homePageHtml";
    // }
}
var waitTime=60;
var waitStatus=false;
var phoneNumber = $('#phone');
var regPhoneNum = /^1(3|4|5|7|8)\d{9}$/;
phoneNumber.on('input propertychange',function(){
    if(!regPhoneNum.test($('#phone').val())){
        $('#getCode').css('background-color', '#D9D9D9');
        $('#getCode').css('color', '#FFFFFF')
    }else{
        $('#getCode').css('background-color', 'rgb(48,157,237)')
    }
});
function getCode() {
    if(waitStatus){
        return;
    }
    if(phoneNumber.val()==null||phoneNumber.val()==""){
        layer.msg("请输入手机号");
        return;
    }
    if(!regPhoneNum.test(phoneNumber.val())){
        layer.msg("手机号不合法");
        return;
    }
    waitStatus=true;
    $.ajax({
        url: "../verify/verificationCode",
        type: "post",
        contentType: "application/json; charset=utf-8",
        dataType: "json",
        data:JSON.stringify({phonenumber:phoneNumber.val()}),
        success: function (data) {
            if(data.stateCode==100){
                layer.msg("发送成功");
                var obtain=$("#getCode");
                waitTime--;
                obtain.text(waitTime+"秒");
                //假定此时发送成功
                var timer=setInterval(function () {
                    waitTime--;
                    obtain.text(waitTime+"秒");
                    if(waitTime<1){
                        clearInterval(timer);
                        waitTime=60;
                        waitStatus=false;
                        obtain.text("获取验证码");
                        $('#getCode').css('background-color', '#d9d9d9')
                    }
                },1000)
            }else {
                layer.msg(data.message);
                return;
            }

        },
        error: function (e) {
            console.log(e);
        }
    });
}
//登录
function login() {
    if(phoneNumber.val()==null||phoneNumber.val()==""){
        layer.msg("请输入手机号");
        return;
    }
    if(!regPhoneNum.test(phoneNumber.val())){
        layer.msg("手机号不合法");
        return;
    }
    if($('#getCode').val()==null){
        layer.msg("请输入验证码");
        return;
    }
    var data = {
        "phoneNumber": phoneNumber.val(),
        "verificationCode": $("#code").val(),
        "loginType": 1
    };
    $.ajax({
        type: "POST",
        url: "../account/verifyLogin",
        contentType: "application/json; charset=utf-8",
        headers: {
            "Accept": "application/json; charset=utf-8"
        },
        data: JSON.stringify(data),
        success: function (res) {
            if(res.stateCode == 100){
                sessionStorage.setItem('token', res.data.authToken);
                sessionStorage.setItem('userStatus', res.data.userStatus);
                window.location.href="../homePage/homePageHtml";
            }else{
                layer.msg(res.message);
            }
        },
        error: function (err) {
            layer.msg(err.message);
        }
    });
}