package com.teamtreehouse.blog.model;

/**
 * Created by Ross on 09/11/2017.
 */
public class Comment {

    private String name;
    private String body;
    //TODO: Possibly add in creation time that we will show
    //This might be calculated from a library implicitly rather than box to add

    public Comment(String name, String body){
        this.name = name;
        this.body = body;
    }

    public String getName(){
        return name;
    }

    public String getBody(){
        return body;
    }
}
