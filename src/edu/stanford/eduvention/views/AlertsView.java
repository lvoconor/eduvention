package edu.stanford.eduvention.views;

import java.util.HashMap;

import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.part.*;
import org.eclipse.ui.texteditor.MarkerUtilities;
import org.eclipse.core.resources.IMarker;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IResourceChangeEvent;
import org.eclipse.core.resources.IResourceChangeListener;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.jface.viewers.*;
import org.eclipse.swt.graphics.Image;
import org.eclipse.jface.action.*;
import org.eclipse.ui.*;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.SWT;

import edu.stanford.eduvention.AlertFile;
import edu.stanford.eduvention.DataManager;
import edu.stanford.eduvention.metrics.*;

/**
 * This sample class demonstrates how to plug-in a new
 * workbench view. The view shows data obtained from the
 * model. The sample creates a dummy model on the fly,
 * but a real implementation would connect to the model
 * available either in this or another plug-in (e.g. the workspace).
 * The view is connected to the model using a content provider.
 * <p>
 * The view uses a label provider to define how model
 * objects should be presented in the view. Each
 * view can present the same model objects using
 * different labels and icons, if needed. Alternatively,
 * a single label provider can be shared between views
 * in order to ensure that objects of the same type are
 * presented in the same way everywhere.
 * <p>
 */

public class AlertsView extends ViewPart implements IResourceChangeListener {

	/**
	 * The ID of the view as specified by the extension.
	 */
	public static final String ID = "edu.stanford.eduvention.views.AlertsView";
	
	public static final int MIN_CHANGE_INTERVAL = 5000;
	
	private TableViewer viewer;
	private Action openPrefs;
	private PrefsView prefs;
	private Action openQuestion;
	private QuestionView question;
	private DataManager dataManager;
	private long lastUpdate;

	/*
	 * The content provider class is responsible for
	 * providing objects to the view. It can wrap
	 * existing objects in adapters or simply return
	 * objects as-is. These objects may be sensitive
	 * to the current input of the view, or ignore
	 * it and always show the same content 
	 * (like Task List, for example).
	 */
	
	class ViewContentProvider implements IStructuredContentProvider {
		public void inputChanged(Viewer v, Object oldInput, Object newInput) {
		}
		public void dispose() {
		}
		public Object[] getElements(Object parent) {
			return MetricManager.get();
		}
	}
	class ViewLabelProvider extends LabelProvider implements ITableLabelProvider {
		public String getColumnText(Object obj, int index) {
			return getText(obj);
		}
		public Image getColumnImage(Object obj, int index) {
			return getImage(obj);
		}
		public Image getImage(Object obj) {
			return PlatformUI.getWorkbench().
					getSharedImages().getImage(ISharedImages.IMG_OBJ_ELEMENT);
		}
	}
	class NameSorter extends ViewerSorter {
	}

	/**
	 * The constructor.
	 */
	public AlertsView() {
		prefs = new PrefsView(PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell());
		question = new QuestionView(PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell());
		dataManager = new DataManager();
		lastUpdate = 0;
		ResourcesPlugin.getWorkspace().addResourceChangeListener(this, IResourceChangeEvent.POST_CHANGE);
	}

	/**
	 * This is a callback that will allow us
	 * to create the viewer and initialize it.
	 */
	public void createPartControl(Composite parent) {
		viewer = new TableViewer(parent, SWT.MULTI | SWT.H_SCROLL | SWT.V_SCROLL);
		viewer.setContentProvider(new ViewContentProvider());
		viewer.setLabelProvider(new ViewLabelProvider());
		viewer.setSorter(new NameSorter());
		viewer.setInput(getViewSite());

		// Create the help context id for the viewer's control
		PlatformUI.getWorkbench().getHelpSystem().setHelp(viewer.getControl(), "edu.stanford.eduvention.viewer");
		makeActions();
		hookContextMenu();
		contributeToActionBars();
	}

