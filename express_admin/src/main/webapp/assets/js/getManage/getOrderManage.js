
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
    //获取所有上门人员
    $.ajax({
        type: 'POST',
        url:   '../receive/getAllTodoorPerson',
        dataType: 'json',
        contentType: 'application/json; charset=utf-8',
        headers: {
            'Accept': 'application/json; charset=utf-8',
            'Authorization': 'Basic ' + sessionStorage.getItem('token')
        },
        data: JSON.stringify(),
        success: function (res) {
            var delivererId = '<option value=""></option>';
            for (var i = 0; i < res.data.length; i++) {
                delivererId += '<option id="' + res.data[i].id + '" value="' + res.data[i].name + '">' + res.data[i].name + '</option>';
            }
            $('#delivererId').append(delivererId);
            form.render();
        },
        error: function (err) {
            layer.alert(err.message, {icon: 0});
        }
    });
});


// layui方法
layui.use(['table', 'layer','form','laydate'], function () {
    var table = layui.table
        , layer = layui.layer //弹层
        , form = layui.form
        ,laydate=layui.laydate

    // 表格渲染
    table.render({
        elem: '#dateTable'                  //指定原始表格元素选择器（推荐id选择器）
        , url: '../receive/expReceiveOrderPageList'
        , height: 'full-300'
        , cellMinWidth: 100
        , request: {
            pageName: 'pageNum' //页码的参数名称，默认：page
            , limitName: 'pageSize' //每页数据量的参数名，默认：limit
        }
        , cols: [[
            {field: 'orderNo',sort:true, title: '订单号', align: 'center',width:'150'}
            , {field: 'deliveryNo',sort:true, title: '快递单号', align: 'center',width:'135'}
            , {field: 'deliveryName', title: '快递公司', align: 'center',width:'100'}
            , {field: 'userCode', title: '用户名', align: 'center',width:'118'}
            , {field: 'userMobile', title: '手机号', align: 'center',width:'118'}
            , {field: 'orderPrice', title: '费用', align: 'center'}
            , {field: 'todoorTipFee', title: '上门小费', align: 'center'}
            , {field: 'payStatusName', title: '收费状态', align: 'center'}
            , {field: 'payType', title: '支付方式', align: 'center'}
            , {field: 'payTime',sort:true, title: '支付时间',width: 177, align: 'center'}
            , {field: 'typeName', title: '派件方式', align: 'center'}
            , {field: 'orderStatusName', title: '订单状态', align: 'center'}
            , {field: 'orderTime',sort:true, title: '下单时间', align: 'center'}
            , {field: 'reachDate',sort:true, title: '到达时间', align: 'center'}
            , {field: 'validateCode', title: '验证码', align: 'center'}
            , {field: 'expressDate',sort:true, title: '签收时间', align: 'center'}
            , {field: 'schoolName', title: '所属网点', align: 'center'}
            , {field: 'receiverName', title: '收件人名称', align: 'center'}
            , {field: 'receiverTel', title: '收件人电话', align: 'center'}
            , {field: 'receiverAddress', title: '收件人地址', align: 'center'}
            , {field: 'todoorDate',sort:true, title: '上门时间', align: 'center'}
            , {field: 'delivererName', title: '上门人员', align: 'center'}
            , {field: 'remark', title: '备注', align: 'center'}
            , {field: 'goodsTypeName', title: '物品类型', align: 'center'}
            , {field: 'goodsDescription', title: '物品说明', align: 'center'}
            , {fixed: 'right',title: '操作', align: 'center', toolbar: '#barDemo', width: 260}
        ]],
        done: function(res) {
            $.ajax({
                type: 'POST',
                url: '../receive/expReceiveOrderCount',
                dataType: 'json',
                contentType: 'application/json; charset=utf-8',
                headers: {
                    'Accept': 'application/json; charset=utf-8',
                    'Authorization': 'Basic ' + sessionStorage.getItem('token')
                },
                success: function (res) {
                    $('#totalQty').html((res.data[0].totalQty));
                    $('#todoorQty').html((res.data[0].todoorQty));
                    $('#todoorFeeTotal').html((res.data[0].todoorFeeTotal));
                    $('#receiveQty').html((res.data[0].receiveQty));
                },
                error: function (err) {
                    layer.alert(err.message, {icon: 0});
                }
            });
        }
        , page: true
        , response: {
            countName: 'total' //数据总数的字段名称，默认：count
        },

    });




//日期范围
    laydate.render({
        elem: '#getDateRange'
        ,range: true
    });
    laydate.render({
        elem: '#receiveDateRange'
        ,range: true
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
                    url: '../receive/expReceiveOrderDelete',
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
                        layer.alert(err.message, {icon: 0});
                    }
                });
            });
        } else if (layEvent === 'edit') {
            location.href = 'getOrderManageEditHtml?id='+data.id+'';
        } else if (layEvent=='smry'){
            $.post('../receive/getAllTodoorPerson', function(res){
                layer.open({
                    title: '上门人员'
                        ,area: ['300px', '220px']
                        ,content: '<div>\n' + '<span>人员维护：</span><select id="delivererId1" class="layer-select1"></select>' +'</div>'
                         ,btn: ['确定']
                        ,yes: function(index, layero){
                        var delivererId1 = ($('#delivererId1').find('option:checked').attr('id'));
                        $.ajax({
                            type: 'POST',
                            url: '../receive/updateTodoorManageChange',
                            dataType: 'json',
                            contentType: 'application/json; charset=utf-8',
                            headers: {
                                'Accept': 'application/json; charset=utf-8',
                                'Authorization': 'Basic ' + sessionStorage.getItem('token')
                            },
                            data: JSON.stringify({id: data.id,delivererId:delivererId1}),
                            success: function (res) {
                                layer.close(index);
                                window.location.reload();
                            },
                            error: function (err) {
                                layer.alert(err.message, {icon: 0});
                            }
                        });
                }
                });
                var delivererId1 = '<option value=""></option>';
                for (var i = 0; i < res.data.length; i++) {
                    delivererId1 += '<option id="' + res.data[i].id + '" value="' + res.data[i].name + '">' + res.data[i].name + '</option>';
                }
                $('#delivererId1').append(delivererId1);
                for (var a = 0; a < $('#delivererId1').find('option').length; a++) {
                    if ($('#delivererId1').find('option').eq(a).attr('id') == data.delivererId) {
                        $('#delivererId1').find('option').eq(a).attr('selected', true);
                        form.render();
                    }
                }
            });
        }else if (layEvent === 'mdzp') {
            layer.open({
                title: '面单照片'
                ,area: ['240px', '220px']
                ,btn: []
                ,content: '<div>\n' +'<img style="width:197px ;height: 125px" id="capture" class="user-img" >' +'</div>'
            })
            // $('#capture').attr('src',"../assets/img/img1.jpg");
            $('#capture').attr('src',data.capture);
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

    form.on('submit(formExport)', function(data) {
        var startTime = $('#getDateRange').val().split(' ')[0] ? $('#getDateRange').val().split(' ')[0] : '';
        var endTime = $('#getDateRange').val().split(' ')[2] ? $('#getDateRange').val().split(' ')[2] : '';
        var expressStartTime = $('#receiveDateRange').val().split(' ')[0] ? $('#receiveDateRange').val().split(' ')[0] : '';
        var expressEndTime = $('#receiveDateRange').val().split(' ')[2] ? $('#receiveDateRange').val().split(' ')[2] : '';
        var delivererId = $('#delivererId').find('option:selected').attr('id');
        var schoolId = $('#schoolId').find("option:selected").attr('id');
        var orderStatus = $('#orderStatus').find("option:selected").attr('id');
        var typeName = $('#typeName').find("option:selected").attr('value');
        if (typeName == '自行取件') {
            typeName = '0'
        } else if (typeName == '派件上门') {
            typeName = '1'
        } else {
            typeName = ''
        }
        var userCode = $('#userCode').val();
        var userMobile = $('#userMobile').val();
        var orderNo = $('#orderNo').val();
        var deliveryNo = $('#deliveryNo').val();
        var isDelete = $('#isDelete').find('option:selected').attr('value');
        if (isDelete == '已删除') {
            isDelete = 'true';
        } else if (isDelete == '未删除') {
            isDelete = 'false';
        } else {
            isDelete = '';
        }
       if(delivererId == undefined){
           delivererId = '';
       }
       if(schoolId == undefined){
           schoolId = '';
       }
       if(orderStatus == undefined){
           orderStatus = '';
       }

        setTimeout(function () {
            var url = "../receive/exportExcelPageList?startTime=" + startTime +
                "&endTime=" + endTime +
                "&expressStartTime=" + expressStartTime +
                "&expressEndTime=" + expressEndTime +
                "&delivererId=" + delivererId +
                "&schoolId=" + schoolId +
                "&orderStatus=" + orderStatus +
                "&userCode=" + userCode +
                "&userMobile=" + userMobile +
                "&isDelete=" + isDelete +
                "&deliveryNo=" + deliveryNo +
                "&orderNo=" + orderNo +
                "&typeName=" + typeName;
            // window.open(url);
            window.location.href = (url)
        },200);
        return false;
    });

    //监听提交
    form.on('submit(formDemo)', function(data){
        var startTime = $('#getDateRange').val().split(' ')[0]?$('#getDateRange').val().split(' ')[0]: '';
        var endTime=  $('#getDateRange').val().split(' ')[2]?$('#getDateRange').val().split(' ')[2]: '';
        var expressStartTime = $('#receiveDateRange').val().split(' ')[0]?$('#receiveDateRange').val().split(' ')[0]: '';
        var expressEndTime=  $('#receiveDateRange').val().split(' ')[2]?$('#receiveDateRange').val().split(' ')[2]: '';
        var delivererId = $('#delivererId').find('option:selected').attr('id');
        var schoolId = $('#schoolId').find("option:selected").attr('id');
        var orderStatus = $('#orderStatus').find("option:selected").attr('id');
        var typeName = $('#typeName').find("option:selected").attr('value');
        if(typeName == '自行取件'){
            typeName = '0'
        }else if(typeName == '派件上门'){
            typeName = '1'
        }else{
            typeName = ''
        }
        var userCode = $('#userCode').val();
        var userMobile = $('#userMobile').val();
        var orderNo = $('#orderNo').val();
        var deliveryNo = $('#deliveryNo').val();
        var isDelete = $('#isDelete').find('option:selected').attr('value');
        if(isDelete == '已删除'){
            isDelete = 'true';
        }else if(isDelete == '未删除'){
            isDelete = 'false';
        }else{
            isDelete = '';
        }

        var table = layui.table
            , layer = layui.layer //弹层
            , form = layui.form
        // 表格渲染
        table.render({
            elem: '#dateTable'                  //指定原始表格元素选择器（推荐id选择器）
            , url: '../receive/expReceiveOrderPageList'
            , height: 471
            , cellMinWidth: 100
            , where:{schoolId:schoolId,orderStatus:orderStatus,type:typeName,userCode:userCode,delivererId:delivererId,userMobile:userMobile,orderStartTime:startTime,
                orderEndTime:endTime,expressStartTime:expressStartTime,expressEndTime:expressEndTime,isDelete:isDelete,orderNo:orderNo,deliveryNo:deliveryNo}
            , request: {
                pageName: 'pageNum' //页码的参数名称，默认：page
                , limitName: 'pageSize' //每页数据量的参数名，默认：limit
            }
            , cols: [[
                {field: 'orderNo',sort:true, title: '订单号', align: 'center',width:'150'}
                , {field: 'deliveryNo',sort:true, title: '快递单号', align: 'center',width:'135'}
                , {field: 'deliveryName', title: '快递公司', align: 'center',width:'100'}
                , {field: 'userCode', title: '用户名', align: 'center',width:'118'}
                , {field: 'userMobile', title: '手机号', align: 'center',width:'118'}
                , {field: 'orderPrice', title: '费用', align: 'center'}
                , {field: 'todoorTipFee', title: '上门小费', align: 'center'}
                , {field: 'payStatusName', title: '收费状态', align: 'center'}
                , {field: 'payType', title: '支付方式', align: 'center'}
                , {field: 'payTime',sort:true, title: '支付时间',width: 177, align: 'center'}
                , {field: 'typeName', title: '派件方式', align: 'center'}
                , {field: 'orderStatusName', title: '订单状态', align: 'center'}
                , {field: 'orderTime',sort:true, title: '下单时间', align: 'center'}
                , {field: 'reachDate',sort:true, title: '到达时间', align: 'center'}
                , {field: 'validateCode', title: '验证码', align: 'center'}
                , {field: 'expressDate',sort:true, title: '签收时间', align: 'center'}
                , {field: 'schoolName', title: '所属网点', align: 'center'}
                , {field: 'receiverName', title: '收件人名称', align: 'center'}
                , {field: 'receiverTel', title: '收件人电话', align: 'center'}
                , {field: 'receiverAddress', title: '收件人地址', align: 'center'}
                , {field: 'todoorDate',sort:true, title: '上门时间', align: 'center'}
                , {field: 'delivererName', title: '上门人员', align: 'center'}
                , {field: 'remark', title: '备注', align: 'center'}
                , {field: 'goodsTypeName', title: '物品类型', align: 'center'}
                , {field: 'goodsDescription', title: '物品说明', align: 'center'}
                , {fixed: 'right',title: '操作', align: 'center', toolbar: '#barDemo', width: 260}
            ]]
            , page: true
            , response: {
                countName: 'total' //数据总数的字段名称，默认：count
            }
        });
        return false;
    });
});



