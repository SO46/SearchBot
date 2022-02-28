package ru.skillbox.diplom.service;

import lombok.RequiredArgsConstructor;
import org.springframework.transaction.annotation.Transactional;
import ru.skillbox.diplom.dto.ErrorResponse;
import ru.skillbox.diplom.dto.SimpleResponse;
import ru.skillbox.diplom.dto.StatisticResponse;
import ru.skillbox.diplom.model.Page;
import ru.skillbox.diplom.repository.FieldRepository;
import ru.skillbox.diplom.repository.IndexRepository;
import ru.skillbox.diplom.repository.LemmaRepository;
import ru.skillbox.diplom.repository.PageRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@Service
@Transactional
@RequiredArgsConstructor
public class SearchService {

    private final PageRepository pageRepository;
    private final FieldRepository fieldRepository;
    private final LemmaRepository lemmaRepository;
    private final IndexRepository indexRepository;

    private final String INDEXING_UP = "Индексация уже запущена";
    private final String INDEXING_DOWN = "Индексация не запущена";
    private final String PAGE_OUT_OF_CONFIG = "Данная страница находится за пределами сайтов, указанных в конфигурационном файле";

    private boolean indexing;

    public ResponseEntity<?> startIndexing() {
        if (indexing) {
            ErrorResponse errorResponse = new ErrorResponse();
            errorResponse.setResult(false);
            errorResponse.setError(INDEXING_UP);
            return ResponseEntity.status(HttpStatus.CONFLICT).body(errorResponse);
        }
        indexing = true;
        // todo start indexing each site in his own thread
        SimpleResponse response = new SimpleResponse();
        response.setResult(true);
        return ResponseEntity.ok(response);
    }

    public ResponseEntity<?> stopIndexing() {
        if (!indexing) {
            ErrorResponse errorResponse = new ErrorResponse();
            errorResponse.setResult(false);
            errorResponse.setError(INDEXING_DOWN);
            return ResponseEntity.status(HttpStatus.CONFLICT).body(errorResponse);
        }
        indexing = false;
        // todo stop indexing
        SimpleResponse response = new SimpleResponse();
        response.setResult(true);
        return ResponseEntity.ok(response);
    }

    public ResponseEntity<?> indexPage(String url) {
        Page page = pageRepository.findByPath(url);
        if (page == null) {
            ErrorResponse errorResponse = new ErrorResponse();
            errorResponse.setResult(false);
            errorResponse.setError(PAGE_OUT_OF_CONFIG);
            return ResponseEntity.status(HttpStatus.CONFLICT).body(errorResponse);
        }
        // todo start indexing page
        SimpleResponse response = new SimpleResponse();
        response.setResult(true);
        return ResponseEntity.ok(response);
    }

    public ResponseEntity<?> statistics() {
        StatisticResponse response = new StatisticResponse();

        response.setResult(true);
        return ResponseEntity.ok(response);
    }
}
