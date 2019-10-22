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
            $('#receiveName').html(res.data.receiveName);
            $('#receiveTel').html(res.data.receiveTel);
            $('#receiveAddress').html(res.data.receiveAddress);
            $('#showArea').val(res.data.doorTimeBeginEnd);
            $('#remarks').html(res.data.remarks);
            $('#toDate').html(res.data.arrivedDate);
            $('#payTime').html(res.data.payTime);
            $('#code').html(res.data.validateCode);
            if(res.data.orderStatus == 3){
                $('#tip').show();
                $('#tip').html('提示：请等候人员接单');
            }
            if(res.data.orderStatus == 4){
                $('#tip').show();
                $('#tip').html('提示：请等候接单人员上门派件');
            }
            if(res.data.xiaoGe){
                $('#xiaoGe').html('上门人员：' + res.data.xiaoGe);
            }
            if(res.data.signtTime){
                $('#signtTime').html('验收时间：'+res.data.signtTime);
            }
            if(res.data.xiaoGeTel){
                $('#xiaoGeTel').html('联系方式：'+res.data.xiaoGeTel);
                $('#xiaoGeTel').append('<a href="tel:'+res.data.xiaoGeTel+'" style="float: right;position: relative;top: -4px;background-color: #0db6fb;color: #fff;font-size: 12px;padding: 2px 18px;border-radius: 3px">联系他/她</a>');
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
