<?php

	//需要上传参数(键名)：
	//user      -当前使用用户的身份id，游客为-1，其他用户使用数据库中的记录数据
	//type      -希望获取是喜欢还是不喜欢的列表，0为喜欢，1为不喜欢

	//返回值：
	//errcode     -错误码
	//errmsg      -错误信息
	//list        -数据对应list

	//错误码：
	//0			-登录成功
	//1         -信息不完整，获取失败
	//2         -用户为游客，获取失败，提示登录
	//3         -非法用户访问，获取失败
	//-1        -系统异常，获取失败

	header("Content-Type: text/html; charset=utf-8");
	$sql_database=SAE_MYSQL_DB;
	$sql_username=SAE_MYSQL_USER;
	$sql_server=SAE_MYSQL_HOST_M;
	$sql_password=SAE_MYSQL_PASS;
	$sql_port=SAE_MYSQL_PORT;
	
	$res=array();

	try{
		if(!(isset($_POST['user'])&&isset($_POST['type']))){
			throw new Exception('1');
		}

		$user=intval($_POST['user']);
		$type=intval($_POST['type']);
		$list=array();

		if($user==-1){
			throw new Exception('2');
		}

		$mysqli=new mysqli($sql_server,$sql_username,$sql_password,$sql_database,$sql_port);
		$sql='SELECT uid FROM user WHERE uid=?';
		$mysqli_query=$mysqli->prepare($sql);
		$mysqli_query->bind_param('i',$user);
		if(!$mysqli_query->execute()){
			$mysqli->close();
			throw new Exception('Database Error!');
		}
		$mysqli_query->bind_result($test);
		if(!$mysqli_query->fetch()){
			$mysqli->close();
			throw new Exception('3');
		}
		$mysqli->close();

		$mysqli=new mysqli($sql_server,$sql_username,$sql_password,$sql_database,$sql_port);
		$sql='SELECT music.name
			  FROM preference,music
			  WHERE perference.mid=music.mid
			  AND preference.uid=?
			  AND preference.trend=?';
		$mysqli_query=$mysqli->prepare($sql);
		$mysqli_query->bind_param('ii',$user,$type);
		if(!$mysqli_query->execute()){
			$mysqli->close();
			throw new Exception('Database Error!');
		}
		$mysqli_query->bind_result($name);
		while($mysqli_query->fetch())
			array_push($list, $name);
		}
		$mysqli->close();

		$res['errcode']=0;
		$res['errmsg']='success';
		$res['list']=$list;
		echo json_encode($res);
	}catch(Exception $e){
		switch($e->getMessage()){
			case '1':
				$res['errcode']=1;
				$res['errmsg']='incomplete information!';
				break;
			case '2':
				$res['errcode']=2;
				$res['errmsg']='please login in order to have this list';
				break;
			case '3':
				$res['errcode']=3;
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