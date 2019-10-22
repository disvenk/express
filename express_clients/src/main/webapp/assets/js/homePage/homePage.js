var token = sessionStorage.getItem('token');
var userStatus = sessionStorage.getItem('userStatus');
if(!token){
    window.location.href="../homePage/loginHtml";
}else{
    if(userStatus == 1){
        window.location.href="../homePage/selectSchoolHtml";
    }else{
        $.ajax({
            type: "POST",
            url: "../userHome/info",
            contentType: "application/json; charset=utf-8",
            headers: {
                "Accept": "application/json; charset=utf-8",
                "Authorization": "Basic " + sessionStorage .getItem("token")
            },
            success: function (res) {
                if(res.stateCode == 100){
                   $('#receiverOrderCount').html(res.data.receiverOrderCount);
                    $('#sendOrderCount').html(res.data.sendOrderCount);
                }else{
                    layer.msg(res.message);
                }
            },
            error: function (err) {
                layer.msg(err.message);
            }
        });
        //收件
        function receive(){
            window.location.href="../homePage/receiveHtml";
        }
        //寄件
        function post(){
            window.location.href="../homePage/postHtml";
        }
        //查询
        function check(){
            window.location.href="../homePage/checkHtml";
        }
        //个人中心
        function info(){
            window.location.href="../homePage/infoHtml";
        }
    }
}