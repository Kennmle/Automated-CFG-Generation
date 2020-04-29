import org.junit.Test;
import org.junit.Before;
import org.junit.After;
import static org.junit.Assert.*;

public class testLastZeroManual {

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

    @Test
    public void testFound() {
        int arr[] = {0};
        assertEquals(LastZero.lastZero(arr),0);
    }

    @Test
    public void testNone() {
        int arr[] = {1,2};
        assertEquals(LastZero.lastZero(arr),-1);
    }
}