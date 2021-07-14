package org.code.javabuilder;

import com.amazonaws.services.s3.AmazonS3;
import java.io.InputStream;
import org.code.protocol.FileWriter;
import org.code.protocol.JavabuilderException;

public class AWSFileWriter implements FileWriter {
  private final String outputBucketName;
  private final AmazonS3 s3Client;
  private final String javabuilderSessionId;
  private final String getOutputURL;
  private int writes;

  // Temporary limit on writes per session until we can more fully limit usage.
  private static final int WRITES_PER_SESSION = 2;

  public AWSFileWriter(
      AmazonS3 s3Client,
      String outputBucketName,
      String javabuilderSessionId,
      String getOutputURL) {
    this.outputBucketName = outputBucketName;
    this.s3Client = s3Client;
    this.javabuilderSessionId = javabuilderSessionId;
    this.writes = 0;
    this.getOutputURL = getOutputURL;
  }

  @Override
  public String writeToFile(String filename, InputStream input) throws JavabuilderException {
    if (this.writes >= WRITES_PER_SESSION) {
      throw new UserInitiatedException(UserInitiatedExceptionKey.TOO_MANY_WRITES);
    }
    String filePath = this.javabuilderSessionId + "/" + filename;
    this.s3Client.putObject(this.outputBucketName, filePath, input, null);
    this.writes++;
    return this.getOutputURL + "/" + filePath;
  }
}
