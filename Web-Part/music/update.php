<?php
	header("Content-Type: text/html; charset=utf-8");
	$sql_database=SAE_MYSQL_DB;
	$sql_username=SAE_MYSQL_USER;
	$sql_server=SAE_MYSQL_HOST_M;
	$sql_password=SAE_MYSQL_PASS;
	$sql_port=SAE_MYSQL_PORT;

	$file=fopen('./musicdata.csv','r');
	print_r(fgetcsv($file));
	while(!feof($file)){
		$mysqli=new mysqli($sql_server,$sql_username,$sql_password,$sql_database,$sql_port);
		$mysqli->query('SET NAMES UTF8');
		$sql='INSERT INTO music VALUES(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)';
		$query=$mysqli->prepare($sql);
		$query->bind_param('isssiiiiiiiiiiii',$music[0],$music[1],$music[2],$music[3],$music[4],$music[5],$music[6],$music[7]
											 ,$music[8],$music[9],$music[10],$music[11],$music[12],$music[13],$music[14],$music[15]);
		$query->execute();
		$mysqli->close();
		echo $sql;
	}
	fclose($file);
?>