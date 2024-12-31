package ut.com.kezi.bamboo.metrics;


import org.junit.Test;
import com.kezi.bamboo.metrics.api.MyPluginComponent;
import com.kezi.bamboo.metrics.impl.MyPluginComponentImpl;


public class MyComponentUnitTest
{

    @Test
    public void testMyName()
    {
        MyPluginComponent component = new MyPluginComponentImpl();
//        assertEquals("names do not match!", "myComponent",component.getName());
    }
}