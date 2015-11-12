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
import java.io.FileReader;
import java.io.InputStreamReader;
import java.util.List;

import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamWriter;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.Options;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class J4Make
	{
	private static final Logger LOG= LoggerFactory.getLogger(J4Make.class);

	public int instanceMain(final String args[])
		{
		BufferedReader in = null;
		try {
			final Options options = new Options();
			final CommandLineParser parser = new DefaultParser();
			final CommandLine cmdLine = parser.parse(options, args);
			final List<String> arglist = cmdLine.getArgList();
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
			final Graph g = Graph.parse(in);
			in.close();
			LOG.debug("parsing done");
			XMLOutputFactory xof = XMLOutputFactory.newFactory();
			XMLStreamWriter w = xof.createXMLStreamWriter(System.out, "UTF-8");
			w.writeStartDocument("UTF-8", "1.0");
			w.writeStartElement("make");
			for(final Target t:g.getTargets())
				{
				w.writeStartElement("target");
				w.writeAttribute("name", t.getName());
				
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
			return 0;
		} catch (Exception e) {
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
