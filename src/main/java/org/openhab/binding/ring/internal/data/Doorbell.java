/**
 * Copyright (c) 2010-2018 by the respective copyright holders.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.openhab.binding.ring.internal.data;

import org.eclipse.smarthome.config.discovery.DiscoveryResult;
import org.eclipse.smarthome.config.discovery.DiscoveryResultBuilder;
import org.eclipse.smarthome.core.thing.ThingUID;
import org.json.simple.JSONObject;

/**
 * @author Wim Vissers - Initial contribution
 */
public class Doorbell extends AbstractRingDevice {

    /**
     * Create Doorbell instance from JSON object.
     *
     * @param jsonDoorbell the JSON doorbell (doorbot) retrieved from the Ring API.
     */
    public Doorbell(JSONObject jsonDoorbell) {
        super(jsonDoorbell);
    }

    /**
     * Get the DiscoveryResult object to identify the device as
     * discovered thing.
     *
     * @return the device as DiscoveryResult instance.
     */
    @Override
    public DiscoveryResult getDiscoveryResult() {
        DiscoveryResult result = DiscoveryResultBuilder.create(new ThingUID("ring:doorbell:" + getId()))
                .withLabel("Ring Video Doorbell").build();
        return result;
    }

}
