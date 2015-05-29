package edu.stanford.eduvention.views;

import org.eclipse.jface.dialogs.IMessageProvider;
import org.eclipse.jface.dialogs.TitleAreaDialog;
import org.eclipse.jface.text.ITextSelection;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IFileEditorInput;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.texteditor.ITextEditor;

import edu.stanford.eduvention.NetworkManager;

/* Sources: 
 * 1. http://www.vogella.com/tutorials/EclipseDialogs/article.html#tutorialjface_userdefined
 * 2. http://help.eclipse.org/luna/index.jsp?topic=%2Forg.eclipse.platform.doc.isv%2Freference%2Fapi%2Forg%2Feclipse%2Fjface%2Fdialogs%2FTitleAreaDialog.html
 * 3. http://stackoverflow.com/questions/443245/how-do-i-get-the-workbench-window-to-open-a-modal-dialog-in-an-eclipse-based-pro
 * 4. http://www.vogella.com/tutorials/EclipsePreferences/article.html
 * 5. http://wiki.eclipse.org/FAQ_How_do_I_load_and_save_plug-in_preferences%3F
 * 6. http://stackoverflow.com/questions/4788315/how-to-store-eclipse-plug-in-state-between-sessions
 * 7. http://www.java2s.com/Tutorial/Java/0280__SWT/Createamultiplelinetextfield.htm */
public class QuestionDialog extends TitleAreaDialog {

	private Text questionTxt;
	private NetworkManager networkManager;

	public QuestionDialog(Shell parentShell) {
		super(parentShell);
		networkManager = new NetworkManager();
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
	
	private String getSelectedFileName() {
		// Reference: https://wiki.eclipse.org/FAQ_How_do_I_access_the_active_project%3F
		IEditorInput input = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().getActiveEditor().getEditorInput();
	    if (!(input instanceof IFileEditorInput))
	    	return null;
	    return ((IFileEditorInput)input).getFile().getName();
	}
	
	private int getSelectedLineNumber() {
		// Reference: http://stackoverflow.com/questions/2395928/grab-selected-text-from-eclipse-java-editor
		IEditorPart part =
		PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().getActiveEditor();

		if(!(part instanceof ITextEditor)) {
			return 1;
		}
	    ITextEditor editor = (ITextEditor)part;
        ISelection selection = editor.getSelectionProvider().getSelection();
        int line = ((ITextSelection)selection).getStartLine();
        return (line == -1) ? 1 : line;
	}
	
	private void sendQuestion(final String question) {
		final String filename = getSelectedFileName();
		final int line_number = getSelectedLineNumber();
		new Thread(new Runnable() {
	    	public void run() {
	    		networkManager.postQuestion(question, filename, line_number);
	    	}
		}).start();
	}
}
