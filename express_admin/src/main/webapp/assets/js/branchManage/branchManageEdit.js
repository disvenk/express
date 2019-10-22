var url = window.location.href;
var id = url.split('=')[1];
layui.use(['form','laydate','layer'], function () {
    var form = layui.form;
    var laydate = layui.laydate;
    var layer = layui.layer;
    form.render();
    setTimeout(function () {
        $.ajax({
            type: 'POST',
            url:  '../school/schoolEdit',
            dataType: 'json',
            contentType: 'application/json; charset=utf-8',
            headers: {
                'Accept': 'application/json; charset=utf-8',
                'Authorization': 'Basic ' + sessionStorage.getItem('token')
            },
            data: JSON.stringify({id: id}),
            success: function (res) {
                if(res.stateCode == 100){
                    $('#isNot input[value='+res.data.captureName+']').attr('checked', true);
                    $('#ispaper input[value='+res.data.ispaperName+']').attr('checked',true);
                    form.render();
                    $('#school').val(res.data.name);
                    $('#takedAddress').val(res.data.takedAddress);
                    $('#template').val(res.data.template);
                    for(var i = 0;i<res.data.expTimeList.length;i++){
                        var tr1 = '';
                        tr1 += ' <tr>' +
                            '<td class="table_stock"><input type="number" value="' + res.data.expTimeList[i].beginTime + '"></td>' +
                            '<td class="table_offline"><input type="number" value="' + res.data.expTimeList[i].endTime + '"></td>' +
                            '<td><button class="layui-btn layui-btn-danger layui-btn-sm" onclick="$(this).parent().parent().remove()">删除</button></td>'+
                            // '<td><span class="ace-icon fa fa-trash-o" type="button" onclick="$(this).parent().parent().remove()">删除</span></td>'+
                            '</tr>';
                        $('#tbody').append(tr1);
                    }
                    for(var i = 0;i<res.data.expTimeList1.length;i++){
                        var tr1 = '';
                        tr1 += ' <tr>' +
                            '<td class="table_stock"><input type="number" value="' + res.data.expTimeList1[i].beginTime + '"></td>' +
                            '<td class="table_offline"><input type="number" value="' + res.data.expTimeList1[i].endTime + '"></td>' +
                            '<td><button class="layui-btn layui-btn-danger layui-btn-sm" onclick="$(this).parent().parent().remove()">删除</button></td>'+
                            // '<td><span class="ace-icon fa fa-trash-o" type="button" onclick="$(this).parent().parent().remove()">删除</span></td>'+
                            '</tr>';
                        $('#tbody1').append(tr1);
                    }
                }else{
                    layer.alert(res.message, {icon: 0})
                }
            },
            error: function (err) {
                layer.alert(err.message, {icon: 0});
            }
        })
    },200)
});
$("#addBtn").click(function(){
    // var submitBtn = document.getElementById('submitBtn');
    // submitBtn.classList.remove('hidden');
    var tb = document.querySelector('#simpleTable > tbody');
    var tr = document.createElement('tr');
    var tdInfo = document.createElement('td');
    var inputInfo = document.createElement('input');
    inputInfo.setAttribute('type', 'number');
    inputInfo.setAttribute('name', 'details');
    inputInfo.setAttribute('placeholder', '开始时间');
    inputInfo.setAttribute('required', 'required');
    inputInfo.classList.add('col-xs-12');
    tdInfo.appendChild(inputInfo);

    var tdAmount = document.createElement('td');
    var inputAmount = document.createElement('input');
    inputAmount.setAttribute('type', 'number');
    inputAmount.setAttribute('name', 'amounts');
    inputAmount.setAttribute('placeholder', '结束时间');
    inputAmount.setAttribute('required', 'required');
    inputAmount.classList.add('col-xs-12');
    tdAmount.appendChild(inputAmount);

    var tdOp = document.createElement('td');
    var delBtn = document.createElement('button');
    delBtn.setAttribute('type', 'button');
    delBtn.setAttribute('title', '删除');
    delBtn.classList.add('layui-btn');
    delBtn.classList.add('layui-btn-danger');
    delBtn.classList.add('layui-btn-sm');
    delBtn.innerHTML = '删除';
    delBtn.addEventListener('click', function(){
        tb.removeChild(tr);
    });
    tdOp.appendChild(delBtn);
    tr.appendChild(tdInfo);
    tr.appendChild(tdAmount);
    tr.appendChild(tdOp);
    tb.appendChild(tr);
});

