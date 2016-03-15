package io.pivotal.microservices.hystrix.monitor;

import javax.annotation.PostConstruct;

import org.apache.commons.lang3.tuple.Pair;
import org.apache.http.nio.client.HttpAsyncClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.jayway.restassured.path.json.JsonPath;

import rx.apache.http.ObservableHttp;

@Service
public class HystrixEventNotifier {
	
	private final HttpAsyncClient httpAsyncClient;
	
	@Autowired
	public HystrixEventNotifier(HttpAsyncClient httpAsyncClient) {
		// TODO Auto-generated constructor stub
		this.httpAsyncClient = httpAsyncClient;
	}

	@PostConstruct
    public void subscribeToHystrixStream() {
        ObservableHttp.createGet("http://localhost:2222/hystrix.stream", httpAsyncClient).toObservable().
                flatMap(response -> response.getContent().map(String::new)).
                filter(hystrixEvent -> hystrixEvent.startsWith("data:")).
                filter(data -> data.contains("requestCount")).
                map(data -> data.substring("data:".length())).
                map(data -> JsonPath.from(data).getInt("requestCount")).
                filter(requestCount -> requestCount > 0).
                doOnNext(requestCount -> {
                    if (requestCount > 40) {
                        System.out.println("*************Request Volume Reached Threshold*************");
                    }
                }).
                doOnError(throwable -> throwable.printStackTrace()).
                subscribe();
    }

}
