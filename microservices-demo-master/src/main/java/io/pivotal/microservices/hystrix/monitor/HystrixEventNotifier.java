package io.pivotal.microservices.hystrix.monitor;

import java.io.IOException;

import javax.annotation.PostConstruct;

import org.apache.commons.lang3.tuple.Pair;
import org.apache.http.nio.client.HttpAsyncClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.jayway.restassured.path.json.JsonPath;

import rx.apache.http.ObservableHttp;

@Service
public class HystrixEventNotifier {
	
	private final Logger log = LoggerFactory.getLogger(HystrixEventNotifier.class);
	private final HttpAsyncClient httpAsyncClient;
	private final HystrixHealth hystrixHealth;
	
	@Autowired
	public HystrixEventNotifier(HttpAsyncClient httpAsyncClient, HystrixHealth hystrixHealth) {
		// TODO Auto-generated constructor stub
		this.httpAsyncClient = httpAsyncClient;
		this.hystrixHealth = hystrixHealth;
	}

	@PostConstruct
	public void subscribeToHystrixStream() {
		log.info("Subscribe");
        ObservableHttp.createGet("http://localhost:" + "2222" + "/hystrix.stream", httpAsyncClient).toObservable().
                flatMap(response -> response.getContent().map(String::new)).
                filter(hystrixEvent -> hystrixEvent.startsWith("data:")).
                filter(data -> data.contains("isCircuitBreakerOpen")).
                map(data -> data.substring("data:".length())).
                map(data -> JsonPath.from(data).getBoolean("isCircuitBreakerOpen")).
                map(isCircuitBreakerCurrentlyOpened -> Pair.of(isCircuitBreakerCurrentlyOpened, hystrixHealth.isCircuitOpen())).
                filter(pair -> pair.getLeft() != pair.getRight()).
                map(Pair::getLeft).
                doOnNext(isCircuitBreakerOpened -> {
                   if (isCircuitBreakerOpened) {
                        hystrixHealth.setCircuitOpen(true);
                    } else {
                        hystrixHealth.setCircuitOpen(false);
                    }
               }).            
            doOnError(throwable -> log.error("Error",throwable)).subscribe(isCircuitBreakerOpened -> {
            	log.info("isCircuiBreakerOpen: " + isCircuitBreakerOpened.toString());
            	if(isCircuitBreakerOpened) {
            		Runtime rt = Runtime.getRuntime();
             		String dir = "C:\\Users\\osampath\\Documents\\GIT\\microservices-demo-master";
             		try {
             			rt.exec("cmd.exe /c cd \""+dir+"\" & start cmd.exe /k \"java -jar target/microservice-demo-0.0.1-SNAPSHOT.jar accounts 2224\"");
             		} catch (IOException e) {
             			// TODO Auto-generated catch block
             			e.printStackTrace();
             		}
            	}
            });
    }

}
