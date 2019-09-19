package ch.exense.util;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;
import java.util.Random;
import java.util.TreeMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class CpuTest{

	public static void main(String... args){
		if(args.length != 2 && args.length != 4) {
			System.out.println("syntax= java -jar cpuTest.jar <nbThreads> <nbSeconds> <workload[1|2|3|all]> <nbRampupSeconds>");
			System.exit(0);
		}
		try {
			new CpuTest().burnCpu(Integer.parseInt(args[0]), Long.parseLong(args[1]), args[2], args.length == 4? Long.parseLong(args[3]) : 0L);
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	private void burnCpu(final int nbThreads, final long nbMilliseconds, final String workload, final long rampupTime) {

		System.out.println("[MAIN THREAD] Getting ready to burn CPU with " + nbThreads + " threads, for " + nbMilliseconds + " ms, and " + rampupTime + " milliseconds of rampup time.");

		ExecutorService executorService = Executors.newFixedThreadPool(nbThreads);

		for(long i = 0; i<nbThreads; i++) {
			final long waitRatio;
			if(rampupTime > 0 && nbThreads > 1) {
				waitRatio = rampupTime / (nbThreads - 1);
			}else {
				waitRatio = 0;
			}

			final long wait = waitRatio * i; 
			System.out.println("[MAIN THREAD] Starting thread " + i+ "/"+nbThreads+" .");
			executorService.submit(new Runnable() {

				@Override
				public void run() {
					//String threadName = Thread.currentThread().getId() +" - " + Thread.currentThread().getName();
					String threadName = String.format("%03d", Thread.currentThread().getId());
					System.out.println("["+threadName+"] Rampup wait: " + wait + "ms.");
					try {
						Thread.sleep(wait);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
					iWasteCPU(nbMilliseconds - wait, threadName, workload);
				}

			});

		}

		System.out.println("[MAIN THREAD] Shutting down executor...");
		executorService.shutdown();
		System.out.println("[MAIN THREAD] Complete.");


		System.out.println("[MAIN THREAD] Awaiting termination...");
		try {
			executorService.awaitTermination(nbMilliseconds, TimeUnit.SECONDS);
		} catch (InterruptedException e) {
			System.out.println("[MAIN THREAD] Something went wrong.");
			e.printStackTrace();
		}
	}


	public void iWasteCPU(long howLong, String id, String workload) {
		SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss.SSS");
		long begin = System.currentTimeMillis();

		while (System.currentTimeMillis() - begin < howLong) {
			final Date start = new Date();
			System.out.println("["+id+"] Check -> Wasting CPU...");
			int outerLoop = 100000;
			int innerLoop =      100;

			for(int i = 0; i < outerLoop; i++) {
				switch(workload) {
				case "1":
					workload1(innerLoop);
					break;
				case "2":
					workload2(innerLoop);
					break;
				case "3":
					workload3(innerLoop);
					break;
				case "all":
					workload1(innerLoop);
					workload2(innerLoop);
					workload3(innerLoop);
					break;
				default:
					break;
				}

			}
			System.out.println(sdf.format(start)+";"+start.getTime()+";" +id+";" + (System.currentTimeMillis() - start.getTime()));
		}
		System.out.println("["+id+"] Check -> Done!");
	}


	private void workload1(int loopNumber) {

		Map<String, String> aCertainHashMap = new TreeMap<String, String>();
		Map<String, String> anotherHashMap = new TreeMap<String, String>();

		Random r = new Random();
		for (int i = 1; i < loopNumber; i++) {
			String key = "k"+r.nextInt(100000)+r.nextInt(100000);
			String value = "v"+r.nextInt(100000)+r.nextInt(100000);
			aCertainHashMap.put(key, value);
			if (i != 0 && key != value) {// throw off the JIT
				anotherHashMap.putAll(aCertainHashMap);
				aCertainHashMap.remove(key);
			}
			anotherHashMap.clear();
		}
	}


	private void workload2(int loopNumber) {

		Map<String, String> aCertainHashMap = new TreeMap<String, String>();
		Map<String, String> anotherHashMap = new TreeMap<String, String>();
		Random r = new Random();
		for (int i=0; i < loopNumber; i++) {
			aCertainHashMap.put("key" + i, "value" + r.nextInt(100000));
			if (i != 0) {
				aCertainHashMap.remove("key" + (i - 1));
				anotherHashMap.putAll(aCertainHashMap);
			}

			aCertainHashMap.remove("key" + i);

		}

	}


	private String workload3(int loopNumber) {
		Random r = new Random();
		String s = "";
		for (int ii = 0 ; ii < loopNumber ; ii++){
			s = ("this XXX a test" + "----- XX YY XXX" + r.nextInt(100000)).replaceAll("XXX", "test" + r.nextInt(100000));
		}
		return s;
	}

}