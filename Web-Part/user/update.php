<?php

	//需要上传参数(键名)：
	//userid     -用户的身份id
	//sex        -用户的性别，0为男，1为女
	//age        -用户的年龄

	//返回值：
	//errcode     -错误码
	//errmsg      -错误信息

	//错误码：
	//0			-注册成功
	//1         -数据不完整，修改失败
	//2         -id不合法，修改失败
	//-1        -系统异常，修改失败

	header("Content-Type: text/html; charset=utf-8");
	$sql_database=SAE_MYSQL_DB;
	$sql_username=SAE_MYSQL_USER;
	$sql_server=SAE_MYSQL_HOST_M;
	$sql_password=SAE_MYSQL_PASS;
	$sql_port=SAE_MYSQL_PORT;
	
	try{
		$res=array();
		if(!(isset($_POST['userid'])){
			throw new Exception('1');
		}

		$user=$_POST['userid'];

		$mysqli=new mysqli($sql_server,$sql_username,$sql_password,$sql_database,$sql_port);
		$sql='SELECT age,sex FROM user WHERE uid=? LIMIT 1';
		$mysqli_query=$mysqli->prepare($sql);
		$mysqli_query->bind_param('i',$user);
		if(!$mysqli_query->execute()){
			$mysqli->close();
			throw new Exception('Database Error!');
		}
		$mysqli_query->bind_result($age,$sex);
		if(!$mysqli_query->fetch()){
			$mysqli->close();
			throw new Exception('2');
		}
		$mysqli->close();

		if(isset($_POST['sex'])){
			$sex=intval($_POST['sex']);
		}
		if(isset($_POST['age'])){
			$age=intval($_POST['age']);
		}

		$mysqli=new mysqli($sql_server,$sql_username,$sql_password,$sql_database,$sql_port);
		$sql='UPDATE user SET age=?,sex=? WHERE uid=?';
		$mysqli_query=$mysqli->prepare($sql);
		$mysqli_query->bind_param('iii',$age,$sex,$user);
		if(!$mysqli_query->execute()){
			$mysqli->close();
			throw new Exception('Database Error!');
		}
		$mysqli->close();

		$res['errcode']=0;
		$res['errmsg']='success';
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
			default:
				$res['errcode']=-1;
				$res['errmsg']=$e->getMessage();
				break;
		}
		echo json_encode($res);
	}
?>