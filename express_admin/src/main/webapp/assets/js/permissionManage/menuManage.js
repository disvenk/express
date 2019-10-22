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

//新增菜单
$('#addCategory').click(function () {
    $("#approveModalLabel").html("添加一级菜单");
    $('#approveModal').modal('show');
    $.ajax({
        type: 'POST',
        url: '../menu/checkMaxSort',
        dataType: 'json',
        contentType: 'application/json; charset=utf-8',
        headers: {
            'Accept': 'application/json; charset=utf-8',
            'Authorization': 'Basic ' + sessionStorage.getItem('token')
        },
        success: function (res) {
            $("#sort").val(res.data);
        },
        error: function (err) {

            alert(err.message);
        }
    })
    var name = $('#name').val("");
    var href = $("#href").val("");
    var icon = $("#iconCodeTxtParent").val("");
})

$('#addSure').click(function (){
    var name = $('#name').val();
    var icon = $("#iconCodeTxtParent").val();
    var sort = $('#sort').val();
    var result = (sort.toString()).indexOf(".");
    ss = {};
    ss = {
        sort: sort,
        name: name,
        icon:icon
        /*parentId: parentId*/
    }
    if (name == '') {
        layer.alert('菜单名称不能为空！', {icon: 0});
    }else if(!/^[0-9]+$/.test(sort)){
        layer.alert('排序只能是纯数字！', {icon: 0});
    }else if(icon=="" || icon==undefined || icon==null){
        layer.alert('请选择图标！', {icon: 0});
    }else {
        $.ajax({
            type: 'POST',
            url: '../menu/newMenuAddManage',
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
                    alert(res.message)
                }
            },
            error: function (err) {
                alert(err.message)
            }
        })
    }
})

// ajax加载列表
$.ajax({
    type: 'POST',
    url: '../menu/getMenuListAll',
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


function load(res) {
    $('.spinner').hide();
    $('#tbody').html('');
    var sort;
    var sort1;
    for (var i = 0; i < res.data.length; i++) {
        sort=i+1;
        productIds='';
        var table1 = '';
        table1 += '<tr class="'+res.data[i].id+'" style="background-color:rgb(243,242,244);height: 50px;">' +
            '<td style="border: none"><span style="color:red" class="color-blue"></span>' + res.data[i].name + '</td>' +
            '<td style="border: none"><span class="color-blue"></span>' + res.data[i].href + '</td>' +
            '<td style="border: none"><span class="color-blue"></span>' + '' + '</td>' +
            '<td style="border: none"><span class="color-blue"></span>' + sort + '</td>' +
            '<td style="border: none"><span id="storageEdit" class="edit1 editModal setting-btn">编辑</span><span class="delete1 setting-btn">删除</span><span class="addCategory1 setting-btn"><br>添加下级菜单</span></td></tr>';
        '<td style="border: none" class="null"></td></tr>';
        for (var j = 0; j < res.data[i].menuList.length; j++) {
            sort1=j+1;
            var detailListArr = res.data[i].menuList[j];
            table1 += '<tr id="' + detailListArr.id + '">' +
                '<td>' + detailListArr.name+ '</td>' +
                '<td>' + detailListArr.href + '</td>' +
                '<td>' + res.data[i].name + '</td>' +
                '<td>' +'('+sort1+')' + '</td>' +
                '<td style="width:100px"><span id="storageEdit" parentId="'+res.data[i].id+'" class="edit2 editModal setting-btn">编辑</span><span class="delete2 setting-btn">删除</span></td></tr>';
        }
        $('#tbody').append(table1);
    }
    // 一级菜单删除
    var categoryId;
    $('.delete1').click(function(){
        $('#myModal').modal('show');
        categoryId = $(this).parent().parent().attr('class');
    })

    $('#delSure').click(function () {
        $.ajax({
            type: 'POST',
            url: '../menu/deleteMenuParentId',
            dataType: 'json',
            contentType: 'application/json; charset=utf-8',
            headers: {
                'Accept': 'application/json; charset=utf-8',
                'Authorization': 'Basic ' + sessionStorage.getItem('token')
            },
            data: JSON.stringify({id: categoryId}),
            success: function (res) {
                $('#myModal').modal('hide');
                if(res.stateCode != 100){
                    alert(res.message);
                }
                window.location.reload();
            },
            error: function (err) {
                $('#myModal').modal('hide');
                alert(err.message);
            }
        });
    })
    // 二级菜单删除
    var delId;
    $('#tbody .delete2').click(function(){
        $('#myModal2').modal('show');
        delId = $(this).parent().parent().attr('id');
    })

    $('#delSure2').click(function () {
        $.ajax({
            type: 'POST',
            url: '../menu/deleteMenuId',
            dataType: 'json',
            contentType: 'application/json; charset=utf-8',
            headers: {
                'Accept': 'application/json; charset=utf-8',
                'Authorization': 'Basic ' + sessionStorage.getItem('token')
            },
            data: JSON.stringify({id: delId}),
            success: function (res) {
                $('#myModal').modal('hide');
                window.location.reload()
            },
            error: function (err) {
                alert(err.message);
            }
        });
    })

    var id;
    var parentId;
    // 编辑父
    $('#tbody .edit1').click(function(){
        $('#approveModal1').modal('show');
        id = $(this).parent().parent().attr('class');
        $.ajax({
            type: 'POST',
            url: '../menu/menuEditDetail',
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
                    $('#sort2').val(res.data.sort);
                    $("#pressentSort").html(res.data.sort);

                }else{
                    alert(res.message);
                }

            },
            error: function (err) {
                alert(err.message);
            }
        });
    })
//编辑子
    $('#tbody .edit2').click(function(){
        $('#approveModal2').modal('show');

        parentId = $(this).attr("parentId");
        id = $(this).parent().parent().attr('id');
        $.ajax({
            type: 'POST',
            url: '../menu/menuEditDetail',
            dataType: 'json',
            contentType: 'application/json; charset=utf-8',
            headers: {
                'Accept': 'application/json; charset=utf-8',
                'Authorization': 'Basic ' + sessionStorage.getItem('token')
            },
            data: JSON.stringify({id: id,parentId:parentId}),
            success: function (res) {
                if(res.stateCode == 100){
                    $('#name3').val(res.data.name);
                    $('#href3').val(res.data.href);
                    $('#sort3').val(res.data.sort);
                    $("#pressentSort1").html(res.data.sort);

                }else{
                    alert(res.message);
                }

            },
            error: function (err) {
                alert(err.message);
            }
        });
    })
//编辑提交父
    $('#editorSure').click(function () {
        ss = {};
        var name = $('#name1').val();
        var href = $('#href1').val();
        var icon =$("#iconCodeTxtParentE").val();
        var sort = $('#sort2').val();
        ss = {
            sort: sort,
            name: name,
            id:id,
            icon:icon,
            href:href,
            parentId: parentId
        }

        if (name == '') {
            layer.alert('名称不能为空！', {icon: 0});
        }else if(icon=="" || icon==undefined || icon==null){
            layer.alert('请选择图标！', {icon: 0});
        } else{
            $.ajax({
                type: 'POST',
                url: '../menu/editMenu',
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
                        alert(res.message)
                    }
                },
                error: function (err) {
                    alert(err.message)
                }
            })
        }
    })
