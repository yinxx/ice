package org.jbei.ice.lib.managers;

import java.util.LinkedHashSet;
import java.util.Set;

import org.hibernate.Query;
import org.jbei.ice.lib.logging.Logger;
import org.jbei.ice.lib.models.Entry;
import org.jbei.ice.lib.models.Sample;

public class SampleManager extends Manager{
	public static Sample get(int id) throws ManagerException {
		Sample result  = null;
		try {
			result = (Sample) dbGet(Sample.class, id);
		} catch (Exception e) {
			String msg = "Could not get Sample by id";
			Logger.error(msg);
			
			e.printStackTrace();
			throw new ManagerException(msg, e);
			
		}
		return result;
	}
	
	
	@SuppressWarnings("unchecked")
	public static Set<Sample> get(Entry entry) throws ManagerException {
		LinkedHashSet<Sample> result = null;
		try {
			String queryString = "from Sample as sample where sample.entry = :entry";
			Query query = HibernateHelper.getSession().createQuery(queryString);
			query.setEntity("entry", entry);
			result = new LinkedHashSet<Sample>(query.list());
			
		} catch (Exception e) {
			String msg = "Could not get Sample by Entry " + entry.getRecordId();
			Logger.error(msg);
			throw new ManagerException(msg, e);
		}
		
		return result;
	}
	
	public static Sample save(Sample sample) throws ManagerException {
		Sample result = null;
		try {
			result = (Sample) dbSave(sample);
		} catch (Exception e) {
			String msg = "Could not save sample " + sample.getLabel();
			Logger.error(msg);
			throw new ManagerException(msg, e);
		}
		return result;
	}
	
	public static void delete(Sample sample) throws ManagerException {
		try {
			dbDelete(sample);
		} catch (Exception e) {
			String msg = "Could not delete sample " + sample.getId();
			Logger.error(msg);
			throw new ManagerException(msg, e);
		}
	}
}