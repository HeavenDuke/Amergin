<?php
	header("Content-Type: text/html; charset=utf-8");
	$sql_database=SAE_MYSQL_DB;
	$sql_username=SAE_MYSQL_USER;
	$sql_server=SAE_MYSQL_HOST_M;
	$sql_password=SAE_MYSQL_PASS;
	$sql_port=SAE_MYSQL_PORT;
	$mysqli=new mysqli($sql_server,$sql_username,$sql_password,$sql_database,$sql_port);
	$sql='select max(picture_id) from main_picture';
	$mysqli_query=$mysqli->prepare($sql);
	$mysqli_query->execute();
	$mysqli_query->bind_result($max);
	$mysqli_query->fetch();
	$pid=rand(1,$max);
	$mysqli->close();


	$mysqli=new mysqli($sql_server,$sql_username,$sql_password,$sql_database,$sql_port);
	$sql='select picture_name from main_picture where picture_id=?';
	$mysqli_query=$mysqli->prepare($sql);
	$mysqli_query->bind_param('i',$pid);
	$mysqli_query->execute();
	$mysqli_query->bind_result($name);
	if($mysqli_query->fetch()){
		echo 'http://amergin-picture.stor.sinaapp.com/main/'.$name.'.jpg';
	}
	else{
		echo 'error!';
	}
	$mysqli->close();
?>