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
        url: "../receiveOrder/findReceiveOrderList",
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
                        '<div onclick="viewOrder('+res.data[i].id+','+res.data[i].orderStatus+')"><div style="border-bottom: 1px solid #f6f6f6;padding: 5px 0"><span style="width: 70%;color: rgb(175,176,179)">订单编号：'+res.data[i].orderNo+'</span><span style="width: 30%;text-align: right;color: red">'+res.data[i].orderStatusName+'</span></div>'+
                    '<div><span style="width: 70%">快递单号：'+res.data[i].deliveryNo+'</span><span style="width: 30%;text-align: right">'+res.data[i].deliveryCompanyName+'</span></div>' +
                    '<div>下单时间：<span>'+res.data[i].orderDate+'</span></div>' +
                    '<div>物品类型：<span>'+res.data[i].goodsTypeName+'</span></div>' +
                    '<div>物品说明：<span>'+res.data[i].goodsDescription+'</span></div></div>';
                    if(res.data[i].orderStatus == 1){
                        address +=  '<div class="list_address">' +
                            '<div style="text-align: right" class="operate">' +
                            '<span class="func-btn" onclick="removeIt('+res.data[i].id+', 1)">取消</span></div>'+
                            '</div></div></div>';
                    }else if(res.data[i].orderStatus == 2){
                        address +=  '<div class="list_address">' +
                            '<div style="text-align: right" class="operate">' +
                            '<span class="func-btn" onclick="refuse('+res.data[i].id+', 2)">拒收</span><span class="func-btn" style="margin-left: 8px;background-color: #0ab5fa" onclick="trans('+res.data[i].id+', 2)">转派件上门</span></div>'+
                            '</div></div></div>';
                    }else if(res.data[i].orderStatus == 6 || res.data[i].orderStatus == 7 || res.data[i].orderStatus == 8){
                        address +=  '<div class="list_address">' +
                            '<div style="text-align: right" class="operate">' +
                            '<span class="func-btn" style="background-color: #0ab5fa" onclick="viewOrder('+res.data[i].id+', 3)">查看订单</span></div>'+
                            '</div></div></div>';
                    } else if(res.data[i].orderStatus == 5 || res.data[i].orderStatus == 4){
                        address +=  '</div></div>';
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
// 取消
function removeIt(id, type) {
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
            data: JSON.stringify({id: id,orderType: type}),
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
// 拒收
function refuse(id, type) {
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
            data: JSON.stringify({id: id,orderType: type}),
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
// 派件上门
function trans(id, type) {
        location.href = '../homePage/myTransOrderHtml?id=' + id + '&type=' + type;
}
// 查看订单
function viewOrder(id, type) {
    if(orderType == 0){
        location.href='../homePage/myGetOrderHtml?id='+id+'&type='+type;
    }else{
        location.href='../homePage/myTakeOrderHtml?id='+id+'&type='+type;
    }
}
// 确认送达
function sureArrive(id, type) {
    $('#iosDialog6').show();
    $('#sure5').click(function () {
        $('#iosDialog6').hide();
        var index = layer.load(2, {
            shade: [0.6, '#000']
        });
        $.ajax({
            type: "POST",
            url: "../receiveOrder/receivedOrder",
            contentType: "application/json; charset=utf-8",
            headers: {
                "Accept": "application/json; charset=utf-8",
                "Authorization": "Basic " + sessionStorage .getItem("token")
            },
            data: JSON.stringify({id: id,orderType: type}),
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
