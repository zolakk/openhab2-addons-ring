/**
 * Copyright (c) 2010-2018 by the respective copyright holders.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.openhab.binding.ring.internal;

import static org.openhab.binding.ring.RingBindingConstants.*;

import java.util.HashSet;
import java.util.Set;

import org.eclipse.smarthome.core.net.NetworkAddressService;
import org.eclipse.smarthome.core.thing.Thing;
import org.eclipse.smarthome.core.thing.ThingTypeUID;
import org.eclipse.smarthome.core.thing.binding.BaseThingHandlerFactory;
import org.eclipse.smarthome.core.thing.binding.ThingHandler;
import org.eclipse.smarthome.core.thing.binding.ThingHandlerFactory;
import org.openhab.binding.ring.handler.AccountHandler;
import org.openhab.binding.ring.handler.ChimeHandler;
import org.openhab.binding.ring.handler.DoorbellHandler;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * The {@link RingHandlerFactory} is responsible for creating things and thing
 * handlers.
 *
 * @author Wim Vissers - Initial contribution
 */
@Component(service = { ThingHandlerFactory.class,
        RingHandlerFactory.class }, immediate = true, configurationPid = "binding.ring")
public class RingHandlerFactory extends BaseThingHandlerFactory {

    private final Set<ThingTypeUID> SUPPORTED_THING_TYPES_UIDS;
    private NetworkAddressService networkAddressService;

    public RingHandlerFactory() {
        SUPPORTED_THING_TYPES_UIDS = new HashSet<>();
        SUPPORTED_THING_TYPES_UIDS.add(THING_TYPE_ACCOUNT);
        SUPPORTED_THING_TYPES_UIDS.add(THING_TYPE_DOORBELL);
        SUPPORTED_THING_TYPES_UIDS.add(THING_TYPE_CHIME);
    }

    @Override
    public boolean supportsThingType(ThingTypeUID thingTypeUID) {
        return SUPPORTED_THING_TYPES_UIDS.contains(thingTypeUID);
    }

    @Override
    protected ThingHandler createHandler(Thing thing) {

        ThingTypeUID thingTypeUID = thing.getThingTypeUID();

        if (thingTypeUID.equals(THING_TYPE_ACCOUNT)) {
            return new AccountHandler(thing, networkAddressService);
        } else if (thingTypeUID.equals(THING_TYPE_DOORBELL)) {
            return new DoorbellHandler(thing);
        } else if (thingTypeUID.equals(THING_TYPE_CHIME)) {
            return new ChimeHandler(thing);
        }

        return null;
    }

    @Reference
    protected void setNetworkAddressService(NetworkAddressService networkAddressService) {
        this.networkAddressService = networkAddressService;
    }

    protected void unsetNetworkAddressService(NetworkAddressService networkAddressService) {
        this.networkAddressService = null;
    }
}
