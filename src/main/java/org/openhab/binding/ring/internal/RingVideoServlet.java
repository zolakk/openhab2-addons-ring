/**
 * Copyright (c) 2010-2020 Contributors to the openHAB project
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

import static org.openhab.binding.ring.RingBindingConstants.*;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.net.URLConnection;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.openhab.binding.ring.internal.data.Profile;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.http.HttpService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Main OSGi service and HTTP servlet for Ring Video
 *
 * @author Peter Mietlowski (zolakk) - Initial contribution
 */
@Component(service = HttpServlet.class)
public class RingVideoServlet extends HttpServlet {

    private static final long serialVersionUID = -5592161948589682812L;

    private final Logger logger = LoggerFactory.getLogger(RingVideoServlet.class);

    private Profile profile;
    /**
     * The RestClient is used to connect to the Ring Account.
     */
    private RestClient restClient;

    public RingVideoServlet() {

    }

    public RingVideoServlet(HttpService httpService, Profile profile) {
        this.profile = profile;
        restClient = new RestClient();
        try {
            httpService.registerServlet(SERVLET_VIDEO_PATH, this, null, httpService.createDefaultHttpContext());
        } catch (Exception e) {
            logger.warn("Register servlet fails", e);
        }
    }

    @SuppressWarnings("null")
    @Override
    protected void service(HttpServletRequest request, HttpServletResponse resp) throws ServletException, IOException {

        InputStream reader = null;
        OutputStream writer = null;
        try {
            String ipAddress = request.getHeader("HTTP_X_FORWARDED_FOR");
            if (ipAddress == null) {
                ipAddress = request.getRemoteAddr();
            }
            String path = request.getRequestURI().substring(0, SERVLET_VIDEO_PATH.length());
            logger.trace("RingVideo: Reqeust from {}:{}{} ({}:{}, {})", ipAddress, request.getRemotePort(), path,
                    request.getRemoteHost(), request.getServerPort(), request.getProtocol());
            if (!request.getMethod().equalsIgnoreCase(HTTP_METHOD_GET)) {
                logger.error("RingVideo: Unexpected method='{}'", request.getMethod());
            }
            if (!path.equalsIgnoreCase(SERVLET_VIDEO_PATH)) {
                logger.error("RingVideo: Invalid request received - path = {}", path);
                return;
            }

            String uri = request.getRequestURI().substring(request.getRequestURI().lastIndexOf("/") + 1);
            String videoUrl = restClient.getRecordingURL(uri, profile);
            logger.debug("RingVideo: {} image '{}' from '{}'", request.getMethod(), uri, videoUrl);
            resp.setContentType(SERVLET_VIDEO_MIME_TYPE);
            resp.setHeader("Access-Control-Allow-Origin", "*");

            URL url = new URL(videoUrl);
            URLConnection conn = url.openConnection();
            conn.setDoInput(true);
            conn.setDoOutput(true);
            reader = conn.getInputStream();
            writer = resp.getOutputStream();

            // read data in 4k chunks
            byte[] data = new byte[4096];
            int n;
            while (((n = reader.read(data)) != -1)) {
                writer.write(data, 0, n);
            }

        } catch (Exception e) {
            logger.error("RingVideo: Unable to process request: {}", e.getMessage());
        } finally {
            if (writer != null) {
                writer.flush();
                writer.close();
            }
            if (reader != null) {
                reader.close();
            }
        }
    }
}
