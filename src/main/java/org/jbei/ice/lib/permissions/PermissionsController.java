package org.jbei.ice.lib.permissions;

import java.util.Set;

import org.jbei.ice.controllers.AccountController;
import org.jbei.ice.controllers.common.Controller;
import org.jbei.ice.controllers.common.ControllerException;
import org.jbei.ice.controllers.permissionVerifiers.EntryPermissionVerifier;
import org.jbei.ice.lib.logging.Logger;
import org.jbei.ice.lib.managers.GroupManager;
import org.jbei.ice.lib.managers.ManagerException;
import org.jbei.ice.lib.models.Account;
import org.jbei.ice.lib.models.Entry;
import org.jbei.ice.lib.models.Group;
import org.jbei.ice.shared.dto.permission.PermissionInfo.PermissionType;

public class PermissionsController extends Controller {

    public PermissionsController(Account account) {
        super(account, new EntryPermissionVerifier());
    }

    /**
     * Adds permission to a specific entry
     * 
     * @param type
     *            type of permission being added
     * @param entry
     *            entry permission is being added to
     * @param id
     *            unique identifier for account or group that is to be granted permissions.
     * @throws ControllerException
     */
    public void addPermission(PermissionType type, Entry entry, long id) throws ControllerException {
        Account account = super.getAccount();
        try {
            switch (type) {
            case READ_ACCOUNT:
                Account readAccount = AccountController.get(id);
                if (readAccount != null && !readAccount.getEmail().equals(account.getEmail()))
                    addReadUser(entry, readAccount);
                break;

            case READ_GROUP:
                Group readGroup = GroupManager.get(id);
                if (readGroup != null)
                    addReadGroup(entry, readGroup);
                break;

            case WRITE_ACCOUNT:
                Account writeAccount = AccountController.get(id);
                if (writeAccount != null && !writeAccount.getEmail().equals(account.getEmail())) {
                    addWriteUser(entry, writeAccount);
                    addReadUser(entry, writeAccount);
                }
                break;

            case WRITE_GROUP:
                Group writeGroup = GroupManager.get(id);
                if (writeGroup != null) {
                    addWriteGroup(entry, writeGroup);
                    addReadGroup(entry, writeGroup);
                }
                break;
            }
        } catch (PermissionException pe) {
            Logger.error(pe);
            throw new ControllerException("User " + account.getEmail()
                    + " does not have permissions to modify entry permissions for " + entry.getId());
        } catch (ManagerException me) {
            Logger.error(me);
            throw new ControllerException(me);
        }
    }

    /**
     * Removes a type of permission from an entry
     * 
     * @param type
     *            type of permission to remove
     * @param entry
     *            entry
     * @param id
     *            identifier for group or account being removed
     * @throws ControllerException
     */
    public void removePermission(PermissionType type, Entry entry, long id)
            throws ControllerException {

        Account account = super.getAccount();
        try {

            switch (type) {
            case READ_ACCOUNT:
                Account readAccount = AccountController.get(id);
                if (readAccount != null && !readAccount.getEmail().equals(account.getEmail())) // cannot remove yourself
                    removeReadUser(entry, readAccount);
                break;

            case READ_GROUP:
                Group readGroup = GroupManager.get(id);
                if (readGroup != null)
                    removeReadGroup(entry, readGroup);
                break;

            case WRITE_ACCOUNT:
                Account writeAccount = AccountController.get(id);
                if (writeAccount != null && !writeAccount.getEmail().equals(account.getEmail())) { // cannot remove yourself 
                    removeWriteUser(entry, writeAccount);
                    removeReadUser(entry, writeAccount);
                }
                break;

            case WRITE_GROUP:
                Group writeGroup = GroupManager.get(id);
                if (writeGroup != null) {
                    removeWriteGroup(entry, writeGroup);
                    removeReadGroup(entry, writeGroup);
                }
                break;
            }
        } catch (PermissionException pe) {
            Logger.error(pe);
            throw new ControllerException("User " + account.getEmail()
                    + " does not have permissions to modify entry permissions for " + entry.getId());
        } catch (ManagerException me) {
            Logger.error(me);
            throw new ControllerException(me);
        }
    }

    /**
     * Set read permissions for specified user {@link Group}s to the given {@link Entry}.
     * <p>
     * This method creates new {@link ReadGroup} objects using the given {@link Group}s.
     * 
     * @param entry
     *            Entry to give permission to.
     * @param groups
     *            Groups to give read permission to.
     * @throws ManagerException
     * @throws PermissionException
     */
    public void setReadGroup(Entry entry, Set<Group> groups) throws ManagerException,
            PermissionException {
        if (PermissionManager.hasWritePermission(entry.getId(), super.getAccount())) {
            PermissionManager.setReadGroup(entry, groups);
        } else {
            throw new PermissionException("Write Permission not permitted");
        }
    }

