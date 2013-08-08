package aQute.bnd.deployer.helpers;

import java.util.*;

import org.junit.*;

import aQute.bnd.service.*;

@Ignore
public class MockRegistry implements Registry {

	private final Set<Object>	plugins	= new HashSet<Object>();

	public void addPlugin(Object plugin) {
		plugins.add(plugin);
	}

	public <T> List<T> getPlugins(Class<T> clazz) {
		List<T> l = new ArrayList<T>();
		for (Object plugin : plugins) {
			if (clazz.isInstance(plugin))
				l.add(clazz.cast(plugin));
		}
		return l;
	}

	public <T> T getPlugin(Class<T> clazz) {
		for (Object plugin : plugins) {
			if (clazz.isInstance(plugin))
				return clazz.cast(plugin);
		}
		return null;
	}

}
