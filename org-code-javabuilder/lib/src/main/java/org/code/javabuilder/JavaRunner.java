package org.code.javabuilder;

import java.util.Scanner;

// https://staging-studio.code.org/v3/files/lRrV835UEgN3-L07XUiaSQ/MyStagingClass.java
public class JavaRunner extends Thread {
  private final OutputSemaphore outputSemaphore;
  public JavaRunner(OutputSemaphore outputSemaphore) {
    this.outputSemaphore = outputSemaphore;
  }

  public void run() {
    // Sample program. In the future, student code will be executed here.
    System.out.println("What's your name? ");
    Scanner in = new Scanner(System.in);
    String s = in.nextLine();
    System.out.println("Hello " + s + "!");

    // Tell the output poller to collect any remaining output from the program.
    outputSemaphore.signalProcessFinalOutput();
  }
}
