/**
 * Copyright (c) 2010-2017 by the respective copyright holders.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.openhab.binding.ring.internal.discovery;

import static org.openhab.binding.ring.RingBindingConstants.*;

import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

import org.eclipse.smarthome.config.discovery.AbstractDiscoveryService;
import org.eclipse.smarthome.core.thing.ThingTypeUID;
import org.openhab.binding.ring.internal.RingDeviceRegistry;
import org.openhab.binding.ring.internal.data.RingDevice;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The BeeClearDiscoveryService is responsible for auto detecting a BeeClear
 * device in the local network.
 *
 * @author Wim Vissers - Initial contribution
 */
public class RingDiscoveryService extends AbstractDiscoveryService {

    private Logger logger = LoggerFactory.getLogger(RingDiscoveryService.class);
    private ScheduledFuture<?> discoveryJob;

    private static Set<ThingTypeUID> SUPPORTED_THING_TYPES_UIDS;
    private static final int INTERVAL = 120;

    public static Set<ThingTypeUID> getSupportedTypes() {
        if (SUPPORTED_THING_TYPES_UIDS == null) {
            SUPPORTED_THING_TYPES_UIDS = new HashSet<>();
            SUPPORTED_THING_TYPES_UIDS.add(THING_TYPE_ACCOUNT);
            SUPPORTED_THING_TYPES_UIDS.add(THING_TYPE_CHIME);
            SUPPORTED_THING_TYPES_UIDS.add(THING_TYPE_DOORBELL);
        }
        return SUPPORTED_THING_TYPES_UIDS;
    }

    public RingDiscoveryService() {
        super(getSupportedTypes(), 5, true);
    }

    public void activate() {
        logger.debug("Starting Ring discovery...");
        // removeOlderResults(System.currentTimeMillis(), getSupportedTypes());
        startScan();
        startBackgroundDiscovery();
    }

    @Override
    public void deactivate() {
        logger.debug("Stopping Ring discovery...");
        stopBackgroundDiscovery();
        stopScan();
    }

    private void discover() {
        RingDeviceRegistry registry = RingDeviceRegistry.getInstance();
        for (RingDevice device : registry.getRingDevices(RingDeviceRegistry.Status.ADDED)) {
            thingDiscovered(device.getDiscoveryResult());
            registry.setStatus(device.getId(), RingDeviceRegistry.Status.DISCOVERED);
        }
        /**
         * if (holdOff == 0) {
         * if (!BeeClearRegistry.getInstance().isRegistered(BEECLEAR_HOSTNAME, BEECLEAR_PORT)) {
         * try {
         * SoftwareVersion softwareVersion = restClient.getSoftwareVersion();
         * if (softwareVersion.getInfo() != null && !softwareVersion.getInfo().isEmpty()) {
         * DiscoveryResult discoveryResult = DiscoveryResultBuilder
         * .create(new ThingUID("beeclear:meter:unit1")).withProperties(getConfigProperties())
         * .withLabel("BeeClear Device").build();
         * thingDiscovered(discoveryResult);
         * BeeClearRegistry.getInstance().registerByName(BEECLEAR_HOSTNAME, BEECLEAR_PORT);
         * }
         * } catch (IOException e) {
         * logger.debug("Could not connect to BeeClear device.", e);
         * }
         * }
         * }
         * if (holdOff > 0) {
         * holdOff--;
         * }
         */
    }

    @Override
    protected void startBackgroundDiscovery() {
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                try {
                    discover();
                } catch (Exception e) {
                    logger.debug("Exception occurred during execution: {}", e.getMessage(), e);
                }
            }
        };
        /*
         * logger.info("Start BeeClear device background discovery");
         * if (discoveryJob == null || discoveryJob.isCancelled()) {
         * restClient = new RestClient(BEECLEAR_HOSTNAME, BEECLEAR_PORT);
         * discoveryJob = scheduler.scheduleAtFixedRate(runnable, 0, INTERVAL, TimeUnit.SECONDS);
         * }
         */
        discoveryJob = scheduler.scheduleAtFixedRate(runnable, 0, INTERVAL, TimeUnit.SECONDS);
    }

    @Override
    protected void stopBackgroundDiscovery() {
        logger.info("Stop Ring background discovery");
        if (discoveryJob != null && !discoveryJob.isCancelled()) {
            discoveryJob.cancel(true);
            discoveryJob = null;
        }
    }

    @Override
    protected void startScan() {
        logger.debug("Starting device search...");
    }

    @Override
    protected synchronized void stopScan() {
        removeOlderResults(getTimestampOfLastScan());
        super.stopScan();
        if (!isBackgroundDiscoveryEnabled()) {
        }
    }

}
