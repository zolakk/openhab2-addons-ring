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
 * AuthenticationException will be thrown if an invalid username or
 * password is used to get access to the Ring account.
 *
 * @author Wim Vissers - Initial contribution
 */
public class AuthenticationException extends Exception {

    private static final long serialVersionUID = -2630294607218363771L;

    public AuthenticationException(String message) {
        super(message);
    }

}
