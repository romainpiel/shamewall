package core.checkstyle;

import com.puppycrawl.tools.checkstyle.api.AuditEvent;
import com.puppycrawl.tools.checkstyle.api.AuditListener;
import com.puppycrawl.tools.checkstyle.api.AutomaticBean;

/**
 * Created by romainpiel on 04/10/2014.
 */
public abstract class CheckstyleListener extends AutomaticBean implements AuditListener {

    @Override
    public void auditStarted(AuditEvent auditEvent) {

    }

    @Override
    public void auditFinished(AuditEvent auditEvent) {

    }

    @Override
    public void fileStarted(AuditEvent auditEvent) {

    }

    @Override
    public void fileFinished(AuditEvent auditEvent) {

    }

    @Override
    public void addError(AuditEvent auditEvent) {

    }

    @Override
    public void addException(AuditEvent auditEvent, Throwable throwable) {

    }
}
