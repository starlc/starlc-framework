package com.starlc.protocol;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class Message<T> {
    private Header header;

    private T content;
}
