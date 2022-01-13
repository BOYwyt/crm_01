layui.use(['form','jquery','jquery_cookie'], function () {
    var form = layui.form,
        layer = layui.layer,
        $ = layui.jquery,
        $ = layui.jquery_cookie($);
//监听提交
    form.on('submit(saveBtn)',function(data){
        var fileData=data.field;
        //ajax
        $.ajax({
            type:"post",
            url:ctx+"/user/update",
            data:{
                oldPassWord:fileData.old_password,
                newPassWord:fileData.new_password,
                confirmPassword:fileData.again_password
            },
            dataType:"json",
            success:function (data){
                if(data.code==200){
                    layer.msg("3s后退出当前页面",function (){
                        //删除当前页面cookie
                        $.removeCookie("userIdStr",{domain:"localhost",path:"/crm"});
                        $.removeCookie("userName",{domain:"localhost",path:"/crm"});
                        $.removeCookie("trueName",{domain:"localhost",path:"/crm"});
                        //跳到登录页面（父页面）
                        window.parent.location.href=ctx+"/index";
                    });
                }else{
                    layer.msg(data.msg);
                }
            }
        })




        return false;
    });
});