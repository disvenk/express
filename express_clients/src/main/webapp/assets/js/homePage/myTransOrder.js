var url = window.location.href;
var array = url.split('&');
var id = array[0].split('=')[1];
var orderType = array[1].split('=')[1];
var isDefault;
var regPhoneNum = /^1(3|4|5|7|8)\d{9}$/;
var city;
var type;
var text1;
var text2;
var text3;
    // 收件地址列表
    $.ajax({
        type: "POST",
        url: "../receiveAddress/list",
        contentType: "application/json; charset=utf-8",
        headers: {
            "Accept": "application/json; charset=utf-8",
            "Authorization": "Basic " + sessionStorage.getItem("token")
        },
        success: function (res) {
            if (res.stateCode == 100) {
          if(res.data.length == 0){
              $('#addReceive').css('display','block');
          }else{
              for (var i = 0; i < res.data.length; i++) {
                  if(res.data[i].isDefault){
                      $('#addReceive').css('display','none');
                      var post = '<div class="post_list" onclick="receive()" value="'+res.data[i].id+'">' +
                          '                <div><span>收件人：</span><span class="node-receiver1">'+res.data[i].name+'</span></div>\n' +
                          '                <div><span>电话：</span><span class="node-tel1">'+res.data[i].tel+'</span></div>\n' +
                          '                <div style="margin-bottom: -20px"><span style="display: inline-block;width: 47px">地址：</span><div class="node-address1" style="position: relative;left: 42px;top: -21px;width: calc(100% - 47px)">'+res.data[i].province+res.data[i].city+res.data[i].zone+res.data[i].address+'</div></div>\n' +
                          '            </div>';
                      $('#receiveAddress').append(post);
                      break;
                  } else {
                      isDefault=false;
                      if (!isDefault) {
                          $('#addReceive').css('display','block');
                      }
                  }
              }
          }
            } else {
                layer.msg(res.message);
            }
        },
        error: function (err) {
            layer.msg(err.message);
        }
    });
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
            $('#orderNo').html(res.data.orderNo);
            $('#orderTime').html(res.data.orderDate);
            $('#toDate').html('到达时间<span class="order-info">'+res.data.arrivedDate+'</span>');
            $('#code').html('验证码：'+res.data.validateCode);
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
                    window.location.href = '../homePage/receiveOrderHtml?type=1';
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
                    window.location.href = '../homePage/receiveOrderHtml?type=1';
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
//上门费
$.ajax({
    type: "POST",
    url: "../toDoorContent/findToDoorFeeBySchool",
    contentType: "application/json; charset=utf-8",
    headers: {
        "Accept": "application/json; charset=utf-8",
        "Authorization": "Basic " + sessionStorage .getItem("token")
    },
    data: JSON.stringify({type: 1}),
    success: function (res) {
        if(res.stateCode == 100){
            $('#todoor').html('上门费用：'+ res.data.fee + '元');
        }else{

        }
    },
    error: function (err) {

    }
});
function getDate(count) {
    var dd = new Date();
    dd.setDate(dd.getDate() + count);
    var y = dd.getFullYear();
    var m = dd.getMonth() + 1;
    var d = dd.getDate();
    return y + '-' + m + '-' + d;
};
//上门时间
$.ajax({
    type: "POST",
    url: "../toDoorContent/findToDoorTimeBySchool",
    contentType: "application/json; charset=utf-8",
    headers: {
        "Accept": "application/json; charset=utf-8",
        "Authorization": "Basic " + sessionStorage .getItem("token")
    },
    data: JSON.stringify({type: 1}),
    success: function (res) {
        if(res.stateCode == 100){
            $('#showArea').on('click', function () {
                var arr = [];
                arr = [{
                    label: getDate(0),
                    value: getDate(0),
                    children: res.data
                },{
                    label: getDate(1),
                    value: getDate(1),
                    children: res.data
                },{
                    label: getDate(2),
                    value: getDate(2),
                    children: res.data
                }];
                weui.picker(arr, {
                    onChange: function (result) {
                    },
                    onConfirm: function (result) {
                        $("#showArea").val(result[0]+"  "+result[1]);
                    }
                });
//        });

            });
        }else{

        }
    },
    error: function (err) {

    }
});

