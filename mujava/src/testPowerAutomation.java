import org.junit.Test;
import org.junit.Before;
import org.junit.After;
import static org.junit.Assert.*;
//SAME
public class testPowerAutomation {

    @Before
    public void setUp() {
    }

    @After
    public void tearDown() {
    }

    @Test
    public void testTrivial() {
        assertEquals(Power.power(2,0),1);
    }

    @Test
    public void testFirstPower() {
        assertEquals(Power.power(2,1),2);
    }

    @Test
    public void testCube() {
        assertEquals(Power.power(2,3),8);
    }
}