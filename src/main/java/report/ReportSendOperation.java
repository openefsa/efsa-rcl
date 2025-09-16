package report;

import dataset.Dataset;
import dataset.RCLDatasetStatus;
import message_creator.OperationType;

public class ReportSendOperation {
	
	private final Dataset dataset;
	private final OperationType opType;

	public ReportSendOperation(Dataset dataset, OperationType opType) {
		this.dataset = dataset;
		this.opType = opType;
	}
	
	public Dataset getDataset() {
		return dataset;
	}
	private String getDatasetId() {
		
		if (dataset != null)
			return dataset.getId();
		
		return null;
	}
	public OperationType getOpType() {
		return opType;
	}
	public RCLDatasetStatus getStatus() {
		
		if (dataset != null)
			return dataset.getRCLStatus();
		
		return null;
	}
	@Override
	public String toString() {
		return "Dataset ID=" + getDatasetId()
			+ "; dataset status=" + getStatus()
			+ "; operation type=" + opType;
	}
}
