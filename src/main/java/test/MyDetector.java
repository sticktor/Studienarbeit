package test;

import edu.umd.cs.findbugs.BugInstance;
import edu.umd.cs.findbugs.BugReporter;
import edu.umd.cs.findbugs.OpcodeStack;
import edu.umd.cs.findbugs.bcel.OpcodeStackDetector;
import org.apache.bcel.Const;
import org.apache.bcel.classfile.*;
import test.helper.*;

import java.util.*;

/***
 * This class is the main class for this SpotBugs Plugin to scan for possible Generalization
 */
public class MyDetector extends OpcodeStackDetector {
    private final BugReporter bugReporter;

    public MyDetector(BugReporter bugReporter) {
        this.bugReporter = bugReporter;
    }
    static List<Integer> list = new ArrayList<>();
    static Method method;
    static Map<Method, List<Parameter>> parametersPerMethod = new HashMap <>();
    static HashMap<Method, HashMap<Parameter, ArrayList<Usage>>> UsagesPerAttributePerMethod = new HashMap <>();
    static OpcodeStack.Item item;
    static int paras;
    static boolean manuallyInvoked = false;

    /***
     * Gets called when an Opcode is seen
     * Main Logic Method
     * @param seen the seen Opcode
     */
    @Override
    public void sawOpcode(int seen) {
        try {
            if (seen == Const.INVOKEVIRTUAL || seen == Const.INVOKESTATIC || seen == Const.INVOKEINTERFACE) {
                manuallyInvoked = true;
                int counter = 0;
                for (int i = 0; i < getMethodDescriptorOperand().getSignature().split(";").length - 1; i++) {
                    counter++;
                }
                item = getStack().getStackItem(counter);
                sawMethod();
            } else if (seen == Const.PUTFIELD) {
                item = getStack().getStackItem(1);
            } else if (seen == Const.GETFIELD) {
                item = getStack().getStackItem(0);
            } else {
                item = null;
            }

            if (seen == Const.RETURN ||
                    seen == Const.ARETURN ||
                    seen == Const.DRETURN ||
                    seen == Const.FRETURN ||
                    seen == Const.IRETURN ||
                    seen == Const.LRETURN) {
                int pc = getPC();
                LineNumberTable table = getMethod().getLineNumberTable();
                LineNumber last = table.getLineNumberTable()[table.getTableLength() - 1];
                int pcOfLast = last.getStartPC();
                if (pc >= pcOfLast) {
                    onLeaveMethod(getMethod());
                }
            }

            list.add(seen);

            //System.out.println(seen);
            Method oldMethod = method;
            method = getMethod();

            if (oldMethod != method) {
                paras = 0;
                int i = 1;
                if (method.isStatic())
                    i--;
                while (true) {
                    OpcodeStack.Item item = getStack().getLVValue(i++);
                    if (item.equals(new OpcodeStack.Item())) {
                        break;
                    }
                    paras++;
                    Parameter parameter = new Parameter();
                    parameter.registerNumber = item.getRegisterNumber();
                    Class<?> c;
                    if (item.getSignature().length() == 1) {
                        c = ClassHelper.GetPrimitiveTypeFromString(item.getSignature());
                    } else {
                        String clazz = item.getSignature().substring(1, item.getSignature().length() - 1);
                        c = Class.forName(clazz.replace("/", "."));
                    }
                    finishCreateParameterAndAddToMap(parameter, c);
                }
                LocalVariableTable table = method.getLocalVariableTable();
                for (int j = i - 1; j < table.getLocalVariableTable().length; j++) {
                    int finalJ = j;
                    Optional<LocalVariable> optional = Arrays.stream(table.getLocalVariableTable()).filter(e -> e.getIndex() == finalJ).findFirst();
                    if (optional.isPresent()) {
                        LocalVariable variable = optional.get();
                        String signature = variable.getSignature();
                        Class<?> c = ClassHelper.GetPrimitiveTypeFromString(signature);
                        if (c == null) {
                            c = Class.forName(signature.substring(1, signature.length() - 1).replace("/", "."));
                        }
                        Parameter p = new Parameter();
                        p.registerNumber = j;
                        finishCreateParameterAndAddToMap(p, c);
                    }
                }
            }
        }
        catch (ClassNotFoundException e)
        {
            e.printStackTrace();
        }
    }

    private void finishCreateParameterAndAddToMap(Parameter parameter, Class<?> c) {
        parameter.setClazz(c);
        if (parametersPerMethod.containsKey(method)) {
            parametersPerMethod.get(method).add(parameter);
        } else {
            List<Parameter> parameters = new ArrayList<>();
            parameters.add(parameter);
            parametersPerMethod.put(method, parameters);
        }
    }

    /**
     * Gets called when a method is seen.
     * Adds a possible MethodUsage to the UsagesMap
     */
    @Override
    public void sawMethod()
    {
        if (!manuallyInvoked)
        {
            return;
        }
        List<Parameter> p = parametersPerMethod.get(getMethod());
        if (p == null)
        {
            manuallyInvoked = false;
            return;
        }

        if (item == null)
        {
            manuallyInvoked = false;
            return;
        }
        Optional<Parameter> optionalParameter = p.stream().filter(e -> e.registerNumber == item.getRegisterNumber()).findFirst();
        Parameter parameter = optionalParameter.orElse(null);
        if (parameter == null)
        {
            manuallyInvoked = false;
            return;
        }
        MethodUsage mu = new MethodUsage();
        mu.setClassDescriptor(getClassDescriptorOperand());
        mu.setMethodDescriptor(getMethodDescriptorOperand());
        if (UsagesPerAttributePerMethod.containsKey(method))
        {
            HashMap<Parameter, ArrayList<Usage>> usage = UsagesPerAttributePerMethod.get(method);
            if (!usage.containsKey(parameter)) {
                usage.put(parameter, new ArrayList<>());
            }
            usage.get(parameter).add(mu);
        }
        else
        {
            UsagesPerAttributePerMethod.put(method, new HashMap <>());
            HashMap<Parameter, ArrayList<Usage>> h = UsagesPerAttributePerMethod.get(method);
            h.put(parameter, new ArrayList<>());
            ArrayList<Usage> f = h.get(parameter);
            f.add(mu);
        }
        item = null;
        manuallyInvoked = false;
    }

