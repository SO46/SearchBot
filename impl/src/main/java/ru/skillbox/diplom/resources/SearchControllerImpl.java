package ru.skillbox.diplom.resources;

import lombok.RequiredArgsConstructor;
import ru.skillbox.diplom.controller.SearchController;
import ru.skillbox.diplom.service.SearchService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
public class SearchControllerImpl implements SearchController {

    private final SearchService service;

    @Override
    public ResponseEntity<?> startIndexing(){
        return service.startIndexing();
    }

    @Override
    public ResponseEntity<?> stopIndexing(){
        return service.stopIndexing();
    }

    @Override
    public ResponseEntity<?> indexPage(@PathVariable String url){
        return service.indexPage(url);
    }

    @Override
    public ResponseEntity<?> statistics(){
        return service.statistics();
    }

}