    /**
     * Set write permissions for specified user {@link Group}s to the given {@link Entry}.
     * <p>
     * This method creates new {@link WriteGroup} objects using the given {@link Group}s.
     * 
     * @param entry
     *            Entry to give permission to.
     * @param groups
     *            Groups to give write permission to.
     * @throws ManagerException
     * @throws PermissionException
     */
    public void setWriteGroup(Entry entry, Set<Group> groups) throws ManagerException,
            PermissionException {
        if (PermissionManager.hasWritePermission(entry.getId(), super.getAccount())) {
            PermissionManager.setWriteGroup(entry, groups);
        } else {
            throw new PermissionException("Write Permission not permitted");
        }
    }

    /**
     * Set read permissions for specified user {@link Account}s to the given {@link Entry}.
     * <p>
     * This method creates new {@link ReadUser} objects using the given {@link Account}s.
     * 
     * @param entry
     *            Entry to give read permission to.
     * @param accounts
     *            Accounts to give read permission to.
     * @throws ManagerException
     * @throws PermissionException
     */
    public void setReadUser(Entry entry, Set<Account> accounts) throws ManagerException,
            PermissionException {
        if (PermissionManager.hasWritePermission(entry.getId(), super.getAccount())) {
            PermissionManager.setReadUser(entry, accounts);
        } else {
            throw new PermissionException("Write Permission not permitted");
        }
    }

    public void addReadUser(Entry entry, Account account) throws ManagerException,
            PermissionException {
        if (PermissionManager.hasWritePermission(entry.getId(), super.getAccount())) {
            PermissionManager.addReadUser(entry, account);
        } else {
            throw new PermissionException("User " + super.getAccount().getEmail()
                    + " does not have write permissions for entry " + entry.getId());
        }
    }

    public void removeReadUser(Entry entry, Account account) throws ManagerException,
            PermissionException {
        if (PermissionManager.hasWritePermission(entry.getId(), super.getAccount())) {
            PermissionManager.removeReadUser(entry, account);
        } else {
            throw new PermissionException("User " + super.getAccount().getEmail()
                    + " does not have write permissions for entry " + entry.getId());
        }
    }

    public void addReadGroup(Entry entry, Group readGroup) throws ManagerException,
            PermissionException {
        if (PermissionManager.hasWritePermission(entry.getId(), super.getAccount())) {
            PermissionManager.addReadGroup(entry, readGroup);
        } else {
            throw new PermissionException("Write Permission not permitted");
        }
    }

    public void removeReadGroup(Entry entry, Group readGroup) throws ManagerException,
            PermissionException {
        if (PermissionManager.hasWritePermission(entry.getId(), super.getAccount())) {
            PermissionManager.removeReadGroup(entry, readGroup);
        } else {
            throw new PermissionException("Write Permission not permitted");
        }
    }

    public void addWriteUser(Entry entry, Account account) throws ManagerException,
            PermissionException {
        if (PermissionManager.hasWritePermission(entry.getId(), super.getAccount())) {
            PermissionManager.addWriteUser(entry, account);
        } else {
            throw new PermissionException("Write Permission not permitted");
        }
    }

    public void removeWriteUser(Entry entry, Account account) throws ManagerException,
            PermissionException {
        if (PermissionManager.hasWritePermission(entry.getId(), super.getAccount())) {
            PermissionManager.removeWriteUser(entry, account);
        } else {
            throw new PermissionException("Write Permission not permitted");
        }
    }

    public void removeWriteGroup(Entry entry, Group writeGroup) throws ManagerException,
            PermissionException {
        if (PermissionManager.hasWritePermission(entry.getId(), super.getAccount())) {
            PermissionManager.removeWriteGroup(entry, writeGroup);
        } else {
            throw new PermissionException("Write Permission not permitted");
        }
    }

    public void addWriteGroup(Entry entry, Group writeGroup) throws ManagerException,
            PermissionException {
        if (PermissionManager.hasWritePermission(entry.getId(), super.getAccount())) {
            PermissionManager.addWriteGroup(entry, writeGroup);
        } else {
            throw new PermissionException("Write Permission not permitted");
        }
    }

    /**
     * Set write permissions for specified user {@link Account}s to the given {@link Entry}.
     * <p>
     * This method creates new {@link WriteUser} objects using the given {@link Account}s.
     * 
     * @param entry
     *            Entry to give permission to.
     * @param accounts
     *            Accounts to give write permission to.
     * @throws ManagerException
     * @throws PermissionException
     */
    public void setWriteUser(Entry entry, Set<Account> accounts) throws ManagerException,
            PermissionException {
        if (PermissionManager.hasWritePermission(entry.getId(), super.getAccount())) {
            PermissionManager.setWriteUser(entry, accounts);
        } else {
            throw new PermissionException("Write Permission not permitted");
        }
    }

}