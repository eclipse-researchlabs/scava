package org.eclipse.scava.crossflow.restmule.core.client;

import static java.util.concurrent.TimeUnit.SECONDS;

import java.util.concurrent.ExecutorService;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;

import okhttp3.Dispatcher;
import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.jackson.JacksonConverterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * 
 * {@link AbstractClient}
 * <p>
 * @version 1.0.0
 *
 */
public abstract class AbstractClient<T> {

	protected OkHttpClient client;
	protected T callbackEndpoint;

	// These are just utility methods (library)

	protected static OkHttpClient.Builder okHttp(final Dispatcher dispatcher) {
		return new OkHttpClient.Builder()
				.dispatcher(dispatcher)
				.readTimeout(0, SECONDS)
				.connectTimeout(0, SECONDS) 
				.writeTimeout(0, SECONDS)
				.retryOnConnectionFailure(true);
	}

	protected static OkHttpClient.Builder okHttp(ExecutorService executor) {
		return new OkHttpClient.Builder()
				.dispatcher(new Dispatcher(executor))
				.readTimeout(0, SECONDS)
				.connectTimeout(0, SECONDS) 
				.writeTimeout(0, SECONDS)
				.retryOnConnectionFailure(true);
	}

	protected static Retrofit retrofit(OkHttpClient client, String baseUrl){
		ObjectMapper mapper = new ObjectMapper();
		mapper.configure(DeserializationFeature.ACCEPT_SINGLE_VALUE_AS_ARRAY, true);
//		mapper.configure(DeserializationFeature.UNWRAP_SINGLE_VALUE_ARRAYS, true);
		return new Retrofit.Builder()
				.addCallAdapterFactory(RxJava2CallAdapterFactory.createAsync()) 
				.addConverterFactory(JacksonConverterFactory.create(mapper))
//				.addConverterFactory(GsonConverterFactory.create())
				.client(client)
				.baseUrl(baseUrl)
				.build();
	}	

}