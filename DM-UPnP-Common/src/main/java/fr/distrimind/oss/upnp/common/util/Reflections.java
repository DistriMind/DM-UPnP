/*
 *     This library is free software; you can redistribute it and/or
 *     modify it under the terms of the GNU Lesser General Public
 *     License as published by the Free Software Foundation; either
 *     version 2.1 of the License.
 *
 *     This library is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 *     Lesser General Public License for more details.
 *
 *     You should have received a copy of the GNU Lesser General Public
 *     License along with this library; if not, write to the Free Software
 *     Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301  USA
 */

/*
 *     This library is free software; you can redistribute it and/or
 *     modify it under the terms of the GNU Lesser General Public
 *     License as published by the Free Software Foundation; either
 *     version 2.1 of the License.
 *
 *     This library is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 *     Lesser General Public License for more details.
 *
 *     You should have received a copy of the GNU Lesser General Public
 *     License along with this library; if not, write to the Free Software
 *     Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301  USA
 */

/*
 *     This library is free software; you can redistribute it and/or
 *     modify it under the terms of the GNU Lesser General Public
 *     License as published by the Free Software Foundation; either
 *     version 2.1 of the License.
 *
 *     This library is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 *     Lesser General Public License for more details.
 *
 *     You should have received a copy of the GNU Lesser General Public
 *     License along with this library; if not, write to the Free Software
 *     Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301  USA
 */

package fr.distrimind.oss.upnp.common.util;


import fr.distrimind.oss.flexilogxml.common.ReflectionTools;
import fr.distrimind.oss.flexilogxml.common.UtilClassLoader;

import java.lang.annotation.Annotation;
import java.lang.reflect.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Christian Bauer
 */
@SuppressWarnings("PMD.AvoidAccessibilityAlteration")
public class Reflections {

	// ####################

	public static Object invoke(Method method, Object target, Object... args) throws Exception {
		try {
			return method.invoke(target, args);
		}
		catch (IllegalArgumentException iae) {
			String message = "Could not invoke method by reflection: " + toString(method);
			if (args != null && args.length > 0) {
				message += " with parameters: (" + toClassNameString(", ", args) + ')';
			}
			message += " on: " + target.getClass().getName();
			throw new IllegalArgumentException(message, iae);
		}
		catch (InvocationTargetException ite) {
			if (ite.getCause() instanceof Exception) {
				throw (Exception) ite.getCause();
			} else {
				throw ite;
			}
		}

	}

	// ####################

	public static Object get(Field field, Object target) throws Exception {
		return ReflectionTools.getValue(field, target);
	}

	// ####################

	public static Method getMethod(Class<?> clazz, String name) throws NoSuchMethodException {
		return ReflectionTools.getMethod(false, clazz, name);
	}

	// ####################

	public static void set(Field field, Object target, Object value) throws IllegalAccessException {
		ReflectionTools.setValue(field, target, value);
	}

	// ####################

	public static String getMethodPropertyName(String methodName) {
		String methodPropertyName = null;
		if (methodName.startsWith("get")) {
			methodPropertyName = decapitalize(methodName.substring(3));
		} else if (methodName.startsWith("is")) {
			methodPropertyName = decapitalize(methodName.substring(2));
		} else if (methodName.startsWith("set")) {
			methodPropertyName = decapitalize(methodName.substring(3));
		}
		return methodPropertyName;
	}

	// ####################

	public static Method getGetterMethod(Class<?> clazz, String name) {
		for (Class<?> superClass = clazz; superClass != null && superClass != Object.class; superClass = superClass.getSuperclass()) {
			for (Method method : superClass.getDeclaredMethods()) {
				String methodName = method.getName();
				if (method.getParameterTypes().length != 0) continue;

				if (methodName.startsWith("get")) {
					if (decapitalize(methodName.substring(3)).equals(name))
						return method;
				} else if (methodName.startsWith("is")) {
					if (decapitalize(methodName.substring(2)).equals(name))
						return method;
				}
			}
		}
		return null;
	}

	// ####################

	public static List<Method> getMethods(Class<?> clazz, Class<? extends Annotation> annotation) {
		List<Method> methods = new ArrayList<>();
		for (Class<?> superClass = clazz; superClass != null && superClass != Object.class; superClass = superClass.getSuperclass()) {
			for (Method method : superClass.getDeclaredMethods()) {
				if (method.isAnnotationPresent(annotation)) {
					methods.add(method);
				}
			}
		}
		return methods;
	}

