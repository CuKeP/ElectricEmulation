package com.kovyazin.electric_emulator.request_handler;

import java.util.List;

/**
 * Created by Vyacheslav on 09.10.2015.
 */
public interface RequestHandler {
    byte[] handle(List<Integer> inputBytes);
}
