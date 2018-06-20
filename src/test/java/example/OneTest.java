package example;

import static org.junit.Assert.*;
import org.junit.Test;

public class OneTest {
  @Test
  public void testFoo() throws Exception {
    One one = new One();
    //Test foo111
      assertEquals("foo", one.foo());
    assertEquals("foo", one.foo());
  }

}