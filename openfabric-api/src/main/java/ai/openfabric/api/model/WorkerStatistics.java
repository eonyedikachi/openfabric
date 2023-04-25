package ai.openfabric.api.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.io.Serializable;


@Entity()
@Data
public class WorkerStatistics extends Datable implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY, generator = "of-uuid")
    @GenericGenerator(name = "of-uuid", strategy = "ai.openfabric.api.model.IDGenerator")
    private String id;
    private String networks;
    @Column(name = "memory_statistics")
    private String memoryStatistics;
    @Column(name = "blkio_statistics")
    private String blkioStatistics;
    @Column(name = "cpu_statistics")
    private String cpuStatistics;
    @Column(name = "num_procs")
    private String numProcs;
    @Column(name = "pid_statistics")
    private String pidsStatistics;
    private String read;

    @OneToOne()
    @JsonIgnore
    private Worker worker;

}

