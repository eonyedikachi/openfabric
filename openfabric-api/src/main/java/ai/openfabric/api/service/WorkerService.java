package ai.openfabric.api.service;


import ai.openfabric.api.exceptions.DataNotFoundException;
import ai.openfabric.api.model.Worker;
import ai.openfabric.api.model.WorkerStatistics;
import ai.openfabric.api.repository.WorkerRepository;
import ai.openfabric.api.repository.WorkerStatisticsRepository;

import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import org.springframework.stereotype.Service;


@Service
@Slf4j
public class WorkerService {

    private final WorkerRepository workerRepository;
    private final WorkerStatisticsRepository workerStatisticsRepo;

    public WorkerService(WorkerRepository workerRepository, WorkerStatisticsRepository workerStatisticsRepo) {
        this.workerRepository = workerRepository;
        this.workerStatisticsRepo = workerStatisticsRepo;
    }

    public Page<Worker> getPaginatedWorkers(int page, int pageSize) {
        Pageable pageable = PageRequest.of(page, pageSize, Sort.by("createdAt"));
        return workerRepository.getAll(pageable);
    }

    public Worker getWorker(String workerId) {
        return workerRepository.findById(workerId)
                .orElseThrow(() ->{
                    String msg = String.format("No worker found with the id=%s", workerId);
                        log.error(msg);
                        throw new DataNotFoundException(msg);
                });
    }

    public WorkerStatistics getWorkerStatistics(String workerId) {
        Worker worker = workerRepository.findById(workerId)
                .orElseThrow(() ->{
                    String msg = String.format("No worker found with id=%s", workerId);
                    log.error(msg);
                    throw new DataNotFoundException(msg);
                });
        return worker.getStatistics();
    }

}
