$(function () {
    $.ajax({
        type: "POST",
        url: "../express/findCompayBySchool",
        contentType: "application/json; charset=utf-8",
        headers: {
            "Accept": "application/json; charset=utf-8",
            "Authorization": "Basic " + sessionStorage .getItem("token")
        },
        success: function (res) {
            if(res.stateCode == 100){
                $('#deliveryCom').append('<option value="" disabled selected style="display: none">--请选择--</option>');
                for(var i = 0; i < res.data.length; i++){
                    var option = '<option class="'+res.data[i].com+'" id="'+res.data[i].id+'">'+res.data[i].name+'</option>';
                    $('#deliveryCom').append(option);
                }
            }else{
                layer.msg(res.message);
            }
        },
        error: function (err) {
            layer.msg(err.message);
        }
    });
});
function check() {
    $.ajax({
        type: "POST",
        url: "../express/searchExpress",
        contentType: "application/json; charset=utf-8",
        headers: {
            "Accept": "application/json; charset=utf-8",
            "Authorization": "Basic " + sessionStorage .getItem("token")
        },
        data: JSON.stringify({
            com: $('#deliveryCom').find('option:selected').attr('class'),
            nu: $('#deliveryNo').val()
        }),
        success: function (res) {
            if(res.stateCode == 100){
                $('#iframe').attr('src', res.data.result);
            }else{
                layer.msg(res.message);
            }
        },
        error: function (err) {
            layer.msg(err.message);
        }
    });
}


//扫一扫
$(function () {
    var  url = window.location.href.split('#')[0];
    var  encodeURI = encodeURIComponent(url);
    $.ajax({        type: 'GET',
        url: '../homePage/getshareinfo?url='+encodeURI,
        dataType: 'json',
        async: false,
        contentType: "application/json; charset=utf-8",
        headers: {
            "Accept": "application/json; charset=utf-8",
            "Authorization": "Basic "
        },
        success: function(res){
            if(res.stateCode == 100){
                wx.config({
                    debug: false, // 开启调试模式,调用的所有api的返回值会在客户端alert出来，若要查看传入的参数，可以在pc端打开，参数信息会通过log打出，仅在pc端时才会打印。
                    appId: res.data.appid, // 必填，公众号的唯一标识
                    timestamp: res.data.timestamp, // 必填，生成签名的时间戳
                    nonceStr: res.data.noncestr, // 必填，生成签名的随机串
                    signature: res.data.signature,// 必填，签名，见附录1
                    jsApiList: ['scanQRCode'] // 必填，需要使用的JS接口列表，所有JS接口列表见附录2
                });
                wx.error(function(res){
                    layer.msg('配置错误')
                })
            }else{
                layer.msg(res.message);
            }
        },
        error:function (err) {
            layer.msg(err.message);
        }
    });
});
$("#scan").click(function () {
    wx.scanQRCode({
        // 默认为0，扫描结果由微信处理，1则直接返回扫描结果
        needResult : 1,
        desc : 'scanQRCode desc',
        success : function(res) {
            //商品条形码，取","后面的
            var url = res.resultStr;
            if(url.indexOf(",")>=0){
                var tempArray = url.split(',');
                var tempNum = tempArray[1];
                $("#deliveryNo").val(tempNum);
            }else{
                $("#deliveryNo").val(url);
            }
            $('.button-small').css({'background-color': '#0ab5fa','color': '#ffffff'});

        }
    });
})