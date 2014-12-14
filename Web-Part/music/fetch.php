<?php

	//需要上传参数(键名)：
	//userid   		-当前使用用户的身份id，游客为-1，其他用户使用数据库中的记录数据
	//weather 		-当前天气编码
	//mood  		-当前心情编码
	//behaviour 	-当前行为编码

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
		if(!(isset($_POST['userid'])&&isset($_POST['weather'])
		   &&isset($_POST['mood'])&&isset($_POST['behaviour']))){
			throw new Exception('1');
		}

		$user=intval($_POST['user']);
		$weather=intval($_POST['weather']);
		$mood=intval($_POST['mood']);
		$behaviour=intval($_POST['behaviour']);
		
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
				throw new Exception('2');
			}
			$mysqli->close();
		}

		$data=array();
		$mysqli=new mysqli($sql_server,$sql_username,$sql_password,$sql_database,$sql_port);
		$sql='select name,artist,album,class from music';
		$mysqli_query=$mysqli->prepare($sql);
		if(!$mysqli_query->execute()){
			$mysqli->close();
			throw new Exception('Database Error!');
		}
		$mysqli_query->bind_result($name,$artist,$album,$class);
		while($mysqli_query->fetch()){
			$temp['name']=urlencode($name);
			$temp['artist']=urlencode($artist);
			$temp['album']=urlencode($album);
			$temp['class']=$class;
			array_push($data, $temp);
		}
		$mysqli->close();

		$count=count($data);
		if($count==0){
			throw new Exception('Database Error!');
		}

		$index=rand(0,$count);
		$res['errcode']=0;
		$res['errmsg']='success';
		$res['music']='http://amergin-music.stor.sinaapp.com/'.$data[$index]['name'].'.mp3';
		$res['lyric']='http://amergin-music.stor.sinaapp.com/lyric/'.$data[$index]['name'].'.lrc';
		$res['name']=$data[$index]['name'];
		$res['artist']=$data[$index]['artist'];
		$res['album']=$data[$index]['album'];
		$res['class']=$data[$index]['class'];
		echo json_encode($res);
	}catch(Exception $e){
		switch($e->getMessage()){
			case '1':
				$res['errcode']=1;
				$res['errmsg']='incomplete access!';
				break;
			case '2':
				$res['errcode']=2;
				$res['errmsg']='invalid user!';
				break;
			default:
				$res['errcode']=-1;
				$res['errmsg']=$e->getMessage();
				break;
		}
		echo json_encode($res);
	}
?>