package ai.openfabric.api.service;


import ai.openfabric.api.constant.WorkerOperation;
import ai.openfabric.api.exceptions.DataNotFoundException;
import ai.openfabric.api.model.Worker;
import ai.openfabric.api.model.WorkerStatistics;
import ai.openfabric.api.repository.WorkerRepository;
import ai.openfabric.api.repository.WorkerStatisticsRepository;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.dockerjava.api.DockerClient;
import com.github.dockerjava.api.model.Container;
import com.github.dockerjava.api.model.Statistics;
import com.github.dockerjava.core.InvocationBuilder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import javax.transaction.Transactional;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;


@Service
@Slf4j
public class WorkerService {

    private final WorkerRepository workerRepository;
    private final WorkerStatisticsRepository workerStatisticsRepo;
    private final DockerClientService dockerClientService;
    DockerClient dockerClient;

    public WorkerService(WorkerRepository workerRepository, WorkerStatisticsRepository workerStatisticsRepo, DockerClientService dockerClientService) {
        this.workerRepository = workerRepository;
        this.workerStatisticsRepo = workerStatisticsRepo;
        this.dockerClientService = dockerClientService;
        dockerClient = dockerClientService.getDockerClient();
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

    public void initializeWorker(String workerId, WorkerOperation operation) {
        Worker worker = getWorker(workerId);
        if (operation.equals(WorkerOperation.STOP)) {
            dockerClient.stopContainerCmd(worker.getContainerId()).exec();
        } else {
            dockerClient.startContainerCmd(worker.getContainerId()).exec();
        }
    }

    @Transactional
    public void loadWorkersFromDocker() throws IOException {
        log.info("About to load workers from docker...");
        ObjectMapper objectMapper = new ObjectMapper();
        List<Container> containers = dockerClient.listContainersCmd().withShowAll(true).exec();
        List<String> workerIds = new ArrayList<>();
        List<Worker> workers = new ArrayList<>();
        List<WorkerStatistics> workerStatisticsList = new ArrayList<>();
        for (Container container : containers) {
            Worker worker = workerRepository.findByContainerId(container.getId());
            if (worker == null) {
                worker = new Worker();
                worker.setContainerId(container.getId());
                worker.setName(container.getNames()[0]);
                worker.setPorts(objectMapper.writeValueAsString(container.getPorts()));
                worker.setImageId(container.getImageId());
                worker.setState(container.getState());
                worker.setStatus(container.getStatus());
                worker.setPorts(objectMapper.writeValueAsString(container.getPorts()));
                worker.setNetworkSettings(objectMapper.writeValueAsString(container.getNetworkSettings()));
                worker.setCommand(container.getCommand());
            } else {
                worker.setStatus(container.getStatus());
                worker.setState(container.getState());
                worker.setPorts(objectMapper.writeValueAsString(container.getPorts()));
            }
            workerIds.add(container.getId());

            workers.add(worker);

            InvocationBuilder.AsyncResultCallback<Statistics> callback = new InvocationBuilder.AsyncResultCallback<>();

            dockerClient.statsCmd(containers.get(0).getId()).exec(callback);
            Statistics containerStatistics = callback.awaitResult();
            callback.close();

            WorkerStatistics workerStatistics = workerStatisticsRepo.findByWorkerContainerId(worker.getContainerId());
            if (workerStatistics == null) {
                workerStatistics = new WorkerStatistics();
            }

            workerStatistics.setWorker(worker);
            workerStatistics.setNetworks(objectMapper.writeValueAsString(containerStatistics.getNetworks()));
            workerStatistics.setMemoryStatistics(objectMapper.writeValueAsString(containerStatistics.getMemoryStats()));
            workerStatistics.setBlkioStatistics(objectMapper.writeValueAsString(containerStatistics.getBlkioStats()));
            workerStatistics.setCpuStatistics(objectMapper.writeValueAsString(containerStatistics.getCpuStats()));
            workerStatistics.setNumProcs(objectMapper.writeValueAsString(containerStatistics.getNumProcs()));
            workerStatistics.setPidsStatistics(objectMapper.writeValueAsString(containerStatistics.getPidsStats()));
            workerStatistics.setRead(containerStatistics.getRead());
            workerStatisticsList.add(workerStatistics);
        }

        if (!CollectionUtils.isEmpty(workerStatisticsList)) {
            workerRepository.saveAll(workers);
            workerStatisticsRepo.saveAll(workerStatisticsList);
        }

        List<Worker> deletedWorkersList = workerRepository.findAllByContainerIdNotIn(workerIds);
        List<WorkerStatistics> deletedStatisticsList = workerStatisticsRepo.findAllByContainerIdNotIn(workerIds);

        if (!CollectionUtils.isEmpty(deletedWorkersList)) {
            deletedWorkersList.stream().forEach(w -> {
                w.setStatus("deleted");
                w.setState("exited");
                w.deletedAt = new Date();
            });
            workerRepository.saveAll(deletedWorkersList);
        }

        if (!CollectionUtils.isEmpty(deletedStatisticsList)) {
            deletedStatisticsList.stream().forEach(s -> {
                s.deletedAt = new Date();
            });
            workerStatisticsRepo.saveAll(deletedStatisticsList);
        }
        log.info("Finished loading workers from docker.");
    }

}
