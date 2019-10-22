var id = window.location.href.split('=')[1];

$.ajax({
    type: "POST",
    url: "../sendExpressOrder/findSendOrder",
    contentType: "application/json; charset=utf-8",
    headers: {
        "Accept": "application/json; charset=utf-8",
        "Authorization": "Basic " + sessionStorage.getItem("token")
    },
    data: JSON.stringify({id: id}),
    success: function (res) {
        if (res.stateCode == 100) {
            console.log(res);
            $('#orderNo').html(res.data.orderNo);
            $('#setOrderTime').html(res.data.orderDate);
            $('#postMan').html(res.data.sendName);
            $('#postManTel').html(res.data.sendTel);
            $('#postManAddress').html(res.data.sendAddress);
            $('#receiveMan').html(res.data.receiverName);
            $('#receiveManTel').html(res.data.receiverTel);
            $('#receiveManAddress').html(res.data.receiverAddress);
            $('#goodsType').html(res.data.goosType);
            $('#deliveryCompany').html(res.data.deliveryCompanyName);
            $('#toGetTime').html(res.data.doorTime);
            $('#tips').html(res.data.remarks);

        } else {
            layer.msg(res.message);
        }
    },
    error: function (err) {
        layer.msg(err.message);
    }
});