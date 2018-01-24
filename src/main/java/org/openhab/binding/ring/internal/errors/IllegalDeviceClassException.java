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
 * IllegalDeviceClassException will be thrown if an device is retrieved
 * from the RingDeviceRegistry and the class is not as expected.
 * E.g. if a Doorbell is expected, but a Chime is returned.
 *
 * @author Wim Vissers - Initial contribution
 */
public class IllegalDeviceClassException extends Exception {

    private static final long serialVersionUID = -4010587859949508962L;

    public IllegalDeviceClassException(String message) {
        super(message);
    }

}
