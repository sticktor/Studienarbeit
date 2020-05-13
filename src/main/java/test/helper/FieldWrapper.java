package test.helper;

import edu.umd.cs.findbugs.classfile.ClassDescriptor;
import edu.umd.cs.findbugs.classfile.FieldDescriptor;

import java.lang.reflect.Field;
import java.util.Objects;

/***
 * Wrapper for Field from Java and Field from Descriptions by SpotBugs
 */
public class FieldWrapper {
	private Class<?> declaringClass;
	private String name;
	private Class<?> type;

	/***
	 * Create Wrapper from Field from Java
	 * @param field to create Wrapper from
	 */
	public FieldWrapper(Field field)
	{
		declaringClass = field.getDeclaringClass();
		name = field.getName();
		type = field.getType();
	}

	/***
	 * Create Wrapper for Field from Descriptions by SpotBugs
	 * @param classDescriptor the descriptor of the declaring class
	 * @param fieldDescriptor the descriptor of the field
	 * @throws ClassNotFoundException when something happened that shouldnt happen
	 */
	public FieldWrapper(ClassDescriptor classDescriptor, FieldDescriptor fieldDescriptor) throws ClassNotFoundException {
		this.declaringClass = Class.forName(classDescriptor.getDottedClassName());

		this.name = fieldDescriptor.getName();
		this.type = ClassHelper.GetClassFromString(fieldDescriptor.getClassDescriptor().getDottedClassName());
	}

	@Override
	public boolean equals(Object obj) {
		if (obj instanceof Field) {
			Field other = (Field)obj;
			return (getDeclaringClass() == other.getDeclaringClass())
					&& (Objects.equals(getName(), other.getName()))
					&& (getType() == other.getType());
		}
		return false;
	}

	public boolean equalField(FieldWrapper other)
	{
		if (other != null) {
			return (getDeclaringClass() == other.getDeclaringClass())
					&& (Objects.equals(getName(), other.getName()))
					&& (getType() == other.getType());
		}
		return false;
	}

	public Class<?> getDeclaringClass() {
		return declaringClass;
	}

	public void setDeclaringClass(Class<?> declaringClass) {
		this.declaringClass = declaringClass;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Class<?> getType() {
		return type;
	}

	public void setType(Class<?> type) {
		this.type = type;
	}
}
