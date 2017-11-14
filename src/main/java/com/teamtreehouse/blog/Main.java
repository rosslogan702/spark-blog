package com.teamtreehouse.blog;

import spark.ModelAndView;
import spark.template.handlebars.HandlebarsTemplateEngine;

import java.util.HashMap;
import java.util.Map;

import static spark.Spark.get;
import static spark.Spark.staticFileLocation;

/**
 * Created by Ross on 09/11/2017.
 */

public class Main {

    public static void main(String[] args) {

        staticFileLocation("/public");

        get("/", (req, res) -> {
            Map<String, String> model = new HashMap<>();
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
