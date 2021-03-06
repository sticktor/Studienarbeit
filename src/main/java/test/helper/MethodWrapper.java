package test.helper;

import edu.umd.cs.findbugs.classfile.ClassDescriptor;
import edu.umd.cs.findbugs.classfile.MethodDescriptor;

import java.lang.reflect.Method;
import java.util.Objects;

/***
 * Wrapper for Method from Java and Method from Descriptions by SpotBugs
 */
public class MethodWrapper
{
	private Class<?> declaringClass;
	private Class<?> returnType;
	private Class<?>[] parameterTypes;
	private String name;

	/***
	 * Create Wrapper from Method from Java
	 * @param method to create Wrapper from
	 */
	public MethodWrapper(Method method) {
		this.declaringClass = method.getDeclaringClass();
		this.returnType = method.getReturnType();
		this.parameterTypes = method.getParameterTypes();
		this.name = method.getName();
	}

	/***
	 * Create Wrapper for Method from Descriptions by SpotBugs
	 * @param classDescriptor the descriptor of the declaring class
	 * @param methodDescriptor the descriptor of the method
	 * @throws ClassNotFoundException when something happened that shouldnt happen
	 */
	public MethodWrapper(ClassDescriptor classDescriptor, MethodDescriptor methodDescriptor) throws ClassNotFoundException
	{
		this.declaringClass = Class.forName(classDescriptor.getDottedClassName());
		String[] signature = methodDescriptor.getSignature().replace("(", "").replace(")", "").split(";");
		parameterTypes = new Class<?>[signature.length-1];

		for (int i = 0; i < signature.length-1; i++) {
			String parameter = signature[i];
			parameterTypes[i] = ClassHelper.GetClassFromString(parameter);
		}
		this.name = methodDescriptor.getName();
		if (signature.length > 1) {
			String parameter = signature[1];
			if (parameter.replace("/", ".").equals("V")) {
				returnType = Void.TYPE;
			} else {
				returnType = ClassHelper.GetClassFromString(parameter);
			}
		}
		else
		{
			returnType = Void.TYPE;
		}
	}

	@Override
	public boolean equals(Object obj)
	{
		if (obj instanceof MethodWrapper) {
			MethodWrapper other = (MethodWrapper)obj;
			if ((getDeclaringClass() == other.getDeclaringClass())
					&& (Objects.equals(getName(), other.getName()))) {
				if (!returnType.equals(other.getReturnType()))
					return false;
				return equalParamTypes(parameterTypes, other.parameterTypes);
			}
		}
		return false;
	}

	public boolean equalMethod(MethodWrapper obj)
	{
		if (obj != null) {
			if (Objects.equals(getName(), obj.getName())) {
				if (!Objects.equals(returnType, obj.getReturnType()))
					return false;
				return equalParamTypes(parameterTypes, obj.parameterTypes);
			}
		}
		return false;
	}

	private boolean equalParamTypes(Class<?>[] params1, Class<?>[] params2)
	{
		if (params1.length == params2.length) {
			for (int i = 0; i < params1.length; i++) {
				if (params1[i] != params2[i])
					return false;
			}
			return true;
		}
		return false;
	}

	public Class<?> getDeclaringClass() {
		return declaringClass;
	}

	public void setDeclaringClass(Class<?> declaringClass) {
		this.declaringClass = declaringClass;
	}

	public Class<?> getReturnType() {
		return returnType;
	}

	public void setReturnType(Class<?> returnType) {
		this.returnType = returnType;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
}
