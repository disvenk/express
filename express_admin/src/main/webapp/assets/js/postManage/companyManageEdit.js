
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

    //获取所有快递公司信息
    $.ajax({
        type: 'POST',
        url:   '../express/list',
        dataType: 'json',
        contentType: 'application/json; charset=utf-8',
        headers: {
            'Accept': 'application/json; charset=utf-8',
            'Authorization': 'Basic ' + sessionStorage.getItem('token')
        },
        success: function (res) {
            var deliveryId = '<option value=""></option>';
            for (var i = 0; i < res.data.length; i++) {
                deliveryId += '<option id="' + res.data[i].id + '" value="' + res.data[i].name + '">' + res.data[i].name + '</option>';
            }
            $('#deliveryId').append(deliveryId);
            form.render();
        },
        error: function (err) {
            layer.alert(res.message, {icon: 0});
        }
    });


//列表
setTimeout(function () {
    $.ajax({
        type: 'POST',
        url:  '../send/deliveryCompanyEdit',
        dataType: 'json',
        contentType: 'application/json; charset=utf-8',
        headers: {
            'Accept': 'application/json; charset=utf-8',
            'Authorization': 'Basic ' + sessionStorage.getItem('token')
        },
        data: JSON.stringify({id: id}),
        success: function (res) {
            if(res.stateCode == 100){
                for(var a = 0;a<$('#schoolId').find('option').length;a++){
                    if($('#schoolId').find('option').eq(a).attr('id') == res.data.schoolId){
                        $('#schoolId').find('option').eq(a).attr('selected',true);
                        form.render();
                    }
                }
                for(var i = 0;i<$('#deliveryId').find('option').length;i++){
                    if($('#deliveryId').find('option').eq(i).attr('id') == res.data.deliveryId){
                        $('#deliveryId').find('option').eq(i).attr('selected',true);
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
},400)
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
        var deliveryId = $('#deliveryId').find('option:selected').attr('id');

        if(deliveryId == undefined || deliveryId == null){
            layer.alert('请选择快递公司',{icon: 0});
            return false;
        }
        if(schoolId == undefined || schoolId == null){
            layer.alert('请选择网点',{icon: 0});
            return false;
        }
        var data = {
            id: id,
            schoolId:schoolId,
            deliveryId:deliveryId,
        };
        $.ajax({
            type: 'POST',
            url: '../send/updateDeliveryCompany',
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
                        location.href = '../home/companyManageHtml';
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

