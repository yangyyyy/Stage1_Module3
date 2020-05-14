作业一：
自定义如下url:

<h>http://localhost:8080/work/handler01?username=zhangsan 我是handler01，只允许zhangsan访问</h>
<h>http://localhost:8080/work/handler02?username=lisi 我是handler02，只允许lisi访问</h>
<h>http://localhost:8080/work/handler03?username=zhangsan 我是handler03，允许zhangsan,lisi访问</h>
<h>http://localhost:8080/work/handler03?username=lisi 我是handler03，允许zhangsan,lisi访问</h>
<h>http://localhost:8080/work/handler04?username=wangwu 我是handler04，Controller配置允许zhangsan,lisi,wangwu访问</h>

