import org.junit.Test;
import org.junit.Before;
import org.junit.After;
import static org.junit.Assert.*;

public class testFindLastAutomation {

    @Before
    public void setUp() {
    }

    @After
    public void tearDown() {
    }

    @Test
    public void testEmpty() {
        int arr[] = {};
        assertEquals(FindLast.findLast(arr,0),-1);
    }
    //Different
    @Test
    public void testNone() {
        int arr[] = {0};
        assertEquals(FindLast.findLast(arr,1),-1);
    }
    //Different
    @Test
    public void testFound() {
        int arr[] = {0,1};
        assertEquals(FindLast.findLast(arr,1),1);
    }
}