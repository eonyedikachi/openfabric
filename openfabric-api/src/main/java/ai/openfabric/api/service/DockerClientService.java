package ai.openfabric.api.service;

import com.github.dockerjava.api.DockerClient;
import com.github.dockerjava.core.DockerClientBuilder;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;

@Service
public class DockerClientService {

    private DockerClient dockerClient;

    @PostConstruct
    public void initializeDockerClient(){
        dockerClient = DockerClientBuilder.getInstance("tcp://localhost:2375").build();
    }
}
