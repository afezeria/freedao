package test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * @author afezeria
 */
@RestController
public class PersonController {
    @Autowired
    private PersonDao dao;


    @GetMapping
    public List<Person> all() {
        return dao.queryByIdNotNull();
    }
}
