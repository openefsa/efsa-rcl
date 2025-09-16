package dataset;

import i18n_messages.Messages;
import java.util.Arrays;

/**
 * Enumerator that identifies the status of a {@link Dataset}
 * @author avonva
 * @author shahaal
 */
public enum RCLDatasetStatus {
	
	DRAFT("DRAFT", Messages.get("draft")),  // local status, if message was never sent
	LOCALLY_VALIDATED("LOCALLY_VALIDATED", Messages.get("validated")),
	UPLOAD_FAILED("UPLOAD_FAILED", Messages.get("upload.failed")),  // local status, used if send message fails
	VALID("VALID", Messages.get("valid")),
	UPLOADED("UPLOADED", Messages.get("uploaded")),  // dataset sent but no response received yet 
	PROCESSING("PROCESSING", Messages.get("processing")),
	VALID_WITH_WARNING("VALID_WITH_WARNING", Messages.get("valid.warning")),
	REJECTED_EDITABLE("REJECTED EDITABLE", Messages.get("rejected.editable")),
	REJECTED("REJECTED", Messages.get("rejected")),
	REJECTION_SENT("REJECTION_SENT", Messages.get("rejection.sent")),
	REJECTION_FAILED("REJECTION_FAILED", Messages.get("rejection.failed")),
	DELETED("DELETED", Messages.get("deleted")),
	SUBMITTED("SUBMITTED", Messages.get("submitted")),
	SUBMISSION_SENT("SUBMISSION_SENT", Messages.get("submission.sent")),
	SUBMISSION_FAILED("SUBMISSION_FAILED", Messages.get("submission.failed")),
	ACCEPTED_DWH("ACCEPTED DWH", Messages.get("accepted.dwh")),
	UPDATED_BY_DATA_RECEIVER("UPLOADED_BY_DATA_RECEIVER", Messages.get("uploaded.receiver")),
	OTHER("OTHER", Messages.get("other"));  // error state
	
	private String status;
	private String label;
	private String step;
	
	private RCLDatasetStatus(String status, String label) {
		this.status = status;
		this.label = label;
	}
	
	/**
	 * Set the status step
	 * @param step
	 */
	public void setStep(String step) {
		this.step = step;
	}
	
	/**
	 * Get the raw status
	 * @return
	 */
	public String getStatus() {
		return status;
	}
	
	/**
	 * Get the status label
	 * @return
	 */
	public String getLabel() {
		return label;
	}
	
	/**
	 * Get the status step
	 * @return
	 */
	public String getStep() {
		return step;
	}

	/**
	 * Check if the dataset is editable or not
	 * @return
	 */
	public boolean isEditable() {
		return this == DRAFT;
	}
	
	
	/**
	 * Check if the dataset can be sent to the dcf or not
	 * @return
	 */
	public boolean canBeSent() {
		return this == LOCALLY_VALIDATED || this == UPLOAD_FAILED 
				|| this == REJECTED;
	}
	
	/**
	 * Check if the dataset is valid or not
	 * @return
	 */
	public boolean isValid() {
		return this == LOCALLY_VALIDATED;
	}
	
	/**
	 * Check if the dataset can be made editable or not
	 * @return
	 */
	public boolean canBeMadeEditable() {
		return this == VALID || this == REJECTION_FAILED || this == SUBMISSION_FAILED 
				|| this == VALID_WITH_WARNING
				|| this == REJECTED_EDITABLE || this == REJECTED 
				|| this == UPLOAD_FAILED || this == DELETED || this == LOCALLY_VALIDATED
				|| this == OTHER;
	}
	
	public boolean canBeChecked() {
		return this == DRAFT;
	}
	
	/**
	 * Check if the dataset can be rejected or not
	 * @return
	 */
	public boolean canBeRejected() {
		return this == VALID || this == REJECTION_FAILED || 
				this == SUBMISSION_FAILED || this == VALID_WITH_WARNING; 
	}
	
	/**
	 * Check if the status of the dataset can be refreshed or not
	 * @return
	 */
	public boolean canBeRefreshed() {
		return this == VALID
				|| this == VALID_WITH_WARNING || this == REJECTION_SENT
				|| this == REJECTED_EDITABLE || this == UPLOADED
				|| this == SUBMISSION_SENT || this == SUBMITTED;
	}
	
