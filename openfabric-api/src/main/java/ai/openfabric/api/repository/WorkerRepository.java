package ai.openfabric.api.repository;

import ai.openfabric.api.model.Worker;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

public interface WorkerRepository extends CrudRepository<Worker, String> {

    @Query(value = "select w from Worker w")
    Page<Worker> getAll(Pageable pageable);

}
