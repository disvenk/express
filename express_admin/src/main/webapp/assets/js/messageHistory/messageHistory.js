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
        , url: '../receive/expSmsMessagePageList'
        , height: 471
        , cellMinWidth: 80
        , request: {
            pageName: 'pageNum' //页码的参数名称，默认：page
            , limitName: 'pageSize' //每页数据量的参数名，默认：limit
        }
        , cols: [[
            {field: 'userMobile', sort: true, title: '用户手机号',  align: 'center'}
            , {field: 'sendTime', sort: true, title: '推送时间',width: 177, align: 'center'}
            , {field: 'validateCode', title: '验证码', align: 'center'}
            , {field: 'sendContent', title: '推送内容', align: 'center'}
            , {field: 'schoolName', title: '所属网点', align: 'center'}
            // , {field: 'sendResult', sort: true, title: '推送结果', align: 'center'}
            // , {fixed: 'right',title: '操作', align: 'center', toolbar: '#barDemo', width: 100}
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
        if (layEvent === 'push') {
            layer.msg('推送第' + data.id + '行的数据');
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
        var userCode = $('#userCode').val();
        var userMobile = $('#userMobile').val();

        var table = layui.table
            , layer = layui.layer //弹层
            , form = layui.form
        // 表格渲染
        table.render({
            elem: '#dateTable'                  //指定原始表格元素选择器（推荐id选择器）
            , url: '../receive/expSmsMessagePageList'
            , height: 471
            , cellMinWidth: 80
            , where:{schoolId:schoolId,userMobile:userMobile,userCode:userCode}
            , request: {
                pageName: 'pageNum' //页码的参数名称，默认：page
                , limitName: 'pageSize' //每页数据量的参数名，默认：limit
            }
            , cols: [[
                {field: 'userMobile', sort: true, title: '用户手机号',  align: 'center'}
                , {field: 'sendTime', sort: true, title: '推送时间',width: 177, align: 'center'}
                , {field: 'validateCode', title: '验证码', align: 'center'}
                , {field: 'sendContent', title: '推送内容', align: 'center'}
                , {field: 'schoolName', title: '所属网点', align: 'center'}
                // , {field: 'sendResult', sort: true, title: '推送结果', align: 'center'}
                // , {fixed: 'right',title: '操作', align: 'center', toolbar: '#barDemo', width: 100}
            ]]
            , page: true
            , response: {
                countName: 'total' //数据总数的字段名称，默认：count
            }
        });
        return false;
    });
});