var value;
$('#paytip .down').click(function () {
    value = $('#value').val();
       value--;
        if(value  < 0){
            $('#value').val(0);
        }else {
            $('#value').val(value);
        }
});
$('#paytip .up').click(function () {
    value = $('#value').val();
    value++;
    $('#value').val(value);
});
//新增收件地址模态框中的地址列表
function receive() {
    var index = layer.load(2, {
        shade: [0.6,'#000']
    });
    $('.modal-address-list').css('display','block');
    $.ajax({
        type: "POST",
        url: "../receiveAddress/list",
        contentType: "application/json; charset=utf-8",
        headers: {
            "Accept": "application/json; charset=utf-8",
            "Authorization": "Basic " + sessionStorage .getItem("token")
        },
        success: function (res) {
            if(res.stateCode == 100){
                layer.close(index);
                $('#list').html('');
                for(var i= 0; i < res.data.length; i++){
                    var address = '';
                    address += '    <div style="margin-bottom: 10px;"><div onclick="addReceive($(this))" class="list_container_body" value="'+res.data[i].id+'">\n' +
                        '        <span class="list_name">'+res.data[i].name+'</span>\n' +
                        '        <span class="list_phone">'+res.data[i].tel+'</span>\n' +
                        '        <div class="list_address">\n' +
                        ''+res.data[i].province+res.data[i].city+res.data[i].zone+res.data[i].address+''+
                        '        </div>\n' +
                        '    </div></div>';
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
// 新增收件地址按钮点击事件
$('#addReceive').click(function () {
    receive();
    $('.modal-address-list').css('display','block');
});
function addReceive(_this) {
    var name=_this.find('.list_name').html();
    var tel=_this.find('.list_phone').html();
    var address=_this.find('.list_address').html();
    $('#addReceive').css('display','none');
    $('#receiveAddress .post_list').eq(0).remove();
    $('.modal-address-list').css('display','none');
    var post = '<div class="post_list" onclick="receive()" value="'+_this.attr('value')+'">\n' +
        '                <div><span>收件人：</span><span class="node-receiver1">'+name+'</span></div>\n' +
        '                <div><span>电话：</span><span class="node-tel1">'+tel+'</span></div>\n' +
        '                <div style="margin-bottom: -20px"><span style="display: inline-block;width: 47px">地址：</span><div class="node-address1" style="position: relative;left: 47px;top: -21px;width: calc(100% - 47px)">'+address+'</div></div>\n' +
        '            </div>';
    $('#receiveAddress').append(post);
}
// 新增收件地址模态框中的新增按钮
$('.add-address-list').click(function () {
    $('.modal-address-list').css('display','none');
    $('.more-address').css('display','block');
    $('#receiver').html('收件人');
    //获取省市区
    $.ajax({
        type: "POST",
        url: "../express/findProvinceCityZone",
        contentType: "application/json; charset=utf-8",
        headers: {
            "Accept": "application/json; charset=utf-8",
            "Authorization": "Basic " + sessionStorage .getItem("token")
        },
        success: function (res) {
            if(res.stateCode == 100){
                city = res.data;
                var first = []; /* 省，直辖市 */
                var second = []; /* 市 */
                var third = []; /* 镇 */
                var selectedIndex = [0, 0, 0]; /* 默认选中的地区 */

                var checked = [0, 0, 0]; /* 已选选项 */

                function creatList(obj, list){
                    obj.forEach(function(item, index, arr){
                        var temp = new Object();
                        temp.text = item.name;
                        temp.value = index;
                        list.push(temp);
                    })
                }

                creatList(city, first);

                if (city[selectedIndex[0]].hasOwnProperty('sub')) {
                    creatList(city[selectedIndex[0]].sub, second);
                } else {
                    second = [{text: '', value: 0}];
                }

                if (city[selectedIndex[0]].sub[selectedIndex[1]].hasOwnProperty('sub')) {
                    creatList(city[selectedIndex[0]].sub[selectedIndex[1]].sub, third);
                } else {
                    third = [{text: '', value: 0}];
                }

                var picker = new Picker({
                    data: [first, second, third],
                    selectedIndex: selectedIndex,
                    title: '请选择地址'
                });

                picker.on('picker.select', function (selectedVal, selectedIndex) {
                    text1 = first[selectedIndex[0]].text;
                    text2 = second[selectedIndex[1]].text;
                    text3 = third[selectedIndex[2]] ? third[selectedIndex[2]].text : '';

                    $('#province').val(text1+text2+text3);
                });

                picker.on('picker.change', function (index, selectedIndex) {
                    if (index === 0){
                        firstChange();
                    } else if (index === 1) {
                        secondChange();
                    }

                    function firstChange() {
                        second = [];
                        third = [];
                        checked[0] = selectedIndex;
                        var firstCity = city[selectedIndex];
                        if (firstCity.hasOwnProperty('sub')) {
                            creatList(firstCity.sub, second);

                            var secondCity = city[selectedIndex].sub[0]
                            if (secondCity.hasOwnProperty('sub')) {
                                creatList(secondCity.sub, third);
                            } else {
                                third = [{text: '', value: 0}];
                                checked[2] = 0;
                            }
                        } else {
                            second = [{text: '', value: 0}];
                            third = [{text: '', value: 0}];
                            checked[1] = 0;
                            checked[2] = 0;
                        }

                        picker.refillColumn(1, second);
                        picker.refillColumn(2, third);
                        picker.scrollColumn(1, 0)
                        picker.scrollColumn(2, 0)
                    }

                    function secondChange() {
                        third = [];
                        checked[1] = selectedIndex;
                        var first_index = checked[0];
                        if (city[first_index].sub[selectedIndex].hasOwnProperty('sub')) {
                            var secondCity = city[first_index].sub[selectedIndex];
                            creatList(secondCity.sub, third);
                            picker.refillColumn(2, third);
                            picker.scrollColumn(2, 0)
                        } else {
                            third = [{text: '', value: 0}];
                            checked[2] = 0;
                            picker.refillColumn(2, third);
                            picker.scrollColumn(2, 0)
                        }
                    }

                });
// picker.on('picker.valuechange', function (selectedVal, selectedIndex) {
//   console.log(selectedVal);
//   console.log(selectedIndex);
// });
                $('#province').click(function () {
                    picker.show();
                });
            }else{
                layer.msg(res.message);
            }
        },
        error: function (err) {
            layer.msg(err.message);
        }
    });
    var receiver=$('#receiver').html();
    var phone=$('#phone').html();
    var province=$('#province').html();
    var address=$('#address').html();
    var data = {
        id: '',
        name: '',
        tel: '',
        province: '',
        city: '',
        zone: '',
        address: '',
        isDefault: true
    };

    function save(href) {
        if($('#name').val() == ''){
            layer.msg('名字不能为空');
            return
        }
        if($('#phone').val() == ''){
            layer.msg('手机号不能为空');
            return
        }
        if(!regPhoneNum.test($('#phone').val())){
            layer.msg('手机号不合法');
            return
        }
        if($('#province').val() == ''){
            layer.msg('请选择省市区');
            return
        }
        if($('#adress').val() == ''){
            layer.msg('请输入详细地址');
            return
        }
        var index = layer.load(2, {
            shade: [0.6,'#000'] //0.1透明度的白色背景
        });
        data = {
            id: '',
            name: $('#name').val(),
            tel: $('#phone').val(),
            province: text1,
            city: text2,
            zone: text3,
            address: $('#address').val(),
            isDefault: true
        };
        $.ajax({
            type: "POST",
            url: href,
            contentType: "application/json; charset=utf-8",
            headers: {
                "Accept": "application/json; charset=utf-8",
                "Authorization": "Basic " + sessionStorage .getItem("token")
            },
            data: JSON.stringify(data),
            success: function (res) {
                if(res.stateCode == 100){
                    layer.close(index);
                    $('#addReceive').css('display','none');
                    $('#receiveAddress .post_list').eq(0).remove();
                    $('.more-address').css('display','none');
                    var post = '<div onclick="receive()" class="post_list" value="'+res.data.id+'">\n' +
                        '                <div><span>寄件人：</span><span>'+data.name+'</span></div>\n' +
                        '                <div><span>电话：</span><span>'+data.tel+'</span></div>\n' +
                        '                <div style="margin-bottom: -20px"><span style="display: inline-block;width: 47px">地址：</span><div style="position: relative;left: 47px;top: -21px;width: calc(100% - 47px)">'+text1+text2+text3+data.address+'</div></div>\n' +
                        '            </div>';
                    $('#receiveAddress').append(post);
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
    //新增保存
    $('#save').click(function () {
            href = '../receiveAddress/save';
            save(href);
    });
})
$('#trans').click(function () {
    if($('#showArea').val() == ''){
        layer.msg('请选择上门时间');
        return;
    }
    if($('#addReceive').css('display') == 'block'){
        layer.msg('请添加收件地址');
        return;
    }
    var index = layer.load(2, {
        shade: [0.6, '#000']
    });
    $.ajax({
        type: "POST",
        url: "../transDoor/transDoorStatus",
        contentType: "application/json; charset=utf-8",
        headers: {
            "Accept": "application/json; charset=utf-8",
            "Authorization": "Basic " + sessionStorage .getItem("token")
        },
        data: JSON.stringify({id: id,doorTime: $('#showArea').val(),fee: $('#value').val(),remarks: $('#remarks').val(),addressId: $('#receiveAddress .post_list').attr('value')}),
        success: function (res) {
            if(res.stateCode == 100){
                var data = {
                    channel: 'WECHAT',
                    orderId: res.data.id,
                    orderType: 1,
                    totalFee: (res.data.todoorFee - 0 + ($('#value').val() - 0)) * 100,
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
                                                        layer.close(index);
                                                        window.location.href = '../homePage/receiveOrderHtml?type=2';
                                        }else{
                                            layer.close(index);
                                            // $.ajax({
                                            //     type: 'POST',
                                            //     url: '../sendExpressOrder/deletePayment',
                                            //     contentType: 'application/json; charset=utf-8',
                                            //     headers: {
                                            //         'Accept': 'application/json; charset=utf-8',
                                            //         'Authorization': 'Basic ' + sessionStorage.getItem('token')
                                            //     },
                                            //     data: JSON.stringify({id: id,orderType:1}),
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
            }else{

            }
        },
        error: function (err) {

        }
    });
});