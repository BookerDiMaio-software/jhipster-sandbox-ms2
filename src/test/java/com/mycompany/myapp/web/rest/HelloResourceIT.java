package com.mycompany.myapp.web.rest;

import com.mycompany.myapp.Microservice2App;
import com.mycompany.myapp.domain.Hello;
import com.mycompany.myapp.repository.HelloRepository;
import com.mycompany.myapp.web.rest.errors.ExceptionTranslator;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.web.PageableHandlerMethodArgumentResolver;
import org.springframework.http.MediaType;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.Validator;

import javax.persistence.EntityManager;
import java.util.List;

import static com.mycompany.myapp.web.rest.TestUtil.createFormattingConversionService;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Integration tests for the {@Link HelloResource} REST controller.
 */
@SpringBootTest(classes = Microservice2App.class)
public class HelloResourceIT {

    private static final String DEFAULT_FIRST_NAME = "AAAAAAAAAA";
    private static final String UPDATED_FIRST_NAME = "BBBBBBBBBB";

    private static final String DEFAULT_LAST_NAME = "AAAAAAAAAA";
    private static final String UPDATED_LAST_NAME = "BBBBBBBBBB";

    private static final String DEFAULT_SALUTATION = "AAAAAAAAAA";
    private static final String UPDATED_SALUTATION = "BBBBBBBBBB";

    @Autowired
    private HelloRepository helloRepository;

    @Autowired
    private MappingJackson2HttpMessageConverter jacksonMessageConverter;

    @Autowired
    private PageableHandlerMethodArgumentResolver pageableArgumentResolver;

    @Autowired
    private ExceptionTranslator exceptionTranslator;

    @Autowired
    private EntityManager em;

    @Autowired
    private Validator validator;

    private MockMvc restHelloMockMvc;

