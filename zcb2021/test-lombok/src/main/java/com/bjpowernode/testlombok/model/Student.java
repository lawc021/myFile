package com.bjpowernode.testlombok.model;

import lombok.*;
import lombok.extern.slf4j.Slf4j;

/**
 * @Data：生成 无参数构造方法， set|get，hashCode, equals, toString
 */

@Slf4j
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class Student {

    private Integer id;
    private String name;
    private Integer age;


}
