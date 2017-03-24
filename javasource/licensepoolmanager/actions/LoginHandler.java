package licensepoolmanager.actions;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import licensepoolmanager.proxies.Account;
import licensepoolmanager.proxies.LicensePool;
import licensepoolmanager.proxies.UserLimit;

import com.mendix.core.Core;
import com.mendix.core.CoreException;
import com.mendix.logging.ILogNode;
import com.mendix.systemwideinterfaces.core.AuthenticationRuntimeException;
import com.mendix.systemwideinterfaces.core.IContext;
import com.mendix.systemwideinterfaces.core.IMendixObject;
import com.mendix.systemwideinterfaces.core.ISession;
import com.mendix.systemwideinterfaces.core.IUser;
import com.mendix.systemwideinterfaces.core.UserAction;

public class LoginHandler extends UserAction<ISession>
{	
	private static ILogNode log = Core.getLogger("LicensePoolManager");
	private final String userName;
	private final String password;
	private final String currentSessionId;
	private final IContext context;
	
	public LoginHandler(Map<String, Object> params) 
	{		
		super(Core.createSystemContext());
		this.context = getContext();
		this.userName = (String)params.get("userName");
		this.password = (String)params.get("password");
		this.currentSessionId = (String)params.get("currentSessionId");
	}

	@Override
	public ISession executeAction() throws Exception
	{		
		// Search the user that wants to login
		IUser mxUser = Core.getUser(this.context, this.userName);

		//perform the user checks!!

		// copied from com.mendix.core.action.user.LoginAction
		if (mxUser == null)
			throw new AuthenticationRuntimeException(new StringBuilder().append("Login FAILED: unknown user '").append(this.userName).append("'.").toString());
		if (mxUser.isWebserviceUser().booleanValue())
			throw new AuthenticationRuntimeException(new StringBuilder().append("Login FAILED: client login attempt for web service user '").append(this.userName).append("'.").toString());
		if (mxUser.isAnonymous().booleanValue())
			throw new AuthenticationRuntimeException(new StringBuilder().append("Login FAILED: client login attempt for guest user '").append(this.userName).append("'.").toString());
		if (!mxUser.isActive().booleanValue())
			throw new AuthenticationRuntimeException(new StringBuilder().append("Login FAILED: user '").append(this.userName).append("' is not active.").toString());
		if (mxUser.isBlocked().booleanValue())
			throw new AuthenticationRuntimeException(new StringBuilder().append("Login FAILED: user '").append(this.userName).append("' is blocked.").toString());
		if (mxUser.getUserRoleNames().isEmpty())
			throw new AuthenticationRuntimeException(new StringBuilder().append("Login FAILED: user '").append(this.userName).append("' does not have any user roles.").toString());
		//~ copied from com.mendix.core.action.user.LoginAction
		
		//check for valid existing session
		ISession currSession = null;
		if(Core.getActiveSession(this.userName) != null && Core.getActiveSession(this.userName).getId().toString().equals(this.currentSessionId)) {
			log.trace("User has valid session, we'll use that. User: " + this.userName);
			currSession = Core.getActiveSession(this.userName);
		}
		else {
			// Check first if the given username and password are correct.
			if(!Core.authenticate(context, mxUser, password))
			{
				throw new AuthenticationRuntimeException("Login attempt failed due incorrect credentials.");
			}
			log.trace("Verified the login credentials for user: " + this.userName);
		}
		// Check if the user a type is of the Account of the License Pool Manager 
		if(Core.isSubClassOf(Account.getType(), mxUser.getMendixObject().getType()))
		{
			Account account = Account.initialize(getContext(), mxUser.getMendixObject());
			LicensePool licensepool = account.getAccount_LicensePool();
			if(licensepool == null || licensepool.getConcurrentUserLimit() == UserLimit.Unlimited)
			{
				log.trace("User: " + this.userName +" may login because the License Pool isn't present or is set on unlimited");
				return currSession != null ? currSession : Core.initializeSession( mxUser, this.currentSessionId);
			} 
			
			List<Account> activeAccountList = getActivePoolAccounts(licensepool);
			int maxUsers = licensepool.getMaxConcurrentUsersSpecified();
			if(currSession != null)
				maxUsers = maxUsers + 1;
			if (maxUsers - activeAccountList.size() > 0 /*|| activeAccountList.contains(account)*/)
			{
				log.trace("User: " + this.userName +" may login because the licensepool hasn't reach the limit or the user is already logged in with another session.");
				return currSession != null ? currSession : Core.initializeSession( mxUser, this.currentSessionId);
			} else
			{
				Core.getLogger("LicensePoolManager")
				.info("Login failed for user "
						+ account.getName()
						+ " from licensepool "
						+ licensepool.getName()
						+ " as "
						+ activeAccountList.size()
						+ " out of "
						+ licensepool.getMaxConcurrentUsersSpecified()
						+ " available concurrent user licenses are currently in use.");		
				if(currSession != null)
					Core.logout(currSession);
				throw new AuthenticationRuntimeException("Login attempt failed due to maximum number of concurrent users reached for license pool.");
			}
		} else
		{
			return currSession != null ? currSession : Core.initializeSession( mxUser, this.currentSessionId);
		}					
	}
	
	private List<Account> getActivePoolAccounts(LicensePool licensepool) throws CoreException
	{
		List<Account> PoolUsersOnline = new ArrayList<Account>();
		// Check for each session is part of the LicensePool
		for (ISession session : Core.getActiveSessions())
		{
			IMendixObject user = session.getUser().getMendixObject();
			if (Core.isSubClassOf(Account.getType(), user.getType()))
			{
				Account loggedaccount = Account.initialize(this.context,user);
				if (loggedaccount.getAccount_LicensePool().equals(licensepool))
				{
					PoolUsersOnline.add(loggedaccount);
				}
			}
		}
		return PoolUsersOnline;
	}
}