	/**
	 * Check if the status of the dataset can be refreshed or not
	 * @return
	 */
	public boolean canDisplayAck() {
		return this == UPLOAD_FAILED || this == DRAFT 
				|| this == REJECTED || this == VALID 
				|| this == REJECTION_FAILED || this == SUBMISSION_FAILED 
				|| this == VALID_WITH_WARNING || this == REJECTED_EDITABLE
				|| this == OTHER;
	}
	
	/**
	 * Check if the dataset can be submitted or not
	 * @return
	 */
	public boolean canBeSubmitted() {
		return this == VALID || this == VALID_WITH_WARNING 
				|| this == REJECTION_FAILED || this == SUBMISSION_FAILED ; 
	}
	
	/**
	 * Check if the dataset can be amended or not
	 * @return
	 */
	public boolean canBeAmended() {
		return this == ACCEPTED_DWH;
	}
	
	/**
	 * Check if an ack for the chosen dataset can be picked up or not
	 * @return
	 */
	public boolean canGetAck() {
		return this == UPLOADED || this == SUBMISSION_SENT
				|| this == REJECTION_SENT;
	}

	/**
	 * Check if the current dataset exists or not in the DCF
	 */
	public boolean existsInDCF() {
		return this == VALID 
				|| this == VALID_WITH_WARNING
				|| this == REJECTED_EDITABLE
				|| this == SUBMITTED
				|| this == ACCEPTED_DWH
				|| this == UPDATED_BY_DATA_RECEIVER
				|| this == SUBMISSION_SENT
				|| this == REJECTION_SENT;
	}
	
	/**
	 * Get the failed version of a status
	 * @param status
	 * @return
	 */
	public static RCLDatasetStatus getFailedVersionOf(RCLDatasetStatus status) {
		
		RCLDatasetStatus failedStatus = null;
		
		switch(status) {
		case UPLOADED:
			failedStatus = RCLDatasetStatus.UPLOAD_FAILED;
			break;
		case SUBMISSION_SENT:
			failedStatus = RCLDatasetStatus.SUBMISSION_FAILED;
			break;
		case REJECTION_SENT:
			failedStatus = RCLDatasetStatus.REJECTION_FAILED;
			break;
		default:
			break;
		}
		
		return failedStatus;
	}

	/**
	 * Get the enumerator that matches the {@code text}
	 * @param text
	 * @return
	 */
	public static RCLDatasetStatus fromString(String text) {
		
		String myStatus = text.toLowerCase().replaceAll(" ", "").replaceAll("_", "");
		
		for (RCLDatasetStatus b : RCLDatasetStatus.values()) {
			
			String otherStatus = b.status.toLowerCase().replaceAll(" ", "").replaceAll("_", "");
			
			if (otherStatus.equalsIgnoreCase(myStatus)) {
				return b;
			}
		}
		
		return OTHER;
	}
	
	public static RCLDatasetStatus fromDcfStatus(DcfDatasetStatus status) {
		
		if (status == null) {
			throw new IllegalArgumentException("Cannot get rcl status from null");
		}
		
		RCLDatasetStatus newStatus;
		
		switch(status) {
		case ACCEPTED_DWH:
			newStatus = ACCEPTED_DWH;
			break;
		case DELETED:
			newStatus = DELETED;
			break;
		case PROCESSING:
			newStatus = PROCESSING;
			break;
		case SUBMITTED:
			newStatus = SUBMITTED;
			break;
		case REJECTED:
			newStatus = REJECTED;
			break;
		case REJECTED_EDITABLE:
			newStatus = REJECTED_EDITABLE;
			break;
		case VALID:
			newStatus = VALID;
			break;
		case VALID_WITH_WARNING:
			newStatus = VALID_WITH_WARNING;
			break;
		case UPDATED_BY_DATA_RECEIVER:
			newStatus = UPDATED_BY_DATA_RECEIVER;
			break;
		case OTHER:
		default:
			newStatus = OTHER;
			break;
		}
		
		return newStatus;
	}
	
	@Override
	public String toString() {
		return this.getLabel();
	}

	public boolean isFinalized() {
		return Arrays.<RCLDatasetStatus>asList(new RCLDatasetStatus[] { ACCEPTED_DWH, REJECTED, REJECTED_EDITABLE }).contains(this);
	}
}
