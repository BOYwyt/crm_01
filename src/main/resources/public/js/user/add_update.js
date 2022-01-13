layui.use(['form', 'layer','formSelects'], function () {
    var form = layui.form,
        layer = parent.layer === undefined ? layui.layer : top.layer,
        $ = layui.jquery,
        // 引入 formSelects 模块
        formSelects = layui.formSelects;



    //添加或者更新用户
    form.on("submit(addOrUpdateUser)",function (data){
        //
        var url=ctx+"/user/save";

        //修改
        if($("input[name='id']").val()){
            url=ctx+"/user/update1";
        }

        //发送ajax请求
        $.post(url,data.field,function (result){
            if (result.code=200){
                parent.location.reload();
            }
            else {
                layer.msg(result.msg,{icon: 5});
            }
        },"json");


        return false;
    })


    /**
     * 加载下拉框数据
     */
        formSelects.config('selectId',{
            type:"post",
            searchUrl:ctx + "/role/queryAllRoles?userId="+$("input[name=id]").val(),
            //自定义返回数据中name的key, 默认 name
            keyName: 'roleName',
            //自定义返回数据中value的key, 默认 value
            keyVal: 'id'
        },true);


    var userId = $("input[name='id']").val();
    formSelects.config('selectId',{
        type:"post",
        searchUrl:ctx+"/role/queryAllRoles?userId="+userId,
        keyName: 'roleName',  //自定义返回数据中name的key, 默认 name
        keyVal: 'id' //自定义返回数据中value的key, 默认 value
    },true);


    /*取消*/
    $("#closeBtn").click(function(){
        //假设这是iframe页
        var index = parent.layer.getFrameIndex(window.name); //先得到当前iframe层的索引
        parent.layer.close(index); //再执行关闭
    });

});