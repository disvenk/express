
var regPhoneNum = /^1(3|4|5|7|8)\d{9}$/;
var isDefault;
var city;
var type;
var text1;
var text2;
var text3;

//物品类型
$.ajax({
    type: "POST",
    url: "../goods/list",
    contentType: "application/json; charset=utf-8",
    headers: {
        "Accept": "application/json; charset=utf-8",
        "Authorization": "Basic " + sessionStorage.getItem("token")
    },
    success: function (res) {
        if (res.stateCode == 100) {
            $('#type').append('<option value="" disabled selected style="display: none">--请选择--</option>');
            for (var i = 0; i < res.data.length; i++) {
                var option = '<option class="' + res.data[i].id + '">' + res.data[i].name + '</option>';
                $('#type').append(option);
            }
        } else {
            layer.msg(res.message);
        }
    },
    error: function (err) {
        layer.msg(err.message);
    }
});
// 快递公司
$.ajax({
    type: "POST",
    url: "../express/findCompayBySchool",
    contentType: "application/json; charset=utf-8",
    headers: {
        "Accept": "application/json; charset=utf-8",
        "Authorization": "Basic " + sessionStorage.getItem("token")
    },
    success: function (res) {
        if (res.stateCode == 100) {
            $('#deliveryCom').append('<option value="" disabled selected style="display: none">--请选择--</option>');
            for (var i = 0; i < res.data.length; i++) {
                var option = '<option class="' + res.data[i].com + '" id="' + res.data[i].id + '">' + res.data[i].name + '</option>';
                $('#deliveryCom').append(option);
            }
        } else {
            layer.msg(res.message);
        }
    },
    error: function (err) {
        layer.msg(err.message);
    }
});

$(function () {
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
                        var post = '<div class="post_list" onclick="receive()" value="'+res.data[i].id+'">' +
                            '                <div><span>收件人：</span><span class="node-receiver1">'+res.data[i].name+'</span></div>\n' +
                            '                <div><span>电话：</span><span class="node-tel1">'+res.data[i].tel+'</span></div>\n' +
                            '                <div><span style="display: inline-block;width: 47px">地址：</span><div class="node-address1" style="position: relative;left: 47px;top: -21px;width: calc(100% - 47px)">'+res.data[i].province+res.data[i].city+res.data[i].zone+res.data[i].address+'</div></div>\n' +
                            '            </div>';
                        isDefault=true;
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
    // 寄件地址列表
    $.ajax({
        type: "POST",
        url: "../sendAddress/list",
        contentType: "application/json; charset=utf-8",
        headers: {
            "Accept": "application/json; charset=utf-8",
            "Authorization": "Basic " + sessionStorage.getItem("token")
        },
        success:function (res) {
            if(res.stateCode == 100){
            if(res.data.length == 0){
                $('#addPost').css('display','block');
            }else{
                for (var i = 0; i < res.data.length; i++) {
                    if(res.data[i].isDefault){
                        var post = '<div class="post_list" onclick="post()"  value="'+res.data[i].id+'">' +
                            '                <div><span>寄件人：</span><span class="node-receiver2">'+res.data[i].name+'</span></div>\n' +
                            '                <div><span>电话：</span><span class="node-tel2">'+res.data[i].tel+'</span></div>\n' +
                            '                <div><span style="display: inline-block;width: 47px">地址：</span><div class="node-address2" style="position: relative;left: 47px;top: -21px;width: calc(100% - 47px)">'+res.data[i].province+res.data[i].city+res.data[i].zone+res.data[i].address+'</div></div>\n' +
                            '            </div>';
                        isDefault=true;
                        $('#postAddress').append(post);
                        break;
                    } else {
                        isDefault=false;
                    }
                }
                if (!isDefault) {
                    $('#addPost').css('display','block');
                }
            }
            } else {
                layer.msg(res.message);
            }
        },
        error:function (err) {
            layer.msg(err.message);
        }
    })
})