    private Hello hello;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.initMocks(this);
        final HelloResource helloResource = new HelloResource(helloRepository);
        this.restHelloMockMvc = MockMvcBuilders.standaloneSetup(helloResource)
            .setCustomArgumentResolvers(pageableArgumentResolver)
            .setControllerAdvice(exceptionTranslator)
            .setConversionService(createFormattingConversionService())
            .setMessageConverters(jacksonMessageConverter)
            .setValidator(validator).build();
    }

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Hello createEntity(EntityManager em) {
        Hello hello = new Hello()
            .firstName(DEFAULT_FIRST_NAME)
            .lastName(DEFAULT_LAST_NAME)
            .salutation(DEFAULT_SALUTATION);
        return hello;
    }
    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Hello createUpdatedEntity(EntityManager em) {
        Hello hello = new Hello()
            .firstName(UPDATED_FIRST_NAME)
            .lastName(UPDATED_LAST_NAME)
            .salutation(UPDATED_SALUTATION);
        return hello;
    }

    @BeforeEach
    public void initTest() {
        hello = createEntity(em);
    }

    @Test
    @Transactional
    public void createHello() throws Exception {
        int databaseSizeBeforeCreate = helloRepository.findAll().size();

        // Create the Hello
        restHelloMockMvc.perform(post("/api/hellos")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(hello)))
            .andExpect(status().isCreated());

        // Validate the Hello in the database
        List<Hello> helloList = helloRepository.findAll();
        assertThat(helloList).hasSize(databaseSizeBeforeCreate + 1);
        Hello testHello = helloList.get(helloList.size() - 1);
        assertThat(testHello.getFirstName()).isEqualTo(DEFAULT_FIRST_NAME);
        assertThat(testHello.getLastName()).isEqualTo(DEFAULT_LAST_NAME);
        assertThat(testHello.getSalutation()).isEqualTo(DEFAULT_SALUTATION);
    }

    @Test
    @Transactional
    public void createHelloWithExistingId() throws Exception {
        int databaseSizeBeforeCreate = helloRepository.findAll().size();

        // Create the Hello with an existing ID
        hello.setId(1L);

        // An entity with an existing ID cannot be created, so this API call must fail
        restHelloMockMvc.perform(post("/api/hellos")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(hello)))
            .andExpect(status().isBadRequest());

        // Validate the Hello in the database
        List<Hello> helloList = helloRepository.findAll();
        assertThat(helloList).hasSize(databaseSizeBeforeCreate);
    }


    @Test
    @Transactional
    public void checkFirstNameIsRequired() throws Exception {
        int databaseSizeBeforeTest = helloRepository.findAll().size();
        // set the field null
        hello.setFirstName(null);

        // Create the Hello, which fails.

        restHelloMockMvc.perform(post("/api/hellos")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(hello)))
            .andExpect(status().isBadRequest());

        List<Hello> helloList = helloRepository.findAll();
        assertThat(helloList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    public void checkLastNameIsRequired() throws Exception {
        int databaseSizeBeforeTest = helloRepository.findAll().size();
        // set the field null
        hello.setLastName(null);

        // Create the Hello, which fails.

        restHelloMockMvc.perform(post("/api/hellos")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(hello)))
            .andExpect(status().isBadRequest());

        List<Hello> helloList = helloRepository.findAll();
        assertThat(helloList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    public void checkSalutationIsRequired() throws Exception {
        int databaseSizeBeforeTest = helloRepository.findAll().size();
        // set the field null
        hello.setSalutation(null);

        // Create the Hello, which fails.

        restHelloMockMvc.perform(post("/api/hellos")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(hello)))
            .andExpect(status().isBadRequest());

        List<Hello> helloList = helloRepository.findAll();
        assertThat(helloList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    public void getAllHellos() throws Exception {
        // Initialize the database
        helloRepository.saveAndFlush(hello);

        // Get all the helloList
        restHelloMockMvc.perform(get("/api/hellos?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(hello.getId().intValue())))
            .andExpect(jsonPath("$.[*].firstName").value(hasItem(DEFAULT_FIRST_NAME.toString())))
            .andExpect(jsonPath("$.[*].lastName").value(hasItem(DEFAULT_LAST_NAME.toString())))
            .andExpect(jsonPath("$.[*].salutation").value(hasItem(DEFAULT_SALUTATION.toString())));
    }
    
    @Test
    @Transactional
    public void getHello() throws Exception {
        // Initialize the database
        helloRepository.saveAndFlush(hello);

        // Get the hello
        restHelloMockMvc.perform(get("/api/hellos/{id}", hello.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.id").value(hello.getId().intValue()))
            .andExpect(jsonPath("$.firstName").value(DEFAULT_FIRST_NAME.toString()))
            .andExpect(jsonPath("$.lastName").value(DEFAULT_LAST_NAME.toString()))
            .andExpect(jsonPath("$.salutation").value(DEFAULT_SALUTATION.toString()));
    }

    @Test
    @Transactional
    public void getNonExistingHello() throws Exception {
        // Get the hello
        restHelloMockMvc.perform(get("/api/hellos/{id}", Long.MAX_VALUE))
            .andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    public void updateHello() throws Exception {
        // Initialize the database
        helloRepository.saveAndFlush(hello);

        int databaseSizeBeforeUpdate = helloRepository.findAll().size();

        // Update the hello
        Hello updatedHello = helloRepository.findById(hello.getId()).get();
        // Disconnect from session so that the updates on updatedHello are not directly saved in db
        em.detach(updatedHello);
        updatedHello
            .firstName(UPDATED_FIRST_NAME)
            .lastName(UPDATED_LAST_NAME)
            .salutation(UPDATED_SALUTATION);

        restHelloMockMvc.perform(put("/api/hellos")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(updatedHello)))
            .andExpect(status().isOk());

        // Validate the Hello in the database
        List<Hello> helloList = helloRepository.findAll();
        assertThat(helloList).hasSize(databaseSizeBeforeUpdate);
        Hello testHello = helloList.get(helloList.size() - 1);
        assertThat(testHello.getFirstName()).isEqualTo(UPDATED_FIRST_NAME);
        assertThat(testHello.getLastName()).isEqualTo(UPDATED_LAST_NAME);
        assertThat(testHello.getSalutation()).isEqualTo(UPDATED_SALUTATION);
    }

    @Test
    @Transactional
    public void updateNonExistingHello() throws Exception {
        int databaseSizeBeforeUpdate = helloRepository.findAll().size();

        // Create the Hello

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restHelloMockMvc.perform(put("/api/hellos")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(hello)))
            .andExpect(status().isBadRequest());

        // Validate the Hello in the database
        List<Hello> helloList = helloRepository.findAll();
        assertThat(helloList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    public void deleteHello() throws Exception {
        // Initialize the database
        helloRepository.saveAndFlush(hello);

        int databaseSizeBeforeDelete = helloRepository.findAll().size();

        // Delete the hello
        restHelloMockMvc.perform(delete("/api/hellos/{id}", hello.getId())
            .accept(TestUtil.APPLICATION_JSON_UTF8))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        List<Hello> helloList = helloRepository.findAll();
        assertThat(helloList).hasSize(databaseSizeBeforeDelete - 1);
    }

    @Test
    @Transactional
    public void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(Hello.class);
        Hello hello1 = new Hello();
        hello1.setId(1L);
        Hello hello2 = new Hello();
        hello2.setId(hello1.getId());
        assertThat(hello1).isEqualTo(hello2);
        hello2.setId(2L);
        assertThat(hello1).isNotEqualTo(hello2);
        hello1.setId(null);
        assertThat(hello1).isNotEqualTo(hello2);
    }
}
