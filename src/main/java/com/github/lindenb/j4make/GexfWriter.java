/*
The MIT License (MIT)

Copyright (c) 2016 Pierre Lindenbaum

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
* 2016 creation

*/
package com.github.lindenb.j4make;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;

import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;

public class GexfWriter implements GraphWriter {
	public GexfWriter() {
	}

	@Override
	public void write(final Graph graph, final OutputStream out) throws IOException {
		try {
			final XMLOutputFactory xof = XMLOutputFactory.newFactory();
			final XMLStreamWriter w = xof.createXMLStreamWriter(out, "UTF-8");
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
    		for(final Target t: graph.getTargets())
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
    		for(final Target t: graph.getTargets())
 	            {
 	           for(final Target c:t.getPrerequisites())
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
			out.flush();
		} catch (XMLStreamException err) {
			throw new IOException(err);
		}
	}
}
