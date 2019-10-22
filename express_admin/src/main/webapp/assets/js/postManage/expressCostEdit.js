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
    //
    // setTimeout(function () {
    //     $.ajax({
    //         type: 'POST',
    //         url:  '../send/provinceServiceFeeEdit',
    //         dataType: 'json',
    //         contentType: 'application/json; charset=utf-8',
    //         headers: {
    //             'Accept': 'application/json; charset=utf-8',
    //             'Authorization': 'Basic ' + sessionStorage.getItem('token')
    //         },
    //         data: JSON.stringify({id: id}),
    //         success: function (res) {
    //             if(res.stateCode == 100){
    //                 for(var a = 1;a<$('#schoolId').find('option').length;a++){
    //                     if($('#schoolId').find('option').eq(a).attr('id') == res.data[0].schoolId){
    //                         $('#schoolId').find('option').eq(a).attr('selected',true);
    //                         form.render();
    //                     }
    //                 }
    //                 for(var i = 0;i<$('#deliveryId').find('option').length;i++){
    //                     if($('#deliveryId').find('option').eq(i).attr('id') == res.data[0].deliveryId){
    //                         $('#deliveryId').find('option').eq(i).attr('selected',true);
    //                         form.render();
    //                     }
    //                 }
    //                 for(var i = 0;i<res.data.length;i++){
    //                     var tr1 = '';
    //                     tr1 +='<tr id="'+res.data[i].expProviceServiceId+'"><td>'+res.data[i].provinceName+'</td>' +
    //                         '<td>'+res.data[i].firstWeightFee+'</td>' +
    //                         '<td>'+res.data[i].otherWeightFee+'</td>' +
    //                         '<td>'+res.data[i].firstCostFee+'</td>' +
    //                         '<td>'+res.data[i].otherCostFee+'</td>' +
    //                         '</tr>'
    //                     $('#tbody').append(tr1);
    //                     form.render();
    //                 }
    //
    //             }else{
    //                 layer.alert(res.message, {icon: 0})
    //             }
    //         },
    //         error: function (err) {
    //             layer.alert(err.message, {icon: 0});
    //         }
    //     })
    // },200)
});

// layui方法
layui.use(['table', 'layer','form'], function () {
    var table = layui.table
        , layer = layui.layer //弹层
        , form = layui.form
    // layui方法
    // 表格渲染
    table.render({
        elem: '#dateTable'                  //指定原始表格元素选择器（推荐id选择器）
        , url: '../send/provinceServiceFeeEditPageList'
        , height: 471
        , cellMinWidth: 30
        , where:{id:id}
        , request: {
            pageName: 'pageNum' //页码的参数名称，默认：page
            , limitName: 'pageSize' //每页数据量的参数名，默认：limit
        }
        , cols: [[
            {field: 'provinceName', title: '省份', align: 'center'}
            , {field: 'firstWeightFee',  title: '寄件首重价',edit: 'text', align: 'center'}
            , {field: 'otherWeightFee',  title: '寄件续重价',edit: 'text', align: 'center'}
            , {field: 'firstCostFee',  title: '成本首重价',edit: 'text', align: 'center'}
            , {field: 'otherCostFee',  title: '成本续重价',edit: 'text', align: 'center'}
        ]],
        done: function(res) {
            if(res.stateCode == 100) {
                for (var a = 0; a < $('#schoolId').find('option').length; a++) {
                    if ($('#schoolId').find('option').eq(a).attr('id') == res.data[0].schoolId) {
                        $('#schoolId').find('option').eq(a).attr('selected', true);
                        form.render();
                    }
                }
                for (var i = 0; i < $('#deliveryId').find('option').length; i++) {
                    if ($('#deliveryId').find('option').eq(i).attr('id') == res.data[0].deliveryId) {
                        $('#deliveryId').find('option').eq(i).attr('selected', true);
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
            id:data.provinceId,
            provinceName:data.provinceName,
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
                    },2000)
                }else{
                }
            },error: function (err) {
                layer.alert(err.message, {icon: 0})
            }
        })
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
    // 表格渲染
    table.render({
        elem: '#dateTable'                  //指定原始表格元素选择器（推荐id选择器）
        , url: '../send/provinceServiceFeeEditBySchoolPageList'
        , height: 471
        , cellMinWidth: 30
        , where:{schoolId:schoolId,deliveryId:deliveryId}
        , request: {
            pageName: 'pageNum' //页码的参数名称，默认：page
            , limitName: 'pageSize' //每页数据量的参数名，默认：limit
        }
        , cols: [[
            {field: 'provinceName', title: '省份', align: 'center'}
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
    });

    form.on('select(deliveryId)', function (data) {
        var schoolId = $('#schoolId').find('option:selected').attr('id');
        var deliveryId = $('#deliveryId').find('option:selected').attr('id');
    // 表格渲染
    table.render({
        elem: '#dateTable'                  //指定原始表格元素选择器（推荐id选择器）
        , url: '../send/provinceServiceFeeEditBySchoolPageList'
        , height: 471
        , cellMinWidth: 30
        , where:{schoolId:schoolId,deliveryId:deliveryId}
        , request: {
            pageName: 'pageNum' //页码的参数名称，默认：page
            , limitName: 'pageSize' //每页数据量的参数名，默认：limit
        }
        , cols: [[
            {field: 'provinceName', title: '省份', align: 'center'}
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
    });
});


//保存
// layui.use(['form','laydate','layer'], function () {
//     var form = layui.form;
//     var laydate = layui.laydate;
//     var layer = layui.layer;
//     laydate.render({
//         elem: '#timeRange'
//         ,type: 'time'
//         ,range: true
//     });
//     form.render();

    //监听提交
//     form.on('submit(formDemo)', function(data){
//         var schoolId = $('#schoolId').find('option:selected').attr('id');
//         var type = $('#type').find('option:selected').attr('id');
//         var data = {
//             id: id,
//             schoolId:schoolId,
//             type:type,
//         };
//         $.ajax({
//             type: 'POST',
//             url: '../send/updateServiceFee',
//             dataType: 'json',
//             contentType: 'application/json; charset=utf-8',
//             headers: {
//                 'Accept': 'application/json; charset=utf-8',
//                 'Authorization': 'Basic ' + sessionStorage.getItem('token')
//             },
//             data: JSON.stringify(data),
//             success: function (res) {
//                 if(res.stateCode == 100){
//                     layer.alert('修改成功',{icon: 1});
//                     setTimeout(function () {
//                         location.href = '../home/courierCostManageHtml';
//                     },1500)
//                 }else{
//                     layer.alert(res.message, {icon: 0})
//                 }
//             },error: function (err) {
//                 layer.alert(err.message, {icon: 0})
//             }
//         })
//         return false;
//     });
// });

