package core.git;

import model.User;
import model.UserScore;
import org.apache.commons.io.FileUtils;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.junit.RepositoryTestCase;
import org.junit.After;
import org.junit.Test;
import play.Application;
import play.Play;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.Map;

import static org.fest.assertions.Assertions.assertThat;
import static org.fest.assertions.Fail.fail;
import static play.test.Helpers.fakeApplication;
import static play.test.Helpers.running;

/**
 * Created by romainpiel on 30/09/2014.
 */
public class GitHandlerTest extends RepositoryTestCase {

    private static final String TMP = "/tmp";
    private static final String REPO = "/repo";
    private static final String FILE_PATH = "file_romain.txt";

    private static String join(String... lines) {
        StringBuilder joined = new StringBuilder();
        for (String line : lines)
            joined.append(line).append('\n');
        return joined.toString();
    }

    @Override
    @After
    public void tearDown() throws Exception {
        super.tearDown();

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

    private Git addAndCommit(String name, String email) throws GitAPIException, IOException {
        Git git = new Git(db);

        String[] content = new String[]{"first", "second", "third"};

        writeTrashFile(FILE_PATH, join(content));
        git.add().addFilepattern(FILE_PATH).call();
        git.commit()
                .setCommitter(name, email)
                .setMessage("create file")
                .call();

        return git;
    }

    @Test
    public void testCloneAndOpen() {
        running(fakeApplication(), () -> {

            Application app = Play.application();

            File localDir = new File(app.path().getPath() + TMP + REPO);
            try {

                GitHandler gitHandler = new GitHandler("https://github.com/RomainPiel/Shimmer-android", localDir);

                assertThat(gitHandler.git()).isNotNull();
                assertThat(localDir.list()).isNotEmpty();

                gitHandler = new GitHandler(localDir);

                assertThat(gitHandler.git()).isNotNull();

            } catch (GitAPIException | IOException e) {
                fail(e.getMessage(), e);
            }
        });

    }

    @Test
    public void testNoLine() throws GitAPIException, IOException {

        String name = "Romain Piel";
        String email = "contact@romainpiel.com";

        Git git = addAndCommit(name, email);
        GitHandler gitHandler = new GitHandler(git, null);

        Map<String, UserScore> map = gitHandler.blameLines(FILE_PATH, Arrays.asList(), null);
        assertThat(map).hasSize(0);
    }

    @Test
    public void testSingleLine() throws GitAPIException, IOException {

        String name = "Romain Piel";
        String email = "contact@romainpiel.com";

        Git git = addAndCommit(name, email);
        GitHandler gitHandler = new GitHandler(git, null);

        Map<String, UserScore> map = gitHandler.blameLines(FILE_PATH, Arrays.asList(0), null);
        assertThat(map).hasSize(1);

        UserScore userScore = map.get(email);
        assertThat(userScore).isNotNull();
        assertThat(userScore.getScore()).isEqualTo(1);

        User user = userScore.getUser();
        assertThat(user).isNotNull();
        assertThat(user.getName()).isEqualTo(name);
        assertThat(user.getEmailAddress()).isEqualTo(email);
    }

    @Test
    public void testDoubleLine() throws GitAPIException, IOException {

        String name = "Romain Piel";
        String email = "contact@romainpiel.com";

        Git git = addAndCommit(name, email);
        GitHandler gitHandler = new GitHandler(git, null);

        Map<String, UserScore> map = gitHandler.blameLines(FILE_PATH, Arrays.asList(0, 1), null);
        assertThat(map).hasSize(1);

        UserScore userScore = map.get(email);
        assertThat(userScore).isNotNull();
        assertThat(userScore.getScore()).isEqualTo(2);

        User user = userScore.getUser();
        assertThat(user).isNotNull();
        assertThat(user.getName()).isEqualTo(name);
        assertThat(user.getEmailAddress()).isEqualTo(email);
    }
}
