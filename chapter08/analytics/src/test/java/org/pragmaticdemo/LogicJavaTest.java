package org.pragmaticdemo;

import org.junit.*;
import scala.collection.immutable.*;

public class LogicJavaTest {
	@Test
	public void testSku() {
        Sku sku = new Sku(1, new HashSet());
        // in chapter 5 we have Assert.assertEquals(1, sku.attributes().size());
        // but as part of the chapter, we correct it - this test should pass
		Assert.assertEquals(0, sku.attributes().size());
	}
}