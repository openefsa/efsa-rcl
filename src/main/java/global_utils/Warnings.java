package global_utils;

import java.util.Collection;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Shell;

import ack.IDcfAckLog;
import ack.OpResError;
import app_config.PropertiesReader;
import converter.ExceptionConverter;
import dataset.IDataset;
import i18n_messages.Messages;
import soap.DetailedSOAPException;
import soap.SOAPError;

public class Warnings {
	
	public static Message create(String message) {
		return create(Messages.get("error.title"), message, SWT.ICON_ERROR, false);
	}
	
	public static Message create(String title, String message) {
		return create(title, message, SWT.ICON_ERROR, false);
	}
	
	public static Message create(String title, String message, int style) {
		return create(title, message, style, false);
	}
	
	public static Message createFatal(String message, IDataset... reports) {
		return createFatal(message, SWT.ICON_ERROR, reports);
	}
	
	public static Message createFatal(String message, int style, IDataset... reports) {
		Message msg = create(Messages.get("error.title"), message, style | SWT.ICON_ERROR, true);
		msg.setReports(reports);
		return msg;
	}
	
	public static Message create(String title, String message, int style, boolean fatal) {
		return new Message(title, message, style, fatal);
	}
	
	/**
	 * Warn the user with a message box with custom style
	 * @param title
	 * @param message
	 * @param icon
	 */
	public static int warnUser(Shell shell, String title, String message, int style) {
		return create(title, message, style).open(shell);
	}
	
	/**
	 * Warn the user with an ERROR message box
	 * @param title
	 * @param message
	 */
	public static int warnUser(Shell shell, String title, String message) {
		return warnUser(shell, title, message, SWT.ICON_ERROR);
	}
	
	public static String getStackTrace(Exception e) {
		String trace = ExceptionConverter.getStackTrace(e);
	    return trace;
	}
	
	public static Message getAckOperationWarning(IDataset report, IDcfAckLog log) {

		OpResError error = log.getOpResError();
		String message = null;
		String code = null;
		
		switch(error) {
		case NOT_EXISTING_DC:
			message = Messages.get("dc.not.valid", 
					log.getDCCode(),
					PropertiesReader.getSupportEmail());
			code = "ERR407";
			break;
		case USER_NOT_AUTHORIZED:
			message = Messages.get("account.unauthorized", 
					log.getDCCode(),
					PropertiesReader.getSupportEmail());
			code = "ERR101";
			break;
		default:
			Collection<String> errorMessages = log.getOpResLog();
			
			StringBuilder sb = new StringBuilder();
			for(String m: errorMessages)
				sb.append(m).append("\n");
			
			message = Messages.get("ack.general.error", 
					sb.toString(),
					PropertiesReader.getSupportEmail());
			code = "ERR502";
			break;
		}
		
		Message m = createFatal(message, report);
		m.setCode(code);
		return m;
	}
	
	public static String[] getSOAPWarning(DetailedSOAPException e) {
		
		String title = null;
		String message = null;
		String code = null;
		SOAPError error = e.getError();
		switch(error) {
		case NO_CONNECTION:
			title = Messages.get("error.title");
			message = Messages.get("no.connection");
			code = "ERR600";
			break;
		case UNAUTHORIZED:
		case FORBIDDEN:
			title = Messages.get("error.title");
			message = Messages.get("wrong.credentials");
			code = "ERR100";
			break;
		case MESSAGE_SEND_FAILED:
			title = Messages.get("error.title");
			message = Messages.get("send.message.failed");
			code = "ERR104";
			break;
		case SERVER_ERROR:
			title = Messages.get("error.title");
			message = Messages.get("internal.server.err", e.getMessage(),PropertiesReader.getSupportEmail());
			code = "ERR500";
			break;
		default:
			break;
		}
		
		return new String[] {title, message, code};
	}
	
	public static Message createSOAPWarning(DetailedSOAPException e) {
		String[] warnings = getSOAPWarning(e);
		Message m = create(warnings[0], warnings[1], SWT.ICON_ERROR);
		m.setCode(warnings[2]);
		return m;
	}
	
	
	public static void showSOAPWarning(Shell shell, DetailedSOAPException error) {
		String[] warning = Warnings.getSOAPWarning(error);
		Warnings.warnUser(shell, warning[0], warning[1]);
	}
}
