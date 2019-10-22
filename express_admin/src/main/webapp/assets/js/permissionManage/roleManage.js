/**
 * Created by 12452 on 2017/10/20.
 */
var categoryid = '';
var pageNum = 1;
var data = {
    pageNum: pageNum
};
var ss;
var urlle = sessionStorage.getItem('urllen');
var productIds = '';
var table1 = '';
// ajax加载列表
$.ajax({
    type: 'POST',
    url: '../role/getRoleListAllByUser',
    dataType: 'json',

    contentType: 'application/json; charset=utf-8',
    headers: {
        'Accept': 'application/json; charset=utf-8',
        'Authorization': 'Basic ' + sessionStorage.getItem('token')
    },
    data: JSON.stringify(data),
    success: function (res) {
    load(res);
    }
})



//新增角色
$('#addCategory').click(function () {
    $('#approveModal').modal('show');
    var name = $('#name').val("");
    $('input:checkbox[name=schoolIds1]:checked').attr("checked",false);

    var treeObj=$.fn.zTree.getZTreeObj("menuTree");
    //treeObj.cancelSelectedNode(nodes[0]);
    treeObj.checkAllNodes(false)
})

$('#addSure').click(function (){
    //获取ztree勾选的节点
    //先获得所有的菜单节点
    var treeObj=$.fn.zTree.getZTreeObj("menuTree");
    //再获得已勾选的节点，勾选一个子节点，父节点会自动勾选
    var nodes=treeObj.getCheckedNodes(true);
    //获取所有节点的id
    var array = new Array();
    for(var i=0;i<nodes.length;i++){
        array.push(nodes[i].id);
    }
    var menuIds=array.join(",");
    //把勾选节点中所有子节点的id用逗号分隔成字符串自后，然后赋给隐藏域
    var name = $('#name').val();
    var desc = $('#desc').val();
    var schoolArr= new Array();
    $('input:checkbox[name=schoolIds1]:checked').each(function(i){
        schoolArr.push($(this).val())
    });
    var schoolStr = schoolArr.join(",");
    ss = {
        name: name,
        desc:desc,
        menuIds:menuIds,
        schoolStr:schoolStr
    }
    if (name == '') {
        layer.alert('角色名称不能为空！', {icon: 0});
    } else if($('input:checkbox[name=schoolIds1]:checked').length==0){
        layer.alert('至少选择一个网点！', {icon: 0});
    } else {
        $.ajax({
            type: 'POST',
            url: '../role/newRoleAddAndEditorManage',
            dataType: 'json',
            contentType: 'application/json; charset=utf-8',
            headers: {
                'Accept': 'application/json; charset=utf-8',
                'Authorization': 'Basic ' + sessionStorage.getItem('token')
            },
            data: JSON.stringify(ss),
            success: function (res) {
                if (res.stateCode == 100) {
                    window.location.reload();
                } else {
                    layer.alert(res.message, {icon: 0});
                }
            },
            error: function (err) {
                layer.alert(err.message, {icon: 0});
            }
        })
    }
})

