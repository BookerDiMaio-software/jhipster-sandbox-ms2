package com.mycompany.myapp.web.rest;

import com.mycompany.myapp.domain.Hello;
import com.mycompany.myapp.repository.HelloRepository;
import com.mycompany.myapp.web.rest.errors.BadRequestAlertException;

import io.github.jhipster.web.util.HeaderUtil;
import io.github.jhipster.web.util.ResponseUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.net.URI;
import java.net.URISyntaxException;

import java.util.List;
import java.util.Optional;

/**
 * REST controller for managing {@link com.mycompany.myapp.domain.Hello}.
 */
@RestController
@RequestMapping("/api")
public class HelloResource {

    private final Logger log = LoggerFactory.getLogger(HelloResource.class);

    private static final String ENTITY_NAME = "microservice2Hello";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final HelloRepository helloRepository;

    public HelloResource(HelloRepository helloRepository) {
        this.helloRepository = helloRepository;
    }

    /**
     * {@code POST  /hellos} : Create a new hello.
     *
     * @param hello the hello to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new hello, or with status {@code 400 (Bad Request)} if the hello has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("/hellos")
    public ResponseEntity<Hello> createHello(@Valid @RequestBody Hello hello) throws URISyntaxException {
        log.debug("REST request to save Hello : {}", hello);
        if (hello.getId() != null) {
            throw new BadRequestAlertException("A new hello cannot already have an ID", ENTITY_NAME, "idexists");
        }
        Hello result = helloRepository.save(hello);
        return ResponseEntity.created(new URI("/api/hellos/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, result.getId().toString()))
            .body(result);
    }

    /**
     * {@code PUT  /hellos} : Updates an existing hello.
     *
     * @param hello the hello to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated hello,
     * or with status {@code 400 (Bad Request)} if the hello is not valid,
     * or with status {@code 500 (Internal Server Error)} if the hello couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/hellos")
    public ResponseEntity<Hello> updateHello(@Valid @RequestBody Hello hello) throws URISyntaxException {
        log.debug("REST request to update Hello : {}", hello);
        if (hello.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        Hello result = helloRepository.save(hello);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, hello.getId().toString()))
            .body(result);
    }

    /**
     * {@code GET  /hellos} : get all the hellos.
     *
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of hellos in body.
     */
    @GetMapping("/hellos")
    public List<Hello> getAllHellos() {
        log.debug("REST request to get all Hellos");
        return helloRepository.findAll();
    }

    /**
     * {@code GET  /hellos/:id} : get the "id" hello.
     *
     * @param id the id of the hello to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the hello, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/hellos/{id}")
    public ResponseEntity<Hello> getHello(@PathVariable Long id) {
        log.debug("REST request to get Hello : {}", id);
        Optional<Hello> hello = helloRepository.findById(id);
        return ResponseUtil.wrapOrNotFound(hello);
    }

    /**
     * {@code DELETE  /hellos/:id} : delete the "id" hello.
     *
     * @param id the id of the hello to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/hellos/{id}")
    public ResponseEntity<Void> deleteHello(@PathVariable Long id) {
        log.debug("REST request to delete Hello : {}", id);
        helloRepository.deleteById(id);
        return ResponseEntity.noContent().headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString())).build();
    }
}
