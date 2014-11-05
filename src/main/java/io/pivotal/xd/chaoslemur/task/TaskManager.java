/*
 * Copyright 2014 Pivotal Software, Inc. All Rights Reserved.
 */

package io.pivotal.xd.chaoslemur.task;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.Resource;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/task")
final class TaskManager implements TaskRepository {

    private final AtomicLong counter = new AtomicLong();

    private final Map<Long, Task> tasks = new ConcurrentHashMap<>();

    private final TaskResourceAssembler resourceAssembler;

    @Autowired
    TaskManager(TaskResourceAssembler resourceAssembler) {
        this.resourceAssembler = resourceAssembler;
    }

    @Override
    public Task create(Trigger trigger) {
        Task task = new Task(this.counter.getAndIncrement(), trigger);
        this.tasks.put(task.getId(), task);
        return task;
    }

    @RequestMapping(method = RequestMethod.GET, value = "", produces = MediaType.APPLICATION_JSON_VALUE)
    Set<Resource<Task>> readAll() {
        return this.tasks.values().stream().map(this.resourceAssembler::toResource).collect(Collectors.toSet());
    }

    @RequestMapping(method = RequestMethod.GET, value = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    ResponseEntity<?> read(@PathVariable Long id) {
        Task task = this.tasks.get(id);
        if (task == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        return new ResponseEntity<>(this.resourceAssembler.toResource(task), HttpStatus.OK);
    }

}