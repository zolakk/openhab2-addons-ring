/**
 * Copyright (c) 2010-2018 by the respective copyright holders.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.openhab.binding.ring.internal;

/**
 * @author Wim Vissers - Initial contribution
 */
public class ApiConstants {

    public static final int API_VERSION = 9;

    // API resources
    public static final String API_BASE = "https://api.ring.com";
    public static final String URL_SESSION = API_BASE + "/clients_api/session";
    public static final String URL_DINGS = API_BASE + "/clients_api/dings/active";
    public static final String URL_DEVICES = API_BASE + "/clients_api/ring_devices";
    public static final String URL_HISTORY = API_BASE + "/clients_api/doorbots/history";
    public static final String URL_RECORDING_START = API_BASE + "/clients_api/dings/";
    public static final String URL_RECORDING_END = "/recording";

    // JSON data names for profile
    public static final String PROFILE_AUTHENTICATION_TOKEN = "authentication_token";
    public static final String PROFILE_ID = "id";
    public static final String PROFILE_EMAIL = "email";
    public static final String PROFILE_HARDWARE_ID = "hardware_id";
    public static final String PROFILE_FIRST_NAME = "first_name";
    public static final String PROFILE_LAST_NAME = "last_name";
    public static final String PROFILE_PHONE_NUMBER = "phone_number";
    public static final String PROFILE_USER_FLOW = "user_flow";
    public static final String PROFILE_EXPLORER_PROGRAM_TERMS = "explorer_program_terms";

    // JSON names for events
    public static final String EVENT_ID = "id";
    public static final String EVENT_CREATED_AT = "created_at";
    public static final String EVENT_ANSWERED = "answered";
    public static final String EVENT_EVENTS = "events";
    public static final String EVENT_KIND = "kind";
    public static final String EVENT_FAVORITE = "favorite";
    public static final String EVENT_SNAPSHOT_URL = "snapshot_url";
    public static final String EVENT_RECORDING = "recording";
    public static final String EVENT_DOORBOT = "doorbot";

    // JSON data names for ring devices
    public static final String DEVICES_DOORBOTS = "doorbots";
    public static final String DEVICES_AUTHORIZED_DOORBOTS = "authorized_doorbots";
    public static final String DEVICES_CHIMES = "chimes";
    public static final String DEVICES_STICKUP_CAMS = "stickup_cams";
    public static final String DEVICES_BASE_STATIONS = "base_stations";

    // JSON data names for generic devices
    public static final String DEVICE_ID = "id";
    public static final String DEVICE_DESCRIPTION = "description";
    public static final String DEVICE_DEVICE_ID = "device_id";
    public static final String DEVICE_FIRMWARE_VERSION = "firmware_version";
    public static final String DEVICE_TIME_ZONE = "time_zone";
    public static final String DEVICE_KIND = "kind";

}
