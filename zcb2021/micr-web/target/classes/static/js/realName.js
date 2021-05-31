
//同意实名认证协议
$(function() {
	$("#agree").click(function(){
		var ischeck = document.getElementById("agree").checked;
		if (ischeck) {
			$("#btnRegist").attr("disabled", false);
			$("#btnRegist").removeClass("fail");
		} else {
			$("#btnRegist").attr("disabled","disabled");
			$("#btnRegist").addClass("fail");
		}
	});

	// 姓名的数据处理
    $("#realName").on("blur",function(){
    	var name = $.trim( $("#realName").val() );
    	if( "" == name){
    		showError("realName","必须输入姓名值");
		} else if( name.length < 2 ){
			showError("realName","姓名至少是2个字");
		} else if( !/^[\u4e00-\u9fa5]{0,}$/.test(name)){
    		showError("realName","姓名必须是中文的");
		} else {
    		showSuccess("realName");
		}

	})


	// idcard的处理
	$("#idCard").on("blur",function(){
		var card = $.trim( $("#idCard").val() );
		if( "" == card){
			showError("idCard","必须输入身份证号");
	 	} else if( !/(^\d{15}$)|(^\d{18}$)|(^\d{17}(\d|X|x)$)/.test(card)){
			showError("idCard","身份证格式不正确");
		} else {
			showSuccess("idCard");
		}
	})


	// 按钮的事件
	$("#btnRegist").on("click",function(){
		$("#realName").blur();
		$("#idCard").blur();

		let text = $("div[id $= 'Err']").text();
		if("" == text){
			//发起ajax请求
			$.ajax({
				url: contextPath + "/loan/realName",
				type:"post",
				data:{
					"name": $.trim( $("#realName").val() ),
					"card": $.trim( $("#idCard").val() ),
					"phone": $.trim( $("#phone").val() )
				},
				dataType:"json",
				success:function(resp){
					//调整到用户中心页面
					if( resp.code == 10000){
						window.location.href=contextPath + "/loan/myCenter";
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
});
//打开注册协议弹层
function alertBox(maskid,bosid){
	$("#"+maskid).show();
	$("#"+bosid).show();
}
//关闭注册协议弹层
function closeBox(maskid,bosid){
	$("#"+maskid).hide();
	$("#"+bosid).hide();
}

//错误提示
function showError(id,msg) {
	$("#"+id+"Ok").hide();
	$("#"+id+"Err").html("<i></i><p>"+msg+"</p>");
	$("#"+id+"Err").show();
	$("#"+id).addClass("input-red");
}
//错误隐藏
function hideError(id) {
	$("#"+id+"Err").hide();
	$("#"+id+"Err").html("");
	$("#"+id).removeClass("input-red");
}
//显示成功
function showSuccess(id) {
	$("#"+id+"Err").hide();
	$("#"+id+"Err").html("");
	$("#"+id+"Ok").show();
	$("#"+id).removeClass("input-red");
}