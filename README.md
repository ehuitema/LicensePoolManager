# LicensePoolManager
 
The LicensePoolManager originates from the appstore, but this module has been enhanced to 1) perform user checks (like existing, blocked, active etc) on login and 2) work correctly in combination with the PersistentLogin module. The enhancements are technical only, functional it is identical to the Appstore module.
 
### Typical usage scenario
In case user management needs to be separated into multiple pools where each pool has a total number of users and a total number of concurrent users. Typically used in multitenant situations.
Always use this in favor of the appstore module due to its enhancements. Typically used in multi-tenant applications.
 
### Features and limitations
* Since it is an extract from the appstore, future improvements/ changes from the appstore will not be automatically incorporated.
 
# Dependencies
* Mendix 5.21.1
 
# Installation
### Prerequisities
 
### Installation steps
* Import the module **LicensePoolManager** in your project
* Refer to the documentation of theLicensePoolManager from the appstore fur further info
 
# Getting Started
 
### configuration / how to use
* Refer to the documentation of the LicensePoolManager from the appstore fur further info and configuration.
 
# Remarks
### Background - how it works
The implementation uses the concept of "custom loginhandler" ( [Explained here](https://bartgroot.nl/mendix/custom-checks-on-login/) ) to check the number of logged in/ assigned users to a licensepool when a user logs in. The class LoginHandler takes care of the licensepool logic but lacked a few features.
1. Unfortunately, the default useraccount checks (e.g. if an account is blocked, etc) have not been implemented in the std. module. These checks are now added to the LoginHandler class.
2. In order to work together with the PersistentLogin module, some changes were made to the login logic in the LoginHandler class. The old code always created a session for the user if the licensepool checks were successful. Now the logic checks if the user already has a session (in case of persistent login) and executes the licensepool checks accordingly. 
 
# Known bugs
* A LicensePool admin is not able to view or edit Blocked and Active boolean values of a User. These are restricted by the System module and work as designed in Mendix.

# Links
 
# License
Licensed under the Apache license.
 
# Developers notes
* `git clone https://github.com/ehuitema/LicensePoolManager`
 
# Version history
