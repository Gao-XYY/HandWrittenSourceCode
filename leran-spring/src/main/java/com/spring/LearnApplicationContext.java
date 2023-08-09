package com.spring;

import java.io.File;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class LearnApplicationContext {

    private Class configClass;
    private ConcurrentHashMap<String, Object> singletonObjects = new ConcurrentHashMap<>();//用来存放单例bean   单例池
    //存所有bean的定义
    private ConcurrentHashMap<String, BeanDefinition> beanDefinitionMap = new ConcurrentHashMap<>();

    private List<BeanPostProcessor> beanPostProcessorList = new ArrayList<>();

    public LearnApplicationContext(Class configClass){
        this.configClass = configClass;

        //解析配置类
        //ComponentScan注解--->扫描路径--->扫描 -----> Beandefinition ----> beanDefinitionMap
        scan(configClass);
        for (Map.Entry<String, BeanDefinition> entry : beanDefinitionMap.entrySet()) {
            String beanName = entry.getKey();
            BeanDefinition beanDefinition = entry.getValue();
            if (beanDefinition.getScope().equals("singleton")){

                Object bean = createBean(beanName, beanDefinition);//单例bean

                singletonObjects.put(beanName, bean);
            }
        }
    }

    //创建bean
    public Object createBean(String beanName, BeanDefinition beanDefinition){
        Class clazz = beanDefinition.getClazz();
        try {
            //通过反射获取创建bean对象
            Object instance = clazz.getDeclaredConstructor().newInstance();
            // 依赖注入
            for (Field declaredField : clazz.getDeclaredFields()) {
                if (declaredField.isAnnotationPresent(Autowired.class)){
                    //根据名字找bean
                    Object bean = getBean(declaredField.getName());
                    declaredField.setAccessible(true);
                    declaredField.set(instance, bean);
                }
            }
            //Aware回调
            if (instance instanceof BeanNameAware){
                ((BeanNameAware)instance).setBeanName(beanName);
            }
            for (BeanPostProcessor beanPostProcessor : beanPostProcessorList) {
                instance = beanPostProcessor.postProcessBeforeInitialization(instance, beanName);
            }
            //初始化
            if (instance instanceof InitializingBean){
                try {
                    ((InitializingBean)instance).afterPropertiesSet();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            for (BeanPostProcessor beanPostProcessor : beanPostProcessorList) {
                instance = beanPostProcessor.psotProcessAfterInitialization(instance, beanName);
            }
            // BeanPostProcessor
            return instance;
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        }

        return null;
    }

    //扫描
    private void scan(Class configClass) {

        //通过getDeclaredAnnotation拿到想要的注解@ComponentScan
        ComponentScan componentScanAnnotation = (ComponentScan) configClass.getDeclaredAnnotation(ComponentScan.class);
        String path = componentScanAnnotation.value();//扫描路径
        path = path.replace(".", "/");
//        System.out.println(path);

        // 扫描
        //Bootstrap ---> jre/lib
        //Ext ---------> jre/ext/lib
        //App ---------> classpath
        //拿到对应的类加载器
        ClassLoader classLoader = LearnApplicationContext.class.getClassLoader();
//        URL resource = classLoader.getResource("com/learn/service");
        //通过类加载器拿到对应的资源()
        URL resource = classLoader.getResource(path);

        File file = new File(resource.getFile());
        //判断是否拿到的是不是一个目录，如果是接着下面的工作
        if (file.isDirectory()){
            File[] files = file.listFiles();
            for(File f : files){
//                System.out.println(f);
                //获取目录名字
                String fileName = f.getAbsolutePath();
                //判断扫出来的是不是(.class)文件
                if (fileName.endsWith(".class")){
                    //截取需要的(从com开始到.class结束)
                    String className = fileName.substring(fileName.indexOf("com"), fileName.indexOf(".class"));
                    //将\替换成点
                    className = className.replace("\\", ".");
//                System.out.println(className);

                    try {
                        //加载一个类获得一个class对象
                        Class<?> clazz = classLoader.loadClass(className);
                        //判断找到的路径里面的类上面有没有Component注解
                        if (clazz.isAnnotationPresent(Component.class)){
                            //表示当前这个类是一个bean
                            //解析类 ----> 生成BeanDefinition，         判断当前bean是单例bean，还是prototype的bean
                            //BeanDefinition ==》bean的定义

                            if (BeanPostProcessor.class.isAssignableFrom(clazz)) {
                                BeanPostProcessor instance = (BeanPostProcessor) clazz.getDeclaredConstructor().newInstance();
                                beanPostProcessorList.add(instance);
                            }
                            //拿出Component注解(为啥要拿这个注解，是为了拿这个注解里面的值也就是bean的名字)
                            Component componentAnnotation = clazz.getDeclaredAnnotation(Component.class);
                            //当前bean对应的名字
                            String beanName = componentAnnotation.value();

                            //生成beanDefinition
                            BeanDefinition beanDefinition = new BeanDefinition();
                            //将bean的类型设置进beanDefinition
                            beanDefinition.setClazz(clazz);
                            //解析类，看有没有设置作用域
                            if (clazz.isAnnotationPresent(Scope.class)){
                                Scope scopeAnnotation = clazz.getDeclaredAnnotation(Scope.class);
                                //获取设置的作用域的值
                                beanDefinition.setScope(scopeAnnotation.value());
                            } else {
                                beanDefinition.setScope("singleton");
                            }

                            beanDefinitionMap.put(beanName, beanDefinition);

                        }

                    } catch (ClassNotFoundException | NoSuchMethodException e) {
                        e.printStackTrace();
                    } catch (InvocationTargetException e) {
                        e.printStackTrace();
                    } catch (InstantiationException e) {
                        e.printStackTrace();
                    } catch (IllegalAccessException e) {
                        e.printStackTrace();
                    }

                }


            }
        }
    }

    public Object getBean(String beanName){

        if (beanDefinitionMap.containsKey(beanName)){
            //获取到beanName对应的beanDefinition对象
            BeanDefinition beanDefinition = beanDefinitionMap.get(beanName);
            //判断当前bean是单例的还是原型的
            if (beanDefinition.getScope().equals("singleton")){
                //如果当前bean是单例的就从单例池中获取对象
                Object o = singletonObjects.get(beanName);
                return o;
            } else {
                //如果当前bean不是单例的就创建一个bean对象
                Object bean = createBean(beanName, beanDefinition);
                return bean;
            }
        } else {
            // 不存在对应的Bean
            throw new NullPointerException();
        }

    }


}
