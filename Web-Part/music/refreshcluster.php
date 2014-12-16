<?php
	
	header("Content-Type: text/html; charset=utf-8");
	// $sql_database=SAE_MYSQL_DB;
	// $sql_username=SAE_MYSQL_USER;
	// $sql_server=SAE_MYSQL_HOST_M;
	// $sql_password=SAE_MYSQL_PASS;
	// $sql_port=SAE_MYSQL_PORT;

	$sql_server='127.0.0.1';
	$sql_username='root';
	$sql_password='';
	$sql_database='test';

	$MINPTS=10;
	$EPS=8000;
	// $Music_Name=$_POST['Name'];
	// $Music_Album=$_POST['Album'];
	// $Music_Artist=$_POST['Artist'];
	// $Music_Speed=$_POST['Speed'];
	// $Music_Stability=$_POST['Stability'];
	// $Music_Normality=$_POST['Normality'];
	// $Music_Happiness=$_POST['Happiness'];
	// $Music_Ease=$_POST['Ease'];
	// $Music_Depression=$_POST['Depression'];
	// $Music_Craziness=$_POST['Craziness'];
	// $Music_Enthusiastism=$_POST['Enthusiastism'];
	// $Music_Grief=$_POST['Grief'];
	// $Music_Softness=$_POST['Softness'];
	$Music_Class=0;
	$Music_Neibour=0;
	$SEED=array();

	$file=fopen('./music.csv','r');
	fgetcsv($file);
	$waiting_list=array();
	while(!feof($file)){
		$temp=fgetcsv($file);
		for($i=4;$i<count($temp);$i++){
			$temp[$i]=intval($temp[$i]);
		}
		array_push($waiting_list,fgetcsv($file));
	}

	$w_num=count($waiting_list);
	for($i=0;$i<$w_num;$i++){
		$mysqli=new mysqli($sql_server,$sql_username,$sql_password,$sql_database);
		$SQL_FETCHQ0='SELECT * FROM music WHERE neibour=?-1
			  AND (pow(speed-?,2)+pow(stability-?,2)+pow(normality-?,2)+pow(happiness-?,2)+
			  	  pow(ease-?,2)+pow(depression-?,2)+pow(craziness-?,2)+pow(enthusiastism-?,2)+
			      pow(grief-?,2)+pow(softness-?,2))<=?';
		$query=$mysqli->prepare($SQL_FETCHQ0);
		$query->bind_param('iiiiiiiiiiii',$MINPTS,$waiting_list[3],$waiting_list[4],$waiting_list[5],
						   			      $waiting_list[6],$waiting_list[7],$waiting_list[8],$waiting_list[9],
						                  $waiting_list[10],$waiting_list[11],$waiting_list[12],$EPS);
		$query->execute();
		$query->bind_result($id,$name,$artist,$album,$speed,$stab,$norm,$happ,$ease,$depre,
							$crazy,$enthu,$grief,$soft,$neib,$class);
		$list_q0=array();
		while($query->fetch()){
			$temp=array();
			array_push($temp, $id);
			array_push($temp, $speed);
			array_push($temp, $stab);
			array_push($temp, $norm);
			array_push($temp, $happ);
			array_push($temp, $ease);
			array_push($temp, $depre);
			array_push($temp, $crazy);
			array_push($temp, $enthu);
			array_push($temp, $grief);
			array_push($temp, $soft);
			array_push($temp, $neib);
			array_push($temp, $class);
			array_push($list_q0, $temp);
		}
		$mysqli->close();

		$q0_cnt=count($list_q0);
		
		$mysqli=new mysqli($sql_server,$sql_username,$sql_password,$sql_database);
		$SQL_UPDATE='UPDATE music SET neibour=neibour+1
					 WHERE (pow(speed-?,2)+pow(stability-?,2)+pow(normality-?,2)+pow(happiness-?,2)+
			  	 			pow(ease-?,2)+pow(depression-?,2)+pow(craziness-?,2)+pow(enthusiastism-?,2)+
			      			pow(grief-?,2)+pow(softness-?,2))<=?';
		$query=$mysqli->prepare($SQL_UPDATE);
		$query->bind_param('iiiiiiiiiii',$waiting_list[3],$waiting_list[4],$waiting_list[5],
						   			     $waiting_list[6],$waiting_list[7],$waiting_list[8],$waiting_list[9],
						                 $waiting_list[10],$waiting_list[11],$waiting_list[12],$EPS);
		$query->execute();
		$mysqli->close();

		$UPDSEED=array();
		for($j=0;$j<$q0_cnt;$j++){
			$mysqli=new mysqli($sql_server,$sql_username,$sql_password,$sql_database);
			$SQL_FETCHQ='SELECT mid,class FROM music WHERE neibour>=?
				  		 AND (pow(speed-?,2)+pow(stability-?,2)+pow(normality-?,2)+pow(happiness-?,2)+
			  	  		 pow(ease-?,2)+pow(depression-?,2)+pow(craziness-?,2)+pow(enthusiastism-?,2)+
			      		 pow(grief-?,2)+pow(softness-?,2))<=?';
			$query=$mysqli->prepare($SQL_UPDATE);
			$query->bind_param('iiiiiiiiiiii',$MINPTS,$list_q0[1],$list_q0[2],$list_q0[3],
											  $list_q0[4],$list_q0[5],$list_q0[6],$list_q0[7],
											  $list_q0[8],$list_q0[9],$list_q0[10],$EPS);
			$query->execute();
			$query->bind_result($id,$class);
			while($query->fetch()){
				$temp=array();
				array_push($temp, $id);
				array_push($temp, $class);
				array_push($UPDSEED, $temp);
			}
			$mysqli->close();
		}

		$UPDSEED=array_unique($UPDSEED);
	}

    function UpdateMusic($pervious,$new){

    }

    function UpdateUser(){

    }
?>