	private void hookContextMenu() {
		MenuManager menuMgr = new MenuManager("#PopupMenu");
		menuMgr.setRemoveAllWhenShown(true);
		menuMgr.addMenuListener(new IMenuListener() {
			public void menuAboutToShow(IMenuManager manager) {
				AlertsView.this.fillContextMenu(manager);
			}
		});
		Menu menu = menuMgr.createContextMenu(viewer.getControl());
		viewer.getControl().setMenu(menu);
		getSite().registerContextMenu(menuMgr, viewer);
	}

	private void contributeToActionBars() {
		IActionBars bars = getViewSite().getActionBars();
		fillLocalPullDown(bars.getMenuManager());
		fillLocalToolBar(bars.getToolBarManager());
	}

	private void fillLocalPullDown(IMenuManager manager) {
		manager.add(openPrefs);
		manager.add(openQuestion);
	}

	private void fillContextMenu(IMenuManager manager) {
		manager.add(openPrefs);
		manager.add(openQuestion);
		// Other plug-ins can contribute there actions here
		manager.add(new Separator(IWorkbenchActionConstants.MB_ADDITIONS));
	}
	
	private void fillLocalToolBar(IToolBarManager manager) {
		manager.add(openPrefs);
		manager.add(openQuestion);
	}

	private void makeActions() {
		openPrefs = new Action() {
			public void run() {
				prefs.open();
			}
		};
		openPrefs.setText("Preferences");
		openPrefs.setToolTipText("Open preferences");
		openPrefs.setImageDescriptor(PlatformUI.getWorkbench().getSharedImages().
			getImageDescriptor(ISharedImages.IMG_OBJS_INFO_TSK));
		openQuestion = new Action() {
			public void run() {
				question.open();
			}
		};
		openQuestion.setText("Ask a Question");
		openQuestion.setToolTipText("Ask a Question");
		openQuestion.setImageDescriptor(PlatformUI.getWorkbench().getSharedImages().
			getImageDescriptor(ISharedImages.IMG_LCL_LINKTO_HELP));
	}

	/**
	 * Passing the focus request to the viewer's control.
	 */
	public void setFocus() {
		viewer.getControl().setFocus();
	}
	
	private void updateMarkers() {
		try {
			ResourcesPlugin.getWorkspace().getRoot().deleteMarkers("edu.stanford.eduvention.marker", true, IResource.DEPTH_INFINITE);
		} catch (CoreException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		for (AlertFile aFile : MetricManager.getAlertFiles()) {
			if (aFile.alerts != null) {
				for (Alert alert : aFile.alerts) {
					if (alert.lineNumber > 0) {
						HashMap map = new HashMap();
					   	MarkerUtilities.setLineNumber(map, alert.lineNumber);
					   	MarkerUtilities.setMessage(map, alert.getWarning());
					   	try {
						   MarkerUtilities.createMarker(aFile.file, map, "edu.stanford.eduvention.marker");
						} catch (CoreException e) {
							e.printStackTrace();
							System.out.println("Failed to add marker.");
						}
					}
				}
			}
		}
	}

	@Override
	public void resourceChanged(IResourceChangeEvent event) {
		if (lastUpdate > System.currentTimeMillis() - MIN_CHANGE_INTERVAL)
			return;
		lastUpdate = System.currentTimeMillis();
		new Thread(new Runnable() {
	    	public void run() {
    			MetricManager.update();
    			Display.getDefault().asyncExec(new Runnable() {
    	               public void run() {
    	                  viewer.refresh();
   	                      updateMarkers();
    	               }
    			});
    			new Thread(new Runnable() {
    		    	public void run() {
    		    		for(AlertFile a: MetricManager.getAlertFiles()) {
    		    			if(a.isValid){
    		    				dataManager.postSnapshot(a);
    		    			}
    					}
    	    		}
    		    }).start();
	    	}
	    }).start();
	}
}