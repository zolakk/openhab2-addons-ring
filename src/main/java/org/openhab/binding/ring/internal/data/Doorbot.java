/**
 * Copyright (c) 2010-2018 by the respective copyright holders.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.openhab.binding.ring.internal.data;

import org.json.simple.JSONObject;
import org.openhab.binding.ring.internal.ApiConstants;

/**
 * @author Wim Vissers - Initial contribution
 */
public class Doorbot {

    /**
     * The JSONObject contains the data retrieved from the Ring API,
     * or the data to send to the API.
     */
    protected JSONObject jsonObject;

    /**
     * Create from a JSONObject, example:
     * {
     * "id": 5047591,
     * "description": "Front Door"
     * }
     *
     * @param jsonObject
     */
    public Doorbot(JSONObject jsonObject) {
        this.jsonObject = jsonObject;
    }

    /**
     * Get the Doorbot id.
     *
     * @return the id.
     */
    @SuppressWarnings("unchecked")
    public String getId() {
        return jsonObject.getOrDefault(ApiConstants.DOORBOT_ID, "?").toString();
    }

    /**
     * Get the Doorbot description.
     *
     * @return the description.
     */
    @SuppressWarnings("unchecked")
    public String getDescription() {
        return jsonObject.getOrDefault(ApiConstants.DOORBOT_DESCRIPTION, "?").toString();
    }

}
