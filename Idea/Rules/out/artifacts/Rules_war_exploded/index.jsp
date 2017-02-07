<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html>
<head>
<title>首页 - 规章管理系统</title>
<meta name="viewport" content="width=device-width, initial-scale=1">
<meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
<script type="application/x-javascript"> addEventListener("load", function() { setTimeout(hideURLbar, 0); }, false); function hideURLbar(){ window.scrollTo(0,1); } </script>
<!-- 加载样式表 -->
<link href="css/bootstrap.css" rel="stylesheet" type="text/css" media="all" />
<link rel="stylesheet" href="css/style.css" type="text/css" media="all" />
<link href="css/font-awesome.css" rel="stylesheet"> 
<link href='css/immersive-slider.css' rel='stylesheet' type='text/css'>
<link rel="stylesheet" href="css/jquery.flipster.css">
<!-- //加载样式表 -->
</head>
<body>
	<!-- 头部的黑条 -->
	<div class="header-top">
		<div class="container">
			<div style="color:white;text-align:right">
				当前登录用户为：普通用户
				<a href="login.jsp">【管理员登录】</a>
			</div>
		</div>
	</div>
	<!-- //头部的黑条 -->
	<!-- 标题及导航栏 -->
	<div class="header">
		<div class="container">
			<nav class="navbar navbar-default">
				<!-- Brand and toggle get grouped for better mobile display -->
				<div class="navbar-header">
					<button type="button" class="navbar-toggle collapsed" data-toggle="collapse" data-target="#bs-example-navbar-collapse-1">
					<span class="sr-only">Toggle navigation</span>
					<span class="icon-bar"></span>
					<span class="icon-bar"></span>
					<span class="icon-bar"></span>
					</button>
					<div class="w3layouts-logo">
						<h1><a>规章管理系统</a></h1>
					</div>
				</div>
				<!-- Collect the nav links, forms, and other content for toggling -->
				<div class="collapse navbar-collapse nav-wil" id="bs-example-navbar-collapse-1">
					<nav>
						<ul class="nav navbar-nav">
							<li class="active"><a>首页</a></li>
							<li><a href="sort.jsp" class="hvr-sweep-to-bottom">分类浏览</a></li>
							<li><a href="search.jsp" class="hvr-sweep-to-bottom">查询</a></li>
							<li><a href="manage.jsp" class="hvr-sweep-to-bottom">后台管理</a></li>
						</ul>
					</nav>
				</div>
			</nav>
		</div>
	</div>
	<!-- //标题及导航栏 -->
	<!--========================================================
			【首页】
		========================================================-->
</body>	
</html>