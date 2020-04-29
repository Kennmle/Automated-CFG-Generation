import org.junit.Test;
import org.junit.Before;
import org.junit.After;
import static org.junit.Assert.*;

public class testOddOrPosAutomation {

    @Before
    public void setUp() {
    }

    @After
    public void tearDown() {
    }

    @Test
    public void testEmpty() {
        int arr[] = {};
        assertEquals(OddOrPos.oddOrPos(arr),0);
    }

    @Test
    public void testFindOne() {
        int arr[] = {2};
        assertEquals(OddOrPos.oddOrPos(arr),1);
    }

    @Test
    public void testNone() {
        int arr[] = {-4,-2};
        assertEquals(OddOrPos.oddOrPos(arr),0);
    }
}