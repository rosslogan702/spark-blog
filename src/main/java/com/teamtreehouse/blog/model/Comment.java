package com.teamtreehouse.blog.model;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Created by Ross on 09/11/2017.
 */
public class Comment {

    private String name;
    private String body;
    private String time;

    public Comment(String name, String body){
        this.name = name;
        this.body = body;
        this.time = DateTimeFormatter.ofPattern("dd-MMM-yyyy HH:mm").format(LocalDateTime.now());
    }

    public String getName(){
        return name;
    }

    public String getBody(){ return body; }

    public String getTime() { return time; }
}
