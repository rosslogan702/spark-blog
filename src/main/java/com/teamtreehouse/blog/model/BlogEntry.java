package com.teamtreehouse.blog.model;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import com.github.slugify.Slugify;

/**
 * Created by Ross on 09/11/2017.
 */
public class BlogEntry {

    private String title;
    private String body;
    private Set<Comment> comments;
    private String time;
    //TODO: Add in the author?
    private String slug;

    public BlogEntry(String title, String body){
        this.title = title;
        this.body = body;
        comments = new HashSet<>();
        time = DateTimeFormatter.ofPattern("dd-MMM-yyyy HH:mm").format(LocalDateTime.now());
        Slugify slugify = new Slugify();
        slug = slugify.slugify(title);
    }

    public String getTitle() {
        return title;
    }

    public String getBody(){
        return body;
    }

    public String getTime() { return time;}

    public String getSlug(){
        return slug;
    }

    public List<Comment> getComments(){
        return new ArrayList<>(comments);
    }

    public boolean addComment(Comment comment) {
        return comments.add(comment);
    }

    //TODO: Placeholder in case we need to return the total number of comments for a blog post


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        BlogEntry blogEntry = (BlogEntry) o;

        if (!title.equals(blogEntry.title)) return false;
        return body.equals(blogEntry.body);
    }

    @Override
    public int hashCode() {
        int result = title.hashCode();
        result = 31 * result + body.hashCode();
        return result;
    }
}
