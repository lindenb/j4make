/*
The MIT License (MIT)

Copyright (c) 2015 Pierre Lindenbaum

Permission is hereby granted, free of charge, to any person obtaining a copy
of this software and associated documentation files (the "Software"), to deal
in the Software without restriction, including without limitation the rights
to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
copies of the Software, and to permit persons to whom the Software is
furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all
copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
SOFTWARE.


History:
* 2015 creation

*/
package com.github.lindenb.j4make;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintStream;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/** main application */
public class J4Make
	{
	private static final Logger LOG= LoggerFactory.getLogger(J4Make.class);
	/** current graph */
	private Graph graph = null;
	/** output format */
	private enum FORMAT {XML,DOT,GEXF,RDF};
	
	private J4Make() {
	}
	
	/** current version */
	public String getVersion()
	{
		return "1.0";
	}
	
	/** write graph as dot */
	private void writeAsDot(PrintStream out)
	{
        out.println("digraph G {");
        for(Target t:this.graph.getTargets())
            {
            out.println("n"+t.getNodeId()+"[label=\""+t.getName()+"\"];");
            }
        for(Target t:this.graph.getTargets())
            {
            for(final Target c:t.getPrerequisites())
                {
            	out.println("n"+c.getNodeId()+" -> n"+t.getNodeId()+";");
                }
            }
        out.println("}");
	}

	/** write graph as gephi/gexf */
	private void writeAsGexf(OutputStream out) throws XMLStreamException
		{
			XMLOutputFactory xof = XMLOutputFactory.newFactory();
			XMLStreamWriter w = xof.createXMLStreamWriter(out, "UTF-8");
			w.writeStartDocument("UTF-8", "1.0");
    		w.writeStartElement("gexf");
    		w.writeAttribute("xmlns", "http://www.gexf.net/1.2draft");
    		w.writeAttribute("version", "1.2");
    		
    		
    		/* meta */
    		w.writeStartElement("meta");
    			w.writeStartElement("creator");
    			  w.writeCharacters("Pierre Lindenbaum");
    			w.writeEndElement();
    			w.writeStartElement("description");
    			  w.writeCharacters("Made with j4make https://github.com/lindenb/j4make");
    			w.writeEndElement();
    		w.writeEndElement();
    		
    		/* graph */
    		w.writeStartElement("graph");
    		w.writeAttribute("mode", "static");
    		w.writeAttribute("defaultedgetype", "directed");
    		
    		
    		/* attributes */
    		w.writeStartElement("attributes");
    		w.writeAttribute("class","node");
    		w.writeAttribute("mode","static");
    			w.writeStartElement("attribute");
    				w.writeAttribute("id", "exists");
    				w.writeAttribute("title", "exists");
    				w.writeAttribute("type", "boolean");
    				w.writeStartElement("default");
    				w.writeCharacters("false");
    				w.writeEndElement();
    			w.writeEndElement();
    			w.writeStartElement("attribute");
					w.writeAttribute("id", "filesize");
					w.writeAttribute("title", "filesize");
					w.writeAttribute("type", "int");
					w.writeStartElement("default");
					w.writeCharacters("-1");
					w.writeEndElement();
				w.writeEndElement();
    			w.writeStartElement("attribute");
					w.writeAttribute("id", "timestamp");
					w.writeAttribute("title", "timestamp");
					w.writeAttribute("type", "int");
					w.writeStartElement("default");
					w.writeCharacters("-1");
					w.writeEndElement();
				w.writeEndElement();
   			
    		
    		
    		w.writeEndElement();//attributes
    		
    		/* nodes */
    		w.writeStartElement("nodes");
    		for(final Target t:this.graph.getTargets())
    			{
    			w.writeStartElement("node");
    			w.writeAttribute("id", String.valueOf(t.getNodeId()));
    			w.writeAttribute("label", t.getName()); 
    			final File targetFile = new File(t.getName());
    			w.writeStartElement("attvalues");
    				w.writeEmptyElement("attvalue");
    					w.writeAttribute("for", "exists");
    					w.writeAttribute("value",String.valueOf(targetFile.exists()));
        			w.writeEmptyElement("attvalue");
    					w.writeAttribute("for", "filesize");
    					w.writeAttribute("value",String.valueOf(targetFile.exists()?targetFile.length():-1L));
        			w.writeEmptyElement("attvalue");
    					w.writeAttribute("for", "timestamp");
    					w.writeAttribute("value",String.valueOf(targetFile.exists()?targetFile.lastModified():-1L));
    			w.writeEndElement();//attvalues
    			w.writeEndElement();
    			}

    		w.writeEndElement();//nodes
    		
    		/* edges */
    		int relid=0;
    		w.writeStartElement("edges");
    		for(Target t:this.graph.getTargets())
 	            {
 	           for(Target c:t.getPrerequisites())
	                {
	    			w.writeEmptyElement("edge");
	    			w.writeAttribute("id", "E"+(++relid));
	    			w.writeAttribute("type","directed");
	    			w.writeAttribute("source",String.valueOf(c.getNodeId()));
	    			w.writeAttribute("target",String.valueOf(t.getNodeId()));
	                }
    			}

    		w.writeEndElement();//edges

    		w.writeEndElement();//graph
    		
    		w.writeEndElement();//gexf
    		w.writeEndDocument();
    		w.flush();

			w.writeEndDocument();
			w.flush();
		}
	
	/** write graph as XML */
	private void writeAsXml(OutputStream out) throws XMLStreamException
		{
			XMLOutputFactory xof = XMLOutputFactory.newFactory();
			XMLStreamWriter w = xof.createXMLStreamWriter(out, "UTF-8");
			w.writeStartDocument("UTF-8", "1.0");
			w.writeStartElement("make");
	
			for(final Target t:this.graph.getTargets())
			{
			w.writeStartElement("target");
			w.writeAttribute("name", t.getName());
			
			final File targetFile = new File(t.getName());
			w.writeAttribute("exists",String.valueOf(targetFile.exists()));
			w.writeAttribute("file-size",String.valueOf(targetFile.exists()?targetFile.length():-1L));
			w.writeAttribute("timestamp",String.valueOf(targetFile.exists()?targetFile.lastModified():-1L));

			
			
			w.writeStartElement("prerequisites");
			for(final Target c:t.getPrerequisites())
				{
				w.writeEmptyElement("prerequisite");
				w.writeAttribute("ref",c.getName());
				}
			w.writeEndElement();
			
			w.writeStartElement("shell");
			for(final String line:t.getShellLines())
				{
				w.writeStartElement("p");
				w.writeCharacters(line);
				w.writeEndElement();
				}
			w.writeEndElement();
			
			
			w.writeEndElement();
			}
		w.writeEndElement();
		w.writeEndDocument();
		w.flush();
		}
	
	/** write graph as XML */
	private void writeAsRDF(OutputStream out) throws XMLStreamException
		{
		final SimpleDateFormat ISO8601DATEFORMAT = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZ");
		final String RDF="http://www.w3.org/1999/02/22-rdf-syntax-ns#";
		final String NS="https://github.com/lindenb/j4make#";

		
			XMLOutputFactory xof = XMLOutputFactory.newFactory();
			XMLStreamWriter w = xof.createXMLStreamWriter(out, "UTF-8");
			w.writeStartDocument("UTF-8", "1.0");
			w.writeStartElement("rdf","RDF",RDF);
			w.writeNamespace("rdf", RDF);
			w.writeNamespace("m", NS);
			
	
			for(final Target t:this.graph.getTargets())
			{
			File tFile = new File(t.getName());
			w.writeStartElement("m","Target",NS);
			w.writeAttribute("rdf",RDF,"about", tFile.toURI().toString());
			if(tFile.exists() && tFile.isFile() && tFile.canRead())
				{
				long fSIze = tFile.length();
				Date lastModified = new Date(tFile.lastModified());
				w.writeStartElement("m", "length", NS);
				w.writeAttribute("rdf", RDF, "datatype",
						"http://www.w3.org/2001/XMLSchema#unsignedLong");
				w.writeCharacters(String.valueOf(fSIze));
				w.writeEndElement();
				
				w.writeStartElement("m", "lastModified", NS);
				w.writeAttribute("rdf", RDF, "datatype",
						"http://www.w3.org/2001/XMLSchema#dateTime");
				w.writeCharacters(ISO8601DATEFORMAT.format(lastModified));
				w.writeEndElement();
				}
			
			
			for(final Target c:t.getPrerequisites())
				{
				w.writeEmptyElement("m","hasPrerequisite",NS);
				w.writeAttribute("rdf",RDF,"resource",
						new File(c.getName()).toURI().toString()
						);
				}
			
			w.writeStartElement("m","shell",NS);
			for(final String line:t.getShellLines())
				{
				w.writeCharacters(line);
				w.writeCharacters("\n");
				}
			w.writeEndElement();
			
			w.writeEndElement();
			}
		w.writeEndElement();
		w.writeEndDocument();
		w.flush();
		}

	public int instanceMain(final String args[])
		{
		FORMAT fmt=FORMAT.DOT;
		BufferedReader in = null;
		try {
			final Options options = new Options();
			options.addOption(Option.builder("f").
					longOpt("format").
					argName("FORMAT").
					desc("output format one of "+Arrays.toString(FORMAT.values())).
					hasArg()
					.build()
					);
			options.addOption(Option.builder("h").
					longOpt("help").
					desc("Print Help and exit").
					hasArg(false).
					build()
					);
			options.addOption(Option.builder("v").
					longOpt("version").
					desc("Print version and exit").
					hasArg(false).
					build()
					);
			final CommandLineParser parser = new DefaultParser();
			final CommandLine cmdLine = parser.parse(options, args);
			final List<String> arglist = cmdLine.getArgList();
			
			if(cmdLine.hasOption("h"))
				{
				HelpFormatter format = new HelpFormatter();
				format.printHelp("j4make. Pierre Lindenbaum PhD 2015 @yokofakun", options);
				return 0;
				}
			if(cmdLine.hasOption("v"))
				{
				System.out.println("Version: "+getVersion());
				return 0;
				}
			if(cmdLine.hasOption("f"))
				{
				fmt = FORMAT.valueOf(cmdLine.getOptionValue("f").toUpperCase());
				}
			
			if(arglist.isEmpty())
				{
				LOG.debug("parsing stdin");
				in = new BufferedReader(new InputStreamReader(System.in));
				}	
			else if(arglist.size()==1)
				{
				LOG.debug("parsing "+arglist.get(0));
				in = new BufferedReader(new FileReader(arglist.get(0)));
				}
			else
				{
				LOG.error("Illegal number of arguments.");
				return -1;
				}
			this.graph = Graph.parse(in);
			in.close();
			LOG.debug("parsing done");
			switch(fmt)
				{
				case XML: writeAsXml(System.out); break;
				case GEXF: writeAsGexf(System.out); break;
				case DOT: writeAsDot(System.out); break;
				case RDF: writeAsRDF(System.out);break;
				default: throw new IllegalArgumentException(""+fmt);
				}
			return 0;
		} catch (Exception e) {
			LOG.error("FAILURE", e);
			return -1;
		} finally
		{
			
		}
		}
	public static void main(final String args[])
		{
		System.exit(new J4Make().instanceMain(args));
		}
	}
