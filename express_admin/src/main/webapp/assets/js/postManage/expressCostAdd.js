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

    //获取所有快递公司信息
    $.ajax({
        type: 'POST',
        url:   '../send/list',
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

});
// layui方法
layui.use(['table', 'layer','form'], function () {
    var table = layui.table
        , layer = layui.layer //弹层
        , form = layui.form

    form.on('select(school)', function (data) {
        var schoolId = $('#schoolId').find('option:selected').attr('id');
        var deliveryId = $('#deliveryId').find('option:selected').attr('id');
        if(schoolId == "" || schoolId == null){
            layer.alert('请选择网点',{icon: 0});
            return false;
        }
        if(deliveryId == "" || deliveryId == null){
            layer.alert('请选择快递公司',{icon: 0});
            return false;
        }
        var data = {
            schoolId:schoolId,
            deliveryId:deliveryId,
        };
        $.ajax({
            type: 'POST',
            url: '../send/addProvinceServiceFeeSubmit',
            dataType: 'json',
            contentType: 'application/json; charset=utf-8',
            headers: {
                'Accept': 'application/json; charset=utf-8',
                'Authorization': 'Basic ' + sessionStorage.getItem('token')
            },
            data: JSON.stringify(data),
            success: function (res) {
                if(res.stateCode != 100){
                    layer.alert(res.message, {icon: 0})
                    setTimeout(function () {
                        window.location.reload();
                    },2000)
                }
            },error: function (err) {
                layer.alert(err.message, {icon: 0})
            }
        })
        return false;
    });


    // 表格渲染
    table.render({
        elem: '#dateTable'                  //指定原始表格元素选择器（推荐id选择器）
        , url: '../send/addProvinceServiceFeePageList'
        , height: 471
        , cellMinWidth: 30
        , request: {
            pageName: 'pageNum' //页码的参数名称，默认：page
            , limitName: 'pageSize' //每页数据量的参数名，默认：limit
        }
        , cols: [[
              {field: 'name', title: '省份', align: 'center'}
            , {field: 'firstWeightFee',  title: '寄件首重价',edit: 'text', align: 'center'}
            , {field: 'otherWeightFee',  title: '寄件续重价',edit: 'text', align: 'center'}
            , {field: 'firstCostFee',  title: '成本首重价',edit: 'text', align: 'center'}
            , {field: 'otherCostFee',  title: '成本续重价',edit: 'text', align: 'center'}
        ]]
        , page: true
        , response: {
            countName: 'total' //数据总数的字段名称，默认：count
        }
    });

    //表格数据编辑保存
    table.on('edit(dateTable)', function(obj){
        if(isNaN(obj.value) || obj.value<0 ){
            var message = '您输入的价格有误';
            layer.alert(message, {icon: 0});
            return;
        }
        var value = obj.value //得到修改后的值
            ,data = obj.data //得到所在行所有键值
            ,field = obj.field; //得到字段
        var schoolId = $('#schoolId').find('option:selected').attr('id');
        var deliveryId = $('#deliveryId').find('option:selected').attr('id');
        var data = {
            schoolId:schoolId,
            deliveryId:deliveryId,
            field:field,
            value:value,
            id:data.id,
            provinceName:data.name,
            schoolDeliveryCompanyId:data.schoolDeliveryCompanyId
        };
        $.ajax({
            type: 'POST',
            url: '../send/addProvinceServiceFee',
            dataType: 'json',
            contentType: 'application/json; charset=utf-8',
            headers: {
                'Accept': 'application/json; charset=utf-8',
                'Authorization': 'Basic ' + sessionStorage.getItem('token')
            },
            data: JSON.stringify(data),
            success: function (res) {
                if(res.stateCode != 100){
                    layer.alert(res.message, {icon: 0})
                    setTimeout(function () {
                        window.location.reload();
                    },3000)
                }else{
                }
            },error: function (err) {
                layer.alert(err.message, {icon: 0})
            }
        })
    });
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
        var data = {
            schoolId:schoolId,
            deliveryId:deliveryId
        };
        $.ajax({
            type: 'POST',
            url: '../send/addProvinceServiceFeeSubmit',
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
                        location.href = '../home/expressCostManageHtml';
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
