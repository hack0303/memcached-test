package com.creating.www.test;

import static org.junit.Assert.*;

import java.net.InetSocketAddress;
import java.util.concurrent.TimeUnit;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import net.spy.memcached.MemcachedClient;

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
	@Test
	public void test() throws InterruptedException {
		memcachedc.set("key_a",5,"haha");
		memcachedc.get("key_a");
		TimeUnit.SECONDS.sleep(6);
		assertNull(memcachedc.get("key_a"));
	}

}
