<?php

	//需要上传参数(键名)：
	//user   -当前使用用户的身份id，游客为-1，其他用户使用数据库中的记录数据
	//class  -当前播放歌曲的类别，基于该类别获取背景图片

	//返回值：
	//errcode     -错误码
	//errmsg      -错误信息
	//background  -背景图片地址

	//错误码：
	//0			-获取成功
	//1         -非法访问（用户信息不完整或不正确），获取失败
	//2         -数据不存在，获取失败
	//-1        -系统异常，获取失败


	header("Content-Type: text/html; charset=utf-8");
	$sql_database=SAE_MYSQL_DB;
	$sql_username=SAE_MYSQL_USER;
	$sql_server=SAE_MYSQL_HOST_M;
	$sql_password=SAE_MYSQL_PASS;
	$sql_port=SAE_MYSQL_PORT;
	
	$res=array();

	try{
		if(!(isset($_POST['user'])&&isset($_POST['class']))){
			throw new Exception('1');
		}

		$user=intval($_POST['user']);
		$class=intval($_POST['class']);
		$data=array();
		if($user!=-1){
			$mysqli=new mysqli($sql_server,$sql_username,$sql_password,$sql_database,$sql_port);
			$sql='select uid from user where uid=?';
			$mysqli_query=$mysqli->prepare($sql);
			$mysqli_query->bind_param('i',$user);
			if(!$mysqli_query->execute()){
				$mysqli->close();
				throw new Exception('Database Error!');
			}
			$mysqli_query->bind_result($user);
			if(!$mysqli_query->fetch()){
				$mysqli->close();
				throw new Exception('1');
			}
			$mysqli->close();
		}

		$mysqli=new mysqli($sql_server,$sql_username,$sql_password,$sql_database,$sql_port);
		$sql='select name from mainpicture where type=?';
		$mysqli_query=$mysqli->prepare($sql);
		$mysqli_query->bind_param('i',$class);
		if(!$mysqli_query->execute()){
			$mysqli->close();
			throw new Exception('Database Error!');
		}
		$mysqli_query->bind_result($name);
		while($mysqli_query->fetch()){
			array_push($data, $name);
		}
		$mysqli->close();

		$count=count($data);
		if($count==0){
			throw new Exception('2');
		}
		$index=rand(0,$count);
		$res['errcode']=0;
		$res['errmsg']='success';
		$res['background']='http://amergin-picture.stor.sinaapp.com/main/'.$data[$index].'.jpg';
		echo json_encode($res);
	}catch(Exception $e){
		switch($e->getMessage()){
			case '1':
				$res['errcode']=1;
				$res['errmsg']='invalid access!';
				break;
			case '2':
				$res['errcode']=2;
				$res['errmsg']='currently we are short of picture for this type:(';
				break;
			default:
				$res['errcode']=-1;
				$res['errmsg']=$e->getMessage();
				break;
		}
		echo json_encode($res);
	}
?>