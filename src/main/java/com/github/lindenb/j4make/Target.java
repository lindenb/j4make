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

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * A target in a Makefile
 * @author lindenb
 *
 */
public abstract class Target
	{
	@SuppressWarnings("unused")
	private static final Logger LOG= LoggerFactory.getLogger(Graph.class);
	/** script file */
	protected List<String> shellLines = new ArrayList<>();
	/** target name as defined in the Makefile */
	private String targetName;
	/** all prerequistites */
	protected Set<Target> _prerequisites= new LinkedHashSet<>();
	/** user data */
	private Map<String, Object> userData=null;
	
	Target(final String targetName)
		{
		this.targetName = targetName;
		}
	
	public Set<Target> getPrerequisites() {
		return Collections.unmodifiableSet(_prerequisites);
	}
	
	@Override
	public int hashCode() {
		return targetName.hashCode();
		}
	
	/** name of the target, as declared in the makefile */
	public String getName()
		{
		return this.targetName;
		}
	
	
	/** get graph owner */
	public abstract Graph getGraph();
	
	/** get node id */
	public abstract long getNodeId();
	
	@Override
	public boolean equals(Object obj) {
		if(obj == this) return true;
		if(obj == null || !(obj instanceof Target)) return false;
		final Target other = Target.class.cast(obj);
		return getName().equals(other.getName()) &&
				getNodeId()==other.getNodeId() && 
				getGraph()==other.getGraph()
				;
		}
	
	public List<String> getShellLines() {
		return shellLines;
	}
	
	public Object getUserData(final String key) {
		return userData==null?null:userData.get(key);
	}
	
	public void putUserData(final String key,final Object value) {
		if(this.userData==null) this.userData=new HashMap<String, Object>();
		this.userData.put(key, value);
	}
	
	@Override
	public String toString() {
		return getName();
		}

	}
