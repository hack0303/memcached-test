package com.creating.www.test;

import static org.junit.Assert.*;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.util.Map;
import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;

import net.spy.memcached.CASValue;
import net.spy.memcached.MemcachedClient;
import net.spy.memcached.internal.OperationFuture;
/**
 * 0
 * 30days
 * timestamp
 * cas use version control
 * */
public class BasicTest {
    static MemcachedClient memcachedc=null;
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
	memcachedc=new MemcachedClient(new InetSocketAddress("192.168.159.146",11211));
	}

	@AfterClass
	public static void tearDownAfterClass() throws Exception {
	}

	@Before
	public void setUp() throws Exception {
	}

	@After
	public void tearDown() throws Exception {
		memcachedc.shutdown(10,TimeUnit.SECONDS);
	}
	@Ignore
	@Test
	public void test() throws InterruptedException {
		memcachedc.set("key_a",5,"haha");
		memcachedc.get("key_a");
		TimeUnit.SECONDS.sleep(6);
		assertNull(memcachedc.get("key_a"));
	}
	@Ignore
	@Test
	public void testGets() throws InterruptedException {
		CASValue<Object> casv=memcachedc.gets("key_a");
		//对象版本号
		System.out.println(casv.getCas());
	}
	/**
	 * override
	 * */
	@Ignore
	@Test
	public void testSet() throws InterruptedException {
		memcachedc.set("key_a",0,"1");
		memcachedc.set("key_a",0,"2");
		memcachedc.set("key_a",0,"3");
		memcachedc.set("key_a",0,"4");	
		String obj=(String) memcachedc.get("key_a");
		assertNotNull(obj);
		assertEquals("4",obj);
	}
	/**
	 * override
	 * */
	@Ignore
	@Test
	public void testAdd() throws InterruptedException {
		memcachedc.set("key_a",0,"1");
		memcachedc.add("key_a",0,"2");
		String obj=(String) memcachedc.get("key_a");
		assertNotNull(obj);
		System.out.println(obj);
		assertEquals("1",obj);
	}
	/**
	 * override
	 * */
	@Ignore
	@Test
	public void testReplace() throws InterruptedException {
		String key="key_a";
		memcachedc.set(key,0,"1");
		System.out.println(memcachedc.get(key));
		
		memcachedc.replace(key,0,"2");
		String obj=(String) memcachedc.get(key);
		assertNotNull(obj);
		assertEquals("2",obj);
		System.out.println(memcachedc.get(key));
		//can effect after existing some value associalate key
		memcachedc.replace("key_b",0,"hahaha");
		assertNull(memcachedc.get("key_b"));
	}
	/**
	 * override
	 * */
	@Ignore
	@Test
	public void testAppend() throws InterruptedException {
	    String key="key_a";
	    memcachedc.append(memcachedc.gets(key).getCas(), key,"hiasdas");
	    System.out.println(memcachedc.get("key_a"));
	}
	/**
	 * Prepend
	 * */
	@Ignore
	@Test
	public void testPrepend() throws InterruptedException {
		String key="key_a";
		String begin="begin";
		memcachedc.set(key,0,begin);
		String prepText="prepend";
		memcachedc.prepend(memcachedc.gets(key).getCas(),key,prepText);
		String obj=(String) memcachedc.get("key_a");		
		assertNotNull(obj);
		assertEquals(prepText+begin,obj);
		System.out.println(obj);
	}
	@Ignore
	@Test
	public void testCAS1() throws InterruptedException {
		String key="key_a";
		memcachedc.set(key,0,"1");
		long casId=memcachedc.gets(key).getCas();
		//other dosometing
		memcachedc.cas(key,memcachedc.gets(key).getCas(),"51");
		memcachedc.cas(key, casId, "52");
		String obj=(String) memcachedc.get(key);
		assertNotNull(obj);
		System.out.printf("want:%s,actual:%s%n","52","51");
		assertEquals("51",obj);
	}
	@Test
	public void testCAS1_() throws InterruptedException {
		String key="key_a";
		memcachedc.set(key,0,"1");
		long casId=memcachedc.gets(key).getCas();
		//other dosometing
		//memcachedc.cas(key,memcachedc.gets(key).getCas(),"51");
		//please understand by yourself,because my english do not support me to explain it,sorry
		System.out.println("other server turn,please do something");
		TimeUnit.SECONDS.sleep(20);
		System.out.println("my turn");
		memcachedc.cas(key, casId, "52");
		String obj=(String) memcachedc.get(key);
		assertNotNull(obj);
		System.out.printf("want:%s,actual:%s%n","52","51");
		assertEquals("51",obj);
	}
	/**
	 * override
	 * pass
	 * */
	@Ignore
	@Test
	public void testCAS2() throws InterruptedException {
		String key="key_a";
		memcachedc.set(key,0,"1");
		System.out.println(memcachedc.get(key));
		new Thread(new Runnable() {
			
			@Override
			public void run() {
				try {
				MemcachedClient memcachedc=new MemcachedClient(new InetSocketAddress("192.168.159.146",11211));
				//memcachedc.set("key_a",0,4);
				memcachedc.cas("key_a",memcachedc.gets("key_a").getCas(),4);
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
			}
		}).start();
		
		TimeUnit.SECONDS.sleep(10);
		System.out.println("---开始---");
		memcachedc.cas(key,memcachedc.gets(key).getCas(),"51");
		String obj=(String) memcachedc.get(key);
		assertNotNull(obj);
		assertEquals("51",obj);
	}
	@Ignore
	@Test
	public void testSTAS() throws InterruptedException {
		Map<SocketAddress,Map<String,String>> m=memcachedc.getStats();
		for(Map.Entry<SocketAddress,Map<String,String>> x:m.entrySet()) {
			String addr=x.getKey().toString();
			Map<String,String> kvs=x.getValue();
			for(Map.Entry<String,String> e:kvs.entrySet())
			System.out.printf("addr:%s,key:%s,val:%s%n",addr,e.getKey(),e.getValue());
		}
	}
	@Ignore
	@Test
	public void testIncrAndDecr() throws InterruptedException {
		String key="key_a";
		Object origin=memcachedc.get(key);
       assertNotNull(origin);
        System.out.println(origin.toString());
        memcachedc.incr(key,1);
        System.out.println(memcachedc.get(key).toString());
        memcachedc.decr(key,1);
        System.out.println(memcachedc.get(key).toString());
	}
	@Ignore
	@Test
	public void testIncrAndDecrConcurrency() throws InterruptedException {
		String key="key_a";
		//reset key_a to 4
		memcachedc.set(key,0,"4");
		System.out.println("reset key_a to 4");
		Object origin=memcachedc.get(key);
       assertNotNull(origin);
        System.out.println(origin.toString());
        /**
         * begin 4,start 100 threads,1 per thread increat,except 104
         * */
      for(int x=0;x<100;x++) {
    	new IncrAndDecrThread(memcachedc, key,1).start();
      }
      TimeUnit.SECONDS.sleep(2);
      assertEquals("104",memcachedc.get(key));
      System.out.println("actual:"+memcachedc.get(key));
        
	}
	static class IncrAndDecrThread extends Thread{
	static CyclicBarrier cb=new CyclicBarrier(100);
		net.spy.memcached.MemcachedClient memcachedc=null;
		int slot=0;
		String key="";
		public IncrAndDecrThread(net.spy.memcached.MemcachedClient memcachedc,String key,int slot) {
			this.memcachedc=memcachedc;
			this.slot=slot;
			this.key=key;
		}
		@Override
		public void run() {
			try {
				cb.await();
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (BrokenBarrierException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			System.out.println("running");
			this.memcachedc.incr(this.key,this.slot);
		}
	}
	/**
	 * delete(String,int)
	 * @throws ExecutionException 
	 * */
	@Ignore
	@Test
	public void testDeleteSI() throws InterruptedException, ExecutionException {
		memcachedc.set("key_b",0,"hahaha");
		//这个指定时间值，没卵用了
         OperationFuture<Boolean> op=memcachedc.delete("key_b",10);
	     assertTrue(op.get().booleanValue());
	    assertNotNull( memcachedc.get("key_b"));
	}
	/**
	 * delete(String)
	 * @throws ExecutionException 
	 * */
	@Ignore
	@Test
	public void testDeleteS() throws InterruptedException, ExecutionException {
		memcachedc.set("key_b",0,"hahaha");
         OperationFuture<Boolean> op=memcachedc.delete("key_b");
	     assertTrue(op.get().booleanValue());
	    assertNull( memcachedc.get("key_b"));
	}
	/**
	 * flushAll()
	 * @throws ExecutionException 
	 * */
	@Ignore
	@Test
	public void testFlushAll() throws InterruptedException, ExecutionException {
		memcachedc.set("key_a",0,"hahaha");
		memcachedc.set("key_b",0,"hahaha");
		memcachedc.set("key_c",0,"hahaha");
		memcachedc.set("key_d",0,"hahaha");
		memcachedc.flush();
		assertNull(memcachedc.get("key_a"));
		assertNull(memcachedc.get("key_b"));
		assertNull(memcachedc.get("key_c"));
		assertNull(memcachedc.get("key_d"));
	}
	/**
	 * flushAll(int)
	 * @throws ExecutionException 
	 * */
	@Ignore
	@Test
	public void testFlushAllI() throws InterruptedException, ExecutionException {
		memcachedc.set("key_a",0,"hahaha");
		memcachedc.set("key_b",0,"hahaha");
		memcachedc.set("key_c",0,"hahaha");
		memcachedc.set("key_d",0,"hahaha");
		//application delay time
		memcachedc.flush(5);
		assertNotNull(memcachedc.get("key_a"));
		assertNotNull(memcachedc.get("key_b"));
		assertNotNull(memcachedc.get("key_c"));
		assertNotNull(memcachedc.get("key_d"));
		TimeUnit.SECONDS.sleep(5);
		assertNull(memcachedc.get("key_a"));
		assertNull(memcachedc.get("key_b"));
		assertNull(memcachedc.get("key_c"));
		assertNull(memcachedc.get("key_d"));
	}
	/**
	 * override
	 * */
	@Ignore
	@Test
	public void testGet() throws InterruptedException {
		String key="key_a";
		memcachedc.set(key, 0, "hhhaasd1");
		memcachedc.set(key, 0, "hhhaasd2");
		memcachedc.set(key, 0, "hhhaasd3");
		System.out.println(memcachedc.get(key));
		
	}
}
