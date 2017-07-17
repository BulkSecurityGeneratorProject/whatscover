package com.whatscover.web.rest;

import com.codahale.metrics.annotation.Timed;
import com.whatscover.domain.InsuranceAgency;

import com.whatscover.repository.InsuranceAgencyRepository;
import com.whatscover.repository.search.InsuranceAgencySearchRepository;
import com.whatscover.web.rest.util.HeaderUtil;
import com.whatscover.web.rest.util.PaginationUtil;
import com.whatscover.service.dto.InsuranceAgencyDTO;
import com.whatscover.service.mapper.InsuranceAgencyMapper;
import io.swagger.annotations.ApiParam;
import io.github.jhipster.web.util.ResponseUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.net.URI;
import java.net.URISyntaxException;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import static org.elasticsearch.index.query.QueryBuilders.*;

/**
 * REST controller for managing InsuranceAgency.
 */
@RestController
@RequestMapping("/api")
public class InsuranceAgencyResource {

    private final Logger log = LoggerFactory.getLogger(InsuranceAgencyResource.class);

    private static final String ENTITY_NAME = "insuranceAgency";

    private final InsuranceAgencyRepository insuranceAgencyRepository;

    private final InsuranceAgencyMapper insuranceAgencyMapper;

    private final InsuranceAgencySearchRepository insuranceAgencySearchRepository;

    public InsuranceAgencyResource(InsuranceAgencyRepository insuranceAgencyRepository, InsuranceAgencyMapper insuranceAgencyMapper, InsuranceAgencySearchRepository insuranceAgencySearchRepository) {
        this.insuranceAgencyRepository = insuranceAgencyRepository;
        this.insuranceAgencyMapper = insuranceAgencyMapper;
        this.insuranceAgencySearchRepository = insuranceAgencySearchRepository;
    }

