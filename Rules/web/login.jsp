<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html>
<head>
    <title>管理员登录 - 规章管理系统</title>
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <meta http-equiv="Content-Type" content="text/html; charset=utf-8"/>
    <link href="css/style-login.css" rel="stylesheet" type="text/css" media="all"/>
</head>
<body>
<div class="main">
    <h1>管理员登录</h1>
    <div class="input_form">
        <form>
            <input type="text" value="" onfocus="this.value = '';" onblur="if (this.value == '') {this.value = '';}"
                   required="">
            <input type="password" value="" onfocus="this.value = '';" onblur="if (this.value == '') {this.value = '';}"
                   required="">
        </form>
    </div>
    <div class="ckeck-bg">
        <div class="checkbox-form">
            <div class="check-left">
                <div class="check">
                    <label class="checkbox"><input type="checkbox" name="checkbox" checked=""><i> </i>记住密码</label>
                </div>
            </div>
            <div class="check-right">
                <form method="POST">
                    <input type="submit" value="登录">
                </form>
            </div>
            <div class="clearfix"></div>
        </div>
    </div>
</div>
</body>
</html>