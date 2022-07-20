package org.example.reggie.entity;

import lombok.Data;

import java.io.Serializable;
import java.time.LocalDate;

@Data
public class Employee implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long id;
    private String name;
    private String username;
    private String password;
    private String phone;
    private String sex;
    private String idNumber;
    private Integer status;
    private LocalDate createTime;
    private LocalDate updateTime;
    private Long createUser;
    private Long updateUser;

}
