/**
 * Created by 12452 on 2017/10/20.
 */



var categoryid = '';
var pageNum = 1;
var data = {
    pageNum: pageNum
};
var ss;
var urlle = sessionStorage.getItem('urllen');
var productIds = '';

// ajax加载列表
    $.ajax({
        type: 'POST',
        url: '../user/getUserListAllByUser',
        dataType: 'json',

        contentType: 'application/json; charset=utf-8',
        headers: {
            'Accept': 'application/json; charset=utf-8',
            'Authorization': 'Basic ' + sessionStorage.getItem('token')
        },
        data: JSON.stringify(data),
        success: function (res) {
            load(res);
        }
    })

var id;
//新增用户
$('#addCategory').click(function () {
    $('#approveModal').modal('show');
    $("#approveModalLabel").html("新增用户")
    $('#userForm')[0].reset();
    $(".roleTD1").html("")

    $.ajax({
        url : '../role/getRoleListAllByUser',
        type : 'POST',
        dataType : 'json',
        contentType: 'application/json; charset=utf-8',
        headers: {
            'Accept': 'application/json; charset=utf-8',
            'Authorization': 'Basic ' + sessionStorage.getItem('token')
        },
        data:JSON.stringify({pageNum:1}),
        success : function(res) {
            $(res.data).each(function(index){
                //生成CheckBox
                var checkbox=$("<input type='checkbox' name='roleIds1'/>");
                checkbox.val(this.id);
                if(index==0){
                    $(".roleTD1").append(checkbox);
                    $(".roleTD1").append(this.name);
                    $(".roleTD1").append("&nbsp;");
                    $(".roleTD1").append("&nbsp;");
                }else if(index%4==0){
                    $(".roleTD1").append("</br>");
                    $(".roleTD1").append(checkbox);
                    $(".roleTD1").append(this.name);
                    $(".roleTD1").append("&nbsp;");
                    $(".roleTD1").append("&nbsp;");
                }else{
                    $(".roleTD1").append(checkbox);
                    $(".roleTD1").append(this.name);
                    $(".roleTD1").append("&nbsp;");
                    $(".roleTD1").append("&nbsp;");


                }

            });
        },
        error : function(msg) {
            alert('加载异常!');
        }
    });
})

$('#addSure').click(function (){
    var tel = $('#tel').val().replace(/[ ]/g,"");
    var userName = $('#userName').val().replace(/[ ]/g,"");
    var password = $("#password").val().replace(/[ ]/g,"");
    var relName = $("#relName").val().replace(/[ ]/g,"");
    var roleArr= new Array();
    $('input:checkbox[name=roleIds1]:checked').each(function(i){
        roleArr.push($(this).val())
    });
    var roleStr = roleArr.join(",");
    ss = {
        tel:tel,
        userName: userName,
        password:password,
        relName:relName,
        roleStr:roleStr,
    }
    var myreg=/^[1][3,4,5,7,8][0-9]{9}$/;
    if (tel == ''){
        layer.alert('手机号不能为空！', {icon: 0});
    }else if(!myreg.test(tel)){
        layer.alert('手机号格式不正确！', {icon: 0});
    }else if(userName==""){
        layer.alert('用户名不能为空！', {icon: 0});
    }else if(password==""){
        layer.alert('密码不能为空！', {icon: 0});
    }else if(relName==""){
        layer.alert('真实姓名不能为空！', {icon: 0});
    }else {
        $.ajax({
            type: 'POST',
            url: '../user/newUserAddManage',
            dataType: 'json',
            contentType: 'application/json; charset=utf-8',
            headers: {
                'Accept': 'application/json; charset=utf-8',
                'Authorization': 'Basic ' + sessionStorage.getItem('token')
            },
            data: JSON.stringify(ss),
            success: function (res) {
                if (res.stateCode == 100) {
                    window.location.reload();
                } else {
                    alert(res.message)
                }
            },
            error: function (err) {
                alert(err.message)
            }
        })
    }
})

