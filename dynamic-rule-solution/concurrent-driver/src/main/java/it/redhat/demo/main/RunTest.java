package it.redhat.demo.main;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import org.jboss.resteasy.client.jaxrs.ResteasyClient;
import org.jboss.resteasy.client.jaxrs.ResteasyClientBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RunTest {
	
	private static final Logger LOG = LoggerFactory.getLogger( RunTest.class );
	
	private static final int PARALLEL_THREADS = Runtime.getRuntime().availableProcessors();
	private static final int NUMBER_OF_TASKS = PARALLEL_THREADS * 3;
	
	public static void main(String[] args) {
		
		LOG.info( "PARALLEL_THREADS: {}", PARALLEL_THREADS );
		LOG.info( "NUMBER_OF_TASKS: {}", NUMBER_OF_TASKS );
		
		ResteasyClient client = new ResteasyClientBuilder()
            .connectionPoolSize(PARALLEL_THREADS)
            .establishConnectionTimeout(10, TimeUnit.SECONDS)
            .socketTimeout(10, TimeUnit.SECONDS)
            .register(new Authenticator("fabio", "fabio$739"))
            .build();
		
		ExecutorService executorService = Executors.newWorkStealingPool( PARALLEL_THREADS );
		StartProcessCommand[] runJobs = new StartProcessCommand[NUMBER_OF_TASKS];
		
		for ( int i = 0; i < NUMBER_OF_TASKS; i ++ ) {
			runJobs[i] = new StartProcessCommand(client);
		}
		
		for ( int i = 0; i < NUMBER_OF_TASKS; i ++ ) {
			executorService.execute( runJobs[i] );
		}
		
		executorService.shutdown();
		try {
			executorService.awaitTermination( 10, TimeUnit.MINUTES );
		}
		catch (InterruptedException e) {
			throw new RuntimeException(e);
		}
		
		client.close();
		
	}

}
