<%@ page import="java.util.List" %>
<%@ page import="com.lagou.edu.pojo.Resume" %>
<%@ page language="java" contentType="text/html; charset=utf-8" pageEncoding="utf-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
    <title>详情界面</title>
</head>
<script src="https://libs.baidu.com/jquery/1.4.2/jquery.min.js"></script>
<script type="text/javascript">

</script>

<body>


<form name="add" action="/resume/addOrUpdateResume" method="post">
<tr>
    <td width="50%">姓名: </td><td width="50%"><input type="text" id="name" name="name"></td>
</tr>
<tr>
  <td width="50%">地址:</td><td width="50%"><input type="text" id="address" name="address"></td>
</tr>
<tr>
  <td width="50%">联系方式:</td><td width="50%"><input type="text" id="phone" name="phone"></td>
</tr>
<tr>
    <input id="submit" type="submit" name="提交">
</tr>
</form>

</body>
</html>