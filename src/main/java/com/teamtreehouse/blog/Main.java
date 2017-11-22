package com.teamtreehouse.blog;

import com.teamtreehouse.blog.dao.SimpleBlogDao;
import com.teamtreehouse.blog.model.BlogEntry;
import com.teamtreehouse.blog.model.Comment;
import spark.ModelAndView;
import spark.Request;
import spark.template.handlebars.HandlebarsTemplateEngine;

import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import static spark.Spark.*;

/**
 * Created by Ross on 09/11/2017.
 */

public class Main {

    private static final DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss");

    public static void main(String[] args) {

        staticFileLocation("/public");
        SimpleBlogDao dao = new SimpleBlogDao();
        //Pre-populated dao for testing and for project where we need at least 3 projects
        prepopulateDao(dao);

        before((req, res) ->{
            if(req.cookie("password")!=null){
                req.attribute("password", req.cookie("password"));
            }
        });

        before("/new", (req, res) ->{
            if (req.attribute("password")==null){
                // set flash message here that the password is incorrect or has not been entered
                res.redirect("/password");
                halt();
            }

            if(!req.attribute("password").equals("admin")){
                System.out.println("You have entered the wrong password");
                res.redirect("/password");
            }
        });

        before("/edit/:slug", (req, res) ->{
            if (req.attribute("password") != "admin"){
                // set flash message here that the password is incorrect or has not been entered
                res.redirect("/password");
                halt();
            }
        });

        get("/", (req, res) -> {
            Map<String, Object> model = new HashMap<>();
            model.put("entries", dao.findAllEntries());
            return new ModelAndView(model, "index.hbs");
        }, new HandlebarsTemplateEngine());

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

        get("/detail/:slug", (req, res) ->{
            Map<String, Object> model = new HashMap<>();
            model.put("entry", dao.findEntryBySlug(req.params("slug")));
            return new ModelAndView(model, "detail.hbs");
        }, new HandlebarsTemplateEngine());

        post("/detail/:slug", (req, res) ->{
            String title = req.queryParams("name");
            String body = req.queryParams("comment");
            Comment comment = new Comment(title, body);
            BlogEntry entry = dao.findEntryBySlug(req.params("slug"));
            entry.addComment(comment);
            res.redirect("/detail/" + req.params("slug"));
            return null;
        });

        get("/edit/:slug", (req, res) -> {
            Map<String, Object> model = new HashMap<> ();
            model.put("entry", dao.findEntryBySlug(req.params("slug")));
            return new ModelAndView(model, "edit.hbs");
        }, new HandlebarsTemplateEngine());

        post("/edit/:slug", (req, res) ->{
            String title = req.queryParams("title");
            String body = req.queryParams("entry");
            BlogEntry entry = dao.findEntryBySlug(req.params("slug"));
            entry.setTitle(title);
            entry.setBody(body);
            res.redirect("/detail/" + req.params("slug"));
            return null;
        });

        get("/password", (req, res) ->{
            Map<String, String> model = new HashMap<>();
            return new ModelAndView(model, "password.hbs");
        }, new HandlebarsTemplateEngine());

        post("/password", (req, res) -> {
            Map<String, String> model = new HashMap<>();
            String password = req.queryParams("password");
            res.cookie("password", password);
            res.redirect("/");
            return null;
        });

    }

    private static void prepopulateDao(SimpleBlogDao dao) {
        String blogEntryOneTitle = "Couling Island";
        String blogEntryOnePost = "Couling Island is an island 1.9 kilometres (1 nmi) long, lying 2 kilometres (1 nmi) " +
                "north of Islay in the William Scoresby Archipelago. \n" +
                "It was discovered and named by Discovery Investigations personnel on the William " +
                "Scoresby in February 1936.";
        String commentOneName = "Joe Bloggs";
        String commentOneBody = "Cool place, not somewhere I had heard of before!";
        Comment blogOneComment = new Comment(commentOneName, commentOneBody);
        BlogEntry blogEntryOne = new BlogEntry(blogEntryOneTitle, blogEntryOnePost);
        blogEntryOne.addComment(blogOneComment);

        String blogEntryTwoTitle = "Great South Pond";
        String blogEntryTwoPost = "Great South Pond is a 292-acre (1.2 km2) reservoir in Plymouth, Massachusetts, " +
                "in South Pond village. The pond is within the Eel River watershed, located southeast of Little South " +
                "Pond, west of South Triangle Pond, and north of Boot Pond.";
        String commentTwoName = "Jack Swanson";
        String commentTwoBody = "Interesting fact! Not something I was aware of before";
        Comment blogTwoComment = new Comment(commentTwoName, commentTwoBody);
        BlogEntry blogEntryTwo = new BlogEntry(blogEntryTwoTitle, blogEntryTwoPost);
        blogEntryTwo.addComment(blogTwoComment);

        String blogEntryThreeTitle = "Chrysler Valiant (AP6)";
        String blogEntryThreePost = "The Chrysler Valiant AP6 is an automobile which was produced by Chrysler Australia" +
                " from 1965 to 1966.[1] It was the fourth Chrysler Valiant model produced in Australia.";
        String commentThreeName = "Melanie Clarkson";
        String commentThreeBody = "This is one of my favourite retro cars!";
        Comment blogThreeComment = new Comment(commentThreeName, commentThreeBody);
        BlogEntry blogEntryThree = new BlogEntry(blogEntryThreeTitle, blogEntryThreePost);
        blogEntryThree.addComment(blogThreeComment);

        dao.addEntry(blogEntryOne);
        dao.addEntry(blogEntryTwo);
        dao.addEntry(blogEntryThree);
    }
}
