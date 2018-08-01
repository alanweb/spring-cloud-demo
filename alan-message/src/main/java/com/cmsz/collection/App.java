package com.cmsz.collection;

import com.cmsz.collection.bean.BossMessage;
import com.cmsz.collection.cache.MessageCache;
import com.cmsz.collection.servlet.KafkaReciever;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * 程序入口
 *
 * @author weibin
 * @date 2018/5/30
 */
@SpringBootApplication
@RestController
@EnableScheduling
public class App {
    private Logger logger = LoggerFactory.getLogger(App.class);
    /*
    @Autowired
    private KafkaReciever kafkaReciever;
    使用了@Lazy 注解的Bean不能直接@Autowired 否则不起作用
    @Lazy 调用的时候才初始化
    */
    @Autowired
    private BeanFactory beanFactory;


    public static void main(String[] args) {
        SpringApplication.run(App.class, args);
    }

    @RequestMapping(value = "test1")
    public Map<String, BossMessage> test1() {
        return MessageCache.bossMessageMap;
    }

    @RequestMapping(value = "test2")
    public Map<String, BossMessage> test2() throws URISyntaxException {
        File file = new File(getClass().getClassLoader().getResource("message.txt").toURI());

        List<String> list = readFileByLines(file);
        for (String message : list) {
//            KafkaReciever kafkaReciever = (KafkaReciever) beanFactory.getBean("kafkaReciever");
//            kafkaReciever.takeMessage(message);
            System.out.println(message);
        }
        return MessageCache.bossMessageMap;
    }

    /**
     * 以行为单位读取文件，常用于读面向行的格式化文件
     */
    public List<String> readFileByLines(File file) throws URISyntaxException {
        List<String> list = new ArrayList<>();
        BufferedReader reader = null;
        try {
//            System.out.println("以行为单位读取文件内容，一次读一整行：");
            reader = new BufferedReader(new FileReader(file));
            String tempString = null;
            int line = 1;
            // 一次读入一行，直到读入null为文件结束
            while ((tempString = reader.readLine()) != null) {
                // 显示行号
//                System.out.println("line " + line + ": " + tempString);
                list.add(tempString);
                line++;
            }
            reader.close();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e1) {
                }
            }
        }
        return list;
    }
}
