/**
 * Copyright (c) 2010-2018 by the respective copyright holders.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.openhab.binding.ring.internal.data;

import org.openhab.binding.ring.internal.ApiConstants;

/**
 * @author Wim Vissers - Initial contribution
 */
public class DataFactory {

    /**
     * Get GET parameters for the session API resource.
     *
     * @return
     */
    public static String getSessionParams(String hardwareId) {
        ParamBuilder pb = new ParamBuilder(false);
        pb.add("device[os]", "android");
        pb.add("device[hardware_id]", hardwareId);
        pb.add("device[app_brand]", "ring");
        pb.add("device[metadata][device_model]", "VirtualBox");
        pb.add("device[metadata][resolution]", "600x800");
        pb.add("device[metadata][app_version]", "1.7.29");
        pb.add("device[metadata][app_instalation_date]", "");
        pb.add("device[metadata][os_version]", "4.4.4");
        pb.add("device[metadata][manufacturer]", "innotek GmbH");
        pb.add("device[metadata][is_tablet]", "true");
        pb.add("device[metadata][linphone_initialized]", "true");
        pb.add("device[metadata][language]", "en");
        pb.add("api_version", "" + ApiConstants.API_VERSION);
        return pb.toString();
    }

    /**
     * Get GET parameters for the ring_devices API resource.
     *
     * @return
     */
    public static String getDevicesParams(Profile profile) {
        ParamBuilder pb = new ParamBuilder(false);
        pb.add("auth_token", profile.getAuthenticationToken());
        pb.add("api_version", "" + ApiConstants.API_VERSION);
        return pb.toString();
    }

    /**
     * Get GET parameters for the ring_history API resource.
     *
     * @return
     */
    public static String getHistoryParams(Profile profile, int limit) {
        ParamBuilder pb = new ParamBuilder(false);
        pb.add("auth_token", profile.getAuthenticationToken());
        pb.add("api_version", "" + ApiConstants.API_VERSION);
        pb.add("limit", "" + limit);
        return pb.toString();
    }

    /**
     * Construct the url to retrieve the recorded video.
     *
     * @param profile the user profile for the authentication token.
     * @param ringEvent the ring event for the id.
     * @return a url to the recorded video.
     */
    public static String getDingVideoUrl(Profile profile, RingEvent ringEvent) {
        StringBuilder b = new StringBuilder();
        b.append(ApiConstants.URL_RECORDING_START).append(ringEvent.getEventId()).append(ApiConstants.URL_RECORDING_END)
                .append("?api_version=").append(ApiConstants.API_VERSION).append("&auth_token=")
                .append(profile.getAuthenticationToken());
        return b.toString();
    }

}
