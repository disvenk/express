// layui.use(['form', 'layedit', 'laydate', 'element'], function () {
//     var form = layui.form
//         , layer = layui.layer
//         , layedit = layui.layedit
//         , laydate = layui.laydate
//         , element = layui.element;
//
//     //创建一个编辑器
//     var editIndex = layedit.build('LAY_demo_editor');
//
//     //自定义验证规则
//     form.verify({
//         title: function (value) {
//             if (value.length < 5) {
//                 return '标题至少得5个字符啊';
//             }
//         }
//         , pass: [/(.+){6,12}$/, '密码必须6到12位']
//         , content: function (value) {
//             layedit.sync(editIndex);
//         }
//     });
//
//     //监听提交
//     form.on('submit(sub)', function (data) {
//         layer.alert(JSON.stringify(data.field), {
//             title: '最终的提交信息'
//         });
//         return false;
//     });
//
//     // you code ...
//
//
// });


var url = window.location.href;
var id = url.split('=')[1];

layui.use(['form','laydate','layer'], function () {
    var form = layui.form;
    var laydate = layui.laydate;
    var layer = layui.layer;
    laydate.render({
        elem: '#timeRange'
        ,type: 'time'
        ,range: true
    });
    form.render();
    var $ = layui.jquery,
        form = layui.form
        , layer = layui.layer;

//获取所有上门人员
    $.ajax({
        type: 'POST',
        url:   '../receive/getAllTodoorPerson',
        dataType: 'json',
        contentType: 'application/json; charset=utf-8',
        headers: {
            'Accept': 'application/json; charset=utf-8',
            'Authorization': 'Basic ' + sessionStorage.getItem('token')
        },
        data: JSON.stringify({id:id}),
        success: function (res) {
            var delivererId = '<option value=""></option>';
            for (var i = 0; i < res.data.length; i++) {
                delivererId += '<option id="' + res.data[i].id + '" value="' + res.data[i].name + '">' + res.data[i].name + '</option>';
            }
            $('#delivererId').append(delivererId);
            form.render();
        },
        error: function (err) {
            layer.alert(err.message, {icon: 0});
        }
    });


//列表
    setTimeout(function () {
        $.ajax({
            type: 'POST',
            url:  '../send/expSendOrderEdit',
            dataType: 'json',
            contentType: 'application/json; charset=utf-8',
            headers: {
                'Accept': 'application/json; charset=utf-8',
                'Authorization': 'Basic ' + sessionStorage.getItem('token')
            },
            data: JSON.stringify({id: id}),
            success: function (res) {
                if(res.stateCode == 100){
                    for(var a = 1;a<$('#delivererId').find('option').length;a++){
                        if($('#delivererId').find('option').eq(a).attr('id') == res.data.delivererId){
                            $('#delivererId').find('option').eq(a).attr('selected',true);
                            form.render();
                        }
                    }
                    $('#orderNo').html(res.data.orderNo);
                    $('#deliveryNo').val(res.data.deliveryNo);
                    $('#deliveryName').html(res.data.deliveryName);
                    $('#userCode').html(res.data.userCode);
                    $('#userMobile').html(res.data.userMobile);
                    $('#schoolName').html(res.data.schoolName);
                    $('#price').html(res.data.price+'元');
                    $('#todoorFee').html(res.data.todoorFee);
                    $('#cost').html(res.data.cost);
                    $('#payStatusName').html(res.data.payStatusName);
                    $('#payType').html(res.data.payType);
                    $('#payTime').html(res.data.payTime);
                    $('#orderTime').html(res.data.orderTime);
                    $('#typeName').html(res.data.typeName);
                    $('#orderStatusName').html(res.data.orderStatusName);
                    $('#expressDate').html(res.data.expressDate);
                    $('#weight').html(res.data.weight);
                    $('#goodsTypeName').html(res.data.goodsTypeName);
                    $('#receiverTel').val(res.data.receiverTel);
                    $('#receiverName').val(res.data.receiverName);
                    $('#receiverAddress').val(res.data.receiverAddress);
                    $('#sendName').val(res.data.sendName);
                    $('#sendTel').val(res.data.sendTel);
                    $('#sendAddress').val(res.data.sendAddress);
                    $('#todoorDate').html(res.data.todoorDate);
                    $('#remark').html(res.data.remark);


                    // 城市联动
                    var province = res.data.province;
                    var provinceCode = res.data.provinceCode;
                    var city = res.data.city;
                    var cityCode = res.data.cityCode;
                    var zone = res.data.zone;
                    var zoneCode = res.data.zoneCode;
//收件省市区三级联动
                    var code='';
                    $.getJSON("../express/findProvince", function (data) {
                        $.each(data.data, function (i, item) {
                            if (i == 0)
                            {
                                code = item.code
                            }
                            $('#drpprovince').append('<option  value=' + item.code + '>' + item.name + "</option>");
                        })
                        var proSelect = $('#drpprovince').find('option');
                        for (var i = 0; i < proSelect.length; i++) {
                            if (proSelect.eq(i).text() == province) {
                                proSelect.eq(i).attr('selected', 'selected');
                            }
                            form.render('select');
                        }

                        $.getJSON('../express/findCity?code=' + provinceCode, function (data) {
                            $.each(data.data, function (i, item) {
                                if (i == 0) {
                                    $.getJSON('../express/findCity?code=' + cityCode, function (data) {
                                        $.each(data.data, function (i, item) {
                                            $('#drparea').append('<option  value=' + item.code + '>' + item.name + "</option>");
                                            form.render('select');
                                        })
                                        var zoneSelect = $('#drparea').find('option');
                                        // alert(zoneSelect.length)
                                        for (var i = 0; i < zoneSelect.length; i++) {
                                            if (zoneSelect.eq(i).text() == zone) {
                                                zoneSelect.eq(i).attr('selected', 'selected');
                                                form.render('select');
                                            }
                                        }
                                    });
                                }
                                $('#drpcity').append('<option  value=' + item.code + '>' + item.name + "</option>");
                                form.render('select');
                            })
                            var citySelect = $('#drpcity').find('option');
                            for (var i = 0; i < citySelect.length; i++) {
                                if (citySelect.eq(i).text() == city) {
                                    citySelect.eq(i).attr('selected', 'selected');
                                    form.render('select');
                                }
                            }
                            form.render('select');
                        });
                    });
                    form.on('select(drpprovince)', function (data) {
                        var code = data.value;
                        $('#drpcity').html("");
                        $('#drparea').html("");
                        $.getJSON('../express/findCity?code=' + code, function (data) {
                            $.each(data.data, function (i, item) {
                                if (i == 0) {
                                    $.getJSON('../express/findCity?code=' + code, function (data) {
                                        $.each(data.data, function (i, item) {
                                            $('#drparea').append('<option  value=' + item.code + '>' + item.name + "</option>");
                                        })
                                        form.render('select');
                                    });
                                }
                                $('#drpcity').append('<option  value=' + item.code + '>' + item.name + "</option>");
                            })
                            form.render('select');
                        });
                    });
                    form.on('select(drpcity)', function (data) {
                        var code = data.value;
                        $('#drparea').html("");
                        $.getJSON('../express/findCity?code=' + code, function (data) {
                            $.each(data.data, function (i, item) {
                                $('#drparea').append('<option  value=' + item.code + '>' + item.name + "</option>");
                            })
                            form.render('select');
                        });
                    });


                    // 寄件省市区三级联动
                    var sendProvince = res.data.sendProvince;
                    var sendProvinceCode = res.data.sendProvinceCode;
                    var sendCity = res.data.sendCity;
                    var sendCityCode = res.data.sendCityCode;
                    var sendZone = res.data.sendZone;

                    var sendCode='';
                    $.getJSON("../express/findProvince", function (data) {
                        $.each(data.data, function (i, item) {
                            if (i == 0)
                            {
                                sendCode = item.code
                            }
                            $('#drpprovince1').append('<option  value=' + item.code + '>' + item.name + "</option>");
                        })
                        var proSendSelect = $('#drpprovince1').find('option');
                        for (var i = 0; i < proSendSelect.length; i++) {
                            if (proSendSelect.eq(i).text() == sendProvince) {
                                proSendSelect.eq(i).attr('selected', 'selected');
                            }
                            form.render('select');
                        }

                        $.getJSON('../express/findCity?code=' + sendProvinceCode, function (data) {
                            $.each(data.data, function (i, item) {
                                if (i == 0) {
                                    $.getJSON('../express/findCity?code=' + sendCityCode, function (data) {
                                        $.each(data.data, function (i, item) {
                                            $('#drparea1').append('<option  value=' + item.code + '>' + item.name + "</option>");
                                        })
                                        form.render('select');
                                        var zoneSendSelect = $('#drparea1').find('option');
                                        // alert(zoneSelect.length)
                                        for (var i = 0; i < zoneSendSelect.length; i++) {
                                            if (zoneSendSelect.eq(i).text() == sendZone) {
                                                zoneSendSelect.eq(i).attr('selected', 'selected');
                                            }
                                            form.render('select');
                                        }
                                    });
                                }
                                $('#drpcity1').append('<option  value=' + item.code + '>' + item.name + "</option>");
                                form.render('select');
                            })
                            var citySendSelect = $('#drpcity1').find('option');
                            for (var i = 0; i < citySendSelect.length; i++) {
                                if (citySendSelect.eq(i).text() == sendCity) {
                                    citySendSelect.eq(i).attr('selected', 'selected');
                                    form.render('select');
                                }
                            }
                            form.render('select');
                        });
                    });
                    form.on('select(drpprovince1)', function (data) {
                        var code1 = data.value;
                        $('#drpcity1').html("");
                        $('#drparea1').html("");
                        $.getJSON('../express/findCity?code=' + code1, function (data) {
                            $.each(data.data, function (i, item) {
                                if (i == 0) {
                                    $.getJSON('../express/findCity?code=' + code, function (data) {
                                        $.each(data.data, function (i, item) {
                                            $('#drparea1').append('<option  value=' + item.code + '>' + item.name + "</option>");
                                        })
                                        form.render('select');
                                    });
                                }
                                $('#drpcity1').append('<option  value=' + item.code + '>' + item.name + "</option>");
                                form.render('select');
                            })

                        });
                    });
                    form.on('select(drpcity1)', function (data) {
                        var code = data.value;
                        $('#drparea1').html("");
                        $.getJSON('../express/findCity?code=' + code, function (data) {
                            $.each(data.data, function (i, item) {
                                $('#drparea1').append('<option  value=' + item.code + '>' + item.name + "</option>");
                            })
                            form.render('select');
                        });
                    });
                }else{
                    layer.alert(res.message, {icon: 0})
                }
            },
            error: function (err) {
                layer.alert(err.message, {icon: 0});
            }
        })
    },200)

});


