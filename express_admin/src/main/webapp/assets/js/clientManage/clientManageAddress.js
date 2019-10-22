var url = window.location.href;
var id = url.split('=')[1];

// layui方法
layui.use(['table', 'layer','form'], function () {
    var table = layui.table
        , layer = layui.layer //弹层
        , form = layui.form

    // 表格渲染
    table.render({
        elem: '#dateTable'                  //指定原始表格元素选择器（推荐id选择器）
        , url: '../customer/expSendAddressPageList'
        , height: 471
        , cellMinWidth: 80
        , where:{id:id}
        , request: {
            pageName: 'pageNum' //页码的参数名称，默认：page
            , limitName: 'pageSize' //每页数据量的参数名，默认：limit
        }
        , cols: [[
            {field: 'name', sort: true, title: '寄件人',  align: 'center'}
            , {field: 'tel', sort: true, title: '寄件人手机', align: 'center'}
            , {field: 'address', title: '寄件人地址',width: 500, align: 'center'}
            , {field: 'isDefault', title: '是否默认', align: 'center'}
            , {fixed: 'right',title: '操作', align: 'center', toolbar: '#barDemo', width: 260}
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
       if (layEvent === 'edit') {
           $.ajax({
               type: 'POST',
               url: '../customer/expSendAddressDefault',
               dataType: 'json',
               contentType: 'application/json; charset=utf-8',
               headers: {
                   'Accept': 'application/json; charset=utf-8',
                   'Authorization': 'Basic ' + sessionStorage.getItem('token')
               },
               data: JSON.stringify({id:data.id,addressId:data.addressId}),
               success: function (res) {
                   if(res.stateCode == 100){
                       // layer.alert('修改成功',{icon: 1});
                       window.location.reload();
                       // setTimeout(function () {
                       //     location.href = '../home/courierCostManageHtml';
                       // },1500)
                   }else{
                       layer.alert(res.message, {icon: 0})
                   }
               },error: function (err) {
                   layer.alert(err.message, {icon: 0})
               }
           })
        }
    });
});