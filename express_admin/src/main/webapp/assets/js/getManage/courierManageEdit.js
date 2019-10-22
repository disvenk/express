//
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

//获取所有网点信息

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

    //获取所有网点信息
    $.ajax({
        type: 'POST',
        url:   '../school/getAllSchoolList',
        dataType: 'json',
        contentType: 'application/json; charset=utf-8',
        headers: {
            'Accept': 'application/json; charset=utf-8',
            'Authorization': 'Basic ' + sessionStorage.getItem('token')
        },
        success: function (res) {
            var schoolId = '<option value=""></option>';
            for (var i = 0; i < res.data.length; i++) {
                schoolId += '<option id="' + res.data[i].id + '" value="' + res.data[i].name + '">' + res.data[i].name + '</option>';
            }
            $('#schoolId').append(schoolId);
            form.render();
        },
        error: function (err) {
            layer.alert(res.message, {icon: 0});
        }
    });

    setTimeout(function () {
        $.ajax({
            type: 'POST',
            url:  '../receive/todoorManageEdit',
            dataType: 'json',
            contentType: 'application/json; charset=utf-8',
            headers: {
                'Accept': 'application/json; charset=utf-8',
                'Authorization': 'Basic ' + sessionStorage.getItem('token')
            },
            data: JSON.stringify({id: id}),
            success: function (res) {
                if(res.stateCode == 100){
                    $('#userCode').val(res.data.userCode);
                    $('#userMobile').val(res.data.userMobile);
                    $('#remark').val(res.data.remark);
                    for(var a = 1;a<$('#schoolId').find('option').length;a++){
                        if($('#schoolId').find('option').eq(a).attr('id') == res.data.schoolId){
                            $('#schoolId').find('option').eq(a).attr('selected',true);
                            form.render();
                        }
                    }
                }else{
                    layer.alert(res.message, {icon: 0})
                }
            },
            error: function (err) {
                layer.alert(res.message, {icon: 0});
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
        var schoolId = $('#schoolId').find('option:selected').attr('id');
        var data = {
            id: id,
            schoolId:schoolId,
            userCode: $('#userCode').val(),
            userMobile: $('#userMobile').val(),
            remark:$('#remark').val()
        };
        $.ajax({
            type: 'POST',
            url: '../receive/updateTodoorManage',
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
                        location.href = '../home/courierManageHtml';
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

