package io.pivotal.microservices.hystrix.monitor;

import org.springframework.stereotype.Service;

@Service
public class HystrixHealth {

	private boolean isCircuitOpen;
	
	public HystrixHealth() {
		this.isCircuitOpen = false;
	}

	public boolean isCircuitOpen() {
		return isCircuitOpen;
	}

	public void setCircuitOpen(boolean isCircuitOpen) {
		this.isCircuitOpen = isCircuitOpen;
	}
	
	
}
