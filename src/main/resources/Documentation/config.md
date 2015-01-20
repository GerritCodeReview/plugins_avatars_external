External URL Avatar Plugin Configuration
========================================


Options:

  * `avatar.url` - the location of avatar images containing `%s`, which will then be replaced by the `username`. Required.
  * `avatar.changeUrl` - the URL shown in Gerrit's user settings to tell the user, where the avatar can be changed. Optional.

Example (to be added to `etc/gerrit.config`):

```
    [avatar]
        url = http://example.org/avatars/%s.jpg
        changeUrl = http://example.org/account.html
```

Please note that `http://` URLs will be automatically rewritten to `https://`, if `gerrit.canonicalWebUrl` uses HTTPS.