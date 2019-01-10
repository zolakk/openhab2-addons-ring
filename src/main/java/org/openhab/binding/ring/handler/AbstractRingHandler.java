/**
 * Copyright (c) 2010-2018 by the respective copyright holders.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.openhab.binding.ring.handler;

import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

import org.eclipse.smarthome.core.library.types.OnOffType;
import org.eclipse.smarthome.core.thing.Thing;
import org.eclipse.smarthome.core.thing.binding.BaseThingHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The {@link AbstractRingHandler} is responsible for handling commands, which are
 * sent to one of the channels.
 *
 * @author Wim Vissers - Initial contribution
 */
public abstract class AbstractRingHandler extends BaseThingHandler {

    // Current status
    protected OnOffType status;
    protected OnOffType enabled;
    protected Logger logger = LoggerFactory.getLogger(AbstractRingHandler.class);

    // Scheduler
    ScheduledFuture<?> refreshJob;

    /**
     * There is no default constructor. We have to define a
     * constructor with Thing object as parameter.
     *
     * @param thing
     */
    public AbstractRingHandler(Thing thing) {
        super(thing);
        status = OnOffType.OFF;
        enabled = OnOffType.ON;
    }

    @Override
    public void initialize() {
        logger.debug("Initializing AbstractRingHandler");
        // super.initialize();
    }

    /**
     * Refresh the state of channels that may have changed by
     * (re-)initialization.
     */
    protected abstract void refreshState();

    /**
     * Called every minute
     */
    protected abstract void minuteTick();

    /**
     * Check every 60 seconds if one of the alarm times is reached.
     */
    protected void startAutomaticRefresh(int refreshInterval) {
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                try {
                    minuteTick();
                } catch (Exception e) {
                    logger.debug("Exception occurred during execution: {}", e.getMessage(), e);
                }
            }
        };

        refreshJob = scheduler.scheduleAtFixedRate(runnable, 0, refreshInterval, TimeUnit.SECONDS);
        refreshState();
    }

    protected void stopAutomaticRefresh() {
        if (refreshJob != null) {
            refreshJob.cancel(true);
            refreshJob = null;
        }
    }

    /**
     * Dispose off the refreshJob nicely.
     */
    @Override
    public void dispose() {
        stopAutomaticRefresh();
    }

}
