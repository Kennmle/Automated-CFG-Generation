import org.junit.Test;
import org.junit.Before;
import org.junit.After;
import static org.junit.Assert.*;

public class testCalManual {

    @Before
    public void setUp() {
    }

    @After
    public void tearDown() {
    }

    @Test
    public void testSameMonth() {
        assertEquals(Cal.cal(1,3,1,10,2007),7);
    }

    @Test
    public void testNormal() {
        assertEquals(Cal.cal(5,3,2,10,2007),38);
    }

    @Test
    public void testLeap() {
        assertEquals(Cal.cal(1,3,4,10,2016),98);
    }
}