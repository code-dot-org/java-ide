package org.code.protocol;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.HashMap;

public abstract class UserFacingError extends Error implements UserFacingThrowableProtocol {
  private final Enum key;

  protected UserFacingError(Enum key) {
    super(key.toString());
    this.key = key;
  }

  protected UserFacingError(Enum key, Throwable cause) {
    super(key.toString(), cause);
    this.key = key;
  }

  public UserFacingThrowableMessage getExceptionMessage() {
    HashMap<String, String> detail = new HashMap<>();
    detail.put("connectionId", Properties.getConnectionId());
    if (this.getCause() != null) {
      detail.put("cause", this.getLoggingString());
    }
    return new UserFacingThrowableMessage(this.key, detail);
  }

  /** @return A pretty version of the exception and stack trace. */
  public String getLoggingString() {
    StringWriter stringWriter = new StringWriter();
    PrintWriter printWriter = new PrintWriter(stringWriter);
    this.printStackTrace(printWriter);
    return stringWriter.toString();
  }
}
