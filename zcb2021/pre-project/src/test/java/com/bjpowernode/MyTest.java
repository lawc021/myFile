package com.bjpowernode;

import com.alibaba.fastjson.JSONObject;
import com.bjpowernode.model.Student;
import com.sun.scenario.effect.impl.sw.sse.SSEBlend_SRC_OUTPeer;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

public class MyTest {
    // 把对象转json：序列化
    @Test
    public void test01(){
        Student student = new Student();
        student.setId(1001);
        student.setName("李想");
        student.setAge(20);

        String json  = JSONObject.toJSONString(student);
        System.out.println("Student转为json="+json);
    }


    @Test
    public void test02(){
        List<Student> list = new ArrayList<>();

        Student student = new Student();
        student.setId(1001);
        student.setName("李想");
        student.setAge(20);
        list.add(student);

        student = new Student();
        student.setId(1002);
        student.setName("周超");
        student.setAge(22);
        list.add(student);

        String json = JSONObject.toJSONString(list);
        System.out.println("List-JSON:"+json);

    }


    @Test
    public void test03(){
        String json="{\"age\":30,\"id\":1003,\"name\":\"周超\"}";
        Student student = JSONObject.parseObject(json,Student.class);
        System.out.println("studet="+student.toString());

        JSONObject jsonObject = JSONObject.parseObject(json);
        Integer id = jsonObject.getInteger("id");
        int age = jsonObject.getIntValue("age");
        System.out.println("id="+id+",age="+age);
    }


    @Test
    public void test04(){
        String str="{\"age\":20,\"name\":\"李响\",\"phone\":\"1350000000\",\"school\":{\"address\":\"北京的海淀区\",\"name\":\"北京大学\"}}";

        JSONObject o1 = JSONObject.parseObject(str);
        JSONObject o2 = o1.getJSONObject("school");

        System.out.println("name:"+o2.getString("name"));


        //JSONObject.parseObject(str).getJSONObject("school").getString("name");

    }


    @Test
    public void test05(){
        ThreadLocalRandom random = ThreadLocalRandom.current();
        /*for(int i=0;i<20;i++){
            System.out.println("nextInt=" +  random.nextInt());
            System.out.println("random.nextInt(100)=" +random.nextInt(100));
            System.out.println("random.nextInt(10,50)="+random.nextInt(10,50));
        }*/

        /*for(int i=0;i<20;i++){
            System.out.println( random.nextInt(1000,10000));
        }*/


        StringBuilder builder = new StringBuilder();
        for(int i=0;i<4;i++){
            builder.append(random.nextInt(10));
        }
        System.out.println("ranndom="+builder.toString());

    }

}
