package com.cnu16.a9;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;

/**
 * Created by vaibhavtulsyan on 20/07/16.
 * Rule: findbugs:DMI_NONSERIALIZABLE_OBJECT_WRITTEN
 * (http://grepcode.com/file/repo1.maven.org/maven2/net.sf.sanity4j/sanity4j/1.1.0/resources/report/rules/findbugs/DMI_NONSERIALIZABLE_OBJECT_WRITTEN.html)
 * This code seems to be passing a non-serializable object to the ObjectOutput.writeObject method.
 * If the object is, indeed, non-serializable, an error will result.
 */

public class RuleC {
    public void f() {
        NonSerializable ns = new NonSerializable();
        ns.setNumber(new Float(1.2));
        ns.setValue(1);
        ns.setStr("aa");
        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream("tmp.txt");
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        try {
            ObjectOutputStream oos = new ObjectOutputStream(fos);
            oos.writeObject(ns);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
