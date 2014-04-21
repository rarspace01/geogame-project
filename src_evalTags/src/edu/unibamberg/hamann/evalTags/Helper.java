package edu.unibamberg.hamann.evalTags;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.lang.management.ManagementFactory;
import java.lang.management.OperatingSystemMXBean;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

/**
 * Old Helper Class from 2010 based on: {@link https://code.google.com/p/snmptrack/source/browse/trunk/src/org/dh/usertrack/HelperClass.java}
 * 
 * @author denis
 * 
 */
public class Helper {

	public static boolean isVerbose = true;
	public static String sWorkpath = "";
	private static String FILENAME = "eval_log.txt";

	/**
	 * returns the logical CPU count
	 * @return
	 */
	public static int getCPUCount() {

		OperatingSystemMXBean osBean = ManagementFactory
				.getOperatingSystemMXBean();

		int numOfProcessors = osBean.getAvailableProcessors();

		return numOfProcessors;

	}

	public static void sleeping(int i) {
		try {
			Thread.sleep(i * 1000);
		} catch (InterruptedException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
	}

	// Fehlermeldung ausgeben, falls Logdatei schreiben fehlerhaft war
	public static void err(String tag, Throwable e) {

		String retValue = null;
		StringWriter sw = null;
		PrintWriter pw = null;
		try {
			sw = new StringWriter();
			pw = new PrintWriter(sw);
			e.printStackTrace(pw);
			retValue = sw.toString();
		} finally {
			try {
				if (pw != null)
					pw.close();
				if (sw != null)
					sw.close();
			} catch (IOException ignore) {
			}
		}
		msgLog(tag, retValue);

	}

	// Schreibt eine Log-Datei f�r Revisionszwecke in Datei agent.log
	public static void msgLog(String tag, String sLog) {
		if (isVerbose)
			System.out.println(printClock() + sLog);
		BufferedWriter out;
		try {
			out = new BufferedWriter(new FileWriter(sWorkpath + FILENAME, true));
			out.write(printClock() + sLog
					+ System.getProperty("line.separator"));
			out.close();
		} catch (IOException e) {
			e.printStackTrace();
			// System.exit(1);
		}
	}

	public static void msgLog(String tag, String sFilename, String sLog) {
		if (isVerbose)
			System.out.println(printClock() + sLog);
		BufferedWriter out;
		try {
			out = new BufferedWriter(
					new FileWriter(sWorkpath + sFilename, true));
			out.write(printClock() + "[" + tag + "]" + sLog
					+ System.getProperty("line.separator"));
			out.close();
		} catch (IOException e) {
			e.printStackTrace();
			// System.exit(1);
		}
	}

	// liefert aktuelle Uhrzeit als String zurück
	public static String printClock() {

		SimpleDateFormat sdatef = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss",
				Locale.GERMAN);

		return sdatef.format(Calendar.getInstance().getTime());

	}

}
