<%@ page language="java" contentType="text/html; charset=utf-8"
         pageEncoding="utf-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
    <title>登录界面</title>
</head>

<body>
<center>
    <br><br><br><br><br><br>
    <h1 style="color:yellow">Login</h1>
    <br>
    <form name="loginForm" action="/resume/login" method="post">
        <table Border="0" >
            <tr >

                <td>账号</td>
                <td><input type="text" name="username"></td>
            </tr>
            <tr>
                <td>密码</td>
                <td><input type="password" name="password">
                </td>
            </tr>
        </table>
        <br>
        <input type="submit" value="登录" style="color:#BC8F8F">
    </form>
</center>

</body>
</html>