var uploadId;
function load(res) {
    $('.spinner').hide();
    $('#tbody').html('');
    var table1 = '';
        for (var i = 0; i< res.data.length; i++) {
            table1 += '<tr id="' + res.data[i].id + '">' +
                '<td><img src="'+(res.data[i].icon==""?"../assets/img/defaultIcon.png":res.data[i].icon)+'" class="goods-img" alt=""></td>' +
                '<td>' + res.data[i].tel+ '</td>' +
                '<td>' + res.data[i].userName + '</td>' +
                '<td>' + res.data[i].relName + '</td>' +
                '<td style="width:100px"><span style="padding-right: 5px" class="upload setting-btn">上传头像</span><span style="padding-right: 5px" class="editReset checkModal setting-btn">密码重置</span><span id="storageEdit" class="edit2 editModal setting-btn">编辑</span><span style="padding-left: 5px" class="delete setting-btn">删除</span></td></tr>';
        }
        $('#tbody').append(table1);


    // 编辑
    $('#tbody .editModal').click(function(){
        $('#approveModal2').modal('show');
        $(".roleTD2").html("")
        $("#pressent").html("");
        $.ajax({
            url : '../role/getRoleListAllByUser',
            type : 'POST',
            dataType : 'json',
            contentType: 'application/json; charset=utf-8',
            headers: {
                'Accept': 'application/json; charset=utf-8',
                'Authorization': 'Basic ' + sessionStorage.getItem('token')
            },
            data:JSON.stringify({pageNum:1}),
            success : function(res) {
                $(res.data).each(function(index){
                    //生成CheckBox
                    var checkbox=$("<input type='checkbox' name='roleIds2'/>");

                    checkbox.val(this.id);
                    if(index==0){
                        $(".roleTD2").append(checkbox);
                        $(".roleTD2").append(this.name);
                        $(".roleTD2").append("&nbsp;");
                        $(".roleTD2").append("&nbsp;");
                    }else if(index%4==0){
                        $(".roleTD2").append("</br>");
                        $(".roleTD2").append(checkbox);
                        $(".roleTD2").append(this.name);
                        $(".roleTD2").append("&nbsp;");
                        $(".roleTD2").append("&nbsp;");
                    }else{
                        $(".roleTD2").append(checkbox);
                        $(".roleTD2").append(this.name);
                        $(".roleTD2").append("&nbsp;");
                        $(".roleTD2").append("&nbsp;");
                    }

                });
                $.ajax({
                    type: 'POST',
                    url: '../user/userCheckDetail',
                    dataType: 'json',
                    contentType: 'application/json; charset=utf-8',
                    headers: {
                        'Accept': 'application/json; charset=utf-8',
                        'Authorization': 'Basic ' + sessionStorage.getItem('token')
                    },
                    data: JSON.stringify({id: id}),
                    success: function (res) {
                        if (res.stateCode == 100) {
                            $('#tel2').val(res.data.tel)
                            $('#userName2').val(res.data.userName);
                            $("#relName2").val(res.data.relName);
                            $(res.data.role).each(function (index, element) {
                                $("input:checkbox[value=" + element.id + "][name=roleIds2]").prop("checked", "checked");
                            });
                        } else {
                            alert(res.message);
                        }

                    },
                    error: function (err) {
                        alert(err.message);
                    }
                })
            },
            error : function(msg) {
                alert('加载异常!');
            }
        });
        id = $(this).parent().parent().attr('id');

         });

        $('#editorSure').click(function (){
            var tel = $('#tel2').val().replace(/[ ]/g,"");
            var userName = $('#userName2').val().replace(/[ ]/g,"");
            var password = $("#password2").val().replace(/[ ]/g,"");
            var relName = $("#relName2").val().replace(/[ ]/g,"");
            var roleArr= new Array();
            $('input:checkbox[name=roleIds2]:checked').each(function(i){
                roleArr.push($(this).val())
            });
            var roleStr = roleArr.join(",");
            ss = {
                id:id,
                tel:tel,
                userName: userName,
                password:password,
                relName:relName,
                roleStr:roleStr
            }
            var myreg=/^[1][3,4,5,7,8][0-9]{9}$/;
            if (tel == ''){
                layer.alert('手机号不能为空！', {icon: 0});
            }else if(!myreg.test(tel)){
                layer.alert('手机号格式不正确！', {icon: 0});
            }else if(userName==""){
                layer.alert('用户名不能为空！', {icon: 0});
            }else if(relName==""){
                layer.alert('真实姓名不能为空！', {icon: 0});
            }else {
                $.ajax({
                    type: 'POST',
                    url: '../user/newUserAddManage',
                    dataType: 'json',
                    contentType: 'application/json; charset=utf-8',
                    headers: {
                        'Accept': 'application/json; charset=utf-8',
                        'Authorization': 'Basic ' + sessionStorage.getItem('token')
                    },
                    data: JSON.stringify(ss),
                    success: function (res) {
                        if (res.stateCode == 100) {
                            window.location.reload();
                        } else {
                            alert(res.message)
                        }
                    },
                    error: function (err) {
                        alert(err.message)
                    }
                })
            }
        })


    // 用户删除
    $('.delete').click(function(){
        $('#myModal').modal('show');
        id = $(this).parent().parent().attr('id');
    })

    $('#delSure').click(function () {
        $.ajax({
            type: 'POST',
            url: '../user/deleteUser',
            dataType: 'json',
            contentType: 'application/json; charset=utf-8',
            headers: {
                'Accept': 'application/json; charset=utf-8',
                'Authorization': 'Basic ' + sessionStorage.getItem('token')
            },
            data: JSON.stringify({id: id}),
            success: function (res) {
                $('#myModal').modal('hide');
                if(res.stateCode != 100){
                    alert(res.message);
                }
                window.location.reload();
            },
            error: function (err) {
                $('#myModal').modal('hide');
                alert(err.message);
            }
        });
    })

    var resetId;
    $('#tbody .editReset').click(function(){
        $('#myModalReset').modal('show');
        resetId = $(this).parent().parent().attr('id');
    });

    $('#resetSure').click(function () {
        $.ajax({
            type: 'POST',
            url: '../user/resetUserPass',
            dataType: 'json',
            contentType: 'application/json; charset=utf-8',
            headers: {
                'Accept': 'application/json; charset=utf-8',
                'Authorization': 'Basic ' + sessionStorage.getItem('token')
            },
            data: JSON.stringify({id: resetId}),
            success: function (res) {
                $('#myModal').modal('hide');
                if(res.stateCode != 100){
                    alert(res.message);
                }
                window.location.reload();
            },
            error: function (err) {
                $('#myModal').modal('hide');
                alert(err.message);
            }
        });
    })


    $('#tbody .upload').click(function(){
        $('#myModalUpload').modal('show');
        uploadId = $(this).parent().parent().attr('id');
    });


    }
