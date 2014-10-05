package core;

import com.puppycrawl.tools.checkstyle.api.AuditEvent;
import core.checkstyle.CheckstyleListener;
import core.git.GitHandler;
import model.UserScore;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.joda.time.DateTime;

import java.io.File;
import java.net.URI;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Created by romainpiel on 04/10/2014.
 */
public class BlamerCheckstyleListener extends CheckstyleListener {

    private List<AuditEvent> currentFileEvents;
    private GitHandler gitHandler;
    private DateTime fromTimeDate;
    private Map<String, UserScore> scores;

    public BlamerCheckstyleListener(GitHandler gitHandler, DateTime fromTimeDate) {
        this.currentFileEvents = new ArrayList<>();
        this.gitHandler = gitHandler;
        this.fromTimeDate = fromTimeDate;
        this.scores = new HashMap<>();
    }

    public Map<String, UserScore> getScores() {
        return scores;
    }

    @Override
    public void fileStarted(AuditEvent auditEvent) {
        super.fileStarted(auditEvent);

        currentFileEvents.clear();
    }

    @Override
    public void fileFinished(AuditEvent auditEvent) {
        super.fileFinished(auditEvent);

        List<Integer> lines = currentFileEvents.stream()
                .map(AuditEvent::getLine)
                .collect(Collectors.toList());

        try {
            File gitRootDir = gitHandler.getRootDirectory();
            URI path = gitRootDir.toURI().relativize(new File(auditEvent.getFileName()).toURI());

            Map<String, UserScore> fileScores = gitHandler.blameLines(path.getPath(), lines, fromTimeDate);
            for (String email : fileScores.keySet()) {
                scores.merge(email, fileScores.get(email), (userScore, userScore2) -> {
                    if (userScore != null) {
                        userScore.incrementScore(userScore2.getScore());
                        return userScore;
                    } else {
                        return userScore2;
                    }
                });
            }
        } catch (GitAPIException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void addError(AuditEvent auditEvent) {
        super.addError(auditEvent);

        currentFileEvents.add(auditEvent);
    }

    @Override
    public void addException(AuditEvent auditEvent, Throwable throwable) {
        super.addException(auditEvent, throwable);

        currentFileEvents.add(auditEvent);
    }
}
