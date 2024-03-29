package md.manastirli;

import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@EnableDiscoveryClient
@SpringBootApplication
public class Main {

    public static void main(String[] args) {
        System.out.println("Hello world!");
    }
}