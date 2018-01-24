/**
 * Copyright (c) 2010-2017 by the respective copyright holders.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.openhab.binding.ring.internal;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.openhab.binding.ring.internal.data.DataFactory;
import org.openhab.binding.ring.internal.data.Profile;
import org.openhab.binding.ring.internal.data.RingDevices;
import org.openhab.binding.ring.internal.data.RingEvent;
import org.openhab.binding.ring.internal.errors.AuthenticationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Wim Vissers - Initial contribution
 */
public class RestClient {

    private static final int CONNECTION_TIMEOUT = 12000;
    private final Logger logger = LoggerFactory.getLogger(RestClient.class);

    private static final String METHOD_POST = "POST";
    private static final String METHOD_GET = "GET";

    // The factory to create data elements
    // private DataElementFactory factory;

    /**
     * Create a new client with the given server and port address.
     *
     * @param endPoint
     */
    public RestClient() {
        logger.info("Creating Ring client for API version {} on endPoint {}", ApiConstants.API_VERSION,
                ApiConstants.API_BASE);
    }

    /**
     * Post data to given url
     *
     * @param url
     * @param data
     * @param unamePassword username:password if applicable, otherwise null
     * @return the servers response
     * @throws AuthenticationException
     */
    private String postRequest(String resourceUrl, String data, String unamePassword) throws AuthenticationException {
        String result = null;
        try {
            byte[] postData = data.getBytes(StandardCharsets.UTF_8);
            int postDataLength = postData.length;
            StringBuilder output = new StringBuilder();
            URL url = new URL(resourceUrl);
            HttpsURLConnection conn = (HttpsURLConnection) url.openConnection();
            conn.setDoInput(true);
            conn.setUseCaches(false);
            conn.setRequestProperty("User-Agent", "openHAB Ring binding");
            conn.setHostnameVerifier(new HostnameVerifier() {
                @Override
                public boolean verify(String hostname, SSLSession session) {
                    return true;
                }
            });
            // SSL setting
            SSLContext context = SSLContext.getInstance("TLS");
            context.init(null, new TrustManager[] { new javax.net.ssl.X509TrustManager() {
                @Override
                public X509Certificate[] getAcceptedIssuers() {
                    return null;
                }

                @Override
                public void checkServerTrusted(X509Certificate[] chain, String authType) throws CertificateException {
                }

                @Override
                public void checkClientTrusted(X509Certificate[] chain, String authType) throws CertificateException {
                }
            } }, null);
            conn.setSSLSocketFactory(context.getSocketFactory());
            conn.setRequestMethod(METHOD_POST);

            // Add basic authentication if requested
            if (unamePassword != null) {
                String encoding = Base64.getEncoder().encodeToString(unamePassword.getBytes());
                conn.setRequestProperty("Authorization", "Basic " + encoding);
            }

            conn.setRequestProperty("cache-control", "no-cache");
            // conn.setRequestProperty("Content-type", "application/json");
            conn.setRequestProperty("Content-length", "" + postDataLength);
            conn.setDoOutput(true);
            conn.setConnectTimeout(CONNECTION_TIMEOUT);

            OutputStream out = conn.getOutputStream();
            out.write(postData);
            switch (conn.getResponseCode()) {
                case 200:
                case 201:
                    break;
                case 400:
                case 401:
                    throw new AuthenticationException("Invalid username or password.");
                default:
                    logger.error("Unhandled http response code: {}", conn.getResponseCode());
                    throw new RuntimeException("Failed : HTTP error code : " + conn.getResponseCode());
            }
            logger.debug("RestApi resource: {}, response code: {}.", resourceUrl, conn.getResponseCode());
            BufferedReader br = new BufferedReader(new InputStreamReader((conn.getInputStream())));
            String line;
            while ((line = br.readLine()) != null) {
                output.append(line);
            }
            conn.disconnect();
            result = output.toString();
            logger.debug("RestApi response: {}.", result);
        } catch (IOException | KeyManagementException | NoSuchAlgorithmException ex) {
            ex.printStackTrace();
        }
        return result;
    }

