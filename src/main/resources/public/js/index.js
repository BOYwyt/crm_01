layui.use(['form','jquery','jquery_cookie'], function () {
    var form = layui.form,
        layer = layui.layer,
        $ = layui.jquery,
        $ = layui.jquery_cookie($);
    /*
    * 用户登录：
    *       表单提交
    * */
    form.on("submit(login)",function (data){
        //获取表单的值
        var filedData=data.field;
        //判断参数是否为空
        if(filedData.username.trim()=="" &&filedData.username=="undefined"){
            layer.msg("用户名不能为空");
            return false;
        }
        if(filedData.password.trim()=="" &&filedData.password=="undefined"){
            layer.msg("用户密码不能为空");
            return false;
        }

        //发送ajax请求
        $.ajax({
            type:"post",
            url:ctx+"/user/login",
            dataType:"json",
            data:{
                userName:filedData.username,
                userPwd:filedData.password
            },
            success:function (result){
                if(result.code==200){
                    layer.msg("登录成功",function (){
                        //将用户信息存储到cookie中
                        $.cookie("userIdStr",result.result.userIdStr);
                        $.cookie("userName",result.result.userName);
                        $.cookie("trueName",result.result.trueName);


                        if($("input[type='checkbox']").is(":checked")){
                            $.cookie("userIdStr",result.result.userIdStr,{expires:7});
                            $.cookie("userName",result.result.userName,{expires:7});
                            $.cookie("trueName",result.result.trueName,{expires:7});
                        }
                        window.location.href=ctx+"/main"
                    });
                }
                else {
                    layer.msg("用户或密码错误")
                }
            }

        })
        //阻止表单跳转
        return  false;
    });


});