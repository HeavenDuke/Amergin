<?php
	header("Content-Type: text/html; charset=utf-8");
	$sql_database=SAE_MYSQL_DB;
	$sql_username=SAE_MYSQL_USER;
	$sql_server=SAE_MYSQL_HOST_M;
	$sql_password=SAE_MYSQL_PASS;
	$sql_port=SAE_MYSQL_PORT;

	$mysqli=new mysqli($sql_server,$sql_username,$sql_password,$sql_database,$sql_port);
	$sql='UPDATE music SET class=0';
	$query=$mysqli->prepare($sql);
	$query->execute();
	$mysqli->close();
?>