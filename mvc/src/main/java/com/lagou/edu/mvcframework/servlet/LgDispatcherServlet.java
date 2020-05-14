package com.lagou.edu.mvcframework.servlet;

import com.lagou.edu.mvcframework.annotations.*;
import com.lagou.edu.mvcframework.pojo.Handler;
import org.apache.commons.lang3.StringUtils;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class LgDispatcherServlet extends HttpServlet {

    private Properties properties = new Properties();

    // 缓存扫描到的全限定类名
    private List<String> classNames = new ArrayList<>();

    // IOC容器
    private Map<String, Object> ioc = new HashMap<>();

    // HandlerMapping
//    private Map<String, Method> handlerMapping = new HashMap<>();

    List<Handler> handlerMapping = new ArrayList<>();

    Map<String, String[]> security = new HashMap<>();

    @Override
    public void init(ServletConfig config) throws ServletException {
        // 1.加载配置文件 springmvc.properties
        String contextConfigLocation = config.getInitParameter("contextConfigLocation");
        doLoadConfig(contextConfigLocation);

        // 2.扫描相关类，扫描注解
        doScan(properties.getProperty("scanPackage"));

        // 3.初始化Bean对象（实现IOC容器，基于注解）
        doInstance();

        // 4.实现依赖注入
        doAutoWired();

        // 5.构造一个HandlerMapping处理器映射器，将配置好的URL和Method激励映射关系
        initHandlerMapping();

        doSecurity();

        System.out.println("lagou mvc 初始化完成。。。。。。");

        // 6.等待请求进入，处理请求
    }

    private void doSecurity() {
        if (ioc.isEmpty()) {
            return;
        }

        for (Map.Entry<String, Object> entry : ioc.entrySet()) {
            // 获取遍历对象class类型
            Class<?> aClass = entry.getValue().getClass();
            Security classAnnotation = aClass.getAnnotation(Security.class);
            if(classAnnotation == null) {continue;}
            // 获取方法
            Method[] methods = aClass.getMethods();
            for (int i = 0; i < methods.length; i++) {
                Method method = methods[i];

                if(aClass.isAnnotationPresent(Security.class)){
                    security.put(method.getName(), aClass.getAnnotation(Security.class).value());
                }

                Security annotation = method.getAnnotation(Security.class);

                if(method.isAnnotationPresent(Security.class)){
                    security.put(method.getName(), getIntersection(annotation.value(),aClass.getAnnotation(Security.class).value()));
                }

            }

        }
    }

    /**
     * 求交集
     *
     * @param m
     * @param n
     * @return
     */
    private static String[] getIntersection(String[] m, String[] n)
    {
        if(null == m || null == n || m.length == 0 || n.length == 0 ){
            return null;
        }



        List<String> rs = new ArrayList<String>();
        // 将较长的数组转换为set
        Set<String> set = new HashSet<String>(Arrays.asList(m.length > n.length ? m : n));

        // 遍历较短的数组，实现最少循环
        for (String i : m.length > n.length ? n : m)
        {
            if (set.contains(i))
            {
                rs.add(i);
            }
        }

        String[] arr = {};
        return rs.toArray(arr);
    }

    /**
     * 构造一个HandlerMapping处理器映射器
     */
    private void initHandlerMapping() {
        if (ioc.isEmpty()) {
            return;
        }

        for (Map.Entry<String, Object> entry : ioc.entrySet()) {
            // 获取遍历对象class类型
            Class<?> aClass = entry.getValue().getClass();

            if(!aClass.isAnnotationPresent(LagouController.class)){
                continue;
            }

            String baseUrl = "";
            if(aClass.isAnnotationPresent(LagouRequestMapping.class)){
                LagouRequestMapping annotation = aClass.getAnnotation(LagouRequestMapping.class);
                baseUrl = annotation.value();
            }

            // 获取方法
            Method[] methods = aClass.getMethods();
            for (int i = 0; i < methods.length; i++) {
                Method method = methods[i];

                // 跳过没有注解的方法
                if(!method.isAnnotationPresent(LagouRequestMapping.class)){
                    continue;
                }

                LagouRequestMapping annotation = method.getAnnotation(LagouRequestMapping.class);
                String methodUrl = annotation.value();
                String url = baseUrl + methodUrl;

                Handler handler = new Handler(entry.getValue(), method, Pattern.compile(url));

                // 处理参数
                Parameter[] parameters = method.getParameters();
                for (int j = 0; j < parameters.length; j++) {
                    Parameter parameter = parameters[j];
                    if(parameter.getType() == HttpServletRequest.class || parameter.getType() == HttpServletResponse.class){
                        handler.getParamIndexMapping().put(parameter.getType().getSimpleName(), j);
                    }else {
                        handler.getParamIndexMapping().put(parameter.getName(), j);
                    }
                }

                // 建立url和method的映射
                handlerMapping.add(handler);

            }

        }
    }

    /**
     * 依赖注入
     */
    private void doAutoWired() {
        if(ioc.isEmpty()){
            return;
        }

        // 遍历IOC，查询对象所有字段且有@LagouAutoWired注解，对其注入处理
        for(Map.Entry<String, Object> entry : ioc.entrySet()){
            // 获取bean对象中的字段信息
            Field[] fields = entry.getValue().getClass().getDeclaredFields();
            for (int i = 0; i < fields.length; i++) {
                Field field = fields[i];
                if(!field.isAnnotationPresent(LagouAutowired.class)){
                    continue;
                }

                // 对@LagouAutoWired注解的属性注入
                LagouAutowired annotation = field.getAnnotation(LagouAutowired.class);
                String beanName = annotation.value();
                if("".equals(beanName.trim())){
                    // 没有配置，需要接口注入
                    beanName = field.getType().getName();

                    // 开启访问
                    field.setAccessible(true);

                    try {
                        field.set(entry.getValue(), ioc.get(beanName));
                    } catch (IllegalAccessException e) {
                        e.printStackTrace();
                    }

                }
            }
        }
    }

    /**
     * IOC容器
     */
    private void doInstance() {
        // 基于缓存的全限定类名和反射技术，完成对象创建和管理
        if(classNames.size() == 0){
            return;
        }

        try{
            for (int i = 0; i < classNames.size(); i++) {
                String className = classNames.get(i);

                // 反射创建对象
                Class<?> aClass = Class.forName(className);

                // 区分Service与Controller
                if(aClass.isAnnotationPresent(LagouController.class)){
                    // controller的ID不做处理，不取Value,直接类名首字母小写
                    String simpleName = aClass.getSimpleName();
                    String id = toLowerCaseFirstOne(simpleName);

                    // 实例化
                    Object instance = aClass.newInstance();

                    // 放入IOC容器
                    ioc.put(id, instance);
                }else if(aClass.isAnnotationPresent(LagouService.class)){
                    LagouService annotation = aClass.getAnnotation(LagouService.class);
                    String beanName = annotation.value();
                    if(null != beanName && !"".equals(beanName.trim())){
                        ioc.put(beanName, aClass.newInstance());
                    }else{
                        String simpleName = aClass.getSimpleName();
                        String id = toLowerCaseFirstOne(simpleName);

                        // 实例化
                        Object instance = aClass.newInstance();

                        // 放入IOC容器
                        ioc.put(id, instance);
                    }

                    // Service层往往是有接口的，面向接口开发，以接口名放入一份对象到IOC，便于注入
                    Class<?>[] interfaces = aClass.getInterfaces();
                    for (int j = 0; j < interfaces.length; j++) {
                        Class<?> anInterface = interfaces[j];

                        //  以接口全限定名作为ID放入IOC
                        ioc.put(anInterface.getName(), aClass.newInstance());
                    }
                }
            }
        }catch (Exception e){
            e.printStackTrace();
        }


    }

    /** 首字母转小写
     *
     * @param s
     * @return
     */
    public static String toLowerCaseFirstOne(String s) {
        if (Character.isLowerCase(s.charAt(0)))
            return s;
        else
            return (new StringBuilder()).append(Character.toLowerCase(s.charAt(0))).append(s.substring(1)).toString();
    }


    /**
     * 扫描相关类，扫描注解
     */
    private void doScan(String scanPackage) {
        String newScanPackage = scanPackage.replaceAll("\\.", "/");
        String basePath = Thread.currentThread().getContextClassLoader().getResource("").getPath();
        String scanPackagePath = basePath + newScanPackage;
        File pack = new File(scanPackagePath);
        File[] files = pack.listFiles();
        for (File file : files) {
            // 子Package递归
            if(file.isDirectory()){
                doScan(scanPackage + "." + file.getName());
            }else if(file.getName().endsWith(".class")){
                String fileName = file.getName().replaceAll(".class", "");
                String className = scanPackage + "." + fileName;
                classNames.add(className);
            }
        }
    }

    /**
     * 加载配置文件
     * @param contextConfigLocation
     */
    private void doLoadConfig(String contextConfigLocation) {
        InputStream resourceAsStream = this.getClass().getClassLoader().getResourceAsStream(contextConfigLocation);
        try {
            properties.load(resourceAsStream);
        } catch (IOException e) {
            e.printStackTrace();
            new Exception("读取配置文件失败");
        }
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        doPost(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        // 处理请求 根据URL获取Handler

        Handler handler = getHandler(req);
        if(handler == null){
            resp.getWriter().write("404 not found!");
            return;
        }

        // 参数绑定
        // 获取参数列表类型数组
        Class<?>[] parameterTypes = handler.getMethod().getParameterTypes();

        // 根据参数列表个数建立一个参数数组
        Object[] paramValues = new Object[parameterTypes.length];

        // 按参数列表放入参数值{填充req,resp外的参数}
        Map<String, String[]> parameterMap = req.getParameterMap();
        for(Map.Entry<String, String[]> param : parameterMap.entrySet()){
            String value = StringUtils.join(param.getValue(),",");

            // 匹配填充数据
            if(!handler.getParamIndexMapping().containsKey(param.getKey())){
                continue;
            }

            Integer index = handler.getParamIndexMapping().get(param.getKey());
            paramValues[index] = value;
        }

        int reqIndex = handler.getParamIndexMapping().get(HttpServletRequest.class.getSimpleName());
        paramValues[reqIndex] = req;

        int respIndex = handler.getParamIndexMapping().get(HttpServletResponse.class.getSimpleName());
        paramValues[respIndex] = resp;

        // 调用方法
        try {
            String methodName = handler.getMethod().getName();

        // 权限判断

            String[] allowUsers = security.get(methodName);
            String[] usernames = parameterMap.get("username");
            String[] intersection = getIntersection(allowUsers, usernames);
            if(null == intersection || intersection.length == 0){
                resp.getWriter().write("You do not have permission to access " + methodName);
                return;
            }


            handler.getMethod().invoke(handler.getController(), paramValues);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }
    }

    private Handler getHandler(HttpServletRequest req) {
        if(handlerMapping.isEmpty()){
            return null;
        }

        // URL
        String requestURI = req.getRequestURI();

        for (Handler handler : handlerMapping) {
            Matcher matcher = handler.getPattern().matcher(requestURI);
            if(!matcher.matches()){
                continue;
            }
            return handler;
        }


        return null;
    }


}
