package org.jbei.ice.lib.net;

import org.jbei.ice.lib.AccountCreator;
import org.jbei.ice.lib.TestEntryCreator;
import org.jbei.ice.lib.account.model.Account;
import org.jbei.ice.lib.dao.DAOFactory;
import org.jbei.ice.lib.dao.hibernate.EntryDAO;
import org.jbei.ice.lib.dao.hibernate.HibernateUtil;
import org.jbei.ice.lib.dto.entry.PartData;
import org.jbei.ice.lib.entry.EntryCreator;
import org.jbei.ice.lib.entry.model.Plasmid;
import org.jbei.ice.lib.entry.model.Strain;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

public class RemoteTransferTest {

    private RemoteTransfer transfer;

    @Before
    public void setUp() throws Exception {
        transfer = new RemoteTransfer();
        HibernateUtil.initializeMock();
        HibernateUtil.beginTransaction();
    }

    @After
    public void tearDown() throws Exception {
        HibernateUtil.rollbackTransaction();
    }

    @Test
    public void testGetPartsForTransfer() throws Exception {
        EntryDAO dao = DAOFactory.getEntryDAO();

        Account account = AccountCreator.createTestAccount("testGetPartsForTransfer", false);
        Strain strain = TestEntryCreator.createTestStrain(account);
        Plasmid plasmid = TestEntryCreator.createTestPlasmid(account);
        strain.getLinkedEntries().add(plasmid);
        DAOFactory.getEntryDAO().update(strain);
        strain = (Strain) DAOFactory.getEntryDAO().get(strain.getId());
        Assert.assertTrue(strain.getLinkedEntries().size() == 1);

        Plasmid plasmid2 = TestEntryCreator.createTestPlasmid(account);
        strain.getLinkedEntries().add(plasmid2);
        dao.update(strain);

        Plasmid plasmid3 = TestEntryCreator.createTestPlasmid(account);
        Strain strain2 = TestEntryCreator.createTestStrain(account);
        strain2.getLinkedEntries().add(plasmid2);
        strain2.getLinkedEntries().add(plasmid3);
        dao.update(strain2);

        ArrayList<Long> ids = new ArrayList<>();
        ids.add(plasmid.getId());
        ids.add(plasmid2.getId());
        ids.add(plasmid3.getId());
        ids.add(strain.getId());
        ids.add(strain2.getId());

        List<PartData> data = transfer.getPartsForTransfer(ids);
        EntryCreator creator = new EntryCreator();

        Assert.assertEquals(data.size(), 2);
        for (PartData datum : data) {
            // both should have 2 linked entries with 1 in common
            Assert.assertEquals(datum.getLinkedParts().size(), 2);

            // transfer
            datum.setRecordId("");
            PartData transferred = creator.receiveTransferredEntry(datum);
            Assert.assertNotNull(transferred);
        }
    }
}