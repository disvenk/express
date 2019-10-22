var url = window.location.href;
var type = url.split('=')[1];
if(type == undefined || type == null || type == ''){
    type = 0
}
var orderType = type;
if(type == 0){
    $('#post').addClass('borderColor');
    get(0);
}else{
    $('#receive').addClass('borderColor');
    get(1);
}
$('#post').click(function(){
    orderType = 0;
    $('#list').html('');
    $(this).siblings().removeClass('borderColor');
    $(this).addClass('borderColor');
    get(orderType);
});
$('#receive').click(function(){
    orderType = 1;
    $('#list').html('');
    $(this).siblings().removeClass('borderColor');
    $(this).addClass('borderColor');
    get(orderType);
});
function get(orderType) {
    var index = layer.load(2, {
        shade: [0.6,'#000']
    });
    $.ajax({
        type: "POST",
        url: "../sendExpressOrder/findSendOrderList",
        contentType: "application/json; charset=utf-8",
        headers: {
            "Accept": "application/json; charset=utf-8",
            "Authorization": "Basic " + sessionStorage .getItem("token")
        },
        data: JSON.stringify({orderType: orderType}),
        success: function (res) {
            if(res.stateCode == 100){
                layer.close(index);
                $('#list').html('');
                for(var i= 0; i < res.data.length; i++){
                    var address = '';
                    address += '<div><div class="list_container_body" style="margin-bottom: 9px">' +
                        '<div onclick="viewOrder('+res.data[i].id+')"><div style="border-bottom: 1px solid #f6f6f6;padding: 5px 0"><span style="width: 70%;color: rgb(175,176,179)">订单编号：'+res.data[i].orderNo+'</span><span style="width: 30%;text-align: right;color: red">'+res.data[i].orderStatusName+'</span></div>'+
                        '<div><span style="width: 60%">收件人：'+res.data[i].receiverName+'</span><span style="width: 40%;text-align: right">'+res.data[i].receiverTel+'</span></div>' +
                        '<div>地址：<span style="line-height: 20px">'+res.data[i].receiverAddress+'</span></div>'+
                        '<div>下单时间：<span>'+res.data[i].orderDate+'</span></div></div>';
                    if(res.data[i].orderStatus == 1){
                        address +=  '<div class="list_address">' +
                            '<div style="text-align: right" class="operate">' +
                            '<span class="func-btn" style="background-color: #3f4b63" onclick="removeIt('+res.data[i].id+')">取消订单</span></div>'+
                            '</div></div></div>';
                    }else if(res.data[i].orderStatus == 2){
                        address +=  '<div class="list_address">' +
                            '<div style="text-align: right" class="operate">' +
                            '<span class="func-btn" style="background-color: #0ab5fa" onclick="viewOrder('+res.data[i].id+')">立即支付</span></div>'+
                            '</div></div></div>';
                    }else if(res.data[i].orderStatus == 3){
                        address +=  '<div class="list_address">' +
                            '<div style="text-align: right" class="operate">' +
                            '<span class="func-btn" style="background-color: #0ab5fa" onclick="viewOrder('+res.data[i].id+')">查看详情</span></div>'+
                            '</div></div></div>';
                    } else if(res.data[i].orderStatus == 4){
                        address +=  '<div class="list_address">' +
                            '<div style="text-align: right" class="operate">' +
                            '<span class="func-btn" style="background-color: #3f4b63" onclick="delete1('+res.data[i].id+')">删除订单</span></div>'+
                            '</div></div></div>';
                    }
                    $('#list').append(address);
                }
            }else{
                layer.close(index);
                layer.msg(res.message);
            }
        },
        error: function (err) {
            layer.close(index);
            layer.msg(err.message);
        }
    });
}
// 取消订单
function removeIt(id) {
    $('#iosDialog1').show();
    $('#suredelete').click(function () {
        $('#iosDialog1').hide();
        var index = layer.load(2, {
            shade: [0.6, '#000']
        });
        $.ajax({
            type: "POST",
            url: "../sendExpressOrder/cancelOrder",
            contentType: "application/json; charset=utf-8",
            headers: {
                "Accept": "application/json; charset=utf-8",
                "Authorization": "Basic " + sessionStorage .getItem("token")
            },
            data: JSON.stringify({id: id}),
            success: function (res) {
                if(res.stateCode == 100){
                    layer.close(index);
                    get(orderType);
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
// 查看订单
function viewOrder(id) {
        location.href='../homePage/myPostOrderHtml?id='+id+'&type='+orderType;
}
// 删除订单
function delete1(id) {
    $('#iosDialog6').show();
    $('#sure5').click(function () {
        $('#iosDialog6').hide();
        var index = layer.load(2, {
            shade: [0.6, '#000']
        });
        $.ajax({
            type: "POST",
            url: "../sendExpressOrder/deleteOrder",
            contentType: "application/json; charset=utf-8",
            headers: {
                "Accept": "application/json; charset=utf-8",
                "Authorization": "Basic " + sessionStorage .getItem("token")
            },
            data: JSON.stringify({id: id}),
            success: function (res) {
                if(res.stateCode == 100){
                    layer.close(index);
                    get(orderType);
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