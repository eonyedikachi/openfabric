package ai.openfabric.api.repository;

import ai.openfabric.api.model.WorkerStatistics;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface WorkerStatisticsRepository extends CrudRepository<WorkerStatistics, String> {

    @Query(value = "select ws from WorkerStatistics ws where ws.worker.containerId not in :containerIds")
    List<WorkerStatistics> findAllByContainerIdNotIn(List<String> containerIds);

    @Query(value = "select ws from WorkerStatistics ws where ws.worker.containerId = :containerId")
    WorkerStatistics findByWorkerContainerId(String containerId);
}

