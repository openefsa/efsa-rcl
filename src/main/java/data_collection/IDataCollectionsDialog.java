package data_collection;

public interface IDataCollectionsDialog {
	/**
	 * Open the dialog and get the selected data collection
	 * @return
	 */
	public IDcfDataCollection open();
	public IDcfDataCollection open(String var1);
}
