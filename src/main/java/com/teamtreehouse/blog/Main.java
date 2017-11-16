package com.teamtreehouse.blog;

import com.teamtreehouse.blog.dao.SimpleBlogDao;
import com.teamtreehouse.blog.model.BlogEntry;
import spark.ModelAndView;
import spark.template.handlebars.HandlebarsTemplateEngine;

import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;

import static spark.Spark.get;
import static spark.Spark.post;
import static spark.Spark.staticFileLocation;

/**
 * Created by Ross on 09/11/2017.
 */

public class Main {

    private static final DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss");

    public static void main(String[] args) {
        staticFileLocation("/public");
        SimpleBlogDao dao = new SimpleBlogDao();

        //Pre-populated dao for testing and for project where we need at least 3 projects
        BlogEntry testEntryOne = new BlogEntry("Test 1", "This is a test");
        BlogEntry testEntryTwo = new BlogEntry("Test 2", "This is a second test");
        dao.addEntry(testEntryOne);
        dao.addEntry(testEntryTwo);

        get("/", (req, res) -> {
            Map<String, Object> model = new HashMap<>();
            model.put("entries", dao.findAllEntries());
            System.out.println("Num entries: " + dao.findAllEntries().size());
            return new ModelAndView(model, "index.hbs");
        }, new HandlebarsTemplateEngine());

//        get("/ideas", (req, res) -> {
//            Map<String, Object> model = new HashMap<>();
//            model.put("ideas", dao.findAll());
//            model.put("flashMessage", captureFlashMessage(req));
//            return new ModelAndView(model, "ideas.hbs");
//        }, new HandlebarsTemplateEngine());

        get("/index", (req, res) -> {
            res.redirect("/");
            return null;
        });

        get("/new", (req, res) ->{
            Map<String, String> model = new HashMap<>();
            return new ModelAndView(model, "new.hbs");
        }, new HandlebarsTemplateEngine());

        post("/new", (req, res) ->{
            String title = req.queryParams("title");
            String body = req.queryParams("entry");
            BlogEntry blogEntry = new BlogEntry(title, body);
            dao.addEntry(blogEntry);
            res.redirect("/");
            return null;
        });

        get("/detail", (req, res) -> {
            Map<String, String> model = new HashMap<> ();
            return new ModelAndView(model, "detail.hbs");
        }, new HandlebarsTemplateEngine());

        get("/edit", (req, res) -> {
            Map<String, String> model = new HashMap<> ();
            return new ModelAndView(model, "edit.hbs");
        }, new HandlebarsTemplateEngine());

    }
}
