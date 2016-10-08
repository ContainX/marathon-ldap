# Marathon LDAP Plugin

[![Build Status](https://travis-ci.org/ContainX/marathon-ldap.svg?branch=master)](https://travis-ci.org/ContainX/marathon-ldap)
[![release](http://github-release-version.herokuapp.com/github/ContainX/marathon-ldap/release.svg?style=flat)](https://github.com/ContainX/marathon-ldap/releases/latest)
[![marathon](https://img.shields.io/badge/compatibility-marathon%201.1.1+-blue.svg)](https://mesosphere.github.io/marathon/)

A plugin for Mesosphere Marathon which authenticates users via UI/REST against an LDAP Server.  

### Features

- Allows authentication against pre-defined static users
- Supports LDAP and user memberships
- Allows membership/group level permissions support full CRUD against Apps and Groups including ID paths
- Tested against Marathon 1.1.1+
- Easy setup and JSON based configuration

### Installation

#### Downloading a the JAR binary

You can download the latest version of the JAR from our [GitHub Releases Page](https://github.com/ContainX/marathon-ldap/releases)

**Download the appropriate version based on your Marathon installation version**

| Marathon Version | Required Marathon-LDAP Version |
| ---------------- | ------------------------------ |
| 1.3+             | [1.3](https://github.com/ContainX/marathon-ldap/releases/tag/1.3) |
| 1.1              | [1.1](https://github.com/ContainX/marathon-ldap/releases/tag/1.1) |
| < 1.1            | [1.0](https://github.com/ContainX/marathon-ldap/releases/tag/1.0) |


#### Building plugin from source

You can clone this repository and quickly build the plugin JAR via source.

**Requirements:**

- Java 8+
- Maven 3+

**Building the JAR:**

The JAR is built under ```/target``` and is called ```marathon-ldap.jar```

```
mvn clean package
```

#### Installing on Marathon Nodes

Make plugin directory on the host and copy the JAR and ```plugin-conf.json```

```
mkdir -p /var/marathon/plugins
cp marathon-ldap.jar /var/marathon/plugins
cp plugin-conf.json /var/marathon/plugins
```

**Configure your LDAP connectivity**

Edit the `/var/marathon/plugins/plugin-conf.json` and configure the 
`plugins.authentication.configuration.ldap` section appropriately for your environment.  
An annotated version of the example that ships with the plugin is shown below (don't add 
comments to the one you deploy)

```
"ldap": {
    /*
     * the url property specifies the server, port and SSL setting of your directory.
     * Default port is 389 for plaintext or STARTTLS, and 636 for SSL.  If you want 
     * SSL, specify the protocol as 'ldaps:' rather than 'ldap:'
     */
    "url": "ldap://my.ldapserver.local:389",

    /*
     * base represents the domain your directory authenticates.  A domain of
     * example.com would normally be expressed in the form below, although note
     * that there is not necessarily a direct correlation between domains that 
     * might be part of an email address or username and the baseDN of the 
     * directory server.
     */
    "base": "dc=example,dc=com",

    /*
     * The dn property tells the plugin how to format a distinguished name for a user
     * that you want to authenticate.  The string {username} MUST exist in here and 
     * will be replaced by whatever the user submits as "username" in the login dialog.
     *
     * When the plugin calculates the DN to use to attempt authentication, it will
     * take the interpolated value here, suffixed with the userSubTree (if defined)
     * and the base property.  For example, the settings here and a submitted username
     * of 'fred' would cause a bind attempt using 'dn=uid=fred,ou=People,dc=example,dc=com'
     */
    "dn": "uid={username}",
    
    /*
     * The userSearch string is used following successful bind in order to obtain the
     * entire user record for the user logging in.  Similar to the 'dn' property above,
     * the supplied username will be substituted into the pattern below and the search
     * will be performed as shown against a search context of 'base' or (if defined)
     * the userSubTree section only.
     */
    "userSearch": "(&(uid={username})(objectClass=inetOrgPerson))",
    
    /* ---- the following properties are optional and can be left undefined ---- */
    
    /*
     * If you want to restrict the user searches and bind attempts to a particular 
     * org unit or other area of the LDAP directory, specify the sub tree here.  The
     * descriptions of earlier properties note where this definition may affect
     * behaviour.
     */
    "userSubTree": "ou=People",
    
    /*
     * If your group memberships are specified by using "memberOf" attributes on the
     * user record, you don't need the following.  However, if your groups are defined 
     * as separate entities and membership is denoted by having all the usernames 
     * inside the group, then you do.  This is common for posixGroup type groups.
     * Specify the 'groupSearch' property as a pattern to find all groups that the 
     * user is a member of.
     */
    "groupSearch": "(&(memberUid={username})(objectClass=posixGroup))",
    
    /*
     * Similar to userSubTree but for the group entities
     */
    "groupSubTree": "ou=Group"
}
```

**Configure Marathon**

Depending on your environment your Marathon configuration is either using files per option, typically found under ```/etc/marathon/conf``` or options are being passed in via the service.

The two options that need to be set (assuming you used /var/marathon/plugins) above are:

```
plugin_dir = /var/marathon/plugins
plugin_conf = /var/marathon/plugins/plugin-conf.json
```

**Restart Marathon**

### License

This software is licensed under the Apache 2 license, quoted below.

Copyright 2016 Jeremy Unruh / ContainX

Licensed under the Apache License, Version 2.0 (the "License"); you may not
use this file except in compliance with the License. You may obtain a copy of
the License at

    http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
License for the specific language governing permissions and limitations under
the License.
