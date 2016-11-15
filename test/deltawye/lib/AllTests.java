package deltawye.lib;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

@SuppressWarnings("javadoc")
@RunWith(Suite.class)
@SuiteClasses({ AtomicPlaneGraphTest.class, AtomicVertexTest.class, CircuitTest.class,
        EdgeTailTest.class, MedialGraphTest.class, PlaneGraphTest.class,
        UndirectedEdgeTest.class, WalkTest.class })

public class AllTests {

}
