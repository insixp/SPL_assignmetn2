package bgu.spl.mics.application;

import junit.framework.TestCase;
import org.junit.Test;

public class CRMSRunnerTest extends TestCase {

    @Test
    public void testMain() {
        String[] args = new String[]{"example_input.json"};
        for(String s : args)
            System.out.println(s);
        CRMSRunner.main(args);
    }



}