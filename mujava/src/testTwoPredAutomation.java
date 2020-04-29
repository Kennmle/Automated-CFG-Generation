import org.junit.Test;
import org.junit.Before;
import org.junit.After;
import static org.junit.Assert.*;

public class testTwoPredAutomation {

    @Before
    public void setUp() {
    }

    @After
    public void tearDown() {
    }

    @Test
    public void testXSmallNotTen() {
        assertEquals(TwoPred.twoPred(1,2),"B");
    }

    @Test
    public void testXSmallandTen() {
        assertEquals(TwoPred.twoPred(3,7),"A");
    }

    @Test
    public void testXLarge() {
        assertEquals(TwoPred.twoPred(2,1),"B");
    }
}