$("#addBtn1").click(function(){
    // var submitBtn = document.getElementById('submitBtn');
    // submitBtn.classList.remove('hidden');
    var tb = document.querySelector('#simpleTable1 > tbody  ');
    var tr = document.createElement('tr');
    var tdInfo = document.createElement('td');
    var inputInfo = document.createElement('input');
    inputInfo.setAttribute('type', 'number');
    inputInfo.setAttribute('name', 'details');
    inputInfo.setAttribute('placeholder', '开始时间');
    inputInfo.setAttribute('required', 'required');
    inputInfo.classList.add('col-xs-12');
    tdInfo.appendChild(inputInfo);

    var tdAmount = document.createElement('td');
    var inputAmount = document.createElement('input');
    inputAmount.setAttribute('type', 'number');
    inputAmount.setAttribute('name', 'amounts');
    inputAmount.setAttribute('placeholder', '结束时间');
    inputAmount.setAttribute('required', 'required');
    inputAmount.classList.add('col-xs-12');
    tdAmount.appendChild(inputAmount);

    var tdOp = document.createElement('td');
    var delBtn = document.createElement('button');
    delBtn.setAttribute('type', 'button');
    delBtn.setAttribute('title', '删除');
    delBtn.classList.add('layui-btn');
    delBtn.classList.add('layui-btn-danger');
    delBtn.classList.add('layui-btn-sm');
    delBtn.innerHTML = '删除';
    delBtn.addEventListener('click', function(){
        tb.removeChild(tr);
    });
    tdOp.appendChild(delBtn);
    tr.appendChild(tdInfo);
    tr.appendChild(tdAmount);
    tr.appendChild(tdOp);
    tb.appendChild(tr);
});
//保存
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
        var pattern = /^[0-9]*$/;
        var sendTimeList = [];
        var table = $('#tbody tr');
        for (var i = 0; i < table.length; i++) {
            sendDoor1 =  table.eq(i).find('td').eq(0).find('input').eq(0).val();
            sendDoor2 =  table.eq(i).find('td').eq(1).find('input').eq(0).val();

            if(!(pattern.test(sendDoor1)) || !(pattern.test(sendDoor2))){
                layer.alert('派件上门的时间不能为小数', {icon: 0});
                return false;
            }
            if(sendDoor1 == 24 || sendDoor2 == 24){
                layer.alert('派件上门的时间不能为24点', {icon: 0});
                return false;
            }
            var obj = {};
            obj = {
                sendDoor1: table.eq(i).find('td').eq(0).find('input').eq(0).val(),
                sendDoor2: table.eq(i).find('td').eq(1).find('input').eq(0).val()
            };
            sendTimeList[i] = obj;
        }
        var receiveTimeList = [];
        var table = $('#tbody1 tr');
        for (var i = 0; i < table.length; i++) {
            receiveDoor1 = table.eq(i).find('td').eq(0).find('input').eq(0).val();
            receiveDoor2 = table.eq(i).find('td').eq(1).find('input').eq(0).val();
            if(!(pattern.test(receiveDoor1)) || !(pattern.test(receiveDoor2))){
                layer.alert('上门取件的时间不能为小数', {icon: 0})
                return false;
            }
            if(receiveDoor1 == 24 || receiveDoor2 == 24){
                layer.alert('上门取件的时间不能为24点', {icon: 0});
                return false;
            }
            var obj = {};
            obj = {
                receiveDoor1: table.eq(i).find('td').eq(0).find('input').eq(0).val(),
                receiveDoor2: table.eq(i).find('td').eq(1).find('input').eq(0).val()
            };
            receiveTimeList[i] = obj;
        }

        var captureName = $('#isNot input[name=yes]:checked').attr('value');
        if(captureName == '是'){
            captureName = true
        }else{
            captureName = false
        }
        var ispaper = $('#ispaper input[name=sex]:checked').attr('value');
        if(ispaper == '纸质面单'){
            ispaper = 1;
        }else{
            ispaper = 0;
        }
        var data = {
            id: id,
            name: $('#school').val(),
            capture:captureName,
            ispaper:ispaper,
            takedAddress:$("#takedAddress").val(),
            sendTimeList:sendTimeList,
            receiveTimeList:receiveTimeList
        };
        $.ajax({
            type: 'POST',
            url: '../school/updateExpSchool',
            dataType: 'json',
            contentType: 'application/json; charset=utf-8',
            headers: {
                'Accept': 'application/json; charset=utf-8',
                'Authorization': 'Basic ' + sessionStorage.getItem('token')
            },
            data: JSON.stringify(data),
            success: function (res) {

                if(res.stateCode == 100){
                    layer.alert('提交成功',{icon: 1});
                    setTimeout(function () {
                        location.href = '../home/branchHtml';
                    },1500)
                }else{
                    layer.alert(res.message, {icon: 0})
                }
            },error: function (err) {
                layer.alert(err.message, {icon: 0})
            }
        })
        return false;
    });
});


