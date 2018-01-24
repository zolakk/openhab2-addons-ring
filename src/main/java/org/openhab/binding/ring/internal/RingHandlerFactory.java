/**
 * Copyright (c) 2014-2016 by the respective copyright holders.
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

import org.eclipse.smarthome.core.thing.Thing;
import org.eclipse.smarthome.core.thing.ThingTypeUID;
import org.eclipse.smarthome.core.thing.binding.BaseThingHandlerFactory;
import org.eclipse.smarthome.core.thing.binding.ThingHandler;
import org.openhab.binding.ring.handler.AccountHandler;
import org.openhab.binding.ring.handler.ChimeHandler;
import org.openhab.binding.ring.handler.DoorbellHandler;

/**
 * The {@link RingHandlerFactory} is responsible for creating things and thing
 * handlers.
 *
 * @author Wim Vissers - Initial contribution
 */
public class RingHandlerFactory extends BaseThingHandlerFactory {

    private final Set<ThingTypeUID> SUPPORTED_THING_TYPES_UIDS;

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
            return new AccountHandler(thing);
        } else if (thingTypeUID.equals(THING_TYPE_DOORBELL)) {
            return new DoorbellHandler(thing);
        } else if (thingTypeUID.equals(THING_TYPE_CHIME)) {
            return new ChimeHandler(thing);
        }

        return null;
    }
}
