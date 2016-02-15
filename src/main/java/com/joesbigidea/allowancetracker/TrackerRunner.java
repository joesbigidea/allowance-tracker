package com.joesbigidea.allowancetracker;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import spark.Request;
import spark.Response;

import java.math.BigDecimal;
import java.util.Date;

import static spark.Spark.*;

/**
 * Created by joe on 2/13/16.
 */
public class TrackerRunner {
    private static AccountController accountController;

    public static void main(String[] args) {
        try {
            PersistenceService.init();
            accountController = new AccountController();

            String staticLocation = System.getProperty("staticLocation");
            if (staticLocation != null) {
                externalStaticFileLocation(staticLocation);
            }
            else {
                staticFileLocation("web-content");
            }

            get("/hello", (req, res) -> "Hello World");
            get("account/transactions", (req, res) -> accountController.getTransactions(), new JsonTransformer());
            get("account/balance", (req, res) -> accountController.getBalance());
            post("account/transactions", "multipart/form-data", TrackerRunner::postTransaction, new JsonTransformer());
        }
        catch (Exception e) {
            e.printStackTrace();
        }

    }

    public static Transaction postTransaction(Request req, Response res) {
        Transaction newTransaction = new Gson().fromJson(req.body(), Transaction.class);
        newTransaction.setPostedDate(new Date());
        return accountController.addTransaction(newTransaction);
    }
}
