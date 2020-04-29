import org.junit.Test;
import org.junit.Before;
import org.junit.After;
import static org.junit.Assert.*;

public class testFindValAutomation {

    @Before
    public void setUp() {
    }

    @After
    public void tearDown() {
    }

    @Test
    public void testEmpty() {
        int arr[] = {};
        assertEquals(FindVal.findVal(arr,0),-1);
    }

    //Different
    @Test
    public void testFound() {
        int arr[] = {1};
        assertEquals(FindVal.findVal(arr,1),0);
    }

    //Different
    @Test
    public void testMissing() {
        int arr[] = {1,2};
        assertEquals(FindVal.findVal(arr,0),0);
    }
}