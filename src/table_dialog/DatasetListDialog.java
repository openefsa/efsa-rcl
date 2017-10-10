package table_dialog;

import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;

import dataset.Dataset;
import dataset.DatasetList;
import dataset.DatasetStatus;
import global_utils.Warnings;

public class DatasetListDialog {

	private Shell parent;
	private Shell dialog;
	private String title;
	private String okBtnText;
	private Dataset selectedDataset;
	
	private TableViewer datasetList;
	
	public DatasetListDialog(Shell parent, String title, String okBtnText) {
		this.parent = parent;
		this.title = title;
		this.okBtnText = okBtnText;
		create();
	}
	
	public void setList(DatasetList list) {
		datasetList.setInput(list);
	}
	
	private void create() {
		
		this.dialog = new Shell(parent, SWT.SHELL_TRIM | SWT.APPLICATION_MODAL);
		this.dialog.setText(title);
		this.dialog.setImage(parent.getImage());
		
		dialog.setLayout(new GridLayout(1, false));
		dialog.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));

		this.datasetList = new TableViewer(dialog, SWT.BORDER | SWT.SINGLE
				| SWT.V_SCROLL | SWT.H_SCROLL | SWT.FULL_SELECTION | SWT.NONE);
		datasetList.getTable().setHeaderVisible(true);
		datasetList.getTable().setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
		datasetList.setContentProvider(new DatasetContentProvider());
		
		// Add the column to the parent table
		TableViewerColumn idCol = new TableViewerColumn(datasetList, SWT.NONE);
		idCol.getColumn().setText("Dataset id");
		idCol.setLabelProvider(new DatasetLabelProvider("id"));
		idCol.getColumn().setWidth(100);
		
		TableViewerColumn senderIdCol = new TableViewerColumn(datasetList, SWT.NONE);
		senderIdCol.getColumn().setText("Sender id");
		senderIdCol.setLabelProvider(new DatasetLabelProvider("senderId"));
		senderIdCol.getColumn().setWidth(100);
		
		TableViewerColumn statusCol = new TableViewerColumn(datasetList, SWT.NONE);
		statusCol.getColumn().setText("DCF status");
		statusCol.setLabelProvider(new DatasetLabelProvider("status"));
		statusCol.getColumn().setWidth(130);
		
		TableViewerColumn revisionCol = new TableViewerColumn(datasetList, SWT.NONE);
		revisionCol.getColumn().setText("Current revision");
		revisionCol.setLabelProvider(new DatasetLabelProvider("revision"));
		revisionCol.getColumn().setWidth(100);

		// ok button to select a dataset
		Button okBtn = new Button(dialog, SWT.PUSH);
		okBtn.setText(okBtnText);
		okBtn.setLayoutData(new GridData(SWT.CENTER, SWT.CENTER, true, false));
		
		okBtn.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent arg0) {
				
				IStructuredSelection selection = (IStructuredSelection) datasetList.getSelection();
				if (selection.isEmpty()) {
					Warnings.warnUser(dialog, "Error", "No dataset was selected");
					return;
				}
				
				selectedDataset = (Dataset) selection.getFirstElement();
				
				dialog.close();
			}
		});
	}
	
	public void open() {
		
		dialog.pack();
		dialog.open();

		// Event loop
		while ( !dialog.isDisposed() ) {
			if ( !dialog.getDisplay().readAndDispatch() )
				dialog.getDisplay().sleep();
		}
	}
	
	public Shell getParent() {
		return parent;
	}
	
	public Shell getDialog() {
		return dialog;
	}
	
	/**
	 * Get the selected dataset
	 * @return
	 */
	public Dataset getSelectedDataset() {
		return selectedDataset;
	}
	
	public static void main(String[] args) {
		
		DatasetList list = new DatasetList();
		
		String[] senderIds = new String[]{
				"IT1704", "IT1704.02", 
				"FR1204", "FR1204.99",
				"SP1707"
				};
		
		for (int i = 0; i < senderIds.length; ++i) {
			Dataset d1 = new Dataset();
			d1.setId("");
			d1.setStatus(DatasetStatus.VALID);
			d1.setSenderId(senderIds[i]);
			list.add(d1);
		}
		
		Display display = new Display();
		Shell shell = new Shell(display);
		
		DatasetListDialog dialog = new DatasetListDialog(shell, "", "OK");
		dialog.setList(list.getDownloadableDatasets());
		dialog.open();
		
		// get all the versions
		Dataset chosen = dialog.getSelectedDataset();
		DatasetList versions = list.filterByDecomposedSenderId(chosen.getDecomposedSenderId());
		System.out.println(versions);
	}
}
