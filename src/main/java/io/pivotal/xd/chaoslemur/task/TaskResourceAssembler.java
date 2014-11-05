/*
 * Copyright 2014 Pivotal Software, Inc. All Rights Reserved.
 */

package io.pivotal.xd.chaoslemur.task;

import org.springframework.hateoas.Resource;
import org.springframework.hateoas.ResourceAssembler;
import org.springframework.hateoas.mvc.ControllerLinkBuilder;
import org.springframework.stereotype.Component;

import java.net.URI;

import static org.springframework.hateoas.mvc.ControllerLinkBuilder.linkTo;

@Component
final class TaskResourceAssembler implements ResourceAssembler<Task, Resource<Task>>, TaskUriBuilder {

    @Override
    public Resource<Task> toResource(Task task) {
        Resource resource = new Resource<>(task);

        resource.add(getTaskLinkBuilder(task).withSelfRel());

        return resource;
    }

    @Override
    public URI getUri(Task task) {
        return getTaskLinkBuilder(task).toUri();
    }

    private ControllerLinkBuilder getTaskLinkBuilder(Task task) {
        return linkTo(TaskManager.class).slash(task.getId());
    }

}
