package dataset;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;

import report.VersionComparator;
import soap.GetDatasetsList;

/**
 * List of dataset received by calling {@link GetDatasetsList}
 * 
 * @author avonva
 *
 */
public class DatasetList extends ArrayList<IDataset> implements IDcfDatasetsList<IDataset> {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * Check if the datasets list contains a report with the chosen senderId
	 * 
	 * @param senderId
	 * @return
	 */
	public boolean contains(String senderId) {
		return !filterBySenderId(senderId).isEmpty();
	}

	/**
	 * Filter the datasets by their sender id (regex)
	 * 
	 * @param regex
	 * @return
	 */
	public DatasetList filterByDatasetId(String regex) {

		DatasetList filteredList = new DatasetList();

		for (IDataset dataset : this) {

			String datasetId = dataset.getId();

			// avoid null senderId
			if (datasetId == null)
				continue;

			if (datasetId.matches(regex))
				filteredList.add(dataset);
		}

		return filteredList;
	}

	/**
	 * Filter the datasets by their sender id (regex)
	 * 
	 * @param regex
	 * @return
	 */
	public DatasetList filterBySenderId(String regex) {

		DatasetList filteredList = new DatasetList();

		for (IDataset dataset : this) {

			String senderId = dataset.getSenderId();

			// avoid null senderId
			if (senderId == null)
				continue;

			if (senderId.matches(regex) || senderId.startsWith(regex))
				filteredList.add(dataset);
		}

		return filteredList;
	}

	/**
	 * Filter the datasets by their decomposed sender id (regex)
	 * 
	 * @param regex
	 * @return
	 */
	public DatasetList filterByDecomposedSenderId(String regex) {

		DatasetList filteredList = new DatasetList();

		for (IDataset dataset : this) {

			String senderId = dataset.getDecomposedSenderId();

			// avoid null senderId
			if (senderId == null)
				continue;

			if (senderId.matches(regex))
				filteredList.add(dataset);
		}

		return filteredList;
	}

	/**
	 * Get the most recent dataset of the list using the datasets id
	 * 
	 * @return
	 */
	public IDataset getMostRecentDataset() {

		if (this.isEmpty())
			return null;

		IDataset max = this.get(0);

		for (IDataset d : this) {
			if (d.getId().compareTo(max.getId()) > 0)
				max = d;
		}

		return max;
	}

	/**
	 * Filter by inclusion
	 * 
	 * @param statusFilter
	 * @return
	 */
	public DatasetList filterByStatus(Collection<RCLDatasetStatus> statusFilter) {
		return filterByStatus(statusFilter, false);
	}

	/**
	 * Filter the datasets by their status
	 * 
	 * @param statusFilter
	 * @return
	 */
	public DatasetList filterByStatus(Collection<RCLDatasetStatus> statusFilter, boolean exclude) {

		DatasetList filteredList = new DatasetList();
		for (IDataset d : this) {

			boolean contained = statusFilter.contains(d.getRCLStatus());

			// if contained and we filter by inclusion
			// of if not contained and we filter by exclusion add it
			boolean addIt = (contained && !exclude) || (!contained && exclude);

			if (addIt) {
				filteredList.add(d);
			}
		}

		return filteredList;
	}

	public DatasetList filterByStatus(RCLDatasetStatus statusFilter, boolean exclude) {

		Collection<RCLDatasetStatus> status = new ArrayList<>();
		status.add(statusFilter);

		return filterByStatus(status, exclude);
	}

	public DatasetList filterByStatus(RCLDatasetStatus statusFilter) {
		return filterByStatus(statusFilter, false);
	}

	/**
	 * Get the last version in status {@link RCLDatasetStatus#ACCEPTED_DWH}
	 * 
	 * @return
	 */
	public IDataset getLastAcceptedVersion(String senderId) {

		// get only accepted datasets
		DatasetList datasets = this.filterByStatus(RCLDatasetStatus.ACCEPTED_DWH);

		// get the last
		return datasets.getLastVersion(senderId);
	}

	/**
	 * Get the last version of a dataset with status which is not
	 * {@link RCLDatasetStatus#DELETED} or {@link RCLDatasetStatus#REJECTED}
	 * 
	 * @param senderId
	 * @return
	 */
	public IDataset getLastExistingVersion(String senderId) {

		Collection<RCLDatasetStatus> statusFilter = new ArrayList<>();
		statusFilter.add(RCLDatasetStatus.DELETED);
		statusFilter.add(RCLDatasetStatus.REJECTED);

		// get only datasets that exist in DCF
		DatasetList datasets = this.filterByStatus(statusFilter, true);

		// get the last
		return datasets.getLastVersion(senderId);
	}

	/**
	 * Get the last version of the dataset with the specified senderId in the list
	 * 
	 * @param senderId
	 * @return
	 */
	public IDataset getLastVersion(String senderId) {

		// get only related datasets
		DatasetList datasets = this.filterByDecomposedSenderId(senderId);

		if (datasets.isEmpty())
			return null;

		IDataset last = datasets.get(0);

		for (IDataset d : datasets) {

			VersionComparator comparator = new VersionComparator();
			int compare = comparator.compare(d, last);

			// if the new has a greater version
			// then save it as last
			if (compare < 0)
				last = d;
		}

		return last;
	}

	/**
	 * Sort the collection by sender id and version
	 */
	public void sort() {
		Collections.sort(this, new VersionComparator());
	}

	public void sortAsc() {
		sort();
		Collections.reverse(this);
	}

	/**
	 * Filter all the old versions of datasets
	 * 
	 * @return
	 */
	public DatasetList filterOldVersions() {

		DatasetList lasts = new DatasetList();
		for (IDataset d : this) {

			String senderId = d.getDecomposedSenderId();

			if (senderId == null)
				continue;

			// get last version of the dataset
			IDataset last = this.getLastVersion(senderId);
			
			// if not already added, put it in the list of lasts
			if (!lasts.contains(last)) {
				lasts.add(last);
			}
		}

		return lasts;
	}

	/**
	 * Get the list of dataset that can be downloaded from the tool
	 * 
	 * @return
	 */
	public DatasetList getDownloadableDatasetsLatestVersions(String validSenderIdPattern) {
		return getDownloadableDatasets(validSenderIdPattern).filterOldVersions();
	}

	/**
	 * Get the list of dataset that can be downloaded from the tool
	 * 
	 * @return
	 */
	public DatasetList getDownloadableDatasets(String validSenderIdPattern) {

		Collection<RCLDatasetStatus> statusFilter = new ArrayList<>();
		statusFilter.add(RCLDatasetStatus.REJECTED);
		statusFilter.add(RCLDatasetStatus.PROCESSING);
		statusFilter.add(RCLDatasetStatus.DELETED);

		// filter also by the sender id, which should
		// be in the format country year(2) month(2) with
		// an optional version number
		// examples: IT1704 FR1411.1 SP1512.01 GR1109.14

		return filterByStatus(statusFilter, true).filterBySenderId(validSenderIdPattern);
	}

	@Override
	public boolean add(IDataset dataset) {

		// if dataset
		if (dataset instanceof Dataset) {
			Dataset d = (Dataset) dataset;
			d.setStatus(RCLDatasetStatus.fromDcfStatus(dataset.getStatus()));

			return super.add(d);
		}

		// if report
		return super.add(dataset);
	}

	@Override
	public IDataset create() {
		return new Dataset();
	}
}
