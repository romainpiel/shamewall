package core.checkstyle;

import com.puppycrawl.tools.checkstyle.Checker;
import com.puppycrawl.tools.checkstyle.ConfigurationLoader;
import com.puppycrawl.tools.checkstyle.PropertiesExpander;
import com.puppycrawl.tools.checkstyle.api.AuditListener;
import com.puppycrawl.tools.checkstyle.api.CheckstyleException;
import com.puppycrawl.tools.checkstyle.api.Configuration;
import org.xml.sax.InputSource;

import java.io.File;
import java.util.List;
import java.util.Properties;

/**
 * Created by romainpiel on 27/09/2014.
 */
public class CheckstyleHandler {

    public void check(ClassLoader classLoader, InputSource configFile, List<File> files, AuditListener auditListener) throws CheckstyleException {

        Configuration configuration = ConfigurationLoader.loadConfiguration(
                configFile, new PropertiesExpander(new Properties()), true);

        Checker checker = new Checker();
        checker.addListener(auditListener);
        checker.setClassloader(classLoader);
        checker.setModuleClassLoader(classLoader);
        checker.configure(configuration);
        checker.process(files);
        checker.destroy();
    }
}