function load(res){
    $('.spinner').hide();
    $('#tbody').html('');

    for (var i = 0; i < res.data.length; i++) {
        table1 += '<tr id="' +  res.data[i].id + '">' +
            '<td>' +  res.data[i].name + '</td>' +
            '<td>' +  res.data[i].desc + '</td>' +
            '<td style="width:100px"><span style="padding-right: 5px" class="check checkModal setting-btn">查看</span><span class="edit editModal setting-btn">编辑</span><span style="padding-left: 5px" class="delete setting-btn">删除</span></td></tr>';
    }
    $('#tbody').append(table1);

    var id;
    // 编辑
    $('#tbody .editModal').click(function(){
        $('#approveModal1').modal('show');
        $('input:checkbox[name=schoolIds2]:checked').each(function(i){
           $(this).attr("checked",false);
        });
        var treeObj=$.fn.zTree.getZTreeObj("menuTreeEditor");
        //treeObj.cancelSelectedNode(nodes[0]);
        treeObj.checkAllNodes(false)
        id = $(this).parent().parent().attr('id');

        $.ajax({
            type: 'POST',
            url: '../role/roleCheckDetail',
            dataType: 'json',
            contentType: 'application/json; charset=utf-8',
            headers: {
                'Accept': 'application/json; charset=utf-8',
                'Authorization': 'Basic ' + sessionStorage.getItem('token')
            },
            data: JSON.stringify({id: id}),
            success: function (res) {
                if(res.stateCode == 100){
                    $('#name1').val(res.data.name);
                    $("#desc1").val(res.data.desc);
                    var zTree_Menu = $.fn.zTree.getZTreeObj("menuTreeEditor");
                    $(res.data.menu).each(function(index,element){
                        if(element.pId!="0"){
                            var node = zTree_Menu.getNodeByParam("id",element.id);
                            var nodeP = zTree_Menu.getNodeByParam("id",element.pId);
                            /*zTree_Menu.selectNode(node,true);//指定选中ID的节点,背景选中
                            zTree_Menu.expandNode(node, true, false);//指定选中ID节点展开*/
                            zTree_Menu.checkNode(node, true, true);//框选中
                            zTree_Menu.expandNode(nodeP, true, true);//指定选中ID节点展开
                        }

                    });
                    if(res.data.school){
                        $(res.data.school).each(function(index,element){
                            $("input:checkbox[value="+element.id+"][name=schoolIds2]").prop("checked",true);
                        });
                    }


                }else {
                    layer.alert(res.message, {icon: 0});
                }
            },
            error: function (err) {
                layer.alert(err.message, {icon: 0});
            }
        });

    })

    $('#editorSure').click(function () {
        var schoolArr= new Array();
        $('input:checkbox[name=schoolIds2]:checked').each(function(i){
            schoolArr.push($(this).val())
        });
        var schoolStr = schoolArr.join(",");
        ss = {};
        //获取ztree勾选的节点
        //先获得所有的菜单节点
        var treeObj=$.fn.zTree.getZTreeObj("menuTreeEditor");
        //再获得已勾选的节点，勾选一个子节点，父节点会自动勾选
        var nodes=treeObj.getCheckedNodes(true);
        //获取所有节点的id
        var array = new Array();
        for(var i=0;i<nodes.length;i++){
            array.push(nodes[i].id);
        }
        var menuIds=array.join(",");
        //把勾选节点中所有子节点的id用逗号分隔成字符串自后，然后赋给隐藏域
        var name = $('#name1').val();
        var desc = $('#desc1').val();
        ss = {

            name: name,
            desc:desc,
            id:id,
            menuIds:menuIds,
            schoolStr:schoolStr
        }

        if (name == '') {
            layer.alert('名称不能为空！', {icon: 0});
        } else if($('input:checkbox[name=schoolIds2]:checked').length==0){
            layer.alert('至少选择一个网点！', {icon: 0});
        } else {
            $.ajax({
                type: 'POST',
                url: '../role/newRoleAddAndEditorManage',
                dataType: 'json',
                contentType: 'application/json; charset=utf-8',
                headers: {
                    'Accept': 'application/json; charset=utf-8',
                    'Authorization': 'Basic ' + sessionStorage.getItem('token')
                },
                data: JSON.stringify(ss),
                success: function (res) {
                    if(res.stateCode == 100){
                        window.location.reload();
                    }else {
                        layer.alert(res.message, {icon: 0});
                    }
                },
                error: function (err) {
                    layer.alert(err.message, {icon: 0});
                }
            })

        }

    })

    // 查看
    $('#tbody .checkModal').click(function(){
        $('#approveModal2').modal('show');


        id = $(this).parent().parent().attr('id');
        $.ajax({
            type: 'POST',
            url: '../role/roleCheckDetail',
            dataType: 'json',
            contentType: 'application/json; charset=utf-8',
            headers: {
                'Accept': 'application/json; charset=utf-8',
                'Authorization': 'Basic ' + sessionStorage.getItem('token')
            },
            data: JSON.stringify({id: id}),
            success: function (res) {
                if(res.stateCode == 100){
                    $('#roleName').html(res.data.name);
                    $('#roleDesc').html(res.data.desc);
                    // var zNodes = eval("(" + res + ")");
                    var zNodes = res.data.menu;
                if(zNodes==undefined){
                    $("#checkMenuTree").html("没有分配菜单")
                }else{
                    $.fn.zTree.init($("#checkMenuTree"), setting1, zNodes);
                    var zTree_Menu = $.fn.zTree.getZTreeObj("checkMenuTree");
                    $(res.data.menu).each(function(index,element){
                        if(element.pId=="0"){

                            var node = zTree_Menu.getNodeByParam("id",element.id);
                           // zTree_Menu.selectNode(node,true);//指定选中ID的节点,背景选中
                           zTree_Menu.expandNode(node, true, false);//指定选中ID节点展开
                            //zTree_Menu.checkNode(node, true, true);//框选中
                            zTree_Menu.expandNode(node, true, true);//指定选中ID节点展开
                        }

                    });
                }

                if(res.data.school==undefined){
                    $("#haveSchool").html("没有分配网点")
                }else {
                    var sc="";
                    $(res.data.school).each(function(index,element) {
                        sc = sc.concat(element.name+"<br>")
                    })
                    $("#haveSchool").html(sc)
                }
                }else{
                    alert(res.message);
                }

            },
            error: function (err) {
                layer.alert(err.message, {icon: 0});
            }
        });

    })

// 角色删除
    var secId;
    $('#tbody .delete').click(function(){
        $('#myModal2').modal('show');
        secId = $(this).parent().parent().attr('id');
    });

    $('#delSure').click(function () {
        $.ajax({
            type: 'POST',
            url: '../role/deleteRoleId',
            dataType: 'json',
            contentType: 'application/json; charset=utf-8',
            headers: {
                'Accept': 'application/json; charset=utf-8',
                'Authorization': 'Basic ' + sessionStorage.getItem('token')
            },
            data: JSON.stringify({id: secId}),
            success: function (res) {
                $('#myModal2').modal('hide');
                window.location.reload()
            },
            error: function (err) {
                alert(err.message);
            }
        });
    })

}

