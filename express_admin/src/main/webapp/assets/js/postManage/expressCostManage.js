$('#expressCostAdd').click(function () {
    location.href='expressCostAddHtml';
})

layui.use(['form','laydate','layer'], function () {
    var form = layui.form;
    var laydate = layui.laydate;
    var layer = layui.layer;
    laydate.render({
        elem: '#timeRange'
        ,type: 'time'
        ,range: true
    });

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
            layer.alert(err.message, {icon: 0});
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

    // 表格渲染
    table.render({
        elem: '#dateTable'                  //指定原始表格元素选择器（推荐id选择器）
        , url: '../send/provinceServiceFeePageList'
        , height: 471
        , cellMinWidth: 80
        , request: {
            pageName: 'pageNum' //页码的参数名称，默认：page
            , limitName: 'pageSize' //每页数据量的参数名，默认：limit
        }
        , cols: [[
              {field: 'deliveryName', sort: true, title: '快递公司', align: 'center'}
            , {field: 'schoolName', sort: true, title: '所属网点', align: 'center'}
            , {fixed: 'right',title: '操作', align: 'center', toolbar: '#barDemo', width: 140}
        ]]
        , page: true
        , response: {
            countName: 'total' //数据总数的字段名称，默认：count
        }
    });

    //监听工具条
    table.on('tool(dateTable)', function (obj) { //注：tool是工具条事件名，test是table原始容器的属性 lay-filter="对应的值"
        var data = obj.data //获得当前行数据
            , layEvent = obj.event; //获得 lay-event 对应的值
        if (layEvent === 'detail') {
            layer.msg('重置第' + data.id + '行的密码');
        } else if (layEvent === 'del') {
            layer.confirm('删除后数据将无法恢复，确认？', function (index) {
                $.ajax({
                    type: 'POST',
                    url: '../send/deliveryProvinceServiceFeeDelete',
                    dataType: 'json',
                    contentType: 'application/json; charset=utf-8',
                    headers: {
                        'Accept': 'application/json; charset=utf-8',
                        'Authorization': 'Basic ' + sessionStorage.getItem('token')
                    },
                    data: JSON.stringify({id: data.id}),
                    success: function (res) {
                        layer.close(index);
                        window.location.reload();
                    },
                    error: function (err) {
                        layer.alert(res.message, {icon: 0});
                    }
                });
            });
        } else if (layEvent === 'edit') {
//                layer.msg('编辑第'+data.id+'行');
            location.href = 'expressCostEditHtml?id='+data.id+'';
        }
    });
});

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
        var schoolId = $('#schoolId').find("option:selected").attr('id');
        var deliveryId = $('#deliveryId').find("option:selected").attr('id');
        var table = layui.table
            , layer = layui.layer //弹层
            , form = layui.form
        // 表格渲染
        table.render({
            elem: '#dateTable'                  //指定原始表格元素选择器（推荐id选择器）
            , url: '../send/provinceServiceFeePageList'
            , height: 471
            , cellMinWidth: 80
            , where:{schoolId:schoolId,deliveryId:deliveryId}
            , request: {
                pageName: 'pageNum' //页码的参数名称，默认：page
                , limitName: 'pageSize' //每页数据量的参数名，默认：limit
            }
            , cols: [[
                {field: 'deliveryName', sort: true, title: '快递公司', align: 'center'}
                , {field: 'schoolName', sort: true, title: '所属网点', align: 'center'}
                , {fixed: 'right',title: '操作', align: 'center', toolbar: '#barDemo', width: 140}
            ]]
            , page: true
            , response: {
                countName: 'total' //数据总数的字段名称，默认：count
            }
        });
        return false;
    });
});