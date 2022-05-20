package org.example.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

/**
 * @author : chy
 * @date: 2022-05-19 3:38 p.m.
 */

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Person {

    @JsonIgnore
    private String id;

    private String name;

    private Integer age;

    @JsonFormat(pattern = "yyyy-MM-dd")
    private Date birthday;
}
