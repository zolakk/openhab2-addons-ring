/**
 * Copyright (c) 2010-2018 by the respective copyright holders.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.openhab.binding.ring.internal.data;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.openhab.binding.ring.internal.ApiConstants;
import org.openhab.binding.ring.internal.RingAccount;

/**
 *
 * @author Wim Vissers - Initial contribution
 */
public class RingDevices {

    private List<Doorbell> doorbells;

    /**
     * Create RingDevices instance from JSON Object.
     * {
     * "doorbots": [
     * {
     * "id": 5047591,
     * "description": "Front Door",
     * "device_id": "341513673f7a",
     * "time_zone": "Europe/Amsterdam",
     * "subscribed": true,
     * "subscribed_motions": true,
     * "battery_life": "93",
     * "external_connection": true,
     * "firmware_version": "Up to Date",
     * "kind": "doorbell_v3",
     * "latitude": 51.9436537,
     * "longitude": 4.5682205,
     * "address": "Nystadstraat 73, 3067 DT Rotterdam, Nederland",
     * "settings": {
     * "enable_vod": 1,
     * "exposure_control": 2,
     * "motion_zones": [
     * 1,
     * 1,
     * 1,
     * 1,
     * 1
     * ],
     * "motion_snooze_preset_profile": "low",
     * "motion_snooze_presets": [
     * "none",
     * "low",
     * "medium",
     * "high"
     * ],
     * "live_view_preset_profile": "middle",
     * "live_view_presets": [
     * "low",
     * "middle",
     * "high",
     * "highest"
     * ],
     * "pir_sensitivity_1": 0,
     * "vod_suspended": 0,
     * "doorbell_volume": 1,
     * "vod_status": "enabled"
     * },
     * "features": {
     * "motions_enabled": true,
     * "show_recordings": false,
     * "show_vod_settings": true
     * },
     * "owned": true,
     * "alerts": {
     * "connection": "online"
     * },
     * "motion_snooze": null,
     * "stolen": false,
     * "location_id": null,
     * "ring_id": null,
     * "owner": {
     * "id": 4445516,
     * "first_name": null,
     * "last_name": null,
     * "email": "xxx@acme.com"
     * }
     * }
     * ],
     * "authorized_doorbots": [],
     * "chimes": [],
     * "stickup_cams": [],
     * "base_stations": []
     * } *
     *
     * @param jsonRingDevices the JSON ring devices retrieved from the Ring API.
     */
    public RingDevices(JSONObject jsonRingDevices, RingAccount ringAccount) {
        addDoorbells((JSONArray) jsonRingDevices.get(ApiConstants.DEVICES_DOORBOTS), ringAccount);
    }

    /**
     * Helper method to create the doorbell list.
     *
     * @param doorbells
     */
    private final void addDoorbells(JSONArray jsonDoorbells, RingAccount ringAccount) {
        doorbells = new ArrayList<>();
        for (Object obj : jsonDoorbells) {
            Doorbell doorbell = new Doorbell((JSONObject) obj);
            doorbell.setRingAccount(ringAccount);
            doorbells.add(doorbell);
        }
    }

    /**
     * Retrieve the Doorbells Collection.
     *
     * @return
     */
    public Collection<Doorbell> getDoorbells() {
        return doorbells;
    }

    /**
     * Retrieve a collection of all devices.
     *
     * @return
     */
    public Collection<RingDevice> getRingDevices() {
        List<RingDevice> result = new ArrayList<>();
        result.addAll(doorbells);
        return result;
    }

}
