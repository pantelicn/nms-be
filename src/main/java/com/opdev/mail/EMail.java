package com.opdev.mail;

import java.util.Map;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PRIVATE)
@Getter
public class EMail {

    private String to;
    private String from;
    private String subject;
    private String content;
    private Map< String, Object > model;

}
