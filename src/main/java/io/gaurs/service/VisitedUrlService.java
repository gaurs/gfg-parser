package io.gaurs.service;

import java.util.Set;

/**
 * The service is used to store the list of already visited urls onto a file
 * system. The same can be extended to use any DB as well
 * 
 * 
 * @author gaurs
 *
 */
public interface VisitedUrlService {

	/**
	 * Save the list of urls visited in this session. It should append/add to
	 * the already visited list. 
	 * 
	 * @param urls
	 * @param append 
	 */
	void saveUrlList(Set<String> urls, boolean append);

	/**
	 * Get the list of visited urls from file/db. Should be called only once per session
	 */
	Set<String> getUrlList();

}