    /**
     * Gets called when a field is seen.
     * Adds a possible FieldUsage to the UsagesMap
     */
    @Override
    public void sawField()
    {
        List<Parameter> p = parametersPerMethod.get(getMethod());
        if (p == null)
        {
            return;
        }

        if (item == null || item.getRegisterNumber() == 0)
        {
            return;
        }
        Optional<Parameter> optionalParameter = p.stream().filter(e -> e.registerNumber == item.getRegisterNumber()).findFirst();
        Parameter parameter = optionalParameter.orElse(null);
        if (parameter == null)
        {
            return;
        }
        FieldUsage fu = new FieldUsage();
        fu.setClassDescriptor(getClassDescriptorOperand());
        fu.setFieldDescriptor(getFieldDescriptorOperand());
        if (UsagesPerAttributePerMethod.containsKey(method))
        {
            HashMap<Parameter, ArrayList<Usage>> usage = UsagesPerAttributePerMethod.get(method);
            if (!usage.containsKey(parameter)) {
                usage.put(parameter, new ArrayList<>());
            }
            usage.get(parameter).add(fu);
        }
        else
        {
            UsagesPerAttributePerMethod.put(method, new HashMap <>());
            HashMap<Parameter, ArrayList<Usage>> h = UsagesPerAttributePerMethod.get(method);
            h.put(parameter, new ArrayList <>());
            ArrayList<Usage> f = h.get(parameter);
            f.add(fu);
        }
        item = null;
    }

    /***
     * Scan a Method of the Map for errors
     * @param method The method to scan for errors
     */
    public void onLeaveMethod(Method method)
    {
        HashMap<Parameter, ArrayList<Usage>> occurences = UsagesPerAttributePerMethod.get(method);
        if (occurences == null)
        {
            return;
        }
        outer:
        for (Map.Entry<Parameter, ArrayList<Usage>> entry : occurences.entrySet())
        {
            Class<?> clazz = entry.getKey().getClazz();
            Class<?>[] interfaces = clazz.getInterfaces();
            interfaceLoop:
            for (Class<?> inter: interfaces)
            {
                ArrayList<Boolean> booleans = new ArrayList<>();
                for (Usage usage : entry.getValue())
                {
                    if (usage instanceof MethodUsage)
                    {
                        FindMethodUsage(inter, booleans, (MethodUsage) usage);
                    }
                    else if (usage instanceof FieldUsage)
                    {
                        break interfaceLoop;
                    }
                }
                if (booleans.stream().allMatch(e -> e))
                {
                    BugInstance bug = new BugInstance(this, "PG", HIGH_PRIORITY)
                            .addClassAndMethod(this)
                            .addSourceLine(this, method.getLineNumberTable().getLineNumberTable()[0].getLineNumber());
                    bugReporter.reportBug(bug);
                    continue outer;
                }
            }

            Class<?> extension = clazz.getSuperclass();
            if (extension != null)
            {
                ArrayList<Boolean> booleans = new ArrayList<>();
                for (Usage usage : entry.getValue())
                {
                    if (usage instanceof MethodUsage)
                    {
                        FindMethodUsage(extension, booleans, (MethodUsage) usage);
                    }
                    else if (usage instanceof FieldUsage)
                    {
                        FindFieldUsage(extension, booleans, (FieldUsage) usage);
                    }
                }
                if (booleans.stream().allMatch(e -> e))
                {
                    BugInstance bug = new BugInstance(this, "PG", HIGH_PRIORITY)
                            .addClassAndMethod(this)
                            .addSourceLine(this, method.getLineNumberTable().getLineNumberTable()[0].getLineNumber());
                    bugReporter.reportBug(bug);
                }
            }
        }
    }

    private static void FindMethodUsage(Class<?> extension, ArrayList<Boolean> booleans, MethodUsage usage) {
        MethodWrapper methodWrapper = null;
        try {
            methodWrapper = new MethodWrapper(usage.getClassDescriptor(), usage.getMethodDescriptor());
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        if (methodWrapper == null)
        {
            return;
        }
        boolean once = false;
        for (java.lang.reflect.Method m : extension.getMethods())
        {
            MethodWrapper interMethodWrapper = new MethodWrapper(m);
            if (methodWrapper.equalMethod(interMethodWrapper))
            {
                once = true;
                break;
            }
        }
        booleans.add(once);
    }

    private static void FindFieldUsage(Class<?> extension, ArrayList<Boolean> booleans, FieldUsage usage) {
        FieldWrapper fieldWrapper = null;
        try {
            fieldWrapper = new FieldWrapper(usage.getClassDescriptor(), usage.getFieldDescriptor());
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        if (fieldWrapper == null)
        {
            return;
        }
        boolean once = false;
        for (java.lang.reflect.Field f : extension.getFields())
        {
            FieldWrapper extensionFieldWrapper = new FieldWrapper(f);
            if (fieldWrapper.equalField(extensionFieldWrapper))
            {
                once = true;
                break;
            }
        }
        booleans.add(once);
    }
}
