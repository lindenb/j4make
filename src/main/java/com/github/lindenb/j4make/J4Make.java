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
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.util.Arrays;
import java.util.List;

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
	

	public int instanceMain(final String args[])
		{
		FORMAT fmt=FORMAT.DOT;
		BufferedReader in = null;
		FileOutputStream fos=null;
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
			options.addOption(Option.builder("o").
					longOpt("out").
					desc("Output file (default stdout)").
					hasArg().
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
			GraphWriter gw = new DotWriter();
			switch(fmt)
				{
				case XML: gw = new XmlWriter(); break;
				case GEXF: gw = new GexfWriter(); break;
				case DOT: gw = new DotWriter(); break;
				case RDF: gw = new RdfWriter(); break;
				default: throw new IllegalArgumentException(""+fmt);
				}
			final OutputStream out;
			if(cmdLine.hasOption("o"))
				{
				final File fileout = new File(cmdLine.getOptionValue("o"));
				LOG.info("opening "+fileout);
				fos = new FileOutputStream(fileout);
				out = fos;
				}
			else
				{
				out = System.out;
				}
			
			gw.write(this.graph,out);
			
			out.flush();
			
			if(fos!=null) {
				fos.flush();
				fos.close();
				fos=null;
			}
			
			return 0;
		} catch (final Exception e) {
			LOG.error("FAILURE", e);
			return -1;
		} finally
		{
			if(fos!=null) try {fos.close();} catch(Exception er2) {}
		}
		}
	public static void main(final String args[])
		{
		System.exit(new J4Make().instanceMain(args));
		}
	}
