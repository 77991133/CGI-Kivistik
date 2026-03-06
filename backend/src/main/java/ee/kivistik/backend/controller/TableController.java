package ee.kivistik.backend.controller;

import ee.kivistik.backend.entity.TableEntity;
import ee.kivistik.backend.repository.TableRepository;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
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
