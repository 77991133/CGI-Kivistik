package ee.kivistik.backend.controller;

import ee.kivistik.backend.dto.TableDTO;
import ee.kivistik.backend.entity.TableEntity;
import ee.kivistik.backend.mapper.TableMapperUtil;
import ee.kivistik.backend.repository.TableRepository;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * REST controller responsible for handling table related API endpoints.
 * Provides functionality for retrieving all tables and creating new tables
 * in the system. The controller interacts with the TableRepository to
 * perform database operations.
 */
@RestController
@CrossOrigin(origins = "http://localhost:5173")
public class TableController {
    private final TableRepository tableRepository;
    /**
     * Constructs a TableController with the required TableRepository dependency.
     *
     * @param tableRepository repository used to access and manage table data
     */
    public TableController(TableRepository tableRepository) {
        this.tableRepository = tableRepository;
    }

    /**
     * Returns all tables stored in the system.
     *
     * @return iterable collection of TableDTO objects
     */
    @GetMapping("/tables")
    public List<TableDTO> findAllTables() {
        return tableRepository.findAll()
                .stream()
                .map(TableMapperUtil::mapToDTO)
                .toList();
    }



    /**
     * Creates and stores a new table in the database.
     *
     * @param table table entity received from the client
     * @return the saved TableEntity object
     */
    @PostMapping("/tables")
    public TableEntity addOneTable(@RequestBody TableEntity table) {
        return this.tableRepository.save(table);
    }
}