	// ####################

	public static Field getField(Class<?> clazz, String name) throws NoSuchFieldException {
		return ReflectionTools.getField(false, clazz, name);
	}

	// ####################

	public static List<Field> getFields(Class<?> clazz, Class<? extends Annotation> annotation) {
		List<Field> fields = new ArrayList<>();
		for (Class<?> superClass = clazz; superClass != null && superClass != Object.class; superClass = superClass.getSuperclass()) {
			for (Field field : superClass.getDeclaredFields()) {
				if (field.isAnnotationPresent(annotation)) {
					fields.add(field);
				}
			}
		}
		return fields;
	}

	// http://www.artima.com/weblogs/viewpost.jsp?thread=208860

	/**
	 * Get the actual type arguments a child class has used to extend a generic base class.
	 *
	 * @param baseClass  the base class
	 * @param childClass the child class
	 * @return a list of the raw classes for the actual type arguments.
	 */
	public static <T> List<Class<?>> getTypeArguments(
			Class<T> baseClass, Class<? extends T> childClass) {
		Map<Type, Type> resolvedTypes = new HashMap<>();
		Type type = childClass;
		// start walking up the inheritance hierarchy until we hit baseClass
		while (!getClass(type).equals(baseClass)) {
			if (type instanceof Class) {
				// there is no useful information for us in raw types, so just keep going.
				type = ((Class<?>) type).getGenericSuperclass();
			} else {
				ParameterizedType parameterizedType = (ParameterizedType) type;
				Class<?> rawType = (Class<?>) parameterizedType.getRawType();

				Type[] actualTypeArguments = parameterizedType.getActualTypeArguments();
				TypeVariable<?>[] typeParameters = rawType.getTypeParameters();
				for (int i = 0; i < actualTypeArguments.length; i++) {
					resolvedTypes.put(typeParameters[i], actualTypeArguments[i]);
				}

				if (!rawType.equals(baseClass)) {
					type = rawType.getGenericSuperclass();
				}
			}
		}

		// finally, for each actual type argument provided to baseClass, determine (if possible)
		// the raw class for that type argument.
		Type[] actualTypeArguments;
		if (type instanceof Class) {
			actualTypeArguments = ((Class<?>) type).getTypeParameters();
		} else {
			actualTypeArguments = ((ParameterizedType) type).getActualTypeArguments();
		}
		List<Class<?>> typeArgumentsAsClasses = new ArrayList<>();
		// resolve types by chasing down type variables.
		for (Type bt : actualTypeArguments) {
			Type baseType=bt;
			while (resolvedTypes.containsKey(baseType)) {
				baseType = resolvedTypes.get(baseType);
			}
			typeArgumentsAsClasses.add(getClass(baseType));
		}
		return typeArgumentsAsClasses;
	}

	/**
	 * Get the underlying class for a type, or null if the type is a variable type.
	 *
	 * @param type the type
	 * @return the underlying class
	 */
	public static Class<?> getClass(Type type) {
		if (type instanceof Class) {
			return (Class<?>) type;
		} else if (type instanceof ParameterizedType) {
			return getClass(((ParameterizedType) type).getRawType());
		} else if (type instanceof GenericArrayType) {
			Type componentType = ((GenericArrayType) type).getGenericComponentType();
			Class<?> componentClass = getClass(componentType);
			if (componentClass != null) {
				return Array.newInstance(componentClass, 0).getClass();
			} else {
				return null;
			}
		} else {
			return null;
		}
	}

	public static Object getAndWrap(Field field, Object target) {
		boolean accessible = field.canAccess(target);
		try {
			field.setAccessible(true);
			return get(field, target);
		}
		catch (RuntimeException e)
		{
			throw e;
		}
		catch (Exception e) {
			throw new IllegalArgumentException("exception setting: " + field.getName(), e);
		}
		finally {
			field.setAccessible(accessible);
		}
	}

	public static void setAndWrap(Field field, Object target, Object value) {
		boolean accessible = field.canAccess(target);
		try {
			field.setAccessible(true);
			set(field, target, value);
		}
		catch (RuntimeException e)
		{
			throw e;
		}
		catch (Exception e) {
			throw new IllegalArgumentException("exception setting: " + field.getName(), e);
		}		finally {
			field.setAccessible(accessible);
		}
	}

