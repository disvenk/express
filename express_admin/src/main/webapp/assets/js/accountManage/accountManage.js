


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
});


// layui方法
layui.use(['table', 'layer','form'], function () {
    var table = layui.table
        , layer = layui.layer //弹层
        , form = layui.form
    // 表格渲染
    table.render({
        elem: '#dateTable'                  //指定原始表格元素选择器（推荐id选择器）
        , url: '../send/accountManagePageList'
        , height: 471
        , cellMinWidth: 80
        , request: {
            pageName: 'pageNum' //页码的参数名称，默认：page
            , limitName: 'pageSize' //每页数据量的参数名，默认：limit
        }
        , cols: [[
              {field: 'time', sort: true, title: '时间',  align: 'center'}
            , {field: 'deliveryName', sort: true, title: '快递公司', align: 'center'}
            , {field: 'count', sort: true, title: '寄件数量', align: 'center'}
            , {field: 'cost', sort: true, title: '成本价', align: 'center'}
            , {field: 'costTotal', sort: true, title: '总价', align: 'center'}
            , {field: 'price', sort: true, title: '寄件价', align: 'center'}
            , {field: 'priceTotal', sort: true, title: '总价', align: 'center'}
            , {field: 'schoolName', sort: true, title: '所属网点', align: 'center'}
        ]]
        , page: true
        , response: {
            countName: 'total' //数据总数的字段名称，默认：count
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
        var schoolId = $('#schoolId').find("option:selected").attr("id");
        var table = layui.table
            , layer = layui.layer //弹层
            , form = layui.form
        // 表格渲染
        table.render({
            elem: '#dateTable'                  //指定原始表格元素选择器（推荐id选择器）
            , url: '../send/accountManagePageList'
            , height: 471
            , cellMinWidth: 80
            , where:{schoolId:schoolId}
            , request: {
                pageName: 'pageNum' //页码的参数名称，默认：page
                , limitName: 'pageSize' //每页数据量的参数名，默认：limit
            }
            , cols: [[
                {field: 'time', sort: true, title: '时间',  align: 'center'}
                , {field: 'deliveryName', sort: true, title: '快递公司', align: 'center'}
                , {field: 'count', sort: true, title: '寄件数量', align: 'center'}
                , {field: 'cost', sort: true, title: '成本价', align: 'center'}
                , {field: 'costTotal', sort: true, title: '总价', align: 'center'}
                , {field: 'price', sort: true, title: '寄件价', align: 'center'}
                , {field: 'priceTotal', sort: true, title: '总价', align: 'center'}
                , {field: 'schoolName', sort: true, title: '所属网点', align: 'center'}
            ]]
            , page: true
            , response: {
                countName: 'total' //数据总数的字段名称，默认：count
            }
        });
        return false;
    });
});
