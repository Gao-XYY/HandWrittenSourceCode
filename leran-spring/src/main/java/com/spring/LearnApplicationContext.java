package com.spring;

import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.net.URL;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class LearnApplicationContext {

    private Class configClass;
    private ConcurrentHashMap<String, Object> singletonObjects = new ConcurrentHashMap<>();//用来存放单例bean   单例池
    //存所有bean的定义
    private ConcurrentHashMap<String, BeanDefinition> beanDefinitionMap = new ConcurrentHashMap<>();

    public LearnApplicationContext(Class configClass){
        this.configClass = configClass;

        //解析配置类
        //ComponentScan注解--->扫描路径--->扫描 -----> Beandefinition ----> beanDefinitionMap
        scan(configClass);
        for (Map.Entry<String, BeanDefinition> entry : beanDefinitionMap.entrySet()) {
            String beanName = entry.getKey();
            BeanDefinition beanDefinition = entry.getValue();
            if (beanDefinition.getScope().equals("singleton")){
                Object bean = createBean(beanDefinition);//单例bean

                singletonObjects.put(beanName, bean);
            }
        }


    }

    //创建bean
    public Object createBean(BeanDefinition beanDefinition){

        Class clazz = beanDefinition.getClazz();

        try {
            //通过反射获取创建bean对象
            Object instance = clazz.getDeclaredConstructor().newInstance();
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


        ComponentScan componentScanAnnotation = (ComponentScan) configClass.getDeclaredAnnotation(ComponentScan.class);
        String path = componentScanAnnotation.value();//扫描路径
        path = path.replace(".", "/");
//        System.out.println(path);

        // 扫描
        //Bootstrap ---> jre/lib
        //Ext ---------> jre/ext/lib
        //App ---------> classpath
        ClassLoader classLoader = LearnApplicationContext.class.getClassLoader();
//        URL resource = classLoader.getResource("com/learn/service");
        URL resource = classLoader.getResource(path);

        File file = new File(resource.getFile());
        if (file.isDirectory()){
            File[] files = file.listFiles();
            for(File f : files){
//                System.out.println(f);
                String fileName = f.getAbsolutePath();
                if (fileName.endsWith(".class")){
                    String className = fileName.substring(fileName.indexOf("com"), fileName.indexOf(".class"));
                    className = className.replace("\\", ".");
//                System.out.println(className);

                    try {
                        Class<?> clazz = classLoader.loadClass(className);

                        if (clazz.isAnnotationPresent(Component.class)){
                            //表示当前这个类是一个bean
                            //解析类 ----> 生成BeanDefinition，         判断当前bean是单例bean，还是prototype的bean
                            //BeanDefinition ==》bean的定义

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

                    } catch (ClassNotFoundException e) {
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
                Object bean = createBean(beanDefinition);
                return bean;
            }
        } else {
            // 不存在对应的Bean
            throw new NullPointerException();
        }

    }


}
