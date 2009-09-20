/*
 * 	$Id: Logger.java,v 1.1 2007/11/13 02:49:55 johnc Exp $
 * 
 * 	Programming and Problem Solving
 *  Copyright (c) 2007 The Trustees of Columbia University
 */
package explorandum;

import java.util.Date;
/**
 * A logging class. Thank you to Group 6 from Project 2 (Christos, Kishan, and 
 * Stefano) for pointing me to their Logger class, on which this class is
 * based. And thank you to Will Mee, who had the idea in project 1 (and group 6 
 * in project 2 borrowed his idea).
 * 
 * If you want to print a string or object, use the methods that take an
 * object as its parameter. If you are catching an exception and want a 
 * stack trace, then pass the Exception as the second "Throwable" object.
 * 
 * @author John Cieslewicz
 */
public final class Logger {
	/**
	 * The LogLevel enumeration contains the different levels at which the log
	 * can provide information.
	 */
	public static enum LogLevel{NONE, FATAL, ERROR, WARN, DEBUG, INFO, TRACE};
	private LogLevel myLogLevel;
	private Class myLogClass;
	/**
	 * Setup the log to log at a certain level for this class.
	 * @param level All log messages of this level and higher are shown.
	 * @param c The class that this log is for.
	 */
	public Logger(LogLevel level, Class c){
		myLogLevel = level;
		myLogClass = c;
	}
	/**
	 * Fatal error, the worst kind.
	 * @param o
	 */
	public void fatal(Object o){
		if(myLogLevel != LogLevel.NONE){
			printLogMessage(LogLevel.FATAL, o);
		}
		
	}
	/**
	 * Error, this is pretty bad.
	 * @param o
	 */
	public void error(Object o){
		if(! (myLogLevel == LogLevel.NONE || myLogLevel == LogLevel.FATAL)){
			printLogMessage(LogLevel.ERROR, o);
		}
	}
	/**
	 * Warn, you really need to know about this. Something is not right.
	 * @param o
	 */
	public void warn(Object o){
		if(! (myLogLevel == LogLevel.NONE 
				|| myLogLevel == LogLevel.FATAL
				|| myLogLevel == LogLevel.ERROR)){
			printLogMessage(LogLevel.WARN, o);
		}
	}
	/**
	 * Debug, you want to know what is happening under the hood.
	 * @param o
	 */
	public void debug(Object o){
		if(myLogLevel == LogLevel.DEBUG
				||myLogLevel == LogLevel.INFO
				|| myLogLevel == LogLevel.TRACE){
			printLogMessage(LogLevel.DEBUG, o);
		}
	}
	/**
	 * Info, just for information. Nothing is wrong.
	 * @param o
	 */
	public void info(Object o){
		if(myLogLevel == LogLevel.TRACE || myLogLevel == LogLevel.INFO){
			printLogMessage(LogLevel.INFO, o);
		}
	}
	/**
	 * Trace, less important detail.
	 * @param o
	 */
	public void trace(Object o){
		if(myLogLevel == LogLevel.TRACE){
			printLogMessage(LogLevel.TRACE, o);
		}
	}
	/*
	 * Print a log message.
	 */
	private void printLogMessage(LogLevel level, Object o) {
		Date d = new Date(System.currentTimeMillis());
		String message =  d + " -- " + myLogClass.toString();
		if(level != LogLevel.DEBUG)
			message += "\n";
		message += "[" + level + "] " + o;
		System.out.println(message);
	}
	/**
	 * Fatal error, the worst kind.
	 * @param o
	 */
	public void fatal(Object o, Throwable t){
		if(myLogLevel != LogLevel.NONE){
			printLogMessage(LogLevel.FATAL, o);
			t.printStackTrace();
		}
		
	}
	/**
	 * Error, this is pretty bad.
	 * @param o
	 */
	public void error(Object o, Throwable t){
		if(! (myLogLevel == LogLevel.NONE || myLogLevel == LogLevel.FATAL)){
			printLogMessage(LogLevel.ERROR, o);
			t.printStackTrace();
		}
	}
	/**
	 * Warn, you really need to know about this. Something is not right.
	 * @param o
	 */
	public void warn(Object o, Throwable t){
		if(! (myLogLevel == LogLevel.NONE 
				|| myLogLevel == LogLevel.FATAL
				|| myLogLevel == LogLevel.ERROR)){
			printLogMessage(LogLevel.WARN, o);
			t.printStackTrace();
		}
	}
	/**
	 * Debug, you want to know what is happening under the hood.
	 * @param o
	 */
	public void debug(Object o, Throwable t){
		if(myLogLevel == LogLevel.DEBUG
				||myLogLevel == LogLevel.INFO
				|| myLogLevel == LogLevel.TRACE){
			printLogMessage(LogLevel.DEBUG, o);
			t.printStackTrace();
		}
	}
	/**
	 * Info, just for information. Nothing is wrong.
	 * @param o
	 */
	public void info(Object o, Throwable t){
		if(myLogLevel == LogLevel.TRACE || myLogLevel == LogLevel.INFO){
			printLogMessage(LogLevel.INFO, o);
			t.printStackTrace();
		}
	}
	/**
	 * Trace, less important detail.
	 * @param o
	 */
	public void trace(Object o, Throwable t){
		if(myLogLevel == LogLevel.TRACE){
			printLogMessage(LogLevel.TRACE, o);
			t.printStackTrace();
		}
	}
}
