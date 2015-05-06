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
import edu.stanford.eduvention.views.Alert;

public class MetricManager {
	/*
	 * Converts all the files in the workspace to AlertFile objects
	 * TODO Add functionality to select only relevant projects in workspace
	 */
	public ArrayList<AlertFile> getAlertFiles(){
		IProject[] projects = ResourcesPlugin.getWorkspace().getRoot().getProjects();
		ArrayList<AlertFile> files = new ArrayList<AlertFile>();
		for (IProject project: projects) {
			files.addAll(processContainer(project));
		}
		//remove non-code files 
		ArrayList<AlertFile> filteredFiles = filterNonCode(files);
		
		Alert toAdd;		
		for (AlertFile file: filteredFiles) {
				/************************************************
				 * 												*
				 * 				ALERTS GO HERE					*
				 * 												*
				 ************************************************/

				/* Alert 1: check for 'a' */
				toAdd = new SimpleMetric().getAlert(file);
				if (toAdd != null)
					file.alerts.add(toAdd);
					
				/* Alert 2: check comment ratio */
				toAdd = new CommentMetric().getAlert(file);
				if (toAdd != null)
					file.alerts.add(toAdd);
				
				/* Alert 3: check average method size */
				toAdd = new DecompMetric().getAlert(file);
				if (toAdd != null)
					file.alerts.add(toAdd);
				
		}
		return filteredFiles;

	}
	/*
	 * Returns a list of warning Strings 
	 */
	public Object[] get() {
		ArrayList<String> alertStrings = new ArrayList<String>();
		
		ArrayList<AlertFile> files = getAlertFiles();
		for (AlertFile file: files) {
			for( Alert a: file.alerts){
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
	/*
	 * Converts an IFile into an AlertFile
	 */
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
	
	private Alert addAlert(IMetric metric, AlertFile code) {
		Alert a = metric.getAlert(code);
		return a;
	}
	/*
	 * Removes files that don't end with ".java"
	 */
	private ArrayList<AlertFile> filterNonCode(ArrayList<AlertFile> files){
		ArrayList<AlertFile> filtered = new ArrayList<AlertFile>();
		for(AlertFile f: files){
			if(f != null && f.name.endsWith(".java")){
				filtered.add(f); 
			}
		}
		return filtered;
	}
}
