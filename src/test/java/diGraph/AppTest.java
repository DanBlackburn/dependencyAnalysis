package diGraph;


import org.junit.Test;

/**
 * Unit test for simple App.
 */
// TODO create tests with assertions and a small testset
public class AppTest
{

    @Test
    public void testDirectories() {
        App.main(new String[] {"src/test/resources/axis"});
    }

    @Test
    public void testSimpleJar() {
        App.main(new String[] {"src/test/resources/axis-1.4.jar"});
    }

    @Test
    public void testJarInJar() {
        App.main(new String[] {"src/test/resources/axis-inside-axis.jar"});
    }

}

