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
	$SEED=array();

	$SQL_FETCH1='SELECT mid,speed,normality,stability,happiness,
						ease,depression,craziness,enthusiastism,
						grief,softness,class
				 FROM music
				 WHERE neibour=?-1
				 AND ((speed-?)*(speed-?)+(normality-?)*(normality-?)+(stability-?)*(stability-?)+
				 	 (happiness-?)*(happiness-?)+(ease-?)*(ease-?)+(depression-?)*(depression-?)+
				 	 (craziness-?)*(craziness-?)+(enthusiastism-?)*(enthusiastism-?)+(grief-?)*(grief-?)+
				 	 (softness-?)*(softness-?))<?';

	$SQL_INSERTP='INSERT INTO music(name,artist,album,speed,stability,normality,
									happiness,ease,depression,craziness,enthusiastism,
									grief,softness,class,neibour) 
				  VALUES(?,?,?,?,?,?,?,?,?,?,?,?,?,0,?)';

	$SQL_FINDPID='SELECT mid FROM music 
				  WHERE name=? AND artist=? AND album=?';

	$SQL_UPDATE1='UPDATE music SET neibour=neibour+1
				  WHERE ((speed-?)*(speed-?)+(normality-?)*(normality-?)+(stability-?)*(stability-?)+
				 	 	(happiness-?)*(happiness-?)+(ease-?)*(ease-?)+(depression-?)*(depression-?)+
				 	 	(craziness-?)*(craziness-?)+(enthusiastism-?)*(enthusiastism-?)+(grief-?)*(grief-?)+
				 	 	(softness-?)*(softness-?))<?';

	$mysqli=new mysqli($sql_server,$sql_username,$sql_password,$sql_database,$sql_port);
	$mysqli_query=$mysqli->prepare($SQL_FETCH1);
	$mysqli_query->bind_param('iiiiiiiiiiii',$MINPTS,$Music_Speed,$Music_Stability,
								   			 $Music_Normality,$Music_Happiness,$Music_Ease,
								   			 $Music_Depression,$Music_Craziness,$Music_Enthusiastism,
								   			 $Music_Grief,$Music_Softness,$EPS);
	$mysqli_query->execute();
	$mysqli_query->bind_result($mid,$speed,$stability,$normality,
								$happiness,$ease,$depression,$craziness,
								$enthusiastism,$grief,$softness,$class);
	$q0=array();
	while($mysqli_query->fetch()){
		$temp['id']=$mid;
		$temp['speed']=$speed;
		$temp['stability']=$stability;
		$temp['normality']=$normality;
		$temp['happiness']=$happiness;
		$temp['ease']=$ease;
		$temp['depression']=$depression;
		$temp['craziness']=$craziness;
		$temp['enthusiastism']=$enthusiastism;
		$temp['grief']=$grief;
		$temp['softness']=$softness;
		$temp['class']=$class;
		array_push($q0, $temp);
	}
	$mysqli->close();

	$Music_Neibour=count($q0);
	
	$mysqli=new mysqli($sql_server,$sql_username,$sql_password,$sql_database,$sql_port);
	$mysqli_query=$mysqli->prepare($SQL_FETCH1);
	$mysqli_query->bind_param('sssiiiiiiiiiii',$Music_Name,$Music_Artist,$Music_Album,
											   $Music_Speed,$Music_Stability,$Music_Normality,
											   $Music_Happiness,$Music_Ease,$Music_Depression,
											   $Music_Craziness,$Music_Enthusiastism,$Music_Grief,
											   $Music_Softness,$Music_Neibour);
	$mysqli_query->execute();
	$mysqli->close();

	$mysqli=new mysqli($sql_server,$sql_username,$sql_password,$sql_database,$sql_port);
	$mysqli_query=$mysqli->prepare($SQL_FINDPID);
	$mysqli_query->bind_param('sss',$Music_Name,$Music_Artist,$Music_Album);
	$mysqli_query->execute();
	$mysqli_query->bind_result($MUSIC_PID);
	$mysqli_QUERY->fetch();
	$mysqli->close();

	$mysqli=new mysqli($sql_server,$sql_username,$sql_password,$sql_database,$sql_port);
	$mysqli_query=$mysqli->prepare($SQL_UPDATE1);
	$mysqli_query->bind_param('iiiiiiiiii',$Music_Speed,$Music_Stability,
								   		   $Music_Normality,$Music_Happiness,$Music_Ease,
								   		   $Music_Depression,$Music_Craziness,$Music_Enthusiastism,
								   		   $Music_Grief,$Music_Softness);
	$mysqli_query->execute();
	$mysqli->close();

	$SQL_FETCHQS='SELECT mid,class FROM music
				  WHERE neibour>=?
				  AND ((speed-?)*(speed-?)+(normality-?)*(normality-?)+(stability-?)*(stability-?)+
				 	  (happiness-?)*(happiness-?)+(ease-?)*(ease-?)+(depression-?)*(depression-?)+
				 	  (craziness-?)*(craziness-?)+(enthusiastism-?)*(enthusiastism-?)+(grief-?)*(grief-?)+
				 	  (softness-?)*(softness-?))<?';

	for($i=0;$i<$Music_Neibour;$i++){
		$MUSIC_QID=$q0[$i];
		$mysqli=new mysqli($sql_server,$sql_username,$sql_password,$sql_database,$sql_port);
		$mysqli_query=$mysqli->prepare($SQL_FINDPID);
		$mysqli_query->bind_param('iiiiiiiiiiiiiiiiiiiiiiii',);
		$mysqli_query->execute();
		$mysqli_query->bind_result($MUSIC_PID);
		$mysqli_QUERY->fetch();
		$mysqli->close();
	}

    function UpdateMusic($pervious,$new){

    }

    function UpdateUser(){

    }
?>