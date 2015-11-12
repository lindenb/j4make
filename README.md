# j4make

java Makefile Graph parser 

# Compilation

```
make
```

an executable jar is created in `dist/j4make.jar`

## Usage

```
 -f,--format <FORMAT>   output format one of [XML, DOT, GEXF]
 -h,--help              Print Help and exit
 -v,--version           Print version and exit
```

## Example

### Dot output

```
$ make -ndBr | java -jar dist/j4make.jar 2> /dev/null 
digraph G {
n3[label="all"];
n8[label="lib/org/slf4j/slf4j-api/1.7.13/slf4j-api-1.7.13.jar"];
n6[label="lib/commons-codec/commons-codec/1.10/commons-codec-1.10.jar"];
n2[label="Makefile"];
n4[label="j4make"];
n1[label="<ROOT>"];
n7[label="lib/commons-cli/commons-cli/1.3.1/commons-cli-1.3.1.jar"];
n5[label="/home/lindenb/src/j4make/src/main/java/com/github/lindenb/j4make/J4Make.java"];
n4 -> n3;
n5 -> n4;
n6 -> n4;
n7 -> n4;
n8 -> n4;
n2 -> n1;
n3 -> n1;
}
```

### XML output

```
$ make -ndBr | java -jar dist/j4make.jar --format XML 2> /dev/null   | xmllint --format -
<?xml version="1.0" encoding="UTF-8"?>
<make>
  <target name="all">
    <prerequisites>
      <prerequisite ref="j4make"/>
    </prerequisites>
    <shell/>
  </target>
  <target name="lib/org/slf4j/slf4j-api/1.7.13/slf4j-api-1.7.13.jar">
    <prerequisites/>
    <shell>
      <p>mkdir -p lib/org/slf4j/slf4j-api/1.7.13/ &amp;&amp; wget -O "lib/org/slf4j/slf4j-api/1.7.13/slf4j-api-1.7.13.jar" "http://central.maven.org/maven2/org/slf4j/slf4j-api/1.7.13/slf4j-api-1.7.13.jar"</p>
    </shell>
  </target>
  <target name="lib/commons-codec/commons-codec/1.10/commons-codec-1.10.jar">
    <prerequisites/>
    <shell>
      <p>mkdir -p lib/commons-codec/commons-codec/1.10/ &amp;&amp; wget -O "lib/commons-codec/commons-codec/1.10/commons-codec-1.10.jar" "http://central.maven.org/maven2/commons-codec/commons-codec/1.10/commons-codec-1.10.jar"</p>
    </shell>
  </target>
  <target name="Makefile">
    <prerequisites/>
    <shell/>
  </target>
  <target name="j4make">
    <prerequisites>
      <prerequisite ref="/home/lindenb/src/j4make/src/main/java/com/github/lindenb/j4make/J4Make.java"/>
      <prerequisite ref="lib/commons-codec/commons-codec/1.10/commons-codec-1.10.jar"/>
      <prerequisite ref="lib/commons-cli/commons-cli/1.3.1/commons-cli-1.3.1.jar"/>
      <prerequisite ref="lib/org/slf4j/slf4j-api/1.7.13/slf4j-api-1.7.13.jar"/>
    </prerequisites>
    <shell>
      <p>mkdir -p /home/lindenb/src/j4make/_tmp/META-INF /home/lindenb/src/j4make/src/main/generated-sources/java/com/github/lindenb/j4make/ /home/lindenb/src/j4make/dist</p>
      <p>#compile</p>
      <p>javac -d /home/lindenb/src/j4make/_tmp -g -classpath "lib/commons-codec/commons-codec/1.10/commons-codec-1.10.jar:lib/commons-cli/commons-cli/1.3.1/commons-cli-1.3.1.jar:lib/org/slf4j/slf4j-api/1.7.13/slf4j-api-1.7.13.jar" -sourcepath /home/lindenb/src/j4make/src/main/java:/home/lindenb/src/j4make/src/main/generated-sources/java /home/lindenb/src/j4make/src/main/java/com/github/lindenb/j4make/J4Make.java</p>
      <p>#create META-INF/MANIFEST.MF</p>
      <p>echo "Manifest-Version: 1.0" &gt; /home/lindenb/src/j4make/_tmp/META-INF/MANIFEST.MF</p>
      <p>echo "Main-Class: com.github.lindenb.j4make.J4Make" &gt;&gt; /home/lindenb/src/j4make/_tmp/META-INF/MANIFEST.MF</p>
      <p>echo "Class-Path: /home/lindenb/src/j4make/lib/commons-codec/commons-codec/1.10/commons-codec-1.10.jar /home/lindenb/src/j4make/lib/commons-cli/commons-cli/1.3.1/commons-cli-1.3.1.jar /home/lindenb/src/j4make/lib/org/slf4j/slf4j-api/1.7.13/slf4j-api-1.7.13.jar /home/lindenb/src/j4make/dist/j4make.jar" | fold -w 71 | awk '{printf("%s%s\n",(NR==1?"": " "),$0);}' &gt;&gt;  /home/lindenb/src/j4make/_tmp/META-INF/MANIFEST.MF</p>
      <p>echo -n "Git-Hash: " &gt;&gt; /home/lindenb/src/j4make/_tmp/META-INF/MANIFEST.MF</p>
      <p>cat /home/lindenb/src/j4make/.git/refs/heads/master  &gt;&gt; /home/lindenb/src/j4make/_tmp/META-INF/MANIFEST.MF </p>
      <p>echo -n "Compile-Date: " &gt;&gt; /home/lindenb/src/j4make/_tmp/META-INF/MANIFEST.MF</p>
      <p>date +%Y-%m-%d:%H-%m-%S &gt;&gt; /home/lindenb/src/j4make/_tmp/META-INF/MANIFEST.MF</p>
      <p>#create jar</p>
      <p>jar cfm /home/lindenb/src/j4make/dist/j4make.jar /home/lindenb/src/j4make/_tmp/META-INF/MANIFEST.MF  -C /home/lindenb/src/j4make/_tmp .</p>
      <p>#create bash executable</p>
      <p>echo '#!/bin/bash' &gt; /home/lindenb/src/j4make/dist/j4make</p>
      <p>echo 'java -Dfile.encoding=UTF8 -Xmx500m    -cp "/home/lindenb/src/j4make/lib/commons-codec/commons-codec/1.10/commons-codec-1.10.jar:/home/lindenb/src/j4make/lib/commons-cli/commons-cli/1.3.1/commons-cli-1.3.1.jar:/home/lindenb/src/j4make/lib/org/slf4j/slf4j-api/1.7.13/slf4j-api-1.7.13.jar:/home/lindenb/src/j4make/dist/j4make.jar" com.github.lindenb.j4make.J4Make $*' &gt;&gt; /home/lindenb/src/j4make/dist/j4make</p>
      <p>chmod  ugo+rx /home/lindenb/src/j4make/dist/j4make</p>
      <p>#cleanup</p>
      <p>rm -rf /home/lindenb/src/j4make/_tmp</p>
    </shell>
  </target>
  <target name="&lt;ROOT&gt;">
    <prerequisites>
      <prerequisite ref="Makefile"/>
      <prerequisite ref="all"/>
    </prerequisites>
    <shell/>
  </target>
  <target name="lib/commons-cli/commons-cli/1.3.1/commons-cli-1.3.1.jar">
    <prerequisites/>
    <shell>
      <p>mkdir -p lib/commons-cli/commons-cli/1.3.1/ &amp;&amp; wget -O "lib/commons-cli/commons-cli/1.3.1/commons-cli-1.3.1.jar" "http://central.maven.org/maven2/commons-cli/commons-cli/1.3.1/commons-cli-1.3.1.jar"</p>
    </shell>
  </target>
  <target name="/home/lindenb/src/j4make/src/main/java/com/github/lindenb/j4make/J4Make.java">
    <prerequisites/>
    <shell/>
  </target>
</make>
```

