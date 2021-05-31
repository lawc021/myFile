$(function(){


	//手机号
    $("#phone").on("blur",function(){
        var phone = $.trim( $("#phone").val() );
        if( "" == phone ){
            showError("phone","必须输入手机号");
        } else if( phone.length != 11 ){
            showError("phone","手机号是11位的")
        } else if(  !/^1[1-9]\d{9}$/.test(phone) ){
            showError("phone","手机号格式不正确");
        } else {
            showSuccess("phone");
        }
    })

	//密码
    $("#loginPassword").on("blur",function(){
        var pwd = $.trim( $("#loginPassword").val() );
        if( "" == pwd){
            showError("loginPassword","必须输入密码");
        } else if( !/^[0-9a-zA-Z]+$/.test(pwd)){
            showError("loginPassword","密码可使用数字和英文字母")
        } else if( !/^(([a-zA-Z]+[0-9]+)|([0-9]+[a-zA-Z]+))[a-zA-Z0-9]*/.test(pwd)){
            showError("loginPassword","密码必须包含数字和英文字母")
        } else if( pwd.length < 6 || pwd.length >20){
            showError("loginPassword","密码是6到20位")
        }
        else {
            showSuccess("loginPassword");
        }
    })


	//登录按钮
    $("#loginId").on("click",function(){
        $("#phone").blur();
        $("#loginPassword").blur();

        var text = $("div[ id$='Err']").text();
        if( "" == text){
            var phone = $.trim( $("#phone").val() );
            var pwd = $.trim( $("#loginPassword").val() );

            $.ajax({
                url: contextPath + "/loan/login",
                type:"post",
                data:{
                    "phone":phone,
                    "pwd": $.md5(pwd)
                },
                dataType:"json",
                success:function(resp){
                    //回到原来的页面
                    if( resp.code  == 10000 ){
                        window.location.href = $("#returnUrl").val();
                    } else {
                        alert(resp.message);
                    }
                },
                error:function(){
                    alert("请求失败，稍后重试");
                }
            })

        }
    })

    //给整个页面添加键盘按下的事件
    $(window).keydown(function (e) {
        //如果按的是回车键，发送请求
        if(e.keyCode==13){
            $("#loginId").click();

        }
    });

})




//错误提示
function showError(id,msg) {
    $("#"+id+"Ok").hide();
    $("#"+id+"Err").html("<i></i><p>"+msg+"</p>");
    $("#"+id+"Err").show();
    $("#"+id).addClass("input-red");
}
//显示成功
function showSuccess(id) {
    $("#"+id+"Err").hide();
    $("#"+id+"Err").html("");
    $("#"+id+"Ok").show();
    $("#"+id).removeClass("input-red");
}