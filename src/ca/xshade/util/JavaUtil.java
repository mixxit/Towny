package ca.xshade.util;

public class JavaUtil {
	
	/**
	 * Recursively check if the interface inherits the super interface. Returns false if not an interface. Returns true if sup = sub.
	 * 
	 * @param sup The class of the interface you think it is a subinterface of.
	 * @param sub The possible subinterface of the super interface.
	 * @return true if it is a subinterface.
	 */
	
	@SuppressWarnings("rawtypes")
	public static boolean isSubInterface(Class sup, Class sub) {
		if (sup.isInterface() && sub.isInterface()) {
			if (sup.equals(sub))
				return true;
			for (Class c : sub.getInterfaces())
				if (isSubInterface(sup, c))
					return true;
		}
		return false;
	}
}
