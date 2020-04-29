import org.junit.Test;
import org.junit.Before;
import org.junit.After;
import static org.junit.Assert.*;

public class testTriangleAutomation {

    @Before
    public void setUp() {
    }

    @After
    public void tearDown() {
    }

    @Test
    public void testNegative() {
        assertEquals(TriangleType.triangle(-1,2,2),Triangle.INVALID);
    }

    @Test
    public void testImpossible() {
        assertEquals(TriangleType.triangle(1,1,2),Triangle.INVALID);
    }

    @Test
    public void testEquilateral() {
        assertEquals(TriangleType.triangle(1,1,1),Triangle.EQUILATERAL);
    }

    @Test
    public void testIsosceles() {
        assertEquals(TriangleType.triangle(2,2,3),Triangle.ISOSCELES);
    }

    @Test
    public void testScalene() {
        assertEquals(TriangleType.triangle(3,4,5),Triangle.SCALENE);
    }
}