//编辑提交子
    $('#editorSure1').click(function () {
        ss = {};
        var name = $('#name3').val();
        var href = $('#href3').val();
        var icon = $("#iconCodeTxtChildE").val();
        var sort = $('#sort3').val();
        ss = {
            sort: sort,
            name: name,
            id:id,
            icon:icon,
            href:href,
            parentId:parentId
        }


        if (name == '') {
            layer.alert('名称不能为空！', {icon: 0});
        } else if(href == ''){
            layer.alert('路径不能为空！', {icon: 0});
        }else if(!/^[0-9]+$/.test(sort)){
            layer.alert('排序只能是纯数字！', {icon: 0});
        }else if(icon=="" || icon==undefined || icon==null){
            layer.alert('请选择图标！', {icon: 0});
        }else{
            $.ajax({
                type: 'POST',
                url: '../menu/editMenuChild',
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
                        alert(res.message)
                    }
                },
                error: function (err) {
                    alert(err.message)
                }
            })
        }
    })

    //添加二级菜单
    var parentId;
    $('#tbody .addCategory1').click(function () {
        $('#approveModa2').modal('show');
        $('#name2').val("");
        parentId = $(this).parent().parent().attr('class');
    })
    $('#addSure2').click(function (){
        var name = $('#name2').val();
        var href = $('#href2').val();
        var icon = $("#iconCodeTxtChild").val();
        var sort = $("#sort1").val();
        ss = {};
        ss = {
            href: href,
            name: name,
            sort:sort,
            icon:icon,
            parentId: parentId
        }
        if (name == '') {
            layer.alert('名称不能为空！', {icon: 0});
        }else if(href == ''){
            layer.alert('路径不能为空！', {icon: 0});
        }else if(!/^[0-9]+$/.test(sort)){
            layer.alert('排序只能是纯数字！', {icon: 0});
        }else{
            $.ajax({
                type: 'POST',
                url: '../menu/menuAddManage',
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
                        alert(res.message)
                    }
                },
                error: function (err) {
                    alert(err.message)
                }
            })
        }
    })
}

// 选择图标

$('#parent li').click(function () {
    $('#iconCodeTxtParent').val();
    $('#parent li').removeClass('active');
    $(this).addClass('active');
    var iconCode=$('#parent li.active').attr('value');
    console.log(iconCode);
    $('#iconCodeTxtParent').val(iconCode);
})

$('#child li').click(function () {
    $('#iconCodeTxtChild').val();
    $('#child li').removeClass('active');
    $(this).addClass('active');
    var iconCode=$('#child li.active').attr('value');
    console.log(iconCode);
    $('#iconCodeTxtChild').val(iconCode);
})

$('#parentE li').click(function () {
    $('#iconCodeTxtParentE').val();
    $('#parentE li').removeClass('active');
    $(this).addClass('active');
    var iconCode=$('#parentE li.active').attr('value');
    console.log(iconCode);
    $('#iconCodeTxtParentE').val(iconCode);
})

$('#childE li').click(function () {
    $('#iconCodeTxtChildE').val();
    $('#childE li').removeClass('active');
    $(this).addClass('active');
    var iconCode=$('#childE li.active').attr('value');
    console.log(iconCode);
    $('#iconCodeTxtChildE').val(iconCode);
})