//新增收件地址模态框中的地址列表
function receive() {
    var index = layer.load(2, {
        shade: [0.6,'#000']
    });
    $('.modal-address-list').css('display','block');
    type = 1;
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

//新增寄件地址模态框中的地址列表
function post() {
    var index = layer.load(2, {
        shade: [0.6,'#000']
    });
    $('.modal-address-list1').css('display','block');
    type = 2;
    $.ajax({
        type: "POST",
        url: "../sendAddress/list",
        contentType: "application/json; charset=utf-8",
        headers: {
            "Accept": "application/json; charset=utf-8",
            "Authorization": "Basic " + sessionStorage .getItem("token")
        },
        success: function (res) {
            if(res.stateCode == 100){
                // console.log(res);
                layer.close(index);
                $('#list1').html('');
                for(var i= 0; i < res.data.length; i++){
                    var address = '';
                    address += '    <div class="post_list" style="margin-bottom: 10px;"><div onclick="addPost($(this))" class="list_container_body" value="'+res.data[i].id+'">\n' +
                        '        <span class="list_name">'+res.data[i].name+'</span>\n' +
                        '        <span class="list_phone">'+res.data[i].tel+'</span>\n' +
                        '        <div class="list_address">\n' +
                        ''+res.data[i].province+res.data[i].city+res.data[i].zone+res.data[i].address+''+
                        '        </div>\n' +
                        '    </div></div>';
                    $('#list1').append(address);
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
    type=1;
});

// 新增寄件地址按钮点击事件
$('#addPost').click(function () {
    post();
    $('.modal-address-list1').css('display','block');
    type=2;
});
function addReceive(_this) {
    type = 1;
    var name=_this.find('.list_name').html();
    var tel=_this.find('.list_phone').html();
    var address=_this.find('.list_address').html();
    $('#addReceive').css('display','none');
    $('#receiveAddress .post_list').eq(0).remove();
    $('.modal-address-list').css('display','none');
    var post = '<div class="post_list" onclick="receive()" value="'+_this.attr('value')+'">\n' +
        '                <div><span>收件人：</span><span class="node-receiver1">'+name+'</span></div>\n' +
        '                <div><span>电话：</span><span class="node-tel1">'+tel+'</span></div>\n' +
        '                <div><span style="display: inline-block;width: 47px">地址：</span><div class="node-address1" style="position: relative;left: 47px;top: -21px;width: calc(100% - 47px)">'+address+'</div></div>\n' +
        '            </div>';
    $('#receiveAddress').append(post);
}
function addPost(_this) {
    type=2;
    var name=_this.find('.list_name').html();
    var tel=_this.find('.list_phone').html();
    var address=_this.find('.list_address').html();
    $('#addPost').css('display','none');
    $('#postAddress .post_list').eq(0).remove();
    $('.modal-address-list1').css('display','none');
    var post = '<div class="post_list" onclick="post()" value="'+_this.attr('value')+'">' +
        '                <div><span>寄件人：</span><span class="node-receiver2">'+name+'</span></div>\n' +
        '                <div><span>电话：</span><span class="node-tel2">'+tel+'</span></div>\n' +
        '                <div><span style="display: inline-block;width: 47px">地址：</span><div class="node-address2" style="position: relative;left: 47px;top: -21px;width: calc(100% - 47px)">'+address+'</div></div>\n' +
        '            </div>';
    $('#postAddress').append(post);
}
// 新增收件地址模态框中的新增按钮
$('.add-address-list').click(function () {
    $('.modal-address-list').css('display','none');
    $('.more-address').css('display','block');
    $('#receiver').html('收件人')
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
    $('#name').html('');
    $('#phone').html('');
    $('#province').html('');
    $('#address').html('');
    var receiver=$('#name').html();
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

    $('#save').click(function () {
        if ($('#name').val() == '') {
            layer.msg('名字不能为空');
            return
        }
        if ($('#phone').val() == '') {
            layer.msg('手机号不能为空');
            return
        }
        if (!regPhoneNum.test($('#phone').val())) {
            layer.msg('手机号不合法');
            return
        }
        if ($('#province').val() == '') {
            layer.msg('请选择省市区');
            return
        }
        if ($('#adress').val() == '') {
            layer.msg('请输入详细地址');
            return
        }
        var index = layer.load(2, {
            shade: [0.6, '#000'] //0.1透明度的白色背景
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
            url: '../receiveAddress/save',
            contentType: "application/json; charset=utf-8",
            headers: {
                "Accept": "application/json; charset=utf-8",
                "Authorization": "Basic " + sessionStorage.getItem("token")
            },
            data: JSON.stringify(data),
            success: function (res) {
                if (res.stateCode == 100) {
                    layer.close(index);
                    $('#addReceive').css('display', 'none');
                    $('#receiveAddress .post_list').eq(0).remove();
                    $('.more-address').css('display', 'none');
                    var post = '<div onclick="receive()" class="post_list" value="' + res.data.id + '">\n' +
                        '                <div><span>收件人：</span><span>' + data.name + '</span></div>\n' +
                        '                <div><span>电话：</span><span>' + data.tel + '</span></div>\n' +
                        '                <div><span style="display: inline-block;width: 47px">地址：</span><div style="position: relative;left: 47px;top: -21px;width: calc(100% - 47px)">' + text1 + text2 + text3 + data.address + '</div></div>\n' +
                        '            </div>'
                    $('#receiveAddress').append(post);
                } else {
                    layer.close(index);
                    layer.msg(res.message);
                }
            },
            error: function (err) {
                layer.close(index);
                layer.msg(err.message);
            }
        });
    })
})
// 新增寄件地址模态框中的新增按钮
$('.add-address-list1').click(function () {
    $('.modal-address-list1').css('display','none');
    $('.more-address').css('display','block');
    $('#receiver').html('寄件人')
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
    $('#name').html('');
    $('#phone').html('');
    $('#province').html('');
    $('#address').html('');
    var receiver=$('#name').html();
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

    $('#save').click(function () {
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
            url: '../sendAddress/save',
            contentType: "application/json; charset=utf-8",
            headers: {
                "Accept": "application/json; charset=utf-8",
                "Authorization": "Basic " + sessionStorage .getItem("token")
            },
            data: JSON.stringify(data),
            success: function (res) {
                if(res.stateCode == 100){
                    layer.close(index);
                    $('#addPost').css('display','none');
                    $('#postAddress .post_list').eq(0).remove();
                    $('.more-address').css('display','none');
                    var post = '<div class="post_list" onclick="post()" value="'+res.data.id+'">' +
                        '                <div><span>寄件人：</span><span>'+data.name+'</span></div>\n' +
                        '                <div><span>电话：</span><span>'+data.tel+'</span></div>\n' +
                        '                <div><span style="display: inline-block;width: 47px">地址：</span><div style="position: relative;left: 47px;top: -21px;width: calc(100% - 47px)">'+text1+text2+text3+data.address+'</div></div>\n' +
                        '            </div>';
                    $('#postAddress').append(post);
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
    })

});
$('#deliveryWay').change(function () {
    var aaa = $(this).children('option:selected').attr('value');
   if(aaa == 1){
       $('#time').show();
   }else{
       $('#time').hide();
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
        data: JSON.stringify({type: 0}),
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

// 点击确认订单提交

$('#sure').click(function () {
    var selectedOption = $('.select-choice option:selected').val();



    // 收件地址
    var receiveId= $('#receiveAddress .post_list').attr('value');
    var nodeReceiver=$('.node-receiver1').html();
    var nodeReceiverTel=$('.node-tel1').html();
    var nodeReceiverAddress=$('.node-address1').html();
    // 寄件地址
    var postId= $('#postAddress .post_list').attr('value');
    var nodePoster=$('.node-receiver2').html();
    var nodePosterTel=$('.node-tel2').html();
    var nodePosterAddress=$('.node-address2').html();
    // 物品类型
    var goodsId=$('#type option:selected').attr('class');
    var goodsType = $('#type option:selected').val();
    // 快递公司
    var deliveryTypeId=$('#deliveryCom option:selected').attr('id');
    var deliveryType = $('#deliveryCom option:selected').val();
    // 取件时间
    var showArea = $('#showArea').val();
    // 备注
    var tips=$('.tips-textarea').val();
    var data={
        sendPid:postId,
        recievePid:receiveId,
        goodsTypeId:goodsId,
        doorTime:showArea,
        expressCompanyId:deliveryTypeId,
        sendType:selectedOption,
        desc:tips
    };
    if(data.sendPid == '' || data.sendPid == undefined || data.sendPid == null){
        layer.msg('请选择寄件地址');
        return;
    }
    if(data.recievePid == '' || data.recievePid == undefined || data.recievePid == null){
        layer.msg('请选择收件地址');
        return;
    }
    if(data.sendType == '' || data.sendType == undefined || data.sendType == null){
        layer.msg('请选择寄件方式');
        return;
    }
    if(data.goodsTypeId == '' || data.goodsTypeId == undefined || data.goodsTypeId == null){
        layer.msg('请选择物品类型');
        return;
    }
    if(data.expressCompanyId == '' || data.expressCompanyId == undefined || data.expressCompanyId == null){
        layer.msg('请选择快递公司');
        return;
    }
    if($('.select-choice option:selected').attr('value') == 1){
        if(data.doorTime == '' || data.doorTime == undefined || data.doorTime == null){
            layer.msg('请选择寄件时间');
            return;
        }
    }
    var index = layer.load(2, {
        shade: [0.6,'#000']
    });
    $.ajax({
        type: "POST",
        url: "../sendExpressOrder/saveExpressOrder",
        contentType: "application/json; charset=utf-8",
        headers: {
            "Accept": "application/json; charset=utf-8",
            "Authorization": "Basic " + sessionStorage.getItem("token")
        },
        data:JSON.stringify(data),
        success: function (res) {
            if (res.stateCode == 100) {
                layer.close(index);
                if (selectedOption == 1) {
                    location.href = '../homePage/postWay1Html?id='+res.data;
                } else if (selectedOption == 0) {
                    location.href = '../homePage/postWay2OrderHtml?id='+res.data;
                }
            } else {
                layer.close(index);
                layer.msg(res.message);
            }
        },
        error: function (err) {
            layer.close(index);
            layer.msg(err.message);
        }
    });
});


