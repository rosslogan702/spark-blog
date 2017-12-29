package com.teamtreehouse.blog.dao;

import com.teamtreehouse.blog.NotFoundException;
import com.teamtreehouse.blog.model.BlogEntry;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * Created by Ross on 15/11/2017.
 */
public class SimpleBlogDao implements BlogDao {

    private List<BlogEntry> blogEntries;

    public SimpleBlogDao () {
        blogEntries = new ArrayList<>();
    }

    @Override
    public boolean addEntry(BlogEntry blogEntry) {
        return blogEntries.add(blogEntry);
    }

    @Override
    public boolean deleteEntry(BlogEntry blogEntry){
        return blogEntries.remove(blogEntry);
    }

    @Override
    public List<BlogEntry> findAllEntries() {
        ArrayList<BlogEntry> blogEntries = new ArrayList<>(this.blogEntries);
        Collections.sort(blogEntries, BlogEntry.EntryComparators.UPDATED);
        return blogEntries;
    }

    @Override
    public BlogEntry findEntryBySlug(String slug) {
        return blogEntries.stream()
                .filter(blogEntry -> blogEntry.getSlug().equals(slug))
                .findFirst()
                .orElseThrow(NotFoundException::new);
    }
}
