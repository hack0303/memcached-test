package com.creating.www.test;

import static org.junit.Assert.*;

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

import net.spy.memcached.MemcachedClient;
import net.spy.memcached.internal.OperationFuture;
import net.spy.memcached.ops.Operation;

public class BasicTest {
    static MemcachedClient memcachedc=null;
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
	memcachedc=new MemcachedClient(new InetSocketAddress("192.168.159.142",11211));
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
	public void testReplace() throws InterruptedException {
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
	public void testAppend() throws InterruptedException {
		memcachedc.set("key_a",0,"1");
		memcachedc.set("key_a",0,"2");
		memcachedc.set("key_a",0,"3");
		memcachedc.set("key_a",0,"4");	
		String obj=(String) memcachedc.get("key_a");
		assertNotNull(obj);
		assertEquals("4",obj);
	}
	/**
	 * Prepend
	 * */
	@Ignore
	@Test
	public void testPrepend() throws InterruptedException {
		memcachedc.set("key_a",0,"1");
		String obj=(String) memcachedc.get("key_a");
		assertNotNull(obj);
		assertEquals("4",obj);
	}
	/**
	 * override
	 * */
	@Ignore
	@Test
	public void testCAS() throws InterruptedException {
		memcachedc.set("key_a",0,"1");
		memcachedc.set("key_a",0,"2");
		memcachedc.set("key_a",0,"3");
		memcachedc.set("key_a",0,"4");	
		String obj=(String) memcachedc.get("key_a");
		assertNotNull(obj);
		assertEquals("4",obj);
	}
	@Ignore
	@Test
	public void testSTAS() throws InterruptedException {
		Map<SocketAddress,Map<String,String>> m=memcachedc.getStats("key_a");
		for(Map.Entry<SocketAddress,Map<String,String>> x:m.entrySet()) {
			String addr=x.getKey().toString();
			Map<String,String> kvs=x.getValue();
			for(Map.Entry<String,String> e:kvs.entrySet())
			System.out.printf("addr:%s,key:%s,val:%s",addr,e.getKey(),e.getValue());
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
	//@Ignore
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
	
}
