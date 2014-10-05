package core.checkstyle;

import com.puppycrawl.tools.checkstyle.api.CheckstyleException;
import org.junit.Test;
import org.xml.sax.InputSource;
import play.Application;
import play.Play;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;

import static org.fest.assertions.Assertions.assertThat;
import static org.fest.assertions.Fail.fail;
import static play.test.Helpers.fakeApplication;
import static play.test.Helpers.running;

/**
 * Created by romainpiel on 27/09/2014.
 */
public class CheckstyleHandlerTest {

    @Test
    public void testOneFileCheck() {
        running(fakeApplication(), () -> {

            Application app = Play.application();

            File javaFile = app.getFile("resources/java/TestClass.java");
            List<File> files = new ArrayList<File>();
            files.add(javaFile);

            File sunChecksConfig = app.getFile("resources/sun_checks.xml");
            InputSource configSource = null;
            try {
                configSource = new InputSource(new FileInputStream(sunChecksConfig));
            } catch (FileNotFoundException e) {
                fail(e.getMessage(), e);
            }

            try {

                CheckstyleEventRecorderListener listener = new CheckstyleEventRecorderListener();

                CheckstyleHandler checker = new CheckstyleHandler();
                checker.check(app.classloader(), configSource, files, listener);

                assertThat(listener.getEvents()).isNotNull().hasSize(5);

            } catch (CheckstyleException e) {
                fail(e.getMessage(), e);
            }
        });
    }
}