    /**
     * Get data from given url
     *
     * @param url
     * @param data
     * @return the servers response
     * @throws AuthenticationException
     */
    private String getRequest(String resourceUrl, String data) throws AuthenticationException {
        String result = null;
        try {
            StringBuilder output = new StringBuilder();
            URL url = new URL(resourceUrl + "?" + data);
            HttpsURLConnection conn = (HttpsURLConnection) url.openConnection();
            conn.setDoInput(true);
            conn.setUseCaches(false);
            conn.setRequestProperty("User-Agent", "openHAB Ring binding");
            conn.setHostnameVerifier(new HostnameVerifier() {
                @Override
                public boolean verify(String hostname, SSLSession session) {
                    return true;
                }
            });
            // SSL setting
            SSLContext context = SSLContext.getInstance("TLS");
            context.init(null, new TrustManager[] { new javax.net.ssl.X509TrustManager() {
                @Override
                public X509Certificate[] getAcceptedIssuers() {
                    return null;
                }

                @Override
                public void checkServerTrusted(X509Certificate[] chain, String authType) throws CertificateException {
                }

                @Override
                public void checkClientTrusted(X509Certificate[] chain, String authType) throws CertificateException {
                }
            } }, null);
            conn.setSSLSocketFactory(context.getSocketFactory());
            conn.setRequestMethod(METHOD_GET);

            conn.setRequestProperty("cache-control", "no-cache");
            // conn.setRequestProperty("Content-type", "application/json");
            // conn.setRequestProperty("Content-length", "" + postDataLength);
            conn.setDoOutput(true);
            conn.setConnectTimeout(12000);

            switch (conn.getResponseCode()) {
                case 200:
                case 201:
                    break;
                case 400:
                case 401:
                    throw new AuthenticationException("Invalid request.");
                default:
                    logger.error("Unhandled http response code: {}", conn.getResponseCode());
                    throw new RuntimeException("Failed : HTTP error code : " + conn.getResponseCode());
            }
            logger.debug("RestApi resource: {}, response code: {}.", resourceUrl, conn.getResponseCode());
            BufferedReader br = new BufferedReader(new InputStreamReader((conn.getInputStream())));
            String line;
            while ((line = br.readLine()) != null) {
                output.append(line);
            }
            conn.disconnect();
            result = output.toString();
            logger.debug("RestApi response: {}.", result);
        } catch (IOException | KeyManagementException | NoSuchAlgorithmException ex) {
            ex.printStackTrace();
        }
        return result;
    }

    /**
     * Get a (new) authenticated profile.
     *
     * @param username the username of the Ring account.
     * @param password the password for the Ring account.
     * @param hardwareId a hardware ID (must be unique for every piece of hardware used).
     * @return a Profile instance with available data stored in it.
     * @throws AuthenticationException
     * @throws ParseException
     */
    public Profile getAuthenticatedProfile(String username, String password, String hardwareId)
            throws AuthenticationException, ParseException {
        String jsonResult = postRequest(ApiConstants.URL_SESSION, DataFactory.getSessionParams(hardwareId),
                username + ":" + password);
        JSONObject obj = (JSONObject) new JSONParser().parse(jsonResult);
        return new Profile((JSONObject) obj.get("profile"));
    }

    /**
     * Get the RingDevices instance, given the authenticated Profile.
     *
     * @param profile the Profile previously retrieved when authenticating.
     * @return the RingDevices instance filled with all available data.
     * @throws AuthenticationException when request is invalid.
     * @throws ParseException when response is invalid JSON.
     */
    public RingDevices getRingDevices(Profile profile, RingAccount ringAccount)
            throws ParseException, AuthenticationException {
        String jsonResult = getRequest(ApiConstants.URL_DEVICES, DataFactory.getDevicesParams(profile));
        JSONObject obj = (JSONObject) new JSONParser().parse(jsonResult);
        return new RingDevices(obj, ringAccount);
    }

    /**
     * Get a List with the last recorded events, newest on top.
     *
     * @param profile the Profile previously retrieved when authenticating.
     * @param limit the maximum number of events.
     * @return
     * @throws AuthenticationException
     * @throws ParseException
     */
    public synchronized List<RingEvent> getHistory(Profile profile, int limit)
            throws AuthenticationException, ParseException {
        String jsonResult = getRequest(ApiConstants.URL_HISTORY, DataFactory.getHistoryParams(profile, limit));
        JSONArray obj = (JSONArray) new JSONParser().parse(jsonResult);
        List<RingEvent> result = new ArrayList<>(limit);
        for (Object jsonEvent : obj.toArray()) {
            result.add(new RingEvent((JSONObject) jsonEvent));
        }
        return result;
    }

}
