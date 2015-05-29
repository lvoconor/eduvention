package edu.stanford.eduvention.views;

import org.eclipse.core.runtime.preferences.IEclipsePreferences;
import org.eclipse.core.runtime.preferences.InstanceScope;
import org.eclipse.jface.dialogs.IMessageProvider;
import org.eclipse.jface.dialogs.TitleAreaDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.osgi.service.prefs.BackingStoreException;

/* Sources: 
 * 1. http://www.vogella.com/tutorials/EclipseDialogs/article.html#tutorialjface_userdefined
 * 2. http://help.eclipse.org/luna/index.jsp?topic=%2Forg.eclipse.platform.doc.isv%2Freference%2Fapi%2Forg%2Feclipse%2Fjface%2Fdialogs%2FTitleAreaDialog.html
 * 3. http://stackoverflow.com/questions/443245/how-do-i-get-the-workbench-window-to-open-a-modal-dialog-in-an-eclipse-based-pro
 * 4. http://www.vogella.com/tutorials/EclipsePreferences/article.html
 * 5. http://wiki.eclipse.org/FAQ_How_do_I_load_and_save_plug-in_preferences%3F
 * 6. http://stackoverflow.com/questions/4788315/how-to-store-eclipse-plug-in-state-between-sessions */
public class PrefsDialog extends TitleAreaDialog {

  private Text sunetTxt;
  private String sunet;

  public PrefsDialog(Shell parentShell) {
    super(parentShell);
    loadSettings();
  }

  @Override
  public void create() {
    super.create();
    setTitle("Eduvention Preferences");
    setMessage("This information is used to let your instructors identify you.", IMessageProvider.INFORMATION);
  }

  @Override
  protected Control createDialogArea(Composite parent) {
    Composite area = (Composite) super.createDialogArea(parent);
    Composite container = new Composite(area, SWT.NONE);
    container.setLayoutData(new GridData(GridData.FILL_BOTH));
    GridLayout layout = new GridLayout(2, false);
    container.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
    container.setLayout(layout);

    GridData sunetD = createField(container, "SUNet ID", sunetTxt);
    
    sunetTxt = new Text(container, SWT.BORDER);
    sunetTxt.setLayoutData(sunetD);
    
    if (sunet != null)
    	sunetTxt.setText(sunet);
    
    return area;
  }

  private GridData createField(Composite container, String label, Text data) {
    Label lbt = new Label(container, SWT.NONE);
    lbt.setText(label);

    GridData gData = new GridData();
    gData.grabExcessHorizontalSpace = true;
    gData.horizontalAlignment = GridData.FILL;
    return gData;
  }

  @Override
  protected boolean isResizable() {
    return true;
  }

  // save content of the Text fields because they get disposed
  // as soon as the Dialog closes
  private void saveInput() {
	this.sunet = sunetTxt.getText();
  }

  @Override
  protected void okPressed() {
    saveInput();
    saveSettings();
    super.okPressed();
  }

  public String getSUNet() {
    return sunet;
  }
  
  private void saveSettings() {
	  // TODO: remove name, place in constant
	  IEclipsePreferences prefs = InstanceScope.INSTANCE.getNode("edu.stanford.eduvention");

	  prefs.put("sunet", this.sunet);
	  try {
		prefs.flush();
	  } catch (BackingStoreException e) {
	  }
	}

	private void loadSettings() {
	  IEclipsePreferences prefs = InstanceScope.INSTANCE.getNode("edu.stanford.eduvention");
	  try {
		prefs.sync();
	  } catch (BackingStoreException e) {
	  }
	  this.sunet = prefs.get("sunet", "forgot");
	}
}
