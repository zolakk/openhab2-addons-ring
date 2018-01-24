/**
 * Copyright (c) 2010-2018 by the respective copyright holders.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.openhab.binding.ring.internal.errors;

/**
 * DeviceNotFoundException will be thrown if an device is requested from
 * the device registry with an id that is not registered.
 *
 * @author Wim Vissers - Initial contribution
 */
public class DeviceNotFoundException extends Exception {

    private static final long serialVersionUID = -463646377949508962L;

    public DeviceNotFoundException(String message) {
        super(message);
    }

}
