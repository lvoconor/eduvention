package edu.stanford.eduvention.metrics;

import java.util.ArrayList;

import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;

public class MetricManager {
	public Object[] get() {
		IProject[] projects = ResourcesPlugin.getWorkspace().getRoot().getProjects();
		ArrayList<AlertFile> files = new ArrayList<AlertFile>();
		for (IProject project: projects) {
			files.addAll(processContainer(project));
		}
		
		ArrayList<String> alerts = new ArrayList<String>();
		
		for (AlertFile code: files) {
			if (code.name.endsWith(".java")) {
				/* Alert 1: check for 'a' */
				IMetric simpleEngine = new SimpleEngine();
				String warning = simpleEngine.getAlert(code.contents);
				if (warning != null) {
					alerts.add(code.name + ": " + warning);
				}
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
		try {
			byte[] buf = new byte[8192];
			file.getContents().read(buf);
			AlertFile aFile = new AlertFile();
			aFile.name = file.getName();
			aFile.contents = new String(buf);
			return aFile;
		}
		catch (Exception e) {
			return null;
		}
	}
}
