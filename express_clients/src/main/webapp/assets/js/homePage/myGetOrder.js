    var url = window.location.href;
    var array = url.split('&');
    var id = array[0].split('=')[1];
    var orderType = array[1].split('=')[1];
    if(orderType == 1){
        $('#show1').show();
    }else if(orderType == 2){
        $('#show2').show();
        $('#tip').show();
    }
    $.ajax({
        type: "POST",
        url: "../receiveOrder/getReceiveOrder",
        contentType: "application/json; charset=utf-8",
        headers: {
            "Accept": "application/json; charset=utf-8",
            "Authorization": "Basic " + sessionStorage .getItem("token")
        },
        data: JSON.stringify({
           id: id,
           orderType: orderType
        }),
        success: function (res) {
            if(res.stateCode == 100){
                $('#deliveryNum').html(res.data.deliveryNo);
                $('#deliveryCom').html(res.data.deliveryCompanyName);
                $('#orderNo').html(res.data.orderNo);
                $('#orderTime').html(res.data.orderDate);
                $('#status').html(res.data.orderStatusName);
                if(res.data.reachDate){
                    $('#toDate').show();
                    $('#toDate').html('到达时间：'+res.data.reachDate);
                }
                if(res.data.expressDate){
                    $('#receiveDate').show();
                    $('#receiveDate').html('签收时间：'+res.data.expressDate);
                }
                if(res.data.refuseTime){
                    $('#refuseDate').show();
                    $('#refuseDate').html('拒收时间：'+res.data.refuseTime);
                }
                if(res.data.validateCode){
                    $('#code').show();
                    $('#code').html('验证码：'+res.data.validateCode);
                }
                $.ajax({
                    type: "POST",
                    url: "../express/searchExpress",
                    contentType: "application/json; charset=utf-8",
                    headers: {
                        "Accept": "application/json; charset=utf-8",
                        "Authorization": "Basic " + sessionStorage .getItem("token")
                    },
                    data: JSON.stringify({
                        com: res.data.shortName,
                        nu: res.data.deliveryNo
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
            }else{
                layer.msg(res.message);
            }
        },
        error: function (err) {
            layer.msg(err.message);
        }
    });
// 取消
function removeIt() {
    $('#iosDialog1').show();
    $('#suredelete').click(function () {
        $('#iosDialog1').hide();
        var index = layer.load(2, {
            shade: [0.6, '#000']
        });
        $.ajax({
            type: "POST",
            url: "../receiveOrder/deleteReceiveOrder",
            contentType: "application/json; charset=utf-8",
            headers: {
                "Accept": "application/json; charset=utf-8",
                "Authorization": "Basic " + sessionStorage .getItem("token")
            },
            data: JSON.stringify({id: id,orderType: orderType}),
            success: function (res) {
                if(res.stateCode == 100){
                    layer.close(index);
                    window.location.href = '../homePage/receiveOrderHtml?type=0';
                }else{
                    layer.close(index);
                }
            },
            error: function (err) {
                layer.close(index);
            }
        });
    });
}
// 拒收
function refuse() {
    $('#iosDialog2').show();
    $('#sure1').click(function () {
        $('#iosDialog2').hide();
        var index = layer.load(2, {
            shade: [0.6, '#000']
        });
        $.ajax({
            type: "POST",
            url: "../receiveOrder/rejectOrder",
            contentType: "application/json; charset=utf-8",
            headers: {
                "Accept": "application/json; charset=utf-8",
                "Authorization": "Basic " + sessionStorage .getItem("token")
            },
            data: JSON.stringify({id: id,orderType: orderType}),
            success: function (res) {
                if(res.stateCode == 100){
                    layer.close(index);
                    window.location.href = '../homePage/receiveOrderHtml?type=0';
                }else{
                    layer.close(index);
                }
            },
            error: function (err) {
                layer.close(index);
            }
        });
    });
}
// 派件上门
function trans() {
    $('#iosDialog3').show();
    $('#sure2').click(function () {
        $('#iosDialog3').hide();
        location.href = '../homePage/myTransOrderHtml?id=' + id + '&type=' + orderType;
    });
}