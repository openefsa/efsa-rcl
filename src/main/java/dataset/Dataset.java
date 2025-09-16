package dataset;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;

import javax.xml.stream.XMLStreamException;

import config.Config;
import duplicates_detector.Checkable;
import soap.DetailedSOAPException;
import soap.GetDataset;
import soap.GetDatasetsList;
import table_skeleton.TableRow;
import table_skeleton.TableVersion;
import user.User;

/**
 * Dcf dataset that is downloaded using the {@link GetDatasetsList}
 * request.
 * @author avonva
 *
 */

public class Dataset extends DcfDataset implements IDataset, Checkable {
	
	private File datasetFile; // cache
	
	private Header header;
	private Operation operation;
	private Collection<TableRow> rows;
	
	private String id;
	private String senderId;
	private RCLDatasetStatus status;

	private String collectionCode;
	
	public Dataset() {
		this.rows = new ArrayList<>();
	}
	
	/**
	 * Get the sender id from a composed sender id of the dataset if possible.
	 * Only usable with senderId in format CountryYearMonth.Version (as FR1705.01)
	 * in the example, this would return FR1705
	 * @return
	 */
	public String getDecomposedSenderId() {
		
		String[] split = splitSenderId();
		if (split == null)
			return this.senderId;
		
		return split[0];
	}
	
	/**
	 * Get the version of the dataset if possible.
	 * Only usable with senderId in format CountryYearMonth.Version (as FR1705.01)
	 * @return
	 */
	public String getVersion() {
		return TableVersion.extractVersionFrom(senderId);
	}
	
	private String[] splitSenderId() {
		return splitSenderId(this.senderId);
	}
	
	public static String[] splitSenderId(String senderId) {
		
		if (senderId == null)
			return null;
		
		String[] split = senderId.split("\\.");
		
		if (split.length != 2)
			return null;
		
		return split;
	}
	
	public File download() throws DetailedSOAPException, NoAttachmentException {
		
		// use cache if possible
		if (this.datasetFile != null && this.datasetFile.exists()) {
			return datasetFile;
		}
		
		GetDataset req = new GetDataset();
		File file = req.getDatasetFile(Config.getEnvironment(), User.getInstance(), id);
		
		if (file == null)
			throw new NoAttachmentException("Cannot find the attachment of the dataset with id=" + id);
		
		this.datasetFile = file;
		
		return file;
	}
	
	/**
	 * Populate the dataset with the header and operation information (from DCF)
	 * @return
	 * @throws XMLStreamException
	 * @throws DetailedSOAPException
	 * @throws IOException
	 * @throws NoAttachmentException 
	 */
	public Dataset populateMetadata() throws XMLStreamException, DetailedSOAPException, IOException, NoAttachmentException {
		
		File file = download();
		
		if (file == null)
			return null;
		
		DatasetMetaDataParser parser = new DatasetMetaDataParser(file);
		
		Dataset dataset = parser.parse();
		
		dataset.setStatus(this.status);
		dataset.setSenderId(this.senderId);
		dataset.setId(dataset.getOperation().getDatasetId());
		
		parser.close();
		
		return dataset;
	}
	
	public void addRow(TableRow row) {
		this.rows.add(row);
	}
	
	public void setHeader(Header header) {
		this.header = header;
	}
	
	public void setOperation(Operation operation) {
		this.operation = operation;
	}
	public Operation getOperation() {
		return operation;
	}
	
	public Header getHeader() {
		return header;
	}
	
	/**
	 * Set the dataset id
	 * @param id
	 */
	public void setId(String id) {
		this.id = id;
	}
	
	/**
	 * Set the dataset sender id (as IT1708.01)
	 * note that the version is contained in it
	 * @param senderId
	 */
	public void setSenderId(String senderId) {
		this.senderId = senderId;
	}
	
	/**
	 * Set the dataset status
	 * @param status
	 */
	public void setStatus(RCLDatasetStatus status) {
		this.status = status;
	}
	
	/**
	 * Get the dataset sender id (id given
	 * by the data provider)
	 * @return
	 */
	public String getSenderId() {
		return senderId;
	}
	
	public Collection<TableRow> getRows() {
		return rows;
	}
	
	/**
	 * Get the dataset status
	 * @return
	 */
	public RCLDatasetStatus getRCLStatus() {
		return status;
	}

	/**
	 * Check if the dataset can be edited or not
	 * @return
	 */
	public boolean isEditable() {

		if (status == null)
			return false;

		return status.isEditable();
	}

	@Override
	public String toString() {
		return "Dataset: id=" + id 
				+ ";senderId=" + senderId 
				+ ";status=" + status
				+ " " + header
				+ " " + operation;
	}
	
	@Override
	public boolean equals(Object arg0) {
		
		if (arg0 instanceof Dataset) {
			return this.senderId.equalsIgnoreCase(((Dataset) arg0).getSenderId());
		}
		
		return super.equals(arg0);
	}
	
	@Override
	public int hashCode() {
		return this.senderId.hashCode();
	}
	
	@Override
	public boolean sameAs(Object arg0) {
		
		Dataset a = (Dataset) arg0;
		return this.senderId == a.senderId;
	}

	@Override
	public String getId() {
		if (operation != null && operation.getDatasetId() != null) {
			return operation.getDatasetId();
		}
		
		if (id != null)
			return id;
		
		return null;
	}
}
