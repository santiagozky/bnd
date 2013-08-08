package biz.aQute.resolve.internal.helpers;

import org.junit.*;
import org.osgi.framework.*;
import org.osgi.service.log.*;

@Ignore
public class NullLogService implements LogService {

	public void log(int level, String message) {}

	public void log(int level, String message, Throwable t) {}

	public void log(ServiceReference sr, int level, String message) {}

	public void log(ServiceReference sr, int level, String message, Throwable t) {}

}
