package core.checkstyle;

import com.puppycrawl.tools.checkstyle.api.AuditEvent;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by romainpiel on 27/09/2014.
 */
public class CheckstyleEventRecorderListener extends CheckstyleListener {

    private List<AuditEvent> events;

    public List<AuditEvent> getEvents() {
        return events;
    }

    @Override
    public void auditStarted(AuditEvent aEvt) {
        events = new ArrayList<>();
    }

    @Override
    public void addError(AuditEvent aEvt) {
        events.add(aEvt);
    }

    @Override
    public void addException(AuditEvent aEvt, Throwable aThrowable) {
        events.add(aEvt);
    }
}
