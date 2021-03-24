package org.code.lambda.javabuilder;

public class OutputSemaphore {
  private static int outputBeingProcessed = 0;
  private static boolean processingFinalOutput = false;

  public OutputSemaphore() {}

  public synchronized static void addOutputInProgress() {
    outputBeingProcessed++;
  }

  public synchronized static void decreaseOutputInProgress() {
    outputBeingProcessed--;
  }

  public synchronized static boolean anyOutputInProgress() {
    return outputBeingProcessed > 0 || processingFinalOutput;
  }

  public synchronized static void signalProcessFinalOutput() {
    processingFinalOutput = true;
  }

  public synchronized static void processFinalOutput() {
    processingFinalOutput = false;
  }
}
