package org.code.javabuilder;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.code.protocol.InternalErrorKey;
import org.code.protocol.JavabuilderThrowableMessage;
import org.code.protocol.Properties;
import org.junit.jupiter.api.Test;

public class UserFacingExceptionTest {
  @Test
  public void getExceptionMessageIncludesConnectionId() {
    UserFacingException exception = new UserFacingException(InternalErrorKey.INTERNAL_EXCEPTION);
    JavabuilderThrowableMessage message = exception.getExceptionMessage();
    assertEquals(message.getDetail().get("connectionId"), Properties.getConnectionId());
  }

  @Test
  public void getExceptionMessageIncludesCause() {
    UserFacingException exception =
        new UserFacingException(
            InternalErrorKey.INTERNAL_EXCEPTION, new Exception("the cause of the exception"));
    JavabuilderThrowableMessage message = exception.getExceptionMessage();
    assertTrue(message.getDetail().getString("cause").contains("the cause of the exception"));
  }
}
