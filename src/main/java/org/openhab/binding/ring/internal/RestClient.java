/**
 * Copyright (c) 2010-2019 Contributors to the openHAB project
 *
 * See the NOTICE file(s) distributed with this work for additional
 * information.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 */
package org.openhab.binding.ring.internal;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.HashMap;
//import java.util.Base64;
import java.util.List;
import java.util.Map;
import java.util.StringJoiner;

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
 * @author Pete Mietlowski - Updated authentication routines
 * @author Chris Milbert - Stickupcam contribution
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
     *
     */

    private String postRequest(String resourceUrl, String data, String oauth_token) throws AuthenticationException {
        String result = null;
        try {
            byte[] postData = data.getBytes(StandardCharsets.UTF_8);
            StringBuilder output = new StringBuilder();
            URL url = new URL(resourceUrl);
            HttpsURLConnection conn = (HttpsURLConnection) url.openConnection();
            conn.setDoInput(true);
            conn.setUseCaches(false);
            conn.setRequestProperty("User-Agent", "Dalvik/1.6.0 (Linux; Android 4.4.4; Build/KTU84Q)");
            conn.setRequestProperty("Authorization", "Bearer " + oauth_token);
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

            // conn.setRequestProperty("Content-type", "application/x-www-form-urlencoded; charset: UTF-8");
            conn.setRequestProperty("X-API-LANG", "en");
            conn.setRequestProperty("Content-length", "gzip, deflate");
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
                    throw new AuthenticationException("Invalid username or password");
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
            logger.error("ERROR!", ex);
            // ex.printStackTrace();
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
            conn.setRequestProperty("User-Agent", "Dalvik/1.6.0 (Linux; Android 4.4.4; Build/KTU84Q)");
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
                    // break;
                    throw new AuthenticationException("Invalid request");
                default:
                    logger.error("Unhandled http response code: {}", conn.getResponseCode());
                    throw new RuntimeException("Failed : HTTP error code : " + conn.getResponseCode());
            }

            if (conn.getResponseCode() != 200) {
                logger.debug("RestApi resource: {}, response code: {}.", resourceUrl, conn.getResponseCode());
            }
            BufferedReader br = new BufferedReader(new InputStreamReader((conn.getInputStream())));
            String line;
            while ((line = br.readLine()) != null) {
                output.append(line);
            }
            conn.disconnect();
            result = output.toString();
            logger.debug("RestApi response: {}.", result);
        } catch (IOException | KeyManagementException | NoSuchAlgorithmException ex) {
            logger.error("ERROR!", ex);
            // ex.printStackTrace();
        }
        return result;
    }

    /**
     * Get a (new) authenticated profile.
     *
     * @param username   the username of the Ring account.
     * @param password   the password for the Ring account.
     * @param hardwareId a hardware ID (must be unique for every piece of hardware used).
     * @return a Profile instance with available data stored in it.
     * @throws AuthenticationException
     * @throws ParseException
     */
    public Profile getAuthenticatedProfile(String username, String password, String hardwareId)
            throws AuthenticationException, ParseException {
        JSONObject oauthToken = get_oauth_token(username, password);
        String jsonResult = postRequest(ApiConstants.URL_SESSION, DataFactory.getSessionParams(hardwareId),
                oauthToken.get("access_token").toString());
        JSONObject obj = (JSONObject) new JSONParser().parse(jsonResult);
        return new Profile((JSONObject) obj.get("profile"), oauthToken.get("refresh_token").toString());
    }

    /**
     * Get a (new) oAuth token.
     *
     * @param username the username of the Ring account.
     * @param password the password for the Ring account.
     * @return a JSONObject with the available data stored in it (access_token, refresh_token)
     * @throws AuthenticationException
     * @throws ParseException
     */
    private JSONObject get_oauth_token(String username, String password)
            throws AuthenticationException, ParseException {

        String result = null;
        JSONObject oauth_token = null;
        String resourceUrl = ApiConstants.API_OAUTH_ENDPOINT;
        try {
            Map<String, String> map = new HashMap<String, String>();

            map.put("client_id", "ring_official_android");
            map.put("grant_type", "password");
            map.put("scope", "client");
            map.put("username", username);
            map.put("password", password);

            URL url = new URL(resourceUrl);
            HttpsURLConnection conn = (HttpsURLConnection) url.openConnection();
            conn.setDoInput(true);
            conn.setUseCaches(false);
            conn.setRequestProperty("User-Agent", "Dalvik/1.6.0 (Linux; Android 4.4.4; Build/KTU84Q)");
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

            conn.setRequestProperty("Content-type", "application/x-www-form-urlencoded; charset: UTF-8");
            conn.setDoOutput(true);
            conn.setConnectTimeout(CONNECTION_TIMEOUT);

            StringJoiner sj = new StringJoiner("&");
            for (Map.Entry<String, String> entry : map.entrySet()) {
                sj.add(URLEncoder.encode(entry.getKey(), "UTF-8") + "=" + URLEncoder.encode(entry.getValue(), "UTF-8"));
            }
            byte[] out = sj.toString().getBytes(StandardCharsets.UTF_8);
            int length = out.length;

            conn.setFixedLengthStreamingMode(length);
            conn.connect();
            OutputStream os = conn.getOutputStream();
            os.write(out);

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

            result = readFullyAsString(conn.getInputStream(), "UTF-8");
            conn.disconnect();

            // JSONObject obj = (JSONObject) new JSONParser().parse(result);
            // oauth_token = obj.get("access_token").toString();
            oauth_token = (JSONObject) new JSONParser().parse(result);
            logger.debug("RestApi response: {}.", result);
        } catch (IOException | KeyManagementException | NoSuchAlgorithmException ex) {
            logger.error("ERROR!", ex);
            // ex.printStackTrace();
        }
        return oauth_token;
    }

    public String readFullyAsString(InputStream inputStream, String encoding) throws IOException {
        return readFully(inputStream).toString(encoding);
    }

    private ByteArrayOutputStream readFully(InputStream inputStream) throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        byte[] buffer = new byte[1024];
        int length = 0;
        while ((length = inputStream.read(buffer)) != -1) {
            baos.write(buffer, 0, length);
        }
        return baos;
    }

    public Boolean refresh_session(String refreshToken) {

        String result = null;
        String resourceUrl = ApiConstants.API_OAUTH_ENDPOINT;
        try {
            Map<String, String> map = new HashMap<String, String>();

            map.put("grant_type", "refresh_token");
            map.put("refresh_token", refreshToken);

            URL url = new URL(resourceUrl);
            HttpsURLConnection conn = (HttpsURLConnection) url.openConnection();
            conn.setDoInput(true);
            conn.setUseCaches(false);
            conn.setRequestProperty("User-Agent", "Dalvik/1.6.0 (Linux; Android 4.4.4; Build/KTU84Q)");
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

            conn.setRequestProperty("Content-type", "application/x-www-form-urlencoded; charset: UTF-8");
            conn.setDoOutput(true);
            conn.setConnectTimeout(CONNECTION_TIMEOUT);

            StringJoiner sj = new StringJoiner("&");
            for (Map.Entry<String, String> entry : map.entrySet()) {
                sj.add(URLEncoder.encode(entry.getKey(), "UTF-8") + "=" + URLEncoder.encode(entry.getValue(), "UTF-8"));
            }
            byte[] out = sj.toString().getBytes(StandardCharsets.UTF_8);
            int length = out.length;

            conn.setFixedLengthStreamingMode(length);
            conn.connect();
            OutputStream os = conn.getOutputStream();
            os.write(out);

            switch (conn.getResponseCode()) {
                case 200:
                case 201:
                    break;
                case 400:
                case 401:
                    return false;
                default:
                    logger.error("Unhandled http response code: {}", conn.getResponseCode());
                    return false;
            }
            logger.debug("RestApi resource: {}, response code: {}.", resourceUrl, conn.getResponseCode());

            result = readFullyAsString(conn.getInputStream(), "UTF-8");
            conn.disconnect();

            logger.debug("RestApi response: {}.", result);
        } catch (IOException | KeyManagementException | NoSuchAlgorithmException ex) {
            logger.error("ERROR!", ex);
            // ex.printStackTrace();
        }
        return true;
    }

    /**
     * Get the RingDevices instance, given the authenticated Profile.
     *
     * @param profile the Profile previously retrieved when authenticating.
     * @return the RingDevices instance filled with all available data.
     * @throws AuthenticationException when request is invalid.
     * @throws ParseException          when response is invalid JSON.
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
     * @param limit   the maximum number of events.
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
