package edu.stanford.eduvention.metrics;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;

import edu.stanford.eduvention.AlertFile;

public class MetricManager {
	
	private static String[] alerts = new String[]{"Alerts loading."};
	private static Boolean updating = false;
	
	
	public static Object[] get() {
		return alerts;
	}

	public static void update() {
		if (updating) {
			return;
		}
		updating = true;
		IProject[] projects = ResourcesPlugin.getWorkspace().getRoot().getProjects();
		ArrayList<AlertFile> files = new ArrayList<AlertFile>();
		for (IProject project: projects) {
			files.addAll(processContainer(project));
		}
		
		ArrayList<String> newAlerts = new ArrayList<String>();
		String toAdd;
		
		for (AlertFile code: files) {
			if (code != null && code.name != null && code.name.endsWith(".java")) {
				/************************************************
				 * 												*
				 * 				ALERTS GO HERE					*
				 * 												*
				 ************************************************/

				/* Alert 1: check for 'e' */
				toAdd = addAlert(new SimpleMetric(), code);
				if (toAdd != null)
					newAlerts.add(toAdd);
				
				/* Alert 2: check comment ratio */
				toAdd = addAlert(new CommentMetric(), code);
				if (toAdd != null)
					newAlerts.add(toAdd);
				
				/* Alert 3: check average method size */
				toAdd = addAlert(new DecompMetric(), code);
				if (toAdd != null)
					newAlerts.add(toAdd);
			}
		}

		/* Add note if there are no alerts */
		if (newAlerts.size() == 0) {
			newAlerts.add("No alerts!");
		}

		/* Convert ArrayList to array and return. */
		String[] ret = new String[newAlerts.size()];
		newAlerts.toArray(ret);
		alerts = ret;
		updating = false;
	}
	
	/* Adapted from http://stackoverflow.com/questions/20744012/
	 * recursively-list-all-files-in-eclipse-workspace-programmatically */
	private static ArrayList<AlertFile> processContainer(IContainer container)
	{
		ArrayList<AlertFile> files = new ArrayList<AlertFile>();
		IResource[] members;
		try {
			members = container.members();
			for (IResource member: members) {
				if (member instanceof IContainer)
					files.addAll(processContainer((IContainer)member));
				else if (member instanceof IFile)
					files.add(processFile((IFile)member));
			}
		} catch (CoreException e) {
		}
		return files;
	}
	
	private static AlertFile processFile(IFile file)
	{
		InputStream is = null;
		java.util.Scanner s = null;
		try {
			is = file.getContents();
		}
		catch (CoreException e) {
			return null;
		}
		if (is == null) {
			return null;
		}
		s = new Scanner(is);
		s.useDelimiter("\\A");
	    String contents = s.hasNext() ? s.next() : "";
		AlertFile aFile = new AlertFile();
		aFile.name = file.getName();
		aFile.contents = contents;
		aFile.lines = getNumLines(contents);
		s.close();
		return aFile;
	}
	
	private static int getNumLines(String code) {
		Matcher m = Pattern.compile("(\n)|(\r)|(\r\n)").matcher(code);
		Integer lines = 1;
		while (m.find()) {
		    lines ++;
		}
		return lines;
	}
	
	private static String addAlert(IMetric metric, AlertFile code) {
		String warning = metric.getAlert(code);
		if (warning != null) {
			return code.name + ": " + warning;
		}
		return null;
	}
	
	public static Boolean isUpdating() {
		return updating;
	}
}
