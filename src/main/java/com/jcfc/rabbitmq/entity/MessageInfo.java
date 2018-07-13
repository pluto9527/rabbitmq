package com.jcfc.rabbitmq.entity;

import java.io.Serializable;

public class MessageInfo implements Serializable {

    private static final long serialVersionUID = 8593215421649205488L;
    private String name;

    private Integer age;

    public MessageInfo() {
    }

    public MessageInfo(String name, Integer age) {
        this.name = name;
        this.age = age;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getAge() {
        return age;
    }

    public void setAge(Integer age) {
        this.age = age;
    }

    @Override
    public String toString() {
        return "MessageInfo{" +
                "name='" + name + '\'' +
                ", age=" + age +
                '}';
    }
}