	public static Object invokeAndWrap(Method method, Object target, Object... args) {
		try {
			return invoke(method, target, args);
		}
		catch (RuntimeException e)
		{
			throw e;
		}
		catch (Exception e) {
			throw new RuntimeException("exception invoking: " + method.getName(), e);
		}
	}

	public static String toString(Member member) {
		return unqualify(member.getDeclaringClass().getName()) +
				'.' +
				member.getName();
	}

	public static Class<?> classForName(String name) throws ClassNotFoundException {
		return UtilClassLoader.getLoader().loadClass(name);
	}

	public static boolean isClassAvailable(String name) {
		return UtilClassLoader.getLoader().isClassAvailable(name);
	}

	public static Class<?> getCollectionElementType(Type collectionType) {
		if (!(collectionType instanceof ParameterizedType)) {
			throw new IllegalArgumentException("collection type not parameterized");
		}
		Type[] typeArguments = ((ParameterizedType) collectionType).getActualTypeArguments();
		if (typeArguments.length == 0) {
			throw new IllegalArgumentException("no type arguments for collection type");
		}
		Type typeArgument = typeArguments.length == 1 ? typeArguments[0] : typeArguments[1]; //handle Maps
		if (typeArgument instanceof ParameterizedType) {
			typeArgument = ((ParameterizedType) typeArgument).getRawType();
		}
		if (!(typeArgument instanceof Class)) {
			throw new IllegalArgumentException("type argument not a class");
		}
		return (Class<?>) typeArgument;
	}

	public static Class<?> getMapKeyType(Type collectionType) {
		if (!(collectionType instanceof ParameterizedType)) {
			throw new IllegalArgumentException("collection type not parameterized");
		}
		Type[] typeArguments = ((ParameterizedType) collectionType).getActualTypeArguments();
		if (typeArguments.length == 0) {
			throw new IllegalArgumentException("no type arguments for collection type");
		}
		Type typeArgument = typeArguments[0];
		if (!(typeArgument instanceof Class)) {
			throw new IllegalArgumentException("type argument not a class");
		}
		return (Class<?>) typeArgument;
	}

	public static Method getSetterMethod(Class<?> clazz, String name) throws NoSuchMethodException {
		return ReflectionTools.getSetterMethod(false, clazz, name);
	}


	public static Method getMethod(Annotation annotation, String name) {
		try {
			return annotation.annotationType().getMethod(name);
		}
		catch (NoSuchMethodException nsme) {
			return null;
		}
	}

	public static boolean isInstanceOf(Class<?> clazz, String name) {
		if (name == null) {
			throw new IllegalArgumentException("name cannot be null");
		}
		for (Class<?> c = clazz; c != Object.class; c = c.getSuperclass()) {
			if (instanceOf(c, name)) {
				return true;
			}
		}
		return false;
	}

	private static boolean instanceOf(Class<?> clazz, String name) {
		if (name.equals(clazz.getName())) {
			return true;
		} else {
			boolean found = false;
			Class<?>[] interfaces = clazz.getInterfaces();
			for (int i = 0; i < interfaces.length && !found; i++) {
				found = instanceOf(interfaces[i], name);
			}
			return found;
		}

	}

	public static String toClassNameString(String sep, Object... objects) {
		if (objects.length == 0) return "";
		StringBuilder builder = new StringBuilder();
		for (Object object : objects) {
			builder.append(sep);
			if (object == null) {
				builder.append("null");
			} else {
				builder.append(object.getClass().getName());
			}
		}
		return builder.substring(sep.length());
	}

	public static String unqualify(String name) {
		return unqualify(name, '.');
	}

	public static String unqualify(String name, char sep) {
		return name.substring(name.lastIndexOf(sep) + 1);
	}

	public static String decapitalize(String name) {
		if (name == null)
			return null;
		if (name.isEmpty() || (name.length() > 1 && Character.isUpperCase(name.charAt(1)))) {
			return name;
		}
		char[] chars = name.toCharArray();
		chars[0] = Character.toLowerCase(chars[0]);
		return new String(chars);
	}

}
