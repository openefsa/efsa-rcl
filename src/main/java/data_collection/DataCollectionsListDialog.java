package data_collection;

import org.eclipse.jface.viewers.DoubleClickEvent;
import org.eclipse.jface.viewers.IDoubleClickListener;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Dialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;

import i18n_messages.Messages;

/**
 * Dialog showing  a list of data collections
 * @author avonva
 *
 */
public class DataCollectionsListDialog extends Dialog implements IDataCollectionsDialog {

	private IDcfDataCollectionsList<IDcfDataCollection> list;
	private IDcfDataCollection selectedDc;
	
	public DataCollectionsListDialog(Shell parent, IDcfDataCollectionsList<IDcfDataCollection> list, int style) {
		super(parent, style);
		this.list = list;
	}
	
	public DataCollectionsListDialog(Shell parent, IDcfDataCollectionsList<IDcfDataCollection> list) {
		this(parent, list, SWT.DIALOG_TRIM | SWT.APPLICATION_MODAL);
	}
	
	public DataCollectionsListDialog(Shell parent, IDcfDataCollectionsList<IDcfDataCollection> list, int style, String buttonTextKey) {
	      super(parent, style);
	      this.list = list;
	}

	protected void createContents(Shell shell) {
		
		TableViewer table = new TableViewer(shell, SWT.BORDER | SWT.SINGLE
				| SWT.V_SCROLL | SWT.H_SCROLL | SWT.FULL_SELECTION | SWT.NONE);
		
		table.getTable().setHeaderVisible(true);
		table.getTable().setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
		table.setContentProvider(new DataCollectionContentProvider());
		
		table.addDoubleClickListener(new IDoubleClickListener() {
			@Override
			public void doubleClick(DoubleClickEvent arg0) {
				select(shell, arg0.getSelection());
			}
		});
		
		String[][] headers = new String[][] {
			{"code", Messages.get("dc.header.code")},
			{"description", Messages.get("dc.header.description")},
			{"activeFrom", Messages.get("dc.header.active.from")},
			{"activeTo", Messages.get("dc.header.active.to")}
		};
		
		for (String[] header : headers) {
			// Add the column to the parent table
			TableViewerColumn col = new TableViewerColumn(table, SWT.NONE);
			col.getColumn().setText(header[1]);
			col.setLabelProvider(new DataCollectionLabelProvider(header[0]));
			col.getColumn().setWidth(150);
		}
		
		table.setInput(list);
		
		Button button = new Button(shell, SWT.PUSH);
		button.setText(Messages.get("dc.dialog.button"));
		button.setEnabled(false);
		button.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				select(shell, table.getSelection());
			}
		});
		button.setLayoutData(new GridData(SWT.CENTER, SWT.CENTER, true, false));
		
		table.addSelectionChangedListener(new ISelectionChangedListener() {
			
			@Override
			public void selectionChanged(SelectionChangedEvent arg0) {
				button.setEnabled(!arg0.getSelection().isEmpty());
			}
		});
		
		shell.pack();
	}
	
	private void select(Shell shell, ISelection selection) {
		
		if (selection.isEmpty())
			return;
		
		IStructuredSelection iSel = (IStructuredSelection) selection;
			
		selectedDc = (IDcfDataCollection) iSel.getFirstElement();
		
		shell.close();
	}

	public IDcfDataCollection open() {

		Shell shell = new Shell(getParent(), SWT.SHELL_TRIM | SWT.APPLICATION_MODAL);
		shell.setLayout(new GridLayout(1, false));
		shell.setLayoutData(new GridData(SWT.CENTER, SWT.CENTER, true, false));
		
		shell.setText(Messages.get("dc.dialog.title"));
		shell.setImage(getParent().getImage());

		createContents(shell);
		
		shell.open();

		Display display = getParent().getDisplay();
		while (!shell.isDisposed()) {
			if (!display.readAndDispatch()) {
				display.sleep();
			}
		}
		
		return selectedDc;
	}
	
	@Override
	   public IDcfDataCollection open(String titleKey) {
	      Shell shell = new Shell(this.getParent(), 66800);
	      shell.setLayout(new GridLayout(1, false));
	      shell.setLayoutData(new GridData(16777216, 16777216, true, false));
	      shell.setText(Messages.get(titleKey));
	      shell.setImage(this.getParent().getImage());
	      this.createContents(shell);
	      shell.open();
	      Display display = this.getParent().getDisplay();

	      while (!shell.isDisposed()) {
	         if (!display.readAndDispatch()) {
	            display.sleep();
	         }
	      }

	      return this.selectedDc;
	   }
}
