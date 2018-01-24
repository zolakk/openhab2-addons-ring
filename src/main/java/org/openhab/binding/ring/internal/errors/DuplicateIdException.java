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
 * DuplicateIdException will be thrown if an device is added to
 * the device registry with an id that is already registered.
 *
 * @author Wim Vissers - Initial contribution
 */
public class DuplicateIdException extends Exception {

    private static final long serialVersionUID = -4010587859949508962L;

    public DuplicateIdException(String message) {
        super(message);
    }

}
