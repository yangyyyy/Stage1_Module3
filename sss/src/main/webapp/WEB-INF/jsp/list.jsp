<%@ page import="java.util.List" %>
<%@ page import="com.lagou.edu.pojo.Resume" %>
<%@ page language="java" contentType="text/html; charset=utf-8" pageEncoding="utf-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
    <title>登录界面</title>
</head>
<script src="https://libs.baidu.com/jquery/1.4.2/jquery.min.js"></script>
<script type="text/javascript">


    $(document).ready(function(){
        $.ajax({
            url: "/resume/queryAll",
            dataType: "json",
            success: function(data){
                /*这个方法里是ajax发送请求成功之后执行的代码*/
                showData(data);//我们仅做数据展示
            },
            error: function(msg){
                alert("ajax连接异常："+msg);
            }
        });
    });

    function showData(data) {
        var str = "";//定义用于拼接的字符串
        for (var i = 0; i < data.length; i++) {
            //拼接表格的行和列
            str = "<tr><td>" + data[i].id + "</td>" +
                "<td>" + data[i].name + "</td>" +
                "<td>" + data[i].address + "</td>" +
                "<td>" + data[i].phone + "</td>" +
                "<td><input type='button' value='编辑' onclick='edit("+ data[i].id +")'/><input type='button' value='删除' onclick='del("+ data[i].id +")'/></td>" +
                "</tr>";
            //追加到table中
            $("#tableBody").append(str);         }
    }

    function del(id){
        if(window.confirm("确定删除吗？")){
            window.location= "/resume/deleteResume?id="+id;
        }

    };

    function edit(id) {
        window.location= "/resume/getResumeById?id="+id ;
    }

    function add() {
        window.location= "/resume/add" ;
    }

</script>

<body>

        <!-- 列表 -->
        <tr><input type="button" value="新增" onclick="add()"/></tr>
        <table id="tableBody" >
            <thead>
            <tr>
                <th width="20%">ID</th>
                <th width="20%">姓名</th>
                <th width="20%">地址</th>
                <th width="20%">联系方式</th>
                <th width="20%" style="text-align:center;">操作</th>
            </tr>
            </thead>
            </tbody>

        </table>

</body>
</html>