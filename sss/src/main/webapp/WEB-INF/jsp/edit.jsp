<%@ page import="java.util.List" %>
<%@ page import="com.lagou.edu.pojo.Resume" %>
<%@ page isELIgnored ="false" %>
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

<input id="resume" type="hidden" value="${resume}">
<tr>
    <td width="50%">ID : ${resume.id}</td>
</tr>
<tr>
  <td width="50%">姓名: ${resume.name}</td>
</tr>
<tr>
  <td width="50%">地址: ${resume.address}</td>
</tr>
<tr>
  <td width="50%">联系方式: ${resume.phone}</td>
</tr>


</body>
</html>