import org.junit.Test;
import org.junit.Before;
import org.junit.After;
import static org.junit.Assert.*;

public class testCountPositiveAutomation {

    @Before
    public void setUp() {
    }

    @After
    public void tearDown() {
    }

    @Test
    public void testEmpty() {
        int arr[] = {};
        assertEquals(CountPositive.countPositive(arr),0);
    }
    //Different
    @Test
    public void testSizeOne() {
        int arr[] = {1};
        assertEquals(CountPositive.countPositive(arr),1);
    }
    //Different
    @Test
    public void testIterate() {
        int arr[] = {-2,-1};
        assertEquals(CountPositive.countPositive(arr),0);
    }
}