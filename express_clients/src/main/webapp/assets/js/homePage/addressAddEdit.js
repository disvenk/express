var url = window.location.href;
var array = url.split('&');
var type = array[0].split('=')[1];
var id = array[1].split('=')[1];
var regPhoneNum = /^1(3|4|5|7|8)\d{9}$/;
var isDefault;
var href;
var city;
var text1;
var text2;
var text3;
if(id == '' || id == null || id == undefined){
    document.title = '新增地址';
}else{
    document.title = '编辑地址';
    if(type == 1){
        $.ajax({
            type: "POST",
            url: "../sendAddress/detail",
            contentType: "application/json; charset=utf-8",
            headers: {
                "Accept": "application/json; charset=utf-8",
                "Authorization": "Basic " + sessionStorage .getItem("token")
            },
            data: JSON.stringify({id: id}),
            success: function (res) {
                if(res.stateCode == 100){
                    $('#name').val(res.data.name);
                    $('#phone').val(res.data.tel);
                    $('#province').val(res.data.province+res.data.city+res.data.zone);
                    $('#address').val(res.data.address);
                    text1 = res.data.province;
                    text2 = res.data.city;
                    text3 = res.data.zone;
                    isDefault = res.data.isDefault;
                }else{
                    layer.msg(res.message);
                }
            },
            error: function (err) {
                layer.msg(err.message);
            }
        });
    }else{
        $.ajax({
            type: "POST",
            url: "../receiveAddress/detail",
            contentType: "application/json; charset=utf-8",
            headers: {
                "Accept": "application/json; charset=utf-8",
                "Authorization": "Basic " + sessionStorage .getItem("token")
            },
            data: JSON.stringify({id: id}),
            success: function (res) {
                if(res.stateCode == 100){
                    $('#name').val(res.data.name);
                    $('#phone').val(res.data.tel);
                    $('#province').val(res.data.province+res.data.city+res.data.zone);
                    $('#address').val(res.data.address);
                    text1 = res.data.province;
                    text2 = res.data.city;
                    text3 = res.data.zone;
                    isDefault = res.data.isDefault;
                }else{
                    layer.msg(res.message);
                }
            },
            error: function (err) {
                layer.msg(err.message);
            }
        });
    }

}
if(type == 1){
    $('#type').html('寄件人');
}else{
    $('#type').html('收件人');
}
//获取省市区
$(function () {
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
});
var data = {
    id: id,
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
    if($('#address').val() == ''){
        layer.msg('请输入详细地址');
        return
    }
    var index = layer.load(2, {
        shade: [0.6,'#000'] //0.1透明度的白色背景
    });
    data = {
        id: id,
        name: $('#name').val(),
        tel: $('#phone').val(),
        province: text1,
        city: text2,
        zone: text3,
        address: $('#address').val(),
        isDefault: isDefault
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
                window.location.href = '../homePage/addressListHtml?type='+type;
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
        if(type == 1){
            href = '../sendAddress/save';
           save(href);
        }else{
            href = '../receiveAddress/save';
            save(href);
        }
});
