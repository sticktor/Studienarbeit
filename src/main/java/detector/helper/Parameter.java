package detector.helper;

import java.util.Objects;

public class Parameter
{
    public int registerNumber;
    private Class<?> clazz;
    private int lineNumber;

    @Override
    public boolean equals(Object o)
    {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Parameter parameter = (Parameter) o;
        return registerNumber == parameter.registerNumber &&
                Objects.equals(clazz, parameter.clazz);
    }

    @Override
    public int hashCode()
    {
        return Objects.hash(registerNumber, clazz);
    }

    public Class getClazz()
    {
        return clazz;
    }

    public void setClazz(Class clazz)
    {
        this.clazz = clazz;
    }

    public int getLineNumber() {
        return lineNumber;
    }

    public void setLineNumber(int lineNumber) {
        this.lineNumber = lineNumber;
    }
}
