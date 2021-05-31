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

//注册协议确认
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

	//给phone绑定事件
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
			//看手机号是否注册过
			$.ajax({
				url: contextPath + "/loan/phone/register",
				data:"phone="+phone,
				dataType:"json",
				success:function(resp) {
					if(resp.code != 10000){
						showError("phone",resp.message);
					}
				},
				error:function(){
					alert("请求失败，请稍后重试");
				}
			})
		}
	});

	//密码域
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



	//验证码按钮
	$("#messageCodeBtn").on("click",function(){
		let btn = $("#messageCodeBtn");

		if( btn.hasClass("on")){
			//有on 不执行发送验证码的逻辑
			return;
		}
		//检查手机号
		$("#phone").blur();
		//判断手机号格式是否正确
		let text = $("#phoneErr").text();
		if( "" == text){ //没有错误
			btn.addClass("on");
			$.leftTime(5,function(d){
				//console.log("d.status="+d.status + "####d.s="+d.s);
				let second = parseInt(d.s);
				if( second == 0 ){
					btn.text("获取验证码");
					btn.removeClass("on");
				} else {
					btn.text(second+"秒重新获取");
				}
			})

			//发送ajax ，对手机号发送验证码。
			$.ajax({
				url: contextPath+"/loan/sendCode",
				data:"phone="+$.trim( $("#phone").val() ),
				success:function(resp){
					if(  resp.code  != 10000 ){
						alert(resp.message);
					}
				},
				error:function(){
					alert("请重新发送验证码");
				}
			});


		}
	})


	//短信验证码text
	$("#messageCode").on("blur",function(){
		var code = $.trim(  $("#messageCode").val() );
		if( "" == null){
			showError("messageCode","必须输入验证码");
		} else if( code.length != 4){
			showError("messageCode","验证码是4位数字");
		} else {
			showSuccess("messageCode");
		}
	});

	//注册按钮
	$("#btnRegist").on("click",function(){
		//调用各个对象的blur事件
		$("#phone").blur();
		$("#loginPassword").blur();
		$("#messageCode").blur();

		var text = $("[id $='Err']").text();

		if( "" == text){ //数据是正确的
			let phone = $.trim( $("#phone").val() );
			let pwd = $.trim( $("#loginPassword").val() );
			let code = $.trim( $("#messageCode").val() );
			$.ajax({
				url:contextPath + "/loan/register",
				type:"post",
				data:{
					"phone":phone,
					"pwd": $.md5(pwd),
					"code":code
				},
				dataType:"json",
				success:function(resp){
					//注册成功，调转到实名认证
					if( resp.code == 10000){
						window.location.href = contextPath + "/loan/realName";
					} else {
						alert(resp.message);
					}
				},
				error:function(){
					alert("请稍后重试");
				}
			})
		}
	})

});