//保存
layui.use(['form','laydate','layer'], function () {
    var form = layui.form;
    var laydate = layui.laydate;
    var layer = layui.layer;
    laydate.render({
        elem: '#timeRange'
        ,type: 'time'
        ,range: true
    });
    form.render();

    //监听提交
    form.on('submit(formDemo)', function(data){
        var deliveryNo = $('#deliveryNo').val();
        var deliveryId = $('#delivererId').find('option:selected').attr('id');
        var receiverName = $('#receiverName').val();
        var receiverTel = $('#receiverTel').val();
        var receiverAddress = $('#receiverAddress').val();
        var sendName = $('#sendName').val();
        var sendTel = $('#sendTel').val();
        var sendAddress = $('#sendAddress').val();
        var province= $('#drpprovince').find('option:selected').text();
        var city= $('#drpcity').find('option:selected').text();
        var zone= $('#drparea').find('option:selected').text();
        var sendProvince= $('#drpprovince1').find('option:selected').text();
        var sendCity= $('#drpcity1').find('option:selected').text();
        var sendZone= $('#drparea1').find('option:selected').text();
        var data = {
            id: id,
            deliveryNo:deliveryNo,
            delivererId:deliveryId,
            receiverName:receiverName,
            receiverTel:receiverTel,
            receiverAddress:receiverAddress,
            sendName:sendName,
            sendTel:sendTel,
            sendAddress:sendAddress,
            city:city,
            zone:zone,
            province:province,
            sendCity:sendCity,
            sendZone:sendZone,
            sendProvince:sendProvince,
        };
        $.ajax({
            type: 'POST',
            url: '../send/updateExpSendOrder',
            dataType: 'json',
            contentType: 'application/json; charset=utf-8',
            headers: {
                'Accept': 'application/json; charset=utf-8',
                'Authorization': 'Basic ' + sessionStorage.getItem('token')
            },
            data: JSON.stringify(data),
            success: function (res) {
                if(res.stateCode == 100){
                    layer.alert('提交成功',{icon: 1});
                    setTimeout(function () {
                        location.href = '../home/postOrderManageHtml';
                    },1500)
                }else{
                    layer.alert(res.message, {icon: 0})
                }
            },error: function (err) {
                layer.alert(err.message, {icon: 0})
            }
        })
        return false;
    });
});

