var token = sessionStorage.getItem('token');
var userStatus = sessionStorage.getItem('userStatus');
if(!token){
    window.location.href="../homePage/loginHtml";
}
//学校网点列表
$.ajax({
    type: "POST",
    url: "../school/list",
    contentType: "application/json; charset=utf-8",
    headers: {
        "Accept": "application/json; charset=utf-8",
        "Authorization": "Basic " + sessionStorage .getItem("token")
    },
    success: function (res) {
        if(res.stateCode == 100){
            for(var i = 0; i < res.data.length; i++){
                var option = '<option class="'+res.data[i].id+'">'+res.data[i].name+'</option>';
                $('#options').append(option);
            }
        }else{
            layer.msg(res.message);
        }
    },
    error: function (err) {
        layer.msg(err.message);
    }
});
//绑定网点
function login() {
    if ($('#options').find('option:selected').attr('class') == '') {
        layer.msg('请选择学校');
        return;
    }
    $.ajax({
        type: "POST",
        url: "../account/saveSchool",
        contentType: "application/json; charset=utf-8",
        headers: {
            "Accept": "application/json; charset=utf-8",
            "Authorization": "Basic " + sessionStorage .getItem("token")
        },
        data: JSON.stringify({id: $('#options').find('option:selected').attr('class')}),
        success: function (res) {
            if(res.stateCode == 100){
                window.location.href="../homePage/homePageHtml";
                sessionStorage.setItem('userStatus', '0')
            }else{
                layer.msg(res.message);
            }
        },
        error: function (err) {
            layer.msg(err.message);
        }
    });
}