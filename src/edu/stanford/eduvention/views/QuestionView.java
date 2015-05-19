package edu.stanford.eduvention.views;

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

import edu.stanford.eduvention.DataManager;

/* Sources: 
 * 1. http://www.vogella.com/tutorials/EclipseDialogs/article.html#tutorialjface_userdefined
 * 2. http://help.eclipse.org/luna/index.jsp?topic=%2Forg.eclipse.platform.doc.isv%2Freference%2Fapi%2Forg%2Feclipse%2Fjface%2Fdialogs%2FTitleAreaDialog.html
 * 3. http://stackoverflow.com/questions/443245/how-do-i-get-the-workbench-window-to-open-a-modal-dialog-in-an-eclipse-based-pro
 * 4. http://www.vogella.com/tutorials/EclipsePreferences/article.html
 * 5. http://wiki.eclipse.org/FAQ_How_do_I_load_and_save_plug-in_preferences%3F
 * 6. http://stackoverflow.com/questions/4788315/how-to-store-eclipse-plug-in-state-between-sessions
 * 7. http://www.java2s.com/Tutorial/Java/0280__SWT/Createamultiplelinetextfield.htm */
public class QuestionView extends TitleAreaDialog {

	private Text questionTxt;
	private DataManager dataManager;

	public QuestionView(Shell parentShell) {
		super(parentShell);
		dataManager = new DataManager();
	}

	@Override
	public void create() {
		super.create();
		setTitle("Ask a Question");
		setMessage(
				"Ask your instructor a question.",
				IMessageProvider.INFORMATION);
	}

	@Override
	protected Control createDialogArea(Composite parent) {
		Composite area = (Composite) super.createDialogArea(parent);
		Composite container = new Composite(area, SWT.NONE);
		container.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
		GridLayout layout = new GridLayout();
		container.setLayout(layout);

		GridData questionD = createField(container, "Question", questionTxt);

		questionTxt = new Text(container, SWT.MULTI | SWT.BORDER | SWT.WRAP | SWT.V_SCROLL);
		questionTxt.setLayoutData(questionD);

		return area;
	}

	private GridData createField(Composite container, String label, Text data) {
		Label lbt = new Label(container, SWT.NONE);
		lbt.setText(label);

		GridData gData = new GridData(SWT.FILL, SWT.FILL, true, true);
		return gData;
	}

	@Override
	protected boolean isResizable() {
		return true;
	}

	@Override
	protected void okPressed() {
		sendQuestion(questionTxt.getText());
		super.okPressed();
	}

	private void sendQuestion(final String question) {
		new Thread(new Runnable() {
	    	public void run() {
	    		dataManager.postQuestion(question);
	    	}
		}).start();
	}
}
