import org.junit.Test;
import org.junit.Before;
import org.junit.After;
import static org.junit.Assert.*;

public class testLastZeroAutomation {

    @Before
    public void setUp() {
    }

    @After
    public void tearDown() {
    }

    @Test
    public void testEmpty() {
        int arr[] = {};
        assertEquals(LastZero.lastZero(arr),-1);
    }
    //Different
    @Test
    public void testNone() {
        int arr[] = {1};
        assertEquals(LastZero.lastZero(arr),-1);
    }

    //Different
    @Test
    public void testFound() {
        int arr[] = {1,0};
        assertEquals(LastZero.lastZero(arr),1);
    }
}