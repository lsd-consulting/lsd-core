package com.lsd;

import lombok.Value;

import java.nio.file.Path;

@Value
public class CapturedReport {
    String title;
    Path path;
    String status;
}
