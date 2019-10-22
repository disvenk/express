var url = window.location.href;
var type = url.split('=')[1];
if(type == undefined || type == null || type == ''){
    type = 1
}
if(type == 1){
    $('#post').addClass('borderColor');
    post();
}else{
    $('#receive').addClass('borderColor');
    receive();
}
$('#post').click(function(){
    type = 1;
    $('#list').html('');
    $(this).siblings().removeClass('borderColor');
    $(this).addClass('borderColor');
    post();
});
$('#receive').click(function(){
    type = 2;
    $('#list').html('');
    $(this).siblings().removeClass('borderColor');
    $(this).addClass('borderColor');
    receive();
});
//寄件地址
function post() {
    var index = layer.load(2, {
        shade: [0.6,'#000']
    });
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
                layer.close(index);
                $('#list').html('');
                for(var i= 0; i < res.data.length; i++){
                    var address = '';
                    address += '    <div class="list_container_body">\n' +
                        '        <span class="list_name">'+res.data[i].name+'</span>\n' +
                        '        <span class="list_phone">'+res.data[i].tel+'</span>\n' +
                        '        <div class="list_address">\n' +
                        ''+res.data[i].province+res.data[i].city+res.data[i].zone+res.data[i].address+''+
                        '        </div>\n' +
                        '    </div>\n' +
                        '            <div class="list_container_footer">';
                    if(res.data[i].isDefault){
                        address += '<span onclick="checked2('+res.data[i].id+')">' +
                            '<label><input name="address" type="radio" value="" checked/>默认地址 </label>' +
                            '</span>'
                    }else{
                        address += '<span onclick="checked2('+res.data[i].id+')">' +
                            '<label><input name="address" type="radio" value=""/>默认地址 </label>' +
                            '</span>'
                    }
                    address +=          '<span>\n' +
                        '                   <span onclick="editPost('+res.data[i].id+')"> <img src="../assets/images/edit.png" alt=""><span style="margin-right: 12px">编辑</span></span>\n' +
                        '                    <span onclick="deletePost('+res.data[i].id+', $(this))"> <img src="../assets/images/delete.png" alt=""><span>删除</span></span>\n' +
                        '                </span>\n' +
                        '            </div>';
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
//收件地址
function receive() {
    var index = layer.load(2, {
        shade: [0.6,'#000']
    });
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
                    address += '    <div><div class="list_container_body">\n' +
                        '        <span class="list_name">'+res.data[i].name+'</span>\n' +
                        '        <span class="list_phone">'+res.data[i].tel+'</span>\n' +
                        '        <div class="list_address">\n' +
                        ''+res.data[i].province+res.data[i].city+res.data[i].zone+res.data[i].address+''+
                        '        </div>\n' +
                        '    </div>\n' +
                        '            <div class="list_container_footer">';
                    if(res.data[i].isDefault){
                        address += '<span onclick="checked1('+res.data[i].id+')">' +
                            '<label><input name="address" type="radio" value="" checked/>默认地址 </label>' +
                            '</span>'
                    }else{
                        address += '<span onclick="checked1('+res.data[i].id+')">' +
                            '<label><input name="address" type="radio" value=""/>默认地址 </label>' +
                            '</span>'
                    }
                    address +=          '<span>\n' +
                        '                    <span onclick="editReceive('+res.data[i].id+')"><img src="../assets/images/edit.png" alt=""><span style="margin-right: 12px">编辑</span></span>\n' +
                        '                     <span onclick="deleteReceive('+res.data[i].id+', $(this))"><img src="../assets/images/delete.png" alt=""><span>删除</span></span>\n' +
                        '                </span>\n' +
                        '            </div></div>';
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
//寄件人删除
function deletePost(id) {
    $('#iosDialog1').show();
    $('#suredelete').click(function () {
        $('#iosDialog1').hide();
        var index = layer.load(2, {
            shade: [0.6,'#000']
        });
        $.ajax({
            type: "POST",
            url: "../sendAddress/remove",
            contentType: "application/json; charset=utf-8",
            headers: {
                "Accept": "application/json; charset=utf-8",
                "Authorization": "Basic " + sessionStorage .getItem("token")
            },
            data: JSON.stringify({id: id}),
            success: function (res) {
                if(res.stateCode == 100){
                    layer.close(index);
                    post();
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
//收件人删除
function deleteReceive(id) {
    $('#iosDialog1').show();
    $('#suredelete').click(function () {
        $('#iosDialog1').hide();
        var index = layer.load(2, {
            shade: [0.6,'#000']
        });
        $.ajax({
            type: "POST",
            url: "../receiveAddress/remove",
            contentType: "application/json; charset=utf-8",
            headers: {
                "Accept": "application/json; charset=utf-8",
                "Authorization": "Basic " + sessionStorage .getItem("token")
            },
            data: JSON.stringify({id: id}),
            success: function (res) {
                if(res.stateCode == 100){
                    layer.close(index);
                    receive();
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
//寄件地址编辑
function editPost(id) {
    window.location.href = '../homePage/addressAddEditHtml?type=1&id='+id;
}
//收件地址编辑
function editReceive(id) {
    window.location.href = '../homePage/addressAddEditHtml?type=2&id='+id;
}
//新增
$('#addressAddEdit').click(function () {
    if(type == 1){
        window.location.href = '../homePage/addressAddEditHtml?type=1&id=';
    }else {
        window.location.href = '../homePage/addressAddEditHtml?type=2&id=';
    }
});
//寄件设为默认
function checked2(id) {
    var index = layer.load(2, {
        shade: [0.6,'#000']
    });
    $.ajax({
        type: "POST",
        url: "../sendAddress/saveDefault",
        contentType: "application/json; charset=utf-8",
        headers: {
            "Accept": "application/json; charset=utf-8",
            "Authorization": "Basic " + sessionStorage .getItem("token")
        },
        data: JSON.stringify({id: id}),
        success: function (res) {
            if(res.stateCode == 100){
                layer.close(index);
            }else{
                layer.close(index);
            }
        },
        error: function (err) {
            layer.close(index);
        }
    });
}
//收件设为默认
function checked1(id) {
    var index = layer.load(2, {
        shade: [0.6,'#000']
    });
    $.ajax({
        type: "POST",
        url: "../receiveAddress/saveDefault",
        contentType: "application/json; charset=utf-8",
        headers: {
            "Accept": "application/json; charset=utf-8",
            "Authorization": "Basic " + sessionStorage .getItem("token")
        },
        data: JSON.stringify({id: id}),
        success: function (res) {
            if(res.stateCode == 100){
                layer.close(index);
            }else{
                layer.close(index);
            }
        },
        error: function (err) {
            layer.close(index);
        }
    });
}