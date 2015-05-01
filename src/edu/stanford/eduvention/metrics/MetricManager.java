package edu.stanford.eduvention.metrics;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;

import edu.stanford.eduvention.AlertFile;
import edu.stanford.eduvention.DataManager;

public class MetricManager {
	public Object[] get() {
		DataManager dm = new DataManager();
		dm.main(null);
		IProject[] projects = ResourcesPlugin.getWorkspace().getRoot().getProjects();
		ArrayList<AlertFile> files = new ArrayList<AlertFile>();
		for (IProject project: projects) {
			files.addAll(processContainer(project));
		}
		
		ArrayList<String> alerts = new ArrayList<String>();
		
		for (AlertFile code: files) {
			if (code.name.endsWith(".java")) {			
				/************************************************
				 * 												*
				 * 				ALERTS GO HERE					*
				 * 												*
				 ************************************************/

				/* Alert 1: check for 'a' */
				addAlert(new SimpleMetric(), alerts, code);

				/* Alert 2: check comment ratio */
				addAlert(new CommentMetric(), alerts, code);
				
				/* Alert 3: check average method size */
				addAlert(new DecompMetric(), alerts, code);
			}
		}

		/* Add note if there are no alerts */
		if (alerts.size() == 0) {
			alerts.add("No alerts!");
		}

		/* Convert ArrayList to array and return. */
		String[] ret = new String[alerts.size()];
		alerts.toArray(ret);
		return ret;
	}
	
	/* Adapted from http://stackoverflow.com/questions/20744012/
	 * recursively-list-all-files-in-eclipse-workspace-programmatically */
	private ArrayList<AlertFile> processContainer(IContainer container)
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
	
	private AlertFile processFile(IFile file)
	{
		InputStream is = null;
		java.util.Scanner s = null;
		try {
			is = file.getContents();
		}
		catch (CoreException e) {
			return null;
		}
		if (is != null) {
			s = new java.util.Scanner(is).useDelimiter("\\A");
		}
	    String contents = s.hasNext() ? s.next() : "";
		AlertFile aFile = new AlertFile();
		aFile.name = file.getName();
		aFile.contents = contents;
		aFile.lines = getNumLines(contents);
		try {
			is.close();
		} catch (IOException e) {
		}
		s.close();
		return aFile;
	}
	
	private Integer getNumLines(String code) {
		Matcher m = Pattern.compile("(\n)|(\r)|(\r\n)").matcher(code);
		Integer lines = 1;
		while (m.find()) {
		    lines ++;
		}
		return lines;
	}
	
	private void addAlert(IMetric metric, ArrayList<String> alerts, AlertFile code) {
		String warning = metric.getAlert(code);
		if (warning != null) {
			alerts.add(code.name + ": " + warning);
		}
	}
}
