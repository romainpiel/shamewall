package core.git;

import model.User;
import model.UserScore;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.blame.BlameResult;
import org.eclipse.jgit.lib.PersonIdent;
import org.joda.time.DateTime;

import java.io.File;
import java.io.IOException;
import java.nio.file.DirectoryNotEmptyException;
import java.nio.file.NotDirectoryException;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Created by romainpiel on 27/09/2014.
 */
public class GitHandler {

    private final Git git;
    private final File rootDirectory;

    public GitHandler(Git git, File rootDirectory) {
        this.git = git;
        this.rootDirectory = rootDirectory;
    }

    public GitHandler(File rootDirectory) throws IOException {
        this.git = Git.open(rootDirectory);
        this.rootDirectory = rootDirectory;
    }

    public GitHandler(String uri, File rootDirectory) throws GitAPIException, DirectoryNotEmptyException, NotDirectoryException {
        this.git = Git.cloneRepository()
                .setURI(uri)
                .setDirectory(rootDirectory)
                .call();
        this.rootDirectory = rootDirectory;
    }

    public Git git() {
        return git;
    }

    public File getRootDirectory() {
        return rootDirectory;
    }

    public Map<String, UserScore> blameLines(String filePath, List<Integer> lineNumbers, DateTime fromTimeDate) throws GitAPIException {

        BlameResult blameResult = git.blame()
                .setFilePath(filePath)
                .call();

        return lineNumbers.stream()
                .map(blameResult::getSourceCommit)
                .filter(revCommit -> {
                    if (fromTimeDate == null) {
                        // ignore
                        return true;
                    } else {
                        DateTime commitTimeDate = new DateTime((long) revCommit.getCommitTime() * 1000l);
                        return fromTimeDate.isBefore(commitTimeDate);
                    }
                })
                .collect(
                        Collectors.groupingBy(
                                commit -> commit.getCommitterIdent().getEmailAddress(),
                                Collectors.reducing(
                                        new UserScore(null),
                                        commit -> {
                                            PersonIdent committer = commit.getCommitterIdent();
                                            User user = new User(committer.getName(), committer.getEmailAddress());
                                            return new UserScore(user);
                                        },
                                        (UserScore u1, UserScore u2) -> {
                                            UserScore u = u1.getUser() == null ? u2 : u1;
                                            u.incrementScore();
                                            return u;
                                        })
                        )
                );
    }


}
