package com.project.software.documents.demos;

import com.project.software.documents.ImageHandler;

import java.io.IOException;
import java.util.Optional;

public interface Demo {

    void exec (ImageHandler imageHandler, Optional<String> optional) throws IOException;

}
