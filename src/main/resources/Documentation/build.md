Build
=====

This plugin can be built with Buck and Maven.


Buck
----

From gerrit source directory:

```
buck build plugins/avatars/external:avatars-external
```

You will find the `avatars-extrenal.jar` file in `buck-out/gen/plugins/avatars/external`.


Maven
-----

From the plugin directory:

```
mvn clean package
```
