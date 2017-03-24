package licensepoolmanager.actions;

import com.mendix.core.Core;
import com.mendix.core.action.user.LoginAction;
import com.mendix.logging.ILogNode;
import com.mendix.systemwideinterfaces.core.UserActionListener;

public class LoginListener extends UserActionListener<LoginAction>
{
	private static ILogNode log = Core.getLogger("LicensePoolManager");
	
	public LoginListener(Class<LoginAction> arg0)
	{
		super(arg0);
	}

	@Override
	public boolean check(LoginAction login)
	{
		log.debug("Check LoginListener: " + login.getUserName());
		return true;		
	}
}
