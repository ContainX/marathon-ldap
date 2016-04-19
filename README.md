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
