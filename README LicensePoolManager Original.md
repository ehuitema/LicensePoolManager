LicensePoolManager Original README

# License Pool Manager, original
 
Author: Mendix, Sjoerd Breur\
Type: Module\
Latest Version: 2.1.0\
Package: filename LicensePoolManager.mpk

# Description
The License Pool Manager enables the definition of multiple license pools on a single server. These license pools have their own specific limits:

Named users. If these are set to unlimited, named users can be created until the server reaches its limit. If a limit is specified, new named users can no longer be created in the license pool. 
Concurrent users. If these are set to be unlimited, users from this license pool can log in until the server reaches its limit. If you choose to specify a limit, users will be unable to log in once the maximum number of concurrent users is reached for the license pool. 

License pools are defined by a server administrator, whereas a pool administrator role exists to manage the membership of the indidividual license pools. Users with this module role can add and remove members, but cannot change the details of the license pool.

### Typical usage scenario

A SaaS portal with different customer companies with their own user limits based on their contract with the service provider. 
Any application where you want to impose limits on individual departments or user groups. 

### Features and limitations
* Enables the specification of named and concurrent user limits for individual license pools. 
* Allows license pool administrators to see the active sessions for their specific license pool. (While allowing only the server administrator to see all active sessions) 

# Installation

The License Pool Manager is based on the Administration module, extending its functionality. To install it, remove the Administration module and add the License Pool Manager module to your project instead. For further information, see the general instructions under How to Install.

# Dependencies
* Mendix 4.1.0 Environment (See 'Older versions' for previous Mendix versions, starting at 2.5.3 ) 

# Configuration
Both the navigation and security aspects of the License Pool Manager have to be defined manually for the module to function properly, since these cannot be fully defined on a module level:

* A navigation menu needs to be defined to access the relevant forms. The following forms and microflows have been set up for use in this menu (Also refer to the navigation screenshot): 
  * _LicensePoolOverview_ServerAdmin_ will give users with ServerAdmin role a data grid showing all the license pools, allowing them to create, edit and manage these. 
  * _LicensePoolOverview_PoolAdmin_ will give users with the PoolAdmin role a data grid showing the license pools they are able to manage, showing both details about the pool limits and allowing them to manage the license pool. 
  * _Account_Overview_ will give users with the ServerAdmin role an overview of all the named users on the server. Functionality is similar to the regular Administration module, but has been extended to show the license pool which the users are part of. 
  * _OpenActiveSessions_ will allow users with the ServerAdmin role to review the active sessions. Similar to the regular Administration module, but also shows the license pool which the users are part of. 
  * _OpenActiveSessionsPoolAdmin_ shows users with the PoolAdmin rank the active sessions of users within the license pool they are administrator of. Aside from this limitation, functionality is similar to ActiveSessions. 
  * _ScheduledEvents_ gives an overview of the scheduled events on the server. Only available to users with the ServerAdmin role, and identical to the Administration module form. 
  * _RuntimeInstances_ is identical in functionality to the Administration module form and only available to users with the ServerAdmin role. 
  * _ManageMyAccount_ is identical to the Administration module. This microflow will open a window allowing users of all roles to change their account details. 
* The project user roles have to be set up to ensure users will have the correct module roles to allow the License Pool Manager module to function correctly: 
  * Server administrators who can define license pools and have administration rights for the entire server should have the Administrator role in the System module and the ServerAdmin role in the LicensePoolManager module.
  * License pool administrators who can manage individual license pools should have the Administrator role in the System module and the PoolAdmin role in the LicensePoolManager module. 
  * Regular users should have the User role in both the System and LicensePoolManager module. 
  * Make sure the administration roles have sufficient rights to manage other user roles.

The constants in the module need to be set to reflect the limits of your server. This will allow for proper validation of the license pool limits, and ensure that the sums of the specified limits of all license pools combined do not exceed your server's limits:

* _ServerNamedUserLimit_ is a boolean constant which indicates whether or not your server has a named user limit. Set this to 'true' if it does, or 'false' if named users on your server are not limited. 
* _ServerSpecifiedNamedUserLimit_ is an integer constant specifying your server's named user limit. Should ServerNamedUserLimit be 'false', this constant will not be used. 
* _ServerConcurrentUserLimit_ is a boolean constant which indicates whether or not your server has a concurrent user limit. Set this to 'true' if it does, or to 'false' if concurrent users are not limited on your server. 
* _ServerSpecifiedConcurrentUserLimit_ is an integer constant specifying your server's concurrent user limit. Should ServerConcurrentUserLimit be 'false', this constant will not be used. 

The microflow _AS_LoginListener_ has to be executed to start the login listener which enforces the concurrent user limits; because of this you should either make it your after startup microflow, or have it called by your own after startup microflow.

Finally, you will need to adapt the _User name/password incorrect_ system text. If a user tries to log in while his license pool is at its concurrent user limit, this system text will be shown. Because of this, it would be advisable to edit this system text to indicate that the failed login could be caused by either providing invalid credentials, or by the user's license pool being at its concurrent user limit.

# Using the License Pool Manager

### ServerAdmin
With the ServerAdmin role, you retain all the functionality of the Administrator role from the Administration module. However if the application was set up with the navigation items as specified under the Installation section, you will notice the following changes:

* The addition of a _License Pool Overview_ screen which lists all the license pools on the server. 
  * You can use the New and Edit buttons to create new license pools and edit their limits. When creating or editting a license pool, you can use the drop-down menu to select the pool administrator; this user will manage the license pool, adding and removing users. The user you select as pool administrator needs to be in that license pool, and should have a project role which has the PoolAdmin module role. 
  * You can use the Delete button to delete the license pool. 
Using the Manage button, you can add new users to the license pool or remove existing users. 
* While similar in functionality, the _Users_ screen will now also show the license pool which the users are in. When creating a new user, you will have to choose the license pool you want this user to be in, or validation checks will prevent this account from being committed. 
* The _Active Sessions_ screen will now also show the license pool users are in. 

### PoolAdmin
The PoolAdmin role is a new addition to the License Pool Manager compared to the Administration module. It's intended to be granted to users which are put in charge of managing a single license pool. Users with the PoolAdmin role will be able to see the license pool's limits, but are unable to change these. Users with PoolAdmin rank will have the following functionality available:

* A _License Pool Overview_ screen showing them the license pools they are the administrator of. (This will normally be one pool.) In this screen they can see the limitations of the selected license pool. They can use the Manage button to bring up a list of users in the license pool, as well as adding and removing users. Users added through this screen will automatically be assigned to this license pool. 
* An _Active Sessions_ screen allowing the PoolAdmin to see the active sessions of users in the license pools they are administrator of. 

### User
Users should not notice any changes compared to the Administration module. The only option available to them is My Account, which allows them to change some of their account details, identical to the Administration module.

# Known bugs

* If the login check to determine if a user is allowed to log in fails, it is performed a second time. This also results in a double log message reporting for which user and license pool the login attempt failed. 
* When a pool is full with users, the new user that wants to login, will always get the message "username and password are incorrect.". This text isn't editable with the module. 

# Frequently Asked Questions
