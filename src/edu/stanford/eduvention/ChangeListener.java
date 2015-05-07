package edu.stanford.eduvention;

import java.util.ArrayList;

import org.eclipse.core.resources.IResourceChangeEvent;
import org.eclipse.core.resources.IResourceChangeListener;
import org.eclipse.core.resources.ResourcesPlugin;

import edu.stanford.eduvention.metrics.MetricManager;

/* Sources:
 * 1. http://www.javaworld.com/article/2077462/learn-java/java-tip-10--implement-callback-routines-in-java.html
 */
public class ChangeListener implements IResourceChangeListener {

	private static final int MIN_LOCAL_WAIT = 2000;
	private static final int MIN_NETWORK_WAIT = 30000;
	
	private DataManager dataManager;
	private MetricManager metricManager;
	private IUpdate update;
	private long lastLocalUpdate;
	private long lastNetworkUpdate;
	
	public ChangeListener(IUpdate update) {
		lastLocalUpdate = 0;
		lastNetworkUpdate = 0;
		this.update = update;
		dataManager = new DataManager();
		metricManager= new MetricManager();
		ResourcesPlugin.getWorkspace().addResourceChangeListener(this, IResourceChangeEvent.POST_CHANGE);
	}
	
	@Override
	public void resourceChanged(IResourceChangeEvent event) {
		long curTime = System.currentTimeMillis();
		if (curTime - lastLocalUpdate > MIN_LOCAL_WAIT) {
			lastLocalUpdate = curTime;
			update.FileSave();
		}
		if (curTime - lastNetworkUpdate > MIN_NETWORK_WAIT) {
			lastNetworkUpdate = curTime;
			ArrayList<AlertFile> alertFiles = metricManager.getAlertFiles();
			for(AlertFile a: alertFiles){
				dataManager.postSnapshot(a);
			}
			//dataManager.main();
		}
	}
}