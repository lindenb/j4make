SHELL=/bin/bash
this.makefile=$(lastword $(MAKEFILE_LIST))
this.dir=$(dir $(realpath ${this.makefile}))
src.dir=${this.dir}src/main/java
generated.dir=${this.dir}src/main/generated-sources
tmp.dir=${this.dir}_tmp
tmp.mft=${tmp.dir}/META-INF/MANIFEST.MF
export dist.dir?=${this.dir}dist
ANT?=ant
JAVAC?=javac
JAVA?=java
JAR?=jar
XJC?=xjc


lib.dir?=lib

EMPTY :=
SPACE := $(EMPTY) $(EMPTY)

commons.codec.jars = \
	$(lib.dir)/commons-codec/commons-codec/1.10/commons-codec-1.10.jar

slf4j.jars  = \
	$(lib.dir)/org/slf4j/slf4j-api/1.7.13/slf4j-api-1.7.13.jar


apache.commons.cli.jars  = \
	$(lib.dir)/commons-cli/commons-cli/1.3.1/commons-cli-1.3.1.jar

all_maven_jars = $(sort   ${apache.commons.cli.jars} ${slf4j.jars} ${commons.codec.jars} )

define compile-cmd

## 1 : target name
## 2 : qualified main class name
## 3 : other deps

$(1)  : ${htsjdk.jars} \
		$(addsuffix .java,$(addprefix ${src.dir}/,$(subst .,/,$(2)))) \
		$(3) $${apache.commons.cli.jars} $${slf4j.jars}
	mkdir -p ${tmp.dir}/META-INF ${generated.dir}/java/$(dir $(subst .,/,$(2))) ${dist.dir}
	#compile
	${JAVAC} -d ${tmp.dir} -g -classpath "$$(subst $$(SPACE),:,$$(filter %.jar,$$^))" -sourcepath ${src.dir}:${generated.dir}/java $$(filter %.java,$$^)
	#create META-INF/MANIFEST.MF
	echo "Manifest-Version: 1.0" > ${tmp.mft}
	echo "Main-Class: $(2)" >> ${tmp.mft}
	echo "Class-Path: $$(realpath $$(filter %.jar,$$^)) ${dist.dir}/$(1).jar" | fold -w 71 | awk '{printf("%s%s\n",(NR==1?"": " "),$$$$0);}' >>  ${tmp.mft}
	echo -n "Git-Hash: " >> ${tmp.mft}
	$$(if $$(realpath .git/refs/heads/master),cat $$(realpath .git/refs/heads/master), echo "undefined")  >> ${tmp.mft} 
	echo -n "Compile-Date: " >> ${tmp.mft}
	date +%Y-%m-%d:%H-%m-%S >> ${tmp.mft}
	#create jar
	${JAR} cfm ${dist.dir}/$(1).jar ${tmp.mft}  -C ${tmp.dir} .
	#create bash executable
	echo '#!/bin/bash' > ${dist.dir}/$(1)
	echo '${JAVA} -Dfile.encoding=UTF8 -Xmx500m $(if ${http.proxy.host},-Dhtt.proxyHost=${http.proxy.host})  $(if ${http.proxy.port},-Dhtt.proxyPort=${http.proxy.por
	t}) -cp "$$(subst $$(SPACE),:,$$(realpath $$(filter %.jar,$$^))):${dist.dir}/$(1).jar" $(2) $$$$*' >> ${dist.dir}/$(1)
	chmod  ugo+rx ${dist.dir}/$(1)
	#cleanup
	rm -rf ${tmp.dir}

endef

.PHONY: all test

all: j4make

test: j4make
	${MAKE} -dnrB j4make | java -jar dist/j4make.jar -f XML  | xmllint --format -
	${MAKE} -dnrB j4make | java -jar dist/j4make.jar -f GEXF  | xmllint --format -

$(eval $(call compile-cmd,j4make,com.github.lindenb.j4make.J4Make,${commons.codec.jars}))

${all_maven_jars}  : 
	mkdir -p $(dir $@) && wget -O "$@" "http://central.maven.org/maven2/$(patsubst ${lib.dir}/%,%,$@)"