var imgs=[]; //图片
//本地上传图片
document.getElementById("upLoad").addEventListener("change",function(e){
    var log = $('#img .img_container > div').length;
    if(log > 1){
        alert('最多上传1张图片');
    }else{
        var files = document.getElementById("upLoad").files;
        if(log + files.length > 1){
            alert('最多上传1张图片');
        }else{
            for(var i= 0; i < files.length; i++){
                var img = new Image();
                var reader =new FileReader();
                reader.readAsDataURL(files[i]);
                reader.onload =function(e){
                    var dx =(e.total/1024)/1024;
                    if(dx>=2){
                        alert("文件大小大于2M");
                        return;
                    }
                    img.src =this.result;
                    img.style.width = '138px';
                    img.style.height ="93px";
                    var image = '<div class="img_container1"><img src="'+this.result+'" class="goods-img" alt=""><span class="delete_img glyphicon glyphicon-remove-circle" onclick="deleteImg($(this))"></span></div>';
                    $.ajax({
                        type: 'POST',
                        url: '../uploadFile/saveUploadFile',
                        dataType: 'json',
                        contentType: 'application/json; charset=utf-8',
                        headers: {
                            'Accept': 'application/json; charset=utf-8',
                            'Authorization': 'Basic ' + sessionStorage.getItem('token')
                        },
                        data: JSON.stringify({base64: this.result}),
                        success: function (res) {
                            if (res.stateCode == 100) {
                                $('#img .img_container').append(image);
                                imgs.push({href: res.data.key});
                                $("input[type='file']").val('');
                            } else {
                                alert(res.message);
                            }
                        },
                        error: function (err) {
                            alert(err.message)
                        }
                    })
                }
            }
        }
    }
});

function deleteImg(i) {
    $.ajax({
        type: 'POST',
        url: '../uploadFile/removeUploadFile',
        dataType: 'json',
        contentType: 'application/json; charset=utf-8',
        headers: {
            'Accept': 'application/json; charset=utf-8',
            'Authorization': 'Basic ' + sessionStorage.getItem('token')
        },
        data: JSON.stringify({id: imgs[i.parent().index()].href}),
        success: function (res) {
            if (res.stateCode == 100) {

            } else {
                // alert(res.message);
            }
        },
        error: function (err) {
            // alert(err.message)
        }
    })
    imgs.splice(i.parent().index(), 1);
    i.parent().remove();
}

//提交编辑信息
var sss;
$('#uploadSure').click(function () {
       if(imgs.length==0){
            layer.alert('请上传图片！', {icon: 0});
        }else {
            sss=true;
        }

        var data = {
           id:uploadId,
            picture:imgs
        };

    if(sss){
        $('.spinner-wrap').show();
        $.ajax({
            type: 'POST',
            url:  '../user/uploadIcon',
            dataType: 'json',
            contentType: 'application/json; charset=utf-8',
            headers: {
                'Accept': 'application/json; charset=utf-8',
                'Authorization': 'Basic ' + sessionStorage.getItem('token')
            },
            data: JSON.stringify(data),
            success: function (res) {

                if (res.stateCode == 100) {
                    window.location.reload();
                } else {
                    alert(res.message);
                }
            },
            error: function (err) {
                alert(err.message)
            }
        })
    }
})