### Gexf output

```
$ make -ndBr | java -jar dist/j4make.jar --format gexf 2> /dev/null   | xmllint --format -
<?xml version="1.0" encoding="UTF-8"?>
<gexf xmlns="http://www.gexf.net/1.2draft" version="1.2">
  <meta>
    <creator>Pierre Lindenbaum</creator>
    <description>Made with j4make https://github.com/lindenb/j4make</description>
  </meta>
  <graph mode="static" defaultedgetype="directed">
    <attributes class="node" mode="static"/>
    <nodes>
      <node id="3" label="all"/>
      <node id="8" label="lib/org/slf4j/slf4j-api/1.7.13/slf4j-api-1.7.13.jar"/>
      <node id="6" label="lib/commons-codec/commons-codec/1.10/commons-codec-1.10.jar"/>
      <node id="2" label="Makefile"/>
      <node id="4" label="j4make"/>
      <node id="1" label="&lt;ROOT&gt;"/>
      <node id="7" label="lib/commons-cli/commons-cli/1.3.1/commons-cli-1.3.1.jar"/>
      <node id="5" label="/home/lindenb/src/j4make/src/main/java/com/github/lindenb/j4make/J4Make.java"/>
    </nodes>
    <edges>
      <edge id="E1" type="directed" source="4" target="3"/>
      <edge id="E2" type="directed" source="5" target="4"/>
      <edge id="E3" type="directed" source="6" target="4"/>
      <edge id="E4" type="directed" source="7" target="4"/>
      <edge id="E5" type="directed" source="8" target="4"/>
      <edge id="E6" type="directed" source="2" target="1"/>
      <edge id="E7" type="directed" source="3" target="1"/>
    </edges>
  </graph>
</gexf>
```

## Author

* Pierre Lindenbaum PhD

## License

MIT License

## See also

* https://github.com/lindenb/makefile2graph
