<?php

	//需要上传参数(键名)：
	//user   -当前使用用户的身份id，游客为-1，其他用户使用数据库中的记录数据
	//music  -当前播放歌曲的识别编号
	//type   -喜欢或不喜欢，喜欢为0，不喜欢为1

	//返回值：
	//errcode     -错误码
	//errmsg      -错误信息

	//错误码：
	//0			-写入成功
	//1         -信息不完整，写入失败
	//2         -非法访问，写入失败
	//3         -已存在同类喜好，写入失败
	//4         -已存在相反喜好，写入失败
	//-1        -系统异常，写入失败

	header("Content-Type: text/html; charset=utf-8");
	$sql_database=SAE_MYSQL_DB;
	$sql_username=SAE_MYSQL_USER;
	$sql_server=SAE_MYSQL_HOST_M;
	$sql_password=SAE_MYSQL_PASS;
	$sql_port=SAE_MYSQL_PORT;
	
	$res=array();

	try{
		if(!(isset($_POST['user'])&&isset($_POST['music'])&&isset($_POST['type']))){
			throw new Exception('1');
		}

		$user=intval($_POST['user']);
		$music=intval($_POST['music']);
		$type=intval($_POST['type']);

		if($type!=1&&$typ2!=0){
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
		$mysqli_query->bind_result($userid);
		if(!$mysqli_query->fetch()){
			$mysqli->close();
			throw new Exception('2');
		}
		$mysqli->close();

		$mysqli=new mysqli($sql_server,$sql_username,$sql_password,$sql_database,$sql_port);
		$sql='SELECT mid FROM music WHERE mid=?';
		$mysqli_query=$mysqli->prepare($sql);
		$mysqli_query->bind_param('i',$music);
		if(!$mysqli_query->execute()){
			$mysqli->close();
			throw new Exception('Database Error!');
		}
		$mysqli_query->bind_result($musicid);
		if(!$mysqli_query->fetch()){
			$mysqli->close();
			throw new Exception('2');
		}
		$mysqli->close();

		$mysqli=new mysqli($sql_server,$sql_username,$sql_password,$sql_database,$sql_port);
		$sql='SELECT trend FROM preference WHERE mid=? AND uid=?';
		$mysqli_query=$mysqli->prepare($sql);
		$mysqli_query->bind_param('ii',$music,$user);
		if(!$mysqli_query->execute()){
			$mysqli->close();
			throw new Exception('Database Error!');
		}
		$mysqli_query->bind_result($trend);
		if($mysqli_query->fetch()){
			$mysqli->close();
			if($trend==$type){
				throw new Exception('3');
			}
			else{
				throw new Exception('4');	
			}
		}
		$mysqli->close();	

		$mysqli=new mysqli($sql_server,$sql_username,$sql_password,$sql_database,$sql_port);
		$sql='INSERT INTO preference VALUES(?,?,?)';
		$mysqli_query=$mysqli->prepare($sql);
		$mysqli_query->bind_param('iii',$user,$music,$type);
		if(!$mysqli_query->execute()){
			$mysqli->close();
			throw new Exception('Database Error!');
		}
		$mysqli->close();			


		$res['errcode']=0;
		$res['errmsg']='success';
		echo json_encode($res);
	}catch(Exception $e){
		switch($e->getMessage()){
			case '1':
				$res['errcode']=1;
				$res['errmsg']='incomplete information!';
				break;
			case '2':
				$res['errcode']=2;
				$res['errmsg']='invalid access!';
				break;
			case '3':
				$res['errcode']=3;
				$res['errmsg']='the same taste information already exists!';
				break;
			case '4':
				$res['errcode']=4;
				$res['errmsg']='different taste information already exists!';
				break;
			default:
				$res['errcode']=-1;
				$res['errmsg']=$e->getMessage();
				break;
		}
		echo json_encode($res);
	}
?>