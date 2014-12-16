<?php
	
	header("Content-Type: text/html; charset=utf-8");
	$sql_database=SAE_MYSQL_DB;
	$sql_username=SAE_MYSQL_USER;
	$sql_server=SAE_MYSQL_HOST_M;
	$sql_password=SAE_MYSQL_PASS;
	$sql_port=SAE_MYSQL_PORT;

	$MINPTS=4;
	$EPS=0.5;
	$Music_Name=$_POST['Name'];
	$Music_Album=$_POST['Album'];
	$Music_Artist=$_POST['Artist'];
	$Music_Speed=$_POST['Speed'];
	$Music_Stability=$_POST['Stability'];
	$Music_Normality=$_POST['Normality'];
	$Music_Happiness=$_POST['Happiness'];
	$Music_Ease=$_POST['Ease'];
	$Music_Depression=$_POST['Depression'];
	$Music_Craziness=$_POST['Craziness'];
	$Music_Enthusiastism=$_POST['Enthusiastism'];
	$Music_Grief=$_POST['Grief'];
	$Music_Softness=$_POST['Softness'];
	$Music_Class=0;
	$Music_Neibour=0;

	$SQL_FETCH1='SELECT mid FROM music
				 WHERE neibour=?
				 AND (speed-?)*(speed-?)+(normality-?)*(normality-?)+(stability-?)*(stability-?)+
				 	 (happiness-?)*(happiness-?)+(ease-?)*(ease-?)+(depression-?)*(depression-?)+
				 	 (craziness-?)*(craziness-?)+(enthusiastism-?)*(enthusiastism-?)+(grief-?)*(grief-?)+
				 	 (softness-?)*(softness-?)<?';

	$mysqli=new mysqli($sql_server,$sql_username,$sql_password,$sql_database,$sql_port);
	$mysqli_query=$mysqli->prepare($SQL_FETCH1);
	$mysqli_query->bind_param('ii',$MINPTS,$EPS);
	$mysqli_query->execute();
	$mysqli_query->bind_result($music);
	$q0=array();
	while($mysqli_query->fetch()){
		array_push($q0, $music);
	}
	$Music_Neibour=count($q0);

    function UpdateMusic($pervious,$new){

    }

    function UpdateUser(){

    }
?>