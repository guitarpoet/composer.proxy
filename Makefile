run:
	mvn tomcat:run
clean:
	rm -rf composer.proxy.db/
debug: clean
	MAVEN_OPTS="$(DEBUG)" mvn tomcat:run

.PHONY: run clean debug
