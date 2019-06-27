package com.kenny.spring.factory;

import com.kenny.spring.vo.BeanDefinition;
import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.InputStream;
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

    private void processDocument(Document doc) {
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

        }
        System.out.println(beanMap);

    }

    public <T> T getBean(String key,Class<T> t){
            return null;
    }

    public static void main(String[] args) throws Exception {
        ClassPathXmlApplicationContext ctx = new ClassPathXmlApplicationContext("spring-configs");
    }
}
