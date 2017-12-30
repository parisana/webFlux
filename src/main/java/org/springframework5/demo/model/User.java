package org.springframework5.demo.model;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Created by Parisana on 30/12/17
 */
public class User {

    private final String name;

    private final int age;

    public User(@JsonProperty("name") String name,@JsonProperty("age") int age) {
        this.name = name;
        this.age = age;
    }

    public String getName() {
        return name;
    }

    public int getAge() {
        return age;
    }

    /*@Override
    public String toString() {
        return "User{" +
                "name='" + name + '\'' +
                ", age=" + age +
                '}';
    }*/
}
