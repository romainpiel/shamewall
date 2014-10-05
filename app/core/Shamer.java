package core;

import com.puppycrawl.tools.checkstyle.api.CheckstyleException;
import core.checkstyle.CheckstyleHandler;
import core.git.GitHandler;
import model.UserScore;
import org.apache.commons.io.FileUtils;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.joda.time.DateTime;
import org.xml.sax.InputSource;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;

/**
 * Created by romainpiel on 05/10/2014.
 */
public class Shamer {

    private ClassLoader classLoader;
    private File checkstyleTemplateFile;
    private File repoLocalDir;
    private String repoRemoteUrl;
    private DateTime fromTimeDate;

    public Shamer(ClassLoader classLoader, File checkstyleTemplateFile, File repoLocalDir, String repoRemoteUrl, DateTime fromTimeDate) {
        this.classLoader = classLoader;
        this.checkstyleTemplateFile = checkstyleTemplateFile;
        this.repoLocalDir = repoLocalDir;
        this.repoRemoteUrl = repoRemoteUrl;
        this.fromTimeDate = fromTimeDate;
    }

    public Map<String, UserScore> process() throws IOException, GitAPIException, CheckstyleException {

        GitHandler gitHandler;
        if (!repoLocalDir.exists() || repoLocalDir.list().length == 0) {
            gitHandler = new GitHandler(repoRemoteUrl, repoLocalDir);
        } else {
            gitHandler = new GitHandler(repoLocalDir);
        }

        Collection<File> files = FileUtils.listFiles(repoLocalDir, new String[]{"java"}, true);

        BlamerCheckstyleListener listener = new BlamerCheckstyleListener(gitHandler, fromTimeDate);

        CheckstyleHandler checkstyleHandler = new CheckstyleHandler();
        checkstyleHandler.check(
                classLoader,
                new InputSource(new FileInputStream(checkstyleTemplateFile)),
                new ArrayList<>(files),
                listener);

        return listener.getScores();
    }
}
