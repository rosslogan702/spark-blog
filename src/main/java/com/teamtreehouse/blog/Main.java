package com.teamtreehouse.blog;

import com.teamtreehouse.blog.dao.SimpleBlogDao;
import com.teamtreehouse.blog.model.BlogEntry;
import com.teamtreehouse.blog.model.Comment;
import spark.ModelAndView;
import spark.Request;
import spark.Response;
import spark.template.handlebars.HandlebarsTemplateEngine;
import java.time.format.DateTimeFormatter;
import java.util.*;

import static spark.Spark.*;

/**
 * Created by Ross on 09/11/2017.
 */

public class Main {

    private static final DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss");
    private static String reqURI = "";
    private static final String FLASH_MESSAGE_KEY = "flash_message";


    public static void main(String[] args) {
        staticFileLocation("/public");
        SimpleBlogDao dao = new SimpleBlogDao();
        prepopulateDao(dao);

        before((req, res) ->{
            if(req.cookie("password")!=null){
                req.attribute("password", req.cookie("password"));
            }
        });

        before("/new", (req, res) -> checkForPasswordAndRedirect(req, res));

        before("/edit/:slug", (req, res) -> checkForPasswordAndRedirect(req, res));

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
            String [] tagCSV = req.queryParams("tags").split(",");
            Set<String> tags = new HashSet<>();
            for(String tag: tagCSV){
                tags.add(tag);
            }
            BlogEntry blogEntry = new BlogEntry(title, body, tags);
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
            BlogEntry entry = dao.findEntryBySlug(req.params("slug"));
            model.put("entry", entry);
            return new ModelAndView(model, "edit.hbs");
        }, new HandlebarsTemplateEngine());

        post("/edit/:slug", (req, res) ->{
            String title = req.queryParams("title");
            String body = req.queryParams("entry");
            String [] tagCSV = req.queryParams("tags").split(",");
            Set<String> tags = new LinkedHashSet<>();
            for(String tag: tagCSV){
                tags.add(tag.trim());
            }
            BlogEntry entry = dao.findEntryBySlug(req.params("slug"));
            entry.setTitle(title);
            entry.setBody(body);
            entry.setTags(tags);
            res.redirect("/detail/" + req.params("slug"));
            return null;
        });

        post("/delete/:slug", (req, res) ->{
            BlogEntry entry = dao.findEntryBySlug(req.params("slug"));
            dao.deleteEntry(entry);
            res.redirect("/");
            return null;
        });

        get("/password", (req, res) ->{
            Map<String, String> model = new HashMap<>();
            model.put("flashMessage", captureFlashMessage(req));
            return new ModelAndView(model, "password.hbs");
        }, new HandlebarsTemplateEngine());

        post("/password", (req, res) -> {
            Map<String, String> model = new HashMap<>();
            String password = req.queryParams("password");
            res.cookie("password", password);
            res.redirect(reqURI);
            return null;
        });

    }

    private static void setFlashMessage(Request req,String message) {
        req.session().attribute(FLASH_MESSAGE_KEY,message);
    }
    private static String getFlashMessage(Request req) {
        if(req.session(false) == null) {
            return null;
        }
        if(!req.session().attributes().contains(FLASH_MESSAGE_KEY)) {
            return null;
        }
        return (String) req.session().attribute(FLASH_MESSAGE_KEY);
    }
    private static String captureFlashMessage(Request req) {
        String message = getFlashMessage(req);
        if(message !=null) {
            req.session().removeAttribute(FLASH_MESSAGE_KEY);
        }
        return message;
    }

    private static void checkForPasswordAndRedirect(Request req, Response res) {
        reqURI = req.uri();
        if (req.attribute("password")==null){
            setFlashMessage(req,"Please enter the password to sign-in");
            res.redirect("/password");
            halt();
        }

        if(!req.attribute("password").equals("admin")){
            setFlashMessage(req,"Incorrect password entered, please re-enter password to sign-in");
            res.redirect("/password");
            halt();
        }
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
        Set<String> blogEntryOneTags = new HashSet<>(Arrays.asList("Island", "Discovery", "Investigation"));
        BlogEntry blogEntryOne = new BlogEntry(blogEntryOneTitle, blogEntryOnePost, blogEntryOneTags);
        blogEntryOne.addComment(blogOneComment);

        String blogEntryTwoTitle = "Great South Pond";
        String blogEntryTwoPost = "Great South Pond is a 292-acre (1.2 km2) reservoir in Plymouth, Massachusetts, " +
                "in South Pond village. The pond is within the Eel River watershed, located southeast of Little South " +
                "Pond, west of South Triangle Pond, and north of Boot Pond.";
        String commentTwoName = "Jack Swanson";
        String commentTwoBody = "Interesting fact! Not something I was aware of before";
        Comment blogTwoComment = new Comment(commentTwoName, commentTwoBody);
        BlogEntry blogEntryTwo = new BlogEntry(blogEntryTwoTitle, blogEntryTwoPost, new HashSet<>());
        blogEntryTwo.addComment(blogTwoComment);

        String blogEntryThreeTitle = "Chrysler Valiant (AP6)";
        String blogEntryThreePost = "The Chrysler Valiant AP6 is an automobile which was produced by Chrysler Australia" +
                " from 1965 to 1966.[1] It was the fourth Chrysler Valiant model produced in Australia.";
        String commentThreeName = "Melanie Clarkson";
        String commentThreeBody = "This is one of my favourite retro cars!";
        Comment blogThreeComment = new Comment(commentThreeName, commentThreeBody);
        Set<String> blogEntryThreeTags = new HashSet<>(Arrays.asList("Cars", "Chrysler"));
        BlogEntry blogEntryThree = new BlogEntry(blogEntryThreeTitle, blogEntryThreePost, blogEntryThreeTags);
        blogEntryThree.addComment(blogThreeComment);

        dao.addEntry(blogEntryOne);
        dao.addEntry(blogEntryTwo);
        dao.addEntry(blogEntryThree);
    }
}
