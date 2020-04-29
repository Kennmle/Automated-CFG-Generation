import org.junit.Test;
import org.junit.Before;
import org.junit.After;
import static org.junit.Assert.*;

public class testCountPositiveManual {

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

    @Test
    public void testIterate() {
        int arr[] = {1,0};
        assertEquals(CountPositive.countPositive(arr),1);
    }
}