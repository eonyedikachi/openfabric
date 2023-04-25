package ai.openfabric.api.model;


import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.io.Serializable;

@Entity()
@Getter
@Setter
@Table(name = "worker")
public class Worker extends Datable implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY, generator = "of-uuid")
    @GenericGenerator(name = "of-uuid", strategy = "ai.openfabric.api.model.IDGenerator")
    public String id;

    public String name;
    @Column(name = "container_id")
    @NotNull
    private String containerId;
    public String ports;
    @Column(name = "image_id")
    public String imageId;
    public String state;
    public String status;
    @Column(name = "network_settings")
    public String networkSettings;
    public String command;

    @OneToOne(mappedBy = "worker", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonIgnore
    private WorkerStatistics statistics;


}
