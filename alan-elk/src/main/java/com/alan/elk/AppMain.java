package com.alan.elk;

import com.alan.elk.bean.Goods;
import com.alan.elk.service.ElasticSearchService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@SpringBootApplication
@RestController
public class AppMain {
    @Autowired
    private ElasticSearchService elasticSearchService;

    @RequestMapping("get")
    public String get(String id) {
        elasticSearchService.get("es-test", "goods", id);
        return "success";
    }

    @RequestMapping("add")
    public String add(Goods goods) {
        return elasticSearchService.add(goods, "es-test", "goods");
    }

    @RequestMapping("deleteOne")
    public String deleteOne(String id) {
        elasticSearchService.delete("es-test", "goods", "14");
        return "success";
    }

    @RequestMapping("deleteBatch")
    public String deleteBatch(String id) {
        return "success";
    }

    public static void main(String[] args) {
        SpringApplication.run(AppMain.class, args);
    }
}
