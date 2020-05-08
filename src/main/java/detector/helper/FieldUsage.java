package detector.helper;

import edu.umd.cs.findbugs.classfile.ClassDescriptor;
import edu.umd.cs.findbugs.classfile.FieldDescriptor;

public class FieldUsage implements Usage
{
    private ClassDescriptor classDescriptor;
    private FieldDescriptor fieldDescriptor;

    public ClassDescriptor getClassDescriptor()
    {
        return classDescriptor;
    }

    public void setClassDescriptor(ClassDescriptor classDescriptor)
    {
        this.classDescriptor = classDescriptor;
    }

    public FieldDescriptor getFieldDescriptor()
    {
        return fieldDescriptor;
    }

    public void setFieldDescriptor(FieldDescriptor fieldDescriptor)
    {
        this.fieldDescriptor = fieldDescriptor;
    }
}
