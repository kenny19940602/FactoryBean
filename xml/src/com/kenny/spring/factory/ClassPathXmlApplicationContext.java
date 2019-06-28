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
     * ͨ����map�洢bean��ʵ��
     */
    private Map<String, Object> instanceMap = new HashMap<>();
    /**
     * ͨ��map�洢�����ļ��ж����bean�����������Ϣ
     */
    private Map<String, BeanDefinition> beanMap = new HashMap<>();
    public ClassPathXmlApplicationContext(String file) throws Exception {
        //1��ȡ�����ļ�
        InputStream in = getClass().getClassLoader().getResourceAsStream(file);
        //2�����ļ���װ����
        parse(in);
    }

    /**
     * ����xml�Ľ�������domʵ��
     * �г�����xml����: dom(����Ҫ������������),dom4j,sax,pull,....
     */
    private void parse(InputStream in) throws Exception {
        //1.������������(�����ȡxml�ļ�)
        DocumentBuilder builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
        //2.����������
        Document doc = builder.parse(in);
        //3.����document
        processDocument(doc);


    }

    private void processDocument(Document doc) throws Exception {
        //1.��ȡ����beanԪ��
        NodeList list = doc.getElementsByTagName("bean");
        //2.����beanԪ��,����������Ϣ���з�װ
        for (int i = 0; i <list.getLength() ; i++) {
            Node node = list.item(i);//bean
            //һ��node�����Ӧһ��BeanDefinition����
            BeanDefinition bd = new BeanDefinition();
            NamedNodeMap nodeMap = node.getAttributes();
            bd.setId(nodeMap.getNamedItem("id").getNodeValue());
            bd.setPkgClass(nodeMap.getNamedItem("class").getNodeValue());
            bd.setLazy(Boolean.valueOf(nodeMap.getNamedItem("lazy").getNodeValue()));
            //�洢������Ϣ
            beanMap.put(bd.getId(), bd);
            //����������Ϣ��lazy���Ե�ֵ,�ж��˵ĵ�ʵ���Ƿ��ӳټ���
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
     * ���ڷ��似���������ʵ������
     */
    private Object newBeanInstance(Class<?> cls) throws Exception {
//        Class<?> cls = Class.forName(pkgClass);
        Constructor<?> con = cls.getDeclaredConstructor();
        con.setAccessible(true);
        return con.newInstance();
    }

    public <T> T getBean(String key,Class<T> t) throws Exception {
        //1.�ж���ǰinstanceMap���Ƿ���bean��ʵ��
        Object obj = instanceMap.get(key);
        if(obj!=null)return (T) obj;
        //2.ʵ��map��û�ж����򴴽�����,Ȼ��洢��map��
        obj = newBeanInstance(t);
        instanceMap.put(key, obj);
        return (T) obj;
    }

    /**�Զ�ע��ע��������������*/
    private void attrAssign(Object object) throws Exception {
        //��ȡ��������е�����
        Field[] fields = object.getClass().getDeclaredFields();
        //�жϵ�ǰ�����Ƿ���ע��
        for (Field field : fields) {
            ExtResource extResource = field.getAnnotation(ExtResource.class);
            if (extResource != null) {
                //�������˽������
                field.setAccessible(true);
                //������˵����������������ע��,�ڴ��������ȡ����Ȼ���������Ը�ֵ
                String fieldName = field.getName();
//                Object target = beans.get(fieldName);
                Object target = getBean(fieldName, object.getClass());
                if (target == null) {
                    throw new RuntimeException("ע��\"" + fieldName + "\"����ʧ�ܣ�bean������û���������");
                }
                //��һ������������������ڵĶ���
                field.set(object,target);
            }
        }

    }
    public static void main(String[] args) throws Exception {
        //1.��ʼ��spring����
        ClassPathXmlApplicationContext ctx = new ClassPathXmlApplicationContext("resources/spring-configs.xml");
        //2.��spring������ȡbeanʵ��
        Object obj = ctx.getBean("obj", Object.class);
        Date date = ctx.getBean("date",Date.class);

        System.out.println(obj);
        System.out.println(date);
    }
}
