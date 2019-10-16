package com.creating.www.test;

import java.net.InetSocketAddress;
import java.util.concurrent.TimeUnit;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import net.spy.memcached.MemcachedClient;
/**
 * 0
 * 30days
 * timestamp
 * cas use version control
 * */
public class MultiServerTest {
    static MemcachedClient memcachedc=null;
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
	memcachedc=new MemcachedClient(new InetSocketAddress("192.168.159.146",11211));
	//memcachedc=new MemcachedClient(new InetSocketAddress("192.168.159.143",11211));
	memcachedc=new MemcachedClient(new InetSocketAddress("192.168.159.146",11211),new InetSocketAddress("192.168.159.143",11211));
	
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
	/*新加一台主机，然后使用时，之前主机上的值获取不到
	 * 多机有问题
	 */
	@Test
	public void test() throws InterruptedException {
		//memcachedc.set("key_a",0,"asd");
		System.out.println(memcachedc.get("key_a"));
		System.out.println(memcachedc.get("key_b"));
		System.out.println(memcachedc.get("key_c"));
		System.out.println(memcachedc.get("key_a_in_node01"));
	}
}
