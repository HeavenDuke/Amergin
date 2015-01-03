<?php

	//需要上传参数(键名)：
	//userid   		-当前使用用户的身份id，游客为-1，其他用户使用数据库中的记录数据
	//weather 		-当前天气编码
	//mood  		-当前心情编码
	//behaviour 	-当前行为编码

	//返回值:
	//errcode     -错误码
	//errmsg      -错误信息
	//mid         -音乐编码
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

		$user=intval($_POST['userid']);
		$weather=intval($_POST['weather']);
		$mood=intval($_POST['mood']);
		$behaviour=intval($_POST['behaviour']);

		if($user!=-1){
			$mysqli=new mysqli($sql_server,$sql_username,$sql_password,$sql_database,$sql_port);
			$sql='select uid,sex,age from user where uid=?';
			$mysqli_query=$mysqli->prepare($sql);
			$mysqli_query->bind_param('i',$user);
			if(!$mysqli_query->execute()){
				$mysqli->close();
				throw new Exception('Database Error!');
			}
			$mysqli_query->bind_result($user,$sex,$age);
			if(!$mysqli_query->fetch()){
				$mysqli->close();
				throw new Exception('2');
			}
			$mysqli->close();
		}
		else{
			$sex=1;
			$age=30;
		}

		$age=ParseAge($age);
		$season=ParseDate();
		$period=ParseTime();

		$classes=GetClasses($mood,$behaviour);
		// echo json_encode($classes);

		// echo $age.'<br>';
		// echo $season.'<br>';
		// echo $period.'<br>';
		// echo $age.'<br>';
		// echo $sex.'<br>';
		// echo $weather.'<br>';
		// echo $mood.'<br>';
		// echo $behaviour.'<br>';

		$data=array();
		$mysqli=new mysqli($sql_server,$sql_username,$sql_password,$sql_database,$sql_port);
		$sql='SELECT mid,name,artist,album,class FROM music where class IN(';
        foreach($classes as $key => $value){
        	$sql.=$value.',';
        }
        $sql[strlen($sql)-1]=')';
		// echo $sql;
		$mysqli_query=$mysqli->prepare($sql);
		if(!$mysqli_query->execute()){
			$mysqli->close();
			throw new Exception('Database Error!');
		}
		$mysqli_query->bind_result($id,$name,$artist,$album,$class);
		while($mysqli_query->fetch()){
			$temp['mid']=$id;
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

		$index=rand(0,$count-1);
		$res['errcode']=0;
		$res['errmsg']='success';
		$res['music']='http://amergin-music.stor.sinaapp.com/'.$data[$index]['name'].'.mp3';
		$res['lyric']='http://amergin-music.stor.sinaapp.com/lyric/'.$data[$index]['name'].'.lrc';
		$res['mid']=$data[$index]['mid'];
		$res['name']=$data[$index]['name'];
		$res['artist']=$data[$index]['artist'];
		$res['album']=$data[$index]['album'];
		$res['class']=$data[$index]['class'];
		echo str_replace("\\/", "/",  json_encode($res));
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

	function ParseTime(){
		$res=0;
		$hour=intval(date('H',time()));
		$minute=intval(date('i',time()));
		$second=intval(date('s',time()));
		if(Time2Number($hour,$minute,$second)<Time2Number(6,30,0)){
			$res=7;
		}
		else if(Time2Number($hour,$minute,$second)<Time2Number(8,0,0)){
			$res=1;
		}
		else if(Time2Number($hour,$minute,$second)<Time2Number(11,30,0)){
			$res=2;
		}
		else if(Time2Number($hour,$minute,$second)<Time2Number(13,30,0)){
			$res=3;
		}
		else if(Time2Number($hour,$minute,$second)<Time2Number(16,30,0)){
			$res=4;
		}
		else if(Time2Number($hour,$minute,$second)<Time2Number(18,0,0)){
			$res=5;
		}
		else if(Time2Number($hour,$minute,$second)<Time2Number(21,00,0)){
			$res=6;
		}
		else{
			$res=7;
		}
		return $res;
	}

	function Time2Number($hour,$minute,$second){
		$res=0;
		$res+=$hour*3600;
		$res+=$minute*60;
		$res+=$second;
		return $res;
	}

	function ParseDate(){
		$res=0;
		$month=date('m',time());
		$month=intval($month);
		if($month==3||$month==4||$month==5){
			$res=1;
		}
		else if($month==6||$month==7||$month==8){
			$res=2;
		}
		else if($month==9||$month==10||$month==11){
			$res=3;
		}
		else{
			$res=4;
		}
		return $res;
	}

	function ParseAge($age){
		$res=0;
		if($age<=6){
			$res=1;
		}
		else if($age<=14){
			$res=2;
		}
		else if($age<=25){
			$res=3;
		}
		else if($age<=40){
			$res=4;
		}
		else if($age<=60){
			$res=5;
		}
		else{
			$res=6;
		}
		return $res;
	}

	function GetClasses($mood,$behaviour){
		$res=array();
		$xml=simplexml_load_file('../General.xml');
		$moods=$xml->children();
		$behaviours=$moods[$mood-1]->children();
		if($behaviours->getName()=='music'){
			$count=count($behaviours);
			for($i=0;$i<$count;$i++){
                $node=$behaviours[$i]->attributes();
				array_push($res, $node['tvalue']);
			}
		}
		else{
			$music=$behaviours[$behaviour-1]->children();
			$count=count($music);
			for($i=0;$i<$count;$i++){
				$node=$music[$i]->attributes();
				array_push($res, $node['tvalue']);
			}
		}
        return $res;
	}
?>