package ai.openfabric.api.controller;

import ai.openfabric.api.model.Worker;
import ai.openfabric.api.model.WorkerStatistics;
import ai.openfabric.api.service.WorkerService;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;


@CrossOrigin
@Validated
@RestController
@RequestMapping("${node.api.path}/worker")
public class WorkerController {


    private final WorkerService workerService;

    public WorkerController(WorkerService workerService) {
        this.workerService = workerService;
    }

    @PostMapping(path = "/hello")
    public @ResponseBody String hello(@RequestBody String name) {
        return "Hello!" + name;
    }

    @Operation(summary = "Get paginated list of workers",
            description = "Returns paginated list of workers")
    @GetMapping(path = "/getPaginatedWorkers")
    public ResponseEntity<Page<Worker>> getPaginatedWorkers(@RequestParam("page") int page, @RequestParam("pageSize") int pageSize) {
        return ResponseEntity.ok(workerService.getPaginatedWorkers(page, pageSize));
    }

    @Operation(summary = "Get worker with the corresponding id",
            description = "Returns worker with the corresponding id")
    @GetMapping
    public ResponseEntity<Worker> getWorker(@RequestParam("workerId") String workerId) {
        return ResponseEntity.ok(workerService.getWorker(workerId));
    }

    @Operation(summary = "Get worker statistics with the corresponding worker id",
            description = "Returns worker statistics with the corresponding worker id")
    @GetMapping(path = "/getStatistics")
    public ResponseEntity<WorkerStatistics> getStatistics(@RequestParam("workerId") String workerId) {
        return ResponseEntity.ok(workerService.getWorkerStatistics(workerId));
    }

}
