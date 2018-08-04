/*
 * [y] hybris Platform
 *
 * Copyright (c) 2018 SAP SE or an SAP affiliate company. All rights reserved.
 *
 * This software is the confidential and proprietary information of SAP
 * ("Confidential Information"). You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms of the
 * license agreement you entered into with SAP.
 */
package com.hybris.ymkt.recommendation.services;

import de.hybris.bootstrap.annotations.ManualTest;

import java.io.IOException;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Proxy;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.List;
import java.util.LongSummaryStatistics;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import org.junit.BeforeClass;
import org.junit.Test;

import com.hybris.ymkt.common.http.HttpURLConnectionService;
import com.hybris.ymkt.common.odata.ODataService;
import com.hybris.ymkt.recommendation.dao.RecommendationScenario;
import com.hybris.ymkt.recommendation.dao.RecommendationScenario.Scenario;


@ManualTest
public class RecommendationServicePerfTest
{

	protected static class ExecuteRecoScenario implements Runnable
	{
		boolean failed;
		long time;

		@Override
		public void run()
		{
			long timeStart = System.currentTimeMillis();
			try
			{
				final RecommendationScenario recoScenario = new RecommendationScenario("40F2E9306E391ED59BDE581AFE71F329",
						"COOKIE_ID");
				recoScenario.getScenarios().add(new Scenario("SAP_MOST_VIEWED_EMAIL_CAMPAIGN"));
				recoService.executeRecommendationScenario(recoScenario, false);
			}
			catch (IOException e)
			{
				failed = true;
			}
			finally
			{
				time = System.currentTimeMillis() - timeStart;
			}
		}
	}

	protected static class ExecuteRecoScenarios implements Callable<ExecuteRecoScenarios>
	{
		List<ExecuteRecoScenario> executions = new ArrayList<>(1200);

		@Override
		public ExecuteRecoScenarios call() throws Exception
		{
			long timeStart = System.currentTimeMillis();
			while (System.currentTimeMillis() < timeStart + THREADS_TIME)
			{
				ExecuteRecoScenario execution = new ExecuteRecoScenario();
				execution.run();
				executions.add(execution);
			}
			return this;
		}
	}

	public static interface FunctionEx<T, R>
	{
		R apply(T t) throws Exception;
	}

	static final HttpURLConnectionService httpURLConnectionService = new HttpURLConnectionService();
	static final ODataService oDataService = new ODataService();
	static final RecommendationService recoService = new RecommendationService();
	static final int THREADS = 32;
	static final int THREADS_TIME = 1000 * 30;

	static <T, R> Function<T, R> $(final FunctionEx<T, R> f)
	{
		return t -> {
			try
			{
				return f.apply(t);
			}
			catch (Exception e)
			{
				throw new RuntimeException(e);
			}
		};
	}

	public static void disableCertificates() throws Exception
	{
		final TrustManager[] trustAllCerts =
		{ (TrustManager) Proxy.getProxyClass(X509TrustManager.class.getClassLoader(), X509TrustManager.class)
				.getConstructor(InvocationHandler.class).newInstance((InvocationHandler) (o, m, args) -> null) };

		final SSLContext sc = SSLContext.getInstance("SSL");
		sc.init(null, trustAllCerts, new SecureRandom());
		HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory());
	}

	@BeforeClass
	public static void setUpBeforeClass() throws Exception
	{
		disableCertificates();
		oDataService.setHttpURLConnectionService(httpURLConnectionService);
		oDataService.setRootUrl("https://localhost:44300/sap/opu/odata/sap/PROD_RECO_RUNTIME_SRV");
		oDataService.setSapClient("");

		oDataService.setUser("");
		oDataService.setPassword("");

		recoService.setODataService(oDataService);
	}

	@Test
	public void testPerformance() throws Exception
	{
		final long timeStart = System.currentTimeMillis();

		ExecutorService taskExecutor = Executors.newFixedThreadPool(THREADS);

		List<ExecuteRecoScenarios> multiExecutions = Stream.generate(ExecuteRecoScenarios::new).limit(THREADS)
				.collect(Collectors.toList());

		List<ExecuteRecoScenario> executions = taskExecutor.invokeAll(multiExecutions).stream() //
				.map($(Future::get)) //
				.map(exes -> exes.executions) //
				.flatMap(List::stream) //
				.collect(Collectors.toList());

		long timeTotal = System.currentTimeMillis() - timeStart;
		int total = executions.size();
		long failed = executions.stream().filter(exe -> exe.failed).count();

		LongSummaryStatistics stats = executions.stream().mapToLong(exe -> exe.time).summaryStatistics();
		long time25 = executions.stream().mapToLong(exe -> exe.time).sorted().limit(total * 1 / 4).max().getAsLong();
		long time50 = executions.stream().mapToLong(exe -> exe.time).sorted().limit(total * 2 / 4).max().getAsLong();
		long time75 = executions.stream().mapToLong(exe -> exe.time).sorted().limit(total * 3 / 4).max().getAsLong();

		System.out.format("Results (%2d threads) : %dms %4d(%d), min=%dms <25=%dms <50=%dms <75 %dms max=%5dms avg=%.2fms", THREADS,
				timeTotal, total, failed, stats.getMin(), time25, time50, time75, stats.getMax(), stats.getAverage());
	}
}
