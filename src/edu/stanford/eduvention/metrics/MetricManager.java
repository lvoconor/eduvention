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

import edu.stanford.eduvention.Alert;
import edu.stanford.eduvention.AlertFile;

public class MetricManager {
	private static ArrayList<AlertFile> alerts = new ArrayList<AlertFile>();
	
	public static void update() {
		alerts = updateAlertFiles();
	}
	
	/*
	 * Returns a list of warning Strings 
	 */
	public static Object[] get() {
		ArrayList<String> alertStrings = new ArrayList<String>();
		
		for (AlertFile file: alerts) {
			if(!file.isValid){
				alertStrings.add("file " + file.name + " does not compile. Please fix syntax errors");
				continue;
			}
			for(Alert a: file.alerts) {
				alertStrings.add(a.getWarning());
			}
		}

		/* Add note if there are no alerts */
		if (alertStrings.size() == 0) {
			alertStrings.add("No alerts!");
		}

		/* Convert ArrayList to array and return. */
		String[] ret = new String[alertStrings.size()];
		alertStrings.toArray(ret);
		return ret;
	}
	
	public static ArrayList<AlertFile> getAlertFiles() {
		return alerts;
	}
	
	/*
	 * Converts all the files in the workspace to AlertFile objects
	 * TODO Add functionality to select only relevant projects in workspace
	 */
	private static ArrayList<AlertFile> updateAlertFiles(){
		IProject[] projects = ResourcesPlugin.getWorkspace().getRoot().getProjects();
		ArrayList<AlertFile> files = new ArrayList<AlertFile>();
		for (IProject project: projects) {
			files.addAll(processContainer(project));
		}
		//remove non-code files 
		ArrayList<AlertFile> filteredFiles = filterNonCode(files);
		ArrayList<Alert> toAdd;		
		for (AlertFile file: filteredFiles) {
				/************************************************
				 * 												*
				 * 				ALERTS GO HERE					*
				 * 												*
				 ************************************************/
				if(!file.isValid){
					continue;	
				}

				/* Alert 1: check comment ratio */
				toAdd = new CommentMetric().getAlerts(file);
				if (toAdd != null)
					file.alerts.addAll(toAdd);

				/* Alert 2: find methods that are too long */
				toAdd = new DecompMetric().getAlerts(file);
				if (toAdd != null)
					file.alerts.addAll(toAdd);
					
		}
		return filteredFiles;

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
				else if (member instanceof IFile){
					AlertFile f = processFile((IFile)member);
					if(f != null){
						files.add(f);
					}
				}
			}
		} catch (CoreException e) {
		}
		return files;
	}

	/*
	 * Converts an IFile into an AlertFile
	 */
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
	    String fileName = file.getName();
		//filter out non-java files
	    if(!fileName.contains(".java")){
			return null;
		}
		AlertFile aFile = new AlertFile(file, fileName, contents);
		s.close();
		
		return aFile;
	}
	
	
	
	/*
	 * Removes files that don't end with ".java"
	 */
	private static ArrayList<AlertFile> filterNonCode(ArrayList<AlertFile> files){
		ArrayList<AlertFile> filtered = new ArrayList<AlertFile>();
		for(AlertFile f: files){
			if(f != null && f.name != null && f.name.endsWith(".java")){
				filtered.add(f); 
			}
		}
		return filtered;
	}
}
