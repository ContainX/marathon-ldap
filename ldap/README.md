## openldap schemas

#### memberof overlay

If you want to use the `memberof` attribute, you need to install the overlay:

##### openldap >= 2.4

```
 ldapadd -Y EXTERNAL -H ldapi:/// -f overlay.ldif
 ldapadd -Y EXTERNAL -H ldapi:/// -f refint.ldif
```

#### marathon schema
Installing the marathon schema is required if you want to store permissions in openldap.

##### openldap >= 2.4

`ldapadd -Y EXTERNAL -H ldapi:/// -f marathon.ldif`