<?php

	//需要上传参数(键名)：
	//username   -用户的登录用户名
	//password   -用户的登录密码

	//返回值：
	//errcode     -错误码
	//errmsg      -错误信息
	//userid      -用户身份id

	//错误码：
	//0			-登录成功
	//1         -用户信息错误，登录失败
	//-1        -系统异常，获取失败

	header("Content-Type: text/html; charset=utf-8");
	$sql_database=SAE_MYSQL_DB;
	$sql_username=SAE_MYSQL_USER;
	$sql_server=SAE_MYSQL_HOST_M;
	$sql_password=SAE_MYSQL_PASS;
	$sql_port=SAE_MYSQL_PORT;
	
	$res=array();

	try{
		if(!(isset($_POST['username'])&&isset($_POST['password']))){
			throw new Exception('1');
		}

		$user=$_POST['user'];
		$user=$_POST['password'];
		$mysqli=new mysqli($sql_server,$sql_username,$sql_password,$sql_database,$sql_port);
		$sql='SELECT uid FROM user WHERE username=? AND password=? LIMIT 1';
		$mysqli_query=$mysqli->prepare($sql);
		$mysqli_query->bind_param('ss',$username,md5($password));
		if(!$mysqli_query->execute()){
			$mysqli->close();
			throw new Exception('Database Error!');
		}
		$mysqli_query->bind_result($userid);
		if(!$mysqli_query->fetch()){
			$mysqli->close();
			throw new Exception('1');
		}
		$mysqli->close();

		$res['errcode']=0;
		$res['errmsg']='success';
		$res['userid']=$userid;
		echo json_encode($res);
	}catch(Exception $e){
		switch($e->getMessage()){
			case '1':
				$res['errcode']=1;
				$res['errmsg']='invalid access!';
				break;
			default:
				$res['errcode']=-1;
				$res['errmsg']=$e->getMessage();
				break;
		}
		echo json_encode($res);
	}
?>