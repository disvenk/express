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
        , url: '../receive/arriveMessagePageList'
        , height: 471
        , cellMinWidth: 80
        , request: {
            pageName: 'pageNum' //页码的参数名称，默认：page
            , limitName: 'pageSize' //每页数据量的参数名，默认：limit
        }
        , cols: [[
              {field: 'deliveryNo', sort: true, title: '快递单号', align: 'center'}
            , {field: 'deliveryName', sort: true, title: '快递公司', align: 'center'}
            , {field: 'reachDate', sort: true, title: '到达时间',width: 177, align: 'center'}
            , {field: 'validateCode', title: '验证码', align: 'center'}
            , {field: 'expressDate', sort: true, title: '签收时间',width: 177, align: 'center'}
            , {field: 'schoolName', sort: true, title: '所属网点', align: 'center'}
            , {field: 'receiverTel', sort: true, title: '收件人电话', align: 'center'}
            , {field: 'isThisUser', sort: true, title: '本地是否存在用户', align: 'center'}
            , {field: 'remark', sort: true, title: '备注', align: 'center'}
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

    //监听导出
    form.on('submit(formExport)', function(data) {
        var receiverTel = $('#receiverTel').val();
        var deliveryNo = $('#deliveryNo').val();
        var schoolId = $('#schoolId').find("option:selected").attr('id');

        if(receiverTel == undefined){
            receiverTel = '';
        }
        if(deliveryNo == undefined){
            deliveryNo = '';
        }
        if(schoolId == undefined){
            schoolId = '';
        }
        setTimeout(function () {
            var url = "../receive/arriveMessageExportExcelPageList?receiverTel=" + receiverTel +
                "&schoolId=" + schoolId + "&deliveryNo=" + deliveryNo ;
            // window.open(url);
            window.location.href = (url)
        },200);
        return false;
    });

    //监听提交
    form.on('submit(formDemo)', function(data){
        var receiverTel = $('#receiverTel').val();
        var deliveryNo = $('#deliveryNo').val();
        var schoolId = $('#schoolId').find("option:selected").attr('id');
        var table = layui.table
            , layer = layui.layer //弹层
            , form = layui.form
        // 表格渲染
        table.render({
            elem: '#dateTable'                  //指定原始表格元素选择器（推荐id选择器）
            , url: '../receive/arriveMessagePageList'
            , height: 471
            , cellMinWidth: 80
            , where:{schoolId:schoolId,receiverTel:receiverTel,deliveryNo:deliveryNo}
            , request: {
                pageName: 'pageNum' //页码的参数名称，默认：page
                , limitName: 'pageSize' //每页数据量的参数名，默认：limit
            }
            , cols: [[
                {field: 'deliveryNo', sort: true, title: '快递单号', align: 'center'}
                , {field: 'deliveryName', sort: true, title: '快递公司', align: 'center'}
                , {field: 'reachDate', sort: true, title: '到达时间',width: 177, align: 'center'}
                , {field: 'validateCode', title: '验证码', align: 'center'}
                , {field: 'expressDate', sort: true, title: '签收时间',width: 177, align: 'center'}
                , {field: 'schoolName', sort: true, title: '所属网点', align: 'center'}
                , {field: 'receiverTel', sort: true, title: '收件人电话', align: 'center'}
                , {field: 'isThisUser', sort: true, title: '本地是否存在用户', align: 'center'}
                , {field: 'remark', sort: true, title: '备注', align: 'center'}
            ]]
            , page: true
            , response: {
                countName: 'total' //数据总数的字段名称，默认：count
            }
        });
        return false;
    });
});

