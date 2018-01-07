package com.joesbigidea.allowancetracker;

import com.google.gson.Gson;
import org.jboss.logging.Logger;
import org.slf4j.LoggerFactory;
import spark.Request;
import spark.Response;

import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Date;
import java.util.Properties;

import static spark.Spark.*;

/**
 * check out restfb
 */
public class TrackerRunner {
    private static AccountController accountController;

    public static void main(String[] args) {
        try {
            LoggerFactory.getLogger(TrackerRunner.class).info("Starting AllowanceLock Application");

            Properties props = new Properties();
            try (Reader reader = Files.newBufferedReader(Paths.get(System.getProperty("configPath")),StandardCharsets.UTF_8)) {
                props.load(reader);
            }
            PersistenceService.init(props);

            accountController = new AccountController();

            String staticLocation = System.getProperty("staticLocation");
            if (staticLocation != null) {
                externalStaticFileLocation(staticLocation);
            } else {
                staticFileLocation("web-content");
            }

            port(Integer.parseInt(props.getProperty("port", "4567")));
            //secure(props.getProperty("keystorePath"), props.getProperty("keystorePass"), null, null);

            before((req, res) -> {
                    Logger.getLogger("access").info(req.requestMethod() + " " + req.url() + " " + req.ip());
            });

            FBIntegrationHandler fbIntegrationHandler = new FBIntegrationHandler(props.getProperty("fbappsecret"));
            before((req, res) -> {
                if (req.pathInfo().contains("authorized") && !fbIntegrationHandler.isLoggedIn(req)) {
                    res.redirect("/login.html");
                }
            });

            get("/", (req, res) -> {
                res.redirect("authorized/view-account.html");
                return "";
            } );
            post("fblogin", (req, res) -> fbIntegrationHandler.processFbLogin(req));
            get("authorized/account/transactions", (req, res) -> accountController.getTransactions(), new JsonTransformer());
            get("authorized/account/balance", (req, res) -> accountController.getBalance());
            post("authorized/account/transactions", TrackerRunner::postTransaction, new JsonTransformer());
            delete("authorized/account/transactions/:id", TrackerRunner::deleteTransaction);
            get("fbappid", (req, res) -> props.getProperty("fbappid"));

            exception(IllegalArgumentException.class, TrackerRunner::handleValidationException);
            exception(NullPointerException.class, TrackerRunner::handleValidationException);
        } catch (Exception e) {
            e.printStackTrace();
            LoggerFactory.getLogger(TrackerRunner.class).error("Exception in main:", e);
        }

    }

    public static boolean deleteTransaction(Request req, Response res) {
        String id = req.params(":id");
        return accountController.deleteTransaction(Long.parseLong(id));
    }

    public static Transaction postTransaction(Request req, Response res) {
        Transaction newTransaction = new Gson().fromJson(req.body(), Transaction.class);
        newTransaction.setDescription(newTransaction.getDescription().replace("'", ""));
        newTransaction.setPostedDate(new Date());
        return accountController.addTransaction(newTransaction);
    }

    public static void handleValidationException(Exception e, Request req, Response resp) {
        LoggerFactory.getLogger(TrackerRunner.class).error("Validation Error", e);
        resp.status(400);
        resp.body("Failed validation: " + e.getMessage());
    }
}
