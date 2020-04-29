import org.junit.Test;
import org.junit.Before;
import org.junit.After;
import static org.junit.Assert.*;

public class testSumManual {

    @Before
    public void setUp() {
    }

    @After
    public void tearDown() {
    }

    @Test
    public void testEmpty() {
        int arr[] = {};
        assertEquals(Sum.sum(arr),0);
    }

    @Test
    public void testFull() {
        int arr[] = {1,2};
        assertEquals(Sum.sum(arr),3);
    }
}