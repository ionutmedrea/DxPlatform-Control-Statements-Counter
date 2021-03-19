call mvn clean install
del control_stmt_counter.jar
copy target\Plugin_CES-1.0-SNAPSHOT-shaded.jar %cd%\Plugin_CES-1.0-SNAPSHOT-shaded.jar
ren Plugin_CES-1.0-SNAPSHOT-shaded.jar control_stmt_counter.jar