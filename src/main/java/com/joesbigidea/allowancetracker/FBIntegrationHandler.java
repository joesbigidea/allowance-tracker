package com.joesbigidea.allowancetracker;

import com.google.gson.Gson;
import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;
import spark.Request;
import spark.Response;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.Charset;
import java.util.Map;

import static spark.Spark.halt;

/**
 * Created by joe on 2/16/16.
 */
public class FBIntegrationHandler {
    public Object processFbLogin(String accessToken) {
        try {
            HttpResponse<String> fbResponse = Unirest.get("https://graph.facebook.com/me")
                    .queryString("access_token", accessToken)
                    .queryString("appsecret_proof", obtainAppSecretProof(accessToken, System.getProperty("fbappsecret"))).asString();
            Map<Object, Object> fbmap = new Gson().fromJson(fbResponse.getBody(), Map.class);
            fbmap.entrySet().forEach(e -> System.out.println("FB: " + e.getKey() + e.getValue()));
            System.out.println("ID FROM FB: " + fbmap);
        } catch (UnirestException e) {
            e.printStackTrace();
            halt(501);
        }

        return "";
    }

    public String obtainAppSecretProof(String accessToken, String appSecret) {

        try {
            byte[] key = appSecret.getBytes(Charset.forName("UTF-8"));
            SecretKeySpec signingKey = new SecretKeySpec(key, "HmacSHA256");
            Mac mac = Mac.getInstance("HmacSHA256");
            mac.init(signingKey);
            byte[] raw = mac.doFinal(accessToken.getBytes());
            byte[] hex = encodeHex(raw);
            return new String(hex, "UTF-8");
        } catch (Exception e) {
            throw new IllegalStateException("Creation of appsecret_proof has failed", e);
        }
    }

    /**
     * Encodes a hex {@code byte[]} from given {@code byte[]}.
     * <p>
     * This function is equivalent to Apache commons-codec binary {@code new Hex().encode(byte[])}
     *
     * @param data The data to encode as hex.
     * @return Hex-encoded {@code byte[]}
     * @throws NullPointerException If {@code data} is {@code null}.
     */
    public byte[] encodeHex(final byte[] data) {
        if (data == null)
            throw new NullPointerException("Parameter 'data' cannot be null.");

        final char[] toDigits = {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f'};
        final int l = data.length;
        final char[] out = new char[l << 1];
        for (int i = 0, j = 0; i < l; i++) {
            out[j++] = toDigits[(0xF0 & data[i]) >>> 4];
            out[j++] = toDigits[0x0F & data[i]];
        }

        return new String(out).getBytes(Charset.forName("UTF-8"));
    }
}
