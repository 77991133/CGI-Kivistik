package ee.kivistik.backend.controller;

import ee.kivistik.backend.entity.TableEntity;
import ee.kivistik.backend.repository.TableRepository;
import org.springframework.web.bind.annotation.*;

@RestController
@CrossOrigin(origins = "http://localhost:5173")
public class TableController {
    private final TableRepository tableRepository;

    public TableController(TableRepository tableRepository) {
        this.tableRepository = tableRepository;
    }

    @GetMapping("/tables")
    public Iterable<TableEntity> findAllTables() {
        return this.tableRepository.findAll();
    }

    @PostMapping("/tables")
    public TableEntity addOneTable(@RequestBody TableEntity table) {
        return this.tableRepository.save(table);
    }
}
