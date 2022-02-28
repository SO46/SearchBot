package ru.skillbox.diplom.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@RequestMapping("/api/v1/")
public interface SearchController {

    @GetMapping("/startIndexing")
    ResponseEntity<?> startIndexing();

    @GetMapping("/stopIndexing")
    ResponseEntity<?> stopIndexing();

    @PostMapping("/indexPage")
    ResponseEntity<?> indexPage(@PathVariable String url);

    @GetMapping("/statistics")
    ResponseEntity<?> statistics();
}
