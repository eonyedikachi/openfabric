package ai.openfabric.api.service;

import com.github.dockerjava.api.DockerClient;
import com.github.dockerjava.core.DockerClientBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;

@Service
public class DockerClientService {

    private DockerClient dockerClient;

    @Value("${docker.url}")
    private String dockerUrl;
    @PostConstruct
    public void initializeDockerClient(){
        dockerClient = DockerClientBuilder.getInstance(dockerUrl).build();
    }

    public DockerClient getDockerClient(){
        return dockerClient;
    }
}
