/**
 * Copyright (c) 2010-2017 by the respective copyright holders.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.openhab.binding.ring.internal;

import org.openhab.binding.ring.internal.data.Profile;

/**
 * The AccountHandler implements this interface to facilitate the
 * use of the common services.
 *
 * @author Wim Vissers - Initial contribution
 */
public interface RingAccount {

    /**
     * Get the linked REST client.
     *
     * @return the REST client.
     */
    public RestClient getRestClient();

    /**
     * Get the linked user profile.
     *
     * @return the user profile.
     */
    public Profile getProfile();

}
