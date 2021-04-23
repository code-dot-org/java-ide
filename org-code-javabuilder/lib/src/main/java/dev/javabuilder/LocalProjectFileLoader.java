package dev.javabuilder;

import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Paths;
import org.code.javabuilder.*;

/** Intended for local testing only. Loads the main.json file from the resources folder. */
public class LocalProjectFileLoader implements ProjectFileLoader {

  @Override
  public UserProjectFiles loadFiles() throws UserFacingException, UserInitiatedException {
    try {
      String mainJson =
          new String(
              Files.readAllBytes(
                  Paths.get(getClass().getClassLoader().getResource("main.json").toURI())));
      return new UserProjectFileParser().parseFileJson(mainJson);
    } catch (IOException | URISyntaxException e) {
      throw new UserFacingException("We could not parse your files", e);
    }
  }
}
