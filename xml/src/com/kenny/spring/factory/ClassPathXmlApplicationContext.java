package com.kenny.spring.factory;

import com.kenny.spring.Annotation.ExtResource;
import com.kenny.spring.vo.BeanDefinition;
import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.InputStream;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * ClassName: ClassPathXmlApplicationContext
 * Function:  TODO
 * Date:      2019/6/27 20:07
 * author     Kenny
 * version    V1.0
 */
public class ClassPathXmlApplicationContext {

    /**
     * 通过此map存储bean的实例
     */
    private Map<String, Object> instanceMap = new HashMap<>();
    /**
     * 通过map存储配置文件中定义的bean对象的配置信息
     */
    private Map<String, BeanDefinition> beanMap = new HashMap<>();
    public ClassPathXmlApplicationContext(String file) throws Exception {
        //1读取配置文件
        InputStream in = getClass().getClassLoader().getResourceAsStream(file);
        //2解析文件封装数据
        parse(in);
    }

    /**
     * 本次xml的解析基于dom实现
     * 市场主流xml解析: dom(不需要引入三方依赖),dom4j,sax,pull,....
     */
    private void parse(InputStream in) throws Exception {
        //1.创建解析对象(负责读取xml文件)
        DocumentBuilder builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
        //2.解析流对象
        Document doc = builder.parse(in);
        //3.处理document
        processDocument(doc);


    }

    private void processDocument(Document doc) throws Exception {
        //1.获取所有bean元素
        NodeList list = doc.getElementsByTagName("bean");
        //2.迭代bean元素,对其配置信息进行封装
        for (int i = 0; i <list.getLength() ; i++) {
            Node node = list.item(i);//bean
            //一个node对象对应一个BeanDefinition对象
            BeanDefinition bd = new BeanDefinition();
            NamedNodeMap nodeMap = node.getAttributes();
            bd.setId(nodeMap.getNamedItem("id").getNodeValue());
            bd.setPkgClass(nodeMap.getNamedItem("class").getNodeValue());
            bd.setLazy(Boolean.valueOf(nodeMap.getNamedItem("lazy").getNodeValue()));
            //存储配置信息
            beanMap.put(bd.getId(), bd);
            //基于配置信息中lazy属性的值,判定此的的实例是否延迟加载
            if(bd.getLazy()){
                Class<?> cls = Class.forName(bd.getPkgClass());
                Object obj = newBeanInstance(cls);
                instanceMap.put(bd.getId(), obj);
            }
            System.out.println(instanceMap);

        }
        System.out.println(beanMap);

    }

    /**
     * 基于反射技术构建类的实例对象
     */
    private Object newBeanInstance(Class<?> cls) throws Exception {
//        Class<?> cls = Class.forName(pkgClass);
        Constructor<?> con = cls.getDeclaredConstructor();
        con.setAccessible(true);
        return con.newInstance();
    }

    public <T> T getBean(String key,Class<T> t) throws Exception {
        //1.判定当前instanceMap中是否有bean的实例
        Object obj = instanceMap.get(key);
        if(obj!=null)return (T) obj;
        //2.实例map中没有对象则创建对象,然后存储到map中
        obj = newBeanInstance(t);
        instanceMap.put(key, obj);
        return (T) obj;
    }

    /**自动注入注入这个对象的属性*/
    private void attrAssign(Object object) throws Exception {
        //获取这个类所有的属性
        Field[] fields = object.getClass().getDeclaredFields();
        //判断当前属性是否有注解
        for (Field field : fields) {
            ExtResource extResource = field.getAnnotation(ExtResource.class);
            if (extResource != null) {
                //允许访问私有属性
                field.setAccessible(true);
                //到这里说明这个属性里有这个注解,在从容器里获取对象然后给这个属性赋值
                String fieldName = field.getName();
//                Object target = beans.get(fieldName);
                Object target = getBean(fieldName, object.getClass());
                if (target == null) {
                    throw new RuntimeException("注入\"" + fieldName + "\"属性失败，bean容器里没有这个对象");
                }
                //第一个参数是这个属性所在的对象
                field.set(object,target);
            }
        }

    }
    public static void main(String[] args) throws Exception {
        //1.初始化spring容器
        ClassPathXmlApplicationContext ctx = new ClassPathXmlApplicationContext("resources/spring-configs.xml");
        //2.从spring容器获取bean实例
        Object obj = ctx.getBean("obj", Object.class);
        Date date = ctx.getBean("date",Date.class);

        System.out.println(obj);
        System.out.println(date);
    }
}
