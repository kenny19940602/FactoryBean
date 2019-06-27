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

    private void processDocument(Document doc) {
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
