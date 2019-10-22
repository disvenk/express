
$('#platAdd').click(function () {
    location.href='platManageAddHtml';
})

var url = 'http://localhost:8080/admin/';
// layui方法
layui.use(['table', 'layer','form'], function () {
    var table = layui.table
        , layer = layui.layer //弹层
        , form = layui.form

    // 表格渲染
    table.render({
        elem: '#dateTable'                  //指定原始表格元素选择器（推荐id选择器）
        , url: url + 'index/textPageList'
        , height: 471
        , cellMinWidth: 80
        , request: {
            pageName: 'pageNum' //页码的参数名称，默认：page
            , limitName: 'pageSize' //每页数据量的参数名，默认：limit
        }
        , cols: [[
            {type: 'checkbox'}
            , {field: 'id', title: 'ID', sort: true, align: 'center'}
            , {field: 'name', sort: true, title: '姓名', edit: 'text', align: 'center'}
            , {field: 'age', sort: true, title: '年龄', align: 'center'}
            , {field: 'uuid', title: 'uu', align: 'center'}
            , {field: 'random', title: '随机数', align: 'center'}
            , {field: 'status', title: '状态', align: 'center'}
            , {field: 'time', sort: true, title: '时间',width: 177, align: 'center'}
            , {fixed: 'right', align: 'center', toolbar: '#barDemo', width: 200}

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
            layer.confirm('真的删除第' + data.id + '行么', function (index) {
                obj.del(); //删除对应行（tr）的DOM结构
                layer.close(index);
                //向服务端发送删除指令
            });
        } else if (layEvent === 'edit') {
//                layer.msg('编辑第'+data.id+'行');
            location.href = 'platManageEditHtml';
        } else if (layEvent === 'add'){
            location.href = 'platManageAddHtml';
        }
    });
});

