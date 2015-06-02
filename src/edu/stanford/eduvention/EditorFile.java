package edu.stanford.eduvention;
import org.eclipse.core.resources.IFile;
import org.eclipse.ui.IEditorPart;

public class EditorFile {

	private IEditorPart editorPart;
	private IFile ifile;
	private String name;
	public EditorFile(IEditorPart editorPart, IFile ifile, String name) {
		super();
		this.editorPart = editorPart;
		this.ifile = ifile;
		this.name = name;
	}
	public IEditorPart getEditorPart() {
		return editorPart;
	}
	public void setEditorPart(IEditorPart editorPart) {
		this.editorPart = editorPart;
	}
	public IFile getIfile() {
		return ifile;
	}
	public void setIfile(IFile ifile) {
		this.ifile = ifile;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}	
}
