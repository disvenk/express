var token = sessionStorage.getItem('token');
$(function () {
    $.ajax({
        type: "POST",
        url: "../account/loginInfo",
        contentType: "application/json; charset=utf-8",
        headers: {
            "Accept": "application/json; charset=utf-8",
            "Authorization": "Basic " + sessionStorage .getItem("token")
        },
        success: function (res) {
            if(res.stateCode == 100){
                $('#school').html(res.data.schoolName);
                $('#phone').html(res.data.mobile);
                $('#name').val(res.data.nickName);
                $('#headImgUrl').attr('src', res.data.icon);
            }else{
                layer.msg(res.message);
            }
        },
        error: function (err) {
            layer.msg(err.message);
        }
    });
});
$('#name').blur(function() {
    $.ajax({
        type: "POST",
        url: "../account/updateName",
        contentType: "application/json; charset=utf-8",
        headers: {
            "Accept": "application/json; charset=utf-8",
            "Authorization": "Basic " + sessionStorage .getItem("token")
        },
        data: JSON.stringify({
            name: $('#name').val()
        }),
        success: function (res) {
            if(res.stateCode == 100){
                layer.msg('修改成功')
            }else{
                layer.msg(res.message);
            }
        },
        error: function (err) {
            layer.msg(err.message);
        }
    });
});
//退出帐号
$('#sure').click(function () {
    sessionStorage.removeItem('token');
    sessionStorage.removeItem('userStatus');
    window.location.href = '../homePage/loginHtml';
});
// var headImgUrl = '';
// var localData;
// $('#picture').click(function(){
//     if(/(iPhone|iPad|iPod|iOS)/i.test(navigator.userAgent)) {
//         wx.chooseImage({
//             count: 1, // 默认9
//             sizeType: ['original', 'compressed'], // 可以指定是原图还是压缩图，默认二者都有
//             sourceType: ['camera', 'album'], // 可以指定来源是相册还是相机，默认二者都有
//             success: function (res) {
//                 headImgUrl = res.localIds[0]; // 返回选定照片的本地ID列表，localId可以作为img标签的src属性显示图片
//                 $('#headImgUrl').attr('src', headImgUrl);
//                 wx.getLocalImgData({
//                     localId: headImgUrl, // 图片的localID
//                     success: function (res) {
//                         localData = res.localData; // localData是图片的base64数据，可以用img标签显示
//                         $('#headImgUrl').attr('src', localData);
//                     }
//                 });
//                 wx.uploadImage({
//                     localId: headImgUrl, // 需要上传的图片的本地ID，由chooseImage接口获得
//                     isShowProgressTips: 0, // 默认为1，显示进度提示
//                     success: function (res) {
//                         headImgUrl = res.serverId; // 返回图片的服务器端ID
//                         updateIcon(headImgUrl);
//                     }
//                 });
//             }
//         });
//     }else if(/android/i.test(navigator.userAgent)) {
//             wx.chooseImage({
//                 count: 1, // 默认9
//                 sizeType: ['original', 'compressed'], // 可以指定是原图还是压缩图，默认二者都有
//                 sourceType: ['camera', 'album'], // 可以指定来源是相册还是相机，默认二者都有
//                 success: function (res) {
//                     headImgUrl = res.localIds[0]; // 返回选定照片的本地ID列表，localId可以作为img标签的src属性显示图片
//                     $('#headImgUrl').attr('src', headImgUrl);
//                     wx.uploadImage({
//                         localId: headImgUrl, // 需要上传的图片的本地ID，由chooseImage接口获得
//                         isShowProgressTips: 0, // 默认为1，显示进度提示
//                         success: function (res) {
//                             headImgUrl = res.serverId; // 返回图片的服务器端ID
//                             updateIcon(headImgUrl);
//                         }
//                     });
//                 }
//             });
//         }
// });
//
// //修改头像
// function updateIcon(icon) {
//     $.ajax({
//         type: "POST",
//         url: "../homePage/updateIcon",
//         contentType: "application/json; charset=utf-8",
//         headers: {
//             "Accept": "application/json; charset=utf-8",
//             "Authorization": "Basic " + sessionStorage .getItem("token")
//         },
//         data: JSON.stringify({
//             icon: icon
//         }),
//         success: function (res) {
//             if(res.stateCode == 100){
//                 layer.msg('修改成功')
//             }else{
//                 layer.msg(res.message);
//             }
//         },
//         error: function (err) {
//             layer.msg(err.message);
//         }
//     });
// }