var url = window.location.href;
var array = url.split('&');
var id = array[0].split('=')[1];
var orderType = array[1].split('=')[1];
var ispaper;
$.ajax({
    type: "POST",
    url: "../sendExpressOrder/findSendOrder",
    contentType: "application/json; charset=utf-8",
    headers: {
        "Accept": "application/json; charset=utf-8",
        "Authorization": "Basic " + sessionStorage .getItem("token")
    },
    data: JSON.stringify({
        id: id
    }),
    success: function (res) {
        if(res.stateCode == 100){
            $('#sendName').html(res.data.sendName);
            $('#sendTel').html(res.data.sendTel);
            $('#sendAddress').html(res.data.sendAddress);
            $('#receiverName').html(res.data.receiverName);
            $('#receiverTel').html(res.data.receiverTel);
            $('#receiverAddress').html(res.data.receiverAddress);
            $('#orderTime').html(res.data.orderDate);
            $('#status').html(res.data.orderStatusName);
            $('#orderNo').html(res.data.orderNo);
             ispaper = res.data.ispaper;
            if(orderType == 1){
                $('#remark').show();
                $('#remarks').html(res.data.remarks);
            }
            if(res.data.orderStatus == 1){
                $('#tip').show();
                    $('#tip').html('提示：请前往指定地点寄件，待工作人员确认后在线付款');
                $('#show2').show();
                $('#operate').html('取消订单');
                $('#operate').click(function () {
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
                                    window.location.href = '../homePage/sendOrderHtml?orderType='+orderType;
                                }else{
                                    layer.close(index);
                                }
                            },
                            error: function (err) {
                                layer.close(index);
                            }
                        });
                    });
                });
            }
            if(res.data.orderStatus == 2){
                $('#tip').show();
                $('#tip').html('提示：请确认物品重量与价格');
                $('#show2').show();
                $('#operate').html('立即支付');
                $('#operate').click(function (){
                    var data = {
                        channel: 'WECHAT',
                        orderId: res.data.id,
                        orderType: 2,
                        totalFee: (res.data.totalFee - 0) * 100,
                        productId: '',
                        tradeType: 'JSAPI',
                        outTradeNo: '',
                        openid: sessionStorage.getItem('openId'),
                        body: $('#orderNo').html(),
                        subject: $('#orderNo').html()
                    };
                    $.ajax({
                        type: 'POST',
                        url: '../pay/union_entrance',
                        contentType: 'application/json; charset=utf-8',
                        headers: {
                            'Accept': 'application/json; charset=utf-8',
                            'Authorization': 'Basic ' + sessionStorage.getItem('token')
                        },
                        data: JSON.stringify(data),
                        success: function(res){
                            if(res.stateCode == 100){
                                function onBridgeReady(){
                                    WeixinJSBridge.invoke(
                                        'getBrandWCPayRequest', {
                                            "appId":res.data.appId,     //公众号名称，由商户传入
                                            "timeStamp":res.data.timeStamp,       //时间戳，自1970年以来的秒数
                                            "nonceStr":res.data.nonceStr, //随机串
                                            "package":res.data.package,
                                            "signType":"MD5",         //微信签名方式：
                                            "paySign":res.data.paySign //微信签名
                                        },
                                        function(res){
                                            if(res.err_msg == "get_brand_wcpay_request:ok" ) {
                                          // if(ispaper == '电子面单'){
                                          //     $.ajax({
                                          //         type: 'POST',
                                          //         url: '../sendExpressOrder/updateSendOrderDeliverNo',
                                          //         contentType: 'application/json; charset=utf-8',
                                          //         headers: {
                                          //             'Accept': 'application/json; charset=utf-8',
                                          //             'Authorization': 'Basic ' + sessionStorage.getItem('token')
                                          //         },
                                          //         data: JSON.stringify({id: id,deliveryName:res.data.deliveryCompanyName}),
                                          //         success: function (res) {
                                          //             if(res.stateCode == 100){
                                          //                 window.location.href = '../homePage/sendOrderHtml?orderType='+orderType;
                                          //             }else{
                                          //                 layer.msg(res.message);
                                          //                 window.location.href = '../homePage/sendOrderHtml?orderType='+orderType;
                                          //             }
                                          //         },
                                          //         error: function (err) {
                                          //             layer.msg(err.message);
                                          //             window.location.href = '../homePage/sendOrderHtml?orderType='+orderType;
                                          //         }
                                          //     });
                                          // }else{
                                              window.location.href = '../homePage/sendOrderHtml?orderType='+orderType;
                                          // }
                                            } else{
                                                layer.close(index);
                                                // $.ajax({
                                                //     type: 'POST',
                                                //     url: '../sendExpressOrder/deletePayment',
                                                //     contentType: 'application/json; charset=utf-8',
                                                //     headers: {
                                                //         'Accept': 'application/json; charset=utf-8',
                                                //         'Authorization': 'Basic ' + sessionStorage.getItem('token')
                                                //     },
                                                //     data: JSON.stringify({id: id,orderType:orderType}),
                                                //     success: function (res) {
                                                //
                                                //     },
                                                //     error: function (err) {
                                                //
                                                //     }
                                                // })
                                            }
                                        }
                                    );
                                }
                                onBridgeReady();
                                if (typeof WeixinJSBridge == "undefined"){
                                    if( document.addEventListener ){
                                        document.addEventListener('WeixinJSBridgeReady', onBridgeReady, false);
                                    }else if (document.attachEvent){
                                        document.attachEvent('WeixinJSBridgeReady', onBridgeReady);
                                        document.attachEvent('onWeixinJSBridgeReady', onBridgeReady);
                                    }
                                }else{
                                    onBridgeReady();
                                }
                            }else{
                                layer.msg(res.message);
                            }
                        },
                        error:function (err) {
                            layer.msg(err.message);
                        }
                    })
                });
            }
            if(res.data.orderStatus == 4){
                $('#show2').show();
                $('#operate').html('删除订单');
                $('#operate').click(function () {
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
                                    window.location.href = '../homePage/sendOrderHtml?orderType='+orderType;
                                }else{
                                    layer.close(index);
                                }
                            },
                            error: function (err) {
                                layer.close(index);
                            }
                        });
                    });
                });
            }
            if(res.data.weight){
                $('#weight').show();
                $('#weight span').html(res.data.weight)
            }
            if(res.data.deliveryCompanyName){
                $('#company').show();
                $('#company span').html(res.data.deliveryCompanyName)
            }
            if(res.data.goosType){
                $('#type').show();
                $('#type span').html(res.data.goosType)
            }
            if(res.data.totalFee){
                $('#price').show();
                $('#price span').html(res.data.totalFee +'元 (含上门费'+ res.data.toDoorFee+'元)');
            }
            if(res.data.takedTime){
                $('#get').show();
                $('#get span').html(res.data.takedTime);
            }
            if(res.data.doorTime){
                $('#time').show();
                $('#time span').html(res.data.doorTime);
            }
            if(res.data.deliveryNo){
                $('#deliveryNo').show();
                $('#deliveryNo span').html(res.data.deliveryNo);
            }
            if(res.data.payTime){
                $('#payTime').show();
                $('#payTime span').html(res.data.payTime);
            }
            if(res.data.deliverer){
                $('#deliverer').html('上门人员：' + res.data.deliverer);
            }
            if(res.data.delivererTel){
                $('#delivererTel').html('联系方式：'+res.data.delivererTel);
                $('#delivererTel').append('<a href="tel:'+res.data.delivererTel+'" style="float: right;position: relative;top: -4px;background-color: #0db6fb;color: #fff;font-size: 12px;padding: 2px 18px;border-radius: 3px">联系他/她</a>');
            }
        }else{
            layer.msg(res.message);
        }
    },
    error: function (err) {
        layer.msg(err.message);
    }
});