    /**
     * POST  /insurance-agencies : Create a new insuranceAgency.
     *
     * @param insuranceAgencyDTO the insuranceAgencyDTO to create
     * @return the ResponseEntity with status 201 (Created) and with body the new insuranceAgencyDTO, or with status 400 (Bad Request) if the insuranceAgency has already an ID
     * @throws URISyntaxException if the Location URI syntax is incorrect
     */
    @PostMapping("/insurance-agencies")
    @Timed
    public ResponseEntity<InsuranceAgencyDTO> createInsuranceAgency(@Valid @RequestBody InsuranceAgencyDTO insuranceAgencyDTO) throws URISyntaxException {
        log.debug("REST request to save InsuranceAgency : {}", insuranceAgencyDTO);
        if (insuranceAgencyDTO.getId() != null) {
            return ResponseEntity.badRequest().headers(HeaderUtil.createFailureAlert(ENTITY_NAME, "idexists", "A new insuranceAgency cannot already have an ID")).body(null);
        }
        InsuranceAgency insuranceAgency = insuranceAgencyMapper.toEntity(insuranceAgencyDTO);
        insuranceAgency = insuranceAgencyRepository.save(insuranceAgency);
        InsuranceAgencyDTO result = insuranceAgencyMapper.toDto(insuranceAgency);
        insuranceAgencySearchRepository.save(insuranceAgency);
        return ResponseEntity.created(new URI("/api/insurance-agencies/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(ENTITY_NAME, result.getId().toString()))
            .body(result);
    }

    /**
     * PUT  /insurance-agencies : Updates an existing insuranceAgency.
     *
     * @param insuranceAgencyDTO the insuranceAgencyDTO to update
     * @return the ResponseEntity with status 200 (OK) and with body the updated insuranceAgencyDTO,
     * or with status 400 (Bad Request) if the insuranceAgencyDTO is not valid,
     * or with status 500 (Internal Server Error) if the insuranceAgencyDTO couldn't be updated
     * @throws URISyntaxException if the Location URI syntax is incorrect
     */
    @PutMapping("/insurance-agencies")
    @Timed
    public ResponseEntity<InsuranceAgencyDTO> updateInsuranceAgency(@Valid @RequestBody InsuranceAgencyDTO insuranceAgencyDTO) throws URISyntaxException {
        log.debug("REST request to update InsuranceAgency : {}", insuranceAgencyDTO);
        if (insuranceAgencyDTO.getId() == null) {
            return createInsuranceAgency(insuranceAgencyDTO);
        }
        InsuranceAgency insuranceAgency = insuranceAgencyMapper.toEntity(insuranceAgencyDTO);
        insuranceAgency = insuranceAgencyRepository.save(insuranceAgency);
        InsuranceAgencyDTO result = insuranceAgencyMapper.toDto(insuranceAgency);
        insuranceAgencySearchRepository.save(insuranceAgency);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(ENTITY_NAME, insuranceAgencyDTO.getId().toString()))
            .body(result);
    }

    /**
     * GET  /insurance-agencies : get all the insuranceAgencies.
     *
     * @param pageable the pagination information
     * @return the ResponseEntity with status 200 (OK) and the list of insuranceAgencies in body
     */
    @GetMapping("/insurance-agencies")
    @Timed
    public ResponseEntity<List<InsuranceAgencyDTO>> getAllInsuranceAgencies(@ApiParam Pageable pageable) {
        log.debug("REST request to get a page of InsuranceAgencies");
        Page<InsuranceAgency> page = insuranceAgencyRepository.findAll(pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(page, "/api/insurance-agencies");
        return new ResponseEntity<>(insuranceAgencyMapper.toDto(page.getContent()), headers, HttpStatus.OK);
    }

    /**
     * GET  /insurance-agencies/:id : get the "id" insuranceAgency.
     *
     * @param id the id of the insuranceAgencyDTO to retrieve
     * @return the ResponseEntity with status 200 (OK) and with body the insuranceAgencyDTO, or with status 404 (Not Found)
     */
    @GetMapping("/insurance-agencies/{id}")
    @Timed
    public ResponseEntity<InsuranceAgencyDTO> getInsuranceAgency(@PathVariable Long id) {
        log.debug("REST request to get InsuranceAgency : {}", id);
        InsuranceAgency insuranceAgency = insuranceAgencyRepository.findOne(id);
        InsuranceAgencyDTO insuranceAgencyDTO = insuranceAgencyMapper.toDto(insuranceAgency);
        return ResponseUtil.wrapOrNotFound(Optional.ofNullable(insuranceAgencyDTO));
    }

    /**
     * DELETE  /insurance-agencies/:id : delete the "id" insuranceAgency.
     *
     * @param id the id of the insuranceAgencyDTO to delete
     * @return the ResponseEntity with status 200 (OK)
     */
    @DeleteMapping("/insurance-agencies/{id}")
    @Timed
    public ResponseEntity<Void> deleteInsuranceAgency(@PathVariable Long id) {
        log.debug("REST request to delete InsuranceAgency : {}", id);
        insuranceAgencyRepository.delete(id);
        insuranceAgencySearchRepository.delete(id);
        return ResponseEntity.ok().headers(HeaderUtil.createEntityDeletionAlert(ENTITY_NAME, id.toString())).build();
    }

    /**
     * SEARCH  /_search/insurance-agencies?query=:query : search for the insuranceAgency corresponding
     * to the query.
     *
     * @param query the query of the insuranceAgency search
     * @param pageable the pagination information
     * @return the result of the search
     */
    @GetMapping("/_search/insurance-agencies")
    @Timed
    public ResponseEntity<List<InsuranceAgencyDTO>> searchInsuranceAgencies(@RequestParam String query, @ApiParam Pageable pageable) {
        log.debug("REST request to search for a page of InsuranceAgencies for query {}", query);
        Page<InsuranceAgency> page = insuranceAgencySearchRepository.search(queryStringQuery(query), pageable);
        HttpHeaders headers = PaginationUtil.generateSearchPaginationHttpHeaders(query, page, "/api/_search/insurance-agencies");
        return new ResponseEntity<>(insuranceAgencyMapper.toDto(page.getContent()), headers, HttpStatus.OK);
    }

}