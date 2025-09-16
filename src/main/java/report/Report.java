package report;

import app_config.AppPaths;
import dataset.RCLDatasetStatus;
import java.util.Objects;
import java.util.Optional;
import providers.ITableDaoService;
import table_database.TableDao;
import table_skeleton.TableRow;
import table_skeleton.TableRowList;
import table_skeleton.TableVersion;
import xlsx_reader.TableSchema;
import xlsx_reader.TableSchemaList;

public abstract class Report extends TableRow implements EFSAReport {

	public Report(TableRow row) {
		super(row);
	}

	public Report(TableSchema schema) {
		super(schema);
	}

	public Report() {
		super();
	}

	@Override
	public boolean isBaselineVersion() {
		return TableVersion.isFirstVersion(this.getVersion());
	}

	public String getMessageId() {
		return this.getCode(AppPaths.REPORT_MESSAGE_ID);
	}

	public void setMessageId(String id) {
		this.put(AppPaths.REPORT_MESSAGE_ID, id);
	}

	@Override
	public String getLastMessageId() {
		return this.getCode(AppPaths.REPORT_LAST_MESSAGE_ID);
	}

	@Override
	public void setLastMessageId(String msgId) {
		this.put(AppPaths.REPORT_LAST_MESSAGE_ID, msgId);
	}

	@Override
	public String getLastModifyingMessageId() {
		return this.getCode(AppPaths.REPORT_LAST_MODIFYING_MESSAGE_ID);
	}

	@Override
	public void setLastModifyingMessageId(String msgId) {
		this.put(AppPaths.REPORT_LAST_MODIFYING_MESSAGE_ID, msgId);
	}

	@Override
	public String getLastValidationMessageId() {
		return this.getCode(AppPaths.REPORT_LAST_VALIDATION_MESSAGE_ID);
	}

	@Override
	public void setLastValidationMessageId(String msgId) {
		this.put(AppPaths.REPORT_LAST_VALIDATION_MESSAGE_ID, msgId);
	}

	public String getId() {
		return this.getCode(AppPaths.REPORT_DATASET_ID);
	}

	public void setId(String id) {
		this.put(AppPaths.REPORT_DATASET_ID, id);
	}

	/**
	 * Get the version contained in the sender id
	 * 
	 * @return
	 */
	public String getVersion() {
		return this.getCode(AppPaths.REPORT_VERSION);
	}

	public void setVersion(String version) {
		this.put(AppPaths.REPORT_VERSION, version);
	}

	public String getSenderId() {
		String str = getCode(AppPaths.REPORT_SENDER_ID);
		return Objects.isNull(str) ? str : str.trim();
	}

	public void setSenderId(String id) {
		this.put(AppPaths.REPORT_SENDER_ID, id);
	}

	/**
	 * Get the status of the dataset attached to the report
	 * 
	 * @return
	 */
	public RCLDatasetStatus getRCLStatus() {
		String status = getCode(AppPaths.REPORT_STATUS);
		return RCLDatasetStatus.fromString(status);
	}

	public void setRCLStatus(String status) {
		this.put(AppPaths.REPORT_PREVIOUS_STATUS, this.getRCLStatus().getStatus());
		this.put(AppPaths.REPORT_STATUS, status);
	}

	public boolean isVisible() {
		return ReportType.SIMPLE_MONTHLY.equals(getType());
	}

	/**
	 * Get the previous status of the dataset
	 * 
	 * @return
	 */
	public RCLDatasetStatus getPreviousStatus() {
		String status = getCode(AppPaths.REPORT_PREVIOUS_STATUS);

		if (status.isEmpty())
			return null;

		return RCLDatasetStatus.fromString(status);
	}

	public void setRCLStatus(RCLDatasetStatus status) {
		this.setRCLStatus(status.getStatus());
	}

	public String getYear() {
		return this.getCode(AppPaths.REPORT_YEAR_COL);
	}

	public void setYear(String year) {
		this.put(AppPaths.REPORT_YEAR_COL, getTableColumnValue(year, AppPaths.YEARS_LIST));
	}

	public String getMonth() {
		return this.getCode(AppPaths.REPORT_MONTH_COL);
	}

	public void setMonth(String month) {
		this.put(AppPaths.REPORT_MONTH_COL, getTableColumnValue(month, AppPaths.MONTHS_LIST));
	}

	/**
	 * Force the report to be editable
	 */
	public void makeEditable() {
		this.setRCLStatus(RCLDatasetStatus.DRAFT);
	}

	/**
	 * Check if the dataset can be edited or not
	 * 
	 * @return
	 */
	public boolean isEditable() {
		return getRCLStatus().isEditable();
	}

	/**
	 * Delete all the versions of the report from the database
	 * 
	 * @return
	 */
	public boolean deleteAllVersions(ITableDaoService daoService) {
		return deleteAllVersions(daoService, this.getSenderId());
	}

	/**
	 * Delete all the versions of the report from the db
	 * 
	 * @param senderId
	 * @return
	 */
	public static boolean deleteAllVersions(ITableDaoService daoService, String senderId) {
		// delete the old versions of the report (the one with the same senderId)
		return daoService.deleteByStringField(TableSchemaList.getByName(AppPaths.REPORT_SHEET), AppPaths.REPORT_SENDER_ID,
				senderId);
	}

	public static TableRowList getAllVersions(String senderId) {
		TableDao dao = new TableDao();
		return dao.getByStringField(TableSchemaList.getByName(AppPaths.REPORT_SHEET), AppPaths.REPORT_SENDER_ID, senderId);
	}

	public void setAggregatorId(Integer aggregatorId) {
		this.put(AppPaths.REPORT_AGGREGATOR_ID, Objects.isNull(aggregatorId) ? "" : String.valueOf(aggregatorId));
	}

	public Integer getAggregatorId() {
		return Optional.<String>ofNullable(getCode(AppPaths.REPORT_AGGREGATOR_ID))
			.map(String::trim)
			.filter(id -> Boolean.FALSE.equals(Boolean.valueOf(id.isEmpty())))
	    	.map(Integer::parseInt)
	    	.orElse(null);
	}

	public boolean isAggregated() {
		return Objects.nonNull(getAggregatorId());
	}

	public void setDcCode(String dcCode) {
	  	this.put(AppPaths.REPORT_DC_CODE, dcCode);
	}

	public String getDcCode() {
	  	return getCode(AppPaths.REPORT_DC_CODE);
	}

	public void setType(ReportType type) {
	  	String val = (Objects.nonNull(type) ? type : ReportType.SIMPLE_MONTHLY).name();
	  	this.put(AppPaths.REPORT_TYPE, val);
	}

	public ReportType getType() {
	  	return ReportType.getOrDefault(getCode(AppPaths.REPORT_TYPE));
	}

	/**
	 * get the name of the field that contains the rowId. The rowId is the field
	 * that identifies a record of the report
	 * 
	 * @return
	 */
	public abstract String getRowIdFieldName();
}
