/*
 * Copyright 2014 Pivotal Software, Inc. All Rights Reserved.
 */

package io.pivotal.xd.chaoslemur.task;

import java.net.URI;

public interface TaskUriBuilder {

    /**
     * Creates a {@code URI} for the {@code Task}
     *
     * @param task The {@code Task} for which a {@code URI} is required.
     * @return The {@code URI} for the {@code Task}
     */
    URI getUri(Task task);
}
