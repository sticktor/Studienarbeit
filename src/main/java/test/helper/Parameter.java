package test.helper;

import java.util.Objects;

/***
 * Class to store Parameters from Methods or Local Variables
 */
public class Parameter
{
    public int registerNumber;
    private Class<?> clazz;

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

    public Class<?> getClazz()
    {
        return clazz;
    }

    public void setClazz(Class<?> clazz)
    {
        this.clazz = clazz;
    }
}
