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

public class XmlWriter implements GraphWriter {
	public XmlWriter() {
	}

	@Override
	public void write(final Graph graph, final OutputStream out) throws IOException {
		try {
			final XMLOutputFactory xof = XMLOutputFactory.newFactory();
			final XMLStreamWriter w = xof.createXMLStreamWriter(out, "UTF-8");
			w.writeStartDocument("UTF-8", "1.0");
			w.writeStartElement("make");

			for (final Target t : graph.getTargets()) {
				w.writeStartElement("target");
				w.writeAttribute("name", t.getName());

				final File targetFile = new File(t.getName());
				w.writeAttribute("exists", String.valueOf(targetFile.exists()));
				w.writeAttribute("file-size", String.valueOf(targetFile.exists() ? targetFile.length() : -1L));
				w.writeAttribute("timestamp", String.valueOf(targetFile.exists() ? targetFile.lastModified() : -1L));

				w.writeStartElement("prerequisites");
				for (final Target c : t.getPrerequisites()) {
					w.writeEmptyElement("prerequisite");
					w.writeAttribute("ref", c.getName());
				}
				w.writeEndElement();

				w.writeStartElement("shell");
				for (final String line : t.getShellLines()) {
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

		} catch (final XMLStreamException err) {
			throw new IOException(err);
		}
	}
}
