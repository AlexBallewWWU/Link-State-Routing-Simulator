import org.junit.runner.JUnitCore;
import org.junit.runner.Request;
import org.junit.runner.Result;
import org.junit.runner.notification.RunListener;
import org.junit.runner.notification.Failure;
import org.junit.runner.Description;
import java.util.List;

class TestListener extends RunListener {
    public void testStarted(Description description) {
        System.out.println("Running " + description);
    }

    public void testFailure(Failure failure) {
        System.out.println("Test failure: " + failure.getDescription() + ":"
            + failure.getMessage());
    }
}

/**
 * This is a Java program that is used to run tests against your code.
 * It uses a Java testing package called JUnit. If you're interested you
 * can read up about how it works.
 */
public class TestRunner {
    public static void main(String[] args) throws ClassNotFoundException {
        if (args.length == 0) {
            System.err.println("No test class specified");
            System.exit(1);
        }

        Request request;
        String[] classAndMethod = args[0].split("#");
        if (classAndMethod.length == 1) {
            // only have class
            // System.out.println("Running " + classAndMethod[0] + "... ");
            request = Request.aClass(Class.forName(classAndMethod[0]));
        } else {
            // Both method and class
            // System.out.println("Running " + classAndMethod[1] + "... ");
            request = Request.method(Class.forName(classAndMethod[0]),
                classAndMethod[1]);
        }

        JUnitCore core = new JUnitCore();
        TestListener listener = new TestListener();
        core.addListener(listener);
        Result result = core.run(request);
        if (result.wasSuccessful()) {
            System.out.println("Success");
            System.exit(0);
        } else {
            System.err.println("Test failed");
            for (int i = 0; i < result.getFailures().size(); i++) {
                System.out.println("\t" + result.getFailures().get(i));
                StackTraceElement[] stackTrace = result.getFailures().get(i).getException().getStackTrace();
                for (int j = 0; j < stackTrace.length; j++) {
                    String classname = stackTrace[j].getClassName();
                    if (classname.equals(classAndMethod[0])) {
                        System.out.println("\t" + stackTrace[j].getFileName() +
                            ", line " + stackTrace[j].getLineNumber());
                    }
                }
            }
            System.exit(1);
        }
    }
}
