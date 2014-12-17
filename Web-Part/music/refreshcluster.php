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
	$EPS=15000;
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

	$file=fopen('./musicdata.csv','r');
	fgetcsv($file);
	$waiting_list=array();
	while(!feof($file)){
		$temp=fgetcsv($file);
		for($i=4;$i<count($temp);$i++){
			$temp[$i]=intval($temp[$i]);
		}
		array_push($waiting_list,$temp);
	}

	$w_num=count($waiting_list);
	for($i=0;$i<$w_num;$i++){
		$mysqli=new mysqli($sql_server,$sql_username,$sql_password,$sql_database);
		$SQL_FETCHQ0='SELECT * FROM music WHERE neibour=?-1
			  AND (pow(speed-?,2)+pow(stability-?,2)+pow(normality-?,2)+pow(happiness-?,2)+
			  	  pow(ease-?,2)+pow(depression-?,2)+pow(craziness-?,2)+pow(enthusiastism-?,2)+
			      pow(grief-?,2)+pow(softness-?,2))<=?';
		$query=$mysqli->prepare($SQL_FETCHQ0);
		$query->bind_param('iiiiiiiiiiii',$MINPTS,$waiting_list[$i][3],$waiting_list[$i][4],$waiting_list[$i][5],
						   			      $waiting_list[$i][6],$waiting_list[$i][7],$waiting_list[$i][8],$waiting_list[$i][9],
						                  $waiting_list[$i][10],$waiting_list[$i][11],$waiting_list[$i][12],$EPS);
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
		$query->bind_param('iiiiiiiiiii',$waiting_list[$i][3],$waiting_list[$i][4],$waiting_list[$i][5],
						   			     $waiting_list[$i][6],$waiting_list[$i][7],$waiting_list[$i][8],$waiting_list[$i][9],
						                 $waiting_list[$i][10],$waiting_list[$i][11],$waiting_list[$i][12],$EPS);
		$query->execute();
		$mysqli->close();

		$UPDSEED=array();
		for($j=0;$j<$q0_cnt;$j++){
			$mysqli=new mysqli($sql_server,$sql_username,$sql_password,$sql_database);
			$SQL_FETCHQ='SELECT mid,class FROM music WHERE neibour>=?
				  		 AND (pow(speed-?,2)+pow(stability-?,2)+pow(normality-?,2)+pow(happiness-?,2)+
			  	  		 pow(ease-?,2)+pow(depression-?,2)+pow(craziness-?,2)+pow(enthusiastism-?,2)+
			      		 pow(grief-?,2)+pow(softness-?,2))<=?';
			$query=$mysqli->prepare($SQL_FETCHQ);
			$query->bind_param('iiiiiiiiiiii',$MINPTS,$list_q0[$j][1],$list_q0[$j][2],$list_q0[$j][3],
											  $list_q0[$j][4],$list_q0[$j][5],$list_q0[$j][6],$list_q0[$j][7],
											  $list_q0[$j][8],$list_q0[$j][9],$list_q0[$j][10],$EPS);
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

		$mysqli=new mysqli($sql_server,$sql_username,$sql_password,$sql_database);
		$SQL_FETCHPN='SELECT count(*) FROM music
			  		  WHERE (pow(speed-?,2)+pow(stability-?,2)+pow(normality-?,2)+pow(happiness-?,2)+
			  	  			 pow(ease-?,2)+pow(depression-?,2)+pow(craziness-?,2)+pow(enthusiastism-?,2)+
			      			 pow(grief-?,2)+pow(softness-?,2))<=?';
		$query=$mysqli->prepare($SQL_FETCHPN);
		$query->bind_param('iiiiiiiiiii',$waiting_list[$i][3],$waiting_list[$i][4],$waiting_list[$i][5],
						   			     $waiting_list[$i][6],$waiting_list[$i][7],$waiting_list[$i][8],$waiting_list[$i][9],
						                 $waiting_list[$i][10],$waiting_list[$i][11],$waiting_list[$i][12],$EPS);
		$query->execute();
		$query->bind_result($count_neibour);
		$query->fetch();
		$mysqli->close();

		$mysqli=new mysqli($sql_server,$sql_username,$sql_password,$sql_database);
		$SQL_INSERTP='INSERT INTO music(name,artist,album,speed,stability,normality,happiness,ease,
								  depression,craziness,enthusiastism,grief,softness,neibour,class)
							 VALUES(?,?,?,?,?,?,?,?,?,?,?,?,?,?,0)';
		$query=$mysqli->prepare($SQL_INSERTP);
		$query->bind_param('sssiiiiiiiiiii',$waiting_list[$i][1],$waiting_list[$i][2],$waiting_list[$i][3],
						   			        $waiting_list[$i][4],$waiting_list[$i][5],$waiting_list[$i][6],
						                    $waiting_list[$i][7],$waiting_list[$i][8],$waiting_list[$i][9],
						                    $waiting_list[$i][10],$waiting_list[$i][11],$waiting_list[$i][12],
						                    $waiting_list[$i][13],$count_neibour);
		$query->execute();
		$mysqli->close();

		$mysqli=new mysqli($sql_server,$sql_username,$sql_password,$sql_database);
		$SQL_GETMIDP='SELECT mid FROM music WHERE name=? AND artist=? AND album=?';
		$query=$mysqli->prepare($SQL_GETMIDP);
		$query->bind_param('sss',$waiting_list[$i][1],$waiting_list[$i][2],$waiting_list[$i][3]);
		$query->execute();
		$query->bind_result($PID);
		$query->fetch();
		$mysqli->close();

		$mysqli=new mysqli($sql_server,$sql_username,$sql_password,$sql_database);
		$SQL_GETCLASS='SELECT max(class) FROM music';
		$query=$mysqli->prepare($SQL_GETCLASS);
		$query->execute();
		$query->bind_result($CLASS_NUMBER);
		$query->fetch();
		$mysqli->close();
		$CLASS_NUMBER++;


		switch(Analysis($UPDSEED)){
			case 1:
				break;
			case 2:
				
				$mysqli=new mysqli($sql_server,$sql_username,$sql_password,$sql_database);
				$SQL_CREATENEW='UPDATE music SET class=? WHERE mid IN';
				$SQL_CREATENEW.=ComposeNewSet($UPDSEED,$PID);
				$query=$mysqli->prepare($SQL_CREATENEW);
				$query->bind_param('i',$CLASS_NUMBER);
				$query->execute();
				$mysqli->close();
				break;
			case 3:
				$mysqli=new mysqli($sql_server,$sql_username,$sql_password,$sql_database);
				$SQL_ABSORPTION='UPDATE music SET class='.GetClass($UPDSEED).' WHERE mid IN';
				$SQL_ABSORPTION.=ComposeAbsoptionSet($UPDSEED,$PID);
				$query=$mysqli->prepare($SQL_ABSORPTION);
				$query->execute();
				$mysqli->close();
				break;
			case 4:
				$mysqli=new mysqli($sql_server,$sql_username,$sql_password,$sql_database);
				$SQL_MERGE='UPDATE music SET class='.$CLASS_NUMBER.' WHERE mid IN';
				$SQL_MERGE.=ComposeMergeSet($UPDSEED,$PID);
				$query=$mysqli->prepare($SQL_ABSORPTION);
				$query->execute();
				$mysqli->close();
				UpdateMusic($UPDSEED,$CLASS_NUMBER);
				UpdateUser($UPDSEED);
				break;
		}
	}

	function GetClass($UPDSEED){
		$clusters=array();
		for($i=0;$i<$cnt;$i++){
			array_push($clusters, $UPDSEED[$i][1]);
		}
		$clusters=array_unique($clusters);
		if($clusters[0]==0){
			return $clusters[1];
		}
		else{
			return $clusters[0];
		}
	}

	function ComposeAbsoptionSet($UPDSEED,$PID){
		$res='(';
		$cnt=count($UPDSEED);
		for($i=0;$i<$cnt;$i++){
			if($UPDSEED[$i][1]==0){
				$res.=$UPDSEED[$i][0].',';
			}
		}
		$res.=$PID.')';
		return $res;
	}

	function ComposeMergeSet($UPDSEED,$PID){
		$res='(';
		$u_cnt=count($UPDSEED);
		for($i=0;$i<$u_cnt;$i++){
			$res.=$UPDSEED[$i][0].',';
		}
		$res.=$PID.')';
		return $res;
	}

	function Analysis($UPDSEED){
		$cnt=count($UPDSEED);
		if($cnt==0){
			return 1;
		}
		$clusters=array();
		for($i=0;$i<$cnt;$i++){
			array_push($clusters, $UPDSEED[$i][1]);
		}
		$clusters=array_unique($clusters);
		if(count($clusters)==1){
			return 2;
		}
		else if(count($clusters)==2){
			if($clusters[0]==0||$clusters[1]==0){
				return 3;
			}
			else{
				return 4;
			}
		}
		else{
			return 4;
		}
	}

	function ComposeNewSet($UPDSEED,$PID){
		$res='(';
		$u_cnt=count($UPDSEED);
		for($i=0;$i<$u_cnt;$i++){
			$res.=$UPDSEED[$i][0].',';
		}
		$res.=$PID.')';
		return $res;
	}

    function UpdateMusic($pervious,$new){

    }

    function UpdateUser(){

    }
?>