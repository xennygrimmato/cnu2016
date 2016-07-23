import edu.umd.cs.findbugs.BugInstance;
import edu.umd.cs.findbugs.BugReporter;
import edu.umd.cs.findbugs.BytecodeScanningDetector;
import edu.umd.cs.findbugs.classfile.ClassDescriptor;
import edu.umd.cs.findbugs.classfile.DescriptorFactory;
import edu.umd.cs.findbugs.classfile.MethodDescriptor;

public class RunDetector extends BytecodeScanningDetector {

    protected BugReporter bugReporter;
    protected boolean entity;


    DescriptorFactory df;
    private static final ClassDescriptor THREAD_CLASS = DescriptorFactory.createClassDescriptor(Thread.class);
    private String RUN_STR = "run";


    public RunDetector(BugReporter bugReporter) {
        this.bugReporter = bugReporter;
    }

    boolean isThreadInstance(ClassDescriptor c) {
        System.out.println("[isThreadInstance] : INSIDE " + c.getDottedClassName());
        if(c == null) {
            System.out.println("[isThreadInstance] : Source=NULL");
            return false;
        }
        try {
            System.out.println("[isThreadInstance] : Source=" + c.getXClass().getSource());
            if (c.equals(THREAD_CLASS)) return true;
            return c.equals(THREAD_CLASS) | isThreadInstance(c.getXClass().getSuperclassDescriptor());
        } catch(Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    @Override
    public void sawMethod() {
        MethodDescriptor invokedMethod = getMethodDescriptorOperand();
        ClassDescriptor invokedObject = getClassDescriptorOperand();

        try {
            if(invokedMethod == null) return;
            ClassDescriptor curClass = invokedObject.getXClass().getClassDescriptor();
            ClassDescriptor curSuper = invokedObject.getXClass().getSuperclassDescriptor();
            String methodname = invokedMethod.getName();

            //System.out.println(curClass.getXClass().getClass().getName());

            if(RUN_STR.equals(methodname)) {
                if (isThreadInstance(curClass)) {
                    System.out.println("DONE! Found Thread.java as an ancestor.");
                    bugReporter.reportBug(
                            new BugInstance(this, "THREAD_INCONSISTENCY", HIGH_PRIORITY)
                                    .addClassAndMethod(this).addSourceLine(this));
                } else {
                    System.out.println("OOPS! Did not find Thread.java as an ancestor.");
                }
            }
        } catch(Exception e) {
            e.printStackTrace();
        }
    }
}