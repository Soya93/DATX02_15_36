package se.chalmers.datx02_15_36.studeraeffektivt;

import junit.framework.TestCase;

import se.chalmers.datx02_15_36.studeraeffektivt.model.AddNumbers;

/**
 * Created by emmawestman on 15-01-30.
 */
public class AddNumberTest extends TestCase {

    @Override
    protected void setUp() throws Exception {
        super.setUp();
    }

    public void testAddNumbers() {
        int result = AddNumbers.addNumbers(2, 6);

        assertEquals(8, result);
    }

    @Override
    protected void tearDown() throws Exception {
        super.tearDown();
    }
}
