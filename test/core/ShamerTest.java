package core;

import com.puppycrawl.tools.checkstyle.api.CheckstyleException;
import model.UserScore;
import org.apache.commons.io.FileUtils;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.joda.time.DateTime;
import org.junit.After;
import org.junit.Test;
import play.Application;
import play.Play;

import java.io.File;
import java.io.IOException;
import java.util.Map;

import static org.fest.assertions.Assertions.assertThat;
import static org.fest.assertions.Fail.fail;
import static play.test.Helpers.fakeApplication;
import static play.test.Helpers.running;

/**
 * Created by romainpiel on 05/10/2014.
 */
public class ShamerTest {

    private static final String TMP = "/tmp";
    private static final String REPO = "/repo";

    @After
    public void tearDown() throws Exception {
        running(fakeApplication(), () -> {

            Application app = Play.application();

            File localDir = new File(app.path().getPath() + TMP);
            try {
                FileUtils.deleteDirectory(localDir);
            } catch (IOException e) {
                fail(e.getMessage(), e);
            }
        });
    }

    @Test
    public void checkCommonExample() {
        running(fakeApplication(), () -> {

            Application app = Play.application();

            File localDir = new File(app.path().getPath() + TMP + REPO);
            try {

                Shamer shamer = new Shamer(
                        app.classloader(), 
                        app.getFile("resources/sun_checks.xml"),
                        localDir,
                        "https://github.com/square/okhttp.git",
                        new DateTime().minusDays(7)
                );

                Map<String, UserScore> scores = shamer.process();
                assertThat(scores).isNotEmpty();

            } catch (GitAPIException | IOException | CheckstyleException e) {
                fail(e.getMessage(), e);
            }
        });
    }
}
