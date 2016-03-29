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
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;

public class RdfWriter implements GraphWriter {
	public RdfWriter() {
	}

	@Override
	public void write(final Graph graph, final OutputStream out) throws IOException {
		try {
			final SimpleDateFormat ISO8601DATEFORMAT = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZ");
			final String RDF = "http://www.w3.org/1999/02/22-rdf-syntax-ns#";
			final String NS = "https://github.com/lindenb/j4make#";

			final XMLOutputFactory xof = XMLOutputFactory.newFactory();
			final XMLStreamWriter w = xof.createXMLStreamWriter(out, "UTF-8");
			w.writeStartDocument("UTF-8", "1.0");
			w.writeStartElement("rdf", "RDF", RDF);
			w.writeNamespace("rdf", RDF);
			w.writeNamespace("m", NS);

			for (final Target t : graph.getTargets()) {
				final File tFile = new File(t.getName());
				w.writeStartElement("m", "Target", NS);
				w.writeAttribute("rdf", RDF, "about", tFile.toURI().toString());
				if (tFile.exists() && tFile.isFile() && tFile.canRead()) {
					long fSIze = tFile.length();
					Date lastModified = new Date(tFile.lastModified());
					w.writeStartElement("m", "length", NS);
					w.writeAttribute("rdf", RDF, "datatype", "http://www.w3.org/2001/XMLSchema#unsignedLong");
					w.writeCharacters(String.valueOf(fSIze));
					w.writeEndElement();

					w.writeStartElement("m", "lastModified", NS);
					w.writeAttribute("rdf", RDF, "datatype", "http://www.w3.org/2001/XMLSchema#dateTime");
					w.writeCharacters(ISO8601DATEFORMAT.format(lastModified));
					w.writeEndElement();
				}

				for (final Target c : t.getPrerequisites()) {
					w.writeEmptyElement("m", "hasPrerequisite", NS);
					w.writeAttribute("rdf", RDF, "resource", new File(c.getName()).toURI().toString());
				}

				w.writeStartElement("m", "shell", NS);
				for (final String line : t.getShellLines()) {
					w.writeCharacters(line);
					w.writeCharacters("\n");
				}
				w.writeEndElement();

				w.writeEndElement();
			}
			w.writeEndElement();
			w.writeEndDocument();
			w.flush();
			out.flush();
		} catch (XMLStreamException err) {
			throw new IOException(err);
		}
	}

}
