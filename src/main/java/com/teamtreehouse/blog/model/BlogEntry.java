package com.teamtreehouse.blog.model;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

import com.github.slugify.Slugify;

/**
 * Created by Ross on 09/11/2017.
 */
public class BlogEntry implements Comparable<BlogEntry> {

    private String title;
    private String body;
    private LinkedHashSet<Comment> comments;
    private String time;
    private String slug;
    private Set<String> tags;
    private String tagList;

    public BlogEntry(String title, String body, Set<String> tags){
        this.title = title;
        this.body = body;
        comments = new LinkedHashSet<>();
        time = DateTimeFormatter.ofPattern("dd-MMM-yyyy HH:mm").format(LocalDateTime.now());
        Slugify slugify = new Slugify();
        slug = slugify.slugify(title);
        setTags(tags);
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title){this.title = title; }

    public String getBody(){
        return body;
    }

    public void setBody(String body){ this.body = body; }

    public String getTime() { return time;}

    public String getSlug(){
        return slug;
    }

    public String getTagList() { return tagList; }

    public List<Comment> getComments(){
        return new ArrayList<>(comments);
    }

    public boolean addComment(Comment comment) {
        return comments.add(comment);
    }

    public void setTags(Set<String> tags){
        this.tags = tags;
        this.tagList = getTagsString();
    }

    public Set<String> getTags() { return new HashSet<>(tags); }

    private String getTagsString() {
        if (this.tags.isEmpty()) {
            return "";
        } else {
            String tags = "";
            for (String s : this.tags) {
                tags += s + ",";
            }
            return tags.substring(0, tags.length()-1);
        }
    }

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

    @Override
    public int compareTo(BlogEntry entry) {
        return EntryComparators.UPDATED.compare(this, entry);
    }

    public static class EntryComparators {

        public static Comparator<BlogEntry> UPDATED = (entry1, entry2) -> entry2.time.compareTo(entry1.time);
    }
}
