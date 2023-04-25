package ai.openfabric.api.repository;

import ai.openfabric.api.model.Worker;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface WorkerRepository extends CrudRepository<Worker, String> {

    @Query(value = "select w from Worker w")
    Page<Worker> getAll(Pageable pageable);

    @Query(value = "select w from Worker w where w.containerId not in( :containerIds)")
    List<Worker> findAllByContainerIdNotIn(List<String> containerIds);

    @Query(value = "select w from Worker w where w.containerId = :containerId")
    Worker findByContainerId(String containerId);

}
