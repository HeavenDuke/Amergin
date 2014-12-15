<?php

	//需要上传参数(键名)：
	//userid   		-当前使用用户的身份id，游客为-1，其他用户使用数据库中的记录数据
	//musicid 		-指定音乐的编号

	//返回值：
	//errcode     -错误码
	//errmsg      -错误信息
	//music  	  -音乐地址
	//name   	  -音乐名
	//album 	  -专辑名
	//artist 	  -艺术家
	//class   	  -音乐类型
	//lyric  	  -歌词地址
	//所有中文使用urlencode

	//错误码：
	//0			-获取成功
	//1         -信息不完整，获取失败
	//2         -信息非法，获取失败
	//-1        -系统异常，获取失败


	header("Content-Type: text/html; charset=utf-8");
	$sql_database=SAE_MYSQL_DB;
	$sql_username=SAE_MYSQL_USER;
	$sql_server=SAE_MYSQL_HOST_M;
	$sql_password=SAE_MYSQL_PASS;
	$sql_port=SAE_MYSQL_PORT;

	try{
		$res=array();
		if(!(isset($_POST['userid'])&&isset($_POST['musicid']))){
			throw new Exception('1');
		}

		$user=intval($_POST['userid']);
		$musicid=intval($_POST['musicid']);
		
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
			throw new Exception('2');
		}
		$mysqli->close();

		$mysqli=new mysqli($sql_server,$sql_username,$sql_password,$sql_database,$sql_port);
		$sql='SELECT name,artist,album,class FROM music WHERE mid=?';
		$mysqli_query=$mysqli->prepare($sql);
		$mysqli_query->bind_param('i',$musicid);
		if(!$mysqli_query->execute()){
			$mysqli->close();
			throw new Exception('Database Error!');
		}
		$mysqli_query->bind_result($name,$artist,$album,$class);
		if($mysqli_query->fetch()){
			$res['name']=urlencode($name);
			$res['artist']=urlencode($artist);
			$res['album']=urlencode($album);
			$res['class']=$class;
		}
		else{
			$mysqli->close();
			throw new Exception('2');
		}
		$mysqli->close();
		$res['errcode']=0;
		$res['errmsg']='success';
		$res['music']='http://amergin-music.stor.sinaapp.com/'.$res['name'].'.mp3';
		$res['lyric']='http://amergin-music.stor.sinaapp.com/lyric/'.$res['name'].'.lrc';
		echo str_replace("\\/", "/",  json_encode($res));
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
			default:
				$res['errcode']=-1;
				$res['errmsg']=$e->getMessage();
				break;
		}
		echo json_encode($res);
	}
?>