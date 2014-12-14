<?php

	//需要上传参数(键名)：
	//username   -用户的登录用户名
	//password   -用户的登录密码
	//email      -用户的邮箱
	//sex        -用户的性别，0为男，1为女
	//age        -用户的年龄

	//返回值：
	//errcode     -错误码
	//errmsg      -错误信息
	//userid      -用户身份id

	//错误码：
	//0			-注册成功
	//1         -用户名已存在，注册失败
	//2         -用户邮箱已存在，注册失败
	//3         -用户邮箱与用户名均已存在，注册失败
	//4         -输入信息不完整，注册失败
	//-1        -系统异常，注册失败

	header("Content-Type: text/html; charset=utf-8");
	$sql_database=SAE_MYSQL_DB;
	$sql_username=SAE_MYSQL_USER;
	$sql_server=SAE_MYSQL_HOST_M;
	$sql_password=SAE_MYSQL_PASS;
	$sql_port=SAE_MYSQL_PORT;
	
	try{
		$res=array();
		if(!(isset($_POST['username'])&&isset($_POST['password'])&&isset($_POST['email']))){
			throw new Exception('4');
		}

		$username=$_POST['username'];
		$password=$_POST['password'];
		$email=$_POST['email'];
		if(isset($_POST['sex'])){
			$sex=intval($_POST['sex']);
		}
		else{
			$sex=0;
		}
		if(isset($_POST['age'])){
			$age=intval($_POST['age']);
		}
		else{
			$age=18;
		}

		$errcode=0;
		$mysqli=new mysqli($sql_server,$sql_username,$sql_password,$sql_database,$sql_port);
		$sql='SELECT uid FROM user WHERE username=? LIMIT 1';
		$mysqli_query=$mysqli->prepare($sql);
		$mysqli_query->bind_param('s',$username);
		if(!$mysqli_query->execute()){
			$mysqli->close();
			throw new Exception('Database Error!');
		}
		$mysqli_query->bind_result($userid);
		if($mysqli_query->fetch()){
			$errcode+=1;
		}
		$mysqli->close();

		$mysqli=new mysqli($sql_server,$sql_username,$sql_password,$sql_database,$sql_port);
		$sql='SELECT uid FROM user WHERE email=? LIMIT 1';
		$mysqli_query=$mysqli->prepare($sql);
		$mysqli_query->bind_param('s',$email);
		if(!$mysqli_query->execute()){
			$mysqli->close();
			throw new Exception('Database Error!');
		}
		$mysqli_query->bind_result($userid);
		if($mysqli_query->fetch()){
			$errcode+=2;
		}
		$mysqli->close();

		if($errcode!=0){
			throw new Exception($errcode);
		}

		$mysqli=new mysqli($sql_server,$sql_username,$sql_password,$sql_database,$sql_port);
		$sql='INSERT INTO user(username,password,email,tendency,age,sex) VALUES(?,?,?,?,?,?)';
		$mysqli_query=$mysqli->prepare($sql);
		$mysqli_query->bind_param('ssssii',$username,md5($password),$email,$username.md5($password),$age,$sex);
		if(!$mysqli_query->execute()){
			$mysqli->close();
			throw new Exception('Database Error!');
		}
		$mysqli->close();

		$mysqli=new mysqli($sql_server,$sql_username,$sql_password,$sql_database,$sql_port);
		$sql='SELECT uid FROM user WHERE username=? LIMIT 1';
		$mysqli_query=$mysqli->prepare($sql);
		$mysqli_query->bind_param('s',$username);
		if(!$mysqli_query->execute()){
			$mysqli->close();
			throw new Exception('Database Error!');
		}
		$mysqli_query->bind_result($userid);
		if(!$mysqli_query->fetch()){
			$mysqli->close();
			throw new Exception('Database Error!');
		}
		$mysqli->close();

		$res['errcode']=0;
		$res['errmsg']='success';
		$res['userid']=$userid;
		echo json_encode($res);
	}catch(Exception $e){
		$res=array();
		switch($e->getMessage()){
			case '1':
				$res['errcode']=1;
				$res['errmsg']='username already exist!';
				break;
			case '2':
				$res['errcode']=2;
				$res['errmsg']='email already exist!';
				break;
			case '3':
				$res['errcode']=3;
				$res['errmsg']='username and email already exist!';
				break;
			case '4':
				$res['errcode']=4;
				$res['errmsg']='incomplete information!';
				break;
			default:
				$res['errcode']=-1;
				$res['errmsg']=$e->getMessage();
				break;
		}
		echo json_encode($res);
	}
?>