package com.joesbigidea.allowancetracker;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.JsonNode;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;
import spark.Request;
import spark.Response;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import javax.xml.bind.DatatypeConverter;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.Charset;
import java.security.MessageDigest;
import java.util.Base64;
import java.util.Date;
import java.util.Map;

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
            } else {
                staticFileLocation("web-content");
            }

            secure(System.getProperty("configPath") + "/keystore", System.getProperty("keystorePass"), null, null);

            get("/hello", (req, res) -> "Hello World");
            get("account/transactions", (req, res) -> accountController.getTransactions(), new JsonTransformer());
            get("account/balance", (req, res) -> accountController.getBalance());
            post("account/transactions", TrackerRunner::postTransaction, new JsonTransformer());
            post("fblogin", TrackerRunner::processFbLogin);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public static Transaction postTransaction(Request req, Response res) {
        Transaction newTransaction = new Gson().fromJson(req.body(), Transaction.class);
        newTransaction.setPostedDate(new Date());
        return accountController.addTransaction(newTransaction);
    }

    public static Object processFbLogin(Request req, Response res) {
        Map<String, String> fbInfo = new Gson().fromJson(req.body(), Map.class);
        new FBIntegrationHandler().processFbLogin(fbInfo.get("accessToken"));
        return "";
    